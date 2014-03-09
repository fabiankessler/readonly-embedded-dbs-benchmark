package com.optimaize.labs.dbperf;

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

    private final int numRecords;
    private final boolean indexed;

    @Nullable
    private final Integer connectionPoolSize;
    @Nullable
    private final Integer threadPoolSize;

    private final int testIterations;

    public RunConfig(@NotNull String name, @NotNull Database database,
                     int numRecords, boolean indexed,
                     @Nullable Integer connectionPoolSize, @Nullable Integer threadPoolSize,
                     int testIterations) {
        this.name = name;
        this.database = database;
        this.numRecords = numRecords;
        this.indexed = indexed;
        this.connectionPoolSize = connectionPoolSize;
        this.threadPoolSize = threadPoolSize;
        this.testIterations = testIterations;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Database getDatabase() {
        return database;
    }

    public int getNumRecords() {
        return numRecords;
    }

    public boolean isIndexed() {
        return indexed;
    }

    @Nullable
    public Integer getConnectionPoolSize() {
        return connectionPoolSize;
    }

    @Nullable
    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public int getTestIterations() {
        return testIterations;
    }


    @Override
    public String toString() {
        return "DbTestRunConfig{" +
                "name='" + name + '\'' +
                ", database=" + database +
                ", numRecords=" + numRecords +
                ", indexed=" + indexed +
                ", connectionPoolSize=" + connectionPoolSize +
                ", threadPoolSize=" + threadPoolSize +
                ", testIterations=" + testIterations +
                '}';
    }
}
