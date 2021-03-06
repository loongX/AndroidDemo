
package com.rdm.base.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.rdm.common.ILog;
import com.rdm.base.annotation.PluginApi;
import com.rdm.base.db.util.PriorityThreadFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadPool {
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 8;
    private static final int KEEP_ALIVE_TIME = 10; // 10 seconds

    // Resource type
    @PluginApi(since = 4)
    public static final int MODE_NONE = 0;
    @PluginApi(since = 4)
    public static final int MODE_CPU = 1;
    @PluginApi(since = 4)
    public static final int MODE_NETWORK = 2;

    public static final JobContext JOB_CONTEXT_STUB = new JobContextStub();

    ResourceCounter mCpuCounter = new ResourceCounter(2);
    ResourceCounter mNetworkCounter = new ResourceCounter(2);

    @PluginApi(since = 4)
    // A Job is like a Callable, but it has an addition JobContext parameter.
    public interface Job<T> {
        @PluginApi(since = 4)
        public T run(JobContext jc);
    }

    @PluginApi(since = 4)
    public interface JobContext {
        @PluginApi(since = 4)
        boolean isCancelled();

        @PluginApi(since = 4)
        boolean setMode(int mode);
    }

    private static class JobContextStub implements JobContext {
        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean setMode(int mode) {
            return true;
        }
    }

    private static class ResourceCounter {
        public int value;

        public ResourceCounter(int v) {
            value = v;
        }
    }

    public static enum Priority {
        @PluginApi(since = 4)
        LOW(1), //
        @PluginApi(since = 4)
        NORMAL(2), //
        @PluginApi(since = 4)
        HIGH(3);

        int priorityInt;

        Priority(int priority) {
            priorityInt = priority;
        }
    }

    private final Executor mExecutor;

    @PluginApi(since = 4)
    public ThreadPool() {
        this("thread-pool", CORE_POOL_SIZE, MAX_POOL_SIZE);
    }

    @PluginApi(since = 4)
    public ThreadPool(String name, int coreSize, int maxSize) {
        if (coreSize <= 0)
            coreSize = 1;
        if (maxSize <= coreSize)
            maxSize = coreSize;

        mExecutor = new ThreadPoolExecutor(coreSize, maxSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new PriorityBlockingQueue<Runnable>(), new PriorityThreadFactory(name,
                        android.os.Process.THREAD_PRIORITY_BACKGROUND));
    }

    public ThreadPool(String name, int coreSize, int maxSize, BlockingQueue<Runnable> queue) {
        if (coreSize <= 0)
            coreSize = 1;
        if (maxSize <= coreSize)
            maxSize = coreSize;

        mExecutor = new ThreadPoolExecutor(coreSize, maxSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS, queue,
                new PriorityThreadFactory(name, android.os.Process.THREAD_PRIORITY_BACKGROUND));
    }

    @PluginApi(since = 4)
    // Submit a job to the thread pool. The listener will be called when the
    // job is finished (or cancelled).
    public <T> Future<T> submit(Job<T> job, FutureListener<T> listener, Priority priority) {
        // handle the priority job.
        Worker<T> w = generateWorker(job, listener, priority);
        mExecutor.execute(w);
        return w;
    }

    @PluginApi(since = 4)
    public <T> Future<T> submit(Job<T> job, FutureListener<T> listener) {
        return submit(job, listener, Priority.NORMAL);
    }

    @PluginApi(since = 4)
    public <T> Future<T> submit(Job<T> job, Priority priority) {
        return submit(job, null, priority);
    }

    @PluginApi(since = 4)
    public <T> Future<T> submit(Job<T> job) {
        return submit(job, null, Priority.NORMAL);
    }

    @PluginApi(since = 4)
    public  void submit(final Runnable runnable) {
        Thread t = new Thread(runnable);
        t.start();
        //下面的方法有异常不抛出，所以不用
      /*  submit(new Job<Object>() {
            @Override
            public Object run(JobContext jc) {
                runnable.run();
                return null;
            }
        });*/

    }

    private <T> Worker<T> generateWorker(Job<T> job, FutureListener<T> listener, Priority priority) {
        final Worker<T> worker;
        switch (priority) {
            case LOW:
                worker = new PriorityWorker<T>(job, listener, priority.priorityInt, false);
                break;

            case NORMAL:
                worker = new PriorityWorker<T>(job, listener, priority.priorityInt, false);
                break;

            case HIGH:
                worker = new PriorityWorker<T>(job, listener, priority.priorityInt, true);
                break;

            default:
                worker = new PriorityWorker<T>(job, listener, priority.priorityInt, false);
                break;
        }
        return worker;
    }

    private class Worker<T> implements Runnable, Future<T>, JobContext {
        private static final String TAG = "Worker";
        private Job<T> mJob;
        private FutureListener<T> mListener;
        private CancelListener mCancelListener;
        private ResourceCounter mWaitOnResource;
        private volatile boolean mIsCancelled;
        private boolean mIsDone;
        private T mResult;
        private int mMode;

        public Worker(Job<T> job, FutureListener<T> listener) {
            mJob = job;
            mListener = listener;
        }

        // This is called by a thread in the thread pool.
        public void run() {
            if (mListener != null)
                mListener.onFutureBegin(this);

            T result = null;

            // A job is in CPU mode by default. setMode returns false
            // if the job is cancelled.
            if (setMode(MODE_CPU)) {
                try {
                    result = mJob.run(this);
                } catch (Throwable ex) {

                    ILog.w(TAG, "Exception in running a job", ex);
                }
            }

            synchronized (this) {
                setMode(MODE_NONE);
                mResult = result;
                mIsDone = true;
                notifyAll();
            }
            if (mListener != null)
                mListener.onFutureDone(this);
        }

        // Below are the methods for Future.
        public synchronized void cancel() {
            if (mIsCancelled)
                return;
            mIsCancelled = true;
            if (mWaitOnResource != null) {
                synchronized (mWaitOnResource) {
                    mWaitOnResource.notifyAll();
                }
            }
            if (mCancelListener != null) {
                mCancelListener.onCancel();
            }
        }

        public boolean isCancelled() {
            return mIsCancelled;
        }

        public synchronized boolean isDone() {
            return mIsDone;
        }

        public synchronized T get() {
            while (!mIsDone) {
                try {
                    wait();
                } catch (Exception ex) {
                    Log.w(TAG, "ignore exception", ex);
                    // ignore.
                }
            }
            return mResult;
        }

        public void waitDone() {
            get();
        }

        // Below are the methods for JobContext (only called from the
        // thread running the job)
        public synchronized void setCancelListener(CancelListener listener) {
            mCancelListener = listener;
            if (mIsCancelled && mCancelListener != null) {
                mCancelListener.onCancel();
            }
        }

        public boolean setMode(int mode) {
            // Release old resource
            ResourceCounter rc = modeToCounter(mMode);
            if (rc != null)
                releaseResource(rc);
            mMode = MODE_NONE;

            // Acquire new resource
            rc = modeToCounter(mode);
            if (rc != null) {
                if (!acquireResource(rc)) {
                    return false;
                }
                mMode = mode;
            }

            return true;
        }

        private ResourceCounter modeToCounter(int mode) {
            if (mode == MODE_CPU) {
                return mCpuCounter;
            } else if (mode == MODE_NETWORK) {
                return mNetworkCounter;
            } else {
                return null;
            }
        }

        private boolean acquireResource(ResourceCounter counter) {
            while (true) {
                synchronized (this) {
                    if (mIsCancelled) {
                        mWaitOnResource = null;
                        return false;
                    }
                    mWaitOnResource = counter;
                }

                synchronized (counter) {
                    if (counter.value > 0) {
                        counter.value--;
                        break;
                    } else {
                        try {
                            counter.wait();
                        } catch (InterruptedException ex) {
                            // ignore.
                        }
                    }
                }
            }

            synchronized (this) {
                mWaitOnResource = null;
            }

            return true;
        }

        private void releaseResource(ResourceCounter counter) {
            synchronized (counter) {
                counter.value++;
                counter.notifyAll();
            }
        }
    }

    static final AtomicLong SEQ = new AtomicLong(0);

    private class PriorityWorker<T> extends Worker<T> implements Comparable<PriorityWorker> {

        /**
         * the bigger, the prior.
         */
        private final int mPriority;

        /**
         * whether filo(with same {@link #mPriority}).
         */
        private final boolean mFilo;

        /**
         * seq number.
         */
        private final long mSeqNum;

        public PriorityWorker(Job<T> job, FutureListener<T> listener, int priority, boolean filo) {
            super(job, listener);
            mPriority = priority;
            mFilo = filo;
            mSeqNum = SEQ.getAndIncrement();
        }

        @Override
        public int compareTo(PriorityWorker another) {
            return mPriority > another.mPriority ? -1 : (mPriority < another.mPriority ? 1 : subCompareTo(another));
        }

        private int subCompareTo(PriorityWorker another) {
            int result = mSeqNum < another.mSeqNum ? -1 : (mSeqNum > another.mSeqNum ? 1 : 0);
            return mFilo ? -result : result;
        }
    }

    // ------------- singleton ---------------------
    @PluginApi(since = 4)
    public static ThreadPool getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        public static final ThreadPool INSTANCE = new ThreadPool();
    }

    @PluginApi(since = 4)
    public static void runOnNonUIThread(final Runnable runnable) {
        ThreadPool.getInstance().submit(new Job<Object>() {
            @Override
            public Object run(JobContext jc) {
                runnable.run();
                return null;
            }
        });
        //强制开其它线程，有可能占用网络线程，所以不用下面的方式处理。
        /*if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            ThreadPool.getInstance().submit(new Job<Object>() {
                @Override
                public Object run(JobContext jc) {
                    runnable.run();
                    return null;

            });
        } else {
            runnable.run();
        }*/
    }

    public static void runOnNonUIThread(final Runnable runnable, final long delay) {

        UI_HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnNonUIThread(runnable);
            }
        }, delay);

    }
    /**
     * UI线程
     * */
    private static Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 副线程的Handle, 只有一个线程
     * 可以执行比较快但不能在ui线程执行的操作.
     * 文件读写不建议在此线程执行, 请使用FILE_THREAD_HANDLER
     */
    private static Handler SUB_THREAD_HANDLER;
    private static HandlerThread SUB_THREAD;

    /**
     * 负责消息回调处理
     */
    private static Handler MESSAGE_THREAD_HANDLER;

    /**
     * 发送消息和定时器等轻量级的用，其它的不要用啊。切记
     * */
    private static Handler SEND_MSG_HANDLER;
    private static HandlerThread SEND_MSG_THREAD;

    /**
     * 发送消息和定时器等轻量级的用，其它的不要用啊。切记
     * @return handler
     */
    public static Handler getSendMsgHandler()
    {
        if(SEND_MSG_HANDLER == null)
        {
            synchronized(ThreadPool.class)
            {

                SEND_MSG_THREAD = new HandlerThread("ThreadPool:send_msg_thread");

                SEND_MSG_THREAD.start();
                SEND_MSG_HANDLER = new Handler(SEND_MSG_THREAD.getLooper());
            }
        }
        return SEND_MSG_HANDLER;
    }



    public static Thread getSubThread()
    {
        if (SUB_THREAD == null)
        {
            getSubThreadHandler();
        }
        return SUB_THREAD;
    }

    /**
     * 获得副线程的Handler.
     * 副线程可以执行比较快但不能在ui线程执行的操作.
     * @return handler
     */
    public static Handler getSubThreadHandler()
    {
        if(SUB_THREAD_HANDLER == null)
        {
            synchronized(ThreadPool.class)
            {


                SUB_THREAD = new HandlerThread("ThreadPool:sub_thread");

                SUB_THREAD.start();
                SUB_THREAD_HANDLER = new Handler(SUB_THREAD.getLooper());
            }
        }
        return SUB_THREAD_HANDLER;
    }

    public static Looper getSubThreadLooper()
    {
        return getSubThreadHandler().getLooper();
    }

    /**
     * 在UI线程运行
     * @param runnable
     */
    public static void runUITask(Runnable runnable) {
        UI_HANDLER.post(runnable);
    }

    public static void runUITask(Runnable runable, long delayMillis){
        UI_HANDLER.postDelayed(runable, delayMillis);
    }

    /**
     * 获取用来消息回调处理的Handler
     * 只提供给消息回调处理线程使用. 其他人不要用
     */

    public static Handler getMessageThreadHandler()
    {
        if(MESSAGE_THREAD_HANDLER == null)
        {
            synchronized(ThreadPool.class)
            {
                HandlerThread thread = new HandlerThread("ThreadPool:message_handler");
                thread.start();
                MESSAGE_THREAD_HANDLER = new Handler(thread.getLooper());
            }
        }
        return MESSAGE_THREAD_HANDLER;
    }

    public static Looper getMessageThreadLooper(){
        return getMessageThreadHandler().getLooper();
    }

    /**
     * 返回一个"线性"的Executor. <br>
     * 通过此对象的execute()方法执行的任务会串行执行. 但不会新建线程, 而是使用线程池中的线程, 因此仍然会受到线程池的限制.<br>
     *
     * 注意每次调用该方法都会返回一个新的对象.只有同一个对象的execute()方法才会串行执行,不同对象的execute方法是不会串行访问的.这一点需要注意<br>
     * @return
     */
    public static Executor newSerialExecutor()
    {
        return new SerialExecutor();
    }

    private static class SerialExecutor implements Executor {
        final Queue<Runnable> mTasks = new LinkedList<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                runOnNonUIThread(mActive);
            }
        }
    }

}
