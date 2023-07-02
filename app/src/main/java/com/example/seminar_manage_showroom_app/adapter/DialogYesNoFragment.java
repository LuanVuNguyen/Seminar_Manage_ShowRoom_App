package com.example.seminar_manage_showroom_app.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.common.interfaces.Callable;

public class DialogYesNoFragment extends android.app.Fragment implements View.OnClickListener{
    private TextView txt_dialog,txt_dialog_cont;
    private Button btn_yes,btn_no;
    private String title="default";
    private String message="default";
    private Context context;
    private Callable callable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        getActivity().getFragmentManager().beginTransaction().remove(FilterFragment.this).commit();
        View view = inflater.inflate(R.layout.dialog_1, container, false);

        //Init view such as txt, btn, edt,...
        initViews(view);
        return view;
    }
    public DialogYesNoFragment(){

    }
    @SuppressLint("ValidFragment")
    public DialogYesNoFragment(Context context, String title, String message, Callable callable){
        this.title=title;
        this.message=message;
        this.context=context;
        this.callable=callable;
    }

    private void initViews(View view) {
        txt_dialog = (TextView) view.findViewById(R.id.txt_dialog);
        txt_dialog_cont = (TextView) view.findViewById(R.id.txt_dialog_cont);
        btn_yes = (Button) view.findViewById(R.id.btn_yes);
        btn_no = (Button) view.findViewById(R.id.btn_no);
        btn_yes.setOnClickListener(this);
        btn_no.setOnClickListener(this);
        txt_dialog.setText(this.title);
        txt_dialog_cont.setText(this.message);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_yes:

                eventClickYes();
                break;
            case R.id.btn_no:

                eventClickNo();
                break;

        }
    }
    private void close(){
        getActivity().getFragmentManager().beginTransaction().remove(DialogYesNoFragment.this).commit();
    }
    private void eventClickNo() {
        callable.call(false);
        close();
    }

    private void eventClickYes() {
        callable.call(true);
        close();
    }
}
