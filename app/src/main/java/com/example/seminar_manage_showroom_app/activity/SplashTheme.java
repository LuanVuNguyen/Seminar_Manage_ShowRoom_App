package com.example.seminar_manage_showroom_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashTheme extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashTheme.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000); // Đợi 5 giây trước khi chuyển sang màn hình chính
    }
}