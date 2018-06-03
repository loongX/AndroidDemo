package com.slzr.dagger2test;

import javax.inject.Inject;

/**
 * Created by pxl on 2017/9/14.
 */

public class ParamForDagger {
    private String mName = null;


    @Inject
    public ParamForDagger() {
        this.mName = "参数插入";
    }


    public String getName() {
        return mName;
    }
}
