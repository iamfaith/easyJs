package easyjs.com.easyjs.service;


import android.accessibilityservice.GestureDescription;
import android.graphics.Bitmap;
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
import android.widget.TextView;


import java.util.List;

import easyjs.com.easyjs.App;
import easyjs.com.easyjs.EasyJs;
import easyjs.com.easyjs.R;
import easyjs.com.easyjs.application.wechatjump.Hack;
import easyjs.com.easyjs.droidcommon.BaseActivity;
import easyjs.com.easyjs.droidcommon.accessibility.GestureManager;
import easyjs.com.easyjs.droidcommon.screencapture.ScreenCapturer;

/**
 * Created by faith on 2018/1/16.
 */

@RequiresApi(api = Build.VERSION_CODES.DONUT)
public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    private static AccessibilityService instance;
    private static final String TAG = "AccessibilityService";
    FrameLayout mLayout;

    ScreenCapturer screenCapturer;
    TextView textView;
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        lp.gravity = Gravity.BOTTOM;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.action_bar, mLayout);
        wm.addView(mLayout, lp);
        configureSwipeButton();
        configurePressButton();
        textView = mLayout.findViewById(R.id.log);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configureSwipeButton() {
        Button swipeButton = (Button) mLayout.findViewById(R.id.swipe);
        swipeButton.setOnClickListener(view -> {
            screenCapturer = ScreenCapturer.newInstance((BaseActivity) App.getApp().getUtil().getCurrentActivity());
            screenCapturer.setCaptureListener(new ScreenCapturer.OnCaptureListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onScreenCaptureSuccess(Bitmap bitmap, String savePath) {
                    if (savePath != null) {
                        Thread thread = new Thread(() -> {
                            StringBuffer log = new StringBuffer(64);
                            List<Integer> list = Hack.calPos(bitmap, log);
//                            textView.setText(log.toString());
                            if (list.size() >= 3) {
                                GestureManager gestureManager = new GestureManager();
                                gestureManager.setService(AccessibilityService.getInstance());
                                gestureManager.press(list.get(1), list.get(2), list.get(0));
                            }
                        });
                        thread.start();
                    }
                }

                @Override
                public void onScreenCaptureFailed(String errorMsg) {
                }

                @Override
                public void onScreenRecordStart() {
                }

                @Override
                public void onScreenRecordStop() {
                }

                @Override
                public void onScreenRecordSuccess(String savePath) {
                }

                @Override
                public void onScreenRecordFailed(String errorMsg) {
                }
            });
            screenCapturer.setImagePath("/sdcard", "1.png");

//                Path swipePath = new Path();
//                swipePath.moveTo(1000, 1000);
//                swipePath.lineTo(100, 1000);
//                GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
//                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 500));
//                dispatchGesture(gestureBuilder.build(), null, null);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configurePressButton() {
        Button pressBtn = (Button) mLayout.findViewById(R.id.press);
        pressBtn.setOnClickListener(view -> {
            if (screenCapturer != null) {
                screenCapturer.screenCapture();
            }
        });
    }
}
