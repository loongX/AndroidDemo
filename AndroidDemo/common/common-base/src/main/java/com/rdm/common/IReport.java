package com.rdm.common;

import android.app.Activity;

import java.util.Map;
import java.util.Properties;

/**
 * 事件上报。
 * @author lokierao
 *
 */
public class IReport {

    private static Delegate gDelegate = null;

    public static void set(Delegate report) {
        gDelegate = report;
    }

    public static void onEvent(String eventId) {
        if (gDelegate != null) {
            gDelegate.onEvent(eventId, null);
        }
    }

    /**
     * 上报事件。
     * @param eventId
     * @param paramas
     */
    public static void onEvent(String eventId, Properties paramas) {
        if (gDelegate != null) {
            gDelegate.onEvent(eventId, paramas);
        }
    }

    /**
     * 上报时长事件。
     * @param eventId
     * @param paramas
     */
    public static void onPageStart(String eventId,Properties prop) {
        if (gDelegate != null) {
            gDelegate.onPageStart(eventId,prop);
        }
    }

    public static void onResume(Activity activity) {
        if (gDelegate != null) {
            gDelegate.onResume(activity);
        }
    }


    public static void onPause(Activity activity) {
        if (gDelegate != null) {
            gDelegate.onPause(activity);
        }
    }



    /**
     *  上报时长事件。
     * @param eventId
     * @param paramas
     */
    public static void onPageEnd(String eventId,Properties prop) {
        if (gDelegate != null) {
            gDelegate.onPageEnd(eventId,prop);
        }
    }

    /**
     * 上报值事件。
     * @param eventId
     * @param paramas
     * @param value
     */
    public static void onEventValue(String eventId,Properties prop, int value) {
        if (gDelegate != null) {
            gDelegate.onEventValue(eventId, prop, value);
        }
    }

    public static interface Delegate {

        void onEvent(String eventId, Properties paramas);

        void onPageStart(String eventId,Properties prop);

        void onPageEnd(String eventId,Properties prop);

        void onEventValue(String eventId, Properties prop, int value);

        void onPause(Activity activity);

        void onResume(Activity activity);
    }
}
