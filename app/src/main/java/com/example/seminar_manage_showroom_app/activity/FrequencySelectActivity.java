package com.example.seminar_manage_showroom_app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.seminar_manage_showroom_app.R;

import java.util.ArrayList;

import jp.co.toshibatec.TecRfidSuite;

public class FrequencySelectActivity extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {
    private final String COMMA = ",";
    /** 周波数チャネル1st */
    private TextView mTextFrequency1st = null;
    /** 周波数チャネル2nd */
    private TextView mTextFrequency2nd = null;
    /** 周波数チャネル3rd */
    private TextView mTextFrequency3rd = null;
    /** 周波数チャネル4th */
    private TextView mTextFrequency4th = null;
    /** 周波数チャネル5th */
    private TextView mTextFrequency5th = null;
    /** 周波数チャネル6th */
    private TextView mTextFrequency6th = null;
    /** 周波数チャネル7th */
    private TextView mTextFrequency7th = null;
    /** 周波数チャネル8th */
    private TextView mTextFrequency8th = null;
    /** 周波数チャネル9th */
    private TextView mTextFrequency9th = null;
    /** 周波数チャネル10th */
    private TextView mTextFrequency10th = null;
    /** 周波数チャネル11th */
    private TextView mTextFrequency11th = null;
    /** 周波数チャネル12th */
    private TextView mTextFrequency12th = null;
    /** 対応表表示切替用チェックボックス */
    private CheckBox mCheckBoxFrequencyDetail = null;
    /** 保存ボタン */
    private ImageView mFrequencySaveBtn = null;
    /** 　frequencyリスト用ArrayList　 */
    private ArrayList<String> mFrequencyList = new ArrayList<String>();
    /** JP1 or JP3 or JP4  or JP6 */
    private String mPowerType = "";
    /** 選択中のView */
    private View mSelectView = null;
    /** 選択されているダイアログアイテム */
    private int mSelectItem = 0;
    /** frequencyタイトル */
    private String TITLE_FREQUENCY = "周波数";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_SKIP = "skip";
    /** frequency_detail */
    PopupWindow mFrequencyDetailPopup;
    /** 自動周波数設定保存用KEY */
    public static final String AUTO_FREQUENCY_CHANNEL_KEY = "AutoFrequencyChannel";
    /** "skip"時のチャネル番号 */
    public static final int FREQUENCY_CHANNEL_SKIP = -1;
    /** "複数チャネル設定保存用のプリファレンス */
    public static final String SETTING_PREFS = "settingprefs";
    /** デバイスタイプ */
    public String mDeviceType = null;
    /** JP1(特小) */
    public static final String JP1 = "JP1";
    public static final String JP2 = "JP2";
    public static final String JP3 = "JP3";
    public static final String JP4 = "JP4";
    public static final String JP5 = "JP5";
    public static final String JP6 = "JP6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.frequency_select);

        mPowerType = getIntent().getStringExtra(MenuDeviceActivity.KEY_POWERTYPE);
        mDeviceType = getIntent().getStringExtra(MenuDeviceActivity.KEY_DEVICENAME);

        //周波数リスト準備
        prepareList();

        //UI定義
        initView();

