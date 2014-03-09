package com.optimaize.labs.dbperf;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Runs a test. Use the {@link DbTestRunnerBuilder}.
 *
 * @author Fabian Kessler
 */
public interface DbTestRunner {

    void prepare() throws IOException, SQLException;

    TestResult run();

    void cleanup(boolean throwOnFailure);

}
