package com.optimaize.labs.dbbench.testdbconfig;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Access to all {@link TestDbConfig query config setups}.
 *
 * @author Fabian Kessler
 */
public class QueriesConfigs {

    public static List<TestDbConfig> all() {
        return ImmutableList.of(
                indexedOneThousand(),
                indexedTenThousand(),
                indexedOneMillion(),
                fulltablescanTenThousand()
        );
    }

    /**
     * Configuration for fast (indexed) queries that only take a few ms per query, and thus don't let others
     * wait for long.
     */
    public static TestDbConfig indexedOneThousand() {
        return new TestDbConfigImpl(1000, true, 10);
    }
    public static TestDbConfig indexedTenThousand() {
        return new TestDbConfigImpl(10000, true, 10);
    }
    public static TestDbConfig indexedOneMillion() {
        return new TestDbConfigImpl(1000000, true, 1);
    }

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
     */
    public static TestDbConfig fulltablescanTenThousand() {
        return new TestDbConfigImpl(10000, false, 1);
    }

}
