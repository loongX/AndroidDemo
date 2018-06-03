package com.rdm.base;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: donnyliu
 */
public class ThreadManager {

    private static boolean debug = false;

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        ThreadManager.debug = debug;
    }

    public static Thread newThread(Runnable runnable, String name) {
        return new Thread(runnable, name);
    }

    public static Thread newThread(ThreadGroup group, Runnable runnable, String threadName, long stackSize) {
        return new Thread(group, runnable, threadName, stackSize);
    }

    public static void execute(Runnable runnable) {
        Executor executor = ThreadBuilder.getInstance().getThreadPoolExecutor();

        executor.execute(runnable);
    }

    public static void executeInSerialQueue(Runnable runnable) {
        Executor executor = ThreadBuilder.getInstance().getSerialExecutor();

        executor.execute(runnable);
    }

    public static Executor newThreadPoolExecutor(int corePoolSize,
                                                 int maximumPoolSize,
                                                 long keepAliveTime,
                                                 TimeUnit unit,
                                                 BlockingQueue<Runnable> workQueue,
                                                 ThreadFactory threadFactory) {

        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }


    public static class ThreadBuilder
    {
        private static ThreadBuilder instance = new ThreadBuilder();

        public static ThreadBuilder getInstance ()
        {
            return instance;
        }

        private static final int CPU_COUNT          = Runtime.getRuntime().availableProcessors();
        private static final int CORE_POOL_SIZE     = CPU_COUNT + 1;
        private static final int MAXIMUM_POOL_SIZE  = CPU_COUNT * 2 + 1;
        private static final int MAXIMUM_QUEUE_SIZE = ThreadManager.isDebug() ? 18 : 128;
        private static final int KEEP_ALIVE         = 1;

        private static final String QT_THREAD_NAME_PREFIX = "QtThread #";

        private static final BlockingQueue<Runnable> POOL_WORK_QUEUE =
                new LinkedBlockingQueue<Runnable>(MAXIMUM_QUEUE_SIZE);

        private static final ThreadFactory THREAD_FACTORY = new ThreadFactory()
        {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread (Runnable r)
            {
                return ThreadManager.newThread(r, QT_THREAD_NAME_PREFIX + mCount.getAndIncrement());
            }
        };

        private static final RejectedExecutionHandler DISCARD_OLDEST_POLICY = new ThreadPoolExecutor.DiscardOldestPolicy()
        {
            @Override
            public void rejectedExecution (Runnable r, ThreadPoolExecutor e)
            {
                if (ThreadManager.isDebug())
                {
                    String rejectedTaskMsg = "Task " + r.toString() +
                            " rejected from " +
                            e.toString();

                    String runningTaskState = runningTaskState();

                    throw new RejectedExecutionException(rejectedTaskMsg + "\n" + runningTaskState);
                }

                super.rejectedExecution(r, e);
            }
        };

        public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, POOL_WORK_QUEUE, THREAD_FACTORY, DISCARD_OLDEST_POLICY
        );

        public static final Executor SERIAL_EXECUTOR = new SerialExecutor();

        private ThreadBuilder ()
        {
        }

        public Executor getThreadPoolExecutor ()
        {
            return THREAD_POOL_EXECUTOR;
        }

        public Executor getSerialExecutor ()
        {
            return SERIAL_EXECUTOR;
        }

        private static class SerialExecutor implements Executor
        {
            private final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();

            private Runnable mActive;

            public synchronized void execute (final Runnable r)
            {
                mTasks.offer(new Runnable()
                {
                    public void run ()
                    {
                        try
                        {
                            r.run();
                        }
                        finally
                        {
                            scheduleNext();
                        }
                    }
                });
                if (mActive == null)
                {
                    scheduleNext();
                }
            }

            protected synchronized void scheduleNext ()
            {
                if ((mActive = mTasks.poll()) != null)
                {
                    THREAD_POOL_EXECUTOR.execute(mActive);
                }
            }
        }

        private static String runningTaskState ()
        {
            StringBuilder sb = new StringBuilder();

            int count = 0;

            Map<Thread, StackTraceElement[]> activeThreads = Thread.getAllStackTraces();
            for (Map.Entry<Thread, StackTraceElement[]> entry : activeThreads.entrySet())
            {
                Thread thread = entry.getKey();
                String name = thread.getName();

                if (name != null && name.startsWith(QT_THREAD_NAME_PREFIX))
                {
                    count++;

                    sb.append(thread.toString()).append('\n').append(stackTraceElement(entry.getValue()));
                }
            }

            sb.insert(0, "\nCurrent ThreadPool Running Tasks:" + count + "\n");

            return sb.toString();
        }

        private static String stackTraceElement (StackTraceElement[] elements)
        {
            if (elements != null)
            {
                StringBuilder sTrace = new StringBuilder(100);

                for (StackTraceElement element : elements)
                {
                    sTrace.append(element.toString()).append('\n');
                }

                return sTrace.toString();
            }
            return "";
        }

    }
}
