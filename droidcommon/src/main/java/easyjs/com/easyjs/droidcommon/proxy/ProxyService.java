package easyjs.com.easyjs.droidcommon.proxy;

import android.Manifest;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;
import net.lightbody.bmp.proxy.CaptureType;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import easyjs.com.easyjs.droidcommon.BaseActivity;
import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.droidcommon.permission.PermissionManager;

/**
 * Created by faith on 2018/1/18.
 */

public class ProxyService {

    private BrowserMobProxy proxy;
    public static int proxyPort = 8888;
    private boolean isProxyRunning = false;
    private AtomicBoolean isStarting = new AtomicBoolean(false);
    private OnProxyListener listener;
    private static ProxyService instance;

    public interface OnProxyListener {
        void OnProxyStartFail(Define.CallBackMsg errMsg);

        void OnProxyStartSuccess();
    }

    private ProxyService() {

    }

    public void startProxy(final @NonNull BaseActivity activity, OnProxyListener listener) {
        this.listener = listener;
        String[] needPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionManager.requestPermission(activity, needPermissions, (eventCode, callBackMsg) -> {
            if (eventCode != Define.EventCode.SUCCESS) {
                this.listener.OnProxyStartFail(callBackMsg);
            } else {
                File dirFile = new File(Environment.getExternalStorageDirectory() + "/har");
                if (dirFile.exists())
                    for (File file : dirFile.listFiles()) {
                        file.delete();
                    }
                else
                    dirFile.mkdir();


                proxy = new BrowserMobProxyServer();
                proxy.setTrustAllServers(true);
                proxy.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.REQUEST_COOKIES,
                        CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_HEADERS, CaptureType.REQUEST_COOKIES,
                        CaptureType.RESPONSE_CONTENT);

                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                        .format(new Date(System.currentTimeMillis()));
                proxy.newHar(time);
                startProxy();
            }
        });
    }

    public static ProxyService getInstance() {
        if (instance == null) {
            instance = new ProxyService();
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Optional<HarLog> getHarLog() {
        return Optional.ofNullable(proxy.getHar().getLog());
    }

    public void startProxy() {
        if (isStarting.compareAndSet(false, true)) {
            Thread thread = new Thread(() -> {
                try {
                    proxy.start(8888);
                    isProxyRunning = true;
                    this.listener.OnProxyStartSuccess();
                } catch (Exception e) {
                    try {
                        // 防止8888已被占用
                        Random rand = new Random();
                        int randNum = rand.nextInt(1000) + 8000;
                        proxyPort = randNum;

                        proxy.start(randNum);
                        isProxyRunning = true;
                        this.listener.OnProxyStartSuccess();
                    } catch (Exception err) {
                        Log.e(ProxyService.class.getName(), "proxy start fail", e);
                    }
                } finally {
                    isStarting.compareAndSet(true, false);
                }
            });
            thread.start();
        }
    }

    public boolean isProxyRunning() {
        return isProxyRunning;
    }

    public void stopProxy() {
        if (proxy != null) {
            proxy.stop();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void forDebugger() {
        Thread t = new Thread(() -> {
            while (true) {
                Optional<HarLog> harLog = getHarLog();
                harLog.ifPresent(harLogs -> {
                    harLogs.getEntries().forEach(harEntry -> {
                        HarRequest harRequest = harEntry.getRequest();
                        HarResponse harResponse = harEntry.getResponse();
                        Log.d("ProxyService", harRequest.getUrl() + "--" + harResponse.getContent().getText());
                    });
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
