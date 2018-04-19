package com.rdm.base.app;

import android.os.Handler;
import android.os.Looper;

import com.rdm.base.Publisher;
import com.rdm.base.Subscriber;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Created by Rao on 2015/1/11.
 * //TODO 当前若没有调用unsubscribe()方法，不会自动回收。
 */
/*package*/ class DefaultPublisher implements Publisher {

    private Map subscriberMap	= new HashMap();
    private final Object		listenerLock		= new Object();
    private Map<Object,Class> typeMap	= new HashMap();
    private Handler				mHandler;

    public DefaultPublisher(Looper looper){
        mHandler = new Handler(looper);

    }


    @Override
    public <T> void subscribe(Class<T> eventClass, Subscriber<T> subscriber) {
        if (eventClass == null)
        {
            throw new IllegalArgumentException("Event class must not be null");
        }
        if (subscriber == null)
        {
            throw new IllegalArgumentException(
                    "Event subscriber must not be null");
        }

        //subscriber.cls = eventClass;
        Class cls =  typeMap.get(subscriber);
        if(cls != null){
           if(cls != eventClass){
                throw new IllegalArgumentException(
                       "Event subscriber has  subscribed!");
           }
            return ;
        }
        typeMap.put(subscriber,eventClass);
         subscribe(eventClass, subscriberMap,
                new WeakReference<Subscriber>(subscriber));
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        Class cls = typeMap.remove(subscriber);
        if(cls!= null){
            unsubscribe(cls, subscriberMap,subscriber);
        }

    }

    @Override
    public void publish(Object event) {
        if (event == null)
        {
            throw new IllegalArgumentException("Cannot publish null event.");
        }
        //publish(event, null, null, getSubscribers(event.getClass()));

        mHandler.post(new PublishRunnable(event,  getSubscribers(event.getClass())));
    }

    public <T> List<T> getSubscribers(Class<T> eventClass)
    {
        synchronized (listenerLock)
        {
            return getSubscribersToClass(eventClass);
        }
    }

    public <T> List<T> getSubscribersToClass(Class<T> eventClass)
    {
        List result = null;
        final Map classMap = subscriberMap;
        Set keys = classMap.keySet();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();)
        {
            Class cl = (Class) iterator.next();
            if (cl.isAssignableFrom(eventClass))
            {
                Collection subscribers = (Collection) classMap.get(cl);
                if (result == null)
                {
                    result = new ArrayList();
                }
                result.addAll(createCopyOfContentsRemoveWeakRefs(subscribers));
            }
        }

        return result;
    }

    private List createCopyOfContentsRemoveWeakRefs(
            Collection subscribersOrVetoListeners)
    {
        if (subscribersOrVetoListeners == null)
        {
            return null;
        }
        List copyOfSubscribersOrVetolisteners = new ArrayList(
                subscribersOrVetoListeners.size());
        for (Iterator iter = subscribersOrVetoListeners.iterator(); iter
                .hasNext();)
        {
            Object elem = iter.next();
             if (elem instanceof WeakReference)
            {
                Object hardRef = ((WeakReference) elem).get();
                if (hardRef == null)
                {
                    // Was reclaimed, unsubscribe
                    iter.remove();
                    // decWeakRefPlusProxySubscriberCount();
                }
                else
                {
                    copyOfSubscribersOrVetolisteners.add(hardRef);
                }
            }
            else
            {
                copyOfSubscribersOrVetolisteners.add(elem);
            }
        }
        return copyOfSubscribersOrVetolisteners;
    }

    protected boolean subscribe(final Object classTopicOrPatternWrapper,
                                final Map<Object, Object> subscriberMap, final Object subscriber)
    {
        if (classTopicOrPatternWrapper == null)
        {
            throw new IllegalArgumentException("Can't subscribe to null.");
        }

        if (subscriber == null)
        {
            throw new IllegalArgumentException(
                    "Can't subscribe null subscriber to "
                            + classTopicOrPatternWrapper);
        }
        boolean alreadyExists = false;
        Object realSubscriber = subscriber;
        boolean isWeakRef = subscriber instanceof WeakReference;
        if (isWeakRef)
        {
            realSubscriber = ((WeakReference) subscriber).get();
        }

        if (realSubscriber == null)
        {
            return false;// already garbage collected? Weird.
        }
        synchronized (listenerLock)
        {
            List currentSubscribers = (List) subscriberMap
                    .get(classTopicOrPatternWrapper);
            if (currentSubscribers == null)
            {

                currentSubscribers = new ArrayList();
                subscriberMap.put(classTopicOrPatternWrapper,
                        currentSubscribers);
            }
            else
            {
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
                        .hasNext();)
                {
                    Object currentSubscriber = iterator.next();
                    Object realCurrentSubscriber = getRealSubscriberAndCleanStaleSubscriberIfNecessary(
                            iterator, currentSubscriber);
                    if (realSubscriber.equals(realCurrentSubscriber))
                    {
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

    protected Object getRealSubscriberAndCleanStaleSubscriberIfNecessary(
            Iterator iterator, Object existingSubscriber)
    {
        if (existingSubscriber instanceof WeakReference)
        {
            existingSubscriber = ((WeakReference) existingSubscriber).get();
            if (existingSubscriber == null)
            {
                iterator.remove();
                // decWeakRefPlusProxySubscriberCount();
            }
        }

        return existingSubscriber;
    }

    protected boolean unsubscribe(Object o, Map subscriberMap, Object subscriber)
    {

        if (o == null)
        {
            throw new IllegalArgumentException("Can't unsubscribe to null.");
        }
        if (subscriber == null)
        {
            throw new IllegalArgumentException(
                    "Can't unsubscribe null subscriber to " + o);
        }

        synchronized (listenerLock)
        {
            return removeFromSetResolveWeakReferences(subscriberMap, o,
                    subscriber);
        }
    }

    private boolean removeFromSetResolveWeakReferences(Map map, Object key,
                                                       Object toRemove)
    {
        List subscribers = (List) map.get(key);
        if (subscribers == null)
        {
            return false;
        }
        if (subscribers.remove(toRemove))
        {
            if (toRemove instanceof WeakReference)
            {
                // decWeakRefPlusProxySubscriberCount();
            }
            return true;
        }

        // search for WeakReferences and ProxySubscribers
        for (Iterator iter = subscribers.iterator(); iter.hasNext();)
        {
            Object existingSubscriber = iter.next();

            if (existingSubscriber instanceof WeakReference)
            {
                WeakReference wr = (WeakReference) existingSubscriber;
                Object realRef = wr.get();
                if (realRef == null)
                {
                    // clean up a garbage collected reference
                    iter.remove();
                    // decWeakRefPlusProxySubscriberCount();
                    return true;
                }
                else if (realRef == toRemove)
                {
                    iter.remove();
                    // decWeakRefPlusProxySubscriberCount();
                    return true;
                }
            }
        }
        return false;
    }


    protected void publish(final Object event, final List subscribers)
    {
        if (event == null )
        {
            throw new IllegalArgumentException(
                    "Can't publish to null event.");
        }

        if (subscribers == null || subscribers.isEmpty())
        {

            return;
        }

        for (int i = 0; i < subscribers.size(); i++)
        {
            Object eh = subscribers.get(i);
            if (event != null)
            {
                Subscriber eventSubscriber = (Subscriber) eh;
                try
                {
                    eventSubscriber.onEvent(event);
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    class PublishRunnable implements Runnable
    {
        Object theEvent;
        List theSubscribers;
        public PublishRunnable(final Object event, final List subscribers)
        {
            this.theEvent = event;
            this.theSubscribers = subscribers;
        }

        @Override
        public void run()
        {
            publish(theEvent,theSubscribers);
        }
    }
}
