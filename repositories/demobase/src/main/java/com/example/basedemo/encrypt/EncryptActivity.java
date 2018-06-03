package com.example.basedemo.encrypt;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.basedemo.R;

import java.security.MessageDigest;

public class EncryptActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();

    TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        testMD5();
        textView = ((TextView)findViewById(R.id.tv));

        String version = getAppVersionName(this);
        textView.append(version);



        String phoneInfo = "\nProduct: " + android.os.Build.PRODUCT;
        phoneInfo += "\n CPU_ABI: " + android.os.Build.CPU_ABI;
        phoneInfo += "\n TAGS: " + android.os.Build.TAGS;
        phoneInfo += "\n VERSION_CODES.BASE: "
                + android.os.Build.VERSION_CODES.BASE;
        phoneInfo += "\n MODEL: " + android.os.Build.MODEL;
        phoneInfo += "\n SDK: " + android.os.Build.VERSION.SDK;//old
        phoneInfo += "\n currentAPIVersion: " + android.os.Build.VERSION.SDK_INT;
        phoneInfo += "\n VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE;
        phoneInfo += "\n DEVICE: " + android.os.Build.DEVICE;
        phoneInfo += "\n DISPLAY: " + android.os.Build.DISPLAY;
        phoneInfo += "\n BRAND: " + android.os.Build.BRAND;
        phoneInfo += "\n BOARD: " + android.os.Build.BOARD;
        phoneInfo += "\n FINGERPRINT: " + android.os.Build.FINGERPRINT;
        phoneInfo += "\n ID: " + android.os.Build.ID;
        phoneInfo += "\n MANUFACTURER: " + android.os.Build.MANUFACTURER;
        phoneInfo += "\n USER: " + android.os.Build.USER;
        Log.i("build", phoneInfo);
        textView.append(phoneInfo);
    }

    // test
    public  void testMD5() {
        String str = getMD5Code("shanghai");
        str +=  "\n" + getMD5Code("beijing");
        str += "\n" + getMD5Code("GUANZHOU");
        str += "\n" + getMD5Code("dfdfdfdfdfffdf[B@351bc694");
        str += "\n" + getMD5Code("czdfecdsfeererc[B@36c236a0");
        Log.i(TAG, str);
        ((TextView)findViewById(R.id.tv)).setText(str);
    }

//Java实现MD5摘要算法
    // md5加密
    public static String getMD5Code(String message) {
        String md5Str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(message.getBytes());
            md5Str = bytes2Hex(md5Bytes);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return md5Str;
    }

    // 2进制转16进制
    public static String bytes2Hex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        int temp;
        try {
            for (int i = 0; i < bytes.length; i++) {
                temp = bytes[i];
                if(temp < 0) {
                    temp += 256;
                }
                if (temp < 16) {
                    result.append("0");
                }
                result.append(Integer.toHexString(temp));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versioncode = 0;

        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return "\nversionName:" + versionName + "\nversioncode:" + versioncode ;
    }


}
