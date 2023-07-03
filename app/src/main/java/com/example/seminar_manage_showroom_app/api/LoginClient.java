package com.example.seminar_manage_showroom_app.api;

import com.example.seminar_manage_showroom_app.common.Config;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;


public class LoginClient {
    private OkHttpClient client;

    public LoginClient() {
        client = new OkHttpClient();
    }

    public void postData(String db, String login, String password, final ApiCallback callback) {
        String json = "{\"jsonrpc\":\"2.0\",\"params\":{\"db\":\"" + db + "\",\"login\":\"" + login + "\",\"password\":\"" + password + "\"}}";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(Config.HTTP_SERVER_SHOP + Config.API_LOGIN)
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
