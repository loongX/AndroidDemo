package com.rdm.base;

import android.os.Bundle;

/**
 * Created by Rao on 2015/1/11.
 */
public interface Publisher {

    /**
     *
     * 订阅指定类的topic。
     * @param eventClass 所有接受的类及其资料。如果是Object类型，将会接受所有的事件；
     * @param subscriber
     */
    public <T> void subscribe( Class<T> eventClass, Subscriber<T> subscriber);


    /**
     * 退订事件。
     * @param subscriber
     */
    public void unsubscribe(Subscriber subscriber);

    /**
     * 发送事件 event，将会在{@link com.rdm.base.Subscriber}回调接口处理。
     * @param event
     */
    public void publish(Object event);




}
