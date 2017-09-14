package com.slzr.dagger2t.app;

/**
 * Created by pxl on 2017/9/14.
 */

public class ApplicationBean {
    private String name = null;


    public ApplicationBean() {
        name = "AppBean";
    }


    public String getAppBeanName() {
        return name;
    }
}
