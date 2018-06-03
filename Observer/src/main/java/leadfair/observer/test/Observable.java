package leadfair.observer.test;

import android.content.Context;
import android.util.Log;

import leadfair.observer.PullMessage;
import leadfair.observer.impl.AndroidPPObservable;


/**
 * Created by TZT on 2017/8/21.
 */

public class Observable extends AndroidPPObservable<Status> {
    public Observable(Context context) {
        super(context);
    }

    @Override
    public <AT extends Status> boolean notifyMessage(PullMessage<AT> pullMessage) {
        Log.e("Observable", pullMessage.toString());
        return true;
    }
}
