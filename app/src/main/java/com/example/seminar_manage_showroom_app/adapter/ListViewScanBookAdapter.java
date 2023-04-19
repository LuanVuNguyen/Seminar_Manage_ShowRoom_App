//package com.example.seminar_manage_showroom_app.adapter;
//
//import android.app.Activity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Filter;
//import android.widget.Filterable;
//import android.widget.TableRow;
//import android.widget.TextView;
//
//import com.example.libraryapp.R;
//import com.example.libraryapp.common.entities.InforBookEntity;
//
//import java.util.LinkedList;
//
///**
// * List View Adapter for Register Data Screen
// *
// * @author Tai-LQ
// * @since 2019/06/10
// */
//public class ListViewScanBookAdapter extends BaseAdapter implements Filterable{
//
//    private LinkedList<InforBookEntity> list;
//    private LinkedList<InforBookEntity> listOld;
//    private Activity activity;
//    private int sizeList;
//
//    public ListViewScanBookAdapter(Activity activity, LinkedList<InforBookEntity> list) {
//
//        super();
//        this.activity = activity;
//        this.list = list;
//        this.sizeList = list.size();
//        this.listOld=list;
//    }
//
//
//
//    /**
//     * Init View Holder
//     */
//    private class ViewHolder {
//
//        TextView lv_title_column1;
//        TextView lv_title_column2;
//        TextView lv_title_column3;
//        TextView lv_title_column4;
//        TextView lv_title_column5;
//
//
//        TableRow cd1;
//        TableRow cd2;
//        TableRow base_price;
//        TableRow tax_include_price;
//
//    }
//
//    /**
//     * Get count item
//     */
//    @Override
//    public int getCount() {
//        return list.size();
//    }
//
//    /**
//     * Get item at index
//     */
//    @Override
//    public Object getItem(int position) {
//        return list.get(position);
//    }
//
//    /**
//     * Get Item Id with position
//     */
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    /**
//     * Set custom layout for list view
//     *
//     * @param position    int
//     * @param convertView {@link View}
//     * @param parent      {@link ViewGroup}
//     */
//    @Override
//    public View getView(final int position, View convertView, final ViewGroup parent) {
//
//        final ViewHolder viewHolder;
//        LayoutInflater inflater = activity.getLayoutInflater();
//        if (convertView == null) {
//            // Init custom layout list scan
//            convertView = inflater.inflate(R.layout.adapter_list_scan, null);
//            viewHolder = new ViewHolder();
//
//            // Init column list view
//            viewHolder.lv_title_column1 = (TextView) convertView.findViewById(R.id.list_column1);
//            viewHolder.lv_title_column2 = (TextView) convertView.findViewById(R.id.list_column2);
//            viewHolder.lv_title_column3 = (TextView) convertView.findViewById(R.id.list_column3);
//            viewHolder.lv_title_column4 = (TextView) convertView.findViewById(R.id.list_column4);
//            viewHolder.lv_title_column5 = (TextView) convertView.findViewById(R.id.list_column5);
//
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//
//
//        // Set data in list to list view
//        InforBookEntity item = list.get(position);
//
//        viewHolder.lv_title_column1.setText(String.valueOf(sizeList - position));
//        viewHolder.lv_title_column2.setText(item.getBooktitle());
//        viewHolder.lv_title_column3.setText(item.getRfidCode());
//        viewHolder.lv_title_column4.setText(item.getAuthor());
//        viewHolder.lv_title_column5.setText(item.getCategories());
//
//
//        // Set background color and text color
///*        if (position == 0) {
//            // Set for first line in list view
//            convertView.setBackgroundColor(Color.parseColor(Constants.BACKGROUND_COLOR_BLUE_GRAY_LIGHT));
//            viewHolder.lv_title_column1.setTextColor(Color.BLACK);
//            viewHolder.lv_title_column2.setTextColor(Color.BLACK);
//            viewHolder.lv_title_column3.setTextColor(Color.BLACK);
//            viewHolder.lv_title_column4.setTextColor(Color.BLACK);
//        } else {
//            // Set background from the second line in list view
//            if (position % 2 == 0) {
//                // Set even line
//                convertView.setBackgroundColor(Color.parseColor(Constants.BACKGROUND_COLOR_BLUE_GRAY_LIGHT));
//                viewHolder.lv_title_column1.setTextColor(Color.BLACK);
//                viewHolder.lv_title_column2.setTextColor(Color.BLACK);
//                viewHolder.lv_title_column3.setTextColor(Color.BLACK);
//                viewHolder.lv_title_column4.setTextColor(Color.BLACK);
//            } else {
//                // Set odd line
//                convertView.setBackgroundColor(Color.parseColor(Constants.BACKGROUND_COLOR_BLUE_GRAY_LIGHT));
//                viewHolder.lv_title_column1.setTextColor(Color.BLACK);
//                viewHolder.lv_title_column2.setTextColor(Color.BLACK);
//                viewHolder.lv_title_column3.setTextColor(Color.BLACK);
//                viewHolder.lv_title_column4.setTextColor(Color.BLACK);
//            }
//
//            // #HUYNHQUANGVINH change text color column quantity when is exist rfid code
//            *//*if (item.getRfidCode() != null && !item.getRfidCode().equals("")) {
//                viewHolder.lv_title_column1.setTypeface(null, Typeface.BOLD);
//                viewHolder.lv_title_column2.setTypeface(null, Typeface.BOLD);
//                viewHolder.lv_title_column3.setTypeface(null, Typeface.BOLD);
//                viewHolder.lv_title_column4.setTypeface(null, Typeface.BOLD);
//            }*//*
//        }*/
//        return convertView;
//
//    }
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                String strSearch=constraint.toString();
//                if (strSearch.isEmpty()){
//                    list=listOld;
//                }else{
//                    LinkedList<InforBookEntity> listsearch= new LinkedList<>();
//                    for(InforBookEntity product : listOld){
//                        if(product.getBooktitle().toLowerCase().contains(strSearch.toLowerCase())){
//                            listsearch.add(product);
//                        }
//                    }
//                    listOld = listsearch;
//                }
//                FilterResults filterResults=new FilterResults();
//                filterResults.values=list;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraints, FilterResults results) {
//                list=(LinkedList<InforBookEntity>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return position % 2;
//    }
//
//}
