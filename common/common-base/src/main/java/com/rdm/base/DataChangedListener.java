package com.rdm.base;

/**
 * Created by Rao on 2015/5/2.
 */
public interface DataChangedListener<T> {

    void onChanged(T data);
}
