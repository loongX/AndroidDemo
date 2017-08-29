package com.rdm.base.loader;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.rdm.base.Abandonable;
import com.rdm.base.BusyFeedback;
import com.rdm.base.ThreadManager;
import com.rdm.common.ILog;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 定义了所有的数据加载的接口：
 * 1、自动缓存内容;
 * 2、支持从本地、从远程加载；
 * 3、加载方式：普通、加载更多、分页加载；
 *
 *
 * Created by lokierao on 2015/5/19.
 */
public abstract class DataLoader<T> implements Abandonable, BusyFeedback {

    public static final long DEFAULT_TIMEOUT = 20000;

    public enum LoadType {

        /**
         * 只从磁盘里面获取
         */
        ONLY_STORE,
        /**
         * 只从网络里面获取
         */
        ONLY_NETWORK,

        /**
         * 会先从网络里面获取，如果没有再从缓存里面获取，如果还没有，就从磁盘里面获取；
         */
       // NETWORK_FIRST,

        /**
         *
         * 会先从缓存里面获取，如果没有再从磁盘里面获取，如果还没有，就从网络里面获取；只会回调一次。
         */
        STORE_FIRST,

        /**
         *
         * 先从本地（磁盘或者缓存）获取， 再从网络里面获取。因此，最终会有两次结果的回调。
         */
        LOCAL_AND_NETWORK,

    }

    public enum ConflictType {

        /**
         * 如果已经在加载当中，则跳过本次load请求。
         */
        SKIP,

        /**
         *如果已经在加载中，等待上一个处理完成，再发送请求；
         */
        WAIT,

        /**
         *忽略是否已经加载中，总是马上发送加载请求；
         */
        ALWAYS,

    }


    public interface DataListener<RESULT> {

        /**
         * 会有多次调用。
         * @param data 如果为空，则result = null;
         */
        void onResult(DataLoader loader, Data<RESULT> data);

        void onError(DataLoader loader, String message, Exception error);

        void onCancel(DataLoader loader);

        void onTimeout(DataLoader loader);
    }

    public static abstract class SimpleDataListener<RESULT> implements DataListener<RESULT> {

        public abstract void onResult(DataLoader loader, boolean succeed, Data<RESULT> data);


            @Override
        public void onResult(DataLoader loader, Data<RESULT> data) {
                onResult(loader,true,data);
        }

        @Override
        public void onError(DataLoader loader, String message, Exception error) {
            onResult(loader,false,null);
        }

        @Override
        public void onCancel(DataLoader loader) {
            onResult(loader,false,null);
        }

        @Override
        public void onTimeout(DataLoader loader) {
            onResult(loader,false,null);
        }
    }


    public interface ResultNotifier<T>{

        /**
         * 通知临时结果，常用于一边加载一边显示。可多次调用。
         * @param data
         */
        void notifyTemporaryReulst(T data);

        /**
         * 通知结果。只能调用一次。
         * @param data
         */
        void notifyResult(T data);

        /**
         * 只能调用一次。
         * @param message
         * @param except
         */
        void notifyError(String message,Exception except);

        /**
         *  只能调用一次。
         */
        void notifyTimeout();

        /**
         * 通知进度值。
         * @param progress
         */
        void notifyProgress(float progress);

        /**
         * 是否已经取消。
         * @return
         */
        boolean isCancel();

    }

    private long mDefaultTimeout = DEFAULT_TIMEOUT;
    private final Looper mLooper;
    private AtomicInteger mBusyCount = new AtomicInteger(0);
    private List<LoadTask<T>> mRunningTaskList = new ArrayList<LoadTask<T>>();
    private LinkedList<LoadTask<T>> mWaintingTaskList = new LinkedList<LoadTask<T>>();
    private boolean mIsAbandon = false;

    private DataListener<T> mDefaultDataListener = null;

    public DataLoader(){
        mLooper = Looper.getMainLooper();
    }

    public DataLoader(Looper looper){
        if(looper == null){
            mLooper = Looper.getMainLooper();
        }else{
            mLooper = looper;
        }
    }

    /**
     *
     *加载本地数据。
     */
    public Data<T> loadFromLocal(){
        try {
            return loadFromLocal(false,false, 0, -1);
        }catch (Exception  ex) {
            return null;
        }
    }

    /**
     * 返回数据上次更新时间。
     * @return
     */
    public Date loadLastUpdateTime() {
        Data data = loadFromLocal();
        if (data != null) {
            return data.getLastModifiedTime();
        }
        return null;
    }

