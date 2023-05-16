package com.example.seminar_manage_showroom_app.activity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.seminar_manage_showroom_app.R;

import org.w3c.dom.Text;

public class PayActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txt_qty, txt_total, txt_cash, txt_change_cash;

    CheckBox check_pay_cash, check_pay_tranfers;

    ListView lv_pay;

    ImageView btn_done_pay, btn_start, btn_clear;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        init();
    }

    private void init()
    {
        txt_qty = findViewById(R.id.txt_pay_qty);
        txt_total = findViewById(R.id.txt_pay_total);
        txt_cash = findViewById(R.id.txt_pay_cash);
        txt_change_cash = findViewById(R.id.txt_pay_changecase);

        btn_done_pay = (ImageView) findViewById(R.id.btn_pay_done);
        btn_done_pay.setOnClickListener(this);
        btn_start = (ImageView) findViewById(R.id.btn_pay_play);
        btn_start.setOnClickListener(this);
        btn_clear = (ImageView) findViewById(R.id.btn_pay_clear);
        btn_clear.setOnClickListener(this);

        lv_pay = (ListView) findViewById(R.id.lv_pay);

        check_pay_cash = (CheckBox) findViewById(R.id.check_pay_cash);
        check_pay_tranfers = (CheckBox) findViewById(R.id.check_pay_tranfers);

    }





    @Override
    public void onClick(View v) {

    }
}
