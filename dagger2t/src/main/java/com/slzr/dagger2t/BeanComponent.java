package com.slzr.dagger2t;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by pxl on 2017/9/14.
 */
@Singleton
@Component(modules = BeanModule.class)
public interface BeanComponent {
    void inject(MainActivity activity);

    //将BeanModule注入OtherClass
    void inject(MainActivity.OtherClass otherClass);
}
