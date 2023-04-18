package com.example.seminar_manage_showroom_app.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;

public class InventoryActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView btn_menu,btn_start,btn_stop,btn_clear,btn_exportfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        init();
    }
    private void init()
    {
        btn_menu = (ImageView) findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(this);

        btn_start = (ImageView) findViewById(R.id.btn_startscan);
        btn_start.setOnClickListener(this);

        btn_stop = (ImageView) findViewById(R.id.btn_stopscan);
        btn_stop.setOnClickListener(this);

        btn_clear = (ImageView) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);

        btn_exportfile = (ImageView) findViewById(R.id.btn_exportcsv);
        btn_exportfile.setOnClickListener(this);
    }

    private void unvisible()
    {
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_clear.getVisibility()==View.INVISIBLE){
                    btn_menu.setImageResource(R.drawable.btn_close);
                    btn_clear.setVisibility(View.VISIBLE);
                    btn_start.setVisibility(View.VISIBLE);
                    btn_stop.setVisibility(View.VISIBLE);
                    btn_exportfile.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_menu.setImageResource(R.drawable.btn_menu);
                    btn_clear.setVisibility(View.INVISIBLE);
                    btn_start.setVisibility(View.INVISIBLE);
                    btn_stop.setVisibility(View.INVISIBLE);
                    btn_exportfile.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_menu:
            {
                unvisible();
            }
        }
    }
}
