package com.yzh.androidquickdevlib.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yzh on 2017/3/7.
 */

public class FileDigestUtil {
    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String md5 = bigInt.toString(16);
        // 不足32位的补0
        while (md5.length() < 32) {
            md5 = "0" + md5;
        }
        return md5;
    }

    /**
     * 获取字符串的md5
     *
     * @param str
     * @return
     */
    public static String getStringMD5(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] strBytes = digest.digest(str.getBytes());
            StringBuilder sb = new StringBuilder(40);
            for (byte x : strBytes) {
                if ((x & 0xff) >> 4 == 0) {
                    sb.append("0")
                            .append(Integer.toHexString(x & 0xff));
                }
                else {
                    sb.append(Integer.toHexString(x & 0xff));
                }
            }
            return sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取byte[]的md5
     *
     * @param bytes
     * @return
     */
    public static String getByteArrayMD5(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return toHex(digest.digest(bytes));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String toHex(byte[] buffer) {
        StringBuffer sb = new StringBuffer(buffer.length * 2);
        for (int i = 0; i < buffer.length; i++) {
            sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
            sb.append(Character.forDigit(buffer[i] & 15, 16));
        }
        return sb.toString();
    }
}
