package com.jike.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jike.entity.QueryParam;

import org.apache.http.Consts;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.HttpEntityWrapperHC4;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtilsHC4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * 网络工具类
 *
 * @author malinkang
 */

public class HttpUtils {

    public static String getUrl(String url) {
        return Constants.DEALER_MOBILE_URL_ROOT + "/" + url;
    }
    public static String getImageUrl(String path) {
        return Constants.IMAGE_URL_ROOT + "/" + path;
    }
    private static final int TIMEOUT_IN_MILLIONS = 5000;




    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     * @throws Exception
     */
    public static String doPostLarge(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl
                    .openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

            if (param != null && !param.trim().equals("")) {
                // 获取URLConnection对象对应的输出流
                out = new PrintWriter(conn.getOutputStream());
                // 发送请求参数
                out.print(param);
                // flush输出流的缓冲
                out.flush();
            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数.
     * @return 所代表远程资源的响应结果
     * @throws Exception
     */
    public static String doPostLarge(String url, QueryParam param) {
        if(param == null){
            param = new QueryParam();
        }
        param.put("appVersion", SystemUtils.getVersionCode() + "");
        return doPostLarge(url, param.toQueryString());
    }
    public static String  doPost(String sendurl,QueryParam param){
        String responseContent = null; // 响应内容
        try {
            HttpEntityWrapperHC4 entity = post(sendurl,param);
            if(entity != null) {
                responseContent = EntityUtilsHC4.toString(entity, Charset.forName("utf-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseContent;
    }

    public static InputStream doPostForStream(String sendurl,QueryParam param){
        InputStream inputStream = null;
        try {
            HttpEntityWrapperHC4 entity = post(sendurl,param);
            if(entity != null) {
                inputStream = entity.getContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }
    private static HttpEntityWrapperHC4 post(String sendurl,QueryParam param){
        if(param == null){
            param = new QueryParam();
        }
        param.put("appVersion", SystemUtils.getVersionCode() + "");

        CloseableHttpClient client = HttpClientBuilder.create().build();
        //请求entity Builder
        EntityBuilder entityBuilder = EntityBuilder.create().setText(param.toQueryString()).setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8));
        //配置Builder
        RequestConfig.Builder builder = RequestConfig.custom().setConnectTimeout(TIMEOUT_IN_MILLIONS);
        //请求Builder
        RequestBuilder requestBuilder =  RequestBuilder.post().setUri(sendurl).setEntity(entityBuilder.build()).setConfig(builder.build());

        HttpEntityWrapperHC4 entity = null; // 响应内容
        CloseableHttpResponse response = null;
        try {
            response = client.execute(requestBuilder.build());
            if (response.getStatusLine().getStatusCode() == 200) {
                entity = new HttpEntityWrapperHC4(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}