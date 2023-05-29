package com.example.seminar_manage_showroom_app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Insets;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.adapter.ListViewAdapterSearch;
import com.example.seminar_manage_showroom_app.adapter.ListViewScanAdapter;
import com.example.seminar_manage_showroom_app.api.HttpPostRfid;
import com.example.seminar_manage_showroom_app.api.HttpPostRfidSearch;
import com.example.seminar_manage_showroom_app.api.HttpRfidResponse;
import com.example.seminar_manage_showroom_app.api.HttpRfidResponseSearch;
import com.example.seminar_manage_showroom_app.common.Config;
import com.example.seminar_manage_showroom_app.common.Constants;
import com.example.seminar_manage_showroom_app.common.Message;
import com.example.seminar_manage_showroom_app.common.entities.InforProductEntity;
import com.example.seminar_manage_showroom_app.common.function.SupModRfidCommon;
import com.example.seminar_manage_showroom_app.common.interfaces.Callable;
import com.example.seminar_manage_showroom_app.connect.ConnectThreadScan;
import com.example.seminar_manage_showroom_app.connect.ConnectThreadSearch;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link search_info#newInstance} factory method to
 * create an instance of this fragment.
 */
public class search_info extends Fragment  implements HttpRfidResponseSearch {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static Activity mActivity;

    Set<String> setCustomOutput = new HashSet<>();

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
    ConnectThreadSearch connectThreadScan = null;
    private boolean isReadBackPress = false;
    Set<String> setRfidNotFound = new HashSet<>();
    TextView txt_name, txt_id, txt_rfid, txt_author, txt_cate, txt_des;
    /*------------------------------------------------------------*/

    public search_info(Activity activity) {
        this.mActivity = activity;
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment search_info.
     */
    // TODO: Rename and change types and number of parameters
    public static search_info newInstance(String param1, String param2) {
        search_info fragment = new search_info(new Activity());
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
        jsonArraytoshiba = new JSONArray();
        if (Constants.CONFIG_DEVICE_NAME.equals(Constants.CONFIG_DEVICE_ATS100)) {
            mActivity.runOnUiThread(new Runnable() {
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
            startReadtag();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search_info, container, false);
        txt_name = (TextView) view.findViewById(R.id.txt_info_bookname);
        txt_id = (TextView) view.findViewById(R.id.txt_info_bookname);
        txt_rfid = (TextView) view.findViewById(R.id.txt_info_bookname);
        txt_author = (TextView) view.findViewById(R.id.txt_info_bookname);
        txt_cate =  (TextView) view.findViewById(R.id.txt_info_bookname);
        txt_des = (TextView) view.findViewById(R.id.txt_info_bookname);
        return view;
    }

    private void startReadtag(){
        if (!mIsStartReadTags){
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

    private void showToast(String s) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity,s+"",Toast.LENGTH_LONG).show();
            }
        });
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
        protected Long
        doInBackground(String... params)
        {
            ArrayList<String> a = new ArrayList<String>();
            if (Check.equals(true)) {
                for (int i = 0; i < mReadData.size(); i++) {
                    if (-1 == mShowReadData.indexOf(mReadData.get(i))) {
                        Log.d("RFID data: ",""+mReadData.get(i));
                        jsonArraytoshiba.put(mReadData.get(i).toUpperCase());
                        if (jsonArraytoshiba.length() != 0) {
                            new HttpPostRfidSearch(search_info.this).execute(Config.CODE_LOGIN,Config.HTTP_SERVER_SHOP+Config.API_ODOO_GETMULTIPLEPRODUCT, jsonArraytoshiba.toString());
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
    private search_info.UpdateReadTagDataTask mUpdateReadTagDataTask = null;
    private DataEventHandler mDataEvent = new DataEventHandler() {
        @Override
        public void onEvent(HashMap<String, TagPack> tagList) {
            for (Map.Entry<String, TagPack> e : tagList.entrySet()) {
                String key = e.getKey();
                mReadData.add(key);
            }
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
                mUpdateReadTagDataTask = new search_info.UpdateReadTagDataTask();
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
                mActivity.finish();
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
            mProgressBar = new ProgressBar(mActivity, null, android.R.attr.progressBarStyleLarge);
            //スクリーンサイズを取得する
            int width;
            int height;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowMetrics display = mActivity.getWindowManager().getCurrentWindowMetrics();
                // 画面サイズ取得
                Insets insets = display.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars() | WindowInsets.Type.displayCutout());
                width = display.getBounds().width() - (insets.right + insets.left);
                height = display.getBounds().height() - (insets.top + insets.bottom);
            } else {
                Display display = mActivity.getWindowManager().getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                width = point.x;
                height = point.y;
            }

            ViewGroup rootView = (ViewGroup) mActivity.getWindow().getDecorView();
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mProgressBar.setPadding(width * 3 / 8, height * 3 / 8, width * 3 / 8, height * 3 / 8);
            rootView.addView(mProgressBar, params);

        }
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        isShowProgress = true;
    }
    private void initDeviceScanVN() {
        //bluetoothDeviceConnect();
        connectThreadScan = new ConnectThreadSearch();
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectThreadScan.connect(bluetoothDeviceConnected2(), search_info.this, new Callable() {
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
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
    private void showProgressRunUi(){
        mActivity.runOnUiThread(new Runnable() {
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
            stopReadtag();
        }
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    List<HttpPostRfid> listHttp = new ArrayList<>();
    @Override
    public void progressRfidFinishSearch(String output, int typeRequestApi, String fileName) {
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

                    JSONArray jArray1 = jArray.getJSONArray(0);

                    for (int j = 0; j < jArray1.length() ; j++)
                    {
                        JSONObject obj2 = jArray1.getJSONObject(j);

                        String stringRfid= obj2.getString(Constants.KEY_RFID);

                        if(setCustomOutput.add(stringRfid))
                        {
                            try{
                                //setDataEntity(obj2);
                            }
                            catch (Exception e)
                            {
                                Log.e("save database faile",e.getMessage());
                            }
                        }
                        JSONArray err = jArray.getJSONArray(1);
                        if (err != null) {
                            for (int i = 0; i < err.length(); i++) {

                                setRfidNotFound.add(err.get(i).toString());
                            }

                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}