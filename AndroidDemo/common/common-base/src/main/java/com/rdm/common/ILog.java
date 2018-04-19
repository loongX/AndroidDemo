package com.rdm.common;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

public class ILog {



    public static enum Level {
        VERBOSE(0), DEBUG(1), INFO(2), WARN(3), ERROR(4), ASSERT(5);
        int levelValue;

        Level(int value) {
            this.levelValue = value;
        }

        public int getValue() {
            return levelValue;
        }
    }

    public static void setDelegate(Delegate delegate) {
        gDelegate = delegate;
    }

    public static void setEnableLog(boolean enable) {
        gEnableLog = enable;
    }


    public static void printStackTrace(Throwable t) {
        t.printStackTrace();
    }

    /**
     * 设置日志的输出等级
     *
     * @param level 大于等于该日志则会输出。
     */
    public static void setLevel(Level level) {
        if (level == null) {
            return;
        }
        gLevel = level;
    }

    /**
     * @param tag
     * @param message
     */
    public static void v(String tag, String message) {
        if (isEnable(Level.VERBOSE)) {
            gDelegate.verbose(tag, message);
        }else{
            Log.v(tag,message);
        }
    }


    public static void v(Class tag, String message) {
        if (isEnable(Level.VERBOSE)) {
            gDelegate.verbose(getTagByClass(tag), message);
        }else{
            Log.v(getTagByClass(tag),message);
        }
    }


    public static void v(String tag, String message, Object... args) {
        if (isEnable(Level.VERBOSE)) {
            gDelegate.verbose(tag, String.format(message, args));
        }
    }
    public static void v(Class tag, String message, Object... args) {
        if (isEnable(Level.VERBOSE)) {
            gDelegate.verbose(getTagByClass(tag), String.format(message, args));
        }
    }

    public static void v(String tag, String message, Throwable throwable) {
        if (isEnable(Level.VERBOSE)) {
            String text = getStackTraceString(throwable);
            gDelegate.verbose(tag, text);
        }
    }

    public static void v(Class tag, String message, Throwable throwable) {
        if (isEnable(Level.VERBOSE)) {
            String text = getStackTraceString(throwable);
            gDelegate.verbose(getTagByClass(tag), text);
        }
    }

    public static void a(String tag, String message) {
        if (isEnable(Level.ASSERT)) {
            gDelegate.assert_(tag, message);
        }
    }

    public static void a(Class tag, String message) {
        if (isEnable(Level.ASSERT)) {
            gDelegate.assert_(getTagByClass(tag), message);
        }
    }

    public static void a(String tag, String message, Object... args) {
        if (isEnable(Level.ASSERT)) {
            gDelegate.assert_(tag, String.format(message, args));
        }
    }

    public static void a(Class tag, String message, Object... args) {
        if (isEnable(Level.ASSERT)) {
            gDelegate.assert_(getTagByClass(tag), String.format(message, args));
        }
    }

    public static void a(String tag, String message, Throwable throwable) {
        if (isEnable(Level.ASSERT)) {
            String text = getStackTraceString(throwable);
            gDelegate.assert_(tag, text);
        }
    }

    public static void a(Class tag, String message, Throwable throwable) {
        if (isEnable(Level.ASSERT)) {
            String text = getStackTraceString(throwable);
            gDelegate.assert_(getTagByClass(tag), text);
        }
    }

    public static void d(String tag, String message, Throwable throwable) {
        if (isEnable(Level.DEBUG)) {
            String text = getStackTraceString(throwable);
            gDelegate.debug(tag, text);
        }
    }

    public static void d(Class tag, String message, Throwable throwable) {
        if (isEnable(Level.DEBUG)) {
            String text = getStackTraceString(throwable);
            gDelegate.debug(getTagByClass(tag), text);
        }else{
            Log.d(getTagByClass(tag),message);
        }
    }

    public static void d(String tag, String message) {
        if (isEnable(Level.DEBUG)) {
            gDelegate.debug(tag, message);
        }else{
            Log.d(tag,message);
        }
    }


    public static void d(Class tag, String message) {
        if (isEnable(Level.DEBUG)) {
            gDelegate.debug(getTagByClass(tag), message);
        }else{
            Log.d(getTagByClass(tag),message);
        }
    }

    public static void d(String tag, String message, Object... args) {
        if (isEnable(Level.DEBUG)) {
            gDelegate.debug(tag, String.format(message, args));
        }
    }

    public static void d(Class tag, String message, Object... args) {
        if (isEnable(Level.DEBUG)) {
            gDelegate.debug(getTagByClass(tag), String.format(message, args));
        }
    }


