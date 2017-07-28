package com.pxl.base.app;

import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by lokierao on 2016/7/30.
 */
public interface ActivityListener {

    public void onCreate(BaseActivity activity, HashMap<String, Object> mParams, Bundle savedInstanceState);

    void onNewIntent(BaseActivity activity, HashMap<String, Object> mParams, Intent intent);

    void onStart(BaseActivity activity, HashMap<String, Object> mParams);

    void onResume(BaseActivity activity, HashMap<String, Object> mParams);

    void onPause(BaseActivity activity, HashMap<String, Object> mParams);

    void onStop(BaseActivity activity, HashMap<String, Object> mParams);

    void onDestroy(BaseActivity activity, HashMap<String, Object> mParams);

    ActivityListener NULL = new ActivityListener() {

        @Override
        public void onCreate(BaseActivity activity, HashMap<String, Object> mParams, Bundle savedInstanceState) {

        }

        @Override
        public void onNewIntent(BaseActivity activity, HashMap<String, Object> mParams, Intent intent) {

        }

        @Override
        public void onStart(BaseActivity activity, HashMap<String, Object> mParams) {

        }

        @Override
        public void onResume(BaseActivity activity, HashMap<String, Object> mParams) {

        }

        @Override
        public void onPause(BaseActivity activity, HashMap<String, Object> mParams) {

        }

        @Override
        public void onStop(BaseActivity activity, HashMap<String, Object> mParams) {

        }

        @Override
        public void onDestroy(BaseActivity activity, HashMap<String, Object> mParams) {

        }
    };



}
