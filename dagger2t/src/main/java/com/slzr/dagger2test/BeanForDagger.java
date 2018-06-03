package com.slzr.dagger2test;

import javax.inject.Inject;

/**
 * Created by pxl on 2017/9/14.
 */

public class BeanForDagger {
    private String mName = null;



    public BeanForDagger() {
        this.mName = "Dagger方式";
    }

    @Inject
    public BeanForDagger(ParamForDagger param) {
        this.mName = param.getName();
    }


    public String getName() {
        return mName;
    }
}
