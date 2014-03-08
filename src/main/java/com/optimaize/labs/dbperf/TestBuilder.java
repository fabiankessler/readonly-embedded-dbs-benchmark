package com.optimaize.labs.dbperf;

/**
 * ...
 *
 * @author Fabian Kessler
 */
class TestBuilder {


    private Database database;

    private int numRecords;
    private boolean indexed;

    private Integer connectionPoolSize = null;
    private Integer threadPoolSize = null;

    private int testIterations;


    public TestBuilder database(Database database) {
        this.database = database;
        return this;
    }

    public TestBuilder numRecords(int numRecords) {
        this.numRecords = numRecords;
        return this;
    }

    public TestBuilder indexed(boolean indexed) {
        this.indexed = indexed;
        return this;
    }

    public TestBuilder connectionPool(int poolSize) {
        if (poolSize<1) throw new IllegalArgumentException();
        this.connectionPoolSize = poolSize;
        return this;
    }
    public TestBuilder singleSharedConnection() {
        this.connectionPoolSize = null;
        return this;
    }

    public TestBuilder threadPool(int poolSize) {
        if (poolSize<1) throw new IllegalArgumentException();
        this.threadPoolSize = poolSize;
        return this;
    }
    public TestBuilder singleThreaded() {
        this.threadPoolSize = null;
        return this;
    }

    public TestBuilder testIterations(int testIterations) {
        this.testIterations = testIterations;
        return this;
    }


    public DbTestRunner build() {
        return new DbTestRunner(
                database,
                numRecords, indexed,
                connectionPoolSize, threadPoolSize,
                testIterations);
    }

}
