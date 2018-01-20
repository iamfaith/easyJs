package easyjs.com.easyjs;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableFloatyWindow;
import com.stardust.enhancedfloaty.util.FloatingWindowPermissionUtil;

import net.lightbody.bmp.core.har.HarEntry;

import java.io.IOException;

import easyjs.com.easyjs.droidcommon.BaseActivity;
import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.droidcommon.proxy.IUriFilter;
import easyjs.com.easyjs.floaty.SampleFloaty;
import easyjs.com.easyjs.droidcommon.proxy.ProxyService;


public class EasyJsActivity extends BaseActivity implements View.OnClickListener {

    private Button btn1;
    private Button btn2;
    private SampleFloaty floatyWindow;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_js);
        initView();
        floatyWindow = new SampleFloaty("test", getApplicationContext());
        ProxyService.getInstance().startProxy(this, new ProxyService.OnProxyListener() {
            @Override
            public void OnProxyStartFail(Define.CallBackMsg errMsg) {
                Toast.makeText(EasyJsActivity.this, errMsg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnCertInstallFail(Define.CallBackMsg errMsg) {
                Log.d("ProxyService", "install cert fail");
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void OnProxyStartSuccess() {
                ProxyService.getInstance().checkProxyData();
                Log.d("ProxyService", "proxy success");
            }

            @Override
            public void OnDataReceive(HarEntry harEntry) {
                String url = harEntry.getRequest().getUrl();
                //问题
                if (url.contains("findQuiz")) {
                    String respStr = harEntry.getResponse().getContent().getText();
                    floatyWindow.updateText(respStr);
                } else {
                    //答案 update database

                }
            }

        }, url -> {
            if (url.contains("https")) {
                Log.d("https", url);
            }
            if (url.contains("https") && url.contains("question.hortor.net")) {
                return true;
            } else
                return false;
        });
        AssetManager assetManager = getApplicationContext().getAssets();
        try {
            String[] dbs = assetManager.list("data");
            for (String db : dbs) {
                Toast.makeText(EasyJsActivity.this, db, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //安装证书
        ProxyService.getInstance().installCert(this, false);
    }

    // 方法：初始化View
    private void initView() {
        btn1 = (Button) findViewById(R.id.button);
        //按钮绑定点击事件的监听器
        btn1.setOnClickListener(this);

        btn2 = (Button) findViewById(R.id.button2);
        //按钮绑定点击事件的监听器
        btn2.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                ProxyService.getInstance().installCert(this, true);
                break;
            case R.id.button2:
                FloatingWindowPermissionUtil.goToFloatingWindowPermissionSettingIfNeeded(this);
                startService(new Intent(this, FloatyService.class));
                FloatyService.addWindow(new ResizableFloatyWindow(floatyWindow));
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (floatyWindow.getScreenCapturer() != null) {
//            floatyWindow.getScreenCapturer().onActivityResult(requestCode, resultCode, data);
//        }
    }

    /**
     * Handle permission here. Like Manifest.permission.WRITE_EXTERNAL_STORAGE
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (floatyWindow.getScreenCapturer() != null) {
//            floatyWindow.getScreenCapturer().onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }


}