    public static void e(String tag, String message) {
        if (isEnable(Level.ERROR)) {
            gDelegate.error(tag, message);
        }
    }


    public static void e(Class tag, String message) {
        if (isEnable(Level.ERROR)) {
            gDelegate.error(getTagByClass(tag), message);
        }
    }

    public static void e(String tag, String message, Object... args) {
        if (isEnable(Level.ERROR)) {
            gDelegate.error(tag, String.format(message, args));
        }
    }


    public static void e(Class tag, String message, Object... args) {
        if (isEnable(Level.ERROR)) {
            gDelegate.error(getTagByClass(tag), String.format(message, args));
        }
    }

    public static void e(String tag, String message, Throwable throwable) {
        if (isEnable(Level.ERROR)) {
            String text = getStackTraceString(throwable);
            gDelegate.error(tag, text);
        }
    }


    public static void e(Class tag, String message, Throwable throwable) {
        if (isEnable(Level.ERROR)) {
            String text = getStackTraceString(throwable);
            gDelegate.error(getTagByClass(tag), text);
        }
    }

    public static void i(String tag, String message) {
        if (isEnable(Level.INFO)) {
            gDelegate.info(tag, message);
        }
    }


    public static void i(Class tag, String message) {
        if (isEnable(Level.INFO)) {
            gDelegate.info(getTagByClass(tag), message);
        }
    }

    public static void i(String tag, String message, Object... args) {
        if (isEnable(Level.INFO)) {
            gDelegate.info(tag, String.format(message, args));
        }
    }


    public static void i(Class tag, String message, Object... args) {
        if (isEnable(Level.INFO)) {
            gDelegate.info(getTagByClass(tag), String.format(message, args));
        }
    }

    public static void i(String tag, String message, Throwable throwable) {
        if (isEnable(Level.INFO)) {
            String text = getStackTraceString(throwable);
            gDelegate.info(tag, text);
        }
    }


    public static void i(Class tag, String message, Throwable throwable) {
        if (isEnable(Level.INFO)) {
            String text = getStackTraceString(throwable);
            gDelegate.info(getTagByClass(tag), text);
        }
    }

    public static void w(String tag, String message) {
        if (isEnable(Level.WARN)) {
            gDelegate.warn(tag, message);
        }
    }

    public static void w(Class tag, String message) {
        if (isEnable(Level.WARN)) {
            gDelegate.warn(getTagByClass(tag), message);
        }
    }

    public static void w(String tag, String message, Object... args) {
        if (isEnable(Level.WARN)) {
            gDelegate.warn(tag, String.format(message, args));
        }
    }

    public static void w(Class tag, String message, Object... args) {
        if (isEnable(Level.WARN)) {
            gDelegate.warn(getTagByClass(tag), String.format(message, args));
        }
    }

    public static void w(String tag, String message, Throwable throwable) {
        if (isEnable(Level.WARN)) {
            String text = getStackTraceString(throwable);
            gDelegate.warn(tag, text);
        }
    }

    public static void w(Class tag, String message, Throwable throwable) {
        if (isEnable(Level.WARN)) {
            String text = getStackTraceString(throwable);
            gDelegate.warn(getTagByClass(tag), text);
        }
    }

    public static boolean isEnable(Level level) {
        if (gDelegate != null && gEnableLog && level.getValue() >= gLevel.getValue()) {
            return true;
        }
        return false;
    }

    private static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    private static String getTagByClass(Class tag) {
        if (tag == null) {
            return "";
        }
        return  tag.getSimpleName();
    }

    private static Level gLevel = Level.VERBOSE;
    private static boolean gEnableLog = true;
    private static Delegate gDelegate = new ConsoleDelegate();

    public static interface Delegate {

        void verbose(String tag, String message);

        void debug(String tag, String messsage);

        void info(String tag, String messsage);

        void warn(String tag, String messsage);

        void error(String tag, String messsage);

        void assert_(String tag, String messsage);

    }

    /**
     * 控制台打印实现类。
     */
    public static class ConsoleDelegate implements Delegate {

        @Override
        public void verbose(String tag, String message) {
            Log.v(tag, message);
        }

        @Override
        public void debug(String tag, String message) {
            Log.d(tag, message);

        }

        @Override
        public void info(String tag, String messsage) {
            Log.i(tag, messsage);

        }

        @Override
        public void warn(String tag, String messsage) {
            Log.w(tag, messsage);

        }

        @Override
        public void error(String tag, String messsage) {
            Log.e(tag, messsage);

        }

        @Override
        public void assert_(String tag, String messsage) {
            Log.e(tag, messsage);
        }
    }

}
