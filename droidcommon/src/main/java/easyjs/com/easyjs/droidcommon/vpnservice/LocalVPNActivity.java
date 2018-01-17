package easyjs.com.easyjs.droidcommon.vpnservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;

import easyjs.com.easyjs.droidcommon.Define;
import easyjs.com.easyjs.droidcommon.screencapture.ScreenCaptureRequestActivity;

/**
 * Created by faith on 2018/1/17.
 */

public class LocalVPNActivity extends Activity {

    private static Define.IEventCallBack callback;
    private static final int VPN_REQUEST_CODE = 0x0F;
    private boolean waitingForVPNStart;

    public static void startVPN(Context context, Define.IEventCallBack requester) {
        if (requester == null)
            return;
        callback = requester;
        context.startActivity(new Intent(context, LocalVPNActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent vpnIntent = VpnService.prepare(this);
        if (vpnIntent != null)
            this.startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        else
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK)
        {
            waitingForVPNStart = true;
            startService(new Intent(this, VPNService.class));
        }
    }
}
