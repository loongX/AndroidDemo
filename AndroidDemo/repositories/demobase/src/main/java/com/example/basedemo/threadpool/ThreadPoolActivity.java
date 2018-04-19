package com.example.basedemo.threadpool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.basedemo.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by pxl on 2017/9/4.
 */

public class ThreadPoolActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ThreadPoolActivity";

    /** 总共多少任务（根据CPU个数决定创建活动线程的个数,这样取的好处就是可以让手机承受得住） */
    // private static final int count = Runtime.getRuntime().availableProcessors() * 3 + 2;

    /**
     * 总共多少任务
     */
    private static final int count = 3;

    /**
     * 所有任务都一次性开始的线程池
     */
    private static ExecutorService mCacheThreadExecutor = null;

    /**
     * 每次执行限定个数个任务的线程池
     */
    private static ExecutorService mFixedThreadExecutor = null;

    /**
     * 创建一个可在指定时间里执行任务的线程池，亦可重复执行
     */
    private static ScheduledExecutorService mScheduledThreadExecutor = null;

    /**
     * 每次只执行一个任务的线程池
     */
    private static ExecutorService mSingleThreadExecutor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threadpool);
        initExecutorService();
        initView();
        Log.e(TAG, "当前线程数 activeCount:" + Thread.activeCount());
    }

    private void initExecutorService() {
        mCacheThreadExecutor = Executors.newCachedThreadPool();// 一个没有限制最大线程数的线程池
        mFixedThreadExecutor = Executors.newFixedThreadPool(count);// 限制线程池大小为count的线程池
        mScheduledThreadExecutor = Executors.newScheduledThreadPool(count);// 一个可以按指定时间可周期性的执行的线程池
        mSingleThreadExecutor = Executors.newSingleThreadExecutor();// 每次只执行一个线程任务的线程池
    }

    private void initView() {
        findViewById(R.id.mCacheThreadExecutorBtn).setOnClickListener(this);
        findViewById(R.id.mFixedThreadExecutorBtn).setOnClickListener(this);
        findViewById(R.id.mScheduledThreadExecutorBtn).setOnClickListener(this);
        findViewById(R.id.mSingleThreadExecutorBtn).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        Log.e(TAG, "id:"+id );
        if (id == R.id.mCacheThreadExecutorBtn) {
            Log.e(TAG, "Thread:按键1" );
            ExecutorServiceThread(mCacheThreadExecutor);

        } else if (id == R.id.mFixedThreadExecutorBtn) {
            Log.e(TAG, "Thread:按键2" );
            ExecutorServiceThread(mFixedThreadExecutor);

        } else if (id == R.id.mScheduledThreadExecutorBtn) {
            Log.e(TAG, "Thread:按键3" );
            ExecutorScheduleThread(mScheduledThreadExecutor);

        } else if (id == R.id.mSingleThreadExecutorBtn) {
            Log.e(TAG, "Thread:按键4" );
            ExecutorServiceThread(mSingleThreadExecutor);

        }

    }

    private void ExecutorServiceThread(ExecutorService executorService) {
        for (int i = 0; i < 10; ++i) {
            final int index = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "Thread:" + Thread.currentThread().getId() + " activeCount:" + Thread.activeCount() + " index:" + index);
                }
            });
        }
    }

    private void ExecutorScheduleThread(ScheduledExecutorService scheduledExecutorService) {
        for (int i = 0; i < 11; ++i) {
            final int index = i;
            scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "Thread:" + Thread.currentThread().getId() + " activeCount:" + Thread.activeCount() + " index:" + index);
                }
            }, 2, TimeUnit.SECONDS);
        }
    }
}
