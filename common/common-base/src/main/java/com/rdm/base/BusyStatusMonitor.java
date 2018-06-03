package com.rdm.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.rdm.base.thread.ThreadPool;
import com.rdm.common.ILog;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * Created by Rao on 2015/1/27.
 */
public class BusyStatusMonitor implements BusyFeedback{


    public interface BusyStatusListener {
        void onBusyStausChanged(boolean isBusy);
    }

    public static final String TAG = "BusyStatusMonitor_";

    private static final int MSG_REFRESH_STATUS = 1;
    //private static final int MSG_SET_BUSY_TRUE = 2;
    //private static final int MSG_SET_BUSY_FALSE = 3;

    private Set<BusyFeedback> mFeedbackSet = new HashSet<BusyFeedback>();
    private BusyStatusListener mListener =  null;

    private long mMonitorBusyMinInterval;   //忙碌的时候侦听变换频率
    private long mMonitorBusyMaxInterval;   //不忙碌的时候侦听变换频率

    private boolean mIsBusy = false;
    private boolean mIsMonitorning = false;

    private Handler mMainHandler = new MyHandle();

    private String mName = "";

    public BusyStatusMonitor(){
        this(30,250);
    }



    public BusyStatusMonitor(long minInterval,int maxInterval){

        if(minInterval < 10  ||  maxInterval < minInterval){
            throw new IllegalArgumentException("");
        }

        mMonitorBusyMinInterval = minInterval;
        mMonitorBusyMaxInterval = maxInterval;
    }
    public void setName(String name) {
        mName = name;
    }

    public boolean isMonitorning(){
        return mIsMonitorning;
    }


    public void setBusyStatusListener(BusyStatusListener ls){
        if(isAbandon()) {
            return;
        }
        mListener = ls;
    }

    public void summitThread(Runnable runnable){
        BusyFeedback.RunnableWrapper taks = new BusyFeedback.RunnableWrapper(runnable,true);
        addBusyFeedback(taks); //监听后台任务是否处理完成。
        ThreadPool.getInstance().submit(taks);
    }

    public void addBusyFeedback(BusyFeedback feeback){
        if(mFeedbackSet == null || feeback == null || feeback.isAbandon()) {
            return;
        }
        mFeedbackSet.add(feeback);
        refreshBusyStatus();
    }

    public void removeFeedback(BusyFeedback feeback){
        if(mFeedbackSet == null || feeback == null) {
            return;
        }
        mFeedbackSet.remove(feeback);
        refreshBusyStatus();
    }


    /**
     * 刷新更新状态。
     * 由于是定时判断是否忙碌的，所以存在要强制刷新当前状态的场景。
     */
    public void refreshBusyStatus(){

        if(isAbandon()){
            return;
        }

        boolean checkBusy = false;
        boolean hasFeedbacks = false;
        Set<BusyFeedback> feedbacks = mFeedbackSet;
        if(feedbacks != null) {
            BusyFeedback[] list =  feedbacks.toArray(new BusyFeedback[0]);
            //remove abandon
            for(BusyFeedback feedback : list){
                if(feedback.isAbandon()){
                    feedbacks.remove(feedback);
                }
            }
            for(BusyFeedback feedback : list){
                if(feedback.isAbandon()){
                    //remove abandon
                    feedbacks.remove(feedback);
                }else if(feedback.isBusy()){
                    checkBusy = true;
                    break;
                }
            }
            hasFeedbacks = !feedbacks.isEmpty();
        }

        if(ILog.isEnable(ILog.Level.VERBOSE)){
            Log.v(TAG + mName, "do monitor : isbusy = " + checkBusy +" , hasFeddbacks = " + hasFeedbacks);
        }

        boolean currentBusy  = isBusy();

        if(checkBusy != currentBusy){
            // busy changed.
           // mMainHandler.removeMessages(MSG_SET_BUSY_FALSE);
            if(checkBusy) {
                //转busy状态时，不延迟。
                setBusy(true);
            }else{
                //转空闲状态时，延迟一下。
              //  long delay = mMonitorBusyMinInterval;
                //mMainHandler.sendEmptyMessageDelayed(MSG_SET_BUSY_FALSE, delay);
                setBusy(false);
            }
        }

        if(hasFeedbacks) {
            long delay = checkBusy ? mMonitorBusyMinInterval : mMonitorBusyMaxInterval;
            mMainHandler.sendEmptyMessageDelayed(MSG_REFRESH_STATUS,delay);
            mIsMonitorning = true;
        }else{
            mIsMonitorning = false;
            Log.v(TAG + mName, "stop monitor ");
        }
    }

