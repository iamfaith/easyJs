package easyjs.com.common;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {


    private HttpUtil() {

    }

    static private class Holder {
        private static HttpUtil instance = new HttpUtil();
    }

    public static HttpUtil getInstance() {
        return Holder.instance;
    }

    public static String postFile(String url, File file) {
        OkHttpClient client = new OkHttpClient();
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
}
