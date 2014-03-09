package com.optimaize.labs.dbperf;

import com.optimaize.labs.dbperf.databases.PerformanceExecutor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
        List<TestResult> allResults = new ArrayList<>();
        System.out.println("Running tests for "+values.length+" databases...");
        for (Database database : values) {
            PerformanceExecutor executor = database.newPerformanceExecutor();
            allResults.addAll(executor.runAll());
        }
        resultWriter.print(allResults);
        System.out.println("Done!");
    }

}
