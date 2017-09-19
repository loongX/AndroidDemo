package com.slzr.dagger2test.app;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by pxl on 2017/9/14.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(DaggerApplication application);


    //说明将BeanForApplication开放给其他Component使用
    ApplicationBean providerAppBean();
}
