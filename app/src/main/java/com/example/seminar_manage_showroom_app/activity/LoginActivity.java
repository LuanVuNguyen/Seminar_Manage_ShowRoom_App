package com.example.seminar_manage_showroom_app.activity;

import  android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.api.Api_GetAllProfile;
import com.example.seminar_manage_showroom_app.api.Api_HomeClient;
import com.example.seminar_manage_showroom_app.api.LoginClient;
import com.example.seminar_manage_showroom_app.common.Config;
import com.example.seminar_manage_showroom_app.common.Constants;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView btn_Login;
    private ImageView btn_Signup, btn_Guest;
    EditText txt_login, txt_pwd;
    Api_GetAllProfile info_product = new Api_GetAllProfile();
    LoginClient loginClient = new LoginClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Init();
        GetInfoProduct();
    }

    private void Init() {
        btn_Signup = findViewById(R.id.btn_creat);
        btn_Signup.setOnClickListener(this);
        btn_Login = findViewById(R.id.btn_login);
        btn_Login.setOnClickListener(this);
        btn_Guest = findViewById(R.id.btn_login_guest);
        btn_Guest.setOnClickListener(this);
        txt_login = findViewById(R.id.txt_Login_email);
        txt_pwd = findViewById(R.id.txt_Login_email);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_creat:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.btn_login:
                //startActivity(new Intent(this, HomeActivity.class));
                getDataFromApi();
                break;
            case R.id.btn_login_guest:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }
    private void getDataFromApi() {
        String db = Config.DATABASE_NAME;
        String login = txt_login.getText().toString();
        String password = txt_pwd.getText().toString();
        if (login.isEmpty() || password.isEmpty()) {
            showToast("Please fill out the information completely ");
        } else {
            loginClient.postData(db, login, password, new LoginClient.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    if (response.contains("error") == true)
                    {
                        showToast("incorrect username or password");
                    }
                    else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString(Constants.KEY_CODE).equals(Constants.VALUE_CODE_OK)) {
                                JSONObject resultObj = jsonObject.getJSONObject("result");
                                if (resultObj.toString().isEmpty()) {
                                    showToast("incorrect username or password");
                                } else {
                                    String uid = resultObj.getString("uid");
                                    if (uid.isEmpty())
                                    {
                                        showToast("User does not exist");
                                    }
                                    else{
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        intent.putExtra("uid", uid);
                                        startActivity(intent);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(String errorMessage) {

                    Log.e("Error", errorMessage);
                }
            });
        }
    }
    private void GetInfoProduct(){
        System.out.println("resultObj");
        info_product.GetData(new Api_GetAllProfile.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString(Constants.KEY_CODE).equals(Constants.VALUE_CODE_OK)) {
                        JSONObject resultObj = jsonObject.getJSONObject("products");
                        System.out.println(resultObj);
                    }
                }
                catch (JSONException e){

                }

            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void showToast(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,s+"",Toast.LENGTH_LONG).show();
            }
        });
    }


}

