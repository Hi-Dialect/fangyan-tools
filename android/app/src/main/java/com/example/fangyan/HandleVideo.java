package com.example.fangyan;

import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.appcompat.app.AppCompatActivity;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import me.rosuh.filepicker.config.FilePickerManager;

public class HandleVideo extends Object {
    private AppCompatActivity appCompatActivity;

    private static final String TAG = "FFmpeg";
    private static String inputPath = "/storage/emulated/0/Hi-Dialect/temp/input.mp4";
    private static String outputPath = "/storage/emulated/0/Hi-Dialect/temp/output.mp4";
    private static String finalPath = "/storage/emulated/0/Hi-Dialect/temp/final.mp4";
    private static String tempVideoPath = "/Hi-Dialect/temp/";

    private String backgroundMusicPath = null;
    private String dialectPath = null;
    private int startTime = 0;
    private int endTime = 1;
    private boolean isMuted = false;

    public HandleVideo(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public void sendProgressMessage(int percentage) {
        Intent intent = new Intent("com.nangch.broadcasereceiver.MYRECEIVER");
        intent.putExtra("type", "updateRenderProgress");
        intent.putExtra("percentage", percentage);
        intent.putExtra("filePath", "file://" + finalPath);
        appCompatActivity.sendBroadcast(intent);
    }

    //第一步，选择剪辑范围
    public void videoClip() {
        EpVideo epVideo = new EpVideo(inputPath);
        epVideo.clip(startTime, endTime - startTime);
        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outputPath);
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: videoClip");
                FileManager.saveFileToSDCardCustomDir(FileManager.loadFileFromSDCard(outputPath),
                        tempVideoPath, "input.mp4");
                FileManager.removeFileFromSDCard(outputPath);
                sendProgressMessage(25);
                mute();
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

    //第二步，选择是否消除原视频的音频
    public void mute() {
        if (isMuted) {
            EpEditor.demuxer(inputPath, outputPath, EpEditor.Format.MP4, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: mute");
                    FileManager.saveFileToSDCardCustomDir(FileManager.loadFileFromSDCard(outputPath),
                            tempVideoPath, "input.mp4");
                    FileManager.removeFileFromSDCard(outputPath);
                    sendProgressMessage(50);
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
            sendProgressMessage(50);
            addBackgroundMusic();
        }
    }

    //第三步，选择是否添加背景音乐
    public void addBackgroundMusic() {
        if (backgroundMusicPath != null && backgroundMusicPath.length() > 0) {
            EpEditor.music(inputPath, backgroundMusicPath, outputPath, 1, 1, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: addBackgroundMusic");
                    FileManager.saveFileToSDCardCustomDir(FileManager.loadFileFromSDCard(outputPath),
                            tempVideoPath, "input.mp4");
                    FileManager.removeFileFromSDCard(outputPath);
                    sendProgressMessage(75);
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
            sendProgressMessage(75);
            addDialect();
        }
    }

    //第四步，选择是否添加方言配音
    public void addDialect() {
        if (dialectPath != null && dialectPath.length() > 0) {
            EpEditor.music(inputPath, dialectPath, outputPath, 1, 1, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: addDialect");
                    FileManager.saveFileToSDCardCustomDir(FileManager.loadFileFromSDCard(outputPath),
                            tempVideoPath, "input.mp4");
                    FileManager.removeFileFromSDCard(outputPath);
                    sendProgressMessage(99);
                    finalStep();
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
            sendProgressMessage(99);
            finalStep();
        }
    }

    //最后一步
    public void finalStep() {
        //生成随机的临时文件名
        String tempFileName = new String(System.currentTimeMillis() + ".mp4");

        finalPath = finalPath.substring(0, finalPath.lastIndexOf("/") + 1) + tempFileName;
        FileManager.saveFileToSDCardCustomDir(FileManager.loadFileFromSDCard(inputPath),
                tempVideoPath, tempFileName);
        FileManager.removeFileFromSDCard(inputPath);
        sendProgressMessage(100);
    }

    @JavascriptInterface
    public void renderVideo(String videoPath, String startTime, String endTime, boolean isMuted,
                            String backgroundMusicPath, String dialectPath) {
        this.startTime = Integer.parseInt(startTime);
        this.endTime = Integer.parseInt(endTime);
        this.isMuted = isMuted;
        this.backgroundMusicPath = backgroundMusicPath;
        this.dialectPath = dialectPath;

        //异步操作临时解决方案：剪辑->消音->添加背景音乐->添加方言配音
        if (videoPath == null || videoPath.length() <= 0) {
            Log.d(TAG, "renderVideo: videoPath Wrong!");
            Intent intent = new Intent("com.nangch.broadcasereceiver.MYRECEIVER");
            intent.putExtra("type", "alertError");
            intent.putExtra("message", "无法读取视频文件");
            appCompatActivity.sendBroadcast(intent);
        } else {
            //FileManager类操控外部储存文件，路径以"/storage"为前缀
            videoPath = videoPath.substring(videoPath.indexOf("/storage"));
            //加载原始视频
            byte[] bytes = FileManager.loadFileFromSDCard(videoPath);
            //视频文件重定向（复制移动）
            FileManager.saveFileToSDCardCustomDir(bytes, tempVideoPath, "input.mp4");
            //开始渲染视频
            sendProgressMessage(0);
            videoClip();
        }
    }

    @JavascriptInterface
    public void selectFile(int type) {
        switch (type) {
            case 1: //选择背景音乐
                FilePickerManager.INSTANCE.from(appCompatActivity).forResult(1);
                break;
            case 2: //选择方言配音
                FilePickerManager.INSTANCE.from(appCompatActivity).forResult(2);
                break;
            case 3: //选择原始视频
                FilePickerManager.INSTANCE.from(appCompatActivity).forResult(3);
                break;
            case 4: //录制视频
                Intent intent4 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                //设置拍摄的视频质量
                intent4.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                //录制视频最大时长为一分钟
                intent4.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
                appCompatActivity.startActivityForResult(intent4, 4);
                break;
            case 5: //录制音频
                Intent intent5 = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                appCompatActivity.startActivityForResult(intent5, 5);
                break;
        }
    }
}