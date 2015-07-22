package com.example.caelum.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * *********************************************
 * Created by caelum on 2015/7/12.
 * 功能：与服务器进行交互
 * 使用：
 * <p/>
 * String username = "";
 * String password = "";
 * <p/>
 * Map param1 = new HashMap();
 * param1.put("username", username);
 * param1.put("password", password);
 * String str = HttpUtil.doGet(HttpUtil.BASEURL + "*", param1);
 * <p/>
 * <p/>
 * JSONObject jsonObject = new JSONObject();
 * try {
 * jsonObject.put("username", username);
 * jsonObject.put("password", password);
 * } catch (JSONException e) {
 * e.printStackTrace();
 * }
 * String param2 = jsonObject.toString();
 * String str = HttpUtil.doPost(HttpUtil.BASEURL + "*", param2);
 * <p/>
 * **********************************************
 */

public class HttpUtil {
    public static final String BASEURL = "http://caelum.oicp.net:22468/Timeby/";

    //public static final String BASEURL = "http://192.168.1.101:8080/Timeby/";

    //public static final String BASEURL = "http://1.timeby2015.sinaapp.com/";
    //设置httpClient
    private static HttpClient getHttpClient() {
        HttpParams httpParams = new BasicHttpParams();
        //设置连接超时和 Socket 超时，以及 Socket 缓存大小
        HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        //设置重定向，缺省为 true
        HttpClientParams.setRedirecting(httpParams, true);
        //设置 user agent
        // String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
        // HttpProtocolParams.setUserAgent(httpParams, userAgent);
        //创建一个 HttpClient 实例
        HttpClient httpClient = new DefaultHttpClient(httpParams);

        return httpClient;
    }

