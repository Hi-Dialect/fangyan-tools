package com.example.fangyan;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.io.IOException;
import java.io.InputStream;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

public class HandleVideo extends Object {
    private static final String TAG = "FFmpeg";
    private String url = "file:///storage/emulated/0/download/Coca-Cola ads.mp4";
    private String output = "file:///storage/emulated/0/download/output.mp4";

    public void test() {
        EpVideo epVideo = new EpVideo(url);
        //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(output);
        outputOption.setWidth(480);//输出视频宽，如果不设置则为原始视频宽高
        outputOption.setHeight(360);//输出视频高度
        outputOption.frameRate = 30;//输出视频帧率,默认30
        outputOption.bitRate = 10;//输出视频码率,默认10
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onProgress(float progress) {
                //这里获取处理进度
            }
        });
    }

    @JavascriptInterface
    public String hello(String msg) {
        test();
        Log.i("FangYan", msg);
        return new String("不服来干");
    }
}