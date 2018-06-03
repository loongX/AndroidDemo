package com.rdm.base.protocol;

import com.rdm.base.BaseSession;
import com.rdm.base.thread.ThreadPool;
import com.rdm.common.ILog;
import com.rdm.common.util.StringUtils;

import java.io.Serializable;

/**
 *
 * 从数据里面获取
 * Created by lokierao on 2016/5/18.
 */
public abstract class CacheProtocol<Param, Result extends Protocol.ProtocolResult> extends Protocol<Param, Result>{

    private BaseSession session;
    private static final String TAG = "CacheProtocol";

    public CacheProtocol(BaseSession session){
        this.session = session;
    }

    public BaseSession getSession(){
        return session;
    }


    @Override
    final protected void onCommintResultCallback(long seq,boolean fromLocal,Param param, Result result, Object callback, long elapsed) {
        super.onCommintResultCallback(seq,fromLocal, param,result, callback, elapsed);
        if(!fromLocal){
            saveResult(seq,param, result);
        }
    }

    /***
     * 保存缓存数据。将在后台线程执行。
     * @param param
     * @param result
     */
    public void saveResult(Param param, Result result){
        this.saveResult(-1,param,result);
    }

    protected boolean needCache(){
        return true;
    }

    /***
     * 保存缓存数据。将在后台线程执行。
     * @param seq
     * @param param
     * @param result
     */
    protected void saveResult(final long seq,Param param,  Result result) {
        if (needCache() && result.succeed) {
            final String id = getUniqueId(param);
            if(StringUtils.isEmpty(id) || !(result instanceof Serializable)){
                return;
            }

            if(ILog.isEnable(ILog.Level.DEBUG)){
                ILog.d(TAG, String.format("start save: seq=%d, result=%s", seq, result.toString()));
            }

            final Serializable data = (Serializable) result;
            ThreadPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    if(session == null){
                        return ;
                    }
                    session.getPool().put(id,data);
                    if(ILog.isEnable(ILog.Level.DEBUG)){
                        ILog.d(TAG, String.format("save suceessful: seq=%d", seq));
                    }
                }
            });
        }
    }

    public void clearLocal(Param param){
        final String id = getUniqueId(param);
        if(StringUtils.isEmpty(id) ||session == null){
            return;
        }
        session.getPool().remove(id);
    }

    @Override
    public Result readFromLocal(Param param){
        if(session == null){
            return null;
        }
        final String id = getUniqueId(param);
        if(StringUtils.isEmpty(id)){
            return null;
        }
        try {
            Serializable data = session.getPool().get(id);
            return (Result) data;
        }catch (Exception ex){
            ILog.w(TAG,"readFromLocal error:" + ex);
        }

        return null;
    }


    public void abandon(){
        session = null;
        super.abandon();
    }

}
