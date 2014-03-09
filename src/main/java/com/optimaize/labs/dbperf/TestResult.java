package com.optimaize.labs.dbperf;

/**
 * ...
 *
 * @author Fabian Kessler
 */
public class TestResult {

    private final long totalTimeMs;
    private final long longestQueryTimeMs;

    public TestResult(long totalTimeMs, long longestQueryTimeMs) {
        this.totalTimeMs = totalTimeMs;
        this.longestQueryTimeMs = longestQueryTimeMs;
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
                "totalTimeMs=" + totalTimeMs +
                ", longestQueryTimeMs=" + longestQueryTimeMs +
                '}';
    }
}
