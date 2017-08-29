package com.rdm.common.util;

import com.rdm.base.app.BaseApp;

/**
 * Created by lokierao on 2015/1/12.
 */
public class NetWorkUtils {


    public static boolean isNetworkAvaliable(){
        BaseApp.NetworkStatus staus = BaseApp.get().getNetworkStatus();
        return staus != BaseApp.NetworkStatus.NotReachable && staus != BaseApp.NetworkStatus.Unkonw;
    }
}
