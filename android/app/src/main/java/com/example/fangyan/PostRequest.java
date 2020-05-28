package com.example.fangyan;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PostRequest {
    private static final String TAG = "PostRequest";
    private static final String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO6jyH";

    /**
     * 上传视频至服务器
     *
     * @param videoPath
     * @param videoName
     * @return
     * @throws Exception
     */
    public static String uploadVideo(String videoPath, String videoName) throws Exception {
        String urlStr = "http://47.95.220.161:8080/videos/importVdo";
        Map<String, String> textMap = new HashMap<String, String>();
        textMap.put("productName", videoName);
        Map<String, String> fileMap = new HashMap<String, String>();
        fileMap.put("file", videoPath);
        String ret = formUpload(urlStr, textMap, fileMap);
        return ret;
    }

    /**
     * 添加视频信息至服务器
     *
     * @param videoPath
     * @param videoName
     * @param videoRemark
     * @param posterPath
     * @param userNo
     * @param isPublic
     * @return
     * @throws Exception
     */
    public static String addVideo(String videoPath, String videoName, String videoRemark, String posterPath,
                                  int userNo, int videoType, int isPublic, int[] labels) throws Exception {
        String urlStr = "http://47.95.220.161:8080/videos/addVdo";
        JSONObject jsonParam = new JSONObject();

        jsonParam.put("vdoNa", videoName);
        jsonParam.put("vdoRemark", videoRemark);
        jsonParam.put("userNo", "" + userNo);
        jsonParam.put("vdoPath", videoPath);
        jsonParam.put("vdoImg", posterPath);
        jsonParam.put("vdoType", "" + videoType);
        jsonParam.put("isPublic", "" + isPublic);

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < labels.length; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("labelId", labels[i]);
            jsonArray.put(jsonObject);
        }

        jsonParam.put("videoLabels", jsonArray);
        String data = getJsonData(jsonParam, urlStr);
        return data;
    }

    public static String getJsonData(JSONObject jsonParam, String urls) {
        StringBuffer sb = new StringBuffer();
        try {
            // 创建url资源
            URL url = new URL(urls);
            // 建立http连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 转换为字节数组
            byte[] data = (jsonParam.toString()).getBytes();
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", "application/json");
            // 开始连接请求
            conn.connect();
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 写入请求的字符串
            out.write((jsonParam.toString()).getBytes());
            out.flush();
            out.close();
            System.out.println(conn.getResponseCode());

            // 请求返回的状态
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                System.out.println("连接成功");
                // 请求返回的数据
                InputStream in1 = conn.getInputStream();
                try {
                    String readLine = new String();
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(in1, "UTF-8"));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                    System.out.println(sb.toString());

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                System.out.println("error++");
            }
        } catch (Exception e) {
            Log.d(TAG, "getJsonData: " + e.getMessage());
        }
        return sb.toString();
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
