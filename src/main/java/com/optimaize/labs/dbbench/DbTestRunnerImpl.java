package com.optimaize.labs.dbbench;

import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PoolConfig;
import com.mchange.v2.c3p0.PooledDataSource;
import com.optimaize.labs.dbbench.databases.DbUtil;
import com.optimaize.labs.dbbench.databases.TestDbCreator;
import com.optimaize.labs.dbbench.util.ConcurrentMaxCollector;
import com.optimaize.labs.dbbench.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Fabian Kessler
 */
class DbTestRunnerImpl implements DbTestRunner {

    @NotNull
    private final RunConfig cfg;

    private final DbUtil dbUtil;
    private final TestDbCreator testDbCreator;

    @Nullable
    private ThreadPoolExecutor threadPoolExecutor;
    @Nullable
    private Connection dbSingleConnection;
    @Nullable
    private PooledDataSource dbConnectionPool;
    @NotNull
    private JdbcTemplate jdbcTemplate;


    DbTestRunnerImpl(@NotNull RunConfig cfg) {
        this.cfg = cfg;
        this.dbUtil = cfg.getDatabase().newDbUtil();
        this.testDbCreator = cfg.getDatabase().newTestDbCreator();
    }


    @NotNull @Override
    public RunConfig getDbTestRunConfig() {
        return cfg;
    }

    @Override
    public void prepare() throws IOException, SQLException {
        testDbCreator.create(cfg.getName(), cfg.getTestDbConfig().getNumRecords(), cfg.getTestDbConfig().isIndexed());
    }

    @Override
    public TestResult run() {
        if (cfg.getConnectionPoolSize() != null) {
            this.dbSingleConnection = null;
            this.dbConnectionPool = makeConnectionPool(cfg.getConnectionPoolSize());
        } else {
            this.dbSingleConnection = Util.makeSingleConnection(dbUtil.connectionStringForReadonly(cfg.getDatabase().getTestDbPathToFile() + cfg.getName()));
            dbUtil.setPropertiesForReadonly(this.dbSingleConnection);
            this.dbConnectionPool = null;
        }
        if (cfg.getThreadPoolSize() != null) {
            this.threadPoolExecutor = Util.makeExecutor(cfg.getThreadPoolSize());
        } else {
            this.threadPoolExecutor = null;
        }
        this.jdbcTemplate = makeJdbcTemplate();

        Stopwatch totaltime = Stopwatch.createStarted();
        ConcurrentMaxCollector maxCollector = ConcurrentMaxCollector.create(0L);

        for (int loop=0; loop<cfg.getTestDbConfig().getTestIterations(); loop++) {
            for (int i=0; i<cfg.getTestDbConfig().getNumRecords(); i++) {
                submit(i, maxCollector);
            }
        }

        if (threadPoolExecutor!=null) {
            shutdown(threadPoolExecutor);
        }

        return new TestResult(this, totaltime.elapsed(TimeUnit.MILLISECONDS), maxCollector.getBest());
    }

    @Override
    public void cleanup(boolean throwOnFailure) {
        closeDbConnections();
        try {
            testDbCreator.delete(cfg.getName());
        } catch (RuntimeException e) {
            if (throwOnFailure) {
                throw e;
            } else {
                //never mind. db file still locked, happens on windows.
            }
        }
    }

    private void closeDbConnections() {
        if (dbSingleConnection!=null) {
            try {
                dbSingleConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                dbSingleConnection = null;
            }
        }
        if (dbConnectionPool!=null) {
            try {
                dbConnectionPool.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                dbConnectionPool = null;
            }
        }
    }


    private void submit(final int num, final ConcurrentMaxCollector maxCollector) {
        if (threadPoolExecutor!=null) {
            threadPoolExecutor.submit(new Runnable() {
                @Override public void run() {
                    maxCollector.offer(execute(num, jdbcTemplate));
                }
            });
        } else {
            maxCollector.offer(execute(num, jdbcTemplate));
        }
    }


    private void shutdown(ThreadPoolExecutor executor) {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException("Gave up waiting before finishing!");
        }
    }

    /**
     * @return the time taken in ms
     */
    private long execute(int num, JdbcTemplate jdbcTemplate) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        int count = jdbcTemplate.queryForInt("select count(*) from test where field1 = ?", Util.md5(Integer.valueOf(num).toString()));
        if (count!=1) throw new AssertionError("Expected to find exactly 1 record!");
        return stopwatch.elapsed(TimeUnit.MILLISECONDS);
    }


    private JdbcTemplate makeJdbcTemplate() {
        if (this.dbSingleConnection != null) {
            return new JdbcTemplate(makeSingleConnectionDataSource());
        } else {
            assert dbConnectionPool != null;
            return new JdbcTemplate(dbConnectionPool);
        }
    }

    private DataSource makeSingleConnectionDataSource() {
        return new SingleConnectionDataSource(
                dbSingleConnection,
                false
        );
    }

    private PooledDataSource makeConnectionPool(int connPoolSize) {
        String dbFile = cfg.getDatabase().getTestDbPathToFile()+cfg.getName();
        Optional<DataSource> unpooled = dbUtil.dataSourceForReadonly(dbFile);
        if (unpooled.isPresent()) {
            return makeConnectionPoolFromSimpleDataSource(connPoolSize, unpooled);
        } else {
            try {
                ComboPooledDataSource cpds = new ComboPooledDataSource();
                cpds.setDriverClass(dbUtil.getDriverClassName());
                cpds.setJdbcUrl(dbUtil.connectionStringForReadonly(dbFile));
                cpds.setInitialPoolSize(0);
                cpds.setMinPoolSize(0);
                cpds.setMaxPoolSize(connPoolSize);
                cpds.setMaxIdleTime(1);
                cpds.setMaxIdleTimeExcessConnections(1);
                //cpds.setAcquireIncrement(Accomodation);
                return cpds;
            } catch (PropertyVetoException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private PooledDataSource makeConnectionPoolFromSimpleDataSource(int connPoolSize, Optional<DataSource> unpooled) {
        //see http://stackoverflow.com/questions/22303796/set-sqlite-connection-properties-in-c3p0-connection-pool
        PoolConfig poolConfig = new PoolConfig();
        poolConfig.setMaxPoolSize(connPoolSize);

        //we do not want to cache prepared statements. our queries are super simple. we only lose speed, don't gain any.
        //see http://www.mchange.com/projects/c3p0/ topic "Configuring Statement Pooling"
        //cpds.setMaxStatements(1000); //prepared statements cache

        try {
            DataSource dataSource = DataSources.pooledDataSource(unpooled.get(), poolConfig);
            return (PooledDataSource)dataSource; //ugly
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String toString() {
        return "DbTestRunnerImpl{" +
                "cfg='" + cfg + '\'' +
                '}';
    }
}
