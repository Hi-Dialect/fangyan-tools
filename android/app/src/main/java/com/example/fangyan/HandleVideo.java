package com.example.fangyan;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.appcompat.app.AppCompatActivity;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;
import me.rosuh.filepicker.config.FilePickerManager;

public class HandleVideo extends Object {
    public AppCompatActivity appCompatActivity;
    private static final String TAG = "FFmpeg";

    public HandleVideo(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public void getFrame(String videoPath, String outputPath) {
        String cmd = "-i " + videoPath + " -y -f image2 -ss 00:00:01 -vframes 1 " + outputPath;
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
                Log.d(TAG, "onProgress: ");
            }
        });
    }

    public void mute(String videoPath, String outputPath) {
        EpEditor.demuxer(videoPath, outputPath, EpEditor.Format.MP4, new OnEditorListener() {
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
                Log.d(TAG, "onProgress: ");
            }
        });
    }

    public void addMusic(String videoPath, String audioPath, String outputPath) {
        EpEditor.music(videoPath, audioPath, outputPath, 1, 1, new OnEditorListener() {
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
                Log.d(TAG, "onProgress: ");
            }
        });
    }

    @JavascriptInterface
    public void command(int type) {
        String video = "file:///storage/emulated/0/download/Coca-Cola.mp4";
        String music = "file:///storage/emulated/0/download/music.mp3";
        String output = "file:///storage/emulated/0/download/output.mp4";

        switch (type) {
            case 1:
                FilePickerManager.INSTANCE.from(appCompatActivity).forResult(1);
                break;
            case 2:
                FilePickerManager.INSTANCE.from(appCompatActivity).forResult(2);
                break;
            case 3:
                FilePickerManager.INSTANCE.from(appCompatActivity).forResult(3);
                break;
        }
    }
}