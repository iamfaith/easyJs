package easyjs.com.easyjs.engine.service;


import android.view.accessibility.AccessibilityEvent;

import java.util.HashMap;
import java.util.Map;

import easyjs.com.easyjs.engine.service.handler.IEventHandler;

/**
 * Created by faith on 2018/1/16.
 */

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    private Map<String, IEventHandler> eventListeners = new HashMap<>();

    public void addEvent(String event, IEventHandler eventHandler) {
        eventListeners.put(event, eventHandler);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        accessibilityEvent.getEventType();
    }

    @Override
    public void onInterrupt() {

    }
}
