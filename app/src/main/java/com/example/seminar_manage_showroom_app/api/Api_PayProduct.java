package com.example.seminar_manage_showroom_app.api;

import com.example.seminar_manage_showroom_app.common.Config;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api_PayProduct {
    private static final String BASE_URL = Config.HTTP_SERVER_SHOP;

    private OkHttpClient client;

    public Api_PayProduct() {
        client = new OkHttpClient();
    }

    public void postData(String rfid, final Api_PayProduct.ApiCallback callback) {
        String json = "{\"rfid\":\"" + rfid + "\"}";
        System.out.println(json);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(BASE_URL + Config.API_PAY_PRODUCT)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    callback.onSuccess(res);
                } else {
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public interface ApiCallback {
        void onSuccess(String response);

        void onError(String errorMessage);
    }
}
