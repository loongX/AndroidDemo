package com.example.stepstrack;

import com.rdm.base.SDKContext;

/**
 * Created by Rao on 2016/5/15.
 */
public class AppSDKContext implements SDKContext {

    private FileConstans mFileConstans = new FileConstans();
//    private DBInitSetting mDBInit = new DBInitSetting();

    public AppSDKContext() {
    }

    @Override
    public Files getFiles() {
        return mFileConstans;
    }

    @Override
    public DBInit getDBInit() {
//        return mDBInit;
        return null;
    }
}
