package com.optimaize.labs.dbperf;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 */
class SqliteDbCreator implements DbCreator {

    private static final String dbFilePath = SqliteDbCreator.class.getResource("/").getFile();
    /**
     * This db file is used for testing but it is not touched, it is copied to dbFilePathToTest.
     */
    private static final String dbFilePathToCopy = dbFilePath+"/sqlite_performance";

    /**
     * @param numRecords eg 10000
     */
    @Override
    public void create(int numRecords, boolean withIndex) throws SQLException, IOException {
        String connString = Util.makeSqliteConnectionStringForImporting(dbFilePathToCopy);
        Connection conn = Util.makeSingleConnection(connString);
        Util.setSqliteConnProbsForImporting(conn);
        Statement statement = conn.createStatement();
        statement.execute("create table test (id INTEGER PRIMARY KEY AUTOINCREMENT, field1 varchar(20) not null, field2 varchar(20) not null)");
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
