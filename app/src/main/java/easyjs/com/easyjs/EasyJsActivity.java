package easyjs.com.easyjs;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableFloatyWindow;
import com.stardust.enhancedfloaty.util.FloatingWindowPermissionUtil;

import easyjs.com.easyjs.droidcommon.vpnservice.LocalVPN;
import easyjs.com.easyjs.floaty.SampleFloaty;


public class EasyJsActivity extends Activity implements View.OnClickListener {

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                this.startActivity( new Intent(this, LocalVPN.class));
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
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (floatyWindow.getScreenCapturer() != null) {
            floatyWindow.getScreenCapturer().onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
