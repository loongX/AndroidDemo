package com.pxl.common.util;

/**
 * Created by Administrator on 2015/2/3.
 */
public class NumberUtils {


    public static int parseInt(String text, int defaultValue){
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long parseLong(String text, long defaultValue){
        try {
            return Long.parseLong(text);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static double parseDouble(String text, double defaultValue){
        try {
            return Double.parseDouble(text);
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