    /**
     * 从磁盘里面获取。在后台线程执行。
     * 如果过程中存在错误信息，则抛出异常。
     * @return
     */
    protected abstract T loadFromStore() throws LoaderException;

    protected abstract T loadMoreFromStore() throws LoaderException;

    protected abstract T loadPageFromStore(int pageIndex, int pageSize) throws LoaderException;


    /**
     * 从网络里面获取。在后台线程执行。
     * 如果过程中存在错误信息，则抛出异常。
     * 实现过程中，加载结果用notifier来回调给UI线程处理。如果忘了使用notifier回调结果的话，超过timeout时间自动抛出timeout事件。
     * @param notifier
     * @return
     */
    protected abstract void onLoadFromNet(ResultNotifier<T> notifier) throws LoaderException;

    protected abstract void onLoadMoreFromNet(ResultNotifier<T> notifier) throws LoaderException;

    protected abstract void onLoadPageFromNet(ResultNotifier<T> notifier,int pageIndex,int pageSize) throws LoaderException;

    /**
     * 将结果保存到磁盘。
     * 如果过程中存在错误信息，则抛出异常。
     */
    protected abstract void saveToStore(Data<T> data) throws LoaderException;


    /**
     * 会话ID。会话ID，用于：1、缓存处理； 2，更新时间处理；
     * @return null表示不缓存。
     */
    public abstract String getSessionId();

    public void setDefaultListener(DataListener<T> listener){
        mDefaultDataListener = listener;
    }

    public void load(LoadType loadType) {
        load(loadType, ConflictType.SKIP, null, mDefaultTimeout);
    }

    public void load(LoadType loadType, ConflictType conflict) {
        load(loadType, conflict, null, mDefaultTimeout);
    }

    public void load(LoadType loadType, long timeout) {
        load(loadType, ConflictType.SKIP, null, timeout);
    }

    public void load(LoadType loadType, DataListener<T> listener) {
         load(loadType,listener, mDefaultTimeout);
    }

    public void load(LoadType loadType, DataListener<T> listener,long timeout) {
         load(loadType, ConflictType.SKIP, listener, timeout);
    }

    public void load(LoadType loadType, ConflictType conflictType, DataListener<T> listener) {
         load(loadType, conflictType, listener, mDefaultTimeout);
    }

    public void load(LoadType loadType, ConflictType conflictType, DataListener<T> listener,long timeout) {
         loadInternel(loadType, TYPE_NORMAL, 0, conflictType, listener, timeout);
    }

    public void loadMore() {
        loadMore(LoadType.ONLY_NETWORK, ConflictType.SKIP, null, mDefaultTimeout);
    }

    public void loadMore(LoadType loadType) {
        loadMore(loadType, ConflictType.SKIP, null, mDefaultTimeout);
    }

    public void loadMore(LoadType loadType, ConflictType conflict) {
        loadMore(loadType, conflict, null, mDefaultTimeout);
    }

    public void loadMore(LoadType loadType, long timeout) {
        loadMore(loadType, ConflictType.SKIP, null, timeout);
    }

    public void loadMore(DataListener<T> listener) {
         loadMore(listener, mDefaultTimeout);
    }

    public void loadMore(DataListener<T> listener,long timeout) {
         loadMore(LoadType.ONLY_NETWORK, ConflictType.SKIP, listener, timeout);
    }

    public void loadMore(LoadType loadType, DataListener<T> listener) {
         loadMore(loadType, ConflictType.SKIP, listener, mDefaultTimeout);
    }

    public void loadMore(LoadType loadType, DataListener<T> listener,long timeout) {
         loadMore(loadType, ConflictType.SKIP, listener, timeout);
    }

    public void loadMore(LoadType loadType, ConflictType conflictType, DataListener<T> listener,long timeout) {
         loadInternel(loadType, TYPE_MORE, 0, conflictType, listener, timeout);
    }

    public void loadPage( int pageIndex, int pageSize) {
        loadPage(pageIndex, pageSize, null);
    }

    public void loadPage(LoadType loadType,int pageIndex, int pageSize) {
        loadPage(loadType, pageIndex, pageSize, null);
    }

    public void loadPage(LoadType loadType, int pageIndex, int pageSize, long timeout) {
        loadPage(loadType,pageIndex, pageSize, null,timeout);
    }

