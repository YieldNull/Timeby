package com.nectar.timeby.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.nectar.timeby.util.PrefsUtil;

/**
 * 监听电话，将控制逻辑转给TimeCountService
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    private static final String TAG = "IncomingCallReceiver";

    public IncomingCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

        Log.i(TAG, "Receive incoming call");

        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING://打入
                if (PrefsUtil.isOnTask(context)) {
                    Log.i(TAG, "Incoming call");
                    context.startService(TimeCountService.genIntent(
                            context, TimeCountService.TYPE_RING_COME));
                }
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK://接通
                Log.i(TAG, "Call off-hook");
                break;

            case TelephonyManager.CALL_STATE_IDLE:  //挂断（可不接通）
                if (PrefsUtil.isOnTask(context)) {
                    Log.i(TAG, "Call idle");
                    context.startService(TimeCountService.genIntent(
                            context, TimeCountService.TYPE_RING_OFF));
                }
                break;

            default:
                break;
        }

    }
}
