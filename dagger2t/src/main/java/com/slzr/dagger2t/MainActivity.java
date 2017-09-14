package com.slzr.dagger2t;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;


public class MainActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();
    @Inject
    BeanForDagger mBeanForDagger;


    @Inject
    BeanNeedParam mBeanNeedParam;

    @Named("TypeA")
    @Inject
    BeanNeedParam mBeanNeedParamA;

    @Named("TypeB")
    @Inject
    BeanNeedParam mBeanNeedParamB;

    //不同构造方法的注入
    @Named("TypeString")
    @Inject
    BeanNeedParam mBeanNeedParamString;


    //不同构造方法的注入
    @Named("TypeInt")
    @Inject
    BeanNeedParam mBeanNeedParamInt;

    @SelfType(1)
    @Inject
    BeanNeedParam mBeanNeedParamSelfTypeA;


    @SelfType(2)
    @Inject
    BeanNeedParam mBeanNeedParamSelfTypeB;

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

    //需要先编译一下，才有DaggerBeanComponent
    private void testDagger() {
        // 触发Dagger机制
        DaggerBeanComponent.create().inject(this);
        if (mBeanForDagger != null) {
            Log.d(TAG, "使用Dagger注入变量，mBeanForDagger Name：" + mBeanForDagger.getName());
        }
        if (mBeanNeedParam != null) {
            Log.d(TAG, "使用Dagger注入变量，mBeanNeedParam Name：" + mBeanNeedParam.getName());
        }

        if (mBeanNeedParamA != null) {
            Log.d(TAG, "使用Dagger注入变量，mBeanNeedParamA Name：" + mBeanNeedParamA.getName());
        }
        if (mBeanNeedParamB != null) {
            Log.d(TAG, "使用Dagger注入变量，mBeanNeedParamB Name：" + mBeanNeedParamB.getName());
        }

        //测试不同构造方法的情况
        if (mBeanNeedParamString != null) {
            Log.d(TAG, "mBeanNeedParamString Name:" + mBeanNeedParamString.getName() + ",Number:" + mBeanNeedParamString.getAge());
        }
        if (mBeanNeedParamInt != null) {
            Log.d(TAG, "mBeanNeedParamInt Name:" + mBeanNeedParamInt.getName() + ",Number:" + mBeanNeedParamInt.getAge());
        }

        //测试不同构造方法的情况
        if (mBeanNeedParamSelfTypeA != null) {
            Log.d(TAG, "mBeanNeedParamSelfType Name:" + mBeanNeedParamSelfTypeA.getName() + ",Number:" + mBeanNeedParamSelfTypeA.getAge());
        }
        if (mBeanNeedParamSelfTypeB != null) {
            Log.d(TAG, "mBeanNeedParamSelfType Name:" + mBeanNeedParamSelfTypeB.getName() + ",Number:" + mBeanNeedParamSelfTypeB.getAge());
        }
    }
}
