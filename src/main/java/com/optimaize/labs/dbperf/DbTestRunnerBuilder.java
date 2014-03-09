package com.optimaize.labs.dbperf;

/**
 * Builder for {@link DbTestRunner}.
 *
 * @author Fabian Kessler
 */
public class DbTestRunnerBuilder {

    private String name;
    private Database database;

    private int numRecords;
    private boolean indexed;

    private Integer connectionPoolSize = null;
    private Integer threadPoolSize = null;

    private int testIterations;

    public DbTestRunnerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DbTestRunnerBuilder database(Database database) {
        this.database = database;
        return this;
    }

    public DbTestRunnerBuilder numRecords(int numRecords) {
        this.numRecords = numRecords;
        return this;
    }

    public DbTestRunnerBuilder indexed(boolean indexed) {
        this.indexed = indexed;
        return this;
    }

    public DbTestRunnerBuilder connectionPool(int poolSize) {
        if (poolSize<1) throw new IllegalArgumentException();
        this.connectionPoolSize = poolSize;
        return this;
    }
    public DbTestRunnerBuilder singleSharedConnection() {
        this.connectionPoolSize = null;
        return this;
    }

    public DbTestRunnerBuilder threadPool(int poolSize) {
        if (poolSize<1) throw new IllegalArgumentException();
        this.threadPoolSize = poolSize;
        return this;
    }
    public DbTestRunnerBuilder singleThreaded() {
        this.threadPoolSize = null;
        return this;
    }

    public DbTestRunnerBuilder testIterations(int testIterations) {
        this.testIterations = testIterations;
        return this;
    }


    public DbTestRunner build() {
        RunConfig cfg = new RunConfig(
                name, database,
                numRecords, indexed,
                connectionPoolSize, threadPoolSize,
                testIterations
        );
        return new DbTestRunnerImpl(cfg);
    }

}
