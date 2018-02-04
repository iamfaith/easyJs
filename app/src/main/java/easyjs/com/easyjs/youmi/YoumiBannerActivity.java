package easyjs.com.easyjs.youmi;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import abc.abc.abc.nm.bn.BannerManager;
import abc.abc.abc.nm.bn.BannerViewListener;
import easyjs.com.easyjs.droidcommon.BaseActivity;

/**
 * 插入有米banner广告的activity（bottom）
 * Created by faith on 2018/1/30.
 */

public class YoumiBannerActivity extends BaseActivity {
    @Override
    protected String getTAG() {
        return "YoumiBannerActivity";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 展示广告条窗口的 onDestroy() 回调方法中调用
        BannerManager.getInstance(context).onDestroy();
    }


    protected void initBanner() {
        // 实例化 LayoutParams（重要）
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        // 设置广告条的悬浮位置
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT; // 这里示例为右下角

        // 获取广告条
        View bannerView = BannerManager.getInstance(context)
                .getBannerView(context, new BannerViewListener() {
                    @Override
                    public void onRequestSuccess() {
                        Log.d(getTAG(), "onRequestSuccess: ");
                    }

                    @Override
                    public void onSwitchBanner() {

                    }

                    @Override
                    public void onRequestFailed() {
                        Log.d(getTAG(), "onRequestFailed: ");
                    }
                });
        // 调用 Activity 的 addContentView 函数
        ((Activity) context).addContentView(bannerView, layoutParams);
    }
}
