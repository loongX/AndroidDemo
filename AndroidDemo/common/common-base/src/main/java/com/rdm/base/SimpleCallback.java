package com.rdm.base;

/**
 * Created by lokierao on 2016/9/1.
 */
public interface SimpleCallback<T> {

    void onResult(boolean success,T data);
}
