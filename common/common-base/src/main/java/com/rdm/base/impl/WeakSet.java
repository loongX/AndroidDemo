package com.rdm.base.impl;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 弱引用的Set容器。
 * Created by lokierao on 2015/10/16.
 */
public class WeakSet<T>{

    private Vector<WeakReference<T>> data = new Vector<WeakReference<T>>();

    public boolean add(T t) {

        if(contains(t)){
            return false;
        }
        data.add(new WeakReference<T>(t));
        return true;
    }

    public void clear() {
        data.clear();
    }

    public boolean contains(T o) {
        List<T> list = getList();
        return list.contains(o);
    }



    public boolean isEmpty() {
        return size() == 0;
    }



    public boolean remove(T o) {
        for(int i = data.size() - 1; i >= 0; i--){
            T value = getOrRemove(i);
            if (value != null && value.equals(o)) {
                Log.i("xxxx", "remove : ");
                data.remove(i);
                return true;
            }
        }
        return false;
    }

    public int size() {
        return getList().size();
    }


    public List<T> getList() {
        ArrayList<T> list = new ArrayList<T>(data.size());

        for(int i = data.size() - 1; i >= 0; i--){
            T value = getOrRemove(i);
            if(value!= null) {
                list.add(value);
            }
        }
        return list;
    }

    private T getOrRemove(int index){

        WeakReference<T> item = data.get(index);

        if(item == null) {
            return null;
        }
        T value = item.get();
        if(value == null) {
            Log.i("xxxx", "remove : " + index);
            data.remove(index);
        }
        return value;

    }


}
