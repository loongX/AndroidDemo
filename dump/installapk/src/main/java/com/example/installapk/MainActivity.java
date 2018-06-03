package com.example.installapk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    Button btInstallUI;
    Button btInstallAccSet;
    Button btInstallAcc;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btInstallUI = (Button) findViewById(R.id.bt_install_UI);
        btInstallAccSet = (Button) findViewById(R.id.bt_install_accessibility_set);
        btInstallAcc = (Button) findViewById(R.id.bt_install_accessibility);
        btInstallUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                installAPK();
            }
        });
        btInstallAccSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAccessibility();
            }
        });
        btInstallAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smartInstall();
            }
        });
        context = this;
    }

    private void installAPK(){
        String str = "/installapk-debug.apk";
        String fileName = Environment.getExternalStorageDirectory() + str;
        File installFile = new File(fileName);
        if (!installFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(installFile), "application/vnd.android.package-archive");
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void setAccessibility(){
        //跳转到开启无障碍服务的界面
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        context.startActivity(intent);
    }
    public void smartInstall() {
        String str = "/key-debug.apk";
        String fileName = Environment.getExternalStorageDirectory() + str;
        File installFile = new File(fileName);
        if (!installFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(installFile), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
