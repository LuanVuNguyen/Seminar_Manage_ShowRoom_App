package com.example.seminar_manage_showroom_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.common.Product;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(Context context, ArrayList<Product> products) {
        super(context, 0, products);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
        }

        Product currentProduct = getItem(position);

        TextView sttTextView = listItemView.findViewById(R.id.txt_fmlc_no);
        TextView nameTextView = listItemView.findViewById(R.id.txt_fmlc_bookname);
        TextView rfidTextView = listItemView.findViewById(R.id.txt_fmlc_rfid);

        sttTextView.setText(String.valueOf(position + 1));
        nameTextView.setText(currentProduct.getName());
        rfidTextView.setText(currentProduct.getX_RFID_PRODUCT());

        return listItemView;
    }
}