readonly-embedded-dbs-benchmark
===============================

Tests *embedded sql* databases in *multi-threaded*, *readonly* mode for performance.


### Databases in test

* SQLite
* H2


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

* SQLite with connection pool wins. For some, this is the only acceptable setup.
* H2 uses synchronization internally, and that behaves as if it was single-threaded.
  When one query takes long, others effectively have to wait. Even if all queries
  are read-only.
  There are two workarounds.
  Option 1: Set MULTI_THREADED=1. This is a global setting, and hence all open dbs
            use this mode, you can't have any other. It's an experimental feature (has been
            for the last 6 years at least), and if you do writes, <a href="https://code.google.com/p/h2database/issues/detail?id=539&can=1&sort=-id">expect corrupt data</a>.
            So it is an option if all your h2 dbs are read-only.
  Option 2: Create multiple single connections to the same db, separate, and mange the
            connections yourself using some kind of pool. This is the option that the
            author Thomas Müller suggested long time ago on the mailing list. It works
            since all connections are read-only and don't do locking.

Therefore: If you know that you don't have any slow queries ever, you can choose the
best setup based on the numbers and your likings.

If you possibly or certainly have slow queries, with concurrent requests, you need one of these
configurations:

* SQLite with a connection pool
* H2 with a connection pool, and MULTI_THREADED=1


### DB-Specific Notes

##### SQLite

##### H2

The applied connection string params are:

* ACCESS_MODE_DATA=r
* MULTI_THREADED=1 see http://www.h2database.com/html/grammar.html#set_multi_threaded
* CACHE_SIZE=65536 see http://www.h2database.com/html/features.html#cache_settings



### Open Questions / TODO

* See GitHub Issues list.


### Test Data Structure

The test database table contains 1-n records. There are tests with varying sizes.

Each record has a numerical incrementing primary key starting starting at 1,
and a text field that stores the md5 value of the pk (eg 1 = "c4ca4238a0b923820dcc509a6f75849b").

Queries are run against that md5 field. There are 'fast' query tests where this field
has an index, and 'slow' that force a full table scan.


### How To Run

Java7 or later, and run with -ea -server

To run the tests yourself, just start the AllPerformanceExecutor app.

To run the tests for one db only, run for example the SqlitePerformanceExecutor app.

To change a different scenario, take a look at QueriesConfigs to add or modify what is currently active.
There you can for example change the number of records in the test db.


##### Adding other databases

Others like HSQLDB, Apache Derby etc. can be added easily. Just add it to the Database enum, and go from there.


##### Demarcation

The tests are exclusively for a multi-threaded readonly environment.
Not client/server, not nosql like key/value stores.


### Results

These results are from my aging workstation, SSD.

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
      Result: -----------------------------
        Config:
          db:            H2
          db connection: pool of 10
          threads:       thread-pool of 10
        Times:
          total time:    1403ms
          longest query: 195ms
      Result: -----------------------------
        Config:
          db:            H2
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    1837ms
          longest query: 79ms


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
          total time:    2000ms
          longest query: 101ms
      Result: -----------------------------
        Config:
          db:            H2
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    2878ms
          longest query: 75ms
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
          total time:    29554ms
          longest query: 148ms
      Result: -----------------------------
        Config:
          db:            H2
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    40967ms
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
          total time:    9146ms
          longest query: 111ms
      Result: -----------------------------
        Config:
          db:            H2
          db connection: pool of 10
          threads:       thread-pool of 10
        Times:
          total time:    10085ms
          longest query: 82ms
      Result: -----------------------------
        Config:
          db:            SQLITE
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    21556ms
          longest query: 132ms
      Result: -----------------------------
        Config:
          db:            H2
          db connection: single-shared
          threads:       thread-pool of 10
        Times:
          total time:    23014ms
          longest query: 161ms
