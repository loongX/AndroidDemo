package com.rdm.base.db.pool;


import com.rdm.base.db.Pool;

/**
 * Author: donnyliu
 */
public interface SerializablePool<Type> extends Pool<Type> {

    void restore();

    void serialize();

    void setInvalidated(boolean invalidated);

}
