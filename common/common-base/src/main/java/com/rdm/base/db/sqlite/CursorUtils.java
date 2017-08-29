
package com.rdm.base.db.sqlite;

import android.database.Cursor;
import android.text.TextUtils;

import com.rdm.common.ILog;
import com.rdm.base.db.EntityContext;
import com.rdm.base.db.entity.ColumnEntity;
import com.rdm.base.db.entity.IdEntity;
import com.rdm.base.db.entity.TableEntity;
import com.rdm.base.db.exception.DBException;

import java.util.ArrayList;

public class CursorUtils {

    private static final String TAG = "CursorUtils";

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static <T> T getEntity(final Cursor cursor, Class<T> entityType, EntityContext entityContext) throws DBException{
        if (cursor == null)
            return null;
        try {
            TableEntity table = TableEntity.get(entityType, entityContext);
            T entity = null;
            if (table.isDynamicClass()) {
               String className = cursor.getString(cursor.getColumnIndex(ColumnEntity.RESERVED_COLUMN_DYNAMIC_CLASS));
                if(!TextUtils.isEmpty(className)) {
                    ClassLoader classLoader = entityContext.getClassLoader();
                    if (classLoader == null) {
                        classLoader = CursorUtils.class.getClassLoader();
                    }
                    if (classLoader != null) {
                        Class clazz = classLoader.loadClass(className);
                        entity = (T) clazz.newInstance();
                    }
                }
            }
            if (entity == null) {
                entity = entityType.newInstance();
            }

            ArrayList<IdEntity> idEntities = table.getIdList();
            if(idEntities != null) {
                for (IdEntity id : idEntities) {
                    String idColumnName = id.getColumnName();
                    int idIndex = cursor.getColumnIndex(idColumnName);
                    id.setValue2Entity(entity, cursor, idIndex, entityContext.getClassLoader());
                }
            }
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String columnName = cursor.getColumnName(i);
                ColumnEntity column = table.columnMap.get(columnName);
                if (column != null) {
                    column.setValue2Entity(entity, cursor, i, entityContext.getClassLoader());
                }
            }
            return entity;
        } catch (Throwable e) {
            ILog.e(TAG, e.getMessage(), e);
          //  if (DebugUtil.isDebuggable()) {
                throw new DBException("CursorUtils Debug Info ",e);
           // }
        }

       // return null;
    }

}
