package com.example.seminar_manage_showroom_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.common.Config;
import com.example.seminar_manage_showroom_app.common.Constants;

public class ActivitySetupServer extends AppCompatActivity implements View.OnClickListener {

    ImageView btn_save;

    EditText txt_server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_server);
        init();
    }

    private void init(){
        btn_save = (ImageView) findViewById(R.id.btn_save_server);
        btn_save.setOnClickListener(this);
        txt_server = (EditText) findViewById(R.id.edit_txt_server);
        txt_server.setText(Config.HTTP_SERVER_SHOP);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_save_server:
            {
                try {
                    Config.HTTP_SERVER_SHOP = null;
                    if (txt_server.getText().toString().isEmpty()){
                        Toast.makeText(this,"Please in put server",Toast.LENGTH_LONG).show();
                    } else if (!txt_server.getText().toString().contains("8069")) {
                        Toast.makeText(this,"Port failed",Toast.LENGTH_LONG).show();
                    }else{
                        Config.HTTP_SERVER_SHOP = txt_server.getText().toString();
                        Thread.sleep(100);
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    }
                    break;
                }catch (Exception e){
                    Log.e("Server",e.getMessage());
                }

            }
        }
    }
}
