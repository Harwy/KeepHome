package com.shu.keephome.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by 14623 on 2018/5/16.
 * 广播类，用于启动后台服务
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
        Intent service = new Intent(context,AutoUpdateService.class);
        context.startService(service);
        Log.v(TAG, "onReceive");

    }
}
