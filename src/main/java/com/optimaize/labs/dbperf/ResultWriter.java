package com.optimaize.labs.dbperf;

import com.google.common.collect.ArrayListMultimap;
import com.optimaize.labs.dbperf.testdbconfig.TestDbConfig;

import java.util.*;

/**
 * Prints the result nicely to standard out.
 *
 * @author Fabian Kessler
 */
public class ResultWriter {
    
    public void print(List<TestResult> results) {
        ArrayListMultimap<TestDbConfig,TestResult> grouped = groupByTestDbConfig(results);
        for (Map.Entry<TestDbConfig, Collection<TestResult>> group : grouped.asMap().entrySet()) {
            _print(group.getKey());
            _print( sort(group.getValue()) );
        }
    }

    private void _print(TestDbConfig cfg) {
        p("=====================================");
        p("  DB Config");
        p("    test db:       "+cfg.getNumRecords()+" records, "+(cfg.isIndexed() ? "indexed" : "not indexed"));
        p("    test run:      "+cfg.getTestIterations()+" test iterations");
        p("  Results: --------------------------");
    }

    private ArrayListMultimap<TestDbConfig,TestResult> groupByTestDbConfig(List<TestResult> results) {
        ArrayListMultimap<TestDbConfig,TestResult> map = ArrayListMultimap.create();
        for (TestResult result : results) {
            map.put(result.getDbTestRunner().getDbTestRunConfig().getTestDbConfig(), result);
        }
        return map;
    }

    private void _print(List<TestResult> results) {
        for (TestResult result : results) {
            RunConfig cfg = result.getDbTestRunner().getDbTestRunConfig();
            p("=====================================");
            p("Result: -----------------------------");
            p("  Times:");
            p("    total time:    "+result.getTotalTimeMs()+"ms");
            p("    longest query: "+result.getLongestQueryTimeMs()+"ms");
            p("  Config:");
            p("    db:            "+cfg.getDatabase());
            p("    db connection: "+ ((cfg.getConnectionPoolSize()==null) ? "single-shared" : "pool of "+cfg.getConnectionPoolSize()));
            p("    threads:       "+ ((cfg.getThreadPoolSize()==null) ? "single-threaded" : "thread-pool of "+cfg.getThreadPoolSize()));
            //p("    test db:       "+cfg.getTestDbConfig().getNumRecords()+" records, "+(cfg.getTestDbConfig().isIndexed() ? "indexed" : "not indexed"));
            //p("    test run:      "+cfg.getTestDbConfig().getTestIterations()+" test iterations");
            p("=====================================");
        }
    }

    private void p(String s) {
        System.out.println(s);
    }

    private List<TestResult> sort(Collection<TestResult> results) {
        List<TestResult> r = new ArrayList<>(results);
        Collections.sort(r);
        return r;
    }

}
