package com.optimaize.labs.dbperf.databases;

import com.optimaize.labs.dbperf.testdbconfig.QueriesConfig;
import com.optimaize.labs.dbperf.TestResult;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Runs performance tests.
 *
 * @author Fabian Kessler
 */
public interface PerformanceExecutor {

    /**
     * Runs all tests, and returns the results in execution order.
     */
    List<TestResult> runAll() throws IOException, SQLException;

    List<TestResult> runForConfig(QueriesConfig queriesConfig) throws IOException, SQLException;
}
