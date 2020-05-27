package com.example.fangyan;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PostRequest {
    private static final String TAG = "PostRequest";
    private static final String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO6jyH";

    public static void uploadVideo(String videoPath, String videoName) throws Exception {
        String urlStr = "http://47.95.220.161:8080/videos/importVdo";
        Map<String, String> textMap = new HashMap<String, String>();
        textMap.put("productName", videoName);
        Map<String, String> fileMap = new HashMap<String, String>();
        fileMap.put("file", videoPath);
        String ret = formUpload(urlStr, textMap, fileMap);
        System.out.println(ret);
    }

    /**
     * 提交表单
     *
     * @param urlStr
     * @param textMap
     * @param fileMap
     * @return
     * @throws Exception
     */
    public static String formUpload(String urlStr, Map<String, String> textMap, Map<String, String> fileMap)
            throws Exception {
        String boundary = BOUNDARY;
        StringBuffer contentBody = new StringBuffer("--" + BOUNDARY);
        String endBoundary = "\r\n--" + boundary + "--\r\n";

        // 1. 向服务器发送post请求
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 发送POST请求必须设置如下两行
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream out = connection.getOutputStream();
        // 2. 写入内容
        // 2.1 处理普通表单域(即形如key = value对)的POST请求
        for (String key : textMap.keySet()) {
            contentBody.append("\r\n").append("Content-Disposition: form-data; name=\"").append(key + "\"")
                    .append("\r\n").append("\r\n").append(textMap.get(key)).append("\r\n").append("--")
                    .append(boundary);
        }
        out.write(contentBody.toString().getBytes("UTF-8"));
        // 2.2 处理文件上传
        for (String key : fileMap.keySet()) {
            contentBody = new StringBuffer();
            contentBody.append("\r\n").append("Content-Disposition:form-data; name=\"").append(key + "\"; ")
                    // form中field的名称
                    .append("filename=\"").append(fileMap.get(key) + "\"")
                    // 上传文件的文件名，包括目录
                    .append("\r\n").append("Content-Type:multipart/form-data")
                    .append("\r\n\r\n");
            out.write(contentBody.toString().getBytes("utf-8"));
            // 开始真正向服务器写文件
            File file = new File(fileMap.get(key));
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[(int) file.length()];
            bytes = dis.read(bufferOut);
            out.write(bufferOut, 0, bytes);
            dis.close();
            contentBody.append(boundary);
            out.write(contentBody.toString().getBytes("utf-8"));
        }
        out.write((boundary + "--\r\n").getBytes("UTF-8"));
        // 3. 写结尾
        out.write(endBoundary.getBytes("utf-8"));
        out.flush();
        out.close();
        // 4. 获得服务器的响应结果和状态码
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            return changeInputStream(connection.getInputStream(), "utf-8");
        } else {
            return null;
        }
    }

    /**
     * 获得网络返回值
     *
     * @param inputStream
     * @param encode
     * @return
     */
    public static String changeInputStream(InputStream inputStream, String encode) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if (null != inputStream) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
                result = new String(outputStream.toByteArray(), encode);
            } catch (IOException e) {
                return null;
            }
        }
        Log.d(TAG, "changeInputStream: " + result);
        return result;
    }
}
