package com.optimaize.labs.dbbench.testdbconfig;

/**
 * Immutable.
 * @author Fabian Kessler
 */
public class TestDbConfigImpl implements TestDbConfig {

    private final int numRecords;
    private final boolean indexed;
    private final int testIterations;

    public TestDbConfigImpl(int numRecords, boolean indexed, int testIterations) {
        this.numRecords = numRecords;
        this.indexed = indexed;
        this.testIterations = testIterations;
    }

    public int getNumRecords() {
        return numRecords;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public int getTestIterations() {
        return testIterations;
    }

    @Override
    public String toString() {
        return "TestDbConfigImpl{" +
                "numRecords=" + numRecords +
                ", indexed=" + indexed +
                ", testIterations=" + testIterations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestDbConfigImpl that = (TestDbConfigImpl) o;

        if (indexed != that.indexed) return false;
        if (numRecords != that.numRecords) return false;
        if (testIterations != that.testIterations) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = numRecords;
        result = 31 * result + (indexed ? 1 : 0);
        result = 31 * result + testIterations;
        return result;
    }
}
