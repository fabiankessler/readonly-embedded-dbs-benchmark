package com.optimaize.labs.dbperf;

import java.sql.Connection;

/**
 * DB-specific functionality.
 *
 * @author Fabian Kessler
 */
public interface DbUtil {

    String getDriverClassName();

    String connectionStringForImporting(String pathWithFilename);
    String connectionStringForReadonly(String pathWithFilename);

    void setPropertiesForImporting(Connection connection);
    void setPropertiesForReadonly(Connection connection);
}
