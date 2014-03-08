package com.optimaize.labs.dbperf;

import com.google.common.base.Stopwatch;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * Stress-tests sqlite readonly with many threads for a while.
 *
 * @author Fabian Kessler
 */
public class ReadonlyConcurrencyTest {

    private static final String dbFilePath = ReadonlyConcurrencyTest.class.getResource("/").getFile();
    /**
     * This db file is used for testing but it is not touched, it is copied to dbFilePathToTest.
     */
    private static final String dbFilePathToCopy = dbFilePath+"/sqlite_stresstest";

    public static final int NUM_RECORDS = 10000;

    @BeforeClass
    public void setup() throws SQLException, IOException {
        TestDbCreators.h2().delete();
        TestDbCreators.h2().create(10000, false);
    }
    @AfterClass
    public static void teardown() throws SQLException {
        cleanup();
    }

    @Test
    public void foo() {
        DbTestRunner runner = new TestBuilder()
                .database(Database.SQLITE)
                .numRecords(NUM_RECORDS)
                .indexed(true)
                .connectionPool(20)
                .threadPool(10)
                .testIterations(10)
                .build();

        runner.cleanup();
        runner.prepare();
        runner.run();
        runner.cleanup();
    }


    private static void cleanup() {
        File dbFile = new File(dbFilePathToCopy);
        if (dbFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dbFile.delete();
        }
    }

    @Test
    public void test() {
//        testMultithreadedReadAccessWithThreadPool();
        //testMultithreadedReadAccessWithSingleSharedConnection();
    }

//    private void testMultithreadedReadAccessWithSingleSharedConnection() {
//        Util.makeConne
//        DbConnector connector = SqliteDbConnector.file(dbFilePathToCopy).forReadingOnly().build();
//        Connection connection = connector.makeConnection();
//        //Connection connection = new SqliteFileDbToMemoryLoader().loadDbIntoMemory(dbFilePathToCopy);
//        SingleConnectionDataSource dataSource = SqliteConnectionUtil.makeSingleConnectionDataSource(connection);
//        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//
//        ThreadPoolExecutor executor = makeExecutor(200);
//
//        Stopwatch totaltime = Stopwatch.createStarted();
//        for (int loop=0; loop<10; loop++) {
//            for (int i=0; i<NUM_RECORDS; i++) {
//                final int num = i;
//                executor.submit(new Runnable() {
//                    @Override public void run() {
//                        execute(num, jdbcTemplate);
//                    }
//                });
//            }
//        }
//        shutdown(executor, totaltime);
//    }

//    private void testMultithreadedReadAccessWithThreadPool() {
//        makePool();
////        DbConnector connector = SqliteDbConnector.file(dbFilePathToCopy).forReadingOnly().build();
////        SingleConnectionDataSource dataSource = SqliteConnectionUtil.makeSingleConnectionDataSource(connector.makeConnection());
////        new DataS
////        final JdbcTemplate jdbcTemplate = new JdbcTemplate(getConnection());
//
//        ThreadPoolExecutor executor = makeExecutor(1);
//
//        Stopwatch totaltime = Stopwatch.createStarted();
//        for (int loop=0; loop<10; loop++) {
//            for (int i=0; i<NUM_RECORDS; i++) {
//                final int num = i;
//                executor.submit(new Runnable() {
//                    @Override public void run() {
//                        JdbcTemplate jdbcTemplate = new JdbcTemplate(cpds);
//                        execute(num, jdbcTemplate);
//                    }
//                });
//            }
//        }
//        shutdown(executor, totaltime);
//    }




    private void shutdown(ThreadPoolExecutor executor, Stopwatch totaltime) {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("Finished in "+totaltime.elapsed(TimeUnit.MILLISECONDS)+"ms");
        } catch (InterruptedException e) {
            System.out.println("Failed finishing");
        }
    }

    private void execute(int num, JdbcTemplate jdbcTemplate) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        int count = jdbcTemplate.queryForInt("select count(*) from test where field1 = ?", Util.md5(Integer.valueOf(num).toString()));
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        assertTrue(elapsed <= 100, ""+elapsed);
        assertEquals(1, count);
    }

    private ComboPooledDataSource cpds = new ComboPooledDataSource();
    private void makePool() {
        try {
            cpds=new ComboPooledDataSource();
            cpds.setDriverClass("org.sqlite.JDBC");
            cpds.setJdbcUrl("jdbc:sqlite://" + dbFilePathToCopy);
            cpds.setMaxPoolSize(20);
            cpds.setMinPoolSize(20);
            //cpds.setAcquireIncrement(Accomodation);
        } catch (PropertyVetoException ex) {
            throw new RuntimeException(ex);
        }
    }
    private Connection getConnection() {
        try {
            return cpds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
