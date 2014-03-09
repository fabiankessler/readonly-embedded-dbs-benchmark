package com.optimaize.labs.dbperf.testdbconfig;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Access to all {@link QueriesConfig query config setups}.
 *
 * @author Fabian Kessler
 */
public class QueriesConfigs {

    public static List<QueriesConfig> all() {
        return ImmutableList.of(
                new FastQueriesConfig(),
                new SlowQueriesConfig()
        );
    }

}
