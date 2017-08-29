package com.pxl.base.app;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.pxl.base.ILog;
import com.pxl.base.Publisher;

/**
 * Created by Administrator on 2017/8/10.
 */

public abstract class BaseApp extends Application {

    public static final String TAG = "BaseApp";

    private static BaseApp gApp = null;

    private final Handler mHandler;

    public BaseApp(){
        mHandler = new Handler(Looper.getMainLooper());
    }

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

    private Object lock = new Object();
    private Publisher mNotification;

    /**
     * 返回事件派发中心。
     * @return
     */
    public synchronized Publisher getPublisher(){
        if(mNotification == null){
            synchronized (lock) {
                if(mNotification == null) {
                    mNotification = new DefaultPublisher(Looper.getMainLooper());
                }
            }
        }
        return mNotification;
    }

    /**
     * 返回全局
     * @return
     */
    public static BaseApp get(){
        return gApp;
    }
}
