package com.example.seminar_manage_showroom_app.activity;

import static com.example.seminar_manage_showroom_app.activity.LibAccessBaseActivity.END;
import static com.example.seminar_manage_showroom_app.activity.LibAccessBaseActivity.START;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.adapter.ProductAdapter;
import com.example.seminar_manage_showroom_app.api.Api_GetInfoProduct;
import com.example.seminar_manage_showroom_app.common.Constants;
import com.example.seminar_manage_showroom_app.common.Product;
import com.example.seminar_manage_showroom_app.common.interfaces.NotifyForActivityInterface;
import com.example.seminar_manage_showroom_app.log.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import jp.co.toshibatec.TecRfidSuite;
import jp.co.toshibatec.callback.ConnectionEventHandler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link search_location#newInstance} factory method to
 * create an instance of this fragment.
 */
public class search_location extends Fragment {
    private LibAccessBaseActivity libAccessBaseActivity;
    private static search_location mSearchRadaMenuActivity = null;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText txt_bookname;
    private TextView txt_targetRFID;
    private String mParam1;
    private String mParam2;
    private Button btn_continue;
    private ListView lv_search;
    private static Activity mActivity;
    private static NotifyForActivityInterface mSettingTool = null;
    private Thread mClaimDeviceThread = null;
    private Runnable mClaimDeviceRunnable = null;
    private String mConnectedString = null;
    private static final String DEVICENAME_UF3000 = "UF-3000";
    private boolean mDisconnectFlag = false;
    private String mReConnectString = null;
    private boolean isSelectedEPC = true;
    private String searchTarget = null;
    private EditText mSearchEditText = null;
    public static final String KEY_TARGET = "target";
    public static final String KEY_SELECTED_EPC = "isSelectedEPC";
    private ArrayList<String> mEpcCodeList = new ArrayList<String>();
    public static final String KEY_EPCLIST = "epclist";
    public static final String KEY_EXCLUSIONLIST = "exclusionlist";
    private ArrayList<String> mExclusionList = new ArrayList<String>();
    public static final int DEFAULT_LOG_SIZE = 1024 * 10;
    private int mSDKLogSize = DEFAULT_LOG_SIZE;
    private static final int WRITE_TO_CONSOLE_AND_SD = 0;
    private int mSDKLogLevel = TecRfidSuite.LOG_LEVEL_INFO;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    protected String mStoragePath = null;
    private static final String SETTING_PATH = "/TEC/SearchSample/";
    private static final String SETTING_FILENAME = "Setting.txt";
    public final static String KEY_FW_MODE = "FwMode";
    private static final String COMMA = ",";
    public static final String SEARCH_PREFS = "serchprefs";
    public static final String SEARCH_FW_MODE = "Config_FWMode";
    public final static String KEY_RADAR_DRAW_MODE = "Radar_DrawMode";
    public static final int DEFAULT_RADAR_DRAW_MODE = 1;
    public static final String SEARCH_RADAR_DRAW_MODE = "Config_Radar_DrawMode";
    public static final int DEFAULT_FW_MODE = 1;
    private static final String NEWLINE = "\n";
    Api_GetInfoProduct info_product = new Api_GetInfoProduct();
    ArrayList<Product> productList = new ArrayList<>();
    private static boolean isFirst = true;

    /*--------------------------------------------------------------------------------*/
    public search_location(Activity activity) {
        this.mActivity = activity;
    }

    public static search_location newInstance(String param1, String param2) {
        search_location fragment = new search_location(mActivity);
        LibAccessBaseActivity libAccessBaseActivity = new LibAccessBaseActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        fragment.setLib(libAccessBaseActivity);
        return fragment;
    }


