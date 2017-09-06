package com.example.basedemo.annotation.diy;

import android.app.Activity;
import android.content.Context;

import android.view.View;



/**
 * Author: wyouflf
 * Date: 13-9-9
 * Time: 下午12:29
 */
public class ViewFinder {


    private Activity activity;




    public ViewFinder(Activity activity) {
        this.activity = activity;
    }



    public View findViewById(int id) {
        return  activity.findViewById(id);
    }

    public View findViewById(int id, int pid) {
        View pView = null;
        if (pid > 0) {
            pView = this.findViewById(pid);
        }

        View view = null;
        if (pView != null) {
            view = pView.findViewById(id);
        } else {
            view = this.activity.findViewById(id);
        }
        return view;
    }


    public Context getContext() {
        if (activity != null) return activity;
        return null;
    }
}
