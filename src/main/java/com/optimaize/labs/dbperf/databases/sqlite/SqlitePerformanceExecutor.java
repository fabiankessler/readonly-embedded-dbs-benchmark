package com.optimaize.labs.dbperf.databases.sqlite;

import com.optimaize.labs.dbperf.*;

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
        for (QueriesConfig queriesConfig : QueriesConfigs.all()) {
            results.addAll(runForConfig(queriesConfig));
        }
        return results;
    }

    @Override
    public List<TestResult> runForConfig(QueriesConfig queriesConfig) throws IOException, SQLException {
        List<TestResult> results = new ArrayList<>();
        results.add( singleSharedConnection(queriesConfig) );
        results.add( connectionPool(queriesConfig) );
        return results;
    }


    public TestResult singleSharedConnection(QueriesConfig queriesConfig) throws IOException, SQLException {
        DbTestRunner runner = new DbTestRunnerBuilder()
                .name("singleSharedConnection")
                .database(Database.SQLITE)
                .numRecords(queriesConfig.getNumRecords())
                .indexed(queriesConfig.isIndexed())
                .singleSharedConnection()
                .threadPool(10)
                .testIterations(queriesConfig.getTestIterations())
                .build();
        return run(runner);
    }

    public TestResult connectionPool(QueriesConfig queriesConfig) throws IOException, SQLException {
        DbTestRunner runner = new DbTestRunnerBuilder()
                .name("connectionPool")
                .database(Database.SQLITE)
                .numRecords(queriesConfig.getNumRecords())
                .indexed(queriesConfig.isIndexed())
                .connectionPool(10)
                .threadPool(10)
                .testIterations(queriesConfig.getTestIterations())
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
