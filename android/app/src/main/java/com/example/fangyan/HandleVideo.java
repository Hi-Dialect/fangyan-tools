package com.example.fangyan;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.appcompat.app.AppCompatActivity;

import com.zlw.main.recorderlib.RecordManager;
import com.zlw.main.recorderlib.recorder.RecordConfig;
import com.zlw.main.recorderlib.recorder.listener.RecordResultListener;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Path;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import me.rosuh.filepicker.config.FilePickerManager;

public class HandleVideo extends Object {
    private RecordManager recordManager;
    private AppCompatActivity appCompatActivity;

    private static final String TAG = "FFmpeg";
    private static String basicPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String tempVideoPath = "/Hi-Dialect/temp/video/";
    private static String userVideoPath = "/Hi-Dialect/myVideo/";
    private static String tempRecordingPath = "/Hi-Dialect/temp/recording/";
    private static String inputPath = basicPath + tempVideoPath + "input.mp4";
    private static String outputPath = basicPath + tempVideoPath + "output.mp4";
    private static String finalVideoPath = basicPath + tempVideoPath + "final.mp4";
    private static String framePath = "/storage/emulated/0/Hi-Dialect/temp/frame%3d.jpg";

    private String backgroundMusicPath = null;
    private String dialectPath = null;
    private float startTime = 0;
    private float endTime = 1;
    private boolean isMuted = false;
    private boolean isSaveToLocal = false;
    private String localSaveName = null;
    private boolean isRecordStart = false;
    private boolean isRecordCancel = false;
    private String recordType = null;
    private String filter = null;

