package com.slzr.dagger2t;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pxl on 2017/9/14.
 */
@Module
public class BeanModule {


    @Provides
    public BeanNeedParam providerBeanZ() {
        BeanNeedParam bean = new BeanNeedParam("BeanWithParam");
        return bean;

    }

    @Named("TypeA")
    @Provides
    public BeanNeedParam providerBean() {
        BeanNeedParam bean = new BeanNeedParam("这是AAA");
        return bean;

    }

    @Named("TypeB")
    @Provides
    public BeanNeedParam providerBeanB() {
        return new BeanNeedParam("这是BBB");
    }

    //不同构造方法的注入
    @Named("TypeString")
    @Provides
    public BeanNeedParam providerBeanString() {
        BeanNeedParam bean = new BeanNeedParam("这是providerBeanString");
        return bean;

    }

    //不同构造方法的注入
    @Named("TypeInt")
    @Provides
    public BeanNeedParam providerBeanInt() {
        BeanNeedParam bean = new BeanNeedParam(33);
        return bean;

    }

    // 对于SelfType为1的变量，使用"A"作为构造方法参数
    @SelfType(1)
    @Provides
    public BeanNeedParam prividerBeanWithTypeAnnotationA() {
        return new BeanNeedParam("A");
    }


    // 对于SelfType为2的变量，使用"B"作为构造方法参数
    @SelfType(2)
    @Provides
    public BeanNeedParam prividerBeanWithTypeAnnotationB() {
        return new BeanNeedParam("B");
    }

    @Provides
    public String providerString(){
        return "param from other function";
    }
}
