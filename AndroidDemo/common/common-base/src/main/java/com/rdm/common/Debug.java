package com.rdm.common;

/**
 * Created by lokierao on 2015/1/9.
 */
public class Debug {

    private static boolean gIsDebug = false;

    public static boolean isDebug() {
        return gIsDebug;
    }

    public static void setDebug(boolean isDebug){
         gIsDebug = isDebug;
    }

}