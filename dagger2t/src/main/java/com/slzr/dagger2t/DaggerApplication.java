package com.slzr.dagger2t;

import android.app.Application;

/**
 * Created by pxl on 2017/9/14.
 */

public class DaggerApplication extends Application {
    private BeanComponent mBeanComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mBeanComponent = DaggerBeanComponent.create();
    }


    public BeanComponent getBeanComponent() {
        return mBeanComponent;
    }
}
