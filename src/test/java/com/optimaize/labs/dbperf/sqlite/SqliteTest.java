package com.optimaize.labs.dbperf.sqlite;

import com.optimaize.labs.dbperf.Database;
import com.optimaize.labs.dbperf.DbTestRunner;
import com.optimaize.labs.dbperf.DbTestRunnerBuilder;
import com.optimaize.labs.dbperf.TestResult;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Stress-tests sqlite readonly with many threads for a while.
 *
 * @author Fabian Kessler
 */
public class SqliteTest {

    @Test
    public void test() throws IOException, SQLException {
        DbTestRunner runner = new DbTestRunnerBuilder()
                .database(Database.SQLITE)
                .numRecords(10000)
                .indexed(true)
//                .connectionPool(20)
                .singleSharedConnection()
                .threadPool(10)
                .testIterations(10)
                .build();

        runner.cleanup();
        runner.prepare();
        TestResult testResult = runner.run();
        runner.cleanup();
        System.out.println(testResult);
        System.exit(0);
    }

}
