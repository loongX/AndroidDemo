package com.rdm.base.protocol;

import android.os.Handler;
import android.os.Looper;

import com.rdm.base.Abandonable;
import com.rdm.base.BusyFeedback;
import com.rdm.base.thread.ThreadPool;
import com.rdm.common.ILog;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * Created by lokierao on 2016/5/18.
 */
public abstract class Protocol<Param, Result extends Protocol.ProtocolResult> implements Abandonable {

    public static enum SendType {
        /**
         * 只从磁盘里面获取
         */
        ONLY_LOCAL,
        /**
         * 只从网络里面获取
         */
        ONLY_NETWORK,

        /**
         *
         * 会先从缓存里面获取，如果没有(获取读取错误)再从磁盘里面获取，如果还没有，就从网络里面获取；只会回调一次。
         */
        LOCAL_FIRST,
        /**
         *
         * 先从本地（磁盘或者缓存）获取， 再从网络里面获取。因此，最终会有两次结果的回调。
         */
        LOCAL_AND_NETWORK,
    }

    public static interface ErrorCode {
        int UNKONW = -10000;
        int NOT_NETWORK = UNKONW - 1;
        int COMMIT_TIMOUT = UNKONW - 2;
        int READ_TIMEOUT = UNKONW - 3;
        int READ_FROM_LOCAL_ERROR = UNKONW - 4;
        int READ_FROM_NETWORK_ERROR = UNKONW - 5;
        int CONNECT_ERROR = UNKONW - 6;
        int CONNECT_TIMEOUT = UNKONW - 7;
        int LOGOUT = UNKONW - 8; //没有登录
        int LOGIN_ERROR = UNKONW - 9; //登录错误
        int SYSTEM_SECRET_ERROR = UNKONW - 10; //登录错误



    }

    public static class ProtocolResult implements Serializable {

       public boolean succeed = false;
       public int error = ErrorCode.UNKONW;
       public String  errMsg;


        @Override
        public String toString(){


            return String.format("succeed:" + succeed + " ,error=%s, errMsg=%s", getErrorCode(error), errMsg);

        }

        public static String getErrorCode(int error){
            final String erroCode;
            switch (error){
                case ErrorCode.UNKONW: {erroCode = "UNKONW"; break;}
                case ErrorCode.NOT_NETWORK: {erroCode = "NOT_NETWORK"; break;}
                case ErrorCode.COMMIT_TIMOUT: {erroCode = "COMMIT_TIMOUT"; break;}
                case ErrorCode.READ_TIMEOUT: {erroCode = "READ_TIMEOUT"; break;}
                case ErrorCode.READ_FROM_LOCAL_ERROR: {erroCode = "READ_FROM_LOCAL_ERROR"; break;}
                case ErrorCode.READ_FROM_NETWORK_ERROR: {erroCode = "READ_FROM_NETWORK_ERROR"; break;}
                case ErrorCode.CONNECT_ERROR: {erroCode = "CONNECT_ERROR"; break;}
                case ErrorCode.LOGOUT: {erroCode = "LOGOUT:没有登录"; break;}
                case ErrorCode.CONNECT_TIMEOUT: {erroCode = "连接超时"; break;}
                case ErrorCode.LOGIN_ERROR: {erroCode = "登陆错误"; break;}
                case ErrorCode.SYSTEM_SECRET_ERROR: {erroCode = "系统加密错误"; break;}

                default:
                    erroCode = ""+ error;
                    break;
            }
            return erroCode;
        }

    }



    public static interface Commiter<T extends Protocol.ProtocolResult>{

        /**
         * 将最终结果提交到结果中。
         * @param result
         */
        void commit(T result);

        void commitTimeout();

        void commitError(int error, String errMsg);

    }



    public static interface Callback<T extends Protocol.ProtocolResult> {

        /**
         * 最终在UI线程里处理。
         * @param result
         */
        void onResult(T result);
    }

    public static interface Callback2<T extends Protocol.ProtocolResult> {

        /**
         * 最终在UI线程里处理。
         * @param result
         */
        void onResult(boolean fromLocal, T result);
    }

    private static long seqIncrement = 0L;

    public Protocol() {
        createResult_();
    }




    /**
     * 获取协议的unique名称。
     * @return
     */
    public abstract String getUniqueId(Param param);


