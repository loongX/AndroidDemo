package com.pxl.base.app;

import android.app.Application;

import com.pxl.base.ILog;

/**
 * Created by Administrator on 2017/8/10.
 */

public abstract class BaseApp extends Application {

    public static final String TAG = "BaseApp";

    private static BaseApp gApp = null;
    @Override
    public void onCreate() {
        super.onCreate();
        ILog.i(TAG,"\n\n-----------App start --------------");
        ILog.i(TAG,"onCreate()");
        gApp = this;
        Thread.UncaughtExceptionHandler crashHandler = getUncaughtExceptionHandler();
        if(crashHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        }

    }

    /**
     * 获得全局监听异常处理。
     * @return
     */
    protected Thread.UncaughtExceptionHandler getUncaughtExceptionHandler(){
        return new CrashHandler();
    }

}