    public void loadPage(int pageIndex, int pageSize, DataListener<T> listener) {
         loadPage(LoadType.ONLY_NETWORK, pageIndex, pageSize, listener, mDefaultTimeout);
    }

    public void loadPage(int pageIndex, int pageSize, DataListener<T> listener,long timeout) {
         loadPage(LoadType.ONLY_NETWORK, pageIndex, pageSize, listener, timeout);
    }

    public void loadPage(LoadType loadType,int pageIndex, int pageSize, DataListener<T> listener) {
         loadPage(loadType, pageIndex, pageSize, ConflictType.SKIP, listener, mDefaultTimeout);
    }

    public void loadPage(LoadType loadType,int pageIndex, int pageSize, DataListener<T> listener,long timeout) {
         loadPage(loadType, pageIndex, pageSize, ConflictType.SKIP, listener, timeout);
    }

    public void loadPage(LoadType loadType, int pageIndex, int pageSize, ConflictType conflictType, DataListener<T> listener, long timeout) {
        if(pageIndex < 0){
            throw  new IllegalArgumentException("pageIndex must >= 0");
        }
         loadInternel(loadType, pageIndex, pageSize, conflictType, listener, timeout);
    }

    protected void beforeNotifyResult(Data<T> data) {
    }


    private synchronized void loadInternel(LoadType loadType, int type, int pageSize, ConflictType conflictType, DataListener<T> listener,long timeout) {

        if(mIsAbandon){
            return ;
        }

        if (listener == null) {
            listener = mDefaultDataListener;
        }

        boolean loadNormal = type == TYPE_NORMAL;
        boolean loadMore = false;
        boolean loadPage = false;
        int pageIndex = 0;
        if (!loadNormal) {
            loadMore = type == TYPE_MORE;
        }
        if (!(loadNormal || loadMore)) {
            if (type >= 0) {
                if (pageSize < 1) {
                    throw new IllegalArgumentException("pageSize must be > 0");
                }
                loadPage = true;
                pageIndex = type;
            }
        }

        if (!(loadMore || loadNormal || loadPage)) {
            throw new RuntimeException("unkonw load type!");
        }

        if (timeout < 1) {
            throw new IllegalArgumentException("timeout must be > 0");
        }

        int runningCount = mRunningTaskList.size();

        LoadTask task = new LoadTask(mLooper,this,loadType,loadMore,loadPage,listener,timeout,pageIndex,pageSize);

        if(runningCount > 0){
            if(conflictType == ConflictType.SKIP){
                task.abandon();
                return ;
            }

            if(conflictType == ConflictType.WAIT){
                mWaintingTaskList.add(task);
                return;
            }
        }
        task.setBegin();
        ThreadManager.execute(task);
        //return task;
    }



    @Override
    public synchronized  void abandon() {
        mIsAbandon = true;
        cancel();
    }

    @Override
    public final boolean isAbandon() {
        return mIsAbandon;
    }

    private void dispacthWaitingTask(){

        if(!mRunningTaskList.isEmpty()){
            return;
        }

        LoadTask task = mWaintingTaskList.poll();
        if (task != null) {
            task.setBegin();
            ThreadManager.execute(task);
        }
    }


    public void setDefaultTimeout(long timeout) {
        if (timeout < 1) {
            throw new IllegalArgumentException("timeout must be > 0");
        }
        mDefaultTimeout = timeout;
    }

    public long getDefaultTimeout() {
        return mDefaultTimeout;
    }

    @Override
    public boolean isBusy() {
        return mBusyCount.get() != 0;
    }

    @Override
    public float getProgress() {
        return 0;
    }

