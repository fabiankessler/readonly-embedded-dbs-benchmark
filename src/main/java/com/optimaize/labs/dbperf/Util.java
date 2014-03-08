package com.optimaize.labs.dbperf;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ...
 *
 * @author Fabian Kessler
 */
public class Util {

    public static Connection makeSingleConnection(String connString) {
        try {
            return DriverManager.getConnection(connString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setSqliteConnProbsForImporting(Connection connection) {
        try {
            //see http://www.sqlite.org/pragma.html
            connection.prepareStatement("PRAGMA synchronous=OFF;").execute();
            connection.prepareStatement("PRAGMA journal_mode=OFF;").execute();
            connection.prepareStatement("PRAGMA temp_store=3;").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setSqliteConnProbsForReadonly(Connection connection) {
        try {
            //see http://www.sqlite.org/pragma.html
            connection.prepareStatement("PRAGMA synchronous=OFF;").execute();
            connection.prepareStatement("PRAGMA journal_mode=OFF;").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        try {
//            //java.sql.SQLException: Cannot set read-only flag after establishing a connection. Use SQLiteConfig#setReadOnly and SQLiteConfig.createConnection().
//            connection.setReadOnly(true);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

    public static String makeSqliteConnectionStringForImporting(String pathWithFilename) {
        return makeSqliteConnectionString(pathWithFilename);
    }
    public static String makeSqliteConnectionStringForReadonly(String pathWithFilename) {
        return makeSqliteConnectionString(pathWithFilename);
    }
    public static String makeSqliteConnectionString(String pathWithFilename) {
        return "jdbc:sqlite:" +pathWithFilename;
    }

    public static String makeH2ConnectionStringForImporting(String pathWithoutFileSuffix) {
        String connectionString = "jdbc:h2:file:" +pathWithoutFileSuffix;
        connectionString += ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0"; //see http://www.h2database.com/html/performance.html
        return connectionString;
    }
    public static String makeH2ConnectionStringForReadonly(String pathWithoutFileSuffix) {
        String connectionString = "jdbc:h2:file:" +pathWithoutFileSuffix;
        connectionString += ";IFEXISTS=TRUE";
        connectionString += ";ACCESS_MODE_DATA=r";
        return connectionString;
    }

    /**
     * @return 32-character string.
     */
    public static String md5(String str) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No M5 messagedigest!", e);
        }

        byte[] defaultBytes = str.getBytes();
        algorithm.reset();
        algorithm.update(defaultBytes);
        byte messageDigest[] = algorithm.digest();
        StringBuilder hexString = new StringBuilder();

        for (byte aMessageDigest : messageDigest) {
            String hex = Integer.toHexString(0xFF & aMessageDigest);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static ThreadPoolExecutor makeExecutor(int maximumPoolSize) {
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        return new ThreadPoolExecutor(
                1, maximumPoolSize,
                10, TimeUnit.SECONDS,
                workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy()
                //new ThreadPoolExecutor.AbortPolicy()
        );
    }

}
