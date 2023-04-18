package com.example.seminar_manage_showroom_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.seminar_manage_showroom_app.R;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btn_search,btn_pay,btn_inventory,btn_creproduct,btn_import,btn_export,btn_setting,btn_profile,btn_logout,avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        Glide.with(this)
                .load("https://i.pinimg.com/736x/68/ea/e5/68eae5110003466af047764ff88e2403.jpg")
                .into(avatar);
    }

    private void init()
    {
        btn_search = (ImageView) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);

        btn_pay = (ImageView) findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(this);

        btn_inventory = (ImageView) findViewById(R.id.btn_inventory);
        btn_inventory.setOnClickListener(this);

        btn_creproduct = (ImageView) findViewById(R.id.btn_createproduct);
        btn_creproduct.setOnClickListener(this);

        btn_import = (ImageView) findViewById(R.id.btn_import);
        btn_import.setOnClickListener(this);

        btn_export = (ImageView) findViewById(R.id.btn_export);
        btn_export.setOnClickListener(this);

        btn_setting = (ImageView) findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(this);

        btn_profile = (ImageView) findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(this);

        btn_logout = (ImageView) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);

        avatar = (ImageView) findViewById(R.id.img_avatar);
        avatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_search:
            {

            }
            case R.id.btn_pay:
            {

            }
            case R.id.btn_inventory:
            {
                startActivity(new Intent(this, InventoryActivity.class));
                break;
            }
            case R.id.btn_createproduct:
            {
                startActivity(new Intent(this, CreateProductActivity.class));
                break;
            }
            case R.id.btn_import:
            {

            }
            case R.id.btn_export:
            {

            }
            case R.id.btn_setting:
            {
                startActivity(new Intent(this, SettingActivity.class));
                break;
            }
            case R.id.btn_profile:
            {

            }
            case R.id.btn_logout:
            {

            }
        }
    }
}