package com.rdm.common.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public class SdcardUtils {

    /**
     * 检测sdcard是否可用
     *
     * @return true为可用，否则为不可用
     */
    public static boolean sdCardIsAvailable() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED))
            return false;
        return true;
    }

    public static File getSDFile(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if   (sdCardExist)      //如果SD卡存在，则获取跟目录
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir;
        }
        return null;

    }



    /**
     * Checks if there is enough Space on SDCard
     *
     * @param updateSize
     *            Size to Check
     * @return True if the Update will fit on SDCard, false if not enough space on SDCard Will also return false, if the SDCard is
     *         not mounted as read/write
     */
    public static boolean enoughSpaceOnSdCard(long updateSize) {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED))
            return false;
        return (updateSize < getRealSizeOnSdcard());
    }

    /**
     * get the space is left over on sdcard
     */
    public static long getRealSizeOnSdcard() {
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * Checks if there is enough Space on phone self
     *
     */
    public static boolean enoughSpaceOnPhone(long updateSize) {
        return getRealSizeOnPhone() > updateSize;
    }

    /**
     * get the space is left over on phone self
     */
    public static long getRealSizeOnPhone() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long realSize = blockSize * availableBlocks;
        return realSize;
    }
}
