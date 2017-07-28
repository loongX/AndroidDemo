package com.pxl.base.app;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;


import com.pxl.common.ui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by Rao on 2015/1/10.
 */
public abstract class BaseActivity extends FragmentActivity {

    private static final boolean ENABLE_VIEW_SERVER = false;

    protected String TAG = getClass().getSimpleName();

    private static Class<? extends BaseActivity> launcherClass;

    private static boolean hasLaunchActivity = false;

    private static final List<Class<? extends BaseActivity>> loginClasses;

    private static ActivityListener activityListener = ActivityListener.NULL;

    public static boolean isDestroyed(Activity activity) {
        return activity == null
                || (activity instanceof BaseActivity && ((BaseActivity) activity).isDestroyed_())
                || activity.isFinishing();
    }

    static {
        loginClasses = new ArrayList<Class<? extends BaseActivity>>();
    }

    protected LayoutInflater mInflater;
//    private BaseActivityView mBaseView;


    private volatile boolean destroyed;
    protected HashMap<String,Object> mParams = new HashMap<>();


    public HashMap<String,Object> getActivityParams(){
        return mParams;
    }

    public static void setActivityListener(ActivityListener listener){
        activityListener = listener;
        if(activityListener == null){
            activityListener = ActivityListener.NULL;
        }
    }

   /* public HashMap<String,Object> getParams(){
        return mParams;
    }*/

    public void startActivityAndFinish(Intent intent) {
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG,"onSaveInstanceState()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG,"onRestoreInstanceState()");

    }

    public static void setLauncherClass(Class<? extends BaseActivity> o) {
        launcherClass = o;
    }

    public static void setHasLauncher(boolean hasLaunch) {
        hasLaunchActivity = hasLaunch;
    }

    public static boolean hasLauncher(){
        return hasLaunchActivity;
    }

    public static void addLoginClass(Class<? extends BaseActivity> o) {
        if (o != null && !loginClasses.contains(o)) {
            loginClasses.add(o);
        }
    }

//    public BaseActivityView getBaseView(){
//        if(mBaseView == null) {
//            mBaseView = new BaseActivityView(this,needFitSystemView());
//        }
//
//        refreshFitSystemView();
//
//        return mBaseView;
//    }

    /**
     * 推荐使用:getBaseView().getTitleView().setTitle(title);
     * @param title
     */
    @Override
    @Deprecated
    public void setTitle(CharSequence title) {
//       getBaseView().getTitleView().setTitle(title);
       super.setTitle(title);
    }



    /**
     * 刷新系统界面的兼容性问题。
     * xml需要包含以下ID
     <View android:id="@+id/layout_status_bar_id_for_system" android:layout_width="match_parent" android:layout_height="@dimen/status_bar_height"/>
     */
    public void refreshFitSystemView() {
        //自适应手机的透明状态栏问题。
        if(isDestroyed_()){
            return;
        }
        BaseActivity act = this;
//        View status_bar_id = act.findViewById(R.id.layout_status_bar_id_for_system);
//        if (status_bar_id != null) {
//            if (act.needFitSystemView()) {
//                status_bar_id.setVisibility(View.VISIBLE);
//            } else {
//                status_bar_id.setVisibility(View.GONE);
//            }
//        }


    }


    /**
     *
     * @param savedInstanceState
     * @return  是否进行启动该Activity，如果为false，将finish该activity不启动
     */
    protected boolean onCreateBefore(Bundle savedInstanceState){
        Log.d(TAG,"onCreateBefore()");

        return true;
    }

    protected void onCreated(Bundle savedInstanceState) {
        Log.d(TAG,"onCreateddddd()");
    }

    private boolean hasCallCreateMethod = false;


    /***
      *
      *  不再提供扩展。使用onCreated()或者onCreateBefore()替代；
      * @param savedInstanceState
    */
    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        if(hasCallCreateMethod){
            //防止被调用两次
            return;
        }
        Log.i(TAG,"onCreate()");
        hasCallCreateMethod = true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        if(needFitSystemView()){
            // 透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
           // window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        boolean continueProcess = onCreateBefore(savedInstanceState);
        super.onCreate(savedInstanceState);

        if(!continueProcess){
            return;
        }

        if ( !hasLaunchActivity  &&   launcherClass != null ) {
            //启动启动画面
            Log.i(TAG,"Will start LuanchActivity first, the current activity : +" + getClass());
            Intent intent = new Intent(this, launcherClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
           // ILog.i(TAG, "isFinishing: " + isFinishing());
            return;
        }

//        super.setContentView(getBaseView());
        mInflater = LayoutInflater.from(this);
        if (enableViewServer()) {
//            ViewServer.get(this).addWindow(this);
        }
        Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {
                if(isDestroyed_()){
                    return;
                }
//                BusyStatusMonitor monitor =  getBusyStatusMonitor();
//                if(monitor == null){
//                    return;
//                }
//                startLoadData(EventType.Init, this, monitor);
//                monitor.refreshBusyStatus();
//                if(!monitor.isBusy()){
//                    onLoadDataStatus(false);
//                }
            }
        });

        this.onCreated(savedInstanceState);

