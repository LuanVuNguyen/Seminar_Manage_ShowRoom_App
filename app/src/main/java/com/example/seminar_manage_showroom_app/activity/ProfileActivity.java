package com.example.seminar_manage_showroom_app.activity;

import static com.example.seminar_manage_showroom_app.common.Constants.name;
import static com.example.seminar_manage_showroom_app.common.Constants.email;
import static com.example.seminar_manage_showroom_app.common.Constants.address;
import static com.example.seminar_manage_showroom_app.common.Constants.phone;
import static com.example.seminar_manage_showroom_app.common.Constants.DoB;
import static com.example.seminar_manage_showroom_app.common.Constants.avatar;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.api.Api_GetInfoUsers;
import com.example.seminar_manage_showroom_app.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_edit;
    TextView txt_name, txt_DoB, txt_email, txt_phone, txt_address;
    ImageView avt;
    Api_GetInfoUsers GetUsers = new Api_GetInfoUsers();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();

    }
    private void init(){
        btn_edit = (Button) findViewById(R.id.btn_profile_edit);
        btn_edit.setOnClickListener(this);
        txt_name = (TextView) findViewById(R.id.txt_profile_name);
        txt_DoB = (TextView) findViewById(R.id.txt_profile_dateofbirth);
        txt_phone = (TextView) findViewById(R.id.txt_profile_phone);
        txt_address = (TextView) findViewById(R.id.txt_profile_address);
        txt_email = (TextView) findViewById(R.id.txt_profile_email);
        avt = (ImageView) findViewById(R.id.img_avatar_profile);
        ApiGetUser(Constants.uid);
    }

    private void ApiGetUser(String uid){
        GetUsers.postData(uid, new Api_GetInfoUsers.ApiCallback() {
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
                                        name = resultObj.getString("name");
                                        email = resultObj.getString("email");
                                        avatar = resultObj.getString("avt");
                                        phone = resultObj.getString("phone");
                                        address = resultObj.getString("address");
                                        DoB = resultObj.getString("date_of_birth");
                                        // Update TextViews
                                        txt_name.setText(name);
                                        txt_email.setText(email);
                                        txt_DoB.setText(DoB);
                                        txt_phone.setText(phone);
                                        txt_address.setText(address);

                                        // Decode and set image
                                        byte[] decodedString = Base64.decode(avatar, Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        avt.setImageBitmap(decodedByte);
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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Thread.sleep(1000);
            ApiGetUser(Constants.uid);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_profile_edit:
            {
                startActivity(new Intent(this, EditProfileActivity.class));
                break;
            }
        }
    }
}
