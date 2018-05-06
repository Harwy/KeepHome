package com.shu.keephome.util;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by 14623 on 2018/5/1.
 * getHttp url请求，返回json
 */

public class HttpUtil {

    /**
     * getHttp
     * @param address：url地址
     * @param callback:回处理
     */
    public static void getHttp(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * postHttp
     * 在formbody下组装json   POST永远是1条一条POST
     * @param address:url地址
     * @param callback:回处理
     */
    public static void postHttp(String address, String json, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        RequestBody formBody = FormBody.create(
                MediaType.parse("application/json; charset=utf-8")
                , json);
        Request request = new Request.Builder()
                .url(address)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
