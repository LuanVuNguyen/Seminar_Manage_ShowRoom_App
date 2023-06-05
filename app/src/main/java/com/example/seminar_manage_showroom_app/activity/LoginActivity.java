package com.example.seminar_manage_showroom_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.api.LoginApiServe;
import com.example.seminar_manage_showroom_app.api.LoginRequestBodyModel;
import com.example.seminar_manage_showroom_app.api.LoginResponseModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView btn_Login;
    private ImageView btn_Signup, btn_Guest;

    private LoginApiServe yourApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Init();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your-api-url.com/") // Replace with your API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        yourApiService = retrofit.create(LoginApiServe.class);
    }

    private void Init() {
        btn_Signup = findViewById(R.id.btn_creat);
        btn_Signup.setOnClickListener(this);
        btn_Login = findViewById(R.id.btn_signup);
        btn_Login.setOnClickListener(this);
        btn_Guest = findViewById(R.id.btn_login_guest);
        btn_Guest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_creat:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.btn_signup:
                postData();
                break;
            case R.id.btn_login_guest:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }

    private void postData() {
        String jsonrpc = "2.0";
        String db = "odoo";
        String login = "admin";
        String password = "admin";

        LoginRequestBodyModel requestBody = new LoginRequestBodyModel(jsonrpc,db, login, password);

        Call<LoginResponseModel> call = yourApiService.postData(requestBody);
        call.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if (response.isSuccessful()) {
                    LoginResponseModel responseData = response.body();
                    if (responseData != null) {
                        // Handle the response data
                    }
                } else {
                    // Handle the error response
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                // Handle the network failure
            }
        });
    }
}

