package easyjs.com.easyjs.droidcommon;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import easyjs.com.easyjs.droidcommon.screencapture.ScreenCapture;

/**
 * Created by faith on 2018/1/18.
 */

public abstract class BaseActivity extends Activity {

    protected Map<Integer, Define.IEventListener> callbacks = new HashMap<>();

    public void registerCallback(Integer code, Define.IEventListener callback) {
        callbacks.put(code, callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Define.IEventListener callback = callbacks.get(requestCode);
        if (callback != null) {
            boolean hasPermission = true;
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        hasPermission = false;
                    }
                }
            } else
                hasPermission = false;
            if (!hasPermission)
                callback.handleEvent(Define.EventCode.CANCEL, Define.CallBackMsg.buildMsg(getString(R.string.user_cancel)));
            else {
                callback.handleEvent(Define.EventCode.SUCCESS, null);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Define.IEventListener callback = callbacks.get(requestCode);
        if (callback != null)
            if (resultCode == Activity.RESULT_OK) {
                callback.handleEvent(Define.EventCode.SUCCESS, Define.CallBackMsg.buildPayload(resultCode, data));
            } else {
                callback.handleEvent(Define.EventCode.CANCEL, Define.CallBackMsg.buildMsg(getString(R.string.user_cancel)));
            }
    }
}
