package com.example.uidemo;

import com.example.uidemo.activitydialog.DialogControl;
import com.example.uidemo.beziercurve.Beziercurve;
import com.example.uidemo.listviewfrash.ReFlashListViewMainActivity;
import com.example.uidemo.slideunlockview.SlideUnlockViewControl;

/**
 * Created by loongago on 2017-06-21.
 */

public class CategoryConstant1 {

    public   Class[] CLAZZES = new Class[]{
            DialogControl.class,//1
            SlideUnlockViewControl.class,//2
            ReFlashListViewMainActivity.class,//3
            Beziercurve.class,//4


    };

    public String[] DESCRIBE = new String[] {
            "activitydialog",//1
            "滑动解锁",//2
            "listview下拉刷新",//3
            "贝塞尔曲线",//4


    };
}
