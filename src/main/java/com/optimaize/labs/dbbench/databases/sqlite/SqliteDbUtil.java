package com.optimaize.labs.dbbench.databases.sqlite;

import com.google.common.base.Optional;
import com.optimaize.labs.dbbench.databases.DbUtil;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
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
    }

    @Override
    public Optional<DataSource> dataSourceForReadonly(String pathWithFilename) {
        org.sqlite.SQLiteConfig config = new org.sqlite.SQLiteConfig();
        config.setReadOnly(true);
        config.setPageSize(4096); //in bytes
        config.setCacheSize(2000); //number of pages
        config.setSynchronous(SQLiteConfig.SynchronousMode.OFF);
        config.setJournalMode(SQLiteConfig.JournalMode.OFF);

        // get an unpooled SQLite DataSource with the desired configuration
        SQLiteDataSource unpooled = new SQLiteDataSource( config );
        unpooled.setUrl(connectionStringForReadonly(pathWithFilename));
        return Optional.of((DataSource)unpooled);
    }

}
