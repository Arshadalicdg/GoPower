package com.android.powerbankpad;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.powerbankpad.ApiUnits.RetrofitInstance;
import com.android.powerbankpad.Model.Data;
import com.android.powerbankpad.Model.DataModel;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    WebView webView = null;
    public static String sn = "00000000";
    final int REQUEST_CODE = 101;
   static String android_id;

   //TextView getsr;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
     //   getsr=findViewById(R.id.getsr);
        EventBus.getDefault().register(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
//        getsr.setOnClickListener(view -> {
//            getapicall();
//        });
        this.webView = findViewById(R.id.root_view);
        WebSettings ws = this.webView.getSettings();
        ws.setJavaScriptEnabled(true);
        this.webView.addJavascriptInterface(new MyJavascriptInterface(), "bridge");
        this.webView.loadUrl("file:///android_asset/panel.html");
        for (int a = 1; a<=10; a++) {
            new Handler().postDelayed(() -> {
                if (sn.equals("00000000")) {
//                    ToastUtils.showLong("get sn");

                    // yourMethod();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String packageName = "com.lechang.service2";
                    String className = "com.lechang.service2.UpdaterService";
                    intent.setClassName(packageName, className);
                    startService(intent);
                    Intent getSerNum = new Intent(ConstantDatas.GET_SERNUM_BROADCAST_ACTION);
                    intent.setComponent(new ComponentName("com.lechang.service2", "com.lechang.service2.ThirdAppReceiver"));
                    MainActivity.this.sendBroadcast(getSerNum);
//                    Toast.makeText(this, "getSerNum" +getSerNum, Toast.LENGTH_SHORT).show();
                    Logger.d("Logger  MainActivity 发送 获取序列号的广播  ");

           android_id = Settings.Secure.getString(getApplicationContext()
                                    .getContentResolver(),
                            Settings.Secure.ANDROID_ID);
//                    Toast.makeText(this, "getSerNumber"+ android_id, Toast.LENGTH_SHORT).show();

                    onEventbusEvent(android_id);
                    getapicall();
                }
            }, 1000*a); // 10 seconds
        }
    }

    private void getapicall() {

        Call<DataModel> call = RetrofitInstance.getInstance().getMyApi().getValue(android_id);
        call.enqueue(new Callback<DataModel>() {
            @Override
            public void onResponse(Call<DataModel> call, Response<DataModel> response) {
                if(response.isSuccessful()){
                    DataModel data = response.body();
                    Log.e("onResponse", "onResponse: "+data.getStatus() );
                    Toast.makeText(getApplicationContext(), ""+data.getStatus(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataModel> call, Throwable t) {
                Log.e("onResponse", "onResponse: "+t );

                Toast.makeText(getApplicationContext(), "An error has occured" +t, Toast.LENGTH_LONG).show();
            }

        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventbusEvent(String msg) {
        Logger.d("Logger  MainActivity onEventbusEvent 收到EVENTBUS消息: " + msg);
//        ToastUtils.showLong(msg);

        //2:联网成功,4:租借功,5:租借失败,6:归还成功,7:归还失败
        //mLog.setText(mLog.getText() + "\n" + msg);
        //this.webView.loadUrl("javascript:javacalljs(" + msg + ")");
        sn = msg;
//        Log.e("TAGTAGTAG", "onEventbusEvent: "+sn );
        this.webView.addJavascriptInterface(new MyJavascriptInterface(), "bridge");
        this.webView.reload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    // in the below line, we are calling on request permission result method.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            // in the below line, we are checking if permission is granted.
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if permissions are granted we are displaying below toast message.
                Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                // in the below line, we are displaying toast message
                // if permissions are not granted.
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}