package com.example.seminar_manage_showroom_app.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.Insets;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.adapter.ListViewScanAdapter;
import com.example.seminar_manage_showroom_app.api.HttpPostRfid;
import com.example.seminar_manage_showroom_app.api.HttpRfidResponse;
import com.example.seminar_manage_showroom_app.common.Config;
import com.example.seminar_manage_showroom_app.common.Constants;
import com.example.seminar_manage_showroom_app.common.Message;
import com.example.seminar_manage_showroom_app.common.entities.InforProductEntity;
import com.example.seminar_manage_showroom_app.common.function.SupModRfidCommon;
import com.example.seminar_manage_showroom_app.common.interfaces.Callable;
import com.example.seminar_manage_showroom_app.connect.ConnectThreadScan;
import com.example.seminar_manage_showroom_app.database.SQLiteDatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.toshibatec.TecRfidSuite;
import jp.co.toshibatec.callback.DataEventHandler;
import jp.co.toshibatec.callback.ErrorEventHandler;
import jp.co.toshibatec.callback.ResultCallback;
import jp.co.toshibatec.model.TagPack;

public class CreateProductActivity extends AppCompatActivity implements HttpRfidResponse, View.OnClickListener{
    ImageView btn_preview, btn_creat;
    ImageView btn_start, btn_stop, btn_clear;
    ListView lv_rfid, lv_pre;
    EditText txt_bookname,txt_author,txt_cate,txt_id,txt_price,txt_pub;
    TextView txt_count;
    SQLiteDatabaseHandler database;
    TextView txt_qty, txt_total, txt_cash, txt_change_cash;
    Set<String> setCustomOutput = new HashSet<>();
    Set<String> setCustomInput = new HashSet<>();
    private int IS_SHOW_DIALOG_LIMIT = 0;
    CheckBox check_pay_cash, check_pay_tranfers;
    private InforProductEntity inforProductEntity;
    ListView lv_pay;
    private int scan_size;
    private Runnable mDissmissProgressRunnable = null;
    private Handler mDissmissProgressHandler = new Handler(Looper.getMainLooper());
    ToneGenerator toneG;
    private boolean mIsStartReadTags = false;
    private static final TecRfidSuite mLib = TecRfidSuite.getInstance();
    private String mFilterID = "00000000";
    private String mFiltermask = "00000000";
    private int mStartReadTagsTimeout = 10000;
    private Boolean Check = true;
    private JSONArray jsonArraytoshiba = null;
    private static final int CARRIERSENSEERROR = 19;
    private static final int WAVEOUTPUTBLOCKERROR = 21;
    private static final int TAGDATAFULLBUFFERERROR = 65;
    private ProgressBar mProgressBar = null;
    private boolean isShowProgress = false;
    ConnectThreadScan connectThreadScan = null;
    private boolean isReadBackPress = false;
    private LinkedList<InforProductEntity> arrDataInList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createproduct);
        init();
        if (Constants.CONFIG_DEVICE_NAME.equals(Constants.CONFIG_DEVICE_ATS100)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        showProgress();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    initDeviceScanVN();
                }
            });
        }
        else if(Constants.CONFIG_DEVICE_NAME.equals((Constants.CONFIG_DEVICE_TOSHIBATEC))){
        }
    }
    private void init(){
        btn_creat = (ImageView) findViewById(R.id.btn_creat);
        btn_creat.setOnClickListener(this);

        btn_preview = (ImageView) findViewById(R.id.btn_preview);
        btn_preview.setOnClickListener(this);

        btn_start = (ImageView) findViewById(R.id.btn_startscan2);
        btn_start.setOnClickListener(this);

        btn_stop = (ImageView) findViewById(R.id.btn_stopscan2);
        btn_stop.setOnClickListener(this);

        btn_clear = (ImageView) findViewById(R.id.btn_clear2);
        btn_clear.setOnClickListener(this);

        txt_bookname = (EditText) findViewById(R.id.txt_edit_bookname);
        txt_author = (EditText) findViewById(R.id.txt_edit_author);
        txt_cate = (EditText) findViewById(R.id.txt_edit_category);
        txt_count = (TextView) findViewById(R.id.txt_count);
        txt_id = (EditText) findViewById(R.id.txt_edit_id);
        txt_price = (EditText) findViewById(R.id.txt_edit_price);
        txt_pub = (EditText) findViewById(R.id.txt_edit_publisher);

        lv_pre = (ListView) findViewById(R.id.list_preview);
        lv_rfid = (ListView) findViewById(R.id.list_rfid_create);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_startscan2:
            {
                startReadtag();
                break;
            }

            case R.id.btn_stopscan2:
            {
                stopReadtag();
                break;
            }

            case R.id.btn_clear2:
            {
                break;

            }
        }

    }



    private void showToast(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateProductActivity.this,s+"",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void startReadtag(){
        if (!mIsStartReadTags){
            btn_start.setImageResource(R.drawable.btn_play_bur);
            btn_stop.setImageResource(R.drawable.btn_top);
            showToast("Start scan!!!");
            mIsStartReadTags = true;
            if (TecRfidSuite.OPOS_SUCCESS != mLib.startReadTags(mFilterID, mFiltermask, mStartReadTagsTimeout, mDataEvent, mErrorEvent)){
                Date currentTime = Calendar.getInstance().getTime();
                Log.i("Start scan","" + currentTime);
            }
            if (TecRfidSuite.OPOS_SUCCESS != mLib.setDataEventEnabled(true)){
                Log.i("Set data","True");
            }
        }
    }
    private ArrayList<String> mShowReadData = new ArrayList<String>();
    private class UpdateReadTagDataTask extends AsyncTask<String, String, Long> {
        @Override
        protected void onPostExecute(Long result) {
            if (TecRfidSuite.OPOS_SUCCESS != mLib.setDataEventEnabled(true)){
                System.out.println("Vuluan:onPostExecute "+result);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            int size = mShowReadData.size();
            for (int i = 0; i < values.length; i++) {
                if(null != values[i] && 0 != values[i].length()){
                    if (Check.equals(true)) {
                        if (mShowReadData.indexOf(values[i]) == -1) {
                            size = mShowReadData.size();
                            mShowReadData.add(values[i]);
                        }
                    }
                    else {
                        size = mShowReadData.size();
                        mShowReadData.add(values[i]);
                    }
                }
            }
        }
        @SuppressLint("WrongThread")
        @Override
        protected Long doInBackground(String... params)
        {
            ArrayList<String> a = new ArrayList<String>();
            if (Check.equals(true)) {
                for (int i = 0; i < mReadData.size(); i++) {
                    if (-1 == mShowReadData.indexOf(mReadData.get(i))) {
                        Log.i("RFID data: ",""+mReadData.get(i));
                        jsonArraytoshiba.put(mReadData.get(i).toUpperCase());
                        if (jsonArraytoshiba.length() != 0) {
                            new HttpPostRfid(CreateProductActivity.this).execute(Config.CODE_LOGIN,Config.HTTP_SERVER_SHOP+Config.API_ODOO_GETMULTIPLEPRODUCT, jsonArraytoshiba.toString());
                        }
                        a.add(mReadData.get(i));
                        if (a.size() >= 50) {
                            publishProgress(a.toArray(new String[a.size()]));
                            a.clear();
                            soundBeep();
                        }
                    }
                }
            }
            else {
                for (int i = 0; i < mReadData.size(); i++) {
                    a.add(mReadData.get(i));
                    if (a.size() >= 50) {
                        publishProgress(a.toArray(new String[a.size()]));
                        a.clear();
                        soundBeep();
                    }
                }
            }
            if(!a.isEmpty()) {
                publishProgress(a.toArray(new String[a.size()]));
                a.clear();
                soundBeep();
            }
            mReadData.clear();
            return null;
        }
    }
    private ArrayList<String> mReadData = new ArrayList<String>();
    private CreateProductActivity.UpdateReadTagDataTask mUpdateReadTagDataTask = null;
    private DataEventHandler mDataEvent = new DataEventHandler() {
        @Override
        public void onEvent(HashMap<String, TagPack> tagList) {
            for (Map.Entry<String, TagPack> e : tagList.entrySet()) {
                String key = e.getKey();
                mReadData.add(key);
            }
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
                mUpdateReadTagDataTask = new CreateProductActivity.UpdateReadTagDataTask();
                mUpdateReadTagDataTask.execute("");
            }

        }
    };
    private ErrorEventHandler mErrorEvent = new ErrorEventHandler() {
        @Override
        public void onEvent(int resultCode, int resultCodeExtended) {

            if (TecRfidSuite.OPOS_SUCCESS != resultCode){
                if (resultCodeExtended != CARRIERSENSEERROR && resultCodeExtended != WAVEOUTPUTBLOCKERROR && resultCodeExtended != TAGDATAFULLBUFFERERROR) {

                }
            }
        }
    };
    private void stopReadtag() {
        if (mIsStartReadTags)
        {
            btn_start.setImageResource(R.drawable.btn_play);
            btn_stop.setImageResource(R.drawable.btn_top_bur);
            showToast("Stop scan!!!");
            if (TecRfidSuite.OPOS_SUCCESS == mLib.stopReadTags(mStopReadTagsResultCallback)) {
                Date currentTime = Calendar.getInstance().getTime();
                Log.i("Stop scan"," " + currentTime);
            }
        }
    }
    private ResultCallback mStopReadTagsResultCallback = new ResultCallback() {
        @Override
        public void onCallback(int resultCode, int resultCodeExtended) {
            if (TecRfidSuite.OPOS_SUCCESS != resultCode){
                System.out.println("Vuluan: onCallback "+ resultCode);
            }
            dismissProgress();
            mIsStartReadTags = false;
            // buttonValid();
            if(isReadBackPress){
                isReadBackPress = false;
                finish();
            }
        }
    };
    private void soundBeep() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
        toneGenerator.release();
    }
    protected void showProgress() {
        if (mProgressBar == null) {
            mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
            //スクリーンサイズを取得する
            int width;
            int height;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowMetrics display = this.getWindowManager().getCurrentWindowMetrics();
                // 画面サイズ取得
                Insets insets = display.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars() | WindowInsets.Type.displayCutout());
                width = display.getBounds().width() - (insets.right + insets.left);
                height = display.getBounds().height() - (insets.top + insets.bottom);
            } else {
                Display display = this.getWindowManager().getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                width = point.x;
                height = point.y;
            }

            ViewGroup rootView = (ViewGroup) getWindow().getDecorView();
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mProgressBar.setPadding(width * 3 / 8, height * 3 / 8, width * 3 / 8, height * 3 / 8);
            rootView.addView(mProgressBar, params);

        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        isShowProgress = true;
    }
    private void initDeviceScanVN() {
        //bluetoothDeviceConnect();
        connectThreadScan = new ConnectThreadScan();
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectThreadScan.connect(bluetoothDeviceConnected2(), CreateProductActivity.this, new Callable() {
                    @Override
                    public void call(boolean result) {
                        showToast("starting…");
                        dismissProgress();
                    }
                });

            }
        }).start();

    }
    private BluetoothDevice bluetoothDeviceConnected2() {
        BluetoothDevice deviceTemp = null;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(InventoryActivity.this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
//        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            int i=0;
            for (BluetoothDevice device : pairedDevices) {
                //String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(i==0)
                    deviceTemp=device;
                if(deviceHardwareAddress.equals(Constants.CONFIG_MAC_HANDWARE)) {
                    System.out.println(deviceHardwareAddress);
                    System.out.println(Constants.CONFIG_MAC_HANDWARE);
                    return device;
                }
                i++;
            }
        }
        return deviceTemp;
    }
    protected void dismissProgress() {
        mDissmissProgressRunnable = new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                if (null != mProgressBar) {
                    mProgressBar.setVisibility(ProgressBar.GONE);
                }
            }
        };
        if (null != mDissmissProgressHandler) {
            mDissmissProgressHandler.post(mDissmissProgressRunnable);
            isShowProgress = false;
        }
    }
    private void setDataEntity(JSONObject obj) {
        String bar1,rfid,name,category;
        int quantity = 0;
        int cost = 0;
        int tax = 0;
        try
        {
            bar1 = obj.getString(Constants.KEY_JANCODE_1);
            quantity = obj.getInt("quantity");
            name = obj.getString(Constants.KEY_GOOD_NAME);
            rfid = obj.getString(Constants.KEY_RFID);
            category = obj.getString("Product Category");
            inforProductEntity.setBarcodeCD1(bar1);
            inforProductEntity.setBasePrice(cost);
            inforProductEntity.setTypeProduct(Constants.TYPE_TABLE_INVENTORY);
            inforProductEntity.setQuantity(quantity);
            inforProductEntity.setTaxIncludePrice(tax);
            inforProductEntity.setGoodName(name);
            inforProductEntity.setRfidCode(rfid);
            inforProductEntity.setCategory(category);
            processBarcode(bar1,cost);
        }
        catch (JSONException e)
        {
            Log.e("setdataentity",""+e.getMessage());
        }
    }
    private void processBarcode(String strBarcode,int cost){
        //CHECK OVER LIMIT ONCE
        if(arrDataInList.size()>=Constants.LIMIT_ONCE){
            showDialogMessageConfirmSaveToContinue();
            return;
        }
        if(strBarcode.isEmpty()){
            return;
        }int price = cost;

        digestBarcode(strBarcode,price);
    }
    private void digestBarcode(String bar_code,int money) {
        addOtherBarcode(bar_code);
    }
    private void addOtherBarcode(String bar_code) {

        // SA-150 修正_UPC-A対応 EDIT START
//        inforProductEntity.setProductCode1(bar_code);
        int bar_code_length = bar_code.length();
        switch (bar_code_length) {
            case 8:
                inforProductEntity.setBarcodeCD1(bar_code + "     ");
                break;
            case 12:
                inforProductEntity.setBarcodeCD1("0" + bar_code);
                break;
            default:
                inforProductEntity.setBarcodeCD1(bar_code);
                break;
        }
        inforProductEntity.setBarcodeCD2(Constants.BLANK);
        updateCurrentView();
        try {
            toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

        }
        catch (RuntimeException e )
        {
            e.printStackTrace();
        }
    }
    private void showDialogMessageConfirmSaveToContinue() {
        if(IS_SHOW_DIALOG_LIMIT==0) {
            //STOP DEVICE SCAN
            IS_SHOW_DIALOG_LIMIT=1;
            //Show message confirm
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateProductActivity.this);
            alertDialog.setMessage(String.format(Message.MESSAGE_CONFIRM_OVER_DATA, Constants.LIMIT_ONCE));

            alertDialog.setCancelable(false);

            // Configure alert dialog button
            alertDialog.setPositiveButton(Message.YES_REGISTER_DATA, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Save list view to database
                    showProgressRunUi();
                    database.insertAllProductsinvCallBack(arrDataInList, new Callable() {
                        @Override
                        public void call(boolean result) {
                            if(result==true){
                                showToast(arrDataInList.size()+"");
                                //arrDataInList.clear();
                                restartListView();
                                //eventEnableButton();
                                IS_SHOW_DIALOG_LIMIT=0;
                                dismissProgress();
                            }
                        }
                    });

                }
            });
            alertDialog.setNegativeButton(Message.NOT_REGISTER_DATA, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    //eventEnableButton();
                    IS_SHOW_DIALOG_LIMIT=0;
                }
            });

            AlertDialog alert = alertDialog.show();
            //eventDisableButton();
            //eventOpenButton(false);
            TextView messageText = (TextView) alert.findViewById(android.R.id.message);
            assert messageText != null;
            messageText.setGravity(Gravity.CENTER);
        }
    }
    private void showProgressRunUi(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgress();
            }
        });
    }
    @Override
    public void onDestroy() {
        if(Constants.CONFIG_DEVICE_NAME.equals(Constants.CONFIG_DEVICE_ATS100)) {
            connectThreadScan.cancel();
        }
        else if (TecRfidSuite.OPOS_SUCCESS != mLib.stopReadTags(mStopReadTagsResultCallback)){
        }
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    private void restartListView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update list view
                ListViewScanAdapter adapterBook = new ListViewScanAdapter(CreateProductActivity.this,
                        arrDataInList);
                lv_pay.setAdapter(adapterBook);
                // Show total number and price
                callTotalNumberAndPrice();

                // Get size of data scan in list view
                scan_size = arrDataInList.size();
                Log.i("Datalist size: ",""+scan_size);
            }
        });
    }
    private void callTotalNumberAndPrice() {

        int intQuantity = 0;
        for (int i = 0; i < arrDataInList.size(); i++) {
            intQuantity += arrDataInList.get(i).getQuantity();
        }
        txt_qty.setText(MessageFormat.format("{0} : {1}", String.valueOf(getText(R.string.total_quantity)), intQuantity));

    }
    private void updateCurrentView() {
        arrDataInList.add(0, inforProductEntity);
        inforProductEntity = new InforProductEntity();
        restartListView();
    }
    private void initListViewScreen() {

        // check array null
        arrDataInList = (LinkedList<InforProductEntity>) getLastCustomNonConfigurationInstance();

        if (arrDataInList == null) {
            arrDataInList = new LinkedList<>();
        }

        // Check if array is not null
        restartListView();

    }
    private void reloadSQLiteData(){
        setCustomOutput.clear();
        setCustomOutput.clear();
        //ADD SQLITE DATA
        try {
            for(InforProductEntity i : database.getAllProductsinvbyType("inventory")){
                setCustomInput.add(i.getRfidCode());
                setCustomOutput.add(i.getRfidCode());
            }
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

    List<HttpPostRfid> listHttp = new ArrayList<>();
    @Override
    public void progressRfidFinish(String output, int typeRequestApi, String fileName) {
        // KILL ALL HTTP
        if(output.contains("Exception")){
            for(HttpPostRfid http : listHttp){
                http.cancel(true);
            }
        }
        try {
            Log.d("OUTPUT", output);
            JSONObject jsonObject = new JSONObject(output);
            if (SupModRfidCommon.isStatusHttpOk(output)) {
                if (jsonObject.getString(Constants.KEY_CODE).equals(Constants.VALUE_CODE_OK)) {

                    JSONArray jArray = jsonObject.getJSONArray(Constants.KEY_DATA);

                    Log.d("Response",""+jArray);

                    JSONArray jArray1 = jArray.getJSONArray(0);

                    Log.d("Response head",""+jArray1);

                    for (int j = 0; j < jArray1.length() ; j++)
                    {

                        JSONObject obj2 = jArray1.getJSONObject(j);

                        Log.d("obj2",""+obj2);

                        String stringRfid= obj2.getString(Constants.KEY_RFID);

                        Log.d("stringRfid",""+stringRfid);

                        if(setCustomOutput.add(stringRfid))
                        {
                            try{
                                setDataEntity(obj2);
                            }
                            catch (Exception e)
                            {
                                Log.e("save database faile",e.getMessage());
                            }

                        }
                        JSONArray err = jArray.getJSONArray(1);
                        if (err != null) {
                            for (int i = 0; i < err.length(); i++) {
                                //setRfidNotFound.add(err.get(i).toString());
                            }

                            //total_error.setText(MessageFormat.format("{0} : {1}", getText(R.string.total_error), setRfidNotFound.size()+""));
                        }
                    }
                }
            } else {
                SupModRfidCommon.showNotifyErrorDialog(CreateProductActivity.this).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
