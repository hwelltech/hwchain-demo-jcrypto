package com.hw.chain.demo.utils;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.hw.chain.demo.cons.Const;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();
        return response.body() == null ? null : response.body().string().trim();
    }

    public static String doPost(JSONObject requestParam) {
        try {
            return OkHttpUtils.post(Const.RPC_URL, requestParam.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
