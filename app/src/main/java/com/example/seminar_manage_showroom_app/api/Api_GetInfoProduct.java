package com.example.seminar_manage_showroom_app.api;

import com.example.seminar_manage_showroom_app.common.Config;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class Api_GetInfoProduct {
    private String BASE_URL = Config.HTTP_SERVER_SHOP;

    private OkHttpClient client;

    public Api_GetInfoProduct() {
        client = new OkHttpClient();
    }

    public void getProductInfo(final Api_GetInfoProduct.ApiCallback callback) {
        String json = "{ }";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(BASE_URL + Config.API_GET_ALL_PRODUCT)
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