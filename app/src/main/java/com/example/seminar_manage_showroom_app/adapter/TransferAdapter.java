//package com.example.seminar_manage_showroom_app.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.example.libraryapp.R;
//
//public class TransferAdapter extends BaseAdapter {
//    Context context;
//    String[] transfertext;
//    LayoutInflater inflter;
//
//    public TransferAdapter(Context applicationContext, String[] transfertext) {
//        this.context = applicationContext;
//        this.transfertext = transfertext;
//        inflter = (LayoutInflater.from(applicationContext));
//    }
//
//    @Override
//    public int getCount() {
//        return transfertext.length;
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return transfertext[i];
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        view = inflter.inflate(R.layout.custom_transfer_spinner_item, null);
//        TextView names = (TextView) view.findViewById(R.id.txt_item_transfer);
//        names.setText(transfertext[i]);
//        return view;
//    }
//}
