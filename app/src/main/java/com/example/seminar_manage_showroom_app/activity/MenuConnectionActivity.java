package com.example.seminar_manage_showroom_app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.seminar_manage_showroom_app.common.interfaces.NotifyForActivityInterface;
import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;

import java.util.ArrayList;

import jp.co.toshibatec.TecRfidSuite;
import jp.co.toshibatec.callback.BluetoothDiscoveryEvent;

public class MenuConnectionActivity extends Activity implements View.OnClickListener, NotifyForActivityInterface, RadioGroup.OnCheckedChangeListener{

    /** 接続ボタン */
    private ImageView mConnectionBtn;

    /** Bluetoothデバイスリスト表示レイアウト */
    private TableLayout mTableLayout;

    /** Bluetoothデバイスリスト */
    private ArrayList<String> mDeviceNameList = new ArrayList<String>();

    /** チェックボックスにて選択されているデバイス */
    private int mCheckIndex = -1;

    /** 接続されているMACアドレス */
    private String mConnectedString = null;

    /** 接続されているデバイス名 */
    private String mConnectedDeviceName = null;

    private boolean mChangeDeviceRadioButton = false;

    /** ダイアログ */
    private AlertDialog.Builder mDialog = null;

    /** ダイアログ用ハンドラー */
    private Handler mShowDialogHandler = new Handler(Looper.getMainLooper());
    /** ダイアログ用ランナブル */
    private Runnable mShowDialogRunnable = null;

    /**
     * GetBluetoothList用引数
     */
    /** AdressArray */
    private ArrayList<String> mAdressArray = new ArrayList<String>();

