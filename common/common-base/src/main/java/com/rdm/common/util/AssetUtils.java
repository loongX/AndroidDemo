package com.rdm.common.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;


/**
 *
 * @author lokierao
 *
 */
//TODO 清理过期版本的拷贝文件。
public class AssetUtils {


    /**
     * 打开assert下的数据库文件。
     * （将会事先拷贝到程序目录下面）
     * @param context
     * @param assertPath
     * @return
     */
    public static SQLiteDatabase open(Context context, String assertPath, boolean alwaysCopy) {

        File tagetFile = openFile(context, assertPath, alwaysCopy);

        return SQLiteDatabase.openOrCreateDatabase(tagetFile, null);

    }

    /**
     * 打开指定Assert目录下的文件。（将会拷贝到程序目录。）
     * @param context
     * @param assertPath
     * @param alwaysCopy
     * @return
     */
    public static File openFile(Context context, String assertPath, boolean alwaysCopy) {

        PackageInfo packInfo = null;
        try {
            packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e1) {
            throw new RuntimeException(e1);
        }
        int version_code = packInfo.versionCode;

        String fileTagetName = assertPath.replaceAll("/", ".");
        File tagetFile = new File(context.getFilesDir(), fileTagetName + "-" + version_code);
        if (alwaysCopy || !tagetFile.exists()) {
            FileOutputStream out = null;
            InputStream in = null;
            try {
                out = new FileOutputStream(tagetFile);
                in = context.getAssets().open(assertPath);

                IOUtils.copy(in, out);
                in.close();
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(in);
            }
        }

        return tagetFile;

    }
}
