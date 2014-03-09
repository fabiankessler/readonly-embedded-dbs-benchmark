package com.optimaize.labs.dbperf;

import com.optimaize.labs.dbperf.h2.H2DbUtil;
import com.optimaize.labs.dbperf.h2.H2TestDbCreator;
import com.optimaize.labs.dbperf.sqlite.SqliteDbUtil;
import com.optimaize.labs.dbperf.sqlite.SqliteTestDbCreator;

import java.util.Locale;

/**
 * The currently supported DBs for these performance tests.
 *
 * @author Fabian Kessler
 */
public enum Database {

    /*
     * To add another db just add it here and make the code compile, that's all.
     */


    H2 {
        @Override public DbUtil newDbUtil() {
            return new H2DbUtil();
        }
        @Override public TestDbCreator makeTestDbCreator() {
            return new H2TestDbCreator();
        }
    },

    SQLITE {
        @Override public DbUtil newDbUtil() {
            return new SqliteDbUtil();
        }
        @Override public TestDbCreator makeTestDbCreator() {
            return new SqliteTestDbCreator();
        }
    };


    public abstract DbUtil newDbUtil();
    public abstract TestDbCreator makeTestDbCreator();

    public String getTestDbPathToFile() {
        return Database.class.getResource("/temp/").getFile() + this.name().toLowerCase(Locale.ENGLISH)+"_";
    }

}
