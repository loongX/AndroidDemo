package com.rdm.common.util;


import android.app.Activity;
import android.content.ContentResolver;
import android.provider.Settings;
import android.provider.Settings.System;
import android.view.WindowManager;

public class LightnessUtils {

    /**
     * 设置app的亮度值
     *
     * @param brightness 0 - 255之间。
     */
    public static void setAppBrightness(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        context.getWindow().setAttributes(lp);
    }

    /**
     * 返回app的亮度值。
     *
     * @param context
     * @return 0 - 255 之间
     */
    public static int getAppBrightness(Activity context) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        return (int) (lp.screenBrightness * 255);
    }

    public static boolean isAutoBrightness(Activity act) {
        boolean automicBrightness = false;
        ContentResolver aContentResolver = act.getContentResolver();
        try {
            automicBrightness = Settings.System.getInt(aContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Exception e) {
            // Toast.makeText(act, "�޷���ȡ����", Toast.LENGTH_SHORT).show();
        }
        return automicBrightness;
    }

    public static void SetLightness(Activity act, int value) {
        try {
            System.putInt(act.getContentResolver(), System.SCREEN_BRIGHTNESS, value);
            WindowManager.LayoutParams lp = act.getWindow().getAttributes();
            lp.screenBrightness = (value <= 0 ? 1 : value) / 255f;
            act.getWindow().setAttributes(lp);
        } catch (Exception e) {
            // Toast.makeText(act, "�޷��ı�����", Toast.LENGTH_SHORT).show();
        }
    }

    public static int getLightness(Activity act) {
        return System.getInt(act.getContentResolver(), System.SCREEN_BRIGHTNESS, -1);
    }

    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public static void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
}
