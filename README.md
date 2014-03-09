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


### Conclusion

These are my current findings from what I've learned from these tests, and what I've read on the net.
Please correct me if I'm wrong, and my setup can be improved for a certain use case.

For fast queries:

* SQLite is 2x as fast as H2 for very small data
* H2 is faster the larger the db gets

For slow queries:

* SQLite with connection pool wins. This is the only acceptable setup.
* H2 uses synchronization internally, and that behaves as it was single-threaded.
  When one query takes long, others effectively have to wait. Even if all queries
  are read-only.

Therefore: If you know that you don't have any slow queries ever, you can choose the
best setup based on the numbers; SQLite for tiny, H2 for larger DBs.

If you possibly or certainly have slow queries, with concurrent requests,
*SQLite with a thread pool is the only viable option*.


### How To Use

To run the tests yourself, just start the AllPerformanceExecutor app.

To run the tests for one db only, run for example the SqlitePerformanceExecutor app.

To change a different scenario, take a look at QueriesConfigs to add or modify what is currently active.
There you can for example change the number of records in the test db.


### Open Questions / TODO

* See GitHub Issues list.


### Test Data Structure

The test database table contains 1-n records. There are tests with varying sizes.

Each record has a numerical incrementing primary key starting starting at 1,
and a text field that stores the md5 value of the pk (eg 1 = "c4ca4238a0b923820dcc509a6f75849b").

Queries are run against that md5 field. There are 'fast' query tests where this field
has an index, and 'slow' where not.


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
