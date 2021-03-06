package easyjs.com.easyjs.droidcommon.util;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.proxy.CaptureType;

import org.apache.commons.io.FileUtils;
import org.littleshoot.proxy.mitm.Authority;
import org.littleshoot.proxy.mitm.CertificateSniffingMitmManager;
import org.littleshoot.proxy.mitm.RootCertificateException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import easyjs.com.common.SecurityUtil;
import easyjs.com.easyjs.droidcommon.BaseActivity;
import easyjs.com.easyjs.droidcommon.BuildConfig;
import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.droidcommon.permission.PermissionManager;

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

//    public static boolean checkSelfPermission(Context context, String permission) {
//        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
//    }


    public boolean cpAssetsFile(String assetsFilePath, final @NonNull File extFile) {
        AssetManager assetManager = context.getAssets();
        try (InputStream inputStream = assetManager.open(assetsFilePath);
             FileOutputStream fileOutputStream = new FileOutputStream(extFile)) {
            byte[] buffer = new byte[1024];
            int size = -1;
            while ((size = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, size);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void cpAsssetsToExtDir(final @NonNull BaseActivity activity, final String assetsFilePath, final @NonNull String extPath, final Define.IEventCallBack callBack) {
        String[] needPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        PermissionManager.requestPermission(activity, needPermissions, (eventCode, callBackMsg) -> {
            if (eventCode == Define.EventCode.SUCCESS) {
                File extFile = new File(Environment.getExternalStorageDirectory() + extPath);
                try {
                    if (!extFile.exists()) {
                        FileUtils.forceMkdir(extFile.getParentFile());
                        extFile.createNewFile();
                    }
                    if (cpAssetsFile(assetsFilePath, extFile)) {
                        callBack.afterExecute(Define.EventCode.SUCCESS, null);
                    } else {
                        callBack.afterExecute(Define.EventCode.FAIL, Define.CallBackMsg.buildMsg("copy error"));
                    }
                } catch (IOException e) {
                    callBack.afterExecute(Define.EventCode.FAIL, Define.CallBackMsg.buildMsg("copy error", e));
                }
            } else {
                callBack.afterExecute(Define.EventCode.FAIL, Define.CallBackMsg.buildMsg("no permissions"));
            }
        });
    }

    public static void goToAccessibilitySetting(Context context) {
        context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }

    private static boolean isDebuggerEnable(Context context) {
        return BuildConfig.DEBUG == false && (android.os.Debug.isDebuggerConnected() || (0 != (context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)));
    }

    public static boolean checkAndExit(Context context) {
        boolean isEnable = isDebuggerEnable(context);
        if (isEnable) {
            exitProcess();
        }
        return isEnable;
    }

    public static void exitProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public static String getSignature(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        StringBuilder sb = new StringBuilder();

        try {
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = pi.signatures;
            for (Signature signature : signatures) {
                sb.append(signature.toCharsString());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String md5 = SecurityUtil.encryptionMD5(sb.toString().getBytes());
        return md5;
    }
}
