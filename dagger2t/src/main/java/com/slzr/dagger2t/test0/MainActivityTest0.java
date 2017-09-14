package com.slzr.dagger2t.test0;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.slzr.dagger2t.R;

public class MainActivityTest0 extends AppCompatActivity {
    String TAG = getClass().getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testOrignal();
        testDagger2();
    }

    private void testOrignal() {
        Bean bean = new Bean();
        Log.d(TAG, "不使用Dagger时 Name：" + bean.getName());
    }



    //需要先编译一下，才有DaggerBeanComponent
    private void testDagger2() {
        // 触发Dagger机制
//        DaggerBeanComponent.create().inject(this);
        //测试注入对象的地址


    }


}
