package com.rdm.base.db.exception;

import android.content.Context;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.rdm.common.ILog;
import com.rdm.common.util.PlatformUtil;

public class DbCacheExceptionHandler {

    private final static String TAG = "DbCacheExceptionHandler";

    private final static boolean DEBUG = true;

    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());

    private volatile Context mContext;

    private DbCacheExceptionHandler() {
    }

    public void attachContext(Context context) {
        mContext = context != null ? context.getApplicationContext() : null;
    }

    public void handleException(Throwable e) {
        if (e == null) {
            return;
        }

        if (DEBUG) {
            ILog.e(TAG, "handle exception", e);
        }

        if ((e instanceof SQLiteDiskIOException)
                || (e instanceof SQLiteDatabaseCorruptException)
                || (PlatformUtil.version() >= PlatformUtil.VERSION_CODES.HONEYCOMB && e.getClass().getSimpleName().equals("SQLiteCantOpenDatabaseException"))
                || (PlatformUtil.version() >= PlatformUtil.VERSION_CODES.HONEYCOMB && e.getClass().getSimpleName().equals("SQLiteAccessPermException"))
                || (e instanceof SQLiteException && e.getMessage().contains("no such table"))) {
            notifyDbError();
        } else {
            // throw as possible as we can.
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new DbCacheError(e);
            }
        }
    }

    private void notifyDbError() {
        if (isUIThread()) {
            showNotify("存取数据失败，请尝试清理手机存储空间或重新安装");
        } else {
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    showNotify("存取数据失败，请尝试清理手机存储空间或重新安装");
                }
            });
        }
    }


    private void showNotify(CharSequence msg) {
        if (mContext != null) {
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }
    }

    private static void runOnUIThread(Runnable runnable) {
        if (isUIThread()) {
            runnable.run();
        } else {
            sMainHandler.post(runnable);
        }
    }

    private static boolean isUIThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    final static class DbCacheError extends Error {
        public DbCacheError(Throwable t) {
            super(t);
        }
    }

    // ------------ singleton ------------
    final static class InstanceHolder {
        final static DbCacheExceptionHandler INSTANCE = new DbCacheExceptionHandler();
    }

    public static DbCacheExceptionHandler getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
