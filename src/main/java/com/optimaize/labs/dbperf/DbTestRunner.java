package com.optimaize.labs.dbperf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ...
 *
 * @author Fabian Kessler
 */
class DbTestRunner {

    @NotNull
    private Database database;

    private int numRecords;
    private boolean indexed;

    @Nullable
    private Integer connectionPoolSize = null;
    @Nullable
    private Integer threadPoolSize = null;

    private int testIterations;

    DbTestRunner(@NotNull Database database,
                 int numRecords, boolean indexed,
                 @Nullable Integer connectionPoolSize, @Nullable Integer threadPoolSize,
                 int testIterations) {
        this.database = database;
        this.numRecords = numRecords;
        this.indexed = indexed;
        this.connectionPoolSize = connectionPoolSize;
        this.threadPoolSize = threadPoolSize;
        this.testIterations = testIterations;
    }

    public void prepare() {

    }

    public void run() {

    }

    public void cleanup() {

    }

}
