package com.optimaize.labs.dbbench.databases.sqlite;

import com.optimaize.labs.dbbench.databases.DbUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Fabian Kessler
 */
public class SqliteDbUtil implements DbUtil {


    @Override
    public String getDriverClassName() {
        return "org.sqlite.JDBC";
    }


    @Override
    public String connectionStringForImporting(String pathWithFilename) {
        if (new File(pathWithFilename).exists()) {
            throw new IllegalArgumentException("Already exists, clean up first: "+pathWithFilename);
        }
        return makeSqliteConnectionString(pathWithFilename);
    }
    @Override
    public String connectionStringForReadonly(String pathWithFilename) {
        if (!new File(pathWithFilename).exists()) {
            throw new IllegalArgumentException("DB does not exist: "+pathWithFilename);
        }
        return makeSqliteConnectionString(pathWithFilename);
    }
    private String makeSqliteConnectionString(String pathWithFilename) {
        return "jdbc:sqlite:" +pathWithFilename;
    }

    @Override
    public void setPropertiesForImporting(Connection connection) {
        try {
            //see http://www.sqlite.org/pragma.html
            connection.prepareStatement("PRAGMA synchronous=OFF;").execute();
            connection.prepareStatement("PRAGMA journal_mode=OFF;").execute();
            connection.prepareStatement("PRAGMA temp_store=3;").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setPropertiesForReadonly(Connection connection) {
        try {
            //see http://www.sqlite.org/pragma.html
            connection.prepareStatement("PRAGMA synchronous=OFF;").execute();
            connection.prepareStatement("PRAGMA journal_mode=OFF;").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        try {
//            //java.sql.SQLException: Cannot set read-only flag after establishing a connection. Use SQLiteConfig#setReadOnly and SQLiteConfig.createConnection().
//            connection.setReadOnly(true);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

//    private void connPool() {
//        org.sqlite.SQLiteConfig config = new org.sqlite.SQLiteConfig();
//        config.setReadOnly(true);
//        config.setPageSize(4096); //in bytes
//        config.setCacheSize(2000); //number of pages
//        config.setSynchronous(SQLiteConfig.SynchronousMode.OFF);
//        config.setJournalMode(SQLiteConfig.JournalMode.OFF);
//
//        SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
//        dataSource.setInitialPoolSize()
//    }

}
