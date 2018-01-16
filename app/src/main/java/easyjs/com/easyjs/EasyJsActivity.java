package easyjs.com.easyjs;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.droidcommon.screencapture.ScreenCapture;
import easyjs.com.easyjs.droidcommon.screencapture.ScreenCaptureRequestActivity;
import easyjs.com.easyjs.droidcommon.util.ScreenMetrics;

public class EasyJsActivity extends Activity implements View.OnClickListener {

    private Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_js);
        initView();
    }

    // 方法：初始化View
    private void initView() {
        btn1 = (Button) findViewById(R.id.button);
        //按钮绑定点击事件的监听器
        btn1.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Toast.makeText(EasyJsActivity.this, "btn1", Toast.LENGTH_SHORT).show();
                ScreenMetrics.initIfNeeded(this);
                ScreenCaptureRequestActivity.request(getApplicationContext(), new ScreenCapture.IRequestResult() {
                    @Override
                    public void onRequestResult(ScreenCapture.ResultCode result, Intent data) {
                        if (result == ScreenCapture.ResultCode.OK) {
                            ScreenCapture screenCapture = new ScreenCapture(getApplicationContext(), data);
                            screenCapture.capture("/sdcard/1.png", new Define.IEventCallBack() {
                                @Override
                                public void afterExecute(Define.EventCode code, Define.CallBackMsg callBackMsg) {
                                    Toast.makeText(EasyJsActivity.this, code.toString(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        } else {
                            Toast.makeText(EasyJsActivity.this, "用户取消了", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}
