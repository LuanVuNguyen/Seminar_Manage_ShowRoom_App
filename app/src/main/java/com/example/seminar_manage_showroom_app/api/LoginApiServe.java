package com.example.seminar_manage_showroom_app.api;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApiServe {
    @POST("your-endpoint-url") // Replace with your API endpoint URL
    Call<LoginResponseModel> postData(@Body LoginRequestBodyModel requestBody);
}
