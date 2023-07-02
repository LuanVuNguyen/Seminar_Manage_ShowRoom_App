package com.example.seminar_manage_showroom_app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView btn_in, btn_out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
    }

    private void init()
    {
        btn_in = (ImageView) findViewById(R.id.btn_login);
        btn_in.setOnClickListener(this);
        btn_out = (ImageView) findViewById(R.id.btn_back);
        btn_out.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_login:
                System.out.println("Register Succesfull");
                break;

        }
    }
}
