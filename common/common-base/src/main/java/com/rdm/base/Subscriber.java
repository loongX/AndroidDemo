package com.rdm.base;

/**
 * 事件接受器。 <br/>
 * 可通过{@link com.rdm.base.Publisher#subscribe(Class, Subscriber)}来订阅一个事件。<br/>
 * 通过{@link Publisher#publish(Object)}发送的event对象将会在这里处理。<br/>
 * 可通过{@link BaseSession#getPublisher()}获取{@link com.rdm.base.Publisher}对象。<br/>
 *
 * Created by Rao on 2015/1/11.
 */
public interface Subscriber<T> {

    /**
     *
     * @param event 包含其子类的实现。
     */
    void onEvent(T event);
}