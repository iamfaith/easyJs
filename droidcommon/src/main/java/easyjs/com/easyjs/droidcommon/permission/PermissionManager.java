package easyjs.com.easyjs.droidcommon.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import easyjs.com.easyjs.droidcommon.BaseActivity;
import easyjs.com.easyjs.droidcommon.Define;

/**
 * Created by faith on 2018/1/18.
 */

public class PermissionManager {

    /**
     * if REQUEST_CODE == 0 indicate that needs no permissions
     * String[] needPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE};
     *
     * @param activity
     * @return
     */
    public static int requestPermission(final @NonNull BaseActivity activity, String[] needPermissions, Define.IEventListener callback) {
        int REQUEST_CODE = 0;
        List<String> listPermissions = new ArrayList<>();
        for (String permisstion : needPermissions) {
            if (ActivityCompat.checkSelfPermission(activity, permisstion) == PackageManager.PERMISSION_DENIED) {
                listPermissions.add(permisstion);
            }
        }
        if (listPermissions.size() > 0) {
            String[] requestPermission = new String[listPermissions.size()];
            listPermissions.toArray(requestPermission);
            REQUEST_CODE = Define.RequestCode.getRequestCode(requestPermission);
            activity.registerCallback(REQUEST_CODE, callback);
            ActivityCompat.requestPermissions(activity, requestPermission, REQUEST_CODE);
        } else {
            callback.handleEvent(Define.EventCode.SUCCESS,null);
        }
        return REQUEST_CODE;
    }
}
