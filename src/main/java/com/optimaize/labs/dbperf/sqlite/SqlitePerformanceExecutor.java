package com.optimaize.labs.dbperf.sqlite;

import com.google.common.collect.ImmutableList;
import com.optimaize.labs.dbperf.Database;
import com.optimaize.labs.dbperf.DbTestRunner;
import com.optimaize.labs.dbperf.DbTestRunnerBuilder;
import com.optimaize.labs.dbperf.TestResult;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Stress-tests sqlite readonly with many threads for a while.
 *
 * @author Fabian Kessler
 */
public class SqlitePerformanceExecutor {

    public static void main(String[] args) throws IOException, SQLException {
        SqlitePerformanceExecutor executor = new SqlitePerformanceExecutor();
        System.out.println(executor.all());
    }

    public SqlitePerformanceExecutor() {
    }

    public List<TestResult> all() throws IOException, SQLException {
        return ImmutableList.of(
            singleSharedConnection(),
            connectionPool()
        );
    }


    public TestResult singleSharedConnection() throws IOException, SQLException {
        DbTestRunner runner = new DbTestRunnerBuilder()
                .name("singleSharedConnection")
                .database(Database.SQLITE)
                .numRecords(10000)
                .indexed(true)
                .singleSharedConnection()
                .threadPool(10)
                .testIterations(1)
                .build();
        return run(runner);
    }

    public TestResult connectionPool() throws IOException, SQLException {
        DbTestRunner runner = new DbTestRunnerBuilder()
                .name("connectionPool")
                .database(Database.SQLITE)
                .numRecords(10000)
                .indexed(true)
                .connectionPool(10)
                .threadPool(10)
                .testIterations(1)
                .build();
        return run(runner);
    }

    private TestResult run(DbTestRunner runner) throws IOException, SQLException {
        runner.cleanup(true);
        runner.prepare();
        TestResult testResult = runner.run();
        runner.cleanup(false);
        return testResult;
    }

}
