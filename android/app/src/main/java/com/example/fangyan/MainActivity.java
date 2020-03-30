package com.example.fangyan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.rosuh.filepicker.config.FilePickerManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FFmpeg";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册广播接收器
        CommonReceiver myReceiver = new CommonReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.nangch.broadcasereceiver.MYRECEIVER");
        registerReceiver(myReceiver, intentFilter);

        webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/index.html");

        //添加js监听 这样html就能调用客户端
        webView.addJavascriptInterface(new HandleVideo(this), "android");

        //允许使用js
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //申请读写权限
        requestMyPermissions();
    }

    //更新渲染进度
    private class CommonReceiver extends BroadcastReceiver {
        @Override
        @SuppressLint("SetJavaScriptEnabled")
        public void onReceive(Context context, Intent intent) {
            int percentage = intent.getIntExtra("percentage", 0);
            String filePath = intent.getStringExtra("filePath");
            webView.loadUrl("javascript:updateRenderBar(" + percentage + ",'" + filePath + "')");
        }
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            List<String> list = FilePickerManager.INSTANCE.obtainData();
            if (list.size() == 0) {
                Toast.makeText(this, "至少选择一个文件", Toast.LENGTH_LONG).show();
            } else if (list.size() > 1) {
                Toast.makeText(this, "只能选择一个文件", Toast.LENGTH_LONG).show();
            } else {
                String suffix = list.get(0).substring(list.get(0).lastIndexOf(".") + 1);

                if (requestCode == 1) {
                    if (suffix.contentEquals("mp3")) {
                        webView.loadUrl("javascript:addBackgroundMusic('" + list.get(0) + "')");
                    } else {
                        Toast.makeText(this, "请选择mp3文件", Toast.LENGTH_LONG).show();
                    }
                } else if (requestCode == 2) {
                    if (suffix.contentEquals("mp3")) {
                        webView.loadUrl("javascript:addDialect('" + list.get(0) + "')");
                    } else {
                        Toast.makeText(this, "请选择mp3文件", Toast.LENGTH_LONG).show();
                    }
                } else if (requestCode == 3) {
                    if (suffix.contentEquals("mp4")) {
                        webView.loadUrl("javascript:addVideo('" + list.get(0) + "')");
                    } else {
                        Toast.makeText(this, "请选择mp4文件", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    //动态权限申请
    private void requestMyPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: 有写SD权限");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: 有读SD权限");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        webView.destroy();
        webView = null;
    }
}