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
    private final Database database;

    private final int numRecords;
    private final boolean indexed;

    @Nullable
    private final Integer connectionPoolSize;
    @Nullable
    private final Integer threadPoolSize;

    private final int testIterations;

    private final DbUtil dbUtil;
    private final TestDbCreator testDbCreator;

    @Nullable
    private ThreadPoolExecutor threadPoolExecutor;
    @Nullable
    private Connection dbSingleConnection;
    @Nullable
    private ComboPooledDataSource dbConnectionPool;


    DbTestRunnerImpl(@NotNull Database database,
                     int numRecords, boolean indexed,
                     @Nullable Integer connectionPoolSize, @Nullable Integer threadPoolSize,
                     int testIterations) {
        this.database = database;

        this.dbUtil = database.newDbUtil();
        this.testDbCreator = database.makeTestDbCreator();

        this.connectionPoolSize = connectionPoolSize;
        this.threadPoolSize = threadPoolSize;

        this.numRecords = numRecords;
        this.indexed = indexed;

        this.testIterations = testIterations;
    }

    @Override
    public void prepare() throws IOException, SQLException {
        testDbCreator.create(numRecords, indexed);
    }

    @Override
    public TestResult run() {
        if (connectionPoolSize != null) {
            this.dbSingleConnection = null;
            this.dbConnectionPool = makeConnectionPool(connectionPoolSize);
        } else {
            this.dbSingleConnection = Util.makeSingleConnection(dbUtil.connectionStringForReadonly(database.getTestDbPathToFile()));
            dbUtil.setPropertiesForReadonly(this.dbSingleConnection);
            this.dbConnectionPool = null;
        }
        if (threadPoolSize != null) {
            this.threadPoolExecutor = Util.makeExecutor(threadPoolSize);
        } else {
            this.threadPoolExecutor = null;
        }

        Stopwatch totaltime = Stopwatch.createStarted();
        ConcurrentMaxCollector maxCollector = ConcurrentMaxCollector.create(0L);

        for (int loop=0; loop<testIterations; loop++) {
            for (int i=0; i<numRecords; i++) {
                submit(i, maxCollector);
            }
        }

        if (threadPoolExecutor!=null) {
            shutdown(threadPoolExecutor);
        }

        return new TestResult(totaltime.elapsed(TimeUnit.MILLISECONDS), maxCollector.getBest());
    }

    @Override
    public void cleanup() {
        testDbCreator.delete();
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
        return new JdbcTemplate(getDataSource());
    }

    private DataSource getDataSource() {
        return new SingleConnectionDataSource(
                getConnection(),
                true  //otherwise we get connection closed errors.
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
            cpds.setJdbcUrl(dbUtil.connectionStringForReadonly(database.getTestDbPathToFile()));
            cpds.setMaxPoolSize(connPoolSize);
            cpds.setMinPoolSize(connPoolSize);
            //cpds.setAcquireIncrement(Accomodation);
            return cpds;
        } catch (PropertyVetoException ex) {
            throw new RuntimeException(ex);
        }
    }
}
