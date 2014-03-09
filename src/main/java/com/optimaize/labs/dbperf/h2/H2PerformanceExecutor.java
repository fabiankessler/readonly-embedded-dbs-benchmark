package com.optimaize.labs.dbperf.h2;

import com.google.common.collect.ImmutableList;
import com.optimaize.labs.dbperf.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Stress-tests h2 readonly with many threads for a while.
 *
 * @author Fabian Kessler
 */
public class H2PerformanceExecutor implements PerformanceExecutor {

    public static void main(String[] args) throws IOException, SQLException {
        H2PerformanceExecutor executor = new H2PerformanceExecutor();
        new ResultWriter().print(executor.all());
    }

    public H2PerformanceExecutor() {
    }

    @Override
    public List<TestResult> all() throws IOException, SQLException {
        return ImmutableList.of(
                singleSharedConnection(),
                connectionPool()
        );
    }


    public TestResult singleSharedConnection() throws IOException, SQLException {
        DbTestRunner runner = new DbTestRunnerBuilder()
                .name("singleSharedConnection")
                .database(Database.H2)
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
                .database(Database.H2)
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