    @Override
    public void onResume() {
        Log.info(START);
        super.onResume();
        if(Constants.CONFIG_SIGNAL_CONECT!="1"){
            if(isFirst) {
                isFirst = false;
                int result = libAccessBaseActivity.open(DEVICENAME_UF3000, mActivity, mSDKLogLevel, mSDKLogSize);
                if (result == TecRfidSuite.OPOS_SUCCESS) {
                    libAccessBaseActivity.close();
                }
            }
        }
        Log.info(END);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        checkPermission();
        readInitSettingFile();
        if(mActivity.getActionBar()!=null) {
            mActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.setMaxFileSize(mSDKLogSize);
        Log.setLogOutPut(WRITE_TO_CONSOLE_AND_SD);
        Log.setNowLevel(mSDKLogLevel);
    }

    public void setLib(LibAccessBaseActivity libAccessBaseActivity){
        this.libAccessBaseActivity = libAccessBaseActivity;
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
        GetInfoProduct();
        txt_bookname = (EditText) view.findViewById(R.id.txt_search_bookname);
        txt_targetRFID = (TextView) view.findViewById(R.id.target_text);
        lv_search = (ListView) view.findViewById(R.id.lv_search_location);
        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Xử lý sự kiện khi người dùng nhấp vào mục tại vị trí 'position'
                Product selectedProduct = productList.get(position);
                String rfidValue = selectedProduct.getX_RFID_PRODUCT();
                txt_targetRFID.setText(rfidValue);
            }
        });

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
                    System.out.println("");
                }
            }
        });
        mSearchRadaMenuActivity = this;
        Object omSearchEditText = view.findViewById(R.id.target_text);
        if (omSearchEditText instanceof EditText) {
            mSearchEditText = (EditText) omSearchEditText;
        } else {
            mSearchEditText = new EditText(mActivity);
        }

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(btn_continue)) {
                    Intent intent = new Intent(getActivity(), SearchLocationActivity.class);
                    if (isSelectedEPC) {
//                      searchTarget = mSearchEditText.getText().toString();
                        searchTarget = txt_targetRFID.getText().toString();
                        if (searchTarget.length() == 0) {
                            libAccessBaseActivity.showDialog(getString(R.string.title_error), getString(R.string.message_target_not_set_error), getString(R.string.btn_txt_ok), null);
                            return;
                        }
                        intent.putExtra(KEY_TARGET, searchTarget);
                        intent.putExtra(KEY_SELECTED_EPC, isSelectedEPC);
                    } else {
                        if (mEpcCodeList.size() == 0) {
                            libAccessBaseActivity.showDialog(getString(R.string.title_error), getString(R.string.message_target_not_set_error), getString(R.string.btn_txt_ok), null);
                            return;
                        }
                        intent.putExtra(KEY_EPCLIST, mEpcCodeList);
                        intent.putExtra(KEY_EXCLUSIONLIST, mExclusionList);
                        intent.putExtra(KEY_SELECTED_EPC, mExclusionList);
                    }
                    startActivity(intent);
                }
            }
        });

        return view;
    }
    private void GetInfoProduct(){
        info_product.getProductInfo(new Api_GetInfoProduct.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString(Constants.KEY_CODE).equals(Constants.VALUE_CODE_OK)) {
                        JSONObject resultObj = jsonObject.getJSONObject("result");
                        JSONArray productsArray = resultObj.getJSONArray("products");
                        for (int i = 0; i < productsArray.length(); i++) {
                            JSONObject productJson = productsArray.getJSONObject(i);

                            String name = productJson.getString("name");
                            String x_RFID_PRODUCT = productJson.getString("x_RFID_PRODUCT");

                            Product product = new Product(name, x_RFID_PRODUCT);
                            productList.add(product);
                        }

                        ProductAdapter adapter = new ProductAdapter(mActivity, productList);
                        lv_search.setAdapter(adapter);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
                System.out.println(errorMessage);
            }
        });
    }

    public static void setListener(NotifyForActivityInterface listener) {
        mSettingTool = listener;
    }
    public static search_location getInstance() {
        return mSearchRadaMenuActivity;
    }
    public boolean getDisconnectFlag() {
        return mDisconnectFlag;
    }
    public void deviceReConnect(Context context) {
        final String connectRequestAddress = mReConnectString;
        // 結果コード
        int result;
        if (  libAccessBaseActivity.getState() == TecRfidSuite.OPOS_S_CLOSED) {
            result =   libAccessBaseActivity.openReconnect(DEVICENAME_UF3000, mActivity);
        } else {
            result = TecRfidSuite.OPOS_SUCCESS;
        }
        // openが成功したら
        if (TecRfidSuite.OPOS_SUCCESS == result) {
            libAccessBaseActivity.showProgress(context);
            mClaimDeviceRunnable = new Runnable() {
                public void run() {
                    int claimDeviceResult =   libAccessBaseActivity.claimDeviceReconnect(connectRequestAddress, mConnectionEventCallback);
                    libAccessBaseActivity.dismissProgress();
                    // claimDeviceが成功したら
                    if (TecRfidSuite.OPOS_SUCCESS == claimDeviceResult) {
                        mDisconnectFlag = false;
                        if (mSettingTool != null) {
                            //mSettingTool.reConnectDeviceSuccess();
                        }
                        // setDeviceEnabledが成功したら
                        if (TecRfidSuite.OPOS_SUCCESS ==   libAccessBaseActivity.setDeviceEnabledReconnect(true)) {
                            mConnectedString = connectRequestAddress;
                        }
                    } else {
                        if (mSettingTool != null) {
                            // mSettingTool.reConnectDeviceFailed();
                        }
                        libAccessBaseActivity.close();
                    }
                }
            };
            // claimDeviceを別スレッドで呼ぶ
            mClaimDeviceThread = new Thread(mClaimDeviceRunnable);
            mClaimDeviceThread.start();
        }
    }
    private ConnectionEventHandler mConnectionEventCallback = new ConnectionEventHandler() {
        @Override
        public void onEvent(int state) {
            Log.info(START);
            // オンライン以外なら
            if (state != TecRfidSuite.ConnectStateOnline) {
                mDisconnectFlag = true;
                libAccessBaseActivity.dismissProgress();
                if (null != mConnectedString) {
                    mReConnectString = mConnectedString;
                }
                mConnectedString = null;
                String message;
                if (state == TecRfidSuite.ConnectStateOffline) {
                    message = getString(R.string.message_connectstate_offline);
                } else {
                    message = getString(R.string.message_connectstate_none);
                }
                if (null != mSettingTool) {
                    mSettingTool.disconnectDevice(getString(R.string.title_error), message,
                            getString(R.string.btn_txt_ok));
                } else {
                    // エラー表示
                    libAccessBaseActivity.showDialog(getString(R.string.title_error), message, getString(R.string.btn_txt_ok), null);
                }
            } else {
                mDisconnectFlag = false;
            }
            Log.info(END);
        }
    };
    private void checkPermission(){
        //権限リクエストが複数ある場合、このリストに追加する
        ArrayList<String> tmpList = new ArrayList<>();
        //権限の有無
        Boolean perExternalStorage = true;
        Boolean perAccessLocation = true;
        //前回権限リクエスト拒否の有無
        Boolean rejectExternalStorage = false;
        Boolean rejectAccessLocation = false;

        //ストレージ書き込み権限チェック
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            perExternalStorage = false;
            tmpList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //前回権限リクエスト拒否しているか
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                rejectExternalStorage = true;
            }
        }
        //位置情報権限チェック
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            perAccessLocation = false;
            tmpList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            //前回権限リクエスト拒否しているか
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                rejectAccessLocation = true;
            }
        }
        //onClick用
        final ArrayList<String> listPermissions = tmpList;

        //ストレージ書き込み、位置情報のどちらかの権限がない、または両方とも権限がない場合、リクエストを行う
        if (!perExternalStorage || !perAccessLocation) {

            //前回権限リクエスト拒否しているか
            if(rejectExternalStorage || rejectAccessLocation) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                if (rejectExternalStorage && rejectAccessLocation) {
                    //両方拒否
                    dialog.setTitle(R.string.permission_All_title);
                    dialog.setMessage(R.string.permission_All_message);
                } else if (rejectExternalStorage) {
                    //ストレージ書き込みのみ拒否
                    dialog.setTitle(R.string.permission_title);
                    dialog.setMessage(R.string.permission_message);
                } else if (rejectAccessLocation) {
                    //位置情報のみ拒否
                    dialog.setTitle(R.string.permission_AccessLocation_title);
                    dialog.setMessage(R.string.permission_AccessLocation_message);
                }
                dialog.setPositiveButton(R.string.btn_txt_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //パーミッションのリクエスト
                        ActivityCompat.requestPermissions(mActivity, listPermissions.toArray(new String[listPermissions.size()]),
                                REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                    }
                });
                dialog.show();
            }
            else {
                //パーミッションのリクエスト
                ActivityCompat.requestPermissions(mActivity, listPermissions.toArray(new String[listPermissions.size()]),
                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION:
                for(int i = 0 ; i < permissions.length ; i++){
                    switch (permissions[i]){
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                //設定ファイル読み込み
                                readInitSettingFile();
                                //許可成功
                                libAccessBaseActivity.showDialog(null, getString(R.string.permission_success), getString(R.string.btn_txt_ok), null);
                            } else {
                                if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    //許可失敗
                                    libAccessBaseActivity.showDialog(null, getString(R.string.permission_denied), getString(R.string.btn_txt_ok), null);
                                }
                                else {
                                    //許可失敗
                                    libAccessBaseActivity.showDialog(null, getString(R.string.permission_failed), getString(R.string.btn_txt_ok), null);
                                }

                            }
                            break;
                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                //許可成功
                                libAccessBaseActivity.showDialog(null, getString(R.string.permission_AccessLocation_success), getString(R.string.btn_txt_ok), null);
                            } else {
                                if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                    //許可失敗
                                    libAccessBaseActivity.showDialog(null, getString(R.string.permission_AccessLocation_denied), getString(R.string.btn_txt_ok), null);
                                }
                                else {
                                    //許可失敗
                                    libAccessBaseActivity.showDialog(null, getString(R.string.permission_AccessLocation_failed), getString(R.string.btn_txt_ok), null);
                                }

                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }
    private void readInitSettingFile() {
        String filePath = mStoragePath + SETTING_PATH + SETTING_FILENAME;
        File file = new File(filePath);
        // ファイルが存在しなければ
        if (!file.exists()) {
            // デフォルト値でファイル作成
            createInitSettingFIle(mStoragePath);
        }
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            int index = 0;
            while (br.ready()) {
                String line = br.readLine();
                if (null != line) {
                    if (line.indexOf(KEY_FW_MODE + COMMA) != -1) {
                        index = line.indexOf(KEY_FW_MODE + COMMA);
                        index += (KEY_FW_MODE + COMMA).length();
                        line = line.substring(index);
                        int i = Integer.parseInt(line);
                        SharedPreferences prefs = libAccessBaseActivity.getSharedPreferences(SEARCH_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt(SEARCH_FW_MODE, i);
                        editor.apply();
                    }else if(line.indexOf(KEY_RADAR_DRAW_MODE + COMMA) != -1) {
                        index = line.indexOf(KEY_RADAR_DRAW_MODE + COMMA);
                        index += (KEY_RADAR_DRAW_MODE + COMMA).length();
                        line = line.substring(index);
                        int i = Integer.parseInt(line);
                        SharedPreferences prefs = libAccessBaseActivity.getSharedPreferences(SEARCH_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt(SEARCH_RADAR_DRAW_MODE, i);
                        editor.apply();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != isr) {
                    isr.close();
                }
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void createInitSettingFIle(String storagePath) {
        String filePath = storagePath + SETTING_PATH;
        File dir = new File(filePath);
        boolean isCreate = dir.mkdirs();
        // ディレクトリがないとき
        if (!isCreate && !dir.exists()) {
            // 特にエラーは出さない
            return;
        }

        FileOutputStream fos = null;
        BufferedWriter bw = null;
        OutputStreamWriter osw = null;
        try {
            filePath = filePath + SETTING_FILENAME;
            File file = new File(filePath);
            // ファイルがあれば作りなおし
            if (file.exists()) {
                boolean fileDel = file.delete();
                if (!fileDel) {
                    // 特にエラーは出さない
                    return;
                }
            }
            String writeDate = null;
            writeDate = KEY_FW_MODE + COMMA + DEFAULT_FW_MODE;
            writeDate = writeDate + NEWLINE + KEY_RADAR_DRAW_MODE + COMMA + DEFAULT_RADAR_DRAW_MODE;
            fos = new FileOutputStream(filePath, true);
            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
            if(writeDate!=null) {
                bw.write(writeDate);
            }
            bw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // 特にエラーは出さない
        } catch (IOException e) {
            e.printStackTrace();
            // 特にエラーは出さない
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != osw) {
                    osw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != bw) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}