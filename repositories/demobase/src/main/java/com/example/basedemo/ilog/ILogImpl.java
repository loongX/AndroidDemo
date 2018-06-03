package com.example.basedemo.ilog;


import com.example.basedemo.ilog.base.ILog;
import com.example.basedemo.ilog.log.TLog;

/**
 * Created by lokierao on 2016/5/17.
 */
public class ILogImpl implements ILog.Delegate {

    public ILogImpl(){
        TLog.enableFileAppender(true, FileConstans.getOrCreateDirectory(FileConstans.DIR_LOGGER_DIR_NAME).getAbsolutePath());
        TLog.enableDebug(true);
    }

    @Override
    public void verbose(String tag, String message) {
        TLog.v(tag,message);
    }

    @Override
    public void debug(String tag, String message) {
        TLog.d(tag,message);

    }

    @Override
    public void info(String tag, String message) {
        TLog.i(tag,message);

    }

    @Override
    public void warn(String tag, String message) {
        TLog.w(tag,message);

    }

    @Override
    public void error(String tag, String message) {
        TLog.e(tag,message);

    }

    @Override
    public void assert_(String tag, String message) {
        TLog.w(tag,message);

    }
}
