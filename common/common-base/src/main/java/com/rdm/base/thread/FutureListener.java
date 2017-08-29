
package com.rdm.base.thread;

import com.rdm.base.annotation.PluginApi;

public interface FutureListener<T> {
    @PluginApi(since = 4)
    public void onFutureBegin(Future<T> future);

    @PluginApi(since = 4)
    public void onFutureDone(Future<T> future);
}
