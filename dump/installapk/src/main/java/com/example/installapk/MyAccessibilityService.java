package com.example.installapk;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class MyAccessibilityService extends AccessibilityService{

    private static final String TAG = "[TAG]";
    private Map<Integer, Boolean> handleMap = new HashMap<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo nodeInfo = accessibilityEvent.getSource();
        if (nodeInfo != null) {
            int eventType = accessibilityEvent.getEventType();
            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (handleMap.get(accessibilityEvent.getWindowId()) == null) {
                    boolean handled = iterateNodesAndHandle(nodeInfo);
                    if (handled) {
                        handleMap.put(accessibilityEvent.getWindowId(), true);
                    }
                }
            }

        }
    }
    //遍历节点，模拟点击安装按钮
    private boolean iterateNodesAndHandle(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            int childCount = nodeInfo.getChildCount();
            Log.e("getName", "name:"+ nodeInfo.getClassName());
            if ("android.widget.Button".equals(nodeInfo.getClassName())) {
                String nodeCotent = nodeInfo.getText().toString();
                Log.e(TAG, "content is: " + nodeCotent);
                if ("安装".equals(nodeCotent) || "打开".equals(nodeCotent) || "确定".equals(nodeCotent) ) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            } else if("android.widget.CheckedTextView".equals(nodeInfo.getClassName())){
                String nodeCotent = nodeInfo.getText().toString();
                Log.e(TAG, "content is: " + nodeCotent);
                if ("设备内存".equals(nodeCotent)){
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            } else if("android.widget.TextView".equals(nodeInfo.getClassName())){
                String nodeCotent = nodeInfo.getText().toString();
                Log.e(TAG, "content is: " + nodeCotent);
            }else if ("android.widget.ScrollView".equals(nodeInfo.getClassName())) {   //遇到ScrollView的时候模拟滑动一下
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                if (iterateNodesAndHandle(childNodeInfo)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void onInterrupt() {

    }
}
