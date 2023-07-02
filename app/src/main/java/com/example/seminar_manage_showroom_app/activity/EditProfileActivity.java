package com.example.seminar_manage_showroom_app.activity;

import static com.example.seminar_manage_showroom_app.common.Constants.DoB;
import static com.example.seminar_manage_showroom_app.common.Constants.address;
import static com.example.seminar_manage_showroom_app.common.Constants.avatar;
import static com.example.seminar_manage_showroom_app.common.Constants.email;
import static com.example.seminar_manage_showroom_app.common.Constants.name;
import static com.example.seminar_manage_showroom_app.common.Constants.phone;
import static com.example.seminar_manage_showroom_app.common.Constants.uid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.api.Api_EditInfoUsers;
import com.example.seminar_manage_showroom_app.common.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_save;
    EditText txt_name, txt_DoB, txt_email, txt_phone, txt_address;
    ImageView avt;
    private static final int REQUEST_IMAGE_PICK = 2;

    private Api_EditInfoUsers client = new Api_EditInfoUsers();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
    }
    private void init(){
        btn_save = (Button) findViewById(R.id.btn_profile_save);
        btn_save.setOnClickListener(this);
        txt_name = (EditText) findViewById(R.id.edit_profile_name);
        txt_DoB = (EditText) findViewById(R.id.edit_profile_dateofbirth);
        txt_phone = (EditText) findViewById(R.id.edit_profile_phone);
        txt_address = (EditText) findViewById(R.id.edit_profile_address);
        txt_email = (EditText) findViewById(R.id.edit_profile_email);
        avt = (ImageView) findViewById(R.id.edit_img_avatar);
        avt.setOnClickListener(this);
        mapdata();
    }
    private void mapdata(){
        txt_name.setText(name);
        txt_DoB.setText(DoB);
        txt_phone.setText(phone);
        txt_address.setText(address);
        txt_email.setText(email);
        byte[] decodedString = Base64.decode(avatar, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        avt.setImageBitmap(decodedByte);
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            avt.setImageURI(selectedImage);
            avatar = convertUriToBase64(selectedImage);
        }
    }
    private String convertUriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void editprofile(){
        try {
            name = txt_name.getText().toString();
            DoB = txt_DoB.getText().toString();
            email = txt_email.getText().toString();
            phone = txt_phone.getText().toString();
            address = txt_address.getText().toString();
            client.postData(uid ,name, email, DoB, avatar, phone, address, new Api_EditInfoUsers.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    EditProfileActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(EditProfileActivity.this, "Edit Profile success", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    EditProfileActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(EditProfileActivity.this, "An error occurred during the update. Please try again later", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        catch (Exception e){
            EditProfileActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(EditProfileActivity.this, "An error occurred during the update. Please try again later", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e("Edit profile failed",e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_profile_save:
            {
                editprofile();
                finish();
                break;
            }

            case R.id.edit_img_avatar:
            {
                openImagePicker();
                break;
            }
        }
    }


}