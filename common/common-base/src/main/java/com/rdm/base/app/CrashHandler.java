package com.rdm.base.app;


import com.rdm.common.ILog;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author user
 *
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String tag = thread != null ? thread.toString() : "";
        ILog.e(tag,ex.getMessage(),ex);
        System.exit(0);
    }
}
