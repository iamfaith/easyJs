package easyjs.com.easyjs;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import easyjs.com.common.HttpUtil;
import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.droidcommon.screencapture.ScreenCapture;
import easyjs.com.easyjs.droidcommon.screencapture.ScreenCaptureRequestActivity;
import easyjs.com.easyjs.droidcommon.util.ScreenMetrics;


public class EasyJsActivity extends Activity implements View.OnClickListener {

    private Button btn1;
    private static int REQUEST_CODE;

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                String[] requestPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE};
                REQUEST_CODE = Define.RequestCode.getRequestCode(requestPermission);
                ActivityCompat.requestPermissions(this, requestPermission, REQUEST_CODE);
                Toast.makeText(EasyJsActivity.this, "btn1", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("result");
            Toast.makeText(EasyJsActivity.this, val, Toast.LENGTH_SHORT).show();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
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
                                final File screenShot = new File("/sdcard/1.png");
                                final String url = "http://api.happyocr.com/send";

                                Runnable runnable = new Runnable(){
                                    @Override
                                    public void run() {
                                        String respStr = HttpUtil.getInstance().postFile(url, screenShot);
                                        Message msg = new Message();
                                        Bundle data = new Bundle();
                                        data.putString("result",respStr);
                                        msg.setData(data);
                                        handler.sendMessage(msg);
                                    }
                                };
                                Thread thread = new Thread(runnable);
                                thread.start();
                            }
                        });
                    } else {
                        Toast.makeText(EasyJsActivity.this, "用户取消了", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
