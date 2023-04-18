package com.example.seminar_manage_showroom_app.activity;

import android.content.Intent;
import android.icu.text.AlphabeticIndex;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView btn_menuconnect,btn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);
        init();
    }
    private void init()
    {
//        btn_save = (ImageView)  findViewById(R.id.getsettings);
//        btn_save.setOnClickListener(this);
//
//        btn_startwork = (ImageView) findViewById(R.id.sendsettings);
//        btn_startwork.setOnClickListener(this);
//
//        btn_open = (ImageView) findViewById(R.id.save);
//        btn_open.setOnClickListener(this);
//
//        btn_read = (ImageView) findViewById(R.id.open);
//        btn_read.setOnClickListener(this);
//
//        btn_restore = (ImageView) findViewById(R.id.reset);
//        btn_restore.setOnClickListener(this);

        btn_menuconnect = (ImageView) findViewById(R.id.menu_deviceconnection);
        btn_menuconnect.setOnClickListener(this);
        btn_setting = (ImageView) findViewById(R.id.menu_setting);
        btn_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.menu_deviceconnection:
                startActivity(new Intent(this, MenuConnectionActivity.class));
                break;
            case R.id.menu_setting:
                startActivity(new Intent(this, MenuSettingActivity.class));
                break;
        }

    }
}
