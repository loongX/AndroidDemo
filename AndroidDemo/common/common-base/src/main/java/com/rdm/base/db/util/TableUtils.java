
package com.rdm.base.db.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import android.text.TextUtils;

import com.rdm.common.ILog;
import com.rdm.base.db.EntityContext;
import com.rdm.base.db.annotation.Id;
import com.rdm.base.db.annotation.Table;
import com.rdm.base.db.converter.ColumnConverterFactory;
import com.rdm.base.db.entity.ColumnEntity;
import com.rdm.base.db.entity.IdEntity;

@SuppressWarnings("rawtypes")
public class TableUtils {

    private static final String TAG = "TableUtils";

    private static ConcurrentHashMap<Class, String> mTableNameCache = new ConcurrentHashMap<Class, String>();

    private TableUtils() {
    }

    /**
     * 根据实体类 获得 实体类对应的表名
     * 
     * @param clazz
     * @return
     */
    public static String getTableName(Class<?> clazz) {
        return getTableName(clazz, null);
    }

    public static String getTableName(Class<?> clazz, String tableName) {
        if (TextUtils.isEmpty(tableName)) {
            tableName = mTableNameCache.get(clazz);
            if (TextUtils.isEmpty(tableName)) {
                Table table = (Table) clazz.getAnnotation(Table.class);
                if (table == null || TextUtils.isEmpty(table.name())) {
                    // 当没有注解的时候默认用类的名称作为表名,并把点（.）替换为下划线(_)
                    tableName = clazz.getName().toLowerCase().replace('.', '_');
                } else {
                    tableName = table.name();
                }
                mTableNameCache.put(clazz, tableName);
            }
        }
        return tableName;
    }

    public static int getVersion(Class<?> clazz) {
        Table table = (Table) clazz.getAnnotation(Table.class);
        if (table == null) {
            return 1;
        }
        return table.version();
    }

    public static boolean getDynamicClass(Class<?> clazz) {
        Table table = (Table) clazz.getAnnotation(Table.class);
        if (table == null) {
            return false;
        }
        return table.dynamicClass();
    }

    /**
     * key: entityType.canonicalName
     */
    private static ConcurrentHashMap<String, ConcurrentHashMap<String, ColumnEntity>> entityColumnsMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, ColumnEntity>>();

    /**
     * @param entityType
     * @return key: columnName
     */
    public static synchronized ConcurrentHashMap<String, ColumnEntity> getColumnMap(Class<?> entityType,
            EntityContext entityContext) {

        if (entityColumnsMap.containsKey(entityType.getCanonicalName())) {
            return entityColumnsMap.get(entityType.getCanonicalName());
        }

        ConcurrentHashMap<String, ColumnEntity> columnMap = new ConcurrentHashMap<String, ColumnEntity>();
        addColumns2Map(entityType,  columnMap, entityContext);
        entityColumnsMap.put(entityType.getCanonicalName(), columnMap);

        return columnMap;
    }

    @SuppressWarnings("unchecked")
    private static void addColumns2Map(Class<?> entityType,
            ConcurrentHashMap<String, ColumnEntity> columnMap, EntityContext entityContext) {
        if (entityType != null && !Object.class.equals(entityType)) {
            try {
                Field[] fields = entityType.getDeclaredFields();
                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers())
                            || !ColumnEntity.isColumn(field)) {
                        continue;
                    }
                    if (ColumnConverterFactory.isSupportColumnConverter(field.getType())) {
                            ColumnEntity column = new ColumnEntity(entityType, field);
                            if (!columnMap.containsKey(column.getColumnName())) {
                                columnMap.put(column.getColumnName(), column);
                            }
                    }
                }
                addColumns2Map(entityType.getSuperclass(), columnMap, entityContext);
            } catch (Throwable e) {
                ILog.e(TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * key: entityType.canonicalName
     */
    private static ConcurrentHashMap<String, ArrayList<IdEntity>> entityIdMap = new ConcurrentHashMap<String, ArrayList<IdEntity>>();

    public static synchronized ArrayList<IdEntity> getIdList(Class<?> entityType) {
        if (Object.class.equals(entityType)) {
           return null;
        }

        if (entityIdMap.containsKey(entityType.getCanonicalName())) {
            return entityIdMap.get(entityType.getCanonicalName());
        }

        ArrayList<IdEntity> idEntities = new ArrayList<IdEntity>();

        Field[] fields = entityType.getDeclaredFields();
        if (fields != null) {

            for (Field field : fields) {
                if (field.getAnnotation(Id.class) != null) {
                    IdEntity id = new IdEntity(entityType, field);
                    idEntities.add(id);
                }
            }

            if (idEntities.isEmpty()) {
                for (Field field : fields) {
                    if ("id".equals(field.getName()) || "_id".equals(field.getName())) {
                        IdEntity id = new IdEntity(entityType, field);
                        idEntities.add(id);
                        break;
                    }
                }
            }
        }


        ArrayList<IdEntity> superClassIds = getIdList(entityType.getSuperclass());
        if (superClassIds != null && !superClassIds.isEmpty()) {
            idEntities.addAll(superClassIds);
        }

        int autoIncrementCount = 0;
        for(IdEntity idEntity : idEntities) {
            if(idEntity.isAutoIncrement()) {
                autoIncrementCount++;
            }
            if (autoIncrementCount > 1) {
                throw new RuntimeException("There's more than one field declared to autoIncrement.");
            }
        }

        entityIdMap.put(entityType.getCanonicalName(), idEntities);
        return idEntities;
    }
}
