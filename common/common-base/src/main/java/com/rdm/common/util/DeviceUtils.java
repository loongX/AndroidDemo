package com.rdm.common.util;


import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.rdm.base.app.BaseApp;

public class DeviceUtils {

    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
        }
        return version;
    }


    public static String getLocalMacAddress(Context context) {
        String mac =  null;
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            mac = info.getMacAddress();
        }catch (Exception ex){

        }

        return mac == null ?"":mac;
    }

    /**
     * 判断是否模屏
     *
     * @param context 上下文
     * @return 返回判断结果
     */
    public static boolean isScreenPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == 1;
    }

    /**
     * 将需要的字体大px转化为sp
     *
     * @param size
     * @return
     */
    public static float px2sp(Context context, float size) {
        final float scale = context.getResources().getDisplayMetrics().density;
        if (size <= 0) {
            size = 15;
        }
        float realSize = (float) (size * (scale - 0.1));
        return realSize;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dip2px(float dpValue) {
        return dip2px(BaseApp.get(),dpValue);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将需要的字体大sp转化为
     *
     * @param size
     * @return
     */
    public static float sp2px(Context context, float size) {
        final float scale = context.getResources().getDisplayMetrics().density;
        if (size <= 0) {
            size = 15;
        }
        float realSize = (float) (size / (scale - 0.1));
        return realSize;
    }


    /**
     * 获取屏幕密度
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取屏幕宽度
     */
    public static float getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static float getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 打开相机的闪关灯。
     * 需要在AndroidMainfest.xml注册：
     * <uses-permission android:name="android.permission.FLASHLIGHT" />
     * <uses-permission android:name="android.permission.CAMERA"/>
     * <uses-feature android:name="android.hardware.camera" />
     * <uses-feature android:name="android.hardware.autofocus"/>
     *
     * @param isOpen 是否打开。
     * @return 返回是否操作成功。
     */
    public static boolean setCameraFlashOpen(boolean isOpen) {
        try {
            Camera camera = Camera.open();
            if (isOpen) {
                Parameters parameter = camera.getParameters();
                parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameter);
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    public void onAutoFocus(boolean success, Camera camera) {
                    }
                });
                camera.startPreview();

            } else {
                Parameters parameter = camera.getParameters();
                parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameter);
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
