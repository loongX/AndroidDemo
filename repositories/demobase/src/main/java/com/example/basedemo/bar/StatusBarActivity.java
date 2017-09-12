package com.example.basedemo.bar;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.basedemo.R;

/**
 * Created by pxl on 2017/9/12.
 * 隐藏了状态栏和标题栏
 */
/*通过配置文件实现

在Android4.4之后提供了可以修改状态栏的属性接口后,我们可以直接通过style文件来配置状态栏, 但是需要注意的是,
为了兼容4.4以下的版本, 所以必须在配置多个API级别的values文件. 这里直接借参考文章中的图:
values-v19 values-v21 styles.xml
最基本的在4.4版本之下会加载默认的values/styles.xml, 如果在4.4版本会加载 values-v19/styles.xml,
 5.0以上加载 values-v21/styles.xml文件. 具体配置如下:

values/styles.xml
<style name="ImageTranslucentTheme" parent="AppTheme">
    <!--在Android 4.4之前的版本上运行，直接跟随系统主题-->
</style>

values-v19/styles.xml
<style name="ImageTranslucentTheme" parent="Theme.AppCompat.Light.DarkActionBar">
    <item name="android:windowTranslucentStatus">true</item>
    <item name="android:windowTranslucentNavigation">true</item>
</style>

values-v21/styles.xml
<style name="ImageTranslucentTheme" parent="Theme.AppCompat.Light.DarkActionBar">
    <item name="android:windowTranslucentStatus">false</item>
    <item name="android:windowTranslucentNavigation">true</item>
    <!--Android 5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色-->
    <item name="android:statusBarColor">@android:color/transparent</item>
</style>
当状态栏需要设置一个单纯的颜色时：

4.4：设置android：windowTranslucateStatus属性，并且手动添加一个和状态栏高度等高的View。
5.0及以上：不设置android:windowTranslucateStatus属性，直接设置statusBarColor的色值。
当状态栏需要设置一个图片

4.4：直接设置android：windowTranslucateStatus属性。
5.0：不设置，同时设置状态栏颜色透明，并使用另一个属性。

 */
public class StatusBarActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOrientation();
        setContentView(R.layout.activity_statusbar);

        chooseBarStatus();

    }

    private void setOrientation() {
        //Configuration.ORIENTATION_PORTRAIT
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void chooseBarStatus() {
        switch (Bar.getStatus()) {
            case hide_StatusBar:
                hideStatusBar();
                break;
            case transparent_StatusBar:
                transparentStatusBar();
//                setStatusBarUpperAPI19();
//                setStatusBarUpperAPI();
                break;
            case hide_StatusBar_A_NavigationBar:
                hideStatusBarANavigationBar();
                break;
            case transparent_StatusBar_A_NavigationBar:
                transparentStatusBarANavigationBar();
                break;
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


    /**
     * 透明状态栏
     */
    private void transparentStatusBar() {
        //5.0 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//19版本 4.4 - 5.0版本
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View statusBarView = mContentView.getChildAt(0);
            //移除假的 View
            if (statusBarView != null && statusBarView.getLayoutParams() != null &&
                    statusBarView.getLayoutParams().height == getStatusBarHeight()) {
                mContentView.removeView(statusBarView);
            }
            //不预留空间
            if (mContentView.getChildAt(0) != null) {
                ViewCompat.setFitsSystemWindows(mContentView.getChildAt(0), false);
            }
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    /**
     * 设置透明状态栏,方法2
     * 状态栏显示, 状态栏背景透明
     */
    private void setStatusBarUpperAPI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//21版本 5.0版本以上

            Window window = getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//          如果不设置setStatusBarColor会跟随actionBar的颜色，并形成层次感
//            window.setStatusBarColor(getResources().getColor(R.color.cyan));
//            window.setStatusBarColor(Color.TRANSPARENT);
            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, false);
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//19版本 4.4 - 5.0版本

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View statusBarView = mContentView.getChildAt(0);
            //移除假的 View
            if (statusBarView != null && statusBarView.getLayoutParams() != null &&
                    statusBarView.getLayoutParams().height == getStatusBarHeight()) {
                mContentView.removeView(statusBarView);
            }
            //不预留空间
            if (mContentView.getChildAt(0) != null) {
                ViewCompat.setFitsSystemWindows(mContentView.getChildAt(0), false);
            }
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    //状态栏显示,状态栏的背景颜色需要设置
    private void setStatusBarUpperAPI21() {
        Window window = getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        //由于setStatusBarColor()这个API最低版本支持21, 本人的是15,所以如果要设置颜色,自行到style中通过配置文件设置
//        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
            ViewCompat.setFitsSystemWindows(mChildView, true);
        }
    }

    //状态栏显示,状态栏的背景颜色需要设置
    private void setStatusBarUpperAPI19() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        int statusBarHeight = getStatusBarHeight();
        int statusColor = getResources().getColor(R.color.colorPrimary);

        View mTopView = mContentView.getChildAt(0);
        if (mTopView != null && mTopView.getLayoutParams() != null &&
                mTopView.getLayoutParams().height == statusBarHeight) {
            //避免重复添加 View
            mTopView.setBackgroundColor(statusColor);
            return;
        }
        //使 ChildView 预留空间
        if (mTopView != null) {
            ViewCompat.setFitsSystemWindows(mTopView, true);
        }

        //添加假 View
        mTopView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        mTopView.setBackgroundColor(statusColor);
        mContentView.addView(mTopView, 0, lp);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = getResources().getDimensionPixelSize(resId);
        }
        return result;
    }

    /**
     * 隐藏导航栏和状态栏
     */
    private void hideStatusBarANavigationBar() {
        /*
        if(Build.VERSION.SDK_INT<16){
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }*/
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    /**
     * 透明状态栏和导航栏
     */
    private void transparentStatusBarANavigationBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    /*

//
//      用于控制NavigationBar的隐藏和显示
//     你用的是Neux手机, 就会发现手机下面还有NavigationBar, 就是退出,Home,程序 3个按键的软键盘. 在全屏模式中,
是无法隐藏掉NavigationBar的. 下面的代码提供隐藏的方法, 可讲代码加入到全屏模式的Activity中:
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void hideSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
     */

}
