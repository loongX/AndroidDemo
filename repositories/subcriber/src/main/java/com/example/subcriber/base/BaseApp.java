package com.example.subcriber.base;

import android.app.Application;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


/**
 * Created by lokierao on 2015/1/9.
 */
public abstract class BaseApp extends Application {

    public static final String TAG = "BaseApp";

    private static BaseApp gApp = null;

    private Object lock = new Object();
    private final Handler mHandler;

    public BaseApp(){
        mHandler = new Handler(Looper.getMainLooper());
    }
    /**
     * 返回全局
     * @return
     */
    public static BaseApp get(){
        return gApp;
    }

    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"\n\n-----------App start --------------");
        Log.i(TAG,"onCreate()");
        gApp = this;

    }



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

    public Handler getHandler(){
        return mHandler;
    }



}