        activityListener.onCreate(this,mParams,savedInstanceState);
    }




    @Override
    public void setContentView(int layoutResID) {
//       getBaseView().setContentView(layoutResID);
//        ViewUtils.inject(this, getBaseView());

    }

    @Override
    public void setContentView(View view) {
//        getBaseView().setContentView(view);
//        ViewUtils.inject(this, getBaseView());

    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
//        getBaseView().setContentView(view, new RelativeLayout.LayoutParams(params));
//        ViewUtils.inject(this, getBaseView());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG,"onNewIntent()");
        super.onNewIntent(intent);
        activityListener.onNewIntent(this,mParams,intent);

    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart()");
        super.onStart();
        activityListener.onStart(this,mParams);

    }


    @Override
    protected void onResume() {
        Log.d(TAG,"onResume()");
        super.onResume();
//        if (enableViewServer()) {
//            ViewServer.get(this).setFocusedWindow(this);
//        }
//        IReport.onResume(this);
//        activityListener.onResume(this,mParams);

    }

    @Override
    protected void onPause() {
        Log.d(TAG,"onPause()");
//        IReport.onPause(this);
        super.onPause();
        activityListener.onPause(this,mParams);

    }


    @Override
    protected void onStop() {
        Log.d(TAG,"onStop()");
        super.onStop();
        activityListener.onStop(this,mParams);

    }


//    private Vector<Abandonable> abandonableList = new Vector<Abandonable>();
//    private Vector<Subscriber> unsubscribeList = new Vector<Subscriber>();

//    public void abandonWhileDestroy(Abandonable abandonable){
//        abandonableList.add(abandonable);
//    }
//
//    public void unsubscribeWhileDestroy(Subscriber subscriber){
//        unsubscribeList.add(subscriber);
//    }

    @Override
    protected void onDestroy() {
//        ILog.i(TAG,"onDestroy()");
//        destroyed = true;
//        hasCallCreateMethod = false;
//        if (enableViewServer()) {
//            ViewServer.get(this).removeWindow(this);
//        }
//        if(mBusyStatusMonitor != null) {
//            mBusyStatusMonitor.abandon();
//            mBusyStatusMonitor = null;
//        }
//
//        for(Abandonable abandonable : abandonableList){
//            abandonable.abandon();
//        }
//        for(Subscriber subscriber : unsubscribeList){
//            BaseApp.get().getPublisher().unsubscribe(subscriber);
//        }
//        abandonableList.clear();
//        unsubscribeList.clear();
//
//        if(mRefresh != null) {
//            mRefresh.unbindRefreshView();
//            mRefresh = null;
//        }
//
//        activityListener.onDestroy(this,mParams);
//        mParams.clear();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG,"onBackPressed()");
        try {
            super.onBackPressed();
        } catch (Throwable t) {
//            Log.printStackTrace(t);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult " + getClass().getSimpleName() + " :" + requestCode + "," + resultCode + "," + data);
    }


    public boolean isDestroyed_() {
        return destroyed || isFinishing();
    }

    private boolean enableViewServer() {
        return ENABLE_VIEW_SERVER;
    }

    protected boolean needFitSystemView() {
        //4.4版本才支持
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }




//    private BusyStatusMonitor mBusyStatusMonitor;
//    public BusyStatusMonitor getBusyStatusMonitor(){
//        if(mBusyStatusMonitor == null) {
//            if(isDestroyed_()){
//                return null;
//            }
//            mBusyStatusMonitor = new BusyStatusMonitor();
//            mBusyStatusMonitor.setName(getClass().getSimpleName());
//            mBusyStatusMonitor.setBusyStatusListener(new BusyStatusMonitor.BusyStatusListener() {
//                @Override
//                public void onBusyStausChanged(boolean isBusy) {
//                    if (!isBusy && mRefresh != null) {
//                        mRefresh.completeAllRefresh();
//                    }
//                    BaseActivity.this.onLoadDataStatus(isBusy);
//                }
//            });
//        }
//        return  mBusyStatusMonitor;
//    }

//    private Refresher mRefresh = null;

    @Override
    public boolean isDestroyed() {
        return super.isDestroyed();
    }

    public static boolean isDestoryed(Activity activity){
        if(activity instanceof BaseActivity){
            return ((BaseActivity)activity).isDestroyed_();
        }else{
            try{
                return activity.isFinishing() || activity.isDestroyed();
            }catch (Exception ex){

            }
        }
        return false;
    }

//    public Refresher getRefresher(){
//        if(mRefresh == null) {
//            if(isDestroyed_()){
//                return null;
//            }
//            mRefresh = new ActivityRefresher(this);
//        }
//        return mRefresh;
//    }


    public void startLoadByRefresh(){
//        this.startLoadData(EventType.Refresh, this, getBusyStatusMonitor());
    }

//    /**
//     *
//     * @param type
//     * @param eventSource
//     * @param monitor  用于监听耗时工作。监听的工作很重要，因为要知道什么时候工作完成，以便重置下拉刷新工作。
//     */
//    public abstract void startLoadData(EventType type, Object eventSource, BusyStatusMonitor monitor);

    protected  abstract void onLoadDataStatus(boolean loading);


//    private static class ActivityRefresher extends Refresher{
//        private BaseActivity activity;
//        public ActivityRefresher(BaseActivity activity){
//            this.activity = activity;
//        }
//
//        @Override
//        protected void doRefresh(View eventSource) {
//            BusyStatusMonitor monitor = activity.getBusyStatusMonitor();
//            activity.startLoadData(EventType.PullRefresh, eventSource,monitor);
//            monitor.refreshBusyStatus();
//            if(!monitor.isBusy()){
//                completeAllRefresh();
//            }
//
//        }
//
//        @Override
//        protected void doLoadMore(View eventSource) {
//            BusyStatusMonitor monitor = activity.getBusyStatusMonitor();
//            activity.startLoadData(EventType.LoadMore, eventSource,monitor);
//            monitor.refreshBusyStatus();
//            if(!monitor.isBusy()){
//                completeAllRefresh();
//            }
//        }
//    }

}
