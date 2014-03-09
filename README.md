readonly-embedded-dbs-performance-tests
=======================================

Tests *embedded sql* databases in *multi-threaded*, *readonly* mode for performance.


### Databases in test

* SQLite
* H2


##### Adding other databases

Others like HSQLDB, Apache Derby etc. can be added easily. Just add it to the Database enum, and go from there.


##### Demarcation

The tests are exclusively for a multi-threaded readonly environment.
Not client/server, not nosql like key/value stores.


### Goal

These performance tests should reveal the characteristics for different data sizes and userland configurations,
and help to pick the right one for each scenario.

The connection options are:

* single shared connection
* connection pool



### How To Use

To run the tests yourself, just start the AllPerformanceExecutor app.

To run the tests for one db only, run for example the SqlitePerformanceExecutor app.

To change a different scenario, take a look at QueriesConfigs to add or modify what is currently active.
There you can for example change the number of records in the test db.


### Open Questions

(todo write)


### Results

Tiny db, fast queries. SQLite with a single-shared db connection wins.

    =====================================
    DB Config
      test db:       1000 records, indexed
      test run:      10 test iterations
    Results: --------------------------
      Result: -----------------------------
        Config:
          db:            SQLITE
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    953ms
          longest query: 35ms
      Result: -----------------------------
        Config:
          db:            SQLITE
          db connection: pool of 10
          threads:       thread-pool of 10
        Times:
          total time:    1054ms
          longest query: 48ms
      Result: -----------------------------
        Config:
          db:            H2
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    1933ms
          longest query: 100ms
      Result: -----------------------------
        Config:
          db:            H2
          db connection: pool of 10
          threads:       thread-pool of 10
        Times:
          total time:    2295ms
          longest query: 211ms
    =====================================


Small db, fast queries. H2 with a connection pool wins.

    DB Config
      test db:       10000 records, indexed
      test run:      10 test iterations
    Results: --------------------------
      Result: -----------------------------
        Config:
          db:            H2
          db connection: pool of 10
          threads:       thread-pool of 10
        Times:
          total time:    2271ms
          longest query: 99ms
      Result: -----------------------------
        Config:
          db:            H2
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    3074ms
          longest query: 91ms
      Result: -----------------------------
        Config:
          db:            SQLITE
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    7104ms
          longest query: 112ms
      Result: -----------------------------
        Config:
          db:            SQLITE
          db connection: pool of 10
          threads:       thread-pool of 10
        Times:
          total time:    11131ms
          longest query: 238ms
    Done!


Large db, fast queries. H2 with a single-shared db connection wins.

    DB Config
      test db:       1000000 records, indexed
      test run:      1 test iterations
    Results: --------------------------
      Result: -----------------------------
        Config:
          db:            H2
          db connection: pool of 10
          threads:       thread-pool of 10
        Times:
          total time:    38244ms
          longest query: 167ms
      Result: -----------------------------
        Config:
          db:            H2
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    42369ms
          longest query: 155ms
      Result: -----------------------------
        Config:
          db:            SQLITE
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    96514ms
          longest query: 323ms
      Result: -----------------------------
        Config:
          db:            SQLITE
          db connection: pool of 10
          threads:       thread-pool of 10
        Times:
          total time:    105040ms
          longest query: 185ms
    =====================================


Small db, slow queries. SQLite with a connection pool wins.

    DB Config
      test db:       10000 records, not indexed
      test run:      1 test iterations
    Results: --------------------------
      Result: -----------------------------
        Config:
          db:            SQLITE
          db connection: pool of 10
          threads:       thread-pool of 10
        Times:
          total time:    9134ms
          longest query: 173ms
      Result: -----------------------------
        Config:
          db:            SQLITE
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    21439ms
          longest query: 172ms
      Result: -----------------------------
        Config:
          db:            H2
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    24400ms
          longest query: 122ms
      Result: -----------------------------
        Config:
          db:            H2
          db connection: pool of 10
          threads:       thread-pool of 10
        Times:
          total time:    24663ms
          longest query: 132ms
    =====================================
