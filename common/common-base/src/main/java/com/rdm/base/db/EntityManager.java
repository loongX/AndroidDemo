
package com.rdm.base.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.text.TextUtils;

import com.rdm.base.annotation.PluginApi;
import com.rdm.base.annotation.PluginVersionCodes;
import com.rdm.base.db.entity.IdEntity;
import com.rdm.base.db.entity.TableEntity;
import com.rdm.base.db.exception.DBException;
import com.rdm.base.db.sqlite.CursorUtils;
import com.rdm.base.db.sqlite.Selector;
import com.rdm.base.db.sqlite.SqlInfo;
import com.rdm.base.db.sqlite.SqlInfoBuilder;
import com.rdm.base.db.sqlite.WhereBuilder;
import com.rdm.base.db.util.KeyValue;
import com.rdm.base.db.util.TableUtils;
import com.rdm.common.ILog;
import com.rdm.common.util.IOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体管理类,用于处理表的增删改查
 *
 * @author hugozhong
 */
@SuppressWarnings("rawtypes")
@PluginApi(since = 6)
public class EntityManager<T> {
    private static final String TAG = "EntityManager";

    private static final String TABLE_VERSIONS_PREFENCE = "table_versions";
    private final SharedPreferences mPreference;
    private final String mPreferenceKey;
    // 是否已经连接数据库
    private Boolean isConnect = false;
    private Class<T> mEntityClass;
    // 执行oepn打开数据库时，保存返回的数据库对象
    private ISQLiteDatabase mSQLiteDatabase = null;
    private ISQLiteOpenHelper mDatabaseHelper = null;
    private OnCloseListener mCloseListener;
    private EntityContext mEntityContext;

    protected EntityManager(Context context, Class<T> clazz, UpdateListener updateListener,
            String databaseName, String table, ClassLoader classLoader,ISQLiteOpenHelper sqliteOpenHelper,String session) {
        mDatabaseHelper = sqliteOpenHelper;
        mSQLiteDatabase = openWritable();
        mEntityClass = clazz;
        if (!TextUtils.isEmpty(table)) {
            table = table.toLowerCase().replace('.', '_');
        }
        String tableName = TableUtils.getTableName(clazz, table);
        mEntityContext = new EntityContext(this, tableName, classLoader);

        mPreference = getPreference(context, session,TABLE_VERSIONS_PREFENCE);
        mPreferenceKey = databaseName + "_" + tableName + "_version";

        checkTableVersion(updateListener);

        createTableIfNotExist(tableName);
    }

