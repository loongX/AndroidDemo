package com.slzr.dagger2t.app.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.slzr.dagger2t.app.DaggerApplication;

import com.slzr.dagger2t.R;
import com.slzr.dagger2t.app.ApplicationBean;
import com.slzr.dagger2t.app.ApplicationComponent;


import javax.inject.Inject;


public class MainActivityApp extends AppCompatActivity {
    @Inject
    ApplicationBean applicationBean1;
    @Inject
    ApplicationBean applicationBean2;
    @Inject
    ActivityBean activityBean;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerApplication application = (DaggerApplication) getApplication();
        ApplicationComponent applicationComponent = application.getAppComponent();
        ActivityComponent activityComponent = DaggerActivityComponent.builder().applicationComponent(applicationComponent).build();
        activityComponent.inject(this);
        Log.d("Dagger", "Activity activityBean:" + activityBean);
        Log.d("Dagger", "Activity applicationBean1:" + applicationBean1);
        Log.d("Dagger", "Activity applicationBean2:" + applicationBean2);
        OtherClass otherClass = new OtherClass();
    }


    class OtherClass {
        @Inject
        ApplicationBean applicationBean1;
        @Inject
        ApplicationBean applicationBean2;
        @Inject
        ActivityBean activityBean;


        public OtherClass() {
            DaggerApplication application = (DaggerApplication) getApplication();
            ApplicationComponent applicationComponent = application.getAppComponent();
            ActivityComponent activityComponent = DaggerActivityComponent.builder().applicationComponent(applicationComponent).build();
            activityComponent.inject(this);
            Log.d("Dagger", "OtherClass activityBean:" + this.activityBean);
            Log.d("Dagger", "OtherClass applicationBean1:" + this.applicationBean1);
            Log.d("Dagger", "OtherClass applicationBean2:" + this.applicationBean2);
        }
    }
}
