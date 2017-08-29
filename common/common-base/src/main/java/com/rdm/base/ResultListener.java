package com.rdm.base;

/**
 *  提供公共的返回结果监听器。
 *  返回结果是在主线程上。
 * Created by lokierao on 2015/1/9.
 */
public interface ResultListener<RESULT, ERROR> {

    void onResult(RESULT result);

    void onError(ERROR error);

}