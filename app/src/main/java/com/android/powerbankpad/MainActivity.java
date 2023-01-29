package com.android.powerbankpad;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.powerbankpad.ApiUnits.RetrofitInstance;
import com.android.powerbankpad.Model.DataModel;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    WebView webView = null;
    public static String sn = "00000000";
    final int REQUEST_CODE = 101;
    static String android_id;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        EventBus.getDefault().register(this);
        Logger.addLogAdapter(new AndroidLogAdapter());

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


                    dialog=new ProgressDialog(this);
                    dialog.setMessage("Downloading Please wait!");
                    dialog.show();
                    getapicall();
                }
            }, 1000*a); // 10 seconds
        }
    }

    ProgressDialog dialog;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createDirectory(String logoUrl, List<String> adsUrl) {

        File yourAppDir = new File(Environment.getExternalStorageDirectory()+File.separator+"/GoPower"+"/assets/");

        if(!yourAppDir.exists() && !yourAppDir.isDirectory())
        {
            if (yourAppDir.mkdirs())
            {
                Log.i("CreateDir","App dir created");
            }
            else
            {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "unable to make directory", Toast.LENGTH_SHORT).show();
                // Create the AlertDialog object and return it
                return;
            }
        }
        handleLogoDownload(yourAppDir,logoUrl);
        handleAdsDownload(yourAppDir,adsUrl);
        dialog.dismiss();
    }

    private void handleAdsDownload(File yourAppDir, List<String> adsUrl) {

        for(int i = 0 ;i<adsUrl.size();i++){
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(adsUrl.get(i));
            String url = String.valueOf(uri);
            String[] urlSnippet = url.split("/");
            String fileType =urlSnippet[3];
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
            String fileName = URLUtil.guessFileName(url,null, MimeTypeMap.getFileExtensionFromUrl(String.valueOf(uri)));
            File path = new File(yourAppDir.getAbsolutePath()+"/"+fileType, fileName);
            if(!path.exists()){
                request.setDestinationUri(Uri.fromFile(path));
                manager.enqueue(request);
            }
//          request.setDestinationInExternalPublicDir(yourAppDir.getAbsolutePath()+"/video", "video.mp4");
        }
    }


    private void handleLogoDownload(File yourAppDir, String logoUrl) {
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(logoUrl);
        String url = String.valueOf(uri);
        String[] urlSnippet = url.split("/");
        String fileType =  urlSnippet[3];
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
        String fileName = URLUtil.guessFileName(url,null, MimeTypeMap.getFileExtensionFromUrl(logoUrl));
        File path = new File(yourAppDir.getAbsolutePath()+"/"+fileType, fileName);
        if(!path.exists()){
            request.setDestinationUri(Uri.fromFile(path));
            manager.enqueue(request);
        }
        //      request.setDestinationInExternalPublicDir(yourAppDir.getAbsolutePath()+"/image", "logo.png");
    }
    private void getapicall() {

        Call<DataModel> call = RetrofitInstance.getInstance().getMyApi().getValue(android_id);
        call.enqueue(new Callback<DataModel>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<DataModel> call, Response<DataModel> response) {
                if(response.isSuccessful()){
                    DataModel data = response.body();
                    Log.e("onResponse", "onResponse: "+data.getData().getAdsUrl() );

                    createDirectory(data.getData().getLogoUrl(), data.getData().getAdsUrl());

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