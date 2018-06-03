
package com.rdm.base.db;

/**
 * Author: hugozhong Date: 2013-11-15
 */
@SuppressWarnings("rawtypes")
public class EntityContext {

    private EntityManager mEntityManager;
    private String mTableName;
    private ClassLoader mClassLoader;//加载序列化数据用到的classLoader（插件的classLoader需要传进来）

    public EntityContext(EntityManager entityManager,String tableName,ClassLoader classLoader) {
        mEntityManager = entityManager;
        mTableName = tableName;
        mClassLoader = classLoader;
    }

    public String getTableName() {
        return mTableName;
    }

    public EntityManager getEntityManager() {
        return mEntityManager;
    }

    public ClassLoader getClassLoader() {
        return mClassLoader;
    }
}
