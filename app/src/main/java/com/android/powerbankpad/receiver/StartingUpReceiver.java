package com.android.powerbankpad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.android.powerbankpad.MainActivity;
import com.orhanobut.logger.Logger;


public class StartingUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String packageName = intent.getDataString();
        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            //接收开机广播
            Logger.e("Logger StartingUpService onReceive():开机，了:" + packageName + "包名的程序");
            Intent intent3 = new Intent(context, MainActivity.class);
            intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent3);
        }
    }
}
