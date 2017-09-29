package com.example.uidemo;

import com.example.uidemo.Animation.combination.TitleBar;
import com.example.uidemo.Animation.combination.TitleBarActivity;
import com.example.uidemo.activitydialog.DialogControl;
import com.example.uidemo.beziercurve.Beziercurve;
import com.example.uidemo.listviewfrash.ReFlashListViewMainActivity;
import com.example.uidemo.slideunlockview.SlideUnlockViewControl;
import com.example.uidemo.springanimationdemo.AnimationMainActivity;

/**
 * Created by loongago on 2017-06-21.
 */

public class CategoryConstant1 {

    public   Class[] CLAZZES = new Class[]{
            DialogControl.class,//1
            SlideUnlockViewControl.class,//2
            ReFlashListViewMainActivity.class,//3
            Beziercurve.class,//4
            AnimationMainActivity.class,//5
            TitleBarActivity.class,//6


    };

    public String[] DESCRIBE = new String[] {
            "activitydialog",//1
            "滑动解锁",//2
            "listview下拉刷新",//3
            "贝塞尔曲线",//4
            "弹性动画",//5
            "组合控件",//6

    };
}
