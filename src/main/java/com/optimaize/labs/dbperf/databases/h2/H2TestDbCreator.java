package com.optimaize.labs.dbperf.databases.h2;

import com.optimaize.labs.dbperf.Database;
import com.optimaize.labs.dbperf.databases.TestDbCreator;
import com.optimaize.labs.dbperf.util.Util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 */
public class H2TestDbCreator implements TestDbCreator {

    private static final H2DbUtil dbUtil = new H2DbUtil();

    /**
     * @param numRecords eg 10000
     */
    @Override
    public void create(String name, int numRecords, boolean withIndex) throws SQLException, IOException {
        String connString = dbUtil.connectionStringForImporting(Database.H2.getTestDbPathToFile()+name);
        Connection conn = Util.makeSingleConnection(connString);
        dbUtil.setPropertiesForImporting(conn);
        Statement statement = conn.createStatement();
        statement.execute("create table test (id INTEGER AUTO_INCREMENT PRIMARY KEY, field1 varchar(32) not null, field2 varchar(32) not null)");
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
    public void delete(String name) {
        String fullPath = Database.H2.getTestDbPathToFile() + name;
        fullPath = dbUtil.addSuffix(fullPath);
        File dbFile = new File(fullPath);
        if (dbFile.exists()) {
            Util.deleteFile(dbFile, 1000);
        }
    }

}
