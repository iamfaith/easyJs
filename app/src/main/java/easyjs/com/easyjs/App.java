package easyjs.com.easyjs;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

//import com.tencent.tinker.loader.app.ApplicationLike;
//import com.tinkerpatch.sdk.TinkerPatch;
//import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;

//import abc.abc.abc.AdManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import easyjs.com.BuildConfig;
import easyjs.com.easyjs.droidcommon.util.AndroidUtil;

/**
 * Created by faith on 2018/1/18.
 */

public class App extends Application {
    private static App app;
    private EasyJs easyJs;
    private AndroidUtil util;
    private static final String TAG = "Tinker.App";
//    private ApplicationLike tinkerApplicationLike;

    public EasyJs getEasyJs() {
        return easyJs;
    }

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
        easyJs = EasyJs.getIntance(this);
        util = easyJs.getAppUtils();

        try{
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {

        }
//        initTinkerPatch();
//        initAds();
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
    }

    private void initAds() {
        if (BuildConfig.ADS_ENABLE) {
//            AdManager.getInstance(this).init(BuildConfig.appId, BuildConfig.appSecret, true);
        }
    }

    /**
     * 我们需要确保至少对主进程跟patch进程初始化 TinkerPatch
     */
//    private void initTinkerPatch() {
//        // 我们可以从这里获得Tinker加载过程的信息
//        if (BuildConfig.TINKER_ENABLE) {
//            tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
//            // 初始化TinkerPatch SDK
//            TinkerPatch.init(
//                    tinkerApplicationLike
////                new TinkerPatch.Builder(tinkerApplicationLike)
////                    .requestLoader(new OkHttp3Loader())
////                    .build()
//            )
//                    .reflectPatchLibrary()
//                    .setPatchRollbackOnScreenOff(true)
//                    .setPatchRestartOnSrceenOff(true)
//                    .setFetchPatchIntervalByHours(1);
//            // 获取当前的补丁版本
//            Log.d(TAG, "Current patch version is " + TinkerPatch.with().getPatchVersion());
//
//            // fetchPatchUpdateAndPollWithInterval 与 fetchPatchUpdate(false)
//            // 不同的是，会通过handler的方式去轮询
//            TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
//        }
//    }
}
