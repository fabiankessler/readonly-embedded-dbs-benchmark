package com.optimaize.labs.dbbench.databases;

import com.optimaize.labs.dbbench.testdbconfig.TestDbConfig;
import com.optimaize.labs.dbbench.TestResult;

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

    List<TestResult> runForConfig(TestDbConfig testDbConfig) throws IOException, SQLException;
}
