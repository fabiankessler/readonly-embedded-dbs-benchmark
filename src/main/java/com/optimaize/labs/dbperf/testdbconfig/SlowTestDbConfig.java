package com.optimaize.labs.dbperf.testdbconfig;

/**
 * Configuration for slow (non-indexed) queries that can take hundreds of ms, and thus let others wait
 * if the queries are run sequentially.
 *
 * The queries may be run sequentially for the reasons:
 *  - userland code uses a single thread instead of a thread pool
 *  - the db runs multiple pending queries sequentially, because of internal locking or synchronization
 *    on the data or the cache or whatever.
 *
 * Since we're looking for the best performance in a multi-threaded readonly scenario, this is what we
 * are trying to prevent.
 *
 * @author Fabian Kessler
 */
public class SlowTestDbConfig implements TestDbConfig {

    @Override
    public int getNumRecords() {
        return 10000;
    }

    @Override
    public boolean isIndexed() {
        return false;
    }

    @Override
    public int getTestIterations() {
        return 1;
    }

}
