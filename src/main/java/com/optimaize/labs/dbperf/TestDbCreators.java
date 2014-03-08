package com.optimaize.labs.dbperf;

import com.optimaize.labs.dbperf.h2.H2TestDbCreator;
import com.optimaize.labs.dbperf.sqlite.SqliteTestDbCreator;

/**
 * ...
 *
 * @author Fabian Kessler
 */
public class TestDbCreators {

    public static TestDbCreator h2() {
        return new H2TestDbCreator();
    }
    public static TestDbCreator sqlite() {
        return new SqliteTestDbCreator();
    }

}
