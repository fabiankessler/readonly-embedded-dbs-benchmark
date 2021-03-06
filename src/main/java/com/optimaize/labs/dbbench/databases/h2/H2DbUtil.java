package com.optimaize.labs.dbbench.databases.h2;

import com.google.common.base.Optional;
import com.optimaize.labs.dbbench.databases.DbUtil;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;

/**
 * @author Fabian Kessler
 */
public class H2DbUtil implements DbUtil {

    private static final String fileSuffix = ".h2.db";


    @Override
    public String getDriverClassName() {
        return "org.h2.Driver";
    }

    @Override
    public String connectionStringForImporting(String pathWithFilename) {
        pathWithFilename = addSuffixIfMissing(pathWithFilename);
        if (new File(pathWithFilename).exists()) {
            throw new IllegalArgumentException("Already exists, clean up first: "+pathWithFilename);
        }
        String connectionString = makeConnString(pathWithFilename);
        connectionString += ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0"; //see http://www.h2database.com/html/performance.html at the end "Fast Database Import"
        return connectionString;
    }

    @Override
    public String connectionStringForReadonly(String pathWithFilename) {
        pathWithFilename = addSuffixIfMissing(pathWithFilename);
        if (!new File(pathWithFilename).exists()) {
            throw new IllegalArgumentException("DB does not exist: "+pathWithFilename);
        }
        String connectionString = makeConnString(pathWithFilename);
        connectionString += ";IFEXISTS=TRUE";
        connectionString += ";ACCESS_MODE_DATA=r";
        connectionString += ";CACHE_SIZE=65536"; //see http://www.h2database.com/html/features.html#cache_settings
        connectionString += ";LOCK_MODE=3"; //see https://groups.google.com/forum/#!msg/h2-database/chvHwUZ20xQ/sYgNWLVSUXQJ
        connectionString += ";MULTI_THREADED=1"; //see http://www.h2database.com/html/grammar.html#set_multi_threaded
        return connectionString;
    }

    private String makeConnString(String pathWithFilename) {
        return "jdbc:h2:file:" +removeSuffix(pathWithFilename);
    }

    /**
     * Example: "foo" => "foo.h2.db"
     */
    public String addSuffix(String dbFile) throws IllegalArgumentException {
        if (dbFile.endsWith(fileSuffix)) {
            throw new IllegalArgumentException("Already has an h2 file name suffix: "+dbFile);
        }
        return dbFile + fileSuffix;
    }
    public String addSuffixIfMissing(String dbFile) throws IllegalArgumentException {
        if (dbFile.endsWith(fileSuffix)) return dbFile;
        return addSuffix(dbFile);
    }
    /**
     * Example: "foo.h2.db" => "foo"
     */
    public String removeSuffix(String dbFile) throws IllegalArgumentException {
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

    @Override
    public Optional<DataSource> dataSourceForReadonly(String pathWithFilename) {
        return Optional.absent();
    }
}
