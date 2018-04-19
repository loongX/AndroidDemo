package com.rdm.common.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

public class ResourcesUtils {

    /**
     * 获取Dimen中的大小
     */
    public static int getDimens(Context context, int resid) {
        return context.getResources().getDimensionPixelSize(resid);
    }

    public static String getString(Context context, int resid) {
        return context.getResources().getString(resid);
    }

    public static Drawable getDrawable(Context context, int resid) {
        return context.getResources().getDrawable(resid);
    }

    public static int getColor(Context context, int resid) {
        return context.getResources().getColor(resid);
    }

    public static ColorStateList getColorStateList(Context context, int resid) {
        return context.getResources().getColorStateList(resid);
    }
}
