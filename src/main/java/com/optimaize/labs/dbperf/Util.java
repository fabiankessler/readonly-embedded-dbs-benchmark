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
