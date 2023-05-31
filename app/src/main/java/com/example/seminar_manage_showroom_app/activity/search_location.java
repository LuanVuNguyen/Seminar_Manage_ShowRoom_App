package com.example.seminar_manage_showroom_app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seminar_manage_showroom_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link search_location#newInstance} factory method to
 * create an instance of this fragment.
 */
public class search_location extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText txt_bookname;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btn_continue;

    private ListView lv_search;

    private static Activity mActivity;


    public search_location(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment search_location.
     */
    // TODO: Rename and change types and number of parameters
    public static search_location newInstance(String param1, String param2) {
        search_location fragment = new search_location(mActivity);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private void showToast(String s) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity,s+"",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search_location, container, false);
        btn_continue = (Button) view.findViewById(R.id.btn_continue);

        txt_bookname = (EditText) view.findViewById(R.id.txt_search_bookname);
        lv_search = (ListView) view.findViewById(R.id.lv_search_location);

        txt_bookname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    showToast(txt_bookname.getText().toString());
                    return true;
                }
                return false;
            }
        });

        View decorView = mActivity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int screenHeight = decorView.getRootView().getHeight();

                // Kiểm tra trạng thái của bàn phím
                int keyboardHeight = screenHeight - rect.bottom;
                boolean isKeyboardOpen = keyboardHeight > screenHeight * 0.15; // Điều kiện tùy chỉnh

                if (!isKeyboardOpen) {
                    showToast(txt_bookname.getText().toString());
                }
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(),ScanActivity.class);
                //startActivity(intent);
                showToast(txt_bookname.getText().toString());
            }
        });

        return view;
    }

}