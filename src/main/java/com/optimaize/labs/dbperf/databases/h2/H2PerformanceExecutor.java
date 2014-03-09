package com.optimaize.labs.dbperf.databases.h2;

import com.optimaize.labs.dbperf.*;
import com.optimaize.labs.dbperf.databases.PerformanceExecutor;
import com.optimaize.labs.dbperf.testdbconfig.TestDbConfig;
import com.optimaize.labs.dbperf.testdbconfig.QueriesConfigs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stress-tests h2 readonly with many threads for a while.
 *
 * @author Fabian Kessler
 */
public class H2PerformanceExecutor implements PerformanceExecutor {

    /**
     * Runs all h2 tests.
     */
    public static void main(String[] args) throws IOException, SQLException {
        H2PerformanceExecutor executor = new H2PerformanceExecutor();
        new ResultWriter().print(executor.runAll());
    }

    public H2PerformanceExecutor() {
    }

    @Override
    public List<TestResult> runAll() throws IOException, SQLException {
        List<TestResult> results = new ArrayList<>();
        for (TestDbConfig testDbConfig : QueriesConfigs.all()) {
            results.addAll(runForConfig(testDbConfig));
        }
        return results;
    }

    @Override
    public List<TestResult> runForConfig(TestDbConfig testDbConfig) throws IOException, SQLException {
        List<TestResult> results = new ArrayList<>();
        results.add( singleSharedConnection(testDbConfig) );
        results.add( connectionPool(testDbConfig) );
        return results;
    }


    public TestResult singleSharedConnection(TestDbConfig testDbConfig) throws IOException, SQLException {
        DbTestRunner runner = new DbTestRunnerBuilder()
                .name("singleSharedConnection")
                .database(Database.H2)
                .testDbConfig(testDbConfig)
                .singleSharedConnection()
                .threadPool(10)
                .build();
        return run(runner);
    }

    public TestResult connectionPool(TestDbConfig testDbConfig) throws IOException, SQLException {
        DbTestRunner runner = new DbTestRunnerBuilder()
                .name("connectionPool")
                .database(Database.H2)
                .testDbConfig(testDbConfig)
                .connectionPool(10)
                .threadPool(10)
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
