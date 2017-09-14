package com.slzr.dagger2t;


import javax.inject.Inject;

/**
 * Created by pxl on 2017/9/14.
 */

public class BeanNeedParam {
    private String mName = null;

    private int age;

    public BeanNeedParam(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public BeanNeedParam(String name) {
        this.mName = name;
    }


    public String getName() {
        return mName;
    }
}
