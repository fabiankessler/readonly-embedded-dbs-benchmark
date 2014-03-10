package com.optimaize.labs.dbbench.databases.sqlite;

import com.optimaize.labs.dbbench.*;
import com.optimaize.labs.dbbench.databases.PerformanceExecutor;
import com.optimaize.labs.dbbench.testdbconfig.TestDbConfig;
import com.optimaize.labs.dbbench.testdbconfig.QueriesConfigs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stress-tests sqlite readonly with many threads for a while.
 *
 * @author Fabian Kessler
 */
public class SqlitePerformanceExecutor implements PerformanceExecutor {

    /**
     * Runs all sqlite tests.
     */
    public static void main(String[] args) throws IOException, SQLException {
        SqlitePerformanceExecutor executor = new SqlitePerformanceExecutor();
        new ResultWriter().print( executor.runAll() );
    }

    public SqlitePerformanceExecutor() {
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
                .database(Database.SQLITE)
                .testDbConfig(testDbConfig)
                .singleSharedConnection()
                .threadPool(10)
                .build();
        return run(runner);
    }

    public TestResult connectionPool(TestDbConfig testDbConfig) throws IOException, SQLException {
        DbTestRunner runner = new DbTestRunnerBuilder()
                .name("connectionPool")
                .database(Database.SQLITE)
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