    private static SharedPreferences getPreference(Context context, String session, String name) {
        name = name.replaceAll(File.separator, "%2F");

        String uinStr = session != null ? session : "";

        String preferenceName = context.getPackageName() + "_" + uinStr + "_" + name;
        return context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    public EntityContext getEntityContext(){
        return mEntityContext;
    }

    public Class<T> getEntityClass() {
        return mEntityClass;
    }

    private static void fillContentValues(ContentValues contentValues, List<KeyValue> list) {
        if (list != null && contentValues != null) {
            for (KeyValue kv : list) {
                Object value = kv.getValue();
                if (value != null) {
                    if (value instanceof byte[] || value instanceof Byte[]) {
                        contentValues.put(kv.getKey(), (byte[]) value);
                    } else if (value instanceof Byte) {
                        contentValues.put(kv.getKey(), (Byte) value);
                    } else if (value instanceof Boolean) {
                        contentValues.put(kv.getKey(), (Boolean) value);
                    } else {
                        contentValues.put(kv.getKey(), value.toString());
                    }

                }
            }
        } else {
            ILog.w(TAG, "List<KeyValue> is empty or ContentValues is empty!");
        }
    }

    private void checkTableVersion(UpdateListener tableUpdateListener) {
        ISQLiteDatabase db = mSQLiteDatabase;
        TableEntity tableEntity = TableEntity.get(mEntityClass, mEntityContext);
        int version = tableEntity.getVersion();
        int preVersion = mPreference.getInt(mPreferenceKey, -1);
        if (preVersion > 0 && preVersion != version) {
            String tableName = tableEntity.getTableName();
            ILog.i(TAG, "table version changed(table:" + tableName + "| oldVersion:" + preVersion + " |version:"
                    + version + ")");
            if (tableUpdateListener != null) {
                ILog.i(TAG,"tableUpdateListener is not empty , dispatch version change event to listener.");
                if (version > preVersion) {
                    tableUpdateListener.onTableUpgrade(db, tableName, preVersion, version);
                } else {
                    tableUpdateListener.onTableDowngrade(db, tableName, preVersion, version);
                }
                mPreference.edit().putInt(mPreferenceKey, version).commit();
            } else {
                if (db != null) {
                    ILog.i(TAG,"tableUpdateListener is empty , try to drop the table "+ tableName);
                    try {
                        if (dropTableInner()) {
                            mPreference.edit().putInt(mPreferenceKey, version).commit();
                        } else {
                            ILog.e(TAG,"drop table "+ tableName+" failed .");
                        }
                    } catch (SQLException e) {
                        ILog.e(TAG, "It occurs some exception when drop table -->" + e.getMessage(), e);
                    } catch (Exception e) {
                        ILog.e(TAG, "It occurs some exception when drop table -->" +e.getMessage(), e);
                    }
                } else {
                    ILog.e(TAG,"db is empty when table version changed [ tableName:"+tableName+"]");
                }
            }
        } else {
            mPreference.edit().putInt(mPreferenceKey, version).commit();
        }

    }

    private ISQLiteDatabase getSQLiteDatabase() {
        ISQLiteDatabase db = mSQLiteDatabase;
        if (db == null || !db.isOpen()) {
            db = openWritable();
        }
        return db;
    }

    /**
     * 测试 TASQLiteDatabase是否可用
     *
     * @return
     */
    public Boolean testSQLiteDatabase() {
        if (isConnect) {
            if (mSQLiteDatabase.isOpen()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // ------------------ operations -------------//

    /**
     * 以读写方式打开数据库，一旦数据库的磁盘空间满了，数据库就不能以只能读而不能写抛出错误。
     *
     * @return
     */
    private ISQLiteDatabase openWritable() {
        ISQLiteDatabase db = null;
        try {
            db = mDatabaseHelper.getWritableDatabase();
            isConnect = true;
            // 注销数据库连接配置信息
            // 暂时不写
        } catch (Exception e) {
            isConnect = false;
        }

        return db;
    }

    /**
     * 替换表中的数据，此操作会将表中的所有数据清空并替换为传入的entities
     *
     * @param entities 最新数据
     */
    public void replace(List<T> entities) {
        if (entities == null) {
            entities = new ArrayList<T>();
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                deleteAllWithoutTransaction(db);
                List<T> copyEntities = new ArrayList<>(entities);
                for (T entity : copyEntities) {
                    saveWithoutTransaction(entity, db);
                }

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "replace entities failed(cannot get sqlitedatabase)!");
        }
    }

    /**
     * 替换表中的数据，此操作会将表中的所有数据清空并替换为传入的entity
     *
     * @param entity 最新数据
     */
    public void replace(T entity) {
        if (entity == null) {
            return;
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                deleteAllWithoutTransaction(db);
                saveWithoutTransaction(entity, db);

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "replace entity failed(cannot get sqlitedatabase)!");
        }
    }

    /**
     * 保存或更新entity（如果数据库中已有改记录则更新否则插入）
     *
     * @param entity
     * @throws DBException
     */
    @PluginApi(since = 6)
    public void saveOrUpdate(T entity) throws DBException {
        if (entity == null) {
            return;
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                saveOrUpdateWithoutTransaction(entity, db);

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "saveOrUpdate entity failed(cannot get sqlitedatabase)!");
        }
    }

    /**
     * 保存或更新一组对象（如果数据库中已有改记录则更新否则插入）
     *
     * @param entities 需要被saveOrUpdate的一组对象
     * @throws DBException
     */
    @PluginApi(since = 6)
    public void saveOrUpdateAll(List<T> entities) throws DBException {
        if (entities == null || entities.size() == 0) {
            return;
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                List<T> copyEntities = new ArrayList<>(entities);
                for (T entity : copyEntities) {
                    saveOrUpdateWithoutTransaction(entity, db);
                }

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "saveOrUpdateAll failed(cannot get sqlitedatabase)!");
        }
    }

    private void throwException(Throwable e) throws DBException {
       // if (DebugUtil.isDebuggable()) {
            throw new DBException("EntityManager Debug Info",e);
      //  }
    }

