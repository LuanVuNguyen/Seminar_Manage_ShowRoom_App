package com.example.seminar_manage_showroom_app.adapter;

import android.app.Activity;
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

/**
 * List View Adapter for Register Data Screen
 *
 * @author Tai-LQ
 * @since 2019/06/10
 */
public class ListViewScanAdapter extends BaseAdapter implements Filterable{

    private LinkedList<InforProductEntity> list;
    private LinkedList<InforProductEntity> listOld;
    private Activity activity;
    private int sizeList;

    public ListViewScanAdapter(Activity activity, LinkedList<InforProductEntity> list) {

        super();
        this.activity = activity;
        this.list = list;
        this.sizeList = list.size();
        this.listOld=list;
    }



    /**
     * Init View Holder
     */
    private class ViewHolder {

        TextView lv_title_column1;
        TextView lv_title_column2;
        TextView lv_title_column3;
        TextView lv_title_column4;
        TextView lv_title_column5;

    }

    /**
     * Get count item
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Get item at index
     */
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    /**
     * Get Item Id with position
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Set custom layout for list view
     *
     * @param position    int
     * @param convertView {@link View}
     * @param parent      {@link ViewGroup}
     */
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder viewHolder;
        LayoutInflater inflater = activity.getLayoutInflater();
        if (convertView == null) {
            // Init custom layout list scan
            convertView = inflater.inflate(R.layout.adapter_list_scan1, null);
            viewHolder = new ViewHolder();

            // Init column list view
            viewHolder.lv_title_column1 = (TextView) convertView.findViewById(R.id.txt_no);
            viewHolder.lv_title_column2 = (TextView) convertView.findViewById(R.id.txt_book_name);
            viewHolder.lv_title_column3 = (TextView) convertView.findViewById(R.id.txt_category_inv);
            viewHolder.lv_title_column4 = (TextView) convertView.findViewById(R.id.txt_id_inv);
            viewHolder.lv_title_column5 = (TextView) convertView.findViewById(R.id.txt_quanlity_inv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set data in list to list view
        InforProductEntity item = list.get(position);
        viewHolder.lv_title_column1.setText(String.valueOf(sizeList - position));
        viewHolder.lv_title_column2.setText(item.getGoodName());
        viewHolder.lv_title_column4.setText(item.getBarcodeCD1());
        viewHolder.lv_title_column5.setText(String.valueOf(item.getQuantity()));
        viewHolder.lv_title_column3.setText(String.valueOf(item.getCategory()));
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
