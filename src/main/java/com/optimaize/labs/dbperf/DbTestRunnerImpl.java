package com.optimaize.labs.dbperf;

import com.google.common.base.Stopwatch;
import com.mchange.v2.c3p0.ComboPooledDataSource;
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
    private final DbTestRunConfig cfg;

    private final DbUtil dbUtil;
    private final TestDbCreator testDbCreator;

    @Nullable
    private ThreadPoolExecutor threadPoolExecutor;
    @Nullable
    private Connection dbSingleConnection;
    @Nullable
    private ComboPooledDataSource dbConnectionPool;


    DbTestRunnerImpl(@NotNull DbTestRunConfig cfg) {
        this.cfg = cfg;
        this.dbUtil = cfg.getDatabase().newDbUtil();
        this.testDbCreator = cfg.getDatabase().makeTestDbCreator();
    }


    @NotNull @Override
    public DbTestRunConfig getDbTestRunConfig() {
        return cfg;
    }

    @Override
    public void prepare() throws IOException, SQLException {
        testDbCreator.create(cfg.getName(), cfg.getNumRecords(), cfg.isIndexed());
    }

    @Override
    public TestResult run() {
        if (cfg.getConnectionPoolSize() != null) {
            this.dbSingleConnection = null;
            this.dbConnectionPool = makeConnectionPool(cfg.getConnectionPoolSize());
        } else {
            this.dbSingleConnection = Util.makeSingleConnection(dbUtil.connectionStringForReadonly(cfg.getDatabase().getTestDbPathToFile()+cfg.getName()));
            dbUtil.setPropertiesForReadonly(this.dbSingleConnection);
            this.dbConnectionPool = null;
        }
        if (cfg.getThreadPoolSize() != null) {
            this.threadPoolExecutor = Util.makeExecutor(cfg.getThreadPoolSize());
        } else {
            this.threadPoolExecutor = null;
        }

        Stopwatch totaltime = Stopwatch.createStarted();
        ConcurrentMaxCollector maxCollector = ConcurrentMaxCollector.create(0L);

        for (int loop=0; loop<cfg.getTestIterations(); loop++) {
            for (int i=0; i<cfg.getNumRecords(); i++) {
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
                //never mind
            }
        }
    }

    private void closeDbConnections() {
        if (dbSingleConnection!=null) {
            try {
                dbSingleConnection.close();
                dbSingleConnection = null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (dbConnectionPool!=null) {
            dbConnectionPool.close();
            dbConnectionPool = null;
        }
    }


    private void submit(final int num, final ConcurrentMaxCollector maxCollector) {
        if (threadPoolExecutor!=null) {
            threadPoolExecutor.submit(new Runnable() {
                @Override public void run() {
                    maxCollector.offer(execute(num, getJdbcTemplate()));
                }
            });
        } else {
            maxCollector.offer(execute(num, getJdbcTemplate()));
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
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
//        assertTrue(elapsed <= 100, ""+elapsed);
        return elapsed;
    }


    private JdbcTemplate getJdbcTemplate() {
        if (this.dbSingleConnection != null) {
            return new JdbcTemplate(getDataSource());
        } else {
            assert dbConnectionPool != null;
            return new JdbcTemplate(dbConnectionPool);
        }
    }

    private DataSource getDataSource() {
        return new SingleConnectionDataSource(
                getConnection(),
                false  //TODO do we want this?
        );
    }

    private Connection getConnection() {
        if (this.dbSingleConnection != null) {
            return dbSingleConnection;
        } else {
            Connection connectionFromPool = getConnectionFromPool();

            //TODO do i need to set this each time to ensure it's set?
            dbUtil.setPropertiesForReadonly(connectionFromPool);

            return connectionFromPool;
        }
    }
    private Connection getConnectionFromPool() {
        try {
            assert dbConnectionPool != null;
            return dbConnectionPool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ComboPooledDataSource makeConnectionPool(int connPoolSize) {
        try {
            ComboPooledDataSource cpds = new ComboPooledDataSource();
            cpds.setDriverClass(dbUtil.getDriverClassName());
            cpds.setJdbcUrl(dbUtil.connectionStringForReadonly(cfg.getDatabase().getTestDbPathToFile()+cfg.getName()));
            cpds.setInitialPoolSize(0);
            cpds.setMinPoolSize(0);
            cpds.setMaxPoolSize(connPoolSize);
            cpds.setMaxIdleTime(0);
            cpds.setMaxIdleTimeExcessConnections(0);
            //cpds.setAcquireIncrement(Accomodation);
            return cpds;
        } catch (PropertyVetoException ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public String toString() {
        return "DbTestRunnerImpl{" +
                "cfg='" + cfg + '\'' +
                '}';
    }
}
