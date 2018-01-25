package easyjs.com.easyjs.common;

import easyjs.com.easyjs.App;
import easyjs.com.easyjs.BuildConfig;

/**
 * Created by faith on 2018/1/26.
 */

public class SignatureChecker {

    public static boolean check() {
        if (BuildConfig.DEBUG == false && !BuildConfig.appSinature.equals(App.getApp().getUtil().getSignature())) {
            App.getApp().getUtil().exitProcess();
            return false;
        }
        return true;
    }
}
