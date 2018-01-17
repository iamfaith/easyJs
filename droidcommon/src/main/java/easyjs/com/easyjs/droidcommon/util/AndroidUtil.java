package easyjs.com.easyjs.droidcommon.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by faith on 2018/1/18.
 */

public class AndroidUtil {

    private Context context;
    private volatile WeakReference<Activity> currentActivity = new WeakReference<>(null);

    public AndroidUtil(Context mContext) {
        this.context = mContext;
    }

    public Activity getCurrentActivity() {
        Log.d("App", "getCurrentActivity: " + currentActivity.get());
        return currentActivity.get();
    }


    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = new WeakReference<>(currentActivity);
        Log.d("App", "setCurrentActivity: " + currentActivity);
    }

    public static boolean checkSelfPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }
}
