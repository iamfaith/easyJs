package easyjs.com.easyjs.application.wechatjump;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by chenliang on 2018/1/1.
 */
public class Hack {


    static final String ADB_PATH = "/Users/chenliang/Library/Android/sdk/platform-tools/adb";

    /**
     * 弹跳系数，现在已经会自动适应各种屏幕，请不要修改。
     */
    static final double JUMP_RATIO = 1.390f;

    private static Random RANDOM = new Random();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Integer> calPos(Bitmap image, StringBuffer log) {
        MyPosFinder myPosFinder = new MyPosFinder();
        NextCenterFinder nextCenterFinder = new NextCenterFinder();
        WhitePointFinder whitePointFinder = new WhitePointFinder();
        double jumpRatio = 0;
        List<Integer> result = new LinkedList<>();
        if (jumpRatio == 0) {
            jumpRatio = JUMP_RATIO * 1080 / image.getWidth();
        }

        int[] myPos = myPosFinder.find(image);
        if (myPos != null) {
            int[] nextCenter = nextCenterFinder.find(image, myPos);
            if (nextCenter == null || nextCenter[0] == 0) {
                log.append("find nextCenter, fail");
                return result;
            } else {
                int centerX, centerY;
                int[] whitePoint = whitePointFinder.find(image, nextCenter[0] - 120, nextCenter[1], nextCenter[0] + 120, nextCenter[1] + 180);
                if (whitePoint != null) {
                    centerX = whitePoint[0];
                    centerY = whitePoint[1];
                } else {
                    if (nextCenter[2] != Integer.MAX_VALUE && nextCenter[4] != Integer.MIN_VALUE) {
                        centerX = (nextCenter[2] + nextCenter[4]) / 2;
                        centerY = (nextCenter[3] + nextCenter[5]) / 2;
                    } else {
                        centerX = nextCenter[0];
                        centerY = nextCenter[1] + 48;
                    }
                }
                log.append("find nextCenter, succ, (" + centerX + ", " + centerY + ")");
                int distance = (int) (Math.sqrt((centerX - myPos[0]) * (centerX - myPos[0]) + (centerY - myPos[1]) * (centerY - myPos[1])) * jumpRatio);
                log.append("distance: " + distance);
                int pressX = 400 + RANDOM.nextInt(100);
                int pressY = 500 + RANDOM.nextInt(100);

                result.add(distance);
                result.add(pressX);
                result.add(pressY);
            }
        } else {
            log.append("find myPos, fail");
        }
        return result;
    }


}
