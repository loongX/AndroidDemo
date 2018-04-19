package com.example.subcriber.base;

/**
 * Created by loonglongago on 2018/8/20.
 */
public interface Publisher {

    /**
     *
     * 订阅指定类的topic。
     * @param eventClass 所有接受的类及其资料。如果是Object类型，将会接受所有的事件；
     * @param subscriber
     */
    public <T> void subscribe(Class<T> eventClass, Subscriber<T> subscriber);


    /**
     * 退订事件。
     * @param subscriber
     */
    public void unsubscribe(Subscriber subscriber);

    /**
     * 发送事件 event，将会在Subscriber回调接口处理。
     * @param event
     */
    public void publish(Object event);




}
