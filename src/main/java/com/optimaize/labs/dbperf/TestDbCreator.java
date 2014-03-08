package com.optimaize.labs.dbperf;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Creates the db that is used for the tests.
 *
 * @author Fabian Kessler
 */
public interface TestDbCreator {

    void create(int numRecords, boolean withIndex) throws SQLException, IOException;

    void delete();

}
