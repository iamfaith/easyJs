package easyjs.com.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class SmsReceiver extends BroadcastReceiver
{
    private static SmsListener mListener;



//    @Override
//    public void onReceive(Context context, Intent intent)
//    {
//        Bundle data  = intent.getExtras();
//
//        Object[] pdus = (Object[]) data.get("pdus");
//
//
//        for(int i = 0;i < pdus.length; i++)
//        {
//            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
//
//            //String sender = smsMessage.getDisplayOriginatingAddress();
//            //You must check here if the sender is your provider and not another one with same text.
//
//            String messageBody = smsMessage.getMessageBody();
//            Log.d("SMS", "onReceive: " + messageBody);
//            //Pass on the text to our listener.
//            if(mListener != null)
//            {
//                mListener.messageReceived(messageBody);
//            }
//        }
//
//    }

    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "yjj";
    public SmsReceiver() {
        Log.i("yjj", "new SmsReceiver");
    }
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(TAG, "jie shou dao");
        Cursor cursor = null;
        try {
            if (SMS_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "sms received!");
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    final SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    if (messages.length > 0) {
                        String content = messages[0].getMessageBody();
                        String sender = messages[0].getOriginatingAddress();
                        long msgDate = messages[0].getTimestampMillis();
                        String smsToast = sender + ":"
                                + content;
                        mListener.messageReceived(smsToast);
                        Toast.makeText(context, smsToast, Toast.LENGTH_LONG)
                                .show();
                        Log.d(TAG, "message from: " + sender + ", message body: " + content
                                + ", message date: " + msgDate);
                        //自己的逻辑
                    }
                }
                cursor = context.getContentResolver().query(Uri.parse("content://sms"), new String[] { "_id", "address", "read", "body", "date" }, "read = ? ", new String[] { "0" }, "date desc");
                if (null == cursor){
                    return;
                }
                Log.i(TAG,"m cursor count is "+cursor.getCount());
                Log.i(TAG,"m first is "+cursor.moveToFirst());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception : " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    private String parseCode(String message)
    {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find())
        {
            code = m.group(0);
        }
        return code;
    }

    public static void bindListener(SmsListener listener)
    {
        mListener = listener;
    }
}
