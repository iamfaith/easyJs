package easyjs.com.easyjs.droidcommon.screencapture;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.droidcommon.util.ScreenMetrics;

@Deprecated
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenCaptureRequestActivity extends Activity {

    private static ScreenCapture.IRequestResult callback;
    private MediaProjectionManager mMediaProjectionManager;
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private static int REQUEST_CODE;

    private void requestPermission() {
        String[] needPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE};
        List<String> listPermissions = new ArrayList<>();
        for (String permisstion : needPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permisstion) == PackageManager.PERMISSION_DENIED) {
                listPermissions.add(permisstion);
            }
        }
        if (listPermissions.size() > 0) {
            String[] requestPermission = new String[listPermissions.size()];
            listPermissions.toArray(requestPermission);
            REQUEST_CODE = Define.RequestCode.getRequestCode(requestPermission);
            ActivityCompat.requestPermissions(this, requestPermission, REQUEST_CODE);
        }
    }

    public static void request(Context context, ScreenCapture.IRequestResult requester) {
        if (requester == null) {
            return;
        }
        callback = requester;
        context.startActivity(new Intent(context, ScreenCaptureRequestActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenMetrics.initIfNeeded(this);
        requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            boolean hasPermission = true;
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        hasPermission = false;
                    }
                }
            } else
                hasPermission = false;
            if (!hasPermission)
                callback.onRequestResult(ScreenCapture.ResultCode.CANCEL, null);
            else {
                mMediaProjectionManager = (MediaProjectionManager)
                        this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                this.startActivityForResult(
                        mMediaProjectionManager.createScreenCaptureIntent(),
                        REQUEST_MEDIA_PROJECTION);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
//                Log.i(TAG, "User cancelled");
                callback.onRequestResult(ScreenCapture.ResultCode.CANCEL, data);
                return;
            } else
                callback.onRequestResult(ScreenCapture.ResultCode.OK, data);
        }
        callback = null;
        finish();
    }
}
