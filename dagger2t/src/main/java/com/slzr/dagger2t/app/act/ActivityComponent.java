package com.slzr.dagger2t.app.act;

import com.slzr.dagger2t.MainActivity;
import com.slzr.dagger2t.app.ApplicationComponent;

import dagger.Component;

/**
 * Created by pxl on 2017/9/14.
 */
@ForActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivityApp activity);


    void inject(MainActivityApp.OtherClass otherClass);
}
