package com.example.seminar_manage_showroom_app.api;

import com.example.seminar_manage_showroom_app.common.Config;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api_GetInfoUsers {
    private static final String BASE_URL = Config.HTTP_SERVER_SHOP;

    private OkHttpClient client;

    public Api_GetInfoUsers() {
        client = new OkHttpClient();
    }

    public void postData(String uid,  final Api_GetInfoUsers.ApiCallback callback) {
        String json = "{\"id\":\"" + uid + "\"}";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(BASE_URL + Config.API_INFO_USER)
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
