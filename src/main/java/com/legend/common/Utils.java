package com.legend.common;

/**
 * @author Legend
 * @data by on 21-1-23.
 * @description
 */
public class Utils {

    public static String getHexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toHexString(b)).append(" ");
        }
        return sb.toString();
    }
}
