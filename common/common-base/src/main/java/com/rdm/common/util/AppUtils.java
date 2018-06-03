package com.rdm.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.rdm.base.app.BaseApp;
import com.rdm.common.ILog;

public class AppUtils {

    private static final String TAG = "AppUtils";

    public static int getVersionCode() {
        Context ctx = BaseApp.get();
        int versionCode = 0;

        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            ILog.e(TAG, "getVersion code error:" + e.getMessage());
            //e.printStackTrace();
        }

        return versionCode;
    }

    public static String getVersionName() {
        String versionName = "1.0";
        Context context = BaseApp.get();

        PackageManager manager = context.getPackageManager();
        if (manager != null) {
            try {
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                if (info != null) {
                    versionName = info.versionName;
                }
            } catch (Exception e) {
                ILog.printStackTrace(e);
            }
        }

        return versionName;
    }

    public static String getPackageName(Context ctx) {
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return pInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            ILog.e(TAG, "getVersion code error:" + e.getMessage());
            //e.printStackTrace();
        }

        return null;
    }
}