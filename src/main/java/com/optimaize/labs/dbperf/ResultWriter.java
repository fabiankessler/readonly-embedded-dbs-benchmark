package com.optimaize.labs.dbperf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Prints the result nicely to standard out.
 *
 * @author Fabian Kessler
 */
public class ResultWriter {
    
    public void print(List<TestResult> results) {
        results = sort(results);
        _print(results);
    }

    private void _print(List<TestResult> results) {
        for (TestResult result : results) {
            p("=====================================");
            p("Result: -----------------------------");
            DbTestRunConfig cfg = result.getDbTestRunner().getDbTestRunConfig();
            p("  Times:");
            p("    total time:    "+result.getTotalTimeMs()+"ms");
            p("    longest query: "+result.getLongestQueryTimeMs()+"ms");
            p("  Config:");
            p("    db:          "+cfg.getDatabase());
            p("    connection:  "+ ((cfg.getConnectionPoolSize()==null) ? "single-shared" : "pool of "+cfg.getConnectionPoolSize()));
            p("    thread-pool: "+ ((cfg.getThreadPoolSize()==null) ? "none, single-threaded" : "pool of "+cfg.getThreadPoolSize()));
            p("    test db:     "+cfg.getNumRecords()+" records, "+(cfg.isIndexed() ? "indexed" : "not indexed"));
            p("    test run:    "+cfg.getTestIterations()+" test iterations");
            p("=====================================");
        }
    }

    private void p(String s) {
        System.out.println(s);
    }

    private List<TestResult> sort(List<TestResult> results) {
        List<TestResult> r = new ArrayList<>(results);
        Collections.sort(r);
        return r;
    }

}
