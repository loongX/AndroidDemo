package com.example.subcriber.base;

/**
 * 事件接受器。
 * 可通过Publisher#subscribe(Class, Subscriber)}来订阅一个事件。
 * 通过{@link Publisher#publish(Object)}发送的event对象将会在这里处理。
 * 可通过getPublisher()}获取Publisher对象。
 *
 * Created by loonglongago on 2018/8/20.
 */
public interface Subscriber<T> {

    /**
     *
     * @param event 包含其子类的实现。
     */
    void onEvent(T event);
}