package com.example.seminar_manage_showroom_app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.common.Constants;

public class SplashTheme extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Constants.uid = sharedPreferences.getString("uid", null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Constants.uid != null)
                {
                    Intent intent = new Intent(SplashTheme.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(SplashTheme.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }
}