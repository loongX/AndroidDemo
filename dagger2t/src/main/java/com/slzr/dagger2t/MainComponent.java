package com.slzr.dagger2t;


import dagger.Component;

/**
 * Created by pxl on 2017/9/13.
 */
@Component(modules = MainModule.class)// 作为桥梁，沟通调用者和依赖对象库
public interface MainComponent {
    //定义注入的方法
    void inject(MainActivity activity);
}
