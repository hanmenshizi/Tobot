package com.tobot.tobot.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tobot.tobot.MainActivity;

/**
 * Created by Javen on 2017/7/26.
 */

public class AutostartReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Log.i("Javen","tobto 开机广播" + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent mIntent = new Intent(context, MainActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }
    }
}