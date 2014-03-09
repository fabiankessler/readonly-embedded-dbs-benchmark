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


### Results

(todo insert)

### How To Use

To run the tests yourself, just start the AllPerformanceExecutor app.

To run the tests for one db only, run for example the SqlitePerformanceExecutor app.

To change a different scenario, take a look at QueriesConfigs to add or modify what is currently active.
There you can for example change the number of records in the test db.


### Open Questions

(todo write)

