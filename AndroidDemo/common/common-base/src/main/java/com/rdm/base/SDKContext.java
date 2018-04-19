package com.rdm.base;

import com.rdm.base.app.BaseApp;
import com.rdm.base.db.EntityManager;
import com.rdm.base.event.NetworkStatusChangedEvent;

import java.io.File;

/**
 *
 * 运行环境。
 * Created by Rao on 2015/1/10.
 */
public  interface SDKContext {






    /**
     * 返回Files配置环境。
     * @return
     */
    Files getFiles();

    /**
     * 返回数据库的配置环境。
     * @return
     */
    DBInit getDBInit();

    public interface DBInit {

        /**
         * 返回DB的版本号。
         * @return
         */
        int getDBVersion();

        /**
         * 返回DB数据库的更新策略。
         *
         * @return 如果返回null，默认使用{@link com.rdm.base.db.DefaultUpdateListener}策略。
         */
        EntityManager.UpdateListener getUpdateListener();

    }


    public interface Files {

        /**
         * 返回基本存储的路径。
         * @return
         */
        File getBaseDir();

        /**
         * 返回Session存储的路径。
         * @return
         */
        File getSessionDirecotry();

    }

}
