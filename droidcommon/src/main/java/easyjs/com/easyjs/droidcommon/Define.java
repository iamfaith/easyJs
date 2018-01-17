package easyjs.com.easyjs.droidcommon;

import android.Manifest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by faith on 2018/1/16.
 */

public class Define {

    public static class RequestCode {
        public static final int WRITE_EXTERNAL_STORAGE = 1;
        public static final int READ_EXTERNAL_STORAGE = 3;
        public static final int INTERNET = 5;
        public static final Map<String, Integer> map = new HashMap<>();

        static {
            map.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
            map.put(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE);
            map.put(Manifest.permission.INTERNET, INTERNET);
        }

        public static int getRequestCode(String[] reqs) {
            int code = 0;
            for (String req : reqs) {
                code += map.get(req);
            }
            return code;
        }
    }

    public enum EventCode {
        SUCCESS("SUCCESS"), FAIL("FAIL");
        private String code;

        EventCode(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "EventCode{" +
                    "code='" + code + '\'' +
                    '}';
        }
    }

    public static class CallBackMsg {
        public Throwable error;
        public String msg;

        private CallBackMsg() {

        }

        public static CallBackMsg buildMsg(String msg) {
            CallBackMsg callBackMsg = new CallBackMsg();
            callBackMsg.msg = msg;
            return callBackMsg;
        }

        public static CallBackMsg buildMsg(String msg, Throwable error) {
            CallBackMsg callBackMsg = buildMsg(msg);
            callBackMsg.error = error;
            return callBackMsg;
        }
    }

    public interface IEventCallBack {
        void afterExecute(EventCode code, CallBackMsg callBackMsg);
    }

    public interface IEventListener {
        void handleEvent();
    }
}
