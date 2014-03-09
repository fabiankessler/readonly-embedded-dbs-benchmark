package com.optimaize.labs.dbperf;

import java.sql.Connection;

/**
 * Provides db-implementation specific functionality.
 *
 * @author Fabian Kessler
 */
public interface DbUtil {

    /**
     * Returns the fully qualified class name for the db driver.
     */
    String getDriverClassName();


    /**
     * Creates a db connection string for the given path to a file for quick data insertion.
     * @throws IllegalArgumentException if the file exists already
     */
    String connectionStringForImporting(String pathWithFilename) throws IllegalArgumentException;

    /**
     * Creates a db connection string for the given path to an existing file for readonly lookups.
     * @throws IllegalArgumentException if the file does not exist already
     */
    String connectionStringForReadonly(String pathWithFilename) throws IllegalArgumentException;


    /**
     * May set properties on the connection for the quick data insertion operation mode.
     */
    void setPropertiesForImporting(Connection connection);

    /**
     * May set properties on the connection for the readonly lookup mode.
     */
    void setPropertiesForReadonly(Connection connection);
}
