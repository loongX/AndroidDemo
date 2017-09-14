package com.slzr.dagger2t.app.act;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pxl on 2017/9/14.
 */
@Module
public class ActivityModule {
    @Provides
    ActivityBean providerActivityBean() {
        return new ActivityBean();
    }
}
