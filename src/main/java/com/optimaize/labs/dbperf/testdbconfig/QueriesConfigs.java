package com.optimaize.labs.dbperf.testdbconfig;

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
                new FastTestDbConfig(),
                new SlowTestDbConfig()
        );
    }

}
