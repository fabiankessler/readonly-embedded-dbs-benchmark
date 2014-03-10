package com.optimaize.labs.dbbench.databases;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Creates the db that is used for the tests on the fly.
 *
 * @author Fabian Kessler
 */
public interface TestDbCreator {

    /**
     * Creates the test db.
     * @param numRecords Puts as many records into the table.
     * @param withIndex if true then an index is created of the fields that are used in where clauses.
     */
    void create(String name, int numRecords, boolean withIndex) throws SQLException, IOException;

    /**
     * Deletes the db file if it exists, silently ignores if not.
     *
     * @throws RuntimeException if the existing file can't be deleted.
     *         This can happen on Windows when the file is still locked because of open connections.
     *
     */
    void delete(String name);

}
