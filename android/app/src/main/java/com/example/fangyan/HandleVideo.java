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
    private String video = "file:///storage/emulated/0/download/Coca-Cola ads.mp4";

    public void test() {
        String output = "file:///storage/emulated/0/download/output.mp4";
        EpVideo epVideo = new EpVideo(video);
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

    public void getFrame() {
        String video = "\"file:///storage/emulated/0/download/Coca-Cola.mp4\"";
        String output = "\"file:///storage/emulated/0/download/output.jpeg\"";
        String cmd = "ffmpeg -i " + video + " -threads 1 -ss 00:00:05.167 -f image2 -r 1 -t 1 -s 256*256 " + output;
        EpEditor.execCmd(cmd, 0, new OnEditorListener() {
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

            }
        });
    }

    @JavascriptInterface
    public String hello(String msg) {
        getFrame();
        Log.i("FangYan", msg);
        return new String("不服来干");
    }
}