package com.nectar.timeby.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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
 * ¹ŠÄÜ£ºÓë·þÎñÆ÷œøÐÐœ»»¥
 * Ê¹ÓÃ£º
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
    //public static final String BASEURL = "http://caelum.oicp.net:22468/Timeby/";

    //public static final String BASEURL = "http://192.168.1.101:8080/Timeby/";

    public static final String BASEURL = "http://1.timeby2015.sinaapp.com/";

    //public static final String BASEURL = "http://caelum.oicp.net:40018/Timeby/";

    //ÉèÖÃhttpClient
    private static HttpClient getHttpClient() {
        HttpParams httpParams = new BasicHttpParams();
        //ÉèÖÃÁ¬œÓ³¬Ê±ºÍ Socket ³¬Ê±£¬ÒÔŒ° Socket »ºŽæŽóÐ¡
        HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        //ÉèÖÃÖØ¶šÏò£¬È±Ê¡Îª true
        HttpClientParams.setRedirecting(httpParams, true);
        //ÉèÖÃ user agent
        // String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
        // HttpProtocolParams.setUserAgent(httpParams, userAgent);
        //ŽŽœšÒ»žö HttpClient ÊµÀý
        HttpClient httpClient = new DefaultHttpClient(httpParams);

        return httpClient;
    }

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

    //GetÇëÇó
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

    //PostÇëÇó
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

    //resultStrÉÏŽ«ÍŒÆ¬
    public static String uploadByPost(String url, File file, String name) {
        HttpPost httpPost = new HttpPost(url);
        HttpClient httpClient = getHttpClient();
        // httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        //httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30 * 1000);
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
            FileBody fileBody = new FileBody(file);
            StringBody fileName = new StringBody(name, Charset.forName("utf-8"));
            MultipartEntity entity = new MultipartEntity();
            entity.addPart("fileName", fileName);
            entity.addPart("fileBody", fileBody);
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
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
        } catch (ClientProtocolException e) {
            errorStr = e.getMessage().toString();
            try {
                statusJson.put("errorStr", errorStr);
            } catch (JSONException je) {
                je.printStackTrace();
            }
            resultJson.put(statusJson);
            resultStr = resultJson.toString();
            e.printStackTrace();
        } catch (IOException e) {
            errorStr = e.getMessage().toString();
            try {
                statusJson.put("errorStr", errorStr);
            } catch (JSONException je) {
                je.printStackTrace();
            }
            resultJson.put(statusJson);
            resultStr = resultJson.toString();
            e.printStackTrace();
        }
        return resultStr;
    }

    //ÏÂÔØÍŒÆ¬
    public static List<Map<String, Object>> downloadImage(List<String> phoneNumList, String fileDir) {
        HttpClient client = getHttpClient();
        //Map<String, File> files = new HashMap();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < phoneNumList.size(); ++i) {
            String phoneNum = phoneNumList.get(i).toString();
            String url = "http://timeby2015-headimage.stor.sinaapp.com/" + phoneNum + ".jpg";
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse;
            try {
                httpResponse = client.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String filePath = fileDir + phoneNum + ".jpg";
                    File file = new File(filePath);
                    FileOutputStream out = new FileOutputStream(file);
                    InputStream in = httpResponse.getEntity().getContent();
                    byte b[] = new byte[1024];
                    int n = 0;
                    while ((n = in.read(b)) != -1) {
                        out.write(b, 0, n);
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("status", 1);
                    map.put("phoneNum", phoneNum);
                    map.put("file", file);
                    list.add(map);
                    out.flush();
                    out.close();
                } else {
                    String errorStr = "Error Response: " + httpResponse.getStatusLine().toString();
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("status", -1);
                    map.put("errorStr", errorStr);
                    list.add(map);
                }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                Map<String, Object> map = new HashMap<String, Object>();
                String errorStr = e.getMessage().toString();
                map.put("status", -1);
                map.put("errorStr", errorStr);
                list.add(map);
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Map<String, Object> map = new HashMap<String, Object>();
                String errorStr = e.getMessage().toString();
                map.put("status", -1);
                map.put("errorStr", errorStr);
                list.add(map);
                e.printStackTrace();
            }

        }
        return list;
    }
}