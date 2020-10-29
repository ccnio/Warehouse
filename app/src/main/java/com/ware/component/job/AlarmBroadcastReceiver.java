package com.ware.component.job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_SYNC = "action_sync";
    public static final String TAG = "AlarmBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        if (intent != null && ACTION_SYNC.equals(intent.getAction())) {
            Log.d(TAG, "onReceive: " + intent.getAction());
        }
    }
}
