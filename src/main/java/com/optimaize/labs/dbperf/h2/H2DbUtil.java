package com.optimaize.labs.dbperf.h2;

import com.optimaize.labs.dbperf.DbUtil;

import java.io.File;
import java.sql.Connection;

/**
 * @author Fabian Kessler
 */
public class H2DbUtil implements DbUtil {

    private static final String fileSuffix = ".h2.db";


    @Override
    public String connectionStringForImporting(String pathWithFilename) {
        if (new File(pathWithFilename).exists()) {
            throw new IllegalArgumentException("Already exists, clean up first: "+pathWithFilename);
        }
        String connectionString = makeConnString(pathWithFilename);
        connectionString += ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0"; //see http://www.h2database.com/html/performance.html
        return connectionString;
    }

    @Override
    public String connectionStringForReadonly(String pathWithFilename) {
        if (!new File(pathWithFilename).exists()) {
            throw new IllegalArgumentException("DB does not exist: "+pathWithFilename);
        }
        String connectionString = makeConnString(pathWithFilename);
        connectionString += ";IFEXISTS=TRUE";
        connectionString += ";ACCESS_MODE_DATA=r";
        return connectionString;
    }

    private String makeConnString(String pathWithFilename) {
        return "jdbc:h2:file:" +removeSuffix(pathWithFilename);
    }

    /**
     * Example: "foo.h2.db" => "foo"
     */
    private static String removeSuffix(String dbFile) throws IllegalArgumentException {
        if (!dbFile.endsWith(fileSuffix)) {
            throw new IllegalArgumentException("Not a valid h2 db file name: "+dbFile);
        }
        return dbFile.substring(0, dbFile.length() - fileSuffix.length());
    }


    @Override
    public void setPropertiesForImporting(Connection connection) {
        //nothing to do
    }

    @Override
    public void setPropertiesForReadonly(Connection connection) {
        //nothing to do
    }
}
