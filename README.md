readonly-embedded-dbs-performance-tests
=======================================

Tests the SQLite and H2 embedded databases in readonly mode for performance.

It is pretty simple to add support for other DBs like HSQLDB, Apache Derby etc.

The goal is to find the best method of connecting, and to learn about the performance characteristics.

The connection options are:
* single shared connection
* connection pool

The tests are exclusively for a multi-threaded readonly environment.

