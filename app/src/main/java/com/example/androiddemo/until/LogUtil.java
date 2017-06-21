package com.example.androiddemo.until;

import android.util.Log;

/**
 * Created by loongago on 2016/12/25 0025.
 */

public class LogUtil {
    public static void v(String tag, String msg) {
        if (!Constant.isVerbose)
            return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.v(tag, getTAG(targetStackTraceElement));
        Log.v(tag, msg);


    }


    public static void v(String tag, String msg, Throwable tr) {
        if (!Constant.isVerbose)
            return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.v(tag, getTAG(targetStackTraceElement));
        Log.v(tag, msg, tr);


    }


    public static void d(String tag, String msg) {
        if (!Constant.isDebug)
            return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.d(tag, getTAG(targetStackTraceElement));
        Log.d(tag, msg);


    }


    public static void d(String tag, String msg, Throwable tr) {
        if (!Constant.isDebug)
            return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.d(tag, getTAG(targetStackTraceElement));
        Log.d(tag, msg, tr);


    }


    public static void i(String tag, String msg) {
        if (!Constant.isInformation)
            return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.i(tag, getTAG(targetStackTraceElement));
        Log.i(tag, msg);


    }


    public static void i(String tag, String msg, Throwable tr) {
        if (!Constant.isInformation)
            return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.i(tag, getTAG(targetStackTraceElement));
        Log.i(tag, msg, tr);


    }


    public static void w(String tag, String msg) {
        if (!Constant.isWarning)
            return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.w(tag, getTAG(targetStackTraceElement));
        Log.w(tag, msg);


    }


    public static void w(String tag, String msg, Throwable tr) {
        if (!Constant.isWarning)
            return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.w(tag, getTAG(targetStackTraceElement));
        Log.w(tag, msg, tr);


    }


    public static void e(String tag, String msg) {
        if (!Constant.isError)
            return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.e(tag, getTAG(targetStackTraceElement));
        Log.e(tag, msg);


    }


    public static void e(String tag, String msg, Throwable tr) {
        if (!Constant.isError)
            return;


        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.e(tag, getTAG(targetStackTraceElement));
        Log.e(tag, msg, tr);


    }


    private static String getTAG(StackTraceElement targetStackTraceElement) {
        return targetStackTraceElement.getClassName() + "." + targetStackTraceElement.getMethodName() + "("
                + targetStackTraceElement.getFileName() + ":" + targetStackTraceElement.getLineNumber() + ")";
    }


    private static StackTraceElement getTargetStackTraceElement() {
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(LogUtil.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }
}
