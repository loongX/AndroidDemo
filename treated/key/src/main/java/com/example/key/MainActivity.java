package com.example.key;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private Button btInstall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        btInstall = (Button) findViewById(R.id.bt_install);
        btInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                installAPK();
            }
        });
    }
    int count = -1;
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_DOWN:

                tv.setText("-----------------"+count);
                count--;

                return true;
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                    this.exitApp();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                tv.setText("++++++++++++++++"+ count);
                count++;
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出程序
     */
    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tv.setText("back!!!!");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }



    private void installAPK(){
        String str = "/key-debug.apk";
        String fileName = Environment.getExternalStorageDirectory() + str;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
