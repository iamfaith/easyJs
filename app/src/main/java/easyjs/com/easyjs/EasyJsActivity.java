package easyjs.com.easyjs;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableFloatyWindow;
import com.stardust.enhancedfloaty.util.FloatingWindowPermissionUtil;

import easyjs.com.easyjs.droidcommon.BaseActivity;
import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.floaty.SampleFloaty;
import easyjs.com.easyjs.droidcommon.proxy.ProxyService;


public class EasyJsActivity extends BaseActivity implements View.OnClickListener {

    private Button btn1;
    private Button btn2;
    private SampleFloaty floatyWindow;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_js);
        initView();
        FloatingWindowPermissionUtil.goToFloatingWindowPermissionSettingIfNeeded(this);
        startService(new Intent(this, FloatyService.class));
        floatyWindow = new SampleFloaty("test", getApplicationContext());
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
                ProxyService.getInstance().startProxy(this, new ProxyService.OnProxyListener() {
                    @Override
                    public void OnProxyStartFail(Define.CallBackMsg errMsg) {
                        Toast.makeText(EasyJsActivity.this, errMsg.msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnProxyStartSuccess() {
                        ProxyService.getInstance().forDebugger();
                        Log.d("ProxyService", "proxy success");

                        // Toast.makeText(EasyJsActivity.this, "proxy success", Toast.LENGTH_SHORT).show();

                    }
                });

                break;
            case R.id.button2:
                FloatyService.addWindow(new ResizableFloatyWindow(floatyWindow));
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (floatyWindow.getScreenCapturer() != null) {
            floatyWindow.getScreenCapturer().onActivityResult(requestCode, resultCode, data);
        }
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
