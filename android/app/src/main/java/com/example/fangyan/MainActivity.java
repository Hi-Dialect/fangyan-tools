package com.example.fangyan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

import me.rosuh.filepicker.config.FilePickerManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FFmpeg";
    private WebView webView;
    private CommonReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册广播接收器
        myReceiver = new CommonReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.nangch.broadcasereceiver.MYRECEIVER");
        registerReceiver(myReceiver, intentFilter);

        webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/homepage.html");

        //添加js监听 这样html就能调用客户端
        webView.addJavascriptInterface(new HandleVideo(this), "android");

        //允许使用js
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //动态申请权限
        requestMyPermissions();
    }

    //接收广播，然后与JS通信
    private class CommonReceiver extends BroadcastReceiver {
        @Override
        @SuppressLint("SetJavaScriptEnabled")
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");

            //更新渲染进度
            if (type.equals("updateRenderProgress")) {
                int percentage = intent.getIntExtra("percentage", 0);
                String filePath = intent.getStringExtra("filePath");
                webView.loadUrl("javascript:updateRenderBar(" + percentage + ",'" + filePath + "')");
            }
            //报错
            else if (type.equals("alertError")) {
                String message = intent.getStringExtra("message");
                webView.loadUrl("javascript:alertError('" + message + "')");
            }
            //添加背景音乐录音
            else if (type.equals("addBackgroundMusic")) {
                String filePath = intent.getStringExtra("filePath");
                webView.loadUrl("javascript:addBackgroundMusic('" + filePath + "')");
            }
            //添加方言配音录音
            else if (type.equals("addDialect")) {
                String filePath = intent.getStringExtra("filePath");
                webView.loadUrl("javascript:addDialect('" + filePath + "')");
            }
        }
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //调用FilePickerManager读取本地文件
        if (requestCode == 1 || requestCode == 3 || requestCode == 5) {
            List<String> list = FilePickerManager.INSTANCE.obtainData();
            if (list.size() == 0) {
                Toast.makeText(this, "至少选择一个文件", Toast.LENGTH_LONG).show();
            } else if (list.size() > 1) {
                Toast.makeText(this, "至多选择一个文件", Toast.LENGTH_LONG).show();
            } else {
                String suffix = list.get(0).substring(list.get(0).lastIndexOf(".") + 1);

                if (requestCode == 1) {
                    if (suffix.contentEquals("mp4")) {
                        webView.loadUrl("javascript:addVideo('" + list.get(0) + "')");
                    } else {
                        Toast.makeText(this, "请选择mp4文件", Toast.LENGTH_LONG).show();
                    }
                } else if (requestCode == 3) {
                    if (suffix.contentEquals("mp3")) {
                        webView.loadUrl("javascript:addBackgroundMusic('" + list.get(0) + "')");
                    } else {
                        Toast.makeText(this, "请选择mp3文件", Toast.LENGTH_LONG).show();
                    }
                } else if (requestCode == 5) {
                    if (suffix.contentEquals("mp3")) {
                        webView.loadUrl("javascript:addDialect('" + list.get(0) + "')");
                    } else {
                        Toast.makeText(this, "请选择mp3文件", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        //录制视频
        else if (requestCode == 2) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToNext()) {
                //获取视频路径并传递到前端
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));

                webView.loadUrl("javascript:addVideo('" + filePath + "')");
                cursor.close();
            }
        }
        //录制背景音乐
        else if (requestCode == 4) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToNext()) {
                //获取音频路径并传递到前端
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));

                webView.loadUrl("javascript:addBackgroundMusic('" + filePath + "')");
                cursor.close();
            }
        }
        //录制方言配音
        else if (requestCode == 6) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToNext()) {
                //获取音频路径并传递到前端
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));

                webView.loadUrl("javascript:addDialect('" + filePath + "')");
                cursor.close();
            }
        }
    }

    //动态权限申请
    private void requestMyPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: 有写SD权限");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: 有读SD权限");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: 有录音权限");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //删除临时生成的视频文件
        FileManager.removeFileFromSDCard(new HandleVideo(this).getTempVideoPath());
        //删除临时生成的录音文件
        FileManager.removeFileFromSDCard(new HandleVideo(this).getTempRecordingPath());

        //注销广播监听器
        unregisterReceiver(myReceiver);

        webView.destroy();
        webView = null;
    }
}