    public HandleVideo(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public String getTempVideoPath() {
        return basicPath + tempVideoPath;
    }

    public String getTempRecordingPath() {
        return basicPath + tempRecordingPath;
    }

    private void sendProgressMessage(float percentage) {
        Intent intent = new Intent("com.nangch.broadcasereceiver.MYRECEIVER");
        intent.putExtra("type", "updateRenderProgress");
        intent.putExtra("percentage", percentage);
        intent.putExtra("filePath", "file://" + finalVideoPath);
        appCompatActivity.sendBroadcast(intent);
    }

    //第一步，选择剪辑范围并添加滤镜
    private void videoClip() {
        EpVideo epVideo = new EpVideo(inputPath);
        epVideo.clip(startTime, endTime - startTime);
        if (filter != null) epVideo.addFilter(filter);
        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outputPath);
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: videoClip");
                FileManager.saveFileToSDCardCustomDir(FileManager.loadFileFromSDCard(outputPath),
                        tempVideoPath, "input.mp4");
                FileManager.removeFileFromSDCard(outputPath);
                sendProgressMessage(50);
                mute();
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "onFailure: videoClip");
            }

            @Override
            public void onProgress(float progress) {
                if (progress < 1) sendProgressMessage(50 * progress);
                Log.d(TAG, "onProgress: videoClip");
            }
        });
    }

    //第二步，选择是否消除原视频的音频
    private void mute() {
        if (isMuted) {
            EpEditor.demuxer(inputPath, outputPath, EpEditor.Format.MP4, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: mute");
                    FileManager.saveFileToSDCardCustomDir(FileManager.loadFileFromSDCard(outputPath),
                            tempVideoPath, "input.mp4");
                    FileManager.removeFileFromSDCard(outputPath);
                    sendProgressMessage(65);
                    addBackgroundMusic();
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "onFailure: mute");
                }

                @Override
                public void onProgress(float progress) {
                    if (progress < 1) sendProgressMessage(50 + 15 * progress);
                    Log.d(TAG, "onProgress: mute");
                }
            });
        } else {
            sendProgressMessage(65);
            addBackgroundMusic();
        }
    }

    //第三步，选择是否添加背景音乐
    private void addBackgroundMusic() {
        if (backgroundMusicPath != null && backgroundMusicPath.length() > 0) {
            EpEditor.music(inputPath, backgroundMusicPath, outputPath, 1, 1, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: addBackgroundMusic");
                    FileManager.saveFileToSDCardCustomDir(FileManager.loadFileFromSDCard(outputPath),
                            tempVideoPath, "input.mp4");
                    FileManager.removeFileFromSDCard(outputPath);
                    sendProgressMessage(80);
                    addDialect();
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "onFailure: addBackgroundMusic");
                }

                @Override
                public void onProgress(float progress) {
                    if (progress < 1) sendProgressMessage(65 + 15 * progress);
                    Log.d(TAG, "onProgress: addBackgroundMusic");
                }
            });
        } else {
            Log.d(TAG, "addBackgroundMusic: backgroundMusicPath Wrong!");
            sendProgressMessage(80);
            addDialect();
        }
    }

    //第四步，选择是否添加方言配音
    private void addDialect() {
        if (dialectPath != null && dialectPath.length() > 0) {
            EpEditor.music(inputPath, dialectPath, outputPath, 1, 1, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: addDialect");
                    FileManager.saveFileToSDCardCustomDir(FileManager.loadFileFromSDCard(outputPath),
                            tempVideoPath, "input.mp4");
                    FileManager.removeFileFromSDCard(outputPath);
                    sendProgressMessage(95);
                    finalStep();
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "onFailure: addDialect");
                }

                @Override
                public void onProgress(float progress) {
                    if (progress < 1) sendProgressMessage(80 + 15 * progress);
                    Log.d(TAG, "onProgress: addDialect");
                }
            });
        } else {
            Log.d(TAG, "addDialect: dialectPath Wrong!");
            sendProgressMessage(95);
            finalStep();
        }
    }

    //最后一步，处理文件存储
    private void finalStep() {
        //生成随机的临时文件名
        String tempFileName = new String(System.currentTimeMillis() + ".mp4");

        finalVideoPath = finalVideoPath.substring(0, finalVideoPath.lastIndexOf("/") + 1) + tempFileName;
        //生成临时文件（退出程序时会自动删除）
        FileManager.saveFileToSDCardCustomDir(FileManager.loadFileFromSDCard(inputPath),
                tempVideoPath, tempFileName);
        //生成永久文件（不会自动删除，由用户选择是否保存）
        if (isSaveToLocal) {
            FileManager.saveFileToSDCardCustomDir(FileManager.loadFileFromSDCard(inputPath),
                    userVideoPath, localSaveName);
        }
        //更新进度，完成渲染
        sendProgressMessage(100);
        //删除"FFmpeg"处理的中间文件
        FileManager.removeFileFromSDCard(inputPath);
    }

    @JavascriptInterface
    public void uploadVideoToServer(String videoPath, String videoName, String videoRemark, String posterPath,
                                    int userNo, int isPublic, int[] labels) {
        try {
            String url = new PostRequest().uploadVideo(videoPath, videoName);
            String success = new PostRequest().addVideo(url, videoName, videoRemark, posterPath, userNo,
                    0, isPublic, labels);
            Log.d(TAG, "finalStep: " + success);
        } catch (Exception e) {
            Log.d(TAG, "finalStep: " + e.getMessage());
        }
    }

    @JavascriptInterface
    public void renderVideo(String videoPath, String backgroundMusicPath, String dialectPath,
                            String startTime, String endTime, int filter, boolean isMuted,
                            boolean isSaveToLocal, String localSaveName) {
        this.backgroundMusicPath = backgroundMusicPath;
        this.dialectPath = dialectPath;
        this.startTime = Float.parseFloat(startTime);
        this.endTime = Float.parseFloat(endTime);
        this.isMuted = isMuted;
        this.isSaveToLocal = isSaveToLocal;
        this.localSaveName = localSaveName;
        switch (filter) {
            case 2: //底片效果
                this.filter = "lutyuv=y=maxval+minval-val:u=maxval+minval-val:v=maxval+minval-val";
                break;
            default: //其他情况不添加滤镜
                this.filter = null;
                break;
        }
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
    public void getVideoFrames(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        //获取视频长宽，注意区分横竖屏拍摄造成的影响
        int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        //截取视频帧供前端使用
        EpEditor.video2pic(videoPath, framePath, width, height, 1, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: getVideoFrames");
                Intent intent = new Intent("com.nangch.broadcasereceiver.MYRECEIVER");
                intent.putExtra("type", "loadFrames");
                intent.putExtra("filePath", "file:///storage/emulated/0" + tempVideoPath);
                appCompatActivity.sendBroadcast(intent);
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "onFailure: getVideoFrames");
            }

            @Override
            public void onProgress(float progress) {
                Log.d(TAG, "onProgress: getVideoFrames");
            }
        });
    }

    @JavascriptInterface
    public void selectFile(int type) {
        switch (type) {
            case 1: //选择原始视频
                FilePickerManager.INSTANCE.from(appCompatActivity).forResult(1);
                break;
            case 2: //录制视频
                Intent intent2 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                //设置拍摄的视频质量
                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                //录制视频最大时长为一分钟
                intent2.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
                appCompatActivity.startActivityForResult(intent2, 2);
                break;
            case 3: //选择背景音乐
                FilePickerManager.INSTANCE.from(appCompatActivity).forResult(3);
                break;
            case 4: //录制背景音乐
                Intent intent4 = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                appCompatActivity.startActivityForResult(intent4, 4);
                break;
            case 5: //选择方言配音
                FilePickerManager.INSTANCE.from(appCompatActivity).forResult(5);
                break;
            case 6: //录制方言配音
                Intent intent6 = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                appCompatActivity.startActivityForResult(intent6, 6);
                break;
        }
    }

    @JavascriptInterface
    public void startRecord() {
        //配置录音模块
        recordManager = RecordManager.getInstance();
        recordManager.init(appCompatActivity.getApplication(), false);
        recordManager.changeFormat(RecordConfig.RecordFormat.MP3);
        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(16000));
        recordManager.changeRecordConfig(recordManager.getRecordConfig().setEncodingConfig(AudioFormat.ENCODING_PCM_8BIT));
        recordManager.changeRecordDir(basicPath + tempRecordingPath);
        //添加监听函数
        recordManager.setRecordResultListener(new RecordResultListener() {
            @Override
            public void onResult(File result) {
                if (!isRecordCancel) {
                    Intent intent1 = new Intent("com.nangch.broadcasereceiver.MYRECEIVER");
                    intent1.putExtra("type", recordType);
                    intent1.putExtra("filePath", "file://" + result.getAbsolutePath());
                    appCompatActivity.sendBroadcast(intent1);
                } else {
                    isRecordCancel = false;
                }
            }
        });
        //开始录音
        recordManager.start();
        isRecordStart = true;
    }

    @JavascriptInterface
    public void stopRecord(int type) {
        switch (type) {
            case -1: //暂停和继续录音
                if (isRecordStart == true) recordManager.pause();
                else recordManager.resume();
                isRecordStart = !isRecordStart;
                break;
            case 0: //取消录音
                isRecordCancel = true;
                recordManager.stop();
            case 1: //添加背景音乐
                recordType = "addBackgroundMusic";
                recordManager.stop();
                break;
            case 2: //添加方言配音
                recordType = "addDialect";
                recordManager.stop();
                break;
        }
    }
}