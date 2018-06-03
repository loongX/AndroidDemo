
package com.rdm.base.db;

import android.content.Context;
import com.rdm.base.db.EntityManager.UpdateListener;

public interface ISQLiteOpenHelper {

    /**
     * 初始化
     * 
     * @param context 上下文
     * @param name 数据库名字
     * @param version 数据库版本
     */
    public void init(Context context, String name, int version);

    /**
     * 初始化
     * 
     * @param context 上下文
     * @param name 数据库名字
     * @param version 数据库版本
     * @param updateListener 数据库更新监听器
     */
    public void init(Context context, String name, int version, UpdateListener updateListener);

    public ISQLiteDatabase getWritableDatabase();

    /**
     * Create and/or open a database. This will be the same object returned by
     * {@link #getWritableDatabase} unless some problem, such as a full disk, requires the database
     * to be opened read-only. In that case, a read-only database object will be returned. If the
     * problem is fixed, a future call to {@link #getWritableDatabase} may succeed, in which case
     * the read-only database object will be closed and the read/write object will be returned in
     * the future.
     * <p class="caution">
     * Like {@link #getWritableDatabase}, this method may take a long time to return, so you should
     * not call it from the application main thread, including from
     * {@link android.content.ContentProvider#onCreate ContentProvider.onCreate()}.
     *
     * @throws android.database.sqlite.SQLiteException if the database cannot be opened
     * @return a database object valid until {@link #getWritableDatabase}is
     *         called.
     */
    public ISQLiteDatabase getReadableDatabase();
}