    public abstract void readFromNetwork(Param param, Commiter<Result> commiter);

    /**
     * 在后台线程处理。
     * @return
     */
    public abstract Result readFromLocal(Param param);


    public BusyFeedback send(Param param, final Callback<Result> callback) {
        return send(getDefaultSendType(),param, new Callback2<Result>() {
            @Override
            public void onResult(boolean fromCache, Result result) {
                if(callback != null) {
                    callback.onResult(result);
                }
            }
        });
    }

    protected SendType getDefaultSendType(){
        return SendType.LOCAL_FIRST;
    }


    public BusyFeedback send(SendType type,final Param param, final Callback2<Result> callback) {
        SendTask task = new SendTask();
        task.type = type;
        task.param =param;
        task.callback = callback;
        task.protocal = this;

        ILog.i(TAG, String.format("REQ: seq=%d, unique=%s, SendType=" + type, task.seq, getUniqueId(param)));
        task.doSend();
        return task;
    }

    private Result notNullNetworkResult(ProtocolResult result){
        if(result == null) {
            result = createResult_();
            result.succeed = false;
            result.error = ErrorCode.READ_FROM_NETWORK_ERROR;
            result.errMsg = "read from local null result";
        }
        return (Result) result;
    }


    private void doReadFromLocal(final  Param param,  final ResultCommiter commiter){
        runInBackground(new Runnable() {
            @Override
            public void run() {
                Result result = readFromLocal(param);
                if(result == null) {
                    result = createResult_();
                    result.succeed = false;
                    result.error = ErrorCode.READ_FROM_LOCAL_ERROR;
                    result.errMsg = "read from local null result";
                }
                commiter.commit(result);
            }
        });
    }


    protected void runInBackground(Runnable runnable){
        ThreadPool.getInstance().submit(runnable);
    }


    /**
     * UI线程。
     * @param fromLocal
     * @param result
     */
    protected void onCommintResultCallback(long seq, boolean fromLocal,Param param, Result result, Object callback, long elapsed) {

        String msg =  String.format("RSP: seq=%d, success=" + result.succeed + " ,from:" + (fromLocal ? "local" : "network") + ", error=%s, elapsed=%d, errMsg=%s", seq, ProtocolResult.getErrorCode(result.error),elapsed, result.errMsg);
        if(result.succeed){
            ILog.i(TAG,msg);
        }else{
            ILog.w(TAG,msg);
        }

        printLog(seq,fromLocal,param,result);

        if(callback instanceof  Callback){
            Callback cb =  (Callback)callback;
            cb.onResult(result);
        }else if(callback instanceof  Callback2){
            Callback2 cb =  (Callback2)callback;
            cb.onResult(fromLocal, result);
        }

    }

    protected void printLog(long seq, boolean fromLocal, Param param, Protocol.ProtocolResult result) {

        if(result.succeed) {
            if (ILog.isEnable(ILog.Level.DEBUG)) {
                StringBuffer sb = new StringBuffer();
                sb.append("seq="+seq+", [result]:"+notNull(result) +"|[param]:"+notNull(param) +"|[from]:" + (fromLocal ? "local" : "network"));
                ILog.d(TAG,sb.toString());
            }
        }else{
            //打印错误信息
            StringBuffer sb = new StringBuffer();
            String className = this.getClass().getSimpleName();
            sb.append("seq="+seq+String.format(", (%s): [unique]:%s", className, notNull(getUniqueId(param))));
            sb.append("\n[result]:"+notNull(result));
            sb.append("\n[from]:" + (fromLocal ? "local" : "network") + " | [param]:" + notNull( param));
            ILog.w(TAG,sb.toString());
        }
    }

    private String notNull( Object ob) {
        return ob == null ? "null" : ob.toString();
    }


    protected long getCommintTimeout(){
        return COMMIT_REUSLT_TIME_OUT;
    }

    private boolean isAbondon = false;

    public void abandon(){
        isAbondon = true;
    }

    public boolean isAbandon(){
        return isAbondon;
    }

