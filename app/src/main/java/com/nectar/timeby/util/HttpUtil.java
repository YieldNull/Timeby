package com.nectar.timeby.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by finalize on 7/14/15.
 */
public class HttpUtil {
    public static final String URL_SERVER_BASE = "http://10.0.3.2:5000/";
    public static final String URL_LOGIN_LOGIN = URL_SERVER_BASE + "login/login";
    public static final String URL_LOGIN_REGISTER = URL_SERVER_BASE + "login/register";
    public static final String URL_LOGIN_RESET = URL_SERVER_BASE + "login/resetPassword";
    public static final String URL_LOGIN_CHECK = URL_SERVER_BASE + "login/checkValid";
    private static final String TAG = "HTTPUtil";

    /**
     * 判断是否能连上网
     *
     * @param context
     * @return
     */
    public static boolean isNetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //并没有打开网络设备
        if (networkInfo == null)
            return false;
        else
            return true;

    }

    /**
     * 通过post方式连接服务器
     *
     * @param url
     * @param datas 参数键值对
     * @return 得到的JSON数据，解析失败则返回null
     * @throws IOException
     */
    public static JSONObject doPost(String url, Map<String, String> datas) throws IOException {

        //设置连接参数
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        //获取连接，将参数写入请求包
        connection.connect();
        OutputStream outputStream = connection.getOutputStream();

        //用URL格式编码参数
        StringBuilder param = new StringBuilder();
        for (Map.Entry<String, String> entry : datas.entrySet()) {
            param.append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                    .append("&");
        }
        param.deleteCharAt(param.length() - 1);//删除末尾的‘&’
        Log.i(TAG, url + "?" + param.toString());

        //以"UTF-8"编码写入请求流
        byte[] data = new String(param.toString().getBytes(), "UTF-8").getBytes();
        outputStream.write(data);
        outputStream.flush();
        outputStream.close();

        //打开服务器返回的输入流，获取服务器返回的数据
        InputStream responseStream = null;
        try {
            responseStream = connection.getInputStream();
        } catch (FileNotFoundException e) {
            responseStream = connection.getErrorStream();
        }
        return readResponse(responseStream);
    }

    /**
     * 使用get方式连接服务器
     *
     * @param url
     * @return 得到的JSON数据，解析失败则返回null
     * @throws IOException
     */
    public static JSONObject doGet(String url) throws IOException {
        //设置连接参数
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setRequestMethod("GET");

        //获取连接，将参数写入请求包
        connection.connect();

        //打开服务器返回的输入流，获取服务器返回的数据
        InputStream responseStream = null;
        try {
            responseStream = connection.getInputStream();
        } catch (FileNotFoundException e) {
            responseStream = connection.getErrorStream();
        }

        return readResponse(responseStream);
    }


    /**
     * 读取服务器返回的数据流，将其解析为JSON
     *
     * @param responseStream
     * @return 返回的数据无法解析为JSON对象则返回null
     * @throws IOException
     */
    private static JSONObject readResponse(InputStream responseStream) throws IOException {

        ByteArrayOutputStream resultOutStream = new ByteArrayOutputStream();

        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = responseStream.read(buffer)) != -1) {
            resultOutStream.write(buffer, 0, len);
        }
        responseStream.close();
        String response = new String(resultOutStream.toByteArray());

        JSONObject result = null;
        try {
            result = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "received invalid JSON string:\n" + response);
        }
        return result;
    }
}
