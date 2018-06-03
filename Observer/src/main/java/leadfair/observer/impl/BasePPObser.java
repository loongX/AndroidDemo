package leadfair.observer.impl;

import android.content.Context;

/**
 * Created by TZT on 2017/8/21.
 */

abstract class BasePPObser {
    final Context context;
    String pullAction;
    String pushAction;

    BasePPObser(Context context) {
        this.context = context;
        pullAction = "pullAction." + context.getPackageName();
        pushAction = "pushAction." + context.getPackageName();
    }


    public String getPullAction() {
        return pullAction;
    }

    public void setPullAction(String pullAction) {
        this.pullAction = pullAction;
    }

    public String getPushAction() {
        return pushAction;
    }

    public void setPushAction(String pushAction) {
        this.pushAction = pushAction;
    }
}
