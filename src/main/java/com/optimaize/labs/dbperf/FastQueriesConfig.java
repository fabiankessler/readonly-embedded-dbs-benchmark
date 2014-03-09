package com.optimaize.labs.dbperf;

/**
 * Configuration for fast (indexed) queries that only take a few ms per query, and thus don't let others
 * wait for long.
 *
 * @author Fabian Kessler
 */
public class FastQueriesConfig implements QueriesConfig {

    @Override
    public int getNumRecords() {
        return 10000;
    }

    @Override
    public boolean isIndexed() {
        return true;
    }

    @Override
    public int getTestIterations() {
        return 10;
    }
}
