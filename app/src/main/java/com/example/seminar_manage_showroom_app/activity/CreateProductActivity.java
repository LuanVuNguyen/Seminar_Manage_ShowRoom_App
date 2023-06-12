package com.example.seminar_manage_showroom_app.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.seminar_manage_showroom_app.adapter.notify;
import com.example.seminar_manage_showroom_app.api.Api_CreateProduct;
import com.example.seminar_manage_showroom_app.api.Api_HomeClient;
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

import java.io.ByteArrayOutputStream;
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
    ImageView btn_start, btn_stop, btn_clear, avt_book;
    ListView lv_rfid;
    EditText txt_bookname,txt_author,txt_cate,txt_id,txt_price,txt_pub;
    TextView txt_count;
    SQLiteDatabaseHandler database;
    TextView txt_qty;
    Set<String> setCustomOutput = new HashSet<>();
    Set<String> setCustomInput = new HashSet<>();
    private int IS_SHOW_DIALOG_LIMIT = 0;
    private InforProductEntity inforProductEntity;

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
    ArrayAdapter adapter;
    private ArrayList<String> dataList;
    Set<String> setRfidNotFound = new HashSet<>();
    CharSequence [] list_Cate = {"Romance","Mystery","Fantasy & Science fiction","Self-help","Short Stories","Cookbooks","History","Comic","Horrible & Spreaking"};
    boolean[] selectitem = {false, false, false, false, false, false, false, false,false};
    private static final int CAMERA_REQUEST_CODE = 1;
    private int id_cate;
    String imagetobase64 = null;

    Api_CreateProduct createProduct = new Api_CreateProduct();
    private ArrayList<String> mShowReadData = new ArrayList<String>();

     notify notify = new notify(this);
    List<HttpPostRfid> listHttp = new ArrayList<>();


    /*--------------------------------------------------------------------*/
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
            btn_start.setVisibility(View.GONE);
            btn_stop.setVisibility(View.GONE);
        }
        else if(Constants.CONFIG_DEVICE_NAME.equals((Constants.CONFIG_DEVICE_TOSHIBATEC))){
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                avt_book.setImageBitmap(imageBitmap);
                imagetobase64 = convertBitmapToBase64(imageBitmap);
            }
        }
    }
    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    private void init(){
        btn_creat = (ImageView) findViewById(R.id.btn_create);
        btn_creat.setOnClickListener(this);
        avt_book = (ImageView) findViewById(R.id.avt_book_creat);
        btn_preview = (ImageView) findViewById(R.id.btn_camera);
        btn_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_start = (ImageView) findViewById(R.id.btn_startscan2);
        btn_start.setOnClickListener(this);

        btn_stop = (ImageView) findViewById(R.id.btn_stopscan2);
        btn_stop.setOnClickListener(this);

        btn_clear = (ImageView) findViewById(R.id.btn_clear2);
        btn_clear.setOnClickListener(this);

        txt_bookname = (EditText) findViewById(R.id.txt_edit_bookname);
        txt_author = (EditText) findViewById(R.id.txt_edit_author);
        txt_cate = (EditText) findViewById(R.id.txt_edit_category);
        txt_cate.setText(itemtostring());
        txt_cate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateProductActivity.this);
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setTitle("Select Categories");
                alertDialogBuilder.setMultiChoiceItems(list_Cate, selectitem, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        selectitem[i] = b;
                    }
                });
                alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txt_cate.setText(itemtostring());
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }
        });

        txt_count = (TextView) findViewById(R.id.txt_count);
        txt_id = (EditText) findViewById(R.id.txt_edit_id);
        txt_price = (EditText) findViewById(R.id.txt_edit_price);
        txt_pub = (EditText) findViewById(R.id.txt_edit_publisher);

        lv_rfid = (ListView) findViewById(R.id.list_rfid_create);
        jsonArraytoshiba=new JSONArray();
        arrDataInList = new LinkedList<>();
        inforProductEntity = new InforProductEntity();

        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        lv_rfid.setAdapter(adapter);
    }
    private String itemtostring(){
        String text = "", select="";
        int convert;
        for (int i = 0;i<selectitem.length;i++){
            if (selectitem[i]){
                text = text+i;
                convert = Integer.parseInt(text);
                id_cate = convert + 1;
                text = Integer.toString(id_cate);
                select = select+list_Cate[i];
            }
        }
        return select.trim();
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

            case  R.id.btn_create:
            {
                ApiGetUser();
            }
        }

    }
    private void ApiGetUser(){
        String bookname = txt_bookname.getText().toString();
        String author = txt_author.getText().toString();
        String id = txt_id.getText().toString();
        String price = txt_price.getText().toString();
        String publisher = txt_pub.getText().toString();
        String id_categ = Integer.toString(id_cate);
        int index = 0;
        for (String rfid: setRfidNotFound)
        {
               index++;
               id = id + "0" +Integer.toString(index);
            try {
                createProduct.postData(bookname, author, id_categ, rfid, id, price, publisher, imagetobase64, new Api_CreateProduct.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString(Constants.KEY_CODE).equals(Constants.VALUE_CODE_OK)) {
                                JSONObject resultObj = jsonObject.getJSONObject("result");
                                if (!resultObj.toString().isEmpty()) {
                                    String code = resultObj.getString("code");
                                    if (code.contains("201")) {
                                        showToast("Create product success");
                                    }
                                } else {
                                    showToast("Create product failled");
                                }
                            }
                        } catch (JSONException e) {
                            notify.showDialog("API ERROR",e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        notify.showDialog("API ERROR",errorMessage);
                    }
                });
                Thread.sleep(1000);
            }
            catch (Exception e){
                notify.showDialog("API ERROR",e.getMessage());
            }
            id = txt_id.getText().toString();
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
                        JSONArray err = jArray.getJSONArray(1);

                        if (err != null) {
                            for (int i = 0; i < err.length(); i++) {
                                updateListView(err.get(i).toString());
                                setRfidNotFound.add(err.get(i).toString());
                            }

                            txt_count.setText(MessageFormat.format("{0} : {1}", getText(R.string.count), setRfidNotFound.size()+""));
                        }

                }
            }
            else {
                SupModRfidCommon.showNotifyErrorDialog(CreateProductActivity.this).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void updateListView(String newValue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Thêm giá trị mới vào danh sách
                if (!dataList.contains(newValue)) {
                    dataList.add(newValue);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

}
