package com.optimaize.labs.dbbench.databases.sqlite;

import com.optimaize.labs.dbbench.Database;
import com.optimaize.labs.dbbench.databases.DbUtil;
import com.optimaize.labs.dbbench.databases.TestDbCreator;
import com.optimaize.labs.dbbench.util.Util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 */
public class SqliteTestDbCreator implements TestDbCreator {

    private static final DbUtil dbUtil = new SqliteDbUtil();


    /**
     * @param numRecords eg 10000
     */
    @Override
    public void create(String name, int numRecords, boolean withIndex) throws SQLException, IOException {
        String connString = dbUtil.connectionStringForImporting(Database.SQLITE.getTestDbPathToFile()+name);
        Connection conn = Util.makeSingleConnection(connString);
        dbUtil.setPropertiesForImporting(conn);
        Statement statement = conn.createStatement();
        statement.execute("create table test (id INTEGER PRIMARY KEY AUTOINCREMENT, field1 varchar(32) not null, field2 varchar(32) not null)");
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
        File dbFile = new File(Database.SQLITE.getTestDbPathToFile()+name);
        if (dbFile.exists()) {
            Util.deleteFile(dbFile, 3000);
        }
    }

}
