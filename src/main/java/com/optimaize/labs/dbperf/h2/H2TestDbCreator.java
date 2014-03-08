package com.optimaize.labs.dbperf.h2;

import com.optimaize.labs.dbperf.DbUtil;
import com.optimaize.labs.dbperf.TestDbCreator;
import com.optimaize.labs.dbperf.Util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 */
public class H2TestDbCreator implements TestDbCreator {

    private static final String dbFilePath = H2TestDbCreator.class.getResource("/").getFile();
    /**
     * This db file is used for testing but it is not touched, it is copied to dbFilePathToTest.
     */
    private static final String dbFilePathToCopy = dbFilePath+"/h2_performance";

    private static final DbUtil dbUtil = new H2DbUtil();

    /**
     * @param numRecords eg 10000
     */
    @Override
    public void create(int numRecords, boolean withIndex) throws SQLException, IOException {
        String connString = dbUtil.connectionStringForImporting(dbFilePathToCopy);
        Connection conn = Util.makeSingleConnection(connString);
        dbUtil.setPropertiesForImporting(conn);
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
