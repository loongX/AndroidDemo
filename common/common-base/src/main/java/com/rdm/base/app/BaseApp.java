package com.rdm.base.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.rdm.base.BaseSession;
import com.rdm.base.Publisher;
import com.rdm.base.SDKContext;
import com.rdm.base.db.DefaultUpdateListener;
import com.rdm.base.db.EntityManager;
import com.rdm.base.db.EntityManagerFactory;
import com.rdm.base.event.NetworkStatusChangedEvent;
import com.rdm.common.Debug;
import com.rdm.common.ILog;

/**
 * Created by lokierao on 2015/1/9.
 */
public abstract class BaseApp extends Application{

    public static final String TAG = "BaseApp";

    public static enum NetworkStatus {
        NotReachable,
        ViaWWAN,
        ViaWiFi,
        Unkonw,
    }

    private static BaseApp gApp = null;


    private NetworkStatus mNetworkStatus = NetworkStatus.Unkonw;
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
        ILog.i(TAG,"\n\n-----------App start --------------");
        ILog.i(TAG,"onCreate()");
        gApp = this;
        Thread.UncaughtExceptionHandler crashHandler = getUncaughtExceptionHandler();
        if(crashHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        }
        registerNetworkSensor(this);
    }

    public void onTerminate() {
        ILog.i(TAG,"onTerminate()");
        gApp = null;
        unregisterNetworkSensor(this);
        super.onTerminate();
    }


    /**
     * 返回网络状态。
     * @return
     */
    public NetworkStatus getNetworkStatus(){
        return mNetworkStatus;
    }

    /**
     * 获得全局监听异常处理。
     * @return
     */
    protected Thread.UncaughtExceptionHandler getUncaughtExceptionHandler(){
        return new CrashHandler();
    }

    public long getRealTime(){

        //TODO 返回网络时间。
        return System.currentTimeMillis();
    }

    private NetworkBroadcastReceiver mReceiver = null;


    private void registerNetworkSensor(Context context)
    {
        ILog.v(TAG, "registerNetworkSensor");
        synchronized (this ){
            if (mReceiver != null)
                return;
            mReceiver = new NetworkBroadcastReceiver();

        }

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable())
        {
            ILog.i(TAG, "network not reachable");
            mNetworkStatus = NetworkStatus.NotReachable;
        }
        else if (info.getType() == ConnectivityManager.TYPE_MOBILE)
        {
            ILog.i(TAG, "network reachable via wwan");
            mNetworkStatus = NetworkStatus.ViaWWAN;

        }
        else if (info.getType() == ConnectivityManager.TYPE_WIFI)
        {
            ILog.i(TAG, "network reachable via wifi");
            mNetworkStatus = NetworkStatus.ViaWiFi;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiver, intentFilter);
    }

    private void unregisterNetworkSensor(Context context)
    {
        if (mReceiver == null)
            return;
        NetworkBroadcastReceiver receiver = mReceiver;
        synchronized (this){
            mReceiver = null;
            context.unregisterReceiver(receiver);
        }
    }


    private class NetworkBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive (Context context, Intent intent)
        {

            ILog.v("NetworkBroadcastReceiver", "onReceive");
            if (intent == null)
                return;

            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = manager.getActiveNetworkInfo();
                NetworkStatus ns = NetworkStatus.NotReachable;
                if (info == null || !info.isAvailable())
                {
                    ILog.i("NetworkBroadcastReceiver", "network not reachable");
                    ns = NetworkStatus.NotReachable;
                }
                else if (info.getType() == ConnectivityManager.TYPE_MOBILE)
                {
                    ILog.i("NetworkBroadcastReceiver", "network reachable via wwan");
                    ns = NetworkStatus.ViaWWAN;

                }
                else if (info.getType() == ConnectivityManager.TYPE_WIFI)
                {
                    ILog.i("NetworkBroadcastReceiver", "network reachable via wifi");
                    ns = NetworkStatus.ViaWiFi;
                }

               if (!mNetworkStatus.equals(ns)) {
                   mNetworkStatus = ns;
                   NetworkStatusChangedEvent event = new NetworkStatusChangedEvent(ns);
                   getPublisher().publish(event);


                }
            }
        }
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

    /**
     * 返回DB的管理中心。
     * @param session
     * @return
     */
    public EntityManagerFactory getEntityManagerFactory(BaseSession session) {
        SDKContext.DBInit init = getSDkContext().getDBInit();
        EntityManager.UpdateListener listener = init.getUpdateListener();
        if (listener == null) {
            listener = new DefaultUpdateListener();
        }
        return EntityManagerFactory.getInstance(this, init.getDBVersion(), session.getUid(), null, listener);
    }

    public abstract SDKContext getSDkContext();

    private BaseSession mGlobalSession = null;
    public BaseSession getGlobalSession(){
        if(mGlobalSession == null) {
            synchronized (this){
                if(mGlobalSession == null) {
                    mGlobalSession = createGlobalSession();
                }
            }

        }
        return mGlobalSession;
    }

    protected BaseSession createGlobalSession(){
        return new BaseSession("global-session");
    }


    /**
     * 返回当前渠道包
     * @return
     */
    public String getChannel(){
        return "default";
    }

    public boolean isDebugMode(){
        return Debug.isDebug();
    }

}
