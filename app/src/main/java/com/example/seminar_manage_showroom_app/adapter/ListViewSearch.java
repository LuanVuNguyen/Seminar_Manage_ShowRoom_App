package com.example.seminar_manage_showroom_app.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.common.entities.InforProductEntity;

import java.util.LinkedList;
import java.util.List;

public class ListViewSearch extends BaseAdapter implements Filterable {

    private LinkedList<InforProductEntity> list;
    private LinkedList<InforProductEntity> listOld;
    private Activity activity;


    private int selectedPosition = -1;

    public ListViewSearch(Activity activity, LinkedList<InforProductEntity> list) {

        super();
        this.activity = activity;
        this.list = list;

        this.listOld=list;
    }

    private class ViewHolder {

        TextView lv_title_column1;
        TextView lv_title_column2;
        TextView lv_title_column3;

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ListViewSearch.ViewHolder viewHolder;
        LayoutInflater inflater = activity.getLayoutInflater();
        if (convertView == null) {
            // Init custom layout list scan
            convertView = inflater.inflate(R.layout.adapter_list_search, null);
            viewHolder = new ListViewSearch.ViewHolder();

            // Init column list view
            viewHolder.lv_title_column1 = (TextView) convertView.findViewById(R.id.txt_fmlc_no);
            viewHolder.lv_title_column2 = (TextView) convertView.findViewById(R.id.txt_fmlc_bookname);
            viewHolder.lv_title_column3 = (TextView) convertView.findViewById(R.id.txt_fmlc_rfid);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ListViewSearch.ViewHolder) convertView.getTag();
        }

        // Set data in list to list view
        InforProductEntity item = list.get(position);
        viewHolder.lv_title_column1.setText(String.valueOf(position+1));
        viewHolder.lv_title_column3.setText(item.getBarcodeCD1());
        viewHolder.lv_title_column2.setText(String.valueOf(item.getCategory()));

        // Thay đổi màu sắc của hàng được chọn
        if (position == selectedPosition) {
            convertView.setBackgroundColor(Color.YELLOW); // Thay đổi màu nền của hàng khi được chọn
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT); // Đặt màu nền của hàng về mặc định
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = position; // Cập nhật vị trí hàng được chọn
                notifyDataSetChanged(); // Cập nhật giao diện người dùng

                // Xử lý logic khác sau khi nhấp vào hàng
                // ...
            }
        });
        return convertView;
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch=constraint.toString();
                if (strSearch.isEmpty()){
                    list=listOld;
                }else{
                    LinkedList<InforProductEntity> listsearch= new LinkedList<>();
                    for(InforProductEntity product : listOld){
                        if(product.getGoodName().toLowerCase().contains(strSearch.toLowerCase())){
                            listsearch.add(product);
                        }
                    }
                    listOld = listsearch;
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraints, FilterResults results) {
                list=(LinkedList<InforProductEntity>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

}
