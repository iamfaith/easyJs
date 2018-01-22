package easyjs.com.easyjs.application.wechatjump;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;


/**
 * Created by chenliang on 2017/12/31.
 */
public class MyPosFinder {

    public static final int R_TARGET = 40;

    public static final int G_TARGET = 43;

    public static final int B_TARGET = 86;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int[] find(Bitmap image) {
        if (image == null) {
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();

        int[] ret = {0, 0};
        int maxX = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        for (int i = 0; i < width; i++) {
            for (int j = height / 4; j < height * 3 / 4; j++) {
                int pixel = image.getPixel(i, j);
                int r = (pixel & 0xff0000) >> 16;
                int g = (pixel & 0xff00) >> 8;
                int b = (pixel & 0xff);
                if (ToleranceHelper.match(r, g, b, R_TARGET, G_TARGET, B_TARGET, 16)) {
                    maxX = Integer.max(maxX, i);
                    minX = Integer.min(minX, i);
                    maxY = Integer.max(maxY, j);
                    minY = Integer.min(minY, j);
                }
            }
        }
        ret[0] = (maxX + minX) / 2 +3;
        ret[1] = maxY;
        System.out.println(maxX + ", " + minX);
        System.out.println("pos, x: " + ret[0] + ", y: " + ret[1]);
        return ret;
    }


}