    @Override
    public synchronized void cancel() {

        mWaintingTaskList.clear();
        for(LoadTask task : mRunningTaskList){
            task.cancel();
        }
        mRunningTaskList.clear();
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public String getDescription() {
        return null;
    }


    protected Date readLastUpdateTime(){
        //TODO
        String session = getSessionId();

        return null;
    }


    private void loadFromNet(boolean loadMore,boolean loadPage, int pageIndex, int pageSize,ResultNotifier notifier)throws LoaderException{
        if(loadPage){
            onLoadPageFromNet(notifier, pageIndex, pageSize);
        }else if(loadMore){
            onLoadMoreFromNet(notifier);
        }else{
            onLoadFromNet(notifier);
        }
    }


    private Data.DataImpl<T> loadFromLocal(boolean loadMore,boolean loadPage, int pageIndex, int pageSize) throws Exception {

        T result = null;
        Data.DataImpl<T> data = null;
        // 从缓存中读取
        String sessionId = getSessionId();
        if (!TextUtils.isEmpty(sessionId)) {
            try {
                data = (Data.DataImpl<T>) MemoryCache.getInstance().getFromCache(getClass(), sessionId);
            }catch (Exception ex){

            }
        }


        if(data == null){
            if(loadPage){
                result = loadPageFromStore(pageIndex, pageSize);
            }else if(loadMore){
                result = loadMoreFromStore();
            }else{
                result = loadFromStore();
            }
            data = new Data.DataImpl<T>();
            data.set(result);
            data.setIsFromLocal(true);
            data.setLastModifiedTime(readLastUpdateTime());
        }

        return data;
    }


    protected  synchronized void onTaskBegin(LoadTask task){
        mBusyCount.incrementAndGet();
        mRunningTaskList.add(task);
    }


    protected synchronized void onTaskFinished(LoadTask task){
        mRunningTaskList.remove(task);
        dispacthWaitingTask();
        mBusyCount.decrementAndGet();
        task.abandon();
    }

    protected synchronized void onPrgerssChaged(LoadTask task, float pregress){

    }


    private  static final int TYPE_NORMAL = -1;
    private  static final int TYPE_MORE = -2;

    private interface  NotifiedFinishedListenr{

        void onFinished();
    }

    private static class TaskTrace<RESUlT> implements ResultNotifier<RESUlT>{

        private final LoadTask mLoadTask;
        private boolean notifyFinished = false;
        private boolean notifyEnable = false;
        private NotifiedFinishedListenr mNotifiedFinishedListenr = null;

        public TaskTrace(LoadTask task) {
            mLoadTask = task;
            notifyFinished = false;
        }

        public boolean hasNotified(){
            return notifyFinished;
        }


        public synchronized void prepareLoadNetwork(NotifiedFinishedListenr listenr){
            notifyEnable = true;
            mNotifiedFinishedListenr = listenr;
            mLoadTask.startTimmingWork(mLoadTask.mTimeout, new Runnable() {
                @Override
                public void run() {
                    TaskTrace.this.notifyTimeout();
                }
            });
        }

        public synchronized void stopNotifiier(){
            notifyEnable = false;
            mLoadTask.stopTimmingWork();
        }


        @Override
        public synchronized void notifyTemporaryReulst(RESUlT result) {
            if(!notifyEnable){
                return;
            }
            if(result == null){
                throw new NullPointerException();
            }
            if(!notifyFinished){
                Data.DataImpl<RESUlT> data = new Data.DataImpl<RESUlT>();
                data.set(result);
                data.setIsTemporary(true);
                data.setIsFromLocal(false);
                mLoadTask.notifyResult(data);
            }
        }

        @Override
        public synchronized void notifyResult(RESUlT result) {
            if(!notifyEnable){
                return;
            }
            if(result == null){
                throw new NullPointerException();
            }

            if (notifyFinished) {
                throw new IllegalStateException("you has notified before.");
            }
            mLoadTask.stopTimmingWork();

            Data.DataImpl<RESUlT> data = new Data.DataImpl<RESUlT>();
            data.set(result);
            data.setLastModifiedTime(new Date());
            data.setIsFromLocal(false);
            data.setIsTemporary(false);

            if(mLoadTask.mLoadMore){
                data.setIsLoadMore(true);
            }else if(mLoadTask.mLoadPage){
                data.setPageIndex(mLoadTask.mPageIndex);
                data.setPageSize(mLoadTask.mPageSize);
            }

            mLoadTask.notifyResult(data);
            finishedNotifyResult();

        }

        @Override
        public synchronized void notifyError(String message, Exception except) {
            if(!notifyEnable){
                return;
            }
            if (notifyFinished) {
                throw new IllegalStateException("you has notified before.");
            }
            mLoadTask.stopTimmingWork();
            mLoadTask.notifyError(message, except);
            finishedNotifyResult();
        }

        @Override
        public synchronized void notifyTimeout() {
            if(!notifyEnable){
                return;
            }
            if (notifyFinished) {
                throw new IllegalStateException("you has notified before.");
                //return;
            }
            mLoadTask.stopTimmingWork();
            mLoadTask.notifyTimeout();
            finishedNotifyResult();

        }


        @Override
        public void notifyProgress(float progress) {
            if(!notifyEnable){
                return;
            }
            mLoadTask.notifyProgress(progress);
        }

        @Override
        public boolean isCancel() {
            return mLoadTask.isCancel;
        }

        private void finishedNotifyResult(){
            notifyFinished = true;

            if (mNotifiedFinishedListenr != null) {
                mNotifiedFinishedListenr.onFinished();
                mNotifiedFinishedListenr = null;
            }
        }


    }

    private static class LoadTask<RESUlT> extends Handler implements Runnable,BusyFeedback {

        LoadType mLoadType;
        int mPageIndex;
        int mPageSize;
        DataListener<RESUlT> mListener;
        long mTimeout;
        boolean mLoadMore = false;
        boolean mLoadPage = false;
        DataLoader<RESUlT> mDataLoader;

        private boolean mBusy = false;
        private boolean isCancel = false;
        private float prgoress = 0f;
        private boolean isAbandon =false;
        private Thread mRunningThread;

        private LoadTask(Looper looper, DataLoader<RESUlT> loader, LoadType type, boolean loadmore, boolean loadPage, DataListener<RESUlT> listener, long timeout, int pageIndex, int pageSize) {
            super(looper);
            mDataLoader = loader;
            mLoadType= type;
            mLoadMore =loadmore;
            mLoadPage = loadPage;
            mListener = listener;
            mTimeout = timeout;
            mPageIndex = pageIndex;
            mPageSize =  pageSize;
            if(mListener == null){
                mListener = NULL_DATA_LISTNER;
            }
        }


        @Override
        public boolean isBusy() {
            return mBusy;
        }

        @Override
        public float getProgress() {
            return prgoress;
        }

        public void cancel(){
            isCancel = true;
            try{
                mRunningThread.interrupted();
            }catch (Exception ex){
                //ignore
            }

        }

        @Override
        public boolean isCancellable() {
            return true;
        }

        public synchronized void setBegin(){

            if (!mBusy) {
                mBusy = true;
                mDataLoader.onTaskBegin(this);
            }

        }

        public synchronized void setFinished(){
            if (mBusy) {
                mBusy = false;
                sendEmptyMessage(MSG_TASK_FINISHED);
            }
        }


        @Override
        public void run() {
           // TaskTrace trace =  new TaskTrace(this);
            mRunningThread = Thread.currentThread();
            Throwable th = null;
            String errMsg = null;

            final TaskTrace taskTrace = new TaskTrace(this);
            try {

                if (LoadType.ONLY_STORE == mLoadType) {
                    Data.DataImpl<RESUlT> data = mDataLoader.loadFromLocal(mLoadMore,mLoadPage, mPageIndex, mPageSize);

                    notifyResult(data);
                    setFinished();
                    return;
                } else if (LoadType.ONLY_NETWORK == mLoadType) {
                    taskTrace.prepareLoadNetwork(new NotifiedFinishedListenr() {
                        @Override
                        public void onFinished() {
                            setFinished();
                        }
                    });
                    mDataLoader.loadFromNet(mLoadMore, mLoadPage, mPageIndex, mPageSize, taskTrace);
                    return;
                } else if (LoadType.LOCAL_AND_NETWORK == mLoadType) {

                    Data.DataImpl<RESUlT> data = mDataLoader.loadFromLocal(mLoadMore,mLoadPage, mPageIndex, mPageSize);
                    notifyResult(data);

                    if(isCancel){
                        notifyCancel();
                        setFinished();
                        return;
                    }

                    taskTrace.prepareLoadNetwork(new NotifiedFinishedListenr() {
                        @Override
                        public void onFinished() {
                            setFinished();
                        }
                    });
                    mDataLoader.loadFromNet(mLoadMore, mLoadPage, mPageIndex, mPageSize, taskTrace);
                    return;
                } else if (LoadType.STORE_FIRST == mLoadType) {

                    Data.DataImpl<RESUlT> data = mDataLoader.loadFromLocal(mLoadMore,mLoadPage,mPageIndex,mPageSize);
                    if (data.get() != null) {
                        notifyResult(data);
                        return;
                    }
                    if(isCancel){
                        notifyCancel();
                        setFinished();
                        return;
                    }

                    taskTrace.prepareLoadNetwork(new NotifiedFinishedListenr() {
                        @Override
                        public void onFinished() {
                            setFinished();
                        }
                    });
                    mDataLoader.loadFromNet(mLoadMore, mLoadPage, mPageIndex, mPageSize, taskTrace);
                    return;
                }

            } catch (Throwable ex) {
                th = ex;
                if (ex != null) {
                    errMsg = ex.getMessage();
                }
            }finally {
                mRunningThread = null;
            }
            if(isCancel){
                notifyCancel();
            }else{
                notifyError(errMsg, th);
            }
            setFinished();
        }






        private void putCahce(Data.DataImpl<RESUlT> data){
            if (!TextUtils.isEmpty(mDataLoader.getSessionId())) {
                MemoryCache.getInstance().putCache(getClass(), mDataLoader.getDescription(), data);
            }
        }


        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case MSG_NOTIFY_RESULT:
                    Data<RESUlT> data = ( Data<RESUlT>) msg.obj;
                    mListener.onResult(mDataLoader, data);
                    break;
                case MSG_TIMMING_WORK:
                    Runnable tiemoutRunnable = (Runnable) msg.obj;// = runnable;
                    tiemoutRunnable.run();
                    break;
                case MSG_NOTIFY_TIMEOUT:
                    mListener.onTimeout(mDataLoader);
                    break;
                case MSG_NOTIFY_ERROR:
                    Object[] params = ( Object[])msg.obj;
                    mListener.onError(mDataLoader, (String) params[0], (Exception) params[1]);
                    break;
                case MSG_NOTIFY_PROGRESS:
                    Float progerss = (Float) msg.obj;
                    mDataLoader.onPrgerssChaged(this, progerss);
                    break;
                case MSG_TASK_FINISHED:
                    mDataLoader.onTaskFinished(this);
                    break;
                case MSG_NOTIFY_CANCEL:
                    mListener.onCancel(mDataLoader);
                    break;

            }

        }

