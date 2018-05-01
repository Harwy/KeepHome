package com.shu.keephome.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 14623 on 2018/5/1.
 * sendHttpRequest url请求，返回json
 */

public class HttpUtil {

    public static void sendHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
