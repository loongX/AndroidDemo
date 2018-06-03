
package com.rdm.base.thread;

import com.rdm.base.annotation.PluginApi;
import com.rdm.base.annotation.PluginVersionCodes;

@PluginApi(since = 4)
public interface Future<T> {
    @PluginApi(since = 4)
    public void cancel();

    @PluginApi(since = 4)
    public boolean isCancelled();

    @PluginApi(since = 4)
    public boolean isDone();

    @PluginApi(since = 4)
    public T get();

    @PluginApi(since = 4)
    public void waitDone();
    @PluginApi(since = PluginVersionCodes.EUTERPE_2_4)
    void setCancelListener(CancelListener listener);

    @PluginApi(since = PluginVersionCodes.EUTERPE_2_4)
    public static interface CancelListener {
    	@PluginApi(since = PluginVersionCodes.EUTERPE_2_4)
        public void onCancel();
    }
}
