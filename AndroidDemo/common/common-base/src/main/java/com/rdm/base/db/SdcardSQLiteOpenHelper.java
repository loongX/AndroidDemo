package com.rdm.base.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.rdm.common.util.FileUtil;
import com.rdm.base.db.DefaultSQLiteOpenHelper.DBHelper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hugozhong on 2014/6/26.
 */
public class SdcardSQLiteOpenHelper implements  ISdcardSQLiteOpenHelper{
    private DBHelper mOpenHelper;
    private String mDBPath;

    @Override
    public void init(Context context, String name, int version) {
        init(context, name, version, null);
    }

    @Override
    public void init(Context context, String name, int version, EntityManager.UpdateListener updateListener) {
        if (mOpenHelper == null) {
            mOpenHelper = new DBHelper(new SdcardDatabaseContext(context,mDBPath), name, null, version, updateListener);
        }
    }

    @Override
    public ISQLiteDatabase getWritableDatabase() {
        return new DefaultSQLiteDatabase(mOpenHelper.getWritableDatabase());
    }

    @Override
    public ISQLiteDatabase getReadableDatabase() {
        return new DefaultSQLiteDatabase(mOpenHelper.getReadableDatabase());
    }

    private static ConcurrentHashMap<String,SdcardSQLiteOpenHelper> sInstanceMap = new ConcurrentHashMap<String, SdcardSQLiteOpenHelper>();

    public static SdcardSQLiteOpenHelper getInstance(String dbName,String dbPath) {
        if (!TextUtils.isEmpty(dbPath)) {
            dbName = dbName+"_"+dbPath.hashCode();
        }
        SdcardSQLiteOpenHelper openHelper = sInstanceMap.get(dbName);
        if (openHelper == null) {
            synchronized (SdcardSQLiteOpenHelper.class) {
                openHelper = sInstanceMap.get(dbName);
                if (openHelper == null) {
                    openHelper = new SdcardSQLiteOpenHelper(dbPath);
                }
            }
        }
        return openHelper;
    }

    private SdcardSQLiteOpenHelper(String dbPath) {
        mDBPath = dbPath;
    }

    @Override
    public String getDatabaPath() {
        return mDBPath;
    }

    /**
     * 用于支持对存储在SD卡上的数据库的访问
     */
    public static class SdcardDatabaseContext extends ContextWrapper {

        private String mDBPath;

        public SdcardDatabaseContext(Context base,String dbPath) {
            super(base);
            this.mDBPath = dbPath;
        }

        @Override
        public File getDatabasePath(String name) {
            //判断是否存在sd卡
            boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
            if (!sdExist) {//如果不存在,
                Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
                return null;
            } else {//如果存在
                String dbDir = mDBPath;
                if (TextUtils.isEmpty(dbDir)) {
                    dbDir = FileUtil.getExternalCacheDirExt(getBaseContext(), "databases");
                }
                if (TextUtils.isEmpty(dbDir)) {
                    dbDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Tecent" + File.separator + getBaseContext().getPackageName()+File.separator+"databases";
                }
                String dbPath = dbDir + "/" + name;//数据库路径
                //判断目录是否存在，不存在则创建该目录
                File dirFile = new File(dbDir);
                if (!dirFile.exists())
                    dirFile.mkdirs();

                //数据库文件是否创建成功
                boolean isFileCreateSuccess = false;
                //判断文件是否存在，不存在则创建该文件
                File dbFile = new File(dbPath);
                if (!dbFile.exists()) {
                    try {
                        isFileCreateSuccess = dbFile.createNewFile();//创建文件
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    isFileCreateSuccess = true;

                //返回数据库文件对象
                if (isFileCreateSuccess)
                    return dbFile;
                else
                    return null;
            }
        }

        /**
         * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
         *
         * @param name
         * @param mode
         * @param factory
         */
        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
            SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            return result;
        }

        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
            SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            return result;
        }
    }
}