    protected Result createResult(){
        try {
            //测试是否有默认的构造方法。
            Class<Result>  clazzOfResult = (Class<Result>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
            return clazzOfResult.newInstance();
        }catch (IllegalAccessException ex){
            throw new RuntimeException("ProtocolResult class must be define: public static class or override createResult() method. ", ex);
        }catch (Exception e) {
            throw new RuntimeException("you must override createResult() method.",e);
        }
    }

    private Result createResult_(){
        Result result = createResult();
        if(result == null) {
            throw new NullPointerException("createResult must no return null.");
        }
        return result;
    }


    private static class SendTask<Param,Result extends ProtocolResult> implements BusyFeedback{

        SendType type;
        Param param;
        Callback2<Result> callback;
        Protocol<Param,Result> protocal;
        private  boolean isBusy = false;
        private long seq;

        private SendTask(){
            synchronized (Protocol.class){
                seq = ++seqIncrement;
            }
        }

        void doSend(){

            final long startTime = System.currentTimeMillis();

            isBusy = true;
            final Handler handler = new Handler(Looper.getMainLooper());
            if (SendType.ONLY_LOCAL == type) {
                final ResultCommiter commit =new ResultCommiter(protocal,handler,callback, COMMIT_REUSLT_TIME_OUT_FOR_READ_LOCAL);
                commit.setResultCommiterListener(new ResultCommiterListener() {
                    @Override
                    public void onDoCommitResult(ProtocolResult result, Object callback) {
                        long elapsed = System.currentTimeMillis() - startTime;
                        protocal.onCommintResultCallback(seq,true, param,(Result) result, callback,elapsed);
                        finalFinished();
                    }
                });
                protocal.doReadFromLocal(param, commit);

                return;
            }else  if(SendType.ONLY_NETWORK == type){
                final ResultCommiter commit = new ResultCommiter(protocal,handler,callback, protocal.getCommintTimeout());
                commit.setResultCommiterListener(new ResultCommiterListener() {
                    @Override
                    public void onDoCommitResult(ProtocolResult result, Object callback) {
                        long elapsed = System.currentTimeMillis() - startTime;
                        protocal.onCommintResultCallback(seq,false,param, protocal.notNullNetworkResult(result), callback,elapsed);
                        finalFinished();

                    }
                });
                commit.startCountTime();
                protocal.readFromNetwork(param,commit);
            }else if(SendType.LOCAL_AND_NETWORK == type) {
                final ResultCommiter localCommit =new ResultCommiter(protocal,handler,callback, COMMIT_REUSLT_TIME_OUT_FOR_READ_LOCAL);
                localCommit.setResultCommiterListener(new ResultCommiterListener() {
                    @Override
                    public void onDoCommitResult(ProtocolResult result, Object callback) {
                        //回调本地结果。
                        long elapsed = System.currentTimeMillis() - startTime;
                        protocal.onCommintResultCallback(seq,true, param,(Result) result, callback,elapsed);

                        final ResultCommiter networkCommit =new ResultCommiter(protocal,handler,callback, protocal.getCommintTimeout());
                        networkCommit.setResultCommiterListener(new ResultCommiterListener() {
                            @Override
                            public void onDoCommitResult(ProtocolResult result, Object callback) {
                                long elapsed = System.currentTimeMillis() - startTime;
                                protocal.onCommintResultCallback(seq,false,param, protocal.notNullNetworkResult(result), callback,elapsed);
                                finalFinished();
                            }
                        });
                        networkCommit.startCountTime();
                        protocal.readFromNetwork(param, networkCommit);

                    }
                });

                protocal.doReadFromLocal(param, localCommit);

            }else if(SendType.LOCAL_FIRST == type) {
                final ResultCommiter localCommit =new ResultCommiter(protocal,handler,callback, COMMIT_REUSLT_TIME_OUT_FOR_READ_LOCAL);
                localCommit.setResultCommiterListener(new ResultCommiterListener() {
                    @Override
                    public void onDoCommitResult(ProtocolResult result, Object callback) {
                        //本地结果success,就回调本地结果，否则回调网络数据。

                        if (result.succeed) {
                            long elapsed = System.currentTimeMillis() - startTime;
                            protocal.onCommintResultCallback(seq,true, param,(Result) result, callback,elapsed);
                            finalFinished();
                            return;
                        }
                        final ResultCommiter networkCommit =new ResultCommiter(protocal,handler,callback, protocal.getCommintTimeout());
                        networkCommit.setResultCommiterListener(new ResultCommiterListener() {
                            @Override
                            public void onDoCommitResult(ProtocolResult result, Object callback) {
                                long elapsed = System.currentTimeMillis() - startTime;
                                protocal.onCommintResultCallback(seq,false, param, protocal.notNullNetworkResult(result), callback,elapsed);
                                finalFinished();
                            }
                        });
                        networkCommit.startCountTime();
                        protocal.readFromNetwork(param, networkCommit);

                    }
                });

                protocal.doReadFromLocal(param, localCommit);

            }
        }

        private void finalFinished(){
            abandon();
            isBusy = false;
        }

        @Override
        public boolean isBusy() {
            return isBusy;
        }

        @Override
        public float getProgress() {
            return 0;
        }

        @Override
        public void cancel() {

        }

        @Override
        public boolean isCancellable() {
            return false;
        }

        @Override
        public String getDescription() {
            Protocol p = protocal;
            Object par = param;
            if(p!= null) {
                return p.getUniqueId(par);
            }
            return null;
        }

        @Override
        public void abandon() {
            type = null;
            param = null;
            callback = null;
            protocal = null;
        }

        @Override
        public boolean isAbandon() {
            return protocal == null;
        }
    }

    private static interface ResultCommiterListener {
        void onDoCommitResult(Protocol.ProtocolResult result, Object callback);
    }

    private static class ResultCommiter<D extends Protocol.ProtocolResult> implements Commiter<D>,Runnable {

       // private boolean isFromLocal;
        private Handler handler = null;
        private Protocol mProtocol;
        private boolean hasCommit = false;
        private Object mCallback;
        private long timeout;

        private ResultCommiterListener resultCommiterListener;

        public ResultCommiter( Protocol protocol, Handler handler, Object callback, long timeout) {
            this.handler = handler;
            mProtocol = protocol;
            mCallback = callback;
            this.timeout = timeout;

            if(mProtocol == null || handler == null) {
                throw  new RuntimeException();
            }

        }


        public void setResultCommiterListener(ResultCommiterListener resultCommiterListener) {
            this.resultCommiterListener = resultCommiterListener;
        }

        /**
         * 开始计时超时时间。
         */
        public void startCountTime(){
            handler.postDelayed(this, timeout);
        }

        @Override
        public synchronized void commit(ProtocolResult result) {
            if(result == null){
                throw new NullPointerException("result not allow null.");
            }
            if(hasCommit) {
                //ignore
                return ;
                //throw new RuntimeException("you have commit before.");
            }
            hasCommit = true;
            final ProtocolResult fianlReulst = result;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mProtocol == null){
                        return;
                    }
                    handler.removeCallbacks(ResultCommiter.this);
                    doCommit(fianlReulst);
                    dispose();
                }
            });
        }



        @Override
        public void commitTimeout() {
            if(mProtocol == null){
                return;
            }
            Protocol.ProtocolResult result = mProtocol.createResult_();
            result.error = ErrorCode.READ_TIMEOUT;
            result.succeed = false;
            result.errMsg = "Read timeout from protocal";
            commit(result);

        }

        @Override
        public void commitError(int error, String errMsg) {
            if(mProtocol == null){
                return;
            }
            Protocol.ProtocolResult result = mProtocol.createResult_();
            result.error = error;
            result.succeed = false;
            result.errMsg = errMsg;
            commit(result);
        }

        @Override
        public void run() {
            if(mProtocol == null){
                return;
            }
            //超时了。
            Protocol.ProtocolResult result = mProtocol.createResult_();
            result.error = ErrorCode.COMMIT_TIMOUT;
            result.succeed = false;
            result.errMsg = "Protocol commit timeout";
            commit(result);
        }

        private void doCommit(Protocol.ProtocolResult result ){
            if(resultCommiterListener != null) {
                resultCommiterListener.onDoCommitResult(result, mCallback);
            }
        }


        private void dispose(){
            mProtocol = null;
            handler = null;
            mCallback = null;
            resultCommiterListener = null;
        }

    }

    private static final long COMMIT_REUSLT_TIME_OUT = 15 * 1000; // 15s
    private static final long COMMIT_REUSLT_TIME_OUT_FOR_READ_LOCAL = 15*100000000000L; // 15s
    public static final String TAG = "Protocol";
}
