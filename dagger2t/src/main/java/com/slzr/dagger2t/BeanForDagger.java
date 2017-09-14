package com.slzr.dagger2t;

import javax.inject.Inject;

/**
 * Created by pxl on 2017/9/14.
 */

public class BeanForDagger {
    private String mName = null;


    @Inject
    public BeanForDagger() {
        this.mName = "Dagger方式";
    }


    public String getName() {
        return mName;
    }
}
