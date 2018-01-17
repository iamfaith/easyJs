package easyjs.com.easyjs;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import easyjs.com.easyjs.droidcommon.util.AndroidUtil;
import easyjs.com.easyjs.droidcommon.util.ScreenMetrics;

/**
 * Created by faith on 2018/1/18.
 */

public class EasyJs {
    private final AndroidUtil appUtils;
    private final Context context;

    private EasyJs(final Context context) {
        this.context = context;
        appUtils = new AndroidUtil(context);
        init();
    }

    private static EasyJs instance;

    public static EasyJs getIntance(Context context) {
        if (instance == null)
            instance = new EasyJs(context);
        return instance;
    }

    public AndroidUtil getAppUtils() {
        return appUtils;
    }

    protected Application getApplication() {
        return App.getApp();
    }

    protected void init() {
        registerActivityLifecycleCallbacks();
    }
    protected void registerActivityLifecycleCallbacks() {
        getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ScreenMetrics.initIfNeeded(activity);
                appUtils.setCurrentActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {
                appUtils.setCurrentActivity(null);
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                appUtils.setCurrentActivity(activity);
            }
        });
    }
}
