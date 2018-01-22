package easyjs.com.easyjs.engine.service;


import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

import easyjs.com.easyjs.engine.R;
import easyjs.com.easyjs.engine.service.handler.IEventHandler;

/**
 * Created by faith on 2018/1/16.
 */

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    private Map<String, IEventHandler> eventListeners = new HashMap<>();
    private static AccessibilityService instance;
    private static final String TAG = "AccessibilityService";
    FrameLayout mLayout;
    public void addEvent(String event, IEventHandler eventHandler) {
        eventListeners.put(event, eventHandler);
    }

    public static AccessibilityService getInstance() {
        return instance;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
//        accessibilityEvent.getEventType();
        Log.d(TAG, "v:" + accessibilityEvent);
    }

    @Override
    public void onInterrupt() {

    }


    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }


    @Override
    protected void onServiceConnected() {
//        Log.v(TAG, "onServiceConnected: " + getServiceInfo().toString());
        instance = this;
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.action_bar, mLayout);
        wm.addView(mLayout, lp);
        configureSwipeButton();
    }

    private void configureSwipeButton() {
        Button swipeButton = (Button) mLayout.findViewById(R.id.swipe);
        swipeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Path swipePath = new Path();
                swipePath.moveTo(1000, 1000);
                swipePath.lineTo(100, 1000);
                GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 500));
                dispatchGesture(gestureBuilder.build(), null, null);
            }
        });
    }
}
