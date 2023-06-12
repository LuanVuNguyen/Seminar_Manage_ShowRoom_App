package com.example.seminar_manage_showroom_app.adapter;

import android.app.AlertDialog;
import android.content.Context;

public class notify {

    private Context context;

    public notify(Context context) {
        this.context = context;
    }

    public void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}