package com.slzr.dagger2test.app.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.slzr.dagger2test.app.DaggerApplication;

import com.slzr.dagger2test.R;
import com.slzr.dagger2test.app.ApplicationBean;
import com.slzr.dagger2test.app.ApplicationComponent;


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

        byte[] par2 = {0x11};
        String  b = "1273705zffffffff";
        byte[] a = b.getBytes();
        byte[] z = {0x12,0x34,0x73,0x70,0x50, (byte) 0xff, (byte) 0xff};
        byte[] y = hexStringToBytes(b);
        int i = b.length();
        String zb =  addEndForStr(b, 26, "a");
         zb = zb.substring(0, 16);
        byte[] bcd = StrToBCD("123");
        Log.e("test", "test" + a + z + y + b + zb + bcd );
    }

    public static String addEndForStr(String str, int strLength, String endStr) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
//            sb.append("0").append(str);// 左补
             sb.append(str).append(endStr);//右补
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 转成BCD，如输入1234，输出是0x12 0x34
     * @param hexString
     * @return
     */
    public static byte[] StrToBCD(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToBCD(hexChars[pos]) << 4 | charToBCD(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToBCD(char c) {
        return (byte) "0123456789".indexOf(c);
    }

    /**
     * bytes字符串转换为Byte值
     * @param  src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src)
    {
        int m=0,n=0;
        int l=src.length()/2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++)
        {
            m=i*2+1;
            n=m+1;
            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));
        }
        return ret;
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