    //Get请求
    public static String doGet(String url, Map params) {
        String paramStr = "";
        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            paramStr += paramStr = "&" + key + "=" + val;
        }
        if (!paramStr.equals("")) {
            paramStr = paramStr.replaceFirst("&", "?");
            url += paramStr;
        }
        System.out.println(url);
        HttpGet httpGet = new HttpGet(url);
        String resultStr = "";
        String errorStr = "";
        JSONArray resultJson = new JSONArray();
        JSONObject statusJson = new JSONObject();
        try {
            statusJson.put("status", -1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            HttpResponse httpResponse = getHttpClient().execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                resultStr = EntityUtils.toString(httpResponse.getEntity());
            } else {
                errorStr = "Result: Error Response: " + httpResponse.getStatusLine().toString();
                try {
                    statusJson.put("errorStr", errorStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                resultJson.put(statusJson);
                resultStr = resultJson.toString();
            }
        } catch (IOException e) {
            errorStr = e.getMessage().toString();
            try {
                statusJson.put("errorStr", errorStr);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            resultJson.put(statusJson);
            e.printStackTrace();
        }
        return resultStr;
    }

    //Post请求
    public static String doPost(String url, String params) {
        HttpPost httpPost = new HttpPost(url);
        String resultStr = "";
        String errorStr = "";
        JSONArray resultJson = new JSONArray();
        JSONObject statusJson = new JSONObject();
        try {
            statusJson.put("status", -1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            StringEntity stringEntity = new StringEntity(params, "utf-8");
            stringEntity.setContentEncoding("utf-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = getHttpClient().execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                resultStr = EntityUtils.toString(httpResponse.getEntity());
            } else {
                errorStr = "Error Response: " + httpResponse.getStatusLine().toString();
                try {
                    statusJson.put("errorStr", errorStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                resultJson.put(statusJson);
                resultStr = resultJson.toString();
            }
        } catch (UnsupportedEncodingException e) {
            errorStr = e.getMessage().toString();
            try {
                statusJson.put("errorStr", errorStr);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            resultJson.put(statusJson);
            //map.put("errorStr", errorStr);
            //resultJson.add(map);
            //resultJson.put(map);
            resultStr = resultJson.toString();
            e.printStackTrace();
        } catch (IOException ie) {
            errorStr = ie.getMessage().toString();
            try {
                statusJson.put("errorStr", errorStr);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            resultJson.put(statusJson);
            resultStr = resultJson.toString();
            ie.printStackTrace();
        }
        return resultStr;
    }

    //上传图片
//    public static String uploadByPost(String url, File file, String name)
//    {
//        HttpPost httpPost = new HttpPost(url);
//        HttpClient httpClient = getHttpClient();
//        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
//        //httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30 * 1000);
//        String resultStr = "";
//        String errorStr = "";
//        JSONArray resultJson = new JSONArray();
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("status", -1);
//        try {
//            FileBody fileBody = new FileBody(file);
//            StringBody fileName = new StringBody(name, Charset.forName("utf-8"));
//            MultipartEntity entity = new MultipartEntity();
//            entity.addPart("fileName", fileName);
//            entity.addPart("fileBody", fileBody);
//            httpPost.setEntity(entity);
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
//            {
//                resultStr = EntityUtils.toString(httpResponse.getEntity());
//            } else {
//                errorStr = "Error Response: " + httpResponse.getStatusLine().toString();
//                map.put("errorStr", errorStr);
//                resultJson.add(map);
//                resultStr = resultJson.toString();
//            }
//        } catch (ClientProtocolException e) {
//            errorStr = e.getMessage().toString();
//            map.put("errorStr", errorStr);
//            resultJson.add(map);
//            resultStr = resultJson.toString();
//            e.printStackTrace();
//        } catch (IOException e) {
//            errorStr = e.getMessage().toString();
//            map.put("errorStr", errorStr);
//            resultJson.add(map);
//            resultStr = resultJson.toString();
//            e.printStackTrace();
//        }
//        return resultStr;
//    }
//
//    //下载图片
//    public static List<Map<String, Object>> downloadImage(Map<String, String> params, String fileDir)
//    {
//        HttpClient client = getHttpClient();
//        //Map<String, File> files = new HashMap();
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        for (Map.Entry<String, String> entry : params.entrySet())
//        {
//            HttpGet httpGet = new HttpGet(entry.getValue());
//            HttpResponse httpResponse;
//            try {
//                httpResponse = client.execute(httpGet);
//                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
//                {
//                    String filePath = entry.getValue().substring(entry.getValue().lastIndexOf("/") + 1);
//                    filePath = fileDir + filePath;
//                    File file = new File(filePath);
//                    FileOutputStream out = new FileOutputStream(file);
//                    InputStream in = httpResponse.getEntity().getContent();
//                    byte b[] = new byte[1024];
//                    int n =0;
//                    while ((n = in.read(b)) != -1)
//                    {
//                        out.write(b, 0, n);
//                    }
//                    Map<String, Object> map = new HashMap<String, Object>();
//                    map.put("status", 1);
//                    map.put("phoneNum", entry.getKey());
//                    map.put("file", file);
//                    list.add(map);
//                    out.flush();
//                    out.close();
//                } else {
//                    String errorStr = "Error Response: " + httpResponse.getStatusLine().toString();
//                    Map<String, Object> map = new HashMap<String, Object>();
//                    map.put("status", -1);
//                    map.put("errorStr", errorStr);
//                    list.add(map);
//                }
//            } catch (ClientProtocolException e) {
//                // TODO Auto-generated catch block
//                Map<String, Object> map = new HashMap<String, Object>();
//                String errorStr = e.getMessage().toString();
//                map.put("status", -1);
//                map.put("errorStr", errorStr);
//                list.add(map);
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                Map<String, Object> map = new HashMap<String, Object>();
//                String errorStr = e.getMessage().toString();
//                map.put("status", -1);
//                map.put("errorStr", errorStr);
//                list.add(map);
//                e.printStackTrace();
//            }
//
//        }
//        return list;
//    }
}
