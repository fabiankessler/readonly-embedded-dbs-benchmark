package com.optimaize.labs.dbperf;

import com.optimaize.labs.dbperf.testdbconfig.TestDbConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The configuration for one {@link DbTestRunner}.
 *
 * Immutable.
 *
 * @author Fabian Kessler
 */
public class RunConfig {

    @NotNull
    private final String name;
    @NotNull
    private final Database database;

    private final TestDbConfig testDbConfig;

    @Nullable
    private final Integer connectionPoolSize;
    @Nullable
    private final Integer threadPoolSize;


    public RunConfig(@NotNull String name, @NotNull Database database,
                     @NotNull TestDbConfig testDbConfig,
                     @Nullable Integer connectionPoolSize, @Nullable Integer threadPoolSize) {
        this.name = name;
        this.database = database;
        this.testDbConfig = testDbConfig;
        this.connectionPoolSize = connectionPoolSize;
        this.threadPoolSize = threadPoolSize;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Database getDatabase() {
        return database;
    }

    @NotNull
    public TestDbConfig getTestDbConfig() {
        return testDbConfig;
    }

    @Nullable
    public Integer getConnectionPoolSize() {
        return connectionPoolSize;
    }

    @Nullable
    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }


    @Override
    public String toString() {
        return "DbTestRunConfig{" +
                "name='" + name + '\'' +
                ", database=" + database +
                ", testDbConfig=" + testDbConfig +
                ", connectionPoolSize=" + connectionPoolSize +
                ", threadPoolSize=" + threadPoolSize +
                '}';
    }
}
