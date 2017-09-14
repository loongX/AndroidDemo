package com.slzr.dagger2t.app;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pxl on 2017/9/14.
 */
@Module
public class ApplicationModule {
    //作为单例模式注入app
    @Singleton
    @Provides
    ApplicationBean privoderAppBean() {
        return new ApplicationBean();
    }
}
