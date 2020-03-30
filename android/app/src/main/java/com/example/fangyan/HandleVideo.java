package com.example.fangyan;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import me.rosuh.filepicker.config.FilePickerManager;

public class HandleVideo extends Object {
    private AppCompatActivity appCompatActivity;
    private static final String TAG = "FFmpeg";
    private String tempPath = "file:///storage/emulated/0/download/temp.mp4";
    private String outputPath = "file:///storage/emulated/0/download/output.mp4";

    private String videoPath = null;
    private String backgroundMusicPath = null;
    private String dialectPath = null;
    private int startTime = 0;
    private int endTime = 10;
    private boolean isMuted = false;

    public HandleVideo(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public void getFrame(String outputPath) {
        String cmd = "-i " + videoPath + " -y -f image2 -ss 00:00:01 -vframes 1 " + outputPath;
        EpEditor.execCmd(cmd, 0, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: getFrame");
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "onFailure: getFrame");
            }

            @Override
            public void onProgress(float progress) {
                Log.d(TAG, "onProgress: getFrame");
            }
        });
    }

    //第一步，选择是否消除原视频的音频
    public void mute() {
        if (isMuted) {
            EpEditor.demuxer(videoPath, tempPath, EpEditor.Format.MP4, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: mute");
                    addBackgroundMusic();
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "onFailure: mute");
                }

                @Override
                public void onProgress(float progress) {
                    Log.d(TAG, "onProgress: mute");
                }
            });
        } else {
            addBackgroundMusic();
        }
    }

    //第二步，选择是否添加背景音乐
    public void addBackgroundMusic() {
        if (backgroundMusicPath != null && backgroundMusicPath.length() > 0) {
            EpEditor.music(videoPath, backgroundMusicPath, tempPath, 1, 1, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: addBackgroundMusic");
                    addDialect();
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "onFailure: addBackgroundMusic");
                }

                @Override
                public void onProgress(float progress) {
                    Log.d(TAG, "onProgress: addBackgroundMusic");
                }
            });
        } else {
            Log.d(TAG, "addBackgroundMusic: backgroundMusicPath Wrong!");
            addDialect();
        }
    }

    //第三步，选择是否添加方言配音
    public void addDialect() {
        if (dialectPath != null && dialectPath.length() > 0) {
            EpEditor.music(videoPath, dialectPath, tempPath, 1, 1, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: addDialect");
                    videoClip();
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "onFailure: addDialect");
                }

                @Override
                public void onProgress(float progress) {
                    Log.d(TAG, "onProgress: addDialect");
                }
            });
        } else {
            Log.d(TAG, "addDialect: dialectPath Wrong!");
            videoClip();
        }
    }

    //第四步，选择剪辑范围
    public void videoClip() {
        EpVideo epVideo = new EpVideo(tempPath);
        Log.d(TAG, "videoClip: " + startTime);
        Log.d(TAG, "videoClip: " + endTime);
        epVideo.clip(startTime, endTime - startTime);
        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outputPath);
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: videoClip");
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "onFailure: videoClip");
            }

            @Override
            public void onProgress(float progress) {
                Log.d(TAG, "onProgress: videoClip");
            }
        });
    }

    @JavascriptInterface
    public void selectFile(int type) {
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

    @JavascriptInterface
    public void renderVideo(String videoPath, String startTime, String endTime, boolean isMuted,
                            String backgroundMusicPath, String dialectPath) {
        this.videoPath = videoPath;
        this.startTime = Integer.parseInt(startTime);
        this.endTime = Integer.parseInt(endTime);
        this.isMuted = isMuted;
        this.backgroundMusicPath = backgroundMusicPath;
        this.dialectPath = dialectPath;

        //异步操作临时解决方案：消音->添加背景音乐->添加方言配音->剪辑输出
        if (videoPath == null || videoPath.length() <= 0) {
            Log.d(TAG, "renderVideo: videoPath Wrong!");
        } else {
            mute();
        }
    }
}