package com.slzr.dagger2t.app;

import android.app.Application;
import android.util.Log;

import com.slzr.dagger2t.BeanComponent;
import com.slzr.dagger2t.DaggerBeanComponent;

import javax.inject.Inject;

/**
 * Created by pxl on 2017/9/14.
 */

public class DaggerApplication extends Application {
    String TAG = getClass().getSimpleName();
    private ApplicationComponent mAppComponent;
    @Inject
    ApplicationBean mAppBean1;
    @Inject
    ApplicationBean mAppBean2;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mAppComponent == null) {
            mAppComponent = DaggerApplicationComponent.create();
        }
        mAppComponent.inject(this);
        Log.d(TAG, "Application mAppBean1:" + mAppBean1);
        Log.d(TAG, "Application mAppBean2:" + mAppBean2);
    }


    public ApplicationComponent getAppComponent() {
        return mAppComponent;
    }
}
