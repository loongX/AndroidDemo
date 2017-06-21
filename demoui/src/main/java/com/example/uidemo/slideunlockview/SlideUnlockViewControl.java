package com.example.uidemo.slideunlockview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.uidemo.R;
import com.example.uidemo.slideunlockview.view.SlideUnlockView;


public class SlideUnlockViewControl extends AppCompatActivity {
    private SlideUnlockView slideUnlockView;
    private ImageView imageView;
    private Vibrator vibrator;
    private ScreenOnOffReceiver receiver;

    public static void launch(Context context){
        Intent intent  = new Intent(context,SlideUnlockViewControl.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_slideunlockview);
        // 注册屏幕锁屏的广播
        registScreenOffReceiver();
        // 获取系统振动器服务
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // 初始化控件
        imageView = (ImageView) findViewById(R.id.imageView);
        slideUnlockView = (SlideUnlockView) findViewById(R.id.slideUnlockView);

        // 设置滑动解锁-解锁的监听
        slideUnlockView.setOnUnLockListener(new SlideUnlockView.OnUnLockListener() {
            @Override
            public void setUnLocked(boolean unLock) {
                // 如果是true，证明解锁
                if (unLock) {
                    // 启动震动器 100ms
                    vibrator.vibrate(100);
                    // 当解锁的时候，执行逻辑操作，在这里仅仅是将图片进行展示
                    imageView.setVisibility(View.VISIBLE);
                    // 重置一下滑动解锁的控件
                    slideUnlockView.reset();
                    // 让滑动解锁控件消失
                    slideUnlockView.setVisibility(View.GONE);
                }
            }
        });

    }

    /**
     * 注册一个屏幕锁屏的广播
     */
    private void registScreenOffReceiver() {
        // TODO Auto-generated method stub
        receiver = new ScreenOnOffReceiver();
        // 创建一个意图过滤器
        IntentFilter filter = new IntentFilter();
        // 添加屏幕锁屏的广播
        filter.addAction("android.intent.action.SCREEN_OFF");
        // 在代码里边来注册广播
        this.registerReceiver(receiver, filter);

    }

    class ScreenOnOffReceiver extends BroadcastReceiver {

        private static final String TAG = "ScreenOnOffReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 关屏的操作
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                // 当手机关屏时，我们同时也锁屏
                slideUnlockView.setVisibility(View.VISIBLE);
                // 设置图片消失
                imageView.setVisibility(View.GONE);
            }
        }
    }
}
