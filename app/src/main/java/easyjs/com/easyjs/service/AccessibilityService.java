package easyjs.com.easyjs.service;


import android.accessibilityservice.GestureDescription;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private static AtomicBoolean isAuto = new AtomicBoolean(false);
    private static AtomicBoolean isFininsh = new AtomicBoolean(false);
    private Timer timer;
    private TimerTask task;

    ScreenCapturer screenCapturer;
    TextView textView;
    Button pressBtn;
    private static Random RANDOM = new Random();

    public static AccessibilityService getInstance() {
        return instance;
    }

    private static final int PRESS_BTN = 1;
    private static final int AUTO_RUN = 2;

    private final Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PRESS_BTN) {
                Bundle list = msg.getData();
                GestureManager gestureManager = new GestureManager();
                gestureManager.setService(AccessibilityService.getInstance());
                gestureManager.press(list.getInt("x"), list.getInt("y"), list.getInt("delay"));
            } else if (msg.what == AUTO_RUN) {
                if (isAuto.get() == false) {
                    return;
                }
                if (isFininsh.get() == true) {
                    pressBtn.performClick();
                }

            }
        }
    };

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
        textView = mLayout.findViewById(R.id.log);
        pressBtn = mLayout.findViewById(R.id.press);
        configureSwipeButton();
        configurePressButton();
        Button autoBtn = mLayout.findViewById(R.id.auto);
        autoBtn.setOnClickListener(view -> {
            if (isAuto.get() == false) {
                autoBtn.setText(getResources().getString(R.string.stop));
                isAuto.set(true);

                task = new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = AUTO_RUN;
                        handler.sendMessage(msg);
                    }
                };
                timer = new Timer();
                timer.schedule(task, 0, 6000);
            } else {
                autoBtn.setText(getResources().getString(R.string.auto));
                isAuto.set(false);

                timer.cancel();
                task.cancel();
            }
        });
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
                        try {
                            StringBuffer log = new StringBuffer(64);
                            List<Integer> list = Hack.calPos(bitmap, log);
//                            textView.setText(log.toString());
                            if (list.size() >= 3) {
                                Message msg = new Message();
                                msg.what = PRESS_BTN;
                                Bundle data = new Bundle();
                                data.putInt("x", list.get(1));
                                data.putInt("y", list.get(2));
                                data.putInt("delay", list.get(0));
                                msg.setData(data);
                                handler.sendMessage(msg);
                            }
                        } catch (Exception e) {

                        } finally {
                            isFininsh.set(true);
                        }
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
        pressBtn.setOnClickListener(view -> {
            try {
                if (screenCapturer != null) {
                    screenCapturer.screenCapture();
                }
            } catch (Exception e) {

            }
        });
    }
}
