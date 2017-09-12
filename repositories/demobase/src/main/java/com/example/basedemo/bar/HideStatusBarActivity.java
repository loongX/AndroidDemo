package com.example.basedemo.bar;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.basedemo.R;

/**
 * Created by pxl on 2017/9/12.
 * 隐藏了状态栏和标题栏
 */

public class HideStatusBarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOrientation();
        setContentView(R.layout.activity_statusbar);

        hideStatusBar();
    }

    private void setOrientation() {
        //Configuration.ORIENTATION_PORTRAIT
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 隐藏状态栏和标题栏，隐藏状态栏和ActionBar的方式在4.1系统之上和4.1系统之下还是不一样的
     * 这里我就不准备考虑4.1系统之下的兼容性了
     */
    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

}
