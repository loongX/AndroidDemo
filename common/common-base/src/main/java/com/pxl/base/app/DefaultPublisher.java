package com.pxl.base.app;

import android.os.Handler;
import android.os.Looper;

import com.pxl.base.Publisher;
import com.pxl.base.Subscriber;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rao on 2015/1/11.
 * //TODO 当前若没有调用unsubscribe()方法，不会自动回收。
 */
/*package*/ class DefaultPublisher implements Publisher {

    private Map subscriberMap = new HashMap();
    private final Object listenerLock = new Object();
    private Map<Object, Class> typeMap = new HashMap();
    private Handler mHandler;

    public DefaultPublisher(Looper looper) {
        mHandler = new Handler(looper);

    }


    //订阅事件
    @Override
    public <T> void subscribe(Class<T> eventClass, Subscriber<T> subscriber) {
        if (eventClass == null) {
            throw new IllegalArgumentException("Event class must not be null");
        }
        if (subscriber == null) {
            throw new IllegalArgumentException(
                    "Event subscriber must not be null");
        }

        //如果订阅了就不在订阅
        //subscriber.cls = eventClass;
        Class cls = typeMap.get(subscriber);
        if (cls != null) {
            if (cls != eventClass) {
                throw new IllegalArgumentException(
                        "Event subscriber has  subscribed!");
            }
            return;
        }
        //put into 事件处理方法和事件类型
        typeMap.put(subscriber, eventClass);
        subscribe(eventClass, subscriberMap,
                new WeakReference<Subscriber>(subscriber));
    }

    //解除事件
    @Override
    public void unsubscribe(Subscriber subscriber) {
        Class cls = typeMap.remove(subscriber);
        if (cls != null) {
            unsubscribe(cls, subscriberMap, subscriber);
        }

    }

    //外部调用产生事件
    @Override
    public void publish(Object event) {
        if (event == null) {
            throw new IllegalArgumentException("Cannot publish null event.");
        }
        //publish(event, null, null, getSubscribers(event.getClass()));

        mHandler.post(new PublishRunnable(event, getSubscribers(event.getClass())));
    }

    //获取订阅者
    public <T> List<T> getSubscribers(Class<T> eventClass) {
        synchronized (listenerLock) {
            return getSubscribersToClass(eventClass);
        }
    }

    public <T> List<T> getSubscribersToClass(Class<T> eventClass) {
        List result = null;
        final Map classMap = subscriberMap;
        Set keys = classMap.keySet();
        for (Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            Class cl = (Class) iterator.next();
            if (cl.isAssignableFrom(eventClass)) {
                Collection subscribers = (Collection) classMap.get(cl);
                if (result == null) {
                    result = new ArrayList();
                }
                result.addAll(createCopyOfContentsRemoveWeakRefs(subscribers));
            }
        }

        return result;
    }

    //创建弱引用
    private List createCopyOfContentsRemoveWeakRefs(
            Collection subscribersOrVetoListeners) {
        if (subscribersOrVetoListeners == null) {
            return null;
        }
        List copyOfSubscribersOrVetolisteners = new ArrayList(
                subscribersOrVetoListeners.size());
        for (Iterator iter = subscribersOrVetoListeners.iterator(); iter
                .hasNext(); ) {
            Object elem = iter.next();
            if (elem instanceof WeakReference) {
                Object hardRef = ((WeakReference) elem).get();
                if (hardRef == null) {
                    // Was reclaimed, unsubscribe
                    iter.remove();
                    // decWeakRefPlusProxySubscriberCount();
                } else {
                    copyOfSubscribersOrVetolisteners.add(hardRef);
                }
            } else {
                copyOfSubscribersOrVetolisteners.add(elem);
            }
        }
        return copyOfSubscribersOrVetolisteners;
    }

    //订阅事件，subscribe(eventClass, subscriberMap, new WeakReference<Subscriber>(subscriber));
    protected boolean subscribe(final Object classTopicOrPatternWrapper,
                                final Map<Object, Object> subscriberMap, final Object subscriber) {
        //检查非空值
        if (classTopicOrPatternWrapper == null) {
            throw new IllegalArgumentException("Can't subscribe to null.");
        }

        if (subscriber == null) {
            throw new IllegalArgumentException(
                    "Can't subscribe null subscriber to "
                            + classTopicOrPatternWrapper);
        }
        boolean alreadyExists = false;
        Object realSubscriber = subscriber;
        boolean isWeakRef = subscriber instanceof WeakReference;
        if (isWeakRef) {
            realSubscriber = ((WeakReference) subscriber).get();//获取弱引用订阅对象
        }

        if (realSubscriber == null) {
            return false;// already garbage collected? Weird.
        }
        synchronized (listenerLock) {
            List currentSubscribers = (List) subscriberMap
                    .get(classTopicOrPatternWrapper);//根据事件类型查找
            if (currentSubscribers == null) {//如果没有订阅者

                currentSubscribers = new ArrayList();
                subscriberMap.put(classTopicOrPatternWrapper,
                        currentSubscribers);//put一个进去
            } else {
                // Double subscription check and stale subscriber cleanup
                // Need to compare the underlying referents for WeakReferences
                // and
                // ProxySubscribers
                // to make sure a weak ref and a hard ref aren't both subscribed
                // to the same topic and object.
                // Use the proxied subscriber for comparison if a
                // ProxySubscribers
                // is used
                // Subscribing the same object by proxy and subscribing
                // explicitly
                // should
                // not subscribe the same object twice
                for (Iterator iterator = currentSubscribers.iterator(); iterator
                        .hasNext(); ) {//迭代器，清理重复的
                    Object currentSubscriber = iterator.next();
                    Object realCurrentSubscriber = getRealSubscriberAndCleanStaleSubscriberIfNecessary(
                            iterator, currentSubscriber);
                    if (realSubscriber.equals(realCurrentSubscriber)) {
                        // Already subscribed.
                        // Remove temporarily, to add to the end of the calling
                        // list
                        iterator.remove();
                        alreadyExists = true;
                    }
                }
            }

            currentSubscribers.add(realSubscriber);
            return !alreadyExists;
        }
    }

    //清除订阅者
    protected Object getRealSubscriberAndCleanStaleSubscriberIfNecessary(
            Iterator iterator, Object existingSubscriber) {
        if (existingSubscriber instanceof WeakReference) {
            existingSubscriber = ((WeakReference) existingSubscriber).get();
            if (existingSubscriber == null) {
                iterator.remove();
                // decWeakRefPlusProxySubscriberCount();
            }
        }

        return existingSubscriber;
    }

    //解除订阅
    protected boolean unsubscribe(Object o, Map subscriberMap, Object subscriber) {

        if (o == null) {
            throw new IllegalArgumentException("Can't unsubscribe to null.");
        }
        if (subscriber == null) {
            throw new IllegalArgumentException(
                    "Can't unsubscribe null subscriber to " + o);
        }

        synchronized (listenerLock) {
            return removeFromSetResolveWeakReferences(subscriberMap, o,
                    subscriber);
        }
    }

    //移除弱引用订阅者
    private boolean removeFromSetResolveWeakReferences(Map map, Object key,
                                                       Object toRemove) {
        List subscribers = (List) map.get(key);
        if (subscribers == null) {
            return false;
        }
        if (subscribers.remove(toRemove)) {
            if (toRemove instanceof WeakReference) {
                // decWeakRefPlusProxySubscriberCount();
            }
            return true;
        }

        // search for WeakReferences and ProxySubscribers
        for (Iterator iter = subscribers.iterator(); iter.hasNext(); ) {
            Object existingSubscriber = iter.next();

            if (existingSubscriber instanceof WeakReference) {
                WeakReference wr = (WeakReference) existingSubscriber;
                Object realRef = wr.get();
                if (realRef == null) {
                    // clean up a garbage collected reference
                    iter.remove();
                    // decWeakRefPlusProxySubscriberCount();
                    return true;
                } else if (realRef == toRemove) {
                    iter.remove();
                    // decWeakRefPlusProxySubscriberCount();
                    return true;
                }
            }
        }
        return false;
    }


    //并让订阅者执行对应事件，订阅者已经筛选出来了
    protected void publish(final Object event, final List subscribers) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Can't publish to null event.");
        }

        if (subscribers == null || subscribers.isEmpty()) {

            return;
        }

        for (int i = 0; i < subscribers.size(); i++) {
            Object eh = subscribers.get(i);
            if (event != null) {
                Subscriber eventSubscriber = (Subscriber) eh;
                try {
                    eventSubscriber.onEvent(event);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //开启任务运行执行事件
    class PublishRunnable implements Runnable {
        Object theEvent;
        List theSubscribers;

        public PublishRunnable(final Object event, final List subscribers) {
            this.theEvent = event;
            this.theSubscribers = subscribers;
        }

        @Override
        public void run() {
            publish(theEvent, theSubscribers);
        }
    }
}
