package com.example.fangyan;

import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/index.html");

        //添加js监听 这样html就能调用客户端
        webView.addJavascriptInterface(new HandleVideo(), "android");

        //允许使用js
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        webView.destroy();
        webView = null;
    }
}

class HandleVideo extends Object {
    @JavascriptInterface
    public String hello(String msg) {
        Log.i("FangYan", msg);
        return new String("不服来干");
    }
}