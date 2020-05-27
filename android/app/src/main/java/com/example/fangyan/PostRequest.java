package com.example.fangyan;

import android.content.Context;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostRequest {
    private static final String TAG = "PostRequest";

    /**
     * 上传文件到服务器
     *
     * @param context
     * @param uploadUrl   上传服务器地址
     * @param oldFilePath 本地文件路径
     */
    public static void uploadVideo(Context context, String uploadUrl, String oldFilePath) {
        try {
            URL url = new URL("49.235.190.178:8080/login");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // 允许Input、Output，不使用Cache
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            con.setConnectTimeout(50000);
            con.setReadTimeout(50000);
            // 设置传送的method=POST
            con.setRequestMethod("POST");
            //在一次TCP连接中可以持续发送多份数据而不会断开连接
            con.setRequestProperty("Connection", "Keep-Alive");
            //设置编码
            con.setRequestProperty("Charset", "UTF-8");
            //text/plain能上传纯文本文件的编码格式
            con.setRequestProperty("Content-Type", "application/octet-stream");

            // 设置DataOutputStream
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());

            // 取得文件的FileInputStream
            FileInputStream fStream = new FileInputStream(oldFilePath);
            // 设置每次写入1024bytes
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int length = -1;
            // 从文件读取数据至缓冲区
            while ((length = fStream.read(buffer)) != -1) {
                // 将资料写入DataOutputStream中
                ds.write(buffer, 0, length);
            }
            ds.flush();
            fStream.close();
            ds.close();
            if (con.getResponseCode() == 200) {
                Log.d(TAG, "uploadLogFile: Success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "uploadLogFile: Failed");
        }
    }
}
