package com.optimaize.labs.dbperf;

/**
 * ...
 *
 * @author Fabian Kessler
 */
public class TestResult implements Comparable<TestResult> {

    private final DbTestRunner dbTestRunner;
    private final long totalTimeMs;
    private final long longestQueryTimeMs;

    public TestResult(DbTestRunner dbTestRunner, long totalTimeMs, long longestQueryTimeMs) {
        this.dbTestRunner = dbTestRunner;
        this.totalTimeMs = totalTimeMs;
        this.longestQueryTimeMs = longestQueryTimeMs;
    }

    public DbTestRunner getDbTestRunner() {
        return dbTestRunner;
    }

    public long getTotalTimeMs() {
        return totalTimeMs;
    }

    public long getLongestQueryTimeMs() {
        return longestQueryTimeMs;
    }


    @Override
    public String toString() {
        return "TestResult{" +
                "runner=" + dbTestRunner +
                "totalTimeMs=" + totalTimeMs +
                ", longestQueryTimeMs=" + longestQueryTimeMs +
                '}';
    }

    @Override
    public int compareTo(TestResult o) {
        int compare = Long.compare(totalTimeMs, o.totalTimeMs);
        if (compare!=0) return compare;

        compare = Long.compare(longestQueryTimeMs, o.longestQueryTimeMs);
        if (compare!=0) return compare;

        return dbTestRunner.getDbTestRunConfig().getName().compareTo(o.getDbTestRunner().getDbTestRunConfig().getName());
    }
}