        //周波数設定反映
        reflectSettings();
    }

    /**
     * リストを使用する準備
     */
    private void prepareList() {
        mFrequencyList.add(FREQUENCY_SKIP);
        mFrequencyList.add(SettingActivity.FREQUENCY_5CH);
        mFrequencyList.add(SettingActivity.FREQUENCY_11CH);
        mFrequencyList.add(SettingActivity.FREQUENCY_17CH);
        mFrequencyList.add(SettingActivity.FREQUENCY_23CH);
        mFrequencyList.add(SettingActivity.FREQUENCY_24CH);
        mFrequencyList.add(SettingActivity.FREQUENCY_25CH);
        if (mPowerType.equals(SettingActivity.JP1) || mPowerType.equals(SettingActivity.JP4)) {
            mFrequencyList.add(SettingActivity.FREQUENCY_26CH);
            mFrequencyList.add(SettingActivity.FREQUENCY_27CH);
            mFrequencyList.add(SettingActivity.FREQUENCY_28CH);
            mFrequencyList.add(SettingActivity.FREQUENCY_29CH);
            mFrequencyList.add(SettingActivity.FREQUENCY_30CH);
            mFrequencyList.add(SettingActivity.FREQUENCY_31CH);
            mFrequencyList.add(SettingActivity.FREQUENCY_32CH);
        }
    }

    /**
     * UI定義
     *
     */
    private void initView() {
        Object omTextFrequency1st = findViewById(R.id.frequency1st);
        if (omTextFrequency1st instanceof TextView) {
            mTextFrequency1st = (TextView) omTextFrequency1st;
        } else {
            mTextFrequency1st = new TextView(this);
        }
        mTextFrequency1st.setOnClickListener(this);

        Object omTextFrequency2nd = findViewById(R.id.frequency2nd);
        if (omTextFrequency2nd instanceof TextView) {
            mTextFrequency2nd = (TextView) omTextFrequency2nd;
        } else {
            mTextFrequency2nd = new TextView(this);
        }
        mTextFrequency2nd.setOnClickListener(this);

        Object omTextFrequency3rd = findViewById(R.id.frequency3rd);
        if (omTextFrequency3rd instanceof TextView) {
            mTextFrequency3rd = (TextView) omTextFrequency3rd;
        } else {
            mTextFrequency3rd = new TextView(this);
        }
        mTextFrequency3rd.setOnClickListener(this);

        Object omTextFrequency4th = findViewById(R.id.frequency4th);
        if (omTextFrequency4th instanceof TextView) {
            mTextFrequency4th = (TextView) omTextFrequency4th;
        } else {
            mTextFrequency4th = new TextView(this);
        }
        mTextFrequency4th.setOnClickListener(this);

        Object omTextFrequency5th = findViewById(R.id.frequency5th);
        if (omTextFrequency5th instanceof TextView) {
            mTextFrequency5th = (TextView) omTextFrequency5th;
        } else {
            mTextFrequency5th = new TextView(this);
        }
        mTextFrequency5th.setOnClickListener(this);

        Object omTextFrequency6th = findViewById(R.id.frequency6th);
        if (omTextFrequency6th instanceof TextView) {
            mTextFrequency6th = (TextView) omTextFrequency6th;
        } else {
            mTextFrequency6th = new TextView(this);
        }
        mTextFrequency6th.setOnClickListener(this);

        Object omTextFrequency7th = findViewById(R.id.frequency7th);
        if (omTextFrequency7th instanceof TextView) {
            mTextFrequency7th = (TextView) omTextFrequency7th;
        } else {
            mTextFrequency7th = new TextView(this);
        }
        mTextFrequency7th.setOnClickListener(this);

        Object omTextFrequency8th = findViewById(R.id.frequency8th);
        if (omTextFrequency8th instanceof TextView) {
            mTextFrequency8th = (TextView) omTextFrequency8th;
        } else {
            mTextFrequency8th = new TextView(this);
        }
        mTextFrequency8th.setOnClickListener(this);

        Object omTextFrequency9th = findViewById(R.id.frequency9th);
        if (omTextFrequency9th instanceof TextView) {
            mTextFrequency9th = (TextView) omTextFrequency9th;
        } else {
            mTextFrequency9th = new TextView(this);
        }
        mTextFrequency9th.setOnClickListener(this);

        Object omTextFrequency10th = findViewById(R.id.frequency10th);
        if (omTextFrequency10th instanceof TextView) {
            mTextFrequency10th = (TextView) omTextFrequency10th;
        } else {
            mTextFrequency10th = new TextView(this);
        }
        mTextFrequency10th.setOnClickListener(this);

        Object omTextFrequency11th = findViewById(R.id.frequency11th);
        if (omTextFrequency11th instanceof TextView) {
            mTextFrequency11th = (TextView) omTextFrequency11th;
        } else {
            mTextFrequency11th = new TextView(this);
        }
        mTextFrequency11th.setOnClickListener(this);

        Object omTextFrequency12th = findViewById(R.id.frequency12th);
        if (omTextFrequency12th instanceof TextView) {
            mTextFrequency12th = (TextView) omTextFrequency12th;
        } else {
            mTextFrequency12th = new TextView(this);
        }
        mTextFrequency12th.setOnClickListener(this);

        Object omFrequencySaveBtn = findViewById(R.id.frequency_save);
        if (omFrequencySaveBtn instanceof ImageView) {
            mFrequencySaveBtn = (ImageView) omFrequencySaveBtn;
        } else {
            mFrequencySaveBtn = new ImageView(this);
        }
        mFrequencySaveBtn.setOnClickListener(this);

        Object omCheckBoxFrequencyDetail = findViewById(R.id.frequency_detail);
        if (omCheckBoxFrequencyDetail instanceof CheckBox) {
            mCheckBoxFrequencyDetail = (CheckBox) omCheckBoxFrequencyDetail;
        } else {
            mCheckBoxFrequencyDetail = new CheckBox(this);
        }
        mCheckBoxFrequencyDetail.setOnCheckedChangeListener(this);
    }

    /**
     * 画面に表示する周波数(skip,5ch～32ch)から周波数チャネル番号(-1,1～13)を取得する
     *
     * @param  frequency 周波数(skip,5ch～32ch)
     * @return String 複数周波数設定周波数チャネル番号(-1,1～13)
     *
     */
    private String changeFrequencyChannel(String frequency) {
        int val = 0;
        if(frequency.equals(SettingActivity.FREQUENCY_5CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh05;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_11CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh11;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_17CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh17;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_23CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh23;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_24CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh24;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_25CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh25;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_26CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh26;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_27CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh27;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_28CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh28;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_29CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh29;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_30CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh30;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_31CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh31;
        }
        else if(frequency.equals(SettingActivity.FREQUENCY_32CH)) {
            val= TecRfidSuite.FrequencyLowChannelTypeCh32;
        }
        else if(frequency.equals(FREQUENCY_SKIP)) {
            val= FREQUENCY_CHANNEL_SKIP;
        }
        return String.valueOf(val);
    }

    /**
     * 番号(-1,1～13)から画面に表示する周波数(skip,5ch～32ch)を取得する
     *
     * @param  frequencyChannel 複数周波数設定周波数チャネル番号(-1,1～13)
     * @return String 周波数(skip,5ch～32ch)
     */
    private String changeFrequencyText(String frequencyChannel) {
        int val = Integer.parseInt(frequencyChannel);
        if(val == TecRfidSuite.FrequencyLowChannelTypeCh05) {
            return SettingActivity.FREQUENCY_5CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh11) {
            return SettingActivity.FREQUENCY_11CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh17) {
            return SettingActivity.FREQUENCY_17CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh23) {
            return SettingActivity.FREQUENCY_23CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh24) {
            return SettingActivity.FREQUENCY_24CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh25) {
            return SettingActivity.FREQUENCY_25CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh26) {
            return SettingActivity.FREQUENCY_26CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh27) {
            return SettingActivity.FREQUENCY_27CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh28) {
            return SettingActivity.FREQUENCY_28CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh29) {
            return SettingActivity.FREQUENCY_29CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh30) {
            return SettingActivity.FREQUENCY_30CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh31) {
            return SettingActivity.FREQUENCY_31CH;
        }
        else if(val == TecRfidSuite.FrequencyLowChannelTypeCh32) {
            return SettingActivity.FREQUENCY_32CH;
        }
        else if(val == FREQUENCY_CHANNEL_SKIP) {
            return FREQUENCY_SKIP;
        }
        return FREQUENCY_SKIP;
    }

    /**
     * 複数周波数設定を周波数画面に反映する
     *
     */
    private void reflectSettings() {
        try {
            SharedPreferences prefs = getSharedPreferences(SETTING_PREFS, Context.MODE_PRIVATE);
            String autoFrequency = prefs.getString(AUTO_FREQUENCY_CHANNEL_KEY, "");
            if(autoFrequency.length()==0) {
                autoFrequency = getDefaultFrequencyList();
            }
            setAllFrequencyText(autoFrequency);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 複数周波数設定デフォルトを取得する
     *
     * @return 複数周波数設定
     */
    private String getDefaultFrequencyList() {
        String autoFrequency = "";
        if (mPowerType.equals(MenuDeviceActivity.JP1)) {
            if(mDeviceType.equals(SettingActivity.DEVICE_TYPE_UF3000)) {
                autoFrequency += TecRfidSuite.FrequencyLowChannelTypeCh26;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh28;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh32;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh17;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh11;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh23;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh29;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh31;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh05;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
            }
            else {
                autoFrequency += TecRfidSuite.FrequencyLowChannelTypeCh26;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh30;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh28;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh32;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh17;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh11;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh23;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh27;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh29;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh31;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh05;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
            }
        }
        else if (mPowerType.equals(MenuDeviceActivity.JP3)){
            if(mDeviceType.equals(SettingActivity.DEVICE_TYPE_UF3000)) {
                autoFrequency += FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh25;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh17;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh11;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh23;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh05;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh24;
            }
            else {
                autoFrequency += FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh17;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh11;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh23;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
                autoFrequency += COMMA + TecRfidSuite.FrequencyLowChannelTypeCh05;
                autoFrequency += COMMA + FREQUENCY_CHANNEL_SKIP;
            }
        }
        else if (mPowerType.equals(JP4)){
            autoFrequency +=TecRfidSuite.FrequencyLowChannelTypeCh26;
            autoFrequency+= COMMA + FREQUENCY_CHANNEL_SKIP;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh28;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh32;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh17;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh11;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh23;
            autoFrequency+= COMMA + FREQUENCY_CHANNEL_SKIP;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh29;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh31;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh05;
            autoFrequency+= COMMA + FREQUENCY_CHANNEL_SKIP;
        }
        else if (mPowerType.equals(JP6)){
            autoFrequency +=FREQUENCY_CHANNEL_SKIP;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyHighChannelTypeCh25;
            autoFrequency+= COMMA + FREQUENCY_CHANNEL_SKIP;
            autoFrequency+= COMMA + FREQUENCY_CHANNEL_SKIP;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh17;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh11;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh23;
            autoFrequency+= COMMA + FREQUENCY_CHANNEL_SKIP;
            autoFrequency+= COMMA + FREQUENCY_CHANNEL_SKIP;
            autoFrequency+= COMMA + FREQUENCY_CHANNEL_SKIP;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyLowChannelTypeCh05;
            autoFrequency+= COMMA + TecRfidSuite.FrequencyHighChannelTypeCh24;
        }
        return autoFrequency;
    }

    /**
     * 1st～12th周波数テキストを設定する
     *
     * @param  autoFrequency 複数周波数設定(1st～12th)
     */
    private void setAllFrequencyText(String autoFrequency) {
        String[] list = autoFrequency.split(COMMA, 0);
        mTextFrequency1st.setText(changeFrequencyText(list[0]));
        mTextFrequency2nd.setText(changeFrequencyText(list[1]));
        mTextFrequency3rd.setText(changeFrequencyText(list[2]));
        mTextFrequency4th.setText(changeFrequencyText(list[3]));
        mTextFrequency5th.setText(changeFrequencyText(list[4]));
        mTextFrequency6th.setText(changeFrequencyText(list[5]));
        mTextFrequency7th.setText(changeFrequencyText(list[6]));
        mTextFrequency8th.setText(changeFrequencyText(list[7]));
        mTextFrequency9th.setText(changeFrequencyText(list[8]));
        mTextFrequency10th.setText(changeFrequencyText(list[9]));
        mTextFrequency11th.setText(changeFrequencyText(list[10]));
        mTextFrequency12th.setText(changeFrequencyText(list[11]));
    }

    @Override
    public void onClick(View view) {
        if(view.equals(mFrequencySaveBtn)) {
            saveSetting();
        }
        else {
            mSelectView = view;
            createSelectDialog(this, mFrequencyList, TITLE_FREQUENCY);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        changeCheckState(isChecked);
    }

    /**
     * 選択ダイアログを表示
     */
    private void createSelectDialog(Context context, ArrayList<String> listItem, String listType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(listType);
        String[] items;
        items = new String[listItem.size()];
        for (int i = 0; i < listItem.size(); i++) {
            // 表示アイテムを指定する //
            items[i] = listItem.get(i);
        }

        builder.setSingleChoiceItems(items, mSelectItem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // どのアイテムが選択されているか
                mSelectItem = which;
            }
        });

        // 決定・キャンセル用にボタンも配置 //
        builder.setPositiveButton(getString(R.string.btn_txt_set), mButtonListener);
        builder.setNeutralButton(getString(R.string.btn_txt_cancel), mButtonListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 決定・キャンセル用ボタンのリスナー
     */
    DialogInterface.OnClickListener mButtonListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    if(mSelectView.equals(mTextFrequency1st)) {
                        mTextFrequency1st.setText(mFrequencyList.get(mSelectItem));
                    }
                    else if(mSelectView.equals(mTextFrequency2nd)){
                        mTextFrequency2nd.setText(mFrequencyList.get(mSelectItem));
                    }
                    else if(mSelectView.equals(mTextFrequency3rd)){
                        mTextFrequency3rd.setText(mFrequencyList.get(mSelectItem));
                    }
                    else if(mSelectView.equals(mTextFrequency4th)){
                        mTextFrequency4th.setText(mFrequencyList.get(mSelectItem));
                    }
                    else if(mSelectView.equals(mTextFrequency5th)){
                        mTextFrequency5th.setText(mFrequencyList.get(mSelectItem));
                    }
                    else if(mSelectView.equals(mTextFrequency6th)){
                        mTextFrequency6th.setText(mFrequencyList.get(mSelectItem));
                    }
                    else if(mSelectView.equals(mTextFrequency7th)){
                        mTextFrequency7th.setText(mFrequencyList.get(mSelectItem));
                    }
                    else if(mSelectView.equals(mTextFrequency8th)){
                        mTextFrequency8th.setText(mFrequencyList.get(mSelectItem));
                    }
                    else if(mSelectView.equals(mTextFrequency9th)){
                        mTextFrequency9th.setText(mFrequencyList.get(mSelectItem));
                    }
                    else if(mSelectView.equals(mTextFrequency10th)){
                        mTextFrequency10th.setText(mFrequencyList.get(mSelectItem));
                    }
                    else if(mSelectView.equals(mTextFrequency11th)){
                        mTextFrequency11th.setText(mFrequencyList.get(mSelectItem));
                    }
                    else if(mSelectView.equals(mTextFrequency12th)){
                        mTextFrequency12th.setText(mFrequencyList.get(mSelectItem));
                    }
                    break;
                case AlertDialog.BUTTON_NEUTRAL:
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * Backボタン操作
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        onBackPressed();
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * 現在の周波数選択画面の複数チャネルとその切り替え順序を
     * アプリの設定情報としてSharedPreferencesに保存
     *
     */
    private void saveSetting() {
        String autoFrequency = "";
        autoFrequency += changeFrequencyChannel(mTextFrequency1st.getText().toString());
        autoFrequency += COMMA +changeFrequencyChannel(mTextFrequency2nd.getText().toString());
        autoFrequency += COMMA +changeFrequencyChannel(mTextFrequency3rd.getText().toString());
        autoFrequency += COMMA +changeFrequencyChannel(mTextFrequency4th.getText().toString());
        autoFrequency += COMMA +changeFrequencyChannel(mTextFrequency5th.getText().toString());
        autoFrequency += COMMA +changeFrequencyChannel(mTextFrequency6th.getText().toString());
        autoFrequency += COMMA +changeFrequencyChannel(mTextFrequency7th.getText().toString());
        autoFrequency += COMMA +changeFrequencyChannel(mTextFrequency8th.getText().toString());
        autoFrequency += COMMA +changeFrequencyChannel(mTextFrequency9th.getText().toString());
        autoFrequency += COMMA +changeFrequencyChannel(mTextFrequency10th.getText().toString());
        autoFrequency += COMMA +changeFrequencyChannel(mTextFrequency11th.getText().toString());
        autoFrequency += COMMA +changeFrequencyChannel(mTextFrequency12th.getText().toString());
        //保存
        SharedPreferences prefs = getSharedPreferences(SETTING_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AUTO_FREQUENCY_CHANNEL_KEY,autoFrequency);
        editor.apply();
        showDialog(getString(R.string.message_frequency_setting_save_success),getString(R.string.message_frequency_setting_save_title),getString(R.string.btn_txt_ok));
    }


    /**
     * [frequency-detail」チェックボックスの
     * 有効無効を切り替える。
     *
     * @param isChecked チェック有無
     */
    private void changeCheckState(boolean isChecked) {
        if(isChecked) {
            LinearLayout popLayout
                    = (LinearLayout)getLayoutInflater().inflate(
                    R.layout.frequency_detail, null);
            mFrequencyDetailPopup = new PopupWindow(this);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                mFrequencyDetailPopup.setWidth(LayoutParams.WRAP_CONTENT);
                mFrequencyDetailPopup.setHeight(LayoutParams.WRAP_CONTENT);
            } else {
                mFrequencyDetailPopup.setWindowLayoutMode(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            mFrequencyDetailPopup.setContentView(popLayout);
            mFrequencyDetailPopup.setBackgroundDrawable(null);
            mFrequencyDetailPopup.showAsDropDown(findViewById(R.id.title_frequency_cycle), 150, 0);
        }
        else {
            mFrequencyDetailPopup.dismiss();
        }

    }

    /**
     * ダイアログ表示
     *
     * @param title 表示タイトル
     * @param message 表示メッセージ
     * @param btn1Txt ボタン1
     */
    protected void showDialog(final String title, final String message, final String btn1Txt) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton(btn1Txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        dialog.show();
    }

}