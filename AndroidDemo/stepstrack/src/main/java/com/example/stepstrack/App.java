package com.example.stepstrack;

import com.pxl.common.ILog;
import com.pxl.base.app.BaseApp;

/**
 * Created by xlpan on 2017/8/26.
 */

public class App extends BaseApp{



    public App(){

        //由于log是很基本的组件，所以一开始就初始化
        ILog.setDelegate(new ILogImpl());
    }

    public static App get(){
        return (App) BaseApp.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
