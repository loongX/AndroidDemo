package com.slzr.dagger2t;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();
    @Inject
    BeanForDagger mBeanForDagger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testOrignal();
        testDagger();
    }

    private void testOrignal() {
        Bean bean = new Bean();
        Log.d(TAG, "不使用Dagger时 Name：" + bean.getName());
    }

    private void testDagger() {
        // 触发Dagger机制
        DaggerBeanComponent.create().inject(this);
        if (mBeanForDagger != null) {
            Log.d(TAG, "使用Dagger注入变量，mBeanForDagger Name：" + mBeanForDagger.getName());
        }
    }
}
