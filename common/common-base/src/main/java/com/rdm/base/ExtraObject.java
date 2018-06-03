package com.rdm.base;

import java.io.Serializable;

/**
 * Created by lokierao on 2015/4/3.
 */
public class ExtraObject implements Serializable {

    private Extra mExtra = null;

    public ExtraObject(){
        this(null);
    }

    public ExtraObject(byte[] data){
        if(data != null){
              mExtra =Extra.parase(data);
        }
    }

    protected Extra getExtra(){
        Extra extra = mExtra;
        if(extra == null) {
            synchronized (this) {
                if (mExtra == null) {
                    mExtra = new Extra();
                }
                extra = mExtra;
            }

        }
        return extra;
    }

    public byte[] toByteArray(){
        Extra extra = null;
        synchronized (this){
            if(mExtra == null){
                return new byte[0];
            }
            extra = mExtra;
        }

       return extra.toByteArray();
    }

    public void loadByteArray(byte[] data){
        if(data ==null){
            throw new NullPointerException();
        }
        Extra extra = Extra.parase(data);
        synchronized (this){
            mExtra = extra;
        }
    }


}
