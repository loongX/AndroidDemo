package com.rdm.common.util;

import com.rdm.base.app.BaseApp;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by lokierao on 2015/1/26.
 */
public class TimeUtils {

    public static final long second = 1000; // a second
    public  static final long minute = second * 60; // a minute
    public static final long hour = minute * 60; // an hour
    public static final long day = hour * 24; // a day
    public static final long week = day * 7; // a week


    /**
     * 返回实时时间。不是手机系统里的时间（手机系统时间会跟实时时间不一样）
     * @return
     */
    public static long getRealTime(){
        return BaseApp.get().getRealTime();
    }

    /**
     * 返回当前网络实时日期
     * @return
     */
    public static Date getRealDate(){
        return new Date(getRealTime());
    }

    /**
     * 读取服务器的时间。耗时操作。
     * @return
     */
    public static Date readServerTime() {
        try {
            URL url = new URL("http://www.baidu.com");// 取得资源对象
            URLConnection uc;
            uc = url.openConnection();
            uc.connect(); // 发出连接
            long ld = uc.getDate();
            Date date = new Date(ld); // 转换为标准时间对象
            return date;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 生成连接对象

        return null;
    }

    public static String getDurationTime(long timestampMillsec) {
        return getDurationTime(timestampMillsec,"yyyy-MM-dd HH:mm:ss");
    }

        /**
         *  返回持续时间。
         * @param timestampMillsec
         * @param pattern  "yyyy-MM-dd HH:mm:ss"
         * @return
         */
    public static String getDurationTime(long timestampMillsec, String pattern) {
        long delta = getRealTime() - timestampMillsec;


        if (delta < 0) {
            String easyReadTime = chinaFormatTime(timestampMillsec,pattern);
            return easyReadTime;
        } else if (delta < minute) {
            return "刚刚";
        } else if (delta < hour) {
            return (delta / minute) + "分钟前";
        } else if (delta < day) {
            return (delta / hour) + "小时前";
        } else if (delta < week) {
            return (delta / day) + "天前";
        } else {
            String easyReadTime = chinaFormatTime(timestampMillsec,pattern);
            return easyReadTime;
        }
    }

    private static String chinaFormatTime(long timestampMillsec,String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String easyReadTime = dateFormat.format(new Date(timestampMillsec));
        return easyReadTime;
    }
}
