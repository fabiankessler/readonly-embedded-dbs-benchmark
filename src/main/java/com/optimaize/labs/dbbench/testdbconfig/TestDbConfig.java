package com.optimaize.labs.dbbench.testdbconfig;

/**
 * Configuration for the test run.
 *
 * @author Fabian Kessler
 */
public interface TestDbConfig {
    /**
     * How many records are to be inserted into the test db.
     */
    int getNumRecords();

    /**
     * Whether the fields used in query 'where' clauses should be indexed, and thus fast.
     */
    boolean isIndexed();

    /**
     * How many times the query iteration loop should run. The higher the longer the whole test runs.
     * You may want to adjust this in relation to the other values in this class.
     */
    int getTestIterations();
}
