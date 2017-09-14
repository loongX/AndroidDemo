package com.slzr.dagger2t;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pxl on 2017/9/13.
 */
@Module//提供依赖对象的实例
public class MainModule {

    @Provides// 关键字，标明该方法提供依赖对象
    Person providerPerson(){
        return new Person();//提供Person对象
    }

}
