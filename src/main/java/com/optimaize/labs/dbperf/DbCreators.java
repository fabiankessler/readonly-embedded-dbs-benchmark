package com.optimaize.labs.dbperf;

/**
 * ...
 *
 * @author Fabian Kessler
 */
public class DbCreators {

    public static DbCreator h2() {
        return new H2DbCreator();
    }
    public static DbCreator sqlite() {
        return new SqliteDbCreator();
    }

}
