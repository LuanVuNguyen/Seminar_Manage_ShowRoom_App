package com.example.seminar_manage_showroom_app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.api.Api_GetInfoUsers;
import com.example.seminar_manage_showroom_app.api.Api_LogOut;
import com.example.seminar_manage_showroom_app.common.Config;
import com.example.seminar_manage_showroom_app.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btn_search,btn_pay,btn_inventory,btn_creproduct,btn_setting,btn_profile,btn_logout,avatar;
    private TextView txt_device, txt_posite, txt_name;
    Api_GetInfoUsers HomeClient = new Api_GetInfoUsers();
    Api_LogOut ClientLogout = new Api_LogOut();
    private  int DOUBLE_BACK_PRESS_COUNT = 2;
    private int backPressedCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }
    private void ApiGetUser(String uid){
        HomeClient.postData(uid, new Api_GetInfoUsers.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString(Constants.KEY_CODE).equals(Constants.VALUE_CODE_OK)) {
                        JSONObject resultObj = jsonObject.getJSONObject("result");
                        if (!resultObj.toString().isEmpty())
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String name = resultObj.getString("name");
                                        String position = resultObj.getString("job_title");
                                        String base64Avt = resultObj.getString("avt");

                                        // Update TextViews
                                        txt_name.setText(name);
                                        txt_posite.setText(position);

                                        // Decode and set image
                                        byte[] decodedString = Base64.decode(base64Avt, Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        avatar.setImageBitmap(decodedByte);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        else{
                            System.out.println("error");
                        }
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(String errorMessage) {

            }
        });
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

        btn_setting = (ImageView) findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(this);

        btn_profile = (ImageView) findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(this);

        btn_logout = (ImageView) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);

        avatar = (ImageView) findViewById(R.id.img_avatar);

        txt_device = (TextView) findViewById(R.id.device_conect);
        txt_device.setText(Constants.CONFIG_DEVICE_NAME);

        txt_name = (TextView) findViewById(R.id.txt_name1);
        txt_posite = (TextView) findViewById(R.id.txt_posite);
        ApiGetUser(Constants.uid);
    }

    private void Logout(){
            ClientLogout.Logout(Constants.uid, new Api_LogOut.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    System.out.println(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString(Constants.KEY_CODE).equals(Constants.VALUE_CODE_OK)) {
                            if (jsonObject.getJSONObject("error") == null){
                                Constants.uid = null;
                                onBackPressed();
                                finish();
                            }
                        }
                    }
                    catch (Exception e){

                    }
                }
                @Override
                public void onError(String errorMessage) {

                }
            });


    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_search:
            {
                startActivity(new Intent(this, SearchActivity.class));
                break;
            }
            case R.id.btn_pay:
            {
                startActivity(new Intent(this, PayActivity.class));
                break;
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
            case R.id.btn_setting:
            {
                startActivity(new Intent(this, MenuDeviceActivity.class));
                break;
            }
            case R.id.btn_profile:
            {
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            }
            case R.id.btn_logout: {
                Constants.uid = null;
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        ApiGetUser(Constants.uid);
    }

    @Override
    public void onBackPressed() {
        if (backPressedCount < DOUBLE_BACK_PRESS_COUNT - 1) {
            backPressedCount++;
            Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();
        } else {
            finishAffinity();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", Constants.uid);
        editor.apply();
    }


}