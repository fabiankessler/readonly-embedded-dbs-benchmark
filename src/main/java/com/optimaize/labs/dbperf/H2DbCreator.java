package com.optimaize.labs.dbperf;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 */
class H2DbCreator implements DbCreator {

    private static final String dbFilePath = H2DbCreator.class.getResource("/").getFile();
    /**
     * This db file is used for testing but it is not touched, it is copied to dbFilePathToTest.
     */
    private static final String dbFilePathToCopy = dbFilePath+"/h2_performance";

    /**
     * @param numRecords eg 10000
     */
    @Override
    public void create(int numRecords, boolean withIndex) throws SQLException, IOException {
        String connString = Util.makeH2ConnectionStringForImporting(dbFilePathToCopy);
        Connection conn = Util.makeSingleConnection(connString);
        Statement statement = conn.createStatement();
        statement.execute("create table test (id INTEGER AUTO_INCREMENT PRIMARY KEY, field1 varchar(20) not null, field2 varchar(20) not null)");
        for (int i=0; i< numRecords; i++) {
            String md5 = Util.md5( Integer.valueOf(i).toString() );
            statement.execute("insert into test (field1, field2) values('"+md5+"', '"+i+"')");
        }
        if (withIndex) {
            statement.execute("create index idx_field1 on test (field1)");
        }
        statement.close();
        conn.close();
    }

    @Override
    public void delete() {
        File dbFile = new File(dbFilePathToCopy);
        if (dbFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dbFile.delete();
        }
    }

}
