package com.example.basedemo.encrypt;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.basedemo.R;

import java.security.MessageDigest;

public class EncryptActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        testMD5();
    }

    // test
    public  void testMD5() {
        String str = getMD5Code("shanghai");
        str +=  "\n" + getMD5Code("beijing");
        str += "\n" + getMD5Code("GUANZHOU");
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
}
