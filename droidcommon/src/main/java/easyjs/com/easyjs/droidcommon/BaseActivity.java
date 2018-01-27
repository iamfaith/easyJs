package easyjs.com.easyjs.droidcommon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import easyjs.com.easyjs.droidcommon.screencapture.ScreenCapture;

/**
 * Created by faith on 2018/1/18.
 */

public abstract class BaseActivity extends Activity {

    protected Context context;

    protected Map<Integer, Define.IEventListener> callbacks = new HashMap<>();

    protected abstract String getTAG();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    public void registerCallback(Integer code, Define.IEventListener callback) {
        callbacks.put(code, callback);
    }

    static {
        System.loadLibrary("SignatureLib");
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


    /**
     * 打印调试级别日志
     *
     * @param format
     * @param args
     */
    protected void logDebug(String format, Object... args) {
        logMessage(Log.DEBUG, format, args);
    }

    /**
     * 打印信息级别日志
     *
     * @param format
     * @param args
     */
    protected void logInfo(String format, Object... args) {
        logMessage(Log.INFO, format, args);
    }

    /**
     * 打印错误级别日志
     *
     * @param format
     * @param args
     */
    protected void logError(String format, Object... args) {
        logMessage(Log.ERROR, format, args);
    }

    /**
     * 展示短时Toast
     *
     * @param format
     * @param args
     */
    protected void showShortToast(String format, Object... args) {
        showToast(Toast.LENGTH_SHORT, format, args);
    }

    /**
     * 展示长时Toast
     *
     * @param format
     * @param args
     */
    protected void showLongToast(String format, Object... args) {
        showToast(Toast.LENGTH_LONG, format, args);
    }

    /**
     * 打印日志
     *
     * @param level
     * @param format
     * @param args
     */
    private void logMessage(int level, String format, Object... args) {
        String formattedString = String.format(format, args);
        switch (level) {
            case Log.DEBUG:
                Log.d(getTAG(), formattedString);
                break;
            case Log.INFO:
                Log.i(getTAG(), formattedString);
                break;
            case Log.ERROR:
                Log.e(getTAG(), formattedString);
                break;
        }
    }

    /**
     * 展示Toast
     *
     * @param duration
     * @param format
     * @param args
     */
    private void showToast(int duration, String format, Object... args) {
        Toast.makeText(context, String.format(format, args), duration).show();
    }
}
