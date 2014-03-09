package com.optimaize.labs.dbperf;

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
    List<TestResult> all() throws IOException, SQLException;

}