    /** BluetoothDiscoveryEvent結果取得コールバック */
    private BluetoothDiscoveryEvent mBluetoothDiscoveryEvent = new BluetoothDiscoveryEvent() {
        // Bluetoothデバイスを発見した際にコールされるメソッド
        @Override
        public void onFindDevice(BluetoothDevice device) {
            // 発見したデバイスを追加表示
            updateDevicePairingUnprocessedList(device.getAddress());
        }

        // 検索が終わった時にコールされるメソッド
        @Override
        public void onDiscoveryFinished() {
            // 特に何もしない
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_connect);

        // タイトルバー表記を"デバイス接続"へ変更
        setTitle(R.string.title_connectdevice);

        mConnectionBtn = (ImageView) findViewById(R.id.connection);
        mConnectionBtn.setOnClickListener(this);

        // リスナーを登録
        MenuDeviceActivity.setListener(this);

        mConnectedString = getIntent().getStringExtra(MenuDeviceActivity.KEY_CONNECTED);

        //機種切替ラジオボタン
        RadioButton omRadioButtonUF2200 = findViewById(R.id.radioButtonUF2200);
        RadioButton omRadioButtonUF3000 = findViewById(R.id.radioButtonUF3000);
        RadioGroup omRadioGroupDevice = findViewById(R.id.radioGroupDevice);
        mConnectedDeviceName = getIntent().getStringExtra(MenuDeviceActivity.KEY_DEVICENAME);
        if(mConnectedDeviceName == null){
            omRadioButtonUF2200.setChecked(true);
            omRadioButtonUF3000.setChecked(false);
            mConnectedDeviceName = "UF-2200";
        }
        else{
            if(mConnectedDeviceName.equals("UF-3000")){
                omRadioButtonUF2200.setChecked(false);
                omRadioButtonUF3000.setChecked(true);
            }
            else{
                omRadioButtonUF2200.setChecked(true);
                omRadioButtonUF3000.setChecked(false);
            }
        }
        omRadioGroupDevice.setOnCheckedChangeListener(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // getBluetoothList(ペアリング済み)に成功したら
        if (TecRfidSuite.OPOS_SUCCESS == MenuDeviceActivity.getSDKLibrary().getBluetoothList(mAdressArray)) {
            // デバイスリストを更新
            updateDevicePairingProcessedList();
        } else{
            // エラー表示
            showDialog(getString(R.string.title_error), getString(R.string.message_processfailed_getBluetoothList), getString(R.string.btn_txt_ok), null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // NotifyForActivityInterfaceにnullを格納
        // ペアリング済みじゃない周囲のBluetoothデバイスを検索
        mChangeDeviceRadioButton = false;
        if (MenuDeviceActivity.getSDKLibrary().getState() == TecRfidSuite.OPOS_S_CLOSED) {
            if(mConnectedDeviceName.equals("UF-3000")){
                //UF-3000
                MenuDeviceActivity.getSDKLibrary().open("UF-3000",MenuDeviceActivity.getInstance().getMenuDeviceActivityContext());
            } else {
                //UF-2200
                MenuDeviceActivity.getSDKLibrary().open("UF-2200",MenuDeviceActivity.getInstance().getMenuDeviceActivityContext());
            }
        }
        if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().startBluetoothDiscovery(this, mBluetoothDiscoveryEvent)){
            showDialog(getString(R.string.title_error), getString(R.string.message_processfailed_startBluetoothDiscovery), getString(R.string.btn_txt_ok), null);
        }
    }

    @Override
    protected void onPause() {
        // startBluetoothDiscoveryをストップ
        if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().stopBluetoothDiscovery()){
            showDialog(getString(R.string.title_error), getString(R.string.message_processfailed_stopBluetoothDiscovery), getString(R.string.btn_txt_ok), null);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mBluetoothDiscoveryEvent = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.devicelistupdate:
                //リスト更新ボタン
                if(mTableLayout!=null) {
                    mTableLayout.removeAllViews();
                }
                if (TecRfidSuite.OPOS_SUCCESS == MenuDeviceActivity.getSDKLibrary().getBluetoothList(mAdressArray)) {
                    // デバイスリストを更新
                    updateDevicePairingProcessedList();
                }
                break;
            default:
                // インテントのインスタンス作成
                Intent data = new Intent();
                //ラジオボタン変更の有無をMenuDeviceActivityに渡す
                data.putExtra(MenuDeviceActivity.KEY_CHANGEDEVICERADIOBUTTON, mChangeDeviceRadioButton);
                // 結果を設定
                setResult(RESULT_CANCELED, data);
                onBackPressed();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onClick(View view) {
        // 接続ボタンの場合
        if (view.equals(mConnectionBtn)) {
            // 検知デバイス分ループを回し、チェックの有無を確認
            for (int i = 0; i < mDeviceNameList.size(); i++) {
                CheckBox check = (CheckBox) mTableLayout.getChildAt(i).findViewById(R.id.CheckBox02);
                if (check.isChecked()) {
                    // チェックが入っていれば
                    if (mCheckIndex == -1) {
                        // インデックスを格納
                        mCheckIndex = i;
                    }
                    // 複数のデバイスが選択されていた場合
                    else {
                        // エラーダイアログ
                        showDialog(getString(R.string.title_selectdevice), getString(R.string.message_selectdevice_error), getString(R.string.btn_txt_ok), null);
                        mCheckIndex = -1;
                        break;
                    }
                }
            }
            // 1項目のみチェックが入っていれば
            if (mCheckIndex != -1) {
                // MACアドレスを格納
                TextView checkText = (TextView) mTableLayout.getChildAt(mCheckIndex).findViewById(R.id.devicename);
                mCheckIndex = -1;
                // インテントのインスタンス作成
                Intent data = new Intent();
                // インテントに値をセット
                data.putExtra(MenuDeviceActivity.KEY_CONNECTIONREQUEST, checkText.getText().toString());
                //選択されたデバイス名をMenuDeviceActivityに渡す
                data.putExtra(MenuDeviceActivity.KEY_DEVICENAME, mConnectedDeviceName);
                //ラジオボタン変更の有無をMenuDeviceActivityに渡す
                data.putExtra(MenuDeviceActivity.KEY_CHANGEDEVICERADIOBUTTON, mChangeDeviceRadioButton);
                // 結果を設定
                setResult(RESULT_OK, data);
                onBackPressed();
            }
        }
    }

    /**
     * getBluetoothListで取得したペアリング済みデバイスを表示更新
     */
    private void updateDevicePairingProcessedList() {
        // getBluetoothListで取得している場合
        if (null != mAdressArray) {
            mDeviceNameList = mAdressArray;
            // 取得したBluetoothList分ループ
            for (int i = 0; i < mDeviceNameList.size(); i++) {
                TableRow tr = (TableRow) getLayoutInflater().inflate(R.layout.devicelist, null);
                tr.setBackgroundResource(R.drawable.underline);
                TextView tvDevaiceName = (TextView) tr.findViewById(R.id.devicename);
                // テキストに取得したアドレスをセット
                tvDevaiceName.setText(mDeviceNameList.get(i));
                if (null != mConnectedString && -1 != mConnectedString.indexOf(mDeviceNameList.get(i))) {
                    ((CheckBox) tr.findViewById(R.id.CheckBox02)).setChecked(true);
                }
                mTableLayout = (TableLayout) findViewById(R.id.tl_device);
                // レイアウトに追加
                mTableLayout.addView(tr);
            }
        }
    }

    /**
     * startBluetoothDiscoveryで取得したペアリング済みではないデバイスを表示更新
     *
     * @param macAdrress
     */
    private void updateDevicePairingUnprocessedList(String macAdrress) {
        TableRow tr = (TableRow) getLayoutInflater().inflate(R.layout.devicelist, null);
        tr.setBackgroundResource(R.drawable.underline);
        TextView tvDevaiceName = (TextView) tr.findViewById(R.id.devicename);
        tvDevaiceName.setText(macAdrress);
        mDeviceNameList.add(macAdrress);
        mTableLayout = (TableLayout) findViewById(R.id.tl_device);
        // レイアウトに追加
        mTableLayout.addView(tr);
    }

    @Override
    public void disconnectDevice(String title, String message, String btn1) {
        showDialog(title, message, btn1, null);
    }

    /**
     * エラーダイアログ表示
     *
     * @param title 表示タイトル
     * @param message 表示メッセージ
     * @param btn1Txt ボタン1
     * @param btn2Txt ボタン2(不要ならnull)
     */
    private void showDialog(final String title, final String message, final String btn1Txt, final String btn2Txt) {
        showDialog(title, message, btn1Txt, btn2Txt, null, null);
    }

    /**
     * エラーダイアログ表示
     *
     * @param title 表示タイトル
     * @param message 表示メッセージ
     * @param btn1Txt ボタン1
     * @param btn2Txt ボタン2(不要ならnull)
     * @param positiveRun OKボタン押下
     * @param negativeRun キャンセルボタン押下
     */
    private void showDialog(final String title, final String message, final String btn1Txt, final String btn2Txt, final Runnable positiveRun, final Runnable negativeRun) {
        if (null != mShowDialogHandler) {
            mShowDialogRunnable = new Runnable() {
                @Override
                public void run() {
                    mDialog = new AlertDialog.Builder(MenuConnectionActivity.this);
                    mDialog.setTitle(title);
                    mDialog.setMessage(message);
                    mDialog.setPositiveButton(btn1Txt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (null != positiveRun) {
                                positiveRun.run();
                            }
                        }
                    });
                    if (null != btn2Txt) {
                        mDialog.setNegativeButton(btn2Txt, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (null != negativeRun) {
                                    negativeRun.run();
                                }
                            }
                        });
                    }
                    mDialog.show();
                }
            };
            mShowDialogHandler.post(mShowDialogRunnable);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//
//        //[更新]ボタン
//        //Create an option menu from res/menu/version.xml
//        getMenuInflater().inflate(R.menu.devicelistupdate, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(mTableLayout != null){
            mTableLayout.removeAllViews();
        }
        if(mAdressArray != null){
            mAdressArray.clear();
        }

