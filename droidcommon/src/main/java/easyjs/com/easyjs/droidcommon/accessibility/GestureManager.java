package easyjs.com.easyjs.droidcommon.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.view.ViewConfiguration;

import java.util.concurrent.atomic.AtomicBoolean;

import easyjs.com.easyjs.droidcommon.util.ScreenMetrics;

/**
 * Created by faith on 2018/1/22.
 */

public class GestureManager {

    private AccessibilityService service;
    private ScreenMetrics screenMetrics = new ScreenMetrics();

    public AccessibilityService getService() {
        return service;
    }

    public void setService(AccessibilityService service) {
        this.service = service;
    }

    public ScreenMetrics getScreenMetrics() {
        return screenMetrics;
    }

    public void setScreenMetrics(ScreenMetrics screenMetrics) {
        this.screenMetrics = screenMetrics;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean click(int x, int y) {
        return press(x, y, ViewConfiguration.getTapTimeout() + 50);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean press(int x, int y, int delay) {
        return gesture(0, delay, new int[]{x, y});
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean gesture(long start, long duration, int[]... points) {
        Path path = pointsToPath(points);
        return gestures(new GestureDescription.StrokeDescription(path, start, duration));
    }

    private Path pointsToPath(int[][] points) {
        Path path = new Path();
        path.moveTo(scaleX(points[0][0]), scaleY(points[0][1]));
        for (int i = 1; i < points.length; i++) {
            int[] point = points[i];
            path.lineTo(scaleX(point[0]), scaleY(point[1]));
        }
        return path;
    }

    private int scaleX(int x) {
        if (screenMetrics == null)
            return x;
        return screenMetrics.scaleX(x);
    }

    private int scaleY(int y) {
        if (screenMetrics == null)
            return y;
        return screenMetrics.scaleY(y);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean gestures(GestureDescription.StrokeDescription... strokes) {
        if (service == null)
            return false;
        GestureDescription.Builder builder = new GestureDescription.Builder();
        for (GestureDescription.StrokeDescription stroke : strokes) {
            builder.addStroke(stroke);
        }
//        if (mHandler == null) {
            return gesturesWithoutHandler(builder.build());
//        } else {
//            return gesturesWithHandler(builder.build());
//        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private boolean gesturesWithHandler(GestureDescription description) {
//        final VolatileDispose<Boolean> result = new VolatileDispose<>();
//        service.dispatchGesture(description, new AccessibilityService.GestureResultCallback() {
//            @Override
//            public void onCompleted(GestureDescription gestureDescription) {
//                result.setAndNotify(true);
//            }
//
//            @Override
//            public void onCancelled(GestureDescription gestureDescription) {
//                result.setAndNotify(false);
//            }
//        }, mHandler);
//        return result.blockedGet();
//    }

    private void prepareLooperIfNeeded() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
    }


    private void quitLoop() {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            looper.quit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean gesturesWithoutHandler(GestureDescription description) {
        prepareLooperIfNeeded();
        final AtomicBoolean result = new AtomicBoolean(false);
        Handler handler = new Handler(Looper.myLooper());
        service.dispatchGesture(description, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                result.set(true);
//                quitLoop();
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                result.set(false);
//                quitLoop();
            }
        }, handler);
        return result.get();
    }

}
