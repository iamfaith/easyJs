package easyjs.com.common;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    private OkHttpClient client;

    private HttpUtil() {
        client = new OkHttpClient();
    }

    static private class Holder {
        private static HttpUtil instance = new HttpUtil();
    }

    public static HttpUtil getInstance() {
        return Holder.instance;
    }

    public String postFile(String url, File file) {
//        String url = "http://api.happyocr.com/send";
//        File file = new File("./a.jpeg");
        try {
            Response response = client.newCall(createRequest(url, file)).execute();
            String respStr = response.body().string();
            return respStr;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Request createRequest(String url, File file) throws IOException {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("text/plain"), file))
                .addFormDataPart("other_field", "other_field_value")
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        return request;
    }

    public String get(String url, Map<String, String> params) {
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuider.addQueryParameter(param.getKey(), param.getValue());
            }
        }
        Request request = new Request.Builder().url(httpBuider.build()).build();
        try {
            Response response = client.newCall(request).execute();
            String respStr = response.body().string();
            return respStr;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
