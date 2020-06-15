package easyjs.com.easyjs;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableFloatyWindow;
import com.stardust.enhancedfloaty.util.FloatingWindowPermissionUtil;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import easyjs.com.CustomSSLSocketFactory;
import easyjs.com.R;
import easyjs.com.easyjs.application.model.Question;
import easyjs.com.easyjs.application.question.SearchEngine;
import easyjs.com.easyjs.droidcommon.BaseActivity;
import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.droidcommon.permission.PermissionManager;
import easyjs.com.easyjs.droidcommon.util.SQLiteUtil;
import easyjs.com.easyjs.floaty.SampleFloaty;
import easyjs.com.easyjs.droidcommon.proxy.ProxyService;
import easyjs.com.sms.SmsListener;
import easyjs.com.sms.SmsReceiver;


@RequiresApi(api = Build.VERSION_CODES.N)
public class EasyJsActivity extends BaseActivity implements View.OnClickListener, SmsListener {

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private TextView textView;
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

    @Override
    protected String getTAG() {
        return TAG;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_js);
        initView();

        String[] needPermissions = new String[]{Manifest.permission.READ_SMS};
        PermissionManager.requestPermission(this, needPermissions, (eventCode, callBackMsg) -> {

        });

        SmsReceiver.bindListener(this);
//        floatyWindow = new SampleFloaty("test", getApplicationContext());
//        if (ProxyService.getInstance().isProxyRunning() == false) {
//            Log.d(TAG, "start proxy !!!!!!!");
//            ProxyService.getInstance().startProxy(this, new ProxyService.OnProxyListener() {
//                @Override
//                public void OnProxyStartFail(Define.CallBackMsg errMsg) {
//                    Toast.makeText(EasyJsActivity.this, errMsg.msg, Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void OnCertInstallFail(Define.CallBackMsg errMsg) {
//                    Log.d(TAG, "install cert fail");
//                }
//
//                @RequiresApi(api = Build.VERSION_CODES.N)
//                @Override
//                public void OnProxyStartSuccess() {
//                    ProxyService.getInstance().checkProxyData();
//                    Log.d(TAG, "proxy success");
//                }
//
//                @Override
//                public void OnDataReceive(HarEntry harEntry) {
//                    String url = harEntry.getRequest().getUrl();
//                    String respStr = harEntry.getResponse().getContent().getText();
//                    if (StringUtils.isEmpty(respStr))
//                        return;
//                    Log.d(TAG, respStr);
//                    //问题
//                    if (url.contains("findQuiz")) {
//                        Log.d(TAG, respStr);
//                        String data = JSON.parseObject(respStr).getString("data");
//                        question = JSON.parseObject(data, Question.class);
//
//                        Optional<String> ansOpt = qryAns(question.getSchool(), question.getType(), question.getQuiz());
//                        ansOpt.ifPresent(ans -> floatyWindow.updateText("标答:" + ans));
//                        if (!ansOpt.isPresent()) {
//                            //baidu search
//                            String searchRet = SearchEngine.baiduSearch(question.getQuiz(), question.getOptions());
//                            floatyWindow.updateText("百度:" + searchRet);
//                            question.isFound = false;
//                        } else {
//                            question.isFound = true;
//                        }
//                    } else if (url.contains("choose")) {
//                        JSONObject object = JSON.parseObject(respStr);
//                        if (object == null)
//                            return;
//                        String dataStr = object.getString("data");
//                        Map<String, Object> data = JSON.parseObject(dataStr, Map.class);
//                        int ansNum = (int) data.get("answer");
//                        question.setAnswer(ansNum + question.getOptions().get(ansNum - 1));
//                        int num = (data.get("num") instanceof Integer ? (int) data.get("num") : Integer.parseInt((String) data.get("num")));
//                        Log.d(TAG, num + "--" + dataStr + "--" + question);
//                        try {
//                            if (question.isFound == false && question.num == num) {
//                                instertQuestion(question);
//                            } else {
//                                //答案 update database
//                                updateQuestion(question);
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }
//
//            }, url -> {
//                if (url.contains("https") && url.contains("question.hortor.net")) {
//                    return true;
//                } else
//                    return false;
//            });
//        }
//        this.initBanner();
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

        textView = (TextView) findViewById(R.id.text);

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
//                Message message = new Message();
////                message.what = INIT_DB;
////                handler.sendMessage(message);
////                ProxyService.getInstance().installCert(this, true);

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String url = "https://api.day.app/nwsrJuWCNoh3XKgogVv8tQ/测试啊";

                        HttpClient client = new DefaultHttpClient();

                        try {
                            if (url.toLowerCase().contains("https://")) {
                                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                                trustStore.load(null, null);
                                SSLSocketFactory sf = new CustomSSLSocketFactory(trustStore);
                                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                                HttpParams params = new BasicHttpParams();
                                SchemeRegistry registry = new SchemeRegistry();
                                registry.register(new Scheme("https", sf, 443));

                                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
                                client = new DefaultHttpClient(ccm, params);
                            }


                            HttpGet request = new HttpGet();
                            request.setURI(new URI(url));
                            HttpResponse response = client.execute(request);
                        } catch (Exception e) {
                            // this.textView.setText(e.toString());
                            Log.e(TAG, e.toString());
                        }
                    }
                });

                thread.start();


                Uri SMS_INBOX = Uri.parse("content://sms/");

                ContentResolver cr = getContentResolver();
                String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
                Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
                if (null == cur) {
                    Log.i("ooc", "************cur == null");
                    return;
                }
                if (cur.moveToNext()) {
                    String number = cur.getString(cur.getColumnIndex("address"));//手机号
                    String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
                    String body = cur.getString(cur.getColumnIndex("body"));//短信内容
                    //至此就获得了短信的相关的内容, 以下是把短信加入map中，构建listview,非必要。
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("num", number);
                    map.put("mess", body);
                    Log.e(TAG, number + body);
                }

