package leadfair.observer.impl;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import leadfair.observer.PPObserver;
import leadfair.observer.PullMessage;
import leadfair.observer.PushMessage;
import leadfair.observer.Role;

/**
 * Created by TZT on 2017/8/21.
 */

public abstract class AndroidPPObserver<T> extends BasePPObser implements PPObserver<T> {
    private final String observerUniqueId;
    private final BroadcastReceiver pullReceiver;
    private final Object syncLock = new Object();
    private String groupName;
    private volatile T data;

    public AndroidPPObserver(Context context, String observerUniqueId) {
        super(context);
        this.observerUniqueId = observerUniqueId;
        pullReceiver = new InnerReceiver();
        context.registerReceiver(pullReceiver, new IntentFilter(pushAction));
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <S extends T> S pull() {
        Intent intent = new Intent(pullAction);
        PullMessage<T> pullMessage = new PullMessage<>(observerUniqueId);
        pullMessage.setPull(true);
        intent.putExtra("data", pullMessage);
        context.sendBroadcast(intent);
        synchronized (syncLock) {
            try {
                syncLock.wait(2000);
                return (S) data;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public <S extends T> boolean pull(S pullData) {
        Intent intent = new Intent(pullAction);
        PullMessage<S> pullMessage = new PullMessage<>(observerUniqueId);
        pullMessage.setData(pullData);
        intent.putExtra("data", pullMessage);
        context.sendBroadcast(intent);
        return true;
    }

    public void close() {
        context.unregisterReceiver(pullReceiver);
    }


    class InnerReceiver extends BroadcastReceiver {
        @Override
        @SuppressWarnings("unchecked")
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(pushAction)) {
                PushMessage<? extends T> initMessage = intent.getParcelableExtra("data");
                if (initMessage.isPull()) {
                    data = initMessage.getData();
                    synchronized (syncLock) {
                        syncLock.notifyAll();
                    }
                    return;
                }
                switch (initMessage.getRole()) {
                    case Role.ALL:
                        push(initMessage.getData());
                        break;
                    case Role.GROUP:
                        if (TextUtils.equals(groupName, initMessage.getPayload())) {
                            push(initMessage.getData());
                        }
                        break;
                    case Role.PRIVATE:
                        if (TextUtils.equals(observerUniqueId, initMessage.getPayload())) {
                            push(initMessage.getData());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
