package leadfair.observer.impl;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import leadfair.observer.PPObservable;
import leadfair.observer.PPObserver;
import leadfair.observer.PullMessage;
import leadfair.observer.Puller;
import leadfair.observer.PushMessage;
import leadfair.observer.Role;

/**
 * Created by TZT on 2017/8/21.
 */
public abstract class AndroidPPObservable<T> extends BasePPObser implements PPObservable<T> {

    private volatile T cacheAnyThing;//?
    private final BroadcastReceiver pushReceiver;

    public AndroidPPObservable(Context context) {
        super(context);
        pushReceiver = new InnerReceiver();
        IntentFilter intentFilter = new IntentFilter(pullAction);
        context.registerReceiver(pushReceiver, intentFilter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Puller subscibe(PPObserver ppObserver) {
        throw new RuntimeException("not support operation!!!");
    }

    @Override
    public <S extends T> void pushMessage(PushMessage<S> pushMessage) {
        this.cacheAnyThing = pushMessage.getData();
        Intent intent = new Intent();
        intent.putExtra("data", pushMessage);
        intent.setAction(pushAction);
        context.sendBroadcast(intent);
    }

    @Override
    public <S extends T> void push(S anyThing) {
        PushMessage<S> pushMessage = new PushMessage<>();
        pushMessage.setData(anyThing);
        pushMessage.setRole(Role.ALL);
        pushMessage(pushMessage);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends T> S cacheLastPushThing() {
        return (S) cacheAnyThing;
    }

    @Override
    public void close() {
        context.unregisterReceiver(pushReceiver);
    }

    class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(pullAction)) {
                PullMessage<? extends T> pullMessage = intent.getParcelableExtra("data");
                if (pullMessage.isPull()) {
                    PushMessage<T> pushMessage = new PushMessage<>();
                    pushMessage.setPull(true);
                    if (TextUtils.isEmpty(pullMessage.getGroupName())) {
                        pushMessage.setRole(Role.PRIVATE);
                        pushMessage.setPayload(pullMessage.getObserverUniqueId());
                    } else {
                        pushMessage.setRole(Role.GROUP);
                        pushMessage.setPayload(pullMessage.getGroupName());
                    }
                    pushMessage.setData(cacheLastPushThing());
                    pushMessage(pushMessage);
                } else {
                    notifyMessage(pullMessage);
                }
            }
        }
    }
}
