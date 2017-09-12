package com.example.basedemo.bar;

/**
 * Created by pxl on 2017/9/12.
 */

public enum Bar {
    hide_StatusBar,
    transparent_StatusBar,
    hide_StatusBar_A_NavigationBar,
    transparent_StatusBar_A_NavigationBar;

    static public Bar status = hide_StatusBar;

    public static void setStatus(Bar status) {
        Bar.status = status;
    }

    public static Bar getStatus() {
        return status;
    }
}