        //変更フラグOn
        mChangeDeviceRadioButton = true;

        int nRet = TecRfidSuite.OPOS_SUCCESS;

        if (MenuDeviceActivity.getSDKLibrary().getState() != TecRfidSuite.OPOS_S_CLOSED) {
            //BLEのクローズ処理は時間がかかる場合があるので、砂時計を表示
            //クローズしてしまう
            MenuDeviceActivity.getSDKLibrary().close();
        }
        //BLEデバイス検索終了
        MenuDeviceActivity.getSDKLibrary().stopBluetoothDiscovery();
        //機種切替スイッチで指定された機種でオープン
        if(checkedId != -1){
            String selectedRadioButton = ((RadioButton)findViewById(checkedId)).getText().toString();
            if(selectedRadioButton.equalsIgnoreCase("UF-3000")){
                nRet = MenuDeviceActivity.getSDKLibrary().open("UF-3000",MenuDeviceActivity.getInstance().getMenuDeviceActivityContext());
                if (TecRfidSuite.OPOS_SUCCESS != nRet ){
                    // 不具合以外ではここにこない
                    return;
                }
                mConnectedDeviceName = "UF-3000";
            }
            else{
                nRet = MenuDeviceActivity.getSDKLibrary().open("UF-2200",MenuDeviceActivity.getInstance().getMenuDeviceActivityContext());
                if (TecRfidSuite.OPOS_SUCCESS != nRet ){
                    // 不具合以外ではここにこない
                    return;
                }
                mConnectedDeviceName = "UF-2200";
                //機種切替スイッチで指定された機種で検索が行われる
                MenuDeviceActivity.getSDKLibrary().startBluetoothDiscovery(MenuDeviceActivity.getInstance().getMenuDeviceActivityContext(),mBluetoothDiscoveryEvent);
            }
            // getBluetoothList(ペアリング済み)に成功したら
            if (TecRfidSuite.OPOS_SUCCESS == MenuDeviceActivity.getSDKLibrary().getBluetoothList(mAdressArray)) {
                // デバイスリストを更新
                updateDevicePairingProcessedList();
            }
        }
    }
}
