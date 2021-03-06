package com.optimaize.labs.dbbench;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Runs a test. Use the {@link DbTestRunnerBuilder} to create one.
 *
 * @author Fabian Kessler
 */
public interface DbTestRunner {

    @NotNull
    RunConfig getDbTestRunConfig();

    void prepare() throws IOException, SQLException;

    TestResult run();

    void cleanup(boolean throwOnFailure);

}
