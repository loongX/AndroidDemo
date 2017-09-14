package com.slzr.dagger2t;

import dagger.Component;

/**
 * Created by pxl on 2017/9/14.
 */
@Component(modules = BeanModule.class)
public interface BeanComponent {
    void inject(MainActivity activity);
}