        public void notifyResult(Data<RESUlT> data) {

            mDataLoader.beforeNotifyResult(data);
            Message msg = Message.obtain();
            msg.what = MSG_NOTIFY_RESULT;
            msg.obj = data;
            sendMessage(msg);
        }



        public void notifyProgress(float progress) {
            Message msg = Message.obtain();
            msg.what = MSG_NOTIFY_PROGRESS;
            msg.obj = new Float(progress);
            prgoress = progress;
            sendMessage(msg);
        }

        public void notifyCancel() {
            sendEmptyMessage(MSG_NOTIFY_CANCEL);
        }

        public synchronized void notifyError(String message, Throwable except) {
            Exception e = null;
            if(except instanceof Exception){
                e = (Exception)except;
            }else{
                e = new Exception(except);
            }
            notifyError(message,e);
        }

        public synchronized void notifyError(String message, Exception except) {
            Object[] params = new Object[]{message,except};
            Message msg = Message.obtain();
            msg.what = MSG_NOTIFY_ERROR;
            msg.obj = params;
            sendMessage(msg);
        }

        public synchronized void notifyTimeout() {
            sendEmptyMessage(MSG_NOTIFY_TIMEOUT);
        }

        public void startTimmingWork(long timeout,Runnable runnable){
            Message msg = Message.obtain();
            msg.what = MSG_TIMMING_WORK;
            msg.obj = runnable;
            sendMessageDelayed(msg,timeout);
        }

        public void stopTimmingWork(){
            removeMessages(MSG_TIMMING_WORK);
        }


        private static final int MSG_NOTIFY_RESULT = 100;
        private static final int MSG_TIMMING_WORK = 101;
        private static final int MSG_NOTIFY_TIMEOUT = 102;
        private static final int MSG_NOTIFY_ERROR = 103;
        private static final int MSG_NOTIFY_PROGRESS = 104;

        private static final int MSG_TASK_FINISHED = 106;
        private static final int MSG_NOTIFY_CANCEL = 107;



        @Override
        public String getDescription() {
            return "LoadTask-" + System.identityHashCode(this);
        }

        @Override
        public void abandon() {
            isAbandon = true;
        }

        @Override
        public boolean isAbandon() {
            return isAbandon;
        }
    }


    private static final DataListener NULL_DATA_LISTNER = new  DataListener(){

        @Override
        public void onResult(DataLoader loader, Data data) {

        }

        @Override
        public void onError(DataLoader loader, String message, Exception error) {

        }

        @Override
        public void onCancel(DataLoader loader) {

        }

        @Override
        public void onTimeout(DataLoader loader) {

        }
    };

}

