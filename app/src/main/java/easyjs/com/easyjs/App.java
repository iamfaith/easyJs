package easyjs.com.easyjs;

import android.app.Application;

import easyjs.com.easyjs.droidcommon.util.AndroidUtil;

/**
 * Created by faith on 2018/1/18.
 */

public class App extends Application {
    private static App app;
    private AndroidUtil util;
    public static App getApp() {
        return app;
    }
    public AndroidUtil getUtil() {
        return util;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        EasyJs easyJs = EasyJs.getIntance(this);
        util = easyJs.getAppUtils();
    }
}
