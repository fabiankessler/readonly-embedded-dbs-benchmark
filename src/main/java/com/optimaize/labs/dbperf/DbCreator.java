package com.optimaize.labs.dbperf;

import java.io.IOException;
import java.sql.SQLException;

/**
 * ...
 *
 * @author Fabian Kessler
 */
interface DbCreator {

    void create(int numRecords, boolean withIndex) throws SQLException, IOException;

    void delete();

}