    /**
     * 保存entity
     */
    @PluginApi(since = 6)
    public boolean save(T entity) throws DBException {
        if (entity == null) {
            return false;
        }

        boolean result = false;
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                result = saveWithoutTransaction(entity, db);

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "save entity failed(cannot get sqlitedatabase)!");
        }
        return result;
    }

    /**
     * 保存entities
     *
     * @param entities 需要被保存的记录
     * @throws DBException
     */
    @PluginApi(since = 6)
    public void saveAll(List<T> entities) throws DBException {
        if (entities == null || entities.size() == 0) {
            return;
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                List<T> copyEntities = new ArrayList<>(entities);
                for (T entity : copyEntities) {
                    if (!saveWithoutTransaction(entity, db)) {
                        throw new DBException("saveBindingId error, transaction will not commit!");
                    }
                }
                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "saveAll entities failed(cannot get sqlitedatabase)!");
        }
    }

    /**
     * 删除entity
     *
     * @param entity 需要被删除的对象
     * @throws DBException
     */
    @PluginApi(since = 6)
    public void delete(T entity) throws DBException {
        if (entity == null) {
            return;
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                deleteWithoutTransaction(entity, db);

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "delete entity failed(cannot get sqlitedatabase)!");
        }
    }

    /**
     * 删除list中的记录
     *
     * @param entities 需要被删除的记录
     * @throws DBException
     */
    @PluginApi(since = 6)
    public void deleteAll(List<T> entities) throws DBException {
        if (entities == null || entities.size() < 1) {
            return;
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                List<T> copyEntities = new ArrayList<>(entities);
                for (T entity : copyEntities) {
                    deleteWithoutTransaction(entity, db);
                }

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "delete entities failed(cannot get sqlitedatabase)!");
        }
    }

    /**
     * 删除所有记录
     *
     * @throws DBException
     */
    @PluginApi(since = 6)
    public void deleteAll() throws DBException {
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                deleteAllWithoutTransaction(db);

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "deleteAll failed(cannot get sqlitedatabase)!");
        }
    }

    private void deleteAllWithoutTransaction(ISQLiteDatabase db) {
        execSQL(SqlInfoBuilder.buildDeleteSqlInfo(mEntityClass, null, mEntityContext),db);
    }

    /**
     * 根据id删除一条记录
     *
     * @param idValue id值
     * @throws DBException
     */
    @PluginApi(since = 6)
    public void deleteById(Object idValue) throws DBException {
        if (idValue == null) {
            return;
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                execSQL(SqlInfoBuilder.buildDeleteSqlInfo(mEntityClass, idValue, mEntityContext), db);

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "deleteById failed(cannot get sqlitedatabase)!");
        }
    }

    /**
     * 根据指定条件删除表记录
     *
     * @param whereBuilder 条件
     */
    @PluginApi(since = 6)
    public void delete(WhereBuilder whereBuilder) throws DBException {
        if (whereBuilder == null) {
            return;
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                SqlInfo sql = SqlInfoBuilder.buildDeleteSqlInfo(mEntityClass, whereBuilder, mEntityContext);
                execSQL(sql, db);

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "delete failed(cannot get sqlitedatabase)!");
        }
    }

    /**
     * 更新一条记录
     *
     * @param entity 需要更新的对象
     * @param updateColumnNames 被更新的列，null则更新所有列
     */
    @PluginApi(since = 6)
    public void update(T entity, String... updateColumnNames) throws DBException {
        if (entity == null) {
            return;
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                updateWithoutTransaction(entity, db, updateColumnNames);

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "update entity failed(cannot get sqlitedatabase)!");
        }
    }

    /**
     * 更新list里的所有记录
     *
     * @param entities 需要被更新的记录
     * @param updateColumnNames 被更新的列，null则更新所有列
     */
    @PluginApi(since = 6)
    public void updateAll(List<T> entities, String... updateColumnNames) throws DBException {
        if (entities == null || entities.size() < 1) {
            return;
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                List<T> copyEntities = new ArrayList<>(entities);
                for (T entity : copyEntities) {
                    updateWithoutTransaction(entity, db, updateColumnNames);
                }

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "updateAll entities failed(cannot get sqlitedatabase)!");
        }
    }

    /**
     * 更新一条记录
     *
     * @param entity 需要更新的对象
     * @param whereBuilder 条件
     * @param updateColumnNames 被更新的列，null则更新所有列
     */
    @PluginApi(since = 6)
    public void update(T entity, WhereBuilder whereBuilder, String... updateColumnNames) throws DBException {
        if (entity == null) {
            return;
        }
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                execSQL(SqlInfoBuilder.buildUpdateSqlInfo(mEntityContext, mEntityClass,entity, whereBuilder, updateColumnNames), db);

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "update entity failed(cannot get sqlitedatabase)!");
        }
    }

    /**
     * 更新记录
     *
     * @param contentValues 被更新的列及值
     * @param whereBuilder 条件
     */
    @PluginApi(since = 7)
    public void update(ContentValues contentValues, WhereBuilder whereBuilder) {
        ISQLiteDatabase db = getSQLiteDatabase();

        if (db != null) {
            try {
                beginTransaction(db);

                TableEntity table = TableEntity.get(mEntityClass, mEntityContext);
                db.update(table.getTableName(), contentValues, whereBuilder.toString(), null);

                setTransactionSuccessful(db);
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                endTransaction(db);
            }
        } else {
            ILog.e(TAG, "update entity failed(cannot get sqlitedatabase)!");
        }
    }

    @PluginApi(since = PluginVersionCodes.EUTERPE_2_2)
    public T createEntityByCursor(Cursor cursor) {
        if (cursor != null) {
            T entity = (T) CursorUtils.getEntity(cursor, mEntityClass, mEntityContext);
            return entity;
        }
        return null;
    }

    /**
     * 通过id获取记录
     *
     * @param idValue id的值
     */
    @PluginApi(since = 6)
    public T findById(Object idValue) throws DBException {
        if (idValue == null) {
            return null;
        }
        ArrayList<IdEntity> idEntities = TableEntity.get(mEntityClass, mEntityContext).getIdList();

        if (idEntities == null || idEntities.size() > 1) {
            ILog.e(TAG,"There's more than one id, cannot use findById method!!");
           return null;
        }
        IdEntity id = idEntities.get(0);
        if (id == null) {
            ILog.e(TAG,"findById failed[id is empty]");
            return null;
        }
        Selector selector = Selector.create().where(id.getColumnName(), "=", idValue);
        String sql = selector.limit(1).buildSql(mEntityClass, mEntityContext);
        Cursor cursor = query(sql);
        try {
            if (cursor != null && cursor.moveToNext()) {
                T entity = (T) CursorUtils.getEntity(cursor, mEntityClass, mEntityContext);
                return entity;
            }
        } catch (Exception e) {
            ILog.e(TAG, e.getMessage(), e);
            throwException(e);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return null;
    }

    public T findBy(String columnName, Object columnValue) throws DBException {
        Selector selector = Selector.create().where(columnName, "=", columnValue);
        return findFirst(selector);
    }

    /**
     * 获取当前查询条件里的第一条记录
     *
     * @param selector 查询条件
     */
    @PluginApi(since = 6)
    public T findFirst(Selector selector) throws DBException {
        if (selector == null) {
            selector = Selector.create();
        }
        String sql = selector.limit(1).buildSql(mEntityClass, mEntityContext);

        Cursor cursor = query(sql);
        try {
            if (cursor != null && cursor.moveToNext()) {
                T entity = (T) CursorUtils.getEntity(cursor, mEntityClass, mEntityContext);
                return entity;
            }
        } catch (Exception e) {
            ILog.e(TAG, e.getMessage(), e);
            throwException(e);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return null;
    }

    /**
     * 查找当前表的所有记录
     */
    @PluginApi(since = 6)
    public List<T> findAll() throws DBException {
        return findAll(Selector.create());
    }

    /**
     * 带查询条件的查找所有
     *
     * @param selector 查询条件
     */
    @PluginApi(since = 6)
    public List<T> findAll(Selector selector) throws DBException {
        if (selector == null) {
            selector = Selector.create();
        }
        String sql = selector.buildSql(mEntityClass, mEntityContext);

        Cursor cursor = query(sql);
        List<T> result = new ArrayList<T>();
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    T entity = (T) CursorUtils.getEntity(cursor, mEntityClass, mEntityContext);
                    result.add(entity);
                }
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                throwException(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return result;
    }

    public void execSQL(SqlInfo sqlInfo) throws DBException {
        execSQL(sqlInfo, null);
    }

    private void execSQL(SqlInfo sqlInfo,ISQLiteDatabase db) throws DBException {
        if (sqlInfo == null) {
            return;
        }
        debugSql(sqlInfo.getSql());
        try {
            if (db == null) {
                db = getSQLiteDatabase();
            }
            if (db != null) {
                if (sqlInfo.getBindArgs() != null) {
                    db.execSQL(sqlInfo.getSql(), sqlInfo.getBindArgsAsArray());
                } else {
                    db.execSQL(sqlInfo.getSql());
                }
            } else {
                ILog.e(TAG, "cannot get sqlitedatabase!" + sqlInfo.getSql());
            }
        } catch (Throwable e) {
            throw new DBException(e);
        }
    }

    @PluginApi(since = 7)
    public void execSQL(String sql) throws DBException {
        execSQL(sql, null);
    }

    private void execSQL(String sql,ISQLiteDatabase db) throws DBException {
        if (sql == null) {
            return;
        }
        debugSql(sql);
        try {
            if (db == null) {
                db = getSQLiteDatabase();
            }
            if (db != null) {
                db.execSQL(sql);
            } else {
                ILog.e(TAG, "cannot get sqlitedatabase!");
            }
        } catch (Throwable e) {
            throw new DBException(e);
        }
    }

    @PluginApi(since = 6)
    public Cursor query(Selector selector) throws DBException {
        if (selector == null) {
            selector = Selector.create();
        }
        String sql = selector.buildSql(mEntityClass, mEntityContext);
        return query(sql);
    }

    public Cursor query(String sql) throws DBException {
        return rawQuery(sql, null);
    }

    // ----------- private operations with out transaction ----------------//
    private void saveOrUpdateWithoutTransaction(T entity, ISQLiteDatabase db) throws DBException {
        if (!hasEmptyIdEntity(entity)) {// id有值则用replace语法实现saveOrUpdate
            replaceWithoutTransaction(entity, db);
        } else { // id没有值则肯定是需要插入的
            saveWithoutTransaction(entity, db);
        }
    }

    private boolean hasEmptyIdEntity(T entity) {
        ArrayList<IdEntity> idEntities = TableUtils.getIdList(mEntityClass);
        if (idEntities != null) {
            for(IdEntity idEntity : idEntities) {
                if (idEntity.getColumnValue(entity) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void replaceWithoutTransaction(T entity, ISQLiteDatabase db) throws DBException {
        execSQL(SqlInfoBuilder.buildReplaceSqlInfo(mEntityClass,entity, mEntityContext), db);
    }

    private boolean saveWithoutTransaction(T entity,ISQLiteDatabase db) throws DBException {
        TableEntity table = TableEntity.get(mEntityClass, mEntityContext);
        ArrayList<IdEntity> idEntities = table.getIdList();
        List<KeyValue> entityKvList = SqlInfoBuilder.collectInsertKeyValues(mEntityClass,entity, mEntityContext);
        if (entityKvList != null && entityKvList.size() > 0) {
            ContentValues cv = new ContentValues();
            fillContentValues(cv, entityKvList);
            if (db == null) {
                db = getSQLiteDatabase();
            }
            if (db != null) {
                Long id = db.insert(table.getTableName(), null, cv);
                if (idEntities != null) {
                    for (IdEntity idColumn : idEntities) {
                        if(idColumn != null) {
                            if (idColumn.isAutoIncrement()) {
                                if (id == -1) {
                                    return false;
                                }
                                idColumn.setAutoIncrementId(entity, id);
                            } else if (idColumn.isUUIDGenerationType()) {
                                Object uuid = cv.get(idColumn.getColumnName());
                                idColumn.setIdValue(entity, uuid);
                            }
                        }
                    }
                }
                return true;
            } else {
                ILog.e(TAG, "saveWithoutTransaction failed(cannot get sqlitedatabase)!");
            }

        }
        return false;
    }

    private void deleteWithoutTransaction(T entity, ISQLiteDatabase db) throws DBException {
        execSQL(SqlInfoBuilder.buildDeleteByObjectSqlInfo(mEntityClass,entity, mEntityContext), db);
    }

    // --------------- tools ---------------//

    private void updateWithoutTransaction(T entity, ISQLiteDatabase db, String... updateColumnNames) throws DBException {
        execSQL(SqlInfoBuilder.buildUpdateSqlInfo(mEntityContext,mEntityClass, entity, updateColumnNames), db);
    }

    private void createTableIfNotExist(String table) throws DBException {
        try {
            SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(mEntityClass, mEntityContext);
            execSQL(sqlInfo);
        } catch (Exception e) {
            ILog.e(TAG,e.getMessage(),e);
        }
    }

    @PluginApi(since = 6)
    public int getCount() {
        TableEntity table = TableEntity.get(mEntityClass, mEntityContext);
        Cursor cursor = null;
        try {
            cursor = query("SELECT COUNT(*) FROM " + table.getTableName());
            if (cursor != null && cursor.moveToNext()) {
                return cursor.getInt(0);
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return 0;
    }

    public boolean tableIsExist() throws DBException {
        TableEntity table = TableEntity.get(mEntityClass, mEntityContext);

        Cursor cursor = null;
        try {
            cursor = query("SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='"
                    + table.getTableName() + "'");
            if (cursor != null && cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    return true;
                }
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }

        return false;
    }

    @PluginApi(since = 6)
    public void dropDb() throws DBException {
        Cursor cursor = null;
        try {
            cursor = query("SELECT name FROM sqlite_master WHERE type ='table'");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    try {
                        String tableName = cursor.getString(0);
                        execSQL("DROP TABLE " + tableName);
                        TableEntity.remove(tableName);
                    } catch (Throwable e) {
                        ILog.e(TAG, e.getMessage(), e);
                    }
                }
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    @PluginApi(since = 6)
    public void dropTable() throws DBException {
        dropTableInner();
    }

    private boolean dropTableInner() {
        TableEntity table = TableEntity.get(mEntityClass, mEntityContext);
        if (table != null) {
            try {
                execSQL("DROP TABLE IF EXISTS " + table.getTableName());
            } catch (Exception e) {
                ILog.e(TAG, e.getMessage(), e);
                return false;
            }
            if (tableIsExist()) {
                return false;
            } else {
                TableEntity.remove(mEntityClass);
                return true;
            }
        } else {
            return false;
        }
    }

    // ---------------- exec sql ----------------//
    private void debugSql(String sql) {
        if (ILog.isEnable(ILog.Level.DEBUG)) {
            ILog.d(TAG, sql);
        }
    }

    private void beginTransaction(ISQLiteDatabase db) {
        try {
            db.beginTransaction();
        } catch (Exception e) {
            ILog.e(TAG,e.getMessage(),e);
        }
    }

    private void setTransactionSuccessful(ISQLiteDatabase db) {
        try {
            db.setTransactionSuccessful();
        } catch (Exception e) {
            ILog.e(TAG,e.getMessage(),e);
            throwException(e);
        }
    }

    private void endTransaction(ISQLiteDatabase db) {
        try {
            db.endTransaction();
        } catch (Exception e) {
            ILog.e(TAG,e.getMessage(),e);
            throwException(e);
        }
    }

    private Cursor rawQuery(String sql, String[] selectionArgs) {
        debugSql(sql);
        try {
            ISQLiteDatabase db = getSQLiteDatabase();
            if (db != null) {
                Cursor cursor = db.rawQuery(sql, selectionArgs);
                if (cursor != null) {
                    return SafeCursorWrapper.create(cursor);
                }
            } else {
                ILog.e(TAG,"rawQuery failed[cannot get sqlitedatabase]!");
            }

        } catch (Exception e) {
            ILog.e(TAG, e.getMessage(), e);
            throwException(e);
        }
        return null;
    }

    /**
     * 关闭数据库
     */
    @PluginApi(since = 6)
    public void close() {
        ISQLiteDatabase db = getSQLiteDatabase();
        if (db != null) {
            db.close();
        }
        notifyClosed();
    }

    private void notifyClosed() {
        final OnCloseListener listener = mCloseListener;
        if (listener != null) {
            listener.onClosed(this);
        }
    }

    /**
     * Set the {@link OnCloseListener}.
     *
     * @param listener close listener to set.
     */
    void setOnCloseListener(OnCloseListener listener) {
        mCloseListener = listener;
    }

    public boolean isClosed() {
        return false;
    }

    /**
     * Interface 数据库升级回调
     */
    @PluginApi(since = 8)
    public interface UpdateListener {

        @PluginApi(since = 8)
        public void onDatabaseUpgrade(ISQLiteDatabase db, int oldVersion, int newVersion);

        @PluginApi(since = 8)
        public void onDatabaseDowngrade(ISQLiteDatabase db, int oldVersion, int newVersion);

        @PluginApi(since = 8)
        public void onTableUpgrade(ISQLiteDatabase db, String tableName, int oldVersion, int newVersion);

        @PluginApi(since = 8)
        public void onTableDowngrade(ISQLiteDatabase db, String tableName, int oldVersion, int newVersion);
    }

    /**
     * Listener to monitor db close.
     */
    interface OnCloseListener {
        public void onClosed(EntityManager<?> entityManager);
    }

}
