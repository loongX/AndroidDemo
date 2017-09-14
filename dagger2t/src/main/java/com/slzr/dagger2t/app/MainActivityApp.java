package com.slzr.dagger2t.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.slzr.dagger2t.Bean;
import com.slzr.dagger2t.BeanComponent;
import com.slzr.dagger2t.BeanNeedParam;
import com.slzr.dagger2t.DaggerApplication;
import com.slzr.dagger2t.DaggerBeanComponent;
import com.slzr.dagger2t.R;

import javax.inject.Inject;


public class MainActivityApp extends AppCompatActivity {
    String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
