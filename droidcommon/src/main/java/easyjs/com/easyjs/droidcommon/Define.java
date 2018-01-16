package easyjs.com.easyjs.droidcommon;

/**
 * Created by faith on 2018/1/16.
 */

public class Define {

    public static class RequestCode {
        public static final int WRITE_EXTERNAL_STORAGE = 1;
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
}
