package easyjs.com.easyjs;

import android.app.Application;

/**
 * Created by faith on 2018/1/18.
 */

public class App extends Application {
    private static App app;

    public static App getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        EasyJs.getIntance(this);
    }
}
