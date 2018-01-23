package easyjs.com.easyjs;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableFloatyWindow;
import com.stardust.enhancedfloaty.util.FloatingWindowPermissionUtil;

import net.lightbody.bmp.core.har.HarEntry;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

import easyjs.com.easyjs.application.model.Question;
import easyjs.com.easyjs.application.question.SearchEngine;
import easyjs.com.easyjs.droidcommon.BaseActivity;
import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.droidcommon.accessibility.GestureManager;
import easyjs.com.easyjs.droidcommon.util.SQLiteUtil;
import easyjs.com.easyjs.service.AccessibilityService;
import easyjs.com.easyjs.floaty.SampleFloaty;
import easyjs.com.easyjs.droidcommon.proxy.ProxyService;


@RequiresApi(api = Build.VERSION_CODES.N)
public class EasyJsActivity extends BaseActivity implements View.OnClickListener {

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private EditText editText;
    private SampleFloaty floatyWindow;
    private static Question question = new Question();
    private final String extPath = "/helper/data.db";
    private static final int INIT_DB = 1;
    private final String TAG = this.getClass().getName();
    private Handler handler = new Handler(msg -> {
        if (msg.what == INIT_DB) {
            initDb();
            return true;
        }

        return false;
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
                Log.d(TAG, "install cert fail");
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void OnProxyStartSuccess() {
                ProxyService.getInstance().checkProxyData();
                Log.d(TAG, "proxy success");
            }

            @Override
            public void OnDataReceive(HarEntry harEntry) {
                String url = harEntry.getRequest().getUrl();
                String respStr = harEntry.getResponse().getContent().getText();
                if (StringUtils.isEmpty(respStr))
                    return;
                Log.d(TAG, respStr);
                //问题
                if (url.contains("findQuiz")) {
                    Log.d(TAG, respStr);
                    String data = JSON.parseObject(respStr).getString("data");
                    question = JSON.parseObject(data, Question.class);

                    Optional<String> ansOpt = qryAns(question.getSchool(), question.getType(), question.getQuiz());
                    ansOpt.ifPresent(ans -> floatyWindow.updateText("标答:" + ans));
                    if (!ansOpt.isPresent()) {
                        //baidu search
                        String searchRet = SearchEngine.baiduSearch(question.getQuiz(), question.getOptions());
                        floatyWindow.updateText("百度:" + searchRet);
                        question.isFound = false;
                    } else {
                        question.isFound = true;
                    }
                } else if (url.contains("choose")) {
                    JSONObject object = JSON.parseObject(respStr);
                    if (object == null)
                        return;
                    String dataStr = object.getString("data");
                    Map<String, Object> data = JSON.parseObject(dataStr, Map.class);
                    int ansNum = (int) data.get("answer");
                    question.setAnswer(ansNum + question.getOptions().get(ansNum - 1));
                    int num = (data.get("num") instanceof Integer ? (int) data.get("num") : Integer.parseInt((String) data.get("num")));
                    Log.d(TAG, num + "--" + dataStr + "--" + question);
                    try {
                        if (question.isFound == false && question.num == num) {
                            instertQuestion(question);
                        } else {
                            //答案 update database
                            updateQuestion(question);
                        }
                    } catch (Exception e) {

                    }
                }
            }

        }, url -> {
            if (url.contains("https") && url.contains("question.hortor.net")) {
                return true;
            } else
                return false;
        });
        //安装证书
//        ProxyService.getInstance().installCert(this, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initDb() {
        App.getApp().getUtil().cpAsssetsToExtDir(this, "data/data.db", extPath, (code, callBackMsg) -> {
            if (code == Define.EventCode.SUCCESS) {
                Toast.makeText(EasyJsActivity.this, "db init success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EasyJsActivity.this, "db init fail" + callBackMsg.msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //"生活", "健康", "「瑜伽」运动起源于哪一地区？"
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Optional<String> qryAns(String school, String type, String quiz) {
        SQLiteUtil instance = SQLiteUtil.openDataBase(Environment.getExternalStorageDirectory() + extPath);
        Map<String, String> result = null;
        quiz = "%" + quiz + "%";
        String sql = "select * from questions where quiz like ?";
        result = instance.query(sql, new String[]{quiz}, new String[]{"options", "answer"});
        Log.d("test", school + "--" + type + "--" + quiz + "--" + result.toString());
        instance.close();
        return Optional.ofNullable(result.get("answer"));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void instertQuestion(Question question) {
        SQLiteUtil instance = SQLiteUtil.openDataBase(Environment.getExternalStorageDirectory() + extPath);
        instance.insert("questions", question.getMap());
        instance.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateQuestion(Question question) {
        SQLiteUtil instance = SQLiteUtil.openDataBase(Environment.getExternalStorageDirectory() + extPath);
        instance.update("questions", question.getMap(), "quiz like ?", new String[]{question.getQuiz()});
        instance.close();
    }

    // 方法：初始化View
    private void initView() {
        btn1 = (Button) findViewById(R.id.button);
        //按钮绑定点击事件的监听器
        btn1.setOnClickListener(this);

        btn2 = (Button) findViewById(R.id.button2);
        //按钮绑定点击事件的监听器
        btn2.setOnClickListener(this);

        btn3 = (Button) findViewById(R.id.button3);
        //按钮绑定点击事件的监听器
        btn3.setOnClickListener(this);

        editText = findViewById(R.id.editText);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                App.getApp().getEasyJs().ensureAccessibilityServiceEnabled();
                break;
            case R.id.button2:
                FloatingWindowPermissionUtil.goToFloatingWindowPermissionSettingIfNeeded(this);
                startService(new Intent(this, FloatyService.class));
                FloatyService.addWindow(new ResizableFloatyWindow(floatyWindow));
                break;

            case R.id.button3:
                Message message = new Message();
                message.what = INIT_DB;
                handler.sendMessage(message);
                ProxyService.getInstance().installCert(this, true);
//                String qry = editText.getText().toString();
//                Optional<String> ansOpt = qryAns("", "", qry);
//                ansOpt.ifPresent(ans -> floatyWindow.updateText("标准答案:" + ans));
//                if (!ansOpt.isPresent())
//                    floatyWindow.updateText("没查到");
                break;
            default:
                break;
        }
    }




}
