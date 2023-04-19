package com.example.seminar_manage_showroom_app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.seminar_manage_showroom_app.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView btn_Login;
    private ImageView btn_Signup, btn_Guest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Init();
    }

    private void Init()
    {
        btn_Signup = (ImageView) findViewById(R.id.btn_creat);
        btn_Signup.setOnClickListener(this);
        btn_Login = (ImageView) findViewById(R.id.btn_signup);
        btn_Login.setOnClickListener(this);
        btn_Guest = (ImageView) findViewById(R.id.btn_login_guest);
        btn_Guest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_creat:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.btn_signup:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.btn_login_guest:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }

}