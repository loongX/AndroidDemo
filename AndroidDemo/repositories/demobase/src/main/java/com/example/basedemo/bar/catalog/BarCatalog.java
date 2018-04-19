package com.example.basedemo.bar.catalog;


import com.example.basedemo.bar.StatusBarActivity;
import com.example.basedemo.bar.ImmersionStatusBarActivity;



/**
 * Created by loongago on 2017-06-21.
 */

public class BarCatalog {

    public Class[] CLAZZES = new Class[]{
            ImmersionStatusBarActivity.class,//1
            StatusBarActivity.class,//2

            StatusBarActivity.class,//4
            StatusBarActivity.class,//5
            StatusBarActivity.class,//6

    };

    public String[] DESCRIBE = new String[] {
            "沉浸栏",//1
            "隐藏状态栏",//2

            "透明状态栏",//4
            "隐藏状态栏和导航栏",//5
            "透明状态栏和导航栏",//6

    };
}
