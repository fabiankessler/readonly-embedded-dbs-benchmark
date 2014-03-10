package com.optimaize.labs.dbperf.databases.h2;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This demonstrates something I consider a bug on an annoyance with H2.
 *
 * @author Fabian Kessler
 */
public class ConnectionTest {

    public static final String FILE = "c:/h2tempdb";

    @AfterTest
    private void clean() {
        new File(FILE+".h2.db").delete();
    }

    @Test(expectedExceptions = java.sql.SQLException.class)
    public void thisFails() throws SQLException {
        run(false);
    }

    @Test
    public void thisWorks() throws SQLException {
        run(true);
    }

    private void run(boolean resetLockModeTo3) throws SQLException {
        //at first we connect using the suggested "quick importing" properties
        //as found here:
        //http://www.h2database.com/html/performance.html at the end "Fast Database Import"
        String connectionString = "jdbc:h2:file:" + FILE;
        connectionString += ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0";
        Connection connection = DriverManager.getConnection(connectionString);
        connection.close();

        //then we connect for readonly mode.
        connectionString = "jdbc:h2:file:" + FILE;
        connectionString += ";IFEXISTS=TRUE";
        connectionString += ";ACCESS_MODE_DATA=r";
        connectionString += ";MULTI_THREADED=1"; //see http://www.h2database.com/html/grammar.html#set_multi_threaded
        if (resetLockModeTo3) {
            //in order to be able to open this way, we need to reset the global lock mode.
            //a user posted this here:
            //https://groups.google.com/forum/#!msg/h2-database/chvHwUZ20xQ/sYgNWLVSUXQJ
            connectionString += ";LOCK_MODE=3";
        }
        connection = DriverManager.getConnection(connectionString);
        connection.close();
    }

}