//                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                // Display the first 500 characters of the response string.
//                                //textView.setText("Response is: " + response.substring(0, 500));
//                            }
//                        }, error -> {
//                    textView.setText("That didn't work!" + error.toString());
//                    Log.e(this.TAG, error.toString());
//                });
//
//                // Add the request to the RequestQueue.
//                queue.add(stringRequest);

//                HttpClient httpclient = new DefaultHttpClient();
//                HttpResponse response = null;
//                try {
//                    response = httpclient.execute(new HttpGet("https://api.day.app/nwsrJuWCNoh3XKgogVv8tQ/测试啊"));
//
//                    StatusLine statusLine = response.getStatusLine();
//                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//                        ByteArrayOutputStream out = new ByteArrayOutputStream();
//                        response.getEntity().writeTo(out);
//                        String responseString = out.toString();
//                        out.close();
//                        //..more logic
//                    } else {
//                        //Closes the connection.
//                        response.getEntity().getContent().close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
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


    @Override
    public void messageReceived(String messageText) {
        Log.d(getTAG(), messageText);
        textView.setText(messageText);

        String url = "https://api.day.app/nwsrJuWCNoh3XKgogVv8tQ/"+ java.net.URLEncoder.encode(messageText);
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {


                HttpClient client = new DefaultHttpClient();

                try {
                    if (url.toLowerCase().contains("https://")) {
                        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        trustStore.load(null, null);
                        SSLSocketFactory sf = new CustomSSLSocketFactory(trustStore);
                        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                        HttpParams params = new BasicHttpParams();
                        SchemeRegistry registry = new SchemeRegistry();
                        registry.register(new Scheme("https", sf, 443));

                        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
                        client = new DefaultHttpClient(ccm, params);
                    }


                    HttpGet request = new HttpGet();
                    request.setURI(new URI(url));
                    HttpResponse response = client.execute(request);
                } catch (Exception e) {
                    // this.textView.setText(e.toString());
                    Log.e(TAG, e.toString());
                }
            }
        });

        thread.start();

    }
}
