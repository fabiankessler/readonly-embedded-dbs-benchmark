package com.optimaize.labs.dbperf.sqlite;

import com.optimaize.labs.dbperf.Database;
import com.optimaize.labs.dbperf.DbTestRunner;
import com.optimaize.labs.dbperf.DbTestRunnerBuilder;
import com.optimaize.labs.dbperf.TestResult;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Stress-tests sqlite readonly with many threads for a while.
 *
 * @author Fabian Kessler
 */
public class SqliteTest {

    @Test
    public void a_singleSharedConnection() throws IOException, SQLException {
        DbTestRunner runner = new DbTestRunnerBuilder()
                .name("a")
                .database(Database.SQLITE)
                .numRecords(10000)
                .indexed(true)
                .singleSharedConnection()
                .threadPool(10)
                .testIterations(1)
                .build();
        TestResult testResult = run(runner);
        System.out.println(testResult);
    }

    @Test
    public void b_connectionPool() throws IOException, SQLException {
        DbTestRunner runner = new DbTestRunnerBuilder()
                .name("b")
                .database(Database.SQLITE)
                .numRecords(10000)
                .indexed(true)
                .connectionPool(10)
                .threadPool(10)
                .testIterations(1)
                .build();
        TestResult testResult = run(runner);
        System.out.println(testResult);
    }

    private TestResult run(DbTestRunner runner) throws IOException, SQLException {
        runner.cleanup(true);
        runner.prepare();
        TestResult testResult = runner.run();
        runner.cleanup(false);
        return testResult;
    }

}
