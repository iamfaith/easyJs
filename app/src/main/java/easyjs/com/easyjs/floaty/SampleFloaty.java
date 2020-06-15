package easyjs.com.easyjs.floaty;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.ResizableFloaty;
import com.stardust.enhancedfloaty.ResizableFloatyWindow;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import easyjs.com.R;
import easyjs.com.easyjs.App;
import easyjs.com.easyjs.EasyJs;
import easyjs.com.easyjs.application.wechatjump.Hack;
import easyjs.com.easyjs.droidcommon.BaseActivity;
import easyjs.com.easyjs.droidcommon.accessibility.GestureManager;
import easyjs.com.easyjs.droidcommon.screencapture.ScreenCapturer;
import easyjs.com.easyjs.service.AccessibilityService;

/**
 * Created by faith on 2018/1/17.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SampleFloaty extends ResizableFloaty.AbstractResizableFloaty {
    private View resizer;
    private View moveCursor;
    private TextView textView;
    private String name;
    private Button button;
    Context context;


    public SampleFloaty(String btnName, Context context) {
        this.name = btnName;
        this.context = context;

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = msg.getData().getString("msg");
            if (!StringUtils.isEmpty(str))
                textView.setText(str);
        }
    };

    public final void updateText(String str) {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.setData(data);
        data.putString("msg", str);
        handler.sendMessage(msg);
    }


    @Override
    public View inflateView(final FloatyService service, final ResizableFloatyWindow window) {
        View view = View.inflate(new ContextThemeWrapper(service, R.style.AppTheme), R.layout.floating_window_expanded, null);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.close();
            }
        });
        view.findViewById(R.id.move_or_resize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moveCursor.getVisibility() == View.VISIBLE) {
                    moveCursor.setVisibility(View.GONE);
                    resizer.setVisibility(View.GONE);
                } else {
                    moveCursor.setVisibility(View.VISIBLE);
                    resizer.setVisibility(View.VISIBLE);
                }
            }
        });
        textView = view.findViewById(R.id.textView);
//        final LinearLayout container = view.findViewById(R.id.container);
//        view.findViewById(R.id.hide).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (container.getVisibility() == View.VISIBLE) {
//                    container.setVisibility(View.GONE);
//                } else {
//                    container.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//
//
//        button = view.findViewById(R.id.button);
//        button.setText(name);
//        button.setOnClickListener(new View.OnClickListener() {
//            @TargetApi(Build.VERSION_CODES.N)
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        return view;
    }

    @Nullable
    @Override
    public View getResizerView(View expandedView) {
        resizer = expandedView.findViewById(R.id.resizer);
        return resizer;
    }

    @Nullable
    @Override
    public View getMoveCursorView(View expandedView) {
        moveCursor = expandedView.findViewById(R.id.move_cursor);
        return moveCursor;
    }
}
