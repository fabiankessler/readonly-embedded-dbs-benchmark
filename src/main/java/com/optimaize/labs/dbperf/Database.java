package com.optimaize.labs.dbperf;

import com.optimaize.labs.dbperf.databases.h2.H2DbUtil;
import com.optimaize.labs.dbperf.databases.h2.H2PerformanceExecutor;
import com.optimaize.labs.dbperf.databases.h2.H2TestDbCreator;
import com.optimaize.labs.dbperf.databases.sqlite.SqliteDbUtil;
import com.optimaize.labs.dbperf.databases.sqlite.SqlitePerformanceExecutor;
import com.optimaize.labs.dbperf.databases.sqlite.SqliteTestDbCreator;

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
        @Override public TestDbCreator newTestDbCreator() {
            return new H2TestDbCreator();
        }
        @Override public PerformanceExecutor newPerformanceExecutor() {
            return new H2PerformanceExecutor();
        }
    },

    SQLITE {
        @Override public DbUtil newDbUtil() {
            return new SqliteDbUtil();
        }
        @Override public TestDbCreator newTestDbCreator() {
            return new SqliteTestDbCreator();
        }
        @Override public PerformanceExecutor newPerformanceExecutor() {
            return new SqlitePerformanceExecutor();
        }
    };


    public abstract DbUtil newDbUtil();
    public abstract TestDbCreator newTestDbCreator();
    public abstract PerformanceExecutor newPerformanceExecutor();

    public String getTestDbPathToFile() {
        return Database.class.getResource("/temp/").getFile() + this.name().toLowerCase(Locale.ENGLISH)+"_";
    }

}
