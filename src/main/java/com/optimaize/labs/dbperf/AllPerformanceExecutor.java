package com.optimaize.labs.dbperf;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Runs all tests of all databases.
 *
 * @author Fabian Kessler
 */
public class AllPerformanceExecutor {

    private static final ResultWriter resultWriter = new ResultWriter();

    public static void main(String[] args) throws IOException, SQLException {
        Database[] values = Database.values();
        System.out.println("Running tests for "+values.length+" databases...");
        for (Database database : values) {
            PerformanceExecutor executor = database.newPerformanceExecutor();
            List<TestResult> results = executor.all();
            resultWriter.print(results);
        }
        System.out.println("Done!");
    }

}
