package com.okloong.door;

import android.app.Application;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by loongago on 2018-03-08.
 */

public class BaseApplication extends Application {
//    public static Typeface typefaceStHeiTi;

    @Override
    public void onCreate() {
        super.onCreate();
//        typefaceStHeiTi = Typeface.createFromAsset(getAssets(), "fonts/stheitisc.ttf");
//
//        try {
//            Field field = Typeface.class.getDeclaredField("MONOSPACE");
//            field.setAccessible(true);
//            field.set(null, typefaceStHeiTi);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }
}
