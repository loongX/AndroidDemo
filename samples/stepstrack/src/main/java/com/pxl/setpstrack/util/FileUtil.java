package com.pxl.setpstrack.util;

import android.os.storage.StorageManager;

//import com.slzr.app.FileConstans;
//import com.slzr.common.ILog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 */

public class FileUtil {

    private static String TAG = "FileUtil";

    /**
     * 读取文件
     * @param strFilePath
     * @return
     */
    public static String readTxtFile(String strFilePath) {
        String path = strFilePath;

        StringBuilder builder = new StringBuilder();
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
//            ILog.d(FileConstans.class, "The " + strFilePath + " File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
//                ILog.d(FileConstans.class, "The " + strFilePath + " File doesn't not exist.");
            } catch (IOException e) {
//                ILog.d(FileConstans.class, e.getMessage());
            }
        }
        return builder.toString();
    }

    /**
     * 读取文件
     * @param file
     * @return
     */
    public static String readTxtFile(File file) {
//        String path = strFilePath;

        StringBuilder builder = new StringBuilder();
        //打开文件
//        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
//            ILog.d(FileConstans.class, "The " + file.getAbsolutePath() + " File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
//                ILog.d(FileConstans.class, "The " + file.getAbsolutePath() + " File doesn't not exist.");
            } catch (IOException e) {
//                ILog.d(FileConstans.class, e.getMessage());
            }
        }
        return builder.toString();
    }

    /**
     * 读取sd卡上指定后缀的所有文件
     * @param files 返回的所有文件
     * @param filePath 路径(可传入sd卡路径)
     * @param suffere 后缀名称 比如 .gif
     * @return
     */
    public static List<File> getSuffixFile(List<File> files, String filePath, String suffere) {

        File f = new File(filePath);

        if (!f.exists()) {
            return null;
        }

        File[] subFiles = f.listFiles();
        for (File subFile : subFiles) {
            if(subFile.isFile() && subFile.getName().endsWith(suffere)){
                files.add(subFile);
            } else if(subFile.isDirectory()){
                getSuffixFile(files, subFile.getAbsolutePath(), suffere);
            } else{
                //非指定目录文件 不做处理
            }

        }

        return files;
    }

    /**
     * 读取sd卡上指定后缀的所有文件,按照文件层级来获取，一般获取到1层就够了
     * @param files 返回的所有文件
     * @param filePath 路径(可传入sd卡路径)
     * @param suffere 后缀名称 比如 .gif
     * @param dirLayer 文件夹层级，比如1表示当前文件夹
     * @return
     */
    public static List<File> getSuffixFile(List<File> files, String filePath, String suffere, int dirLayer) {
        if (dirLayer < 1) {
            return files;
        } else {
            dirLayer--;
        }

        File f = new File(filePath);

        if (!f.exists()) {
            return null;
        }
//        ILog.i(TAG, "parent file: " + f.getAbsolutePath() + " f.listFiles():" + f.listFiles());

        File[] subFiles = f.listFiles();
        if (subFiles == null) {
            return null;
        }
        for (File subFile : subFiles) {
            if(subFile.isFile() && subFile.getName().endsWith(suffere)){
//                ILog.i(TAG, subFile.getAbsolutePath());
                files.add(subFile);
            } else if(subFile.isDirectory()){
//                ILog.i(TAG, subFile.getAbsolutePath());
                getSuffixFile(files, subFile.getAbsolutePath(), suffere, dirLayer);
            } else{
                //非指定目录文件 不做处理
            }

        }

        return files;
    }


    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     * @param absolutePath
     * @return
     */
    public static boolean deleteFile(String absolutePath) {
        File file = new File(absolutePath);
        if (!file.exists()){
            return true;
        }
        if (file.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取盘符
     * 示例：StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
     * @param storageManager StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
     */
    public static void getDiskSymbol(StorageManager storageManager) {
        String[] result = null;

        StorageManager Manager = storageManager;
        try {
            Method method = StorageManager.class.getMethod("getVolumePaths");
            method.setAccessible(true);
            try {
                result = (String[]) method.invoke(storageManager);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < result.length; i++) {
//                ILog.i(TAG, "path----> " + result[i] + "\n");
            }
        } catch (Exception e) {
//            ILog.printStackTrace(e);
        }
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
//            ILog.e(TAG, "", e);
        }

    }

    /**
     * 获取目录下所有文件(按时间顺序)
     *
     * @param files
     * @param earlyToLate true由新到旧，数组的第0位位最新文件；false由旧到新，数组顺序反之
     * @return
     */
    public static List<File> listFileSortByModifyTime(List<File> files, boolean earlyToLate) {
        List<File> list = files;

        if (list != null && list.size() > 0) {
            if (earlyToLate) {
                Collections.sort(list, new Comparator<File>() {
                    public int compare(File file, File newFile) {
                        if (file.lastModified() < newFile.lastModified()) {
                            return 1;
                        } else if (file.lastModified() == newFile.lastModified()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
            } else {
                Collections.sort(list);//由旧到新
            }


        }
        return list;
    }


}