    public synchronized boolean isBusy() {
        return mIsBusy;
    }

    private synchronized void setBusy(boolean busy){
        if(mIsBusy != busy) {
            mIsBusy = busy;
            BusyStatusListener listener = mListener;
            if(listener != null) {
                listener.onBusyStausChanged(mIsBusy);
            }
        }

    }

    @Override
    public float getProgress() {
        return 0;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }



    private class MyHandle extends Handler {

       // private long mLastEndBusyTime = System.currentTimeMillis();
       // private boolean mLastBusyStatus = false;

        public MyHandle(){
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case MSG_REFRESH_STATUS:
                    refreshBusyStatus();
                    break;
               /* case MSG_SET_BUSY_TRUE:
                    setBusy(true);
                    break;*/
              /*  case MSG_SET_BUSY_FALSE:
                    setBusy(false);
                    break;*/
                default:
            }

        }


       /* private void doMonitor() {
            boolean checkBusy = false;
            boolean hasFeedbacks = false;
            WeakSet<BusyFeedback> feedbacks = mFeedbackSet;
            if(feedbacks != null) {
                List<BusyFeedback> list =  feedbacks.getList();
                //remove abandon
                for(BusyFeedback feedback : list){
                    if(feedback.isAbandon()){
                        feedbacks.remove(feedback);
                    }
                }
                for(BusyFeedback feedback : list){
                    if(feedback.isAbandon()){
                        //remove abandon
                        feedbacks.remove(feedback);
                    }else if(feedback.isBusy()){
                        checkBusy = true;
                        break;
                    }
                }
                hasFeedbacks = !feedbacks.isEmpty();
            }

            if(ILog.isEnable(ILog.Level.VERBOSE)){
                Log.v(TAG + mName, "do monitor : isbusy = " + checkBusy +" , hasFeddbacks = " + hasFeedbacks);
            }


            if(checkBusy != mLastBusyStatus){
                // busy changed.
                mLastBusyStatus = checkBusy;
                removeMessages(MSG_SET_BUSY_FALSE);
                removeMessages(MSG_SET_BUSY_TRUE);
                if(checkBusy) {
                    //有没有到达上一次设置空闲状态最少时间。
                    //long elscape = System.currentTimeMillis() - mLastEndBusyTime;
                    long delay = mBusyChangedDelayInterval;
                    sendEmptyMessageDelayed(MSG_SET_BUSY_TRUE,delay);

                }else{
                    //有没有到达上一次设置忙碌状态最少时间。
                    long elscape = System.currentTimeMillis() - mLastStartBusyTime;
                    long delay = Math.max(0, mBusyChangedDelayInterval - elscape);
                    sendEmptyMessageDelayed(MSG_SET_BUSY_FALSE,delay);
                    //mLastEndBusyTime = System.currentTimeMillis();
                }
            }

            if(hasFeedbacks) {
                monitor(checkBusy ? mMonitorBusyInterval : mBusyChangedDelayInterval);
            }else{
                mIsMonitorning = false;
                Log.v(TAG + mName, "stop monitor ");
            }

        }*/
    };

    @Override
    public void abandon() {
        refreshBusyStatus();
        if(mFeedbackSet != null) {
            mFeedbackSet.clear();
        }

        mListener = null;
        mFeedbackSet = null;
    }

    @Override
    public boolean isAbandon() {
        return mFeedbackSet == null;
    }
}
