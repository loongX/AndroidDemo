package com.example.ilog;

import com.example.ilog.base.ILog;
import com.example.ilog.comment.SdcardUtils;




import java.io.File;

/**
 * Created by Rao on 2016/5/15.
 */
public class FileConstans {

    private static final String DIR_BASE_PATH = "AAAAAAAAAAAAAAA";

    public static final String DIR_SESSION_DIR_NAME= "session";
    public static final String DIR_DOWNLOAD_DIR_NAME= "download";
    public static final String DIR_LOGGER_DIR_NAME= "log";


    public FileConstans(){

    }

    private static File baseFile = null;


    public File getBaseDir() {
        return getBaseDir_();
    }


    public File getSessionDirecotry() {
        return new File(getBaseDir(),DIR_SESSION_DIR_NAME);
    }


    /***
     * 返回程序的基本目录
     * @return
     */
    public static File getBaseDir_(){
        if(baseFile == null) {
            baseFile = new File(SdcardUtils.getSDFile(),DIR_BASE_PATH);
            if(!baseFile.exists()) {
                if(!baseFile.mkdirs()){
                    if(!baseFile.exists()) {
                        ILog.e(FileConstans.class, "创建base文件夹失败");
                        throw new RuntimeException("create base dir error!!!");
                    }
                }
            }
        }
        return baseFile;
    }

    /**
     * 创建或者返回当前程序目录下面的指定目录。
     * @param name
     * @return
     */
    public static File getOrCreateDirectory(String name){
        File dir = new File(getBaseDir_(),name);
        if(!dir.exists()) {
            if(!dir.mkdirs()){
                ILog.e(FileConstans.class,"创建base文件夹失败");

                throw  new RuntimeException("create base dir error!!!");
            }
        }
        return dir;
    }
}
