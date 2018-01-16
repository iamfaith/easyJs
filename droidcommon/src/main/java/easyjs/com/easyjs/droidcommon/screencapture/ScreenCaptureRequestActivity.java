package easyjs.com.easyjs.droidcommon.screencapture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenCaptureRequestActivity extends Activity {

    private static ScreenCapture.RequestResult callback;
    private MediaProjectionManager mMediaProjectionManager;
    private static final int REQUEST_MEDIA_PROJECTION = 1;


    public static void request(Context context, ScreenCapture.RequestResult requester) {
        if (requester != null) {
            return;
        }
        callback = requester;
        context.startActivity(new Intent(context, ScreenCaptureRequestActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaProjectionManager = (MediaProjectionManager)
                this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        this.startActivityForResult(
                mMediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        requester.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
//                Log.i(TAG, "User cancelled");
                callback.onRequestResult(ScreenCapture.ResultCode.CANCEL, data);
                return;
            }
            callback.onRequestResult(ScreenCapture.ResultCode.OK, data);
//            setUpMediaProjection();
//            setUpVirtualDisplay();
        }
        callback = null;
        finish();
    }
}
