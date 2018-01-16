package easyjs.com.easyjs.droidcommon.screencapture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.droidcommon.util.ScreenMetrics;

/**
 * Created by faith on 2018/1/16.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenCapture {
    public enum ResultCode {
        OK, CANCEL
    }

    public interface IRequestResult {

        void onRequestResult(ResultCode result, Intent data);
    }

    public interface ICaptureResult {
        void finishCapture(Image image);
    }

    private final int mScreenWidth;
    private final int mScreenHeight;
    private final int mScreenDensity;
    private Handler mHandler;
    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private AtomicBoolean hasCapture = new AtomicBoolean(false);

    public ScreenCapture(Context context, Intent data, int screenWidth, int screenHeight, int screenDensity, Handler handler) {
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mScreenDensity = screenDensity;
        mHandler = handler;
        MediaProjectionManager manager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        initVirtualDisplay(manager, data, screenWidth, screenHeight, screenDensity);
        mHandler = handler;
//        startAcquireImageLoop();
    }

    public ScreenCapture(Context context, Intent data, int screenWidth, int screenHeight) {
        this(context, data, screenWidth, screenHeight, ScreenMetrics.getDeviceScreenDensity(), null);
    }

    public ScreenCapture(Context context, Intent data) {
        this(context, data, ScreenMetrics.getDeviceScreenWidth(), ScreenMetrics.getDeviceScreenHeight());
    }

    private void initVirtualDisplay(MediaProjectionManager manager, Intent data, int screenWidth, int screenHeight, int screenDensity) {
        mImageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
        mMediaProjection = manager.getMediaProjection(Activity.RESULT_OK, data);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                screenWidth, screenHeight, screenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    public void capture(final ICaptureResult callback) {
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {

            @Override
            public void onImageAvailable(ImageReader reader) {
                if (hasCapture.get() == false) {
                    Image image = reader.acquireLatestImage();
                    callback.finishCapture(image);
                }
            }
        }, null);
    }

    public void capture(final String path, final Define.IEventCallBack callBack) {
        capture(new ICaptureResult() {
            @Override
            public void finishCapture(Image image) {
                Log.d("capture", "catch image--" + image);
                if (image != null) {
                    try (FileOutputStream fos = new FileOutputStream(path)) {
                        Bitmap bitmap = toBitMap(image);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        callBack.afterExecute(Define.EventCode.SUCCESS, Define.CallBackMsg.buildMsg("capture success"));
                        hasCapture.compareAndSet(false, true);
                    } catch (Exception e) {
                        callBack.afterExecute(Define.EventCode.FAIL, Define.CallBackMsg.buildMsg("capture fail", e));
                    } finally {
                        if (image != null)
                            image.close();
                    }
                } else {
                    callBack.afterExecute(Define.EventCode.FAIL, Define.CallBackMsg.buildMsg("capture fail"));
                }
            }
        });

    }

    private Bitmap toBitMap(Image image) {
        Image.Plane plane = image.getPlanes()[0];
        ByteBuffer buffer = plane.getBuffer();
        buffer.position(0);
        int pixelStride = plane.getPixelStride();
        int rowPadding = plane.getRowStride() - pixelStride * image.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void release() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mImageReader != null) {
            mImageReader.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            release();
        } finally {
            super.finalize();
        }
    }

}
