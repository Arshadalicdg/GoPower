package com.android.powerbankpad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.powerbankpad.ConstantDatas;
import com.blankj.utilcode.util.StringUtils;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String packageName = intent.getDataString();
        Logger.d("Logger BootReceiver onReceive():接收到Intent.getAction() = " + intent.getAction() + " , 包名 = " + intent.getDataString());

        if (StringUtils.isTrimEmpty(action)) {
            return;
        }

        switch (action) {
            case ConstantDatas.BROADCAST_ACTION:

                int voice = intent.getIntExtra("voice", ConstantDatas.INT__1);
                Logger.d("Logger BootReceiver onReceive(): 充电宝广播 播放声音: " + voice);
                String id = intent.getStringExtra("id");
                Logger.d("Logger BootReceiver onReceive(): 充电宝广播 设备id: " + id);

                //2:联网成功,4:租借功,5:租借失败,6:归还成功,7:归还失败
//                EventBus.getDefault().post(String.valueOf(voice));
                break;

            case ConstantDatas.SET_SERNUM_BROADCAST_ACTION:
                String id2 = intent.getStringExtra("id");
                Logger.d("Logger BootReceiver onReceive(): 充电宝广播 设备id: " + id2);
                if (!StringUtils.isTrimEmpty(id2)) {
                    EventBus.getDefault().post(id2);
                }
                break;
        }
    }
}
