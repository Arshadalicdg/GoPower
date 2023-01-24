package com.android.powerbankpad;

import android.webkit.JavascriptInterface;

public class MyJavascriptInterface {
    @JavascriptInterface
    public static String getSN() {

        return MainActivity.sn;

    }
}
