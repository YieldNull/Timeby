package com.nectar.timeby.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCallReceiver extends BroadcastReceiver {
    private static final String TAG = "IncomingCallReceiver";

    public IncomingCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //如果是来电
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i(TAG, "Incoming call");
                //打入
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //接通
                Log.i(TAG, "call off-hook");
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //挂断
                Log.i(TAG, "call idle");
                break;
        }
    }
}
