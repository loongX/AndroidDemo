package com.rdm.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.rdm.base.app.BaseApp;
import com.rdm.base.db.EntityManager;
import com.rdm.base.db.Pool;
import com.rdm.base.db.pool.PoolTableV2;
import com.rdm.base.db.pool.SimpleDbPool;
import com.rdm.common.util.StringUtils;

import java.io.File;
import java.io.Serializable;

/**
 *
 *  Session提供某个状态所需要的运行环境。
 * Created by lokierao on 2015/1/9.
 */
public class BaseSession {

    final private  File mDirectory;
    final private  String mUuid;


    public BaseSession(String uid){

        if(StringUtils.isEmpty(uid) ){
            throw new RuntimeException("BaseSession uid empty arguments");
        }
        File directory = new File(BaseApp.get().getSDkContext().getFiles().getSessionDirecotry(), uid);

        if(!directory.exists()){
            if(!directory.mkdirs()){
                throw new RuntimeException("create BaseSession dir fail.");

            }
        }
        mDirectory = directory;
        mUuid = uid;
        if(!mDirectory.exists()){
           if(! mDirectory.mkdirs()){
               throw new RuntimeException("BaseSession create directory error.");
           }
        }
    }


    /**
     * 返回Session的存储目录。
     * @return
     */
    public  File getDirecotry(){
        return mDirectory;
    }

    /**
     * 返回该Session的用户ID。用于标示sessionID。一般的，跟用户登陆账号是绑定的，相一致的。
     * @return
     */
    public String getUid(){
        return mUuid;
    }

    /**
     * 返回配置文件对象。
     * @return
     */
    public SharedPreferences getPreferences(){
        return getPreferences(BaseSession.class.getName());
    }

    /**
     * 返回配置文件对象。
     * @param name 配置名称。
     * @return
     */
    public SharedPreferences getPreferences(String name){
        return BaseApp.get().getSharedPreferences(name,Context.MODE_PRIVATE);
    }

    public <T> EntityManager<T> getEntityManager(Class<T> clazz) {
        return BaseApp.get().getEntityManagerFactory(this).getEntityManager(clazz);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseSession session = (BaseSession) o;

        if (mUuid != null ? !mUuid.equals(session.mUuid) : session.mUuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mUuid != null ? mUuid.hashCode() : 0;
    }

    private Pool<Serializable> mDBPool = null;
    public synchronized Pool<Serializable> getPool(){
        if(mDBPool == null){
            mDBPool = new SimpleDbPool(PoolTableV2.TABLE_NAME, this);
        }
        return mDBPool;
    }

    public void dispose(){
        mDBPool = null;
    }

}
