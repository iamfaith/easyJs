package easyjs.com.easyjs;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import easyjs.com.BuildConfig;
import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.droidcommon.IAppConfig;
import easyjs.com.easyjs.droidcommon.util.AndroidUtil;
import easyjs.com.easyjs.droidcommon.util.ScreenMetrics;
import easyjs.com.easyjs.service.AccessibilityService;

/**
 * Created by faith on 2018/1/18.
 */

public class EasyJs {
    private final AndroidUtil appUtils;
    private final Context context;

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private EasyJs(final Context context) {
        this.context = context;
        appUtils = new AndroidUtil(context);
        init();
    }

    private static EasyJs instance;

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static EasyJs getIntance(Context context) {
        if (instance == null)
            instance = new EasyJs(context);
        return instance;
    }

    public AndroidUtil getAppUtils() {
        return appUtils;
    }

    public void ensureAccessibilityServiceEnabled() {
        if (!appUtils.isAccessibilityServiceEnabled(this.context, AccessibilityService.class)) {
            appUtils.goToAccessibilitySetting(this.context);
        }
    }

    protected Application getApplication() {
        return App.getApp();
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void init() {
        Define.appConfig = () -> BuildConfig.appSinature;
        registerActivityLifecycleCallbacks();
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
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
