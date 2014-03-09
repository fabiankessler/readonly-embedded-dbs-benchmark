package com.optimaize.labs.dbperf;

import com.optimaize.labs.dbperf.testdbconfig.TestDbConfig;

/**
 * Builder for {@link DbTestRunner}.
 *
 * @author Fabian Kessler
 */
public class DbTestRunnerBuilder {

    private String name;
    private Database database;

    private TestDbConfig testDbConfig;

    private Integer connectionPoolSize = null;
    private Integer threadPoolSize = null;


    public DbTestRunnerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DbTestRunnerBuilder database(Database database) {
        this.database = database;
        return this;
    }

    public DbTestRunnerBuilder testDbConfig(TestDbConfig testDbConfig) {
        this.testDbConfig = testDbConfig;
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


    public DbTestRunner build() {
        RunConfig cfg = new RunConfig(
                name, database,
                testDbConfig,
                connectionPoolSize, threadPoolSize
        );
        return new DbTestRunnerImpl(cfg);
    }

}
