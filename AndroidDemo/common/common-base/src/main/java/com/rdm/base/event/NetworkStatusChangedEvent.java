package com.rdm.base.event;

import com.rdm.base.app.BaseApp;

/**
 *
 * 网络变化事件。
 * Created by Rao on 2015/1/10.
 */
public class NetworkStatusChangedEvent {

    final private BaseApp.NetworkStatus mStatus;

    public NetworkStatusChangedEvent(BaseApp.NetworkStatus status) {
        mStatus = status;
    }

    public BaseApp.NetworkStatus getStatus() {
        return mStatus;
    }
}
