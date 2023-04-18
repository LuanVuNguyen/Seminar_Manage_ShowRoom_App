package com.example.seminar_manage_showroom_app.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Insets;
import android.graphics.Point;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.common.Constants;
import com.example.seminar_manage_showroom_app.common.interfaces.NotifyForActivityInterface;

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
import java.util.HashMap;
import java.util.Iterator;

import jp.co.toshibatec.TecRfidSuite;
import jp.co.toshibatec.callback.BatteryLevelCallback;
import jp.co.toshibatec.callback.ConnectionEventHandler;
import jp.co.toshibatec.callback.FirmwareVerCallback;
import jp.co.toshibatec.callback.ResultCallback;

public class MenuDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    /** インターフェイス */
    private static NotifyForActivityInterface mSampleApp = null;

    /** デバイス接続ボタン */
    private ImageButton mDeviceConnectionBtn;
    /** 設定ボタン */
    private ImageButton mSettingBtn;
    /** 読取テストボタン */
    /** バーコードボタン */
    private ImageButton mBarcodeBtn;

    /** デバイス名用テキスト */
    private TextView mTvDevice = null;
    /** FW Ver用テキスト */
    private TextView mTvFmver = null;
    /** バッテリーの画像 */
    private ImageView mButteryView = null;
    /** バッテリー残量テキスト */
    private TextView mButteryText = null;
    /** % バッテリー残量テキスト用 */
    private static final String PERCENT = "%";

    /** 表示用ハンドラー */
    private Handler mViewHandler = new Handler(Looper.getMainLooper());
    /** 表示用ランナブル */
    private Runnable mViewRunnable = null;
    /** ライブラリアクセス中プログレス */
    private ProgressBar mProgressBar = null;
    /** プログレス表示フラグ */
    private boolean isShowProgress = false;
    /** プログレスディスミス用ハンドラー */
    private Handler mDissmissProgressHandler = new Handler(Looper.getMainLooper());
    /** プログレスディスミス用ランナブル */
    private Runnable mDissmissProgressRunnable = null;
    /** ダイアログ用ハンドラー */
    private Handler mShowDialogHandler = new Handler(Looper.getMainLooper());
    /** ダイアログ用ランナブル */
    private Runnable mShowDialogRunnable = null;
    /** ダイアログ */
    private AlertDialog.Builder mDialog = null;

    /** 遷移元がConnectDeviceActivityか識別する為の要求コード(任意の固有値) */
    private static final int CONNECTDEVICE_ACTIVITY = 1001;
    /** MACアドレス受け渡し用キー (ConnectDeviceActivity → MenuDeviceActivity) */
    public static final String KEY_CONNECTIONREQUEST = "connectionRequest";
    /** MACアドレス受け渡し用キー (MenuDeviceActivity → ConnectDeviceActivity) */
    public static final String KEY_CONNECTED = "connected";
    /** 接続ラジオボタン変更の有無 */
    public static final String KEY_CHANGEDEVICERADIOBUTTON = "changeDeviceRadioButton";
    /** デバイス名受け渡し用キー (MenuDeviceActivity → ConnectDeviceActivity) */
    public static final String KEY_DEVICENAME = "devicename";

    /** JP1 or JP2 or JP3 or JP4 or JP5 or JP6*/
    private static String mPowerType = "";
    /** JP1 or JP2 or JP3 or JP4 or JP5 or JP6受け渡し用キー */
    public static final String KEY_POWERTYPE = "powerType";
    /** 前回接続デバイスMACアドレス保存ディレクトリパス */
    private static final String SAVE_CONNECTED_PATH = "/TEC/tool/connected/";
    /** ファイル名 */
    private static final String SAVE_CONNECTED_FILENAME = "ConnectedDevice.txt";
    /** 前回接続デバイス名保存ディレクトリパス */
    private static final String SAVE_CONNECTED_DEVNAME_PATH = "/TEC/tool/connected/";
    /** ファイル名 */
    private static final String SAVE_CONNECTED_DEVNAME_FILENAME = "ConnectedDeviceName.txt";
    /** 前回接続デバイスMACアドレス保存ディレクトリパス */
    private static final String INITSETTING_PATH = "/TEC/tool/initsetting/";
    /** ファイル名 */
    private static final String INITSETTING_FILENAME = "InitSetting.txt";
    /** SDK出力ログサイズ */
    private int mSDKLogSize = DEFAULT_LOGSIZE;
    /** SDK出力ログレベル */
    private int mSDKLogLevel = TecRfidSuite.LOG_LEVEL_INFO;
    /** ログサイズ(デフォルト) */
    public static final int DEFAULT_LOGSIZE = 1024 * 10;
    /** テキスト書込み用 */
    public final static String KEY_AUTOFREQUENCYLIST = "AutoFrequencyList";
    /** テキスト書込み用 */
    public final static String KEY_SDKLOGLEVEL = "SDKLogLevel";
    /** テキスト書込み用 */
    public final static String KEY_SDKLOGSIZE = "SDKLogSize";
    /** フラグAB三選択肢表示有無(デフォルト) */
    public static final int DEFAULT_THREE_CHOICES_FLAG_AB = 0;
    /** テキスト書込み用 */
    public final static String KEY_THREE_CHOICES_FLAG_AB = "ThreeChoicesFlagABSetting";
    /** フラグAB三選択肢表示有無 */
    private boolean isThreeChoicesFlagAB = false;
    /** テキスト書込み用 */
    private static final String COMMA = ",";
    /** 改行 */
    private static final String NEWLINE = "\n";
    /** AutoFrequencyList */
    private ArrayList<Integer> mAutoFrequencyList = new ArrayList<Integer>();
    /** FWバージョンが取得後ファイルを再生成するか */
    private static boolean createNewFileAfterGetFWVer = false;
    /** ライブラリインスタンス */
    private static final TecRfidSuite  mLib = TecRfidSuite.getInstance();
    /** resultCodeExtendedがないとき設定 */
    protected static final int NOT_RESULTCODEEXTENDED = -1;
    /** JP1(特小) */
    public static final String JP1 = "JP1";
    /** JP2(免許局) */
    public static final String JP2 = "JP2";
    /** JP3(登録局) */
    public static final String JP3 = "JP3";
    /** JP4(スキャナ搭載－特小) */
    public static final String JP4 = "JP4";
    /** JP5(スキャナ搭載－免許局) */
    public static final String JP5 = "JP5";
    /** JP6(スキャナ搭載－登録局) */
    public static final String JP6 = "JP6";

    /**
     * claimDevice用引数
     */
    /** 接続済みMACアドレス */
    private String mConnectedString = null;
    /** 接続済みデバイスFWver */
    private String mFWverString = null;
    /** 接続要求MACアドレス */
    private String mConnectionRequestString = null;
    /** 接続要求デバイス名 */
    private String mConnectionDevicename = null;
    /** 接続ラジオボタン変更の有無 */
    private boolean mChangeDeviceRadioButton = false;

    /** ストレージパス保存用 */
    private String mStoragePath;

    /**
     * getBatteryLevel用引数
     */
    /** バッテリー残量 */
    private int mBatteryLevel = 0;

    /**
     * open用引数
     */
    /** deviceName */
    private static final String DEVICENAME = "UF-2200";
    /** deviceName(UF-3000) */
    private static final String DEVICENAME_UF3000 = "UF-3000";
    /**
     * ログレベル取得（ConnectDeviceActivityで使用）
     * @return
     */
    public int getSDKLogLevel(){
        return mSDKLogLevel;
    }
    /**
     * ログサイズ取得（ConnectDeviceActivityで使用）
     * @return
     */
    public int getSDKLogSize(){
        return mSDKLogSize;
    }


    /**
     * claimDevice用引数
     */
    /** 切断検知コールバック */
    private ConnectionEventHandler mConnectionEventCallback = new ConnectionEventHandler() {
        @Override
        public void onEvent(int state) {
            // オンライン以外なら
            if (state != TecRfidSuite.ConnectStateOnline) {
                dismissProgress();
                mConnectedString = null;
                mFWverString = null;
                mBatteryLevel = 0;
                viewUpdate();
                String message = null;
                if (state == TecRfidSuite.ConnectStateOffline) {
                    message = getString(R.string.message_connectstate_offline);
                } else {
                    message = getString(R.string.message_connectstate_none);
                }
                if (null != mSampleApp) {
                    mSampleApp.disconnectDevice(getString(R.string.title_error), message, getString(R.string.btn_txt_ok));
                } else {
                    // エラー表示
                    showDialog(getString(R.string.title_error), message, getString(R.string.btn_txt_ok), null);
                }
            } else {
                if (getCommunicationMode() == TecRfidSuite.CommunicationMode.COMMUNICATION_MODE_USB.getInt()) {
                    if (isClaimed() == false) {
                        deviceConnect("USB");
                    }
                }
                try {
                    mContext.unregisterReceiver(mUsbReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * getFirmwareVer用引数
     */
    /** FirmwareVerCallback
     * 0         1         2         3         4         5         6         7         8         9         10
     * 01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789
     * d:"TEC UHF RFID UF-2200             JP1 Z#00C2014-03-06",x:"R2000 rev 00A0 0",y:"RFID1 Z#00  sppi_v2.04"
     * */
    private FirmwareVerCallback mFirmwareVerCallback = new FirmwareVerCallback() {
        @Override
        public void onCallback(String firmVer, int resultCode, int resultCodeExtended) {
            resultDialog(resultCode, resultCodeExtended, getString(R.string.message_processfailed_getFirmwareVer));
            if (TecRfidSuite.OPOS_SUCCESS == resultCode) {
                StringBuilder sb = new StringBuilder();
                sb.append(firmVer.substring(36, 45));
                sb.append(firmVer.substring(92, 103));
                // ファームウエアバージョンを格納
                mFWverString = new String(sb);
                // JP1かJP2かJP3かJP4かJP5かJP6を格納
                mPowerType = (firmVer.substring(36, 39)).trim();
                if(createNewFileAfterGetFWVer){
                    createNewFileAfterGetFWVer = false;
                    createInitSettingFIle(mStoragePath);
                    mAutoFrequencyList.clear();
                    readInitSettingFile();
                }
                viewUpdate();
            }
        }
    };

    /**
     * getBatteryLevel用引数
     */
    /** BatteryLevelCallback */
    private BatteryLevelCallback mBatteryLevelCallback = new BatteryLevelCallback() {
        @Override
        public void onCallback(int level, int resultCode, int resultCodeExtended) {
            resultDialog(resultCode, resultCodeExtended, getString(R.string.message_processfailed_getBatteryLevel));
            if (TecRfidSuite.OPOS_SUCCESS == resultCode) {
                mBatteryLevel = level;
                viewUpdate();
            }
        }
    };

    private static MenuDeviceActivity mMenuDeviceActivity = null;

    /**
     * コンテキスト取得
     * @return
     */
    public Context getMenuDeviceActivityContext() {
        return MenuDeviceActivity.this;
    }

    /**
     * 唯一のインスタンスを返す
     *
     * @return TecRfidSuite インスタンス
     */
    public static MenuDeviceActivity getInstance() {
        return mMenuDeviceActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        //設定ファイル読み込み
        readInitSettingFile();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);

        mMenuDeviceActivity = this;

        // タイトルバー表記を"メニュー"へ変更
        setTitle(R.string.title_menu);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            mStoragePath = getApplicationContext().getExternalFilesDir(null) + "";
        } else {
            mStoragePath = Environment.getExternalStorageDirectory() + "";
        }
        mDeviceConnectionBtn = (ImageButton) findViewById(R.id.menu_deviceconnection);
        mDeviceConnectionBtn.setOnClickListener(this);

        mSettingBtn = (ImageButton) findViewById(R.id.menu_setting);
        mSettingBtn.setOnClickListener(this);


        Object omTvDevice = findViewById(R.id.tv_device);
        if (omTvDevice instanceof TextView) {
            mTvDevice = (TextView) omTvDevice;
        } else {
            mTvDevice = new TextView(MenuDeviceActivity.this);
        }
        Object omTvFmver = findViewById(R.id.tv_fmver);
        if (omTvFmver instanceof TextView) {
            mTvFmver = (TextView) omTvFmver;
        } else {
            mTvFmver = new TextView(MenuDeviceActivity.this);
        }

        Object omButteryView = findViewById(R.id.icon_buttery);
        if (omButteryView instanceof ImageView) {
            mButteryView = (ImageView) omButteryView;
        } else {
            mButteryView = new ImageView(MenuDeviceActivity.this);
        }
        Object omButteryText = findViewById(R.id.tv_buttery);
        if (omButteryText instanceof TextView) {
            mButteryText = (TextView) omButteryText;
        } else {
            mButteryText = new TextView(MenuDeviceActivity.this);
        }

        String connectedAddress = readConnectedAddressFile();
        // 前回接続済み端末があれば
        if (null != connectedAddress) {
            mConnectionRequestString = connectedAddress;
        }
        String connectedDeviceName = readConnectedDeviceNameFile();
        // 前回接続済み端末があれば
        if (null != connectedDeviceName) {
            mConnectionDevicename = connectedDeviceName;
        }

        //メインメニュー表示時に、バッテリー残量の表示更新が行われない場合があるため対策。
        //ここでは、デバイス接続ボタンのサイズが変更されるたびにバッテリー残量の表示更新を行うようにする
        mDeviceConnectionBtn.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // バッテリー残量を取得
                if(mConnectedString != null) {
                    getBatteryLevel(mBatteryLevelCallback);
                }
            }
        });

        //deviceValidate();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListener(null);
        String connectedAddress = mConnectedString;
        final String connectRequestAddress = mConnectionRequestString;
        if((mChangeDeviceRadioButton) && (null == connectRequestAddress)){
            //デバイス接続画面で、ラジオボタン変更、かつ戻るボタン押下の場合、
            //切断状態になっているので、画面を未接続状態に戻す
            mConnectedString = null;
            mFWverString = null;
            mBatteryLevel = 0;
            viewUpdate();
            return;
        }
        if(isUSBPermissonDlgShow == true) {
            mChangeDeviceRadioButton = false;
            return;
        }

        // 接続要求デバイスがあれば
        if (null != connectRequestAddress) {
            // 接続済みデバイスがある
            if (null != connectedAddress) {
                // 違うデバイスに接続したい
                if (!connectRequestAddress.equals(connectedAddress)) {
                    // デバイス切断
                    deviceDisConnect();
                    // デバイス接続
                    deviceConnect(connectRequestAddress);
                } else {
                    //通信断チェックを行い、切断の場合は接続処理を行う
                    if(mChangeDeviceRadioButton){
                        // デバイス接続
                        deviceConnect(connectRequestAddress);
                    }
                    else {
                        // バッテリー残量を取得
                        getBatteryLevel(mBatteryLevelCallback);
                    }
                }
            } else {
                //前回USB接続、かつパーミッションなしの場合、なにもしない（ひとまず待つ）
                if(connectRequestAddress.equalsIgnoreCase("USB") && !mUsbPermissionGranted) {
                    mChangeDeviceRadioButton = false;
                    return;
                }
                // デバイス接続
                deviceConnect(connectRequestAddress);
            }
        } else {
            // 接続済みデバイスがある
            if (null != connectedAddress) {
                // バッテリー残量を取得
                getBatteryLevel(mBatteryLevelCallback);
            }
        }
        mChangeDeviceRadioButton = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // ConnectDeviceActivityなら
        if (requestCode == CONNECTDEVICE_ACTIVITY) {
            mChangeDeviceRadioButton = data.getBooleanExtra(KEY_CHANGEDEVICERADIOBUTTON, false);
            // resultCodeがOKか確認する
            if (resultCode == RESULT_OK) {
                mConnectionRequestString = data.getStringExtra(KEY_CONNECTIONREQUEST);
                mConnectionDevicename = data.getStringExtra(KEY_DEVICENAME);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.menu_deviceconnection:{
                Intent intent = new Intent(MenuDeviceActivity.this, ConnectDeviceActivity.class);
                intent.putExtra(KEY_CONNECTED, mConnectedString);
                intent.putExtra(KEY_DEVICENAME, mConnectionDevicename);
                startActivityForResult(intent, CONNECTDEVICE_ACTIVITY);
                return;
            }

            case R.id.menu_setting:
            {
                Intent intent = new Intent(MenuDeviceActivity.this, SettingActivity.class);
                intent.putExtra(KEY_POWERTYPE, mPowerType);
                intent.putIntegerArrayListExtra(KEY_AUTOFREQUENCYLIST, mAutoFrequencyList);
                intent.putExtra(KEY_THREE_CHOICES_FLAG_AB,isThreeChoicesFlagAB);
                intent.putExtra(KEY_DEVICENAME, mConnectionDevicename);
                startActivity(intent);
                return;
            }

            case R.id.menu_barcode:{
                Constants.CONFIG_SIGNAL_CONECT="1";
                Intent intent = new Intent(MenuDeviceActivity.this, MenuBussinessActivity.class);
                startActivity(intent);
                return;
            }

        }
    }

    @Override
    protected void onDestroy() {
        // デバイス切断
        deviceDisConnect();
        mViewHandler.removeCallbacks(mViewRunnable);
        mViewHandler = null;
        mViewRunnable = null;
        mConnectionEventCallback = null;
        mFirmwareVerCallback = null;
        mBatteryLevelCallback = null;
        mShowDialogHandler.removeCallbacks(mShowDialogRunnable);
        mShowDialogHandler = null;
        mShowDialogRunnable = null;
        mDissmissProgressHandler.removeCallbacks(mDissmissProgressRunnable);
        mDissmissProgressHandler = null;
        mDissmissProgressRunnable = null;
        super.onDestroy();
    }

    /**
     * SDKインスタンスを取得する
     *
     * @return  mLib　ライブラリインスタンス
     */
    public static TecRfidSuite getSDKLibrary() {
        return mLib;
    }

    /**
     * リスナーをセットする
     *
     * @param listener
     */
    public static void setListener(NotifyForActivityInterface listener) {
        mSampleApp = listener;
    }

    /**
     * デバイスのOpen状態を取得
     *
     * @return Open状態
     */
    protected int getState() {
        return mLib.getState();
    }

    /**
     * デバイスのClaim状態を取得
     *
     * @return Claim状態
     */
    protected boolean isClaimed() {
        return mLib.isClaimed();
    }

    /**
     * デバイスのEnabled状態を取得
     *
     * @return Enabled状態
     */
    protected boolean isDeviceEnabled() {
        return mLib.isDeviceEnabled();
    }

    /**
     * open(ログサイズ指定用)
     *
     * @param deviceName オープンするデバイス名
     * @param logLevel SDK出力ログレベル
     * @param logSize SDK出力ログサイズ
     */
    protected int open(String deviceName, int logLevel, int logSize) {
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        //ログレベル
        hm.put(TecRfidSuite.OptionPackKeyLogLevel, logLevel);
        //ログサイズ
        hm.put(TecRfidSuite.OptionPackKeyLogFileSize, logSize);

        int resultCode = mLib.open(deviceName, mContext);
        resultDialog(resultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_open));
        mLib.setOptions(hm);
        hm = null;
        return resultCode;
    }

    /**
     * open(UF-3000対応)
     *
     * @param deviceName オープンするデバイス名
     * @param context アプリのコンテキスト
     * @param logLevel SDK出力ログレベル
     * @param logSize SDK出力ログサイズ
     * @return
     */
    protected int open(String deviceName, Context context, int logLevel, int logSize) {
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        //ログレベル
        hm.put(TecRfidSuite.OptionPackKeyLogLevel, logLevel);
        //ログサイズ
        hm.put(TecRfidSuite.OptionPackKeyLogFileSize, logSize);

        int resultCode = mLib.open(deviceName, context);
        resultDialog(resultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_open));
        mLib.setOptions(hm);
        hm = null;
        return resultCode;
    }

    /**
     * claimDevice
     *
     * @param connectionString UF-2200の場合は、BluetoothのMACアドレスを指定
     * @param callback 切断検知コールバック
     */
    protected int claimDevice(String connectionString, ConnectionEventHandler callback) {
        int resultCode = mLib.claimDevice(connectionString, callback);
        resultDialog(resultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_claimDevice));
        return resultCode;
    }

    /**
     * close
     */
    protected void close() {
        int resultCode = mLib.close();
        resultDialog(resultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_close));
    }

    /**
     * releaseDevice
     */
    protected int releaseDevice() {
        int resultCode = mLib.releaseDevice();
        resultDialog(resultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_releaseDevice));
        return resultCode;
    }

    /**
     * setDeviceEnabled
     *
     * @param deviceEnabled
     */
    protected int setDeviceEnabled(boolean deviceEnabled) {
        int resultCode = mLib.setDeviceEnabled(deviceEnabled);
        resultDialog(resultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_setDevaiceEnabled));
        return resultCode;
    }

    /**
     * getFirmwareVer
     *
     * @param callback 非同期取得結果コールバック
     */
    protected int getFirmwareVer(FirmwareVerCallback callback) {
        int resultCode = mLib.getFirmwareVer(callback);
        resultDialog(resultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_getFirmwareVer));
        return resultCode;
    }

    /**
     * getBatteryLevel
     *
     * @param callback 非同期取得結果コールバック resultCodeが正常時、levelには下記値が入る
     */
    protected int getBatteryLevel(BatteryLevelCallback callback) {
        int resultCode = mLib.getBatteryLevel(callback);
        resultDialog(resultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_getBatteryLevel));
        return resultCode;
    }

    /**
     * リーダライタとの通信モードを取得します。
     * @return 通信モード
     */
    protected int getCommunicationMode(){
        return mLib.getCommunicationMode();
    }

    /**
     * デバイス接続 open → claimDevice → setDeviceEnabled → getFirmwareVer →
     * getBatteryLevel
     *
     * @param connectRequestAddress
     */
    private void deviceConnect(final String connectRequestAddress) {
        // 結果コード
        int result;
        // デバイスがクローズ状態の場合
        if (mLib.getState() == TecRfidSuite.OPOS_S_CLOSED) {
            // デバイスオープン
            if(mConnectionDevicename.equals(DEVICENAME_UF3000)){
                //UF-3000
                result = open(DEVICENAME_UF3000, MenuDeviceActivity.this, mSDKLogLevel, mSDKLogSize);
            }
            else{
                //UF-2200
                result = open(DEVICENAME, MenuDeviceActivity.this, mSDKLogLevel, mSDKLogSize);
            }
            // 失敗した場合
            if (TecRfidSuite.OPOS_SUCCESS != result){
                // エラー表示
                showDialog(getString(R.string.title_error), getString(R.string.message_processfailed_open), getString(R.string.btn_txt_ok), null);
            }
        } else {
            // 既にオープン済みなので、結果コードにSUCCESSを格納
            result = TecRfidSuite.OPOS_SUCCESS;
        }
        // openが成功したら
        if (TecRfidSuite.OPOS_SUCCESS == result) {
            showProgress();
            Runnable claimDeviceRunnable = new Runnable() {
                public void run() {
                    mConnectionRequestString = null;
                    int claimDeviceResult = claimDevice(connectRequestAddress, mConnectionEventCallback);
                    dismissProgress();
                    // claimDeviceが成功したら
                    if (TecRfidSuite.OPOS_SUCCESS == claimDeviceResult) {
                        viewUpdate();
                        // setDeviceEnabledが成功したら
                        if (TecRfidSuite.OPOS_SUCCESS == setDeviceEnabled(true)) {
                            if ( mLib.getIsAvailableScanner() == TecRfidSuite.ScannerDecision.INDEFINITE_SCANNER_STATE
                                    .getInt() || mLib.getIsAvailableTagReadMode() == TecRfidSuite.TagReadModeDecision.INDEFINITE_TAGREADMODE_STATE.getInt()) {
                                int ret =mLib.enableModelCheckProperty(new ResultCallback() {
                                    @Override
                                    public void onCallback(int resultCode, int resultCodeExtended) {
                                        if(resultCode == TecRfidSuite.OPOS_SUCCESS) {
                                            if (null != mViewHandler) {
                                                mViewRunnable = new Runnable() {
                                                    @Override
                                                    public void run() {
//                                                        deviceValidate();
                                                    }
                                                };
                                                mViewHandler.post(mViewRunnable);
                                            }
                                        }
                                        else {
                                            // エラー表示
                                            showDialog(getString(R.string.title_error),
                                                    getString(R.string.message_enableModelCheckProperty_error),
                                                    getString(R.string.btn_txt_ok), null);
                                        }
                                    }
                                });
                                if (ret != TecRfidSuite.OPOS_SUCCESS) {
                                    // エラー表示
                                    showDialog(getString(R.string.title_error),
                                            getString(R.string.message_enableModelCheckProperty_error),
                                            getString(R.string.btn_txt_ok), null);
                                }
                            }
                            mConnectedString = connectRequestAddress;
                            //USB接続の場合、デバイス名の欄に"USB"と表示する
                            if (TecRfidSuite.getInstance().getCommunicationMode() == TecRfidSuite.CommunicationMode.COMMUNICATION_MODE_USB.getInt()) {
                                mConnectedString = "USB";
                            }
                            // バッテリー残量を取得
                            getBatteryLevel(mBatteryLevelCallback);
                            // ファームウエアバージョンを取得
                            getFirmwareVer(mFirmwareVerCallback);
                            // 接続したMACアドレスをファイルへ保存
                            saveConnectedAddress(connectRequestAddress);
                            // 接続したデバイス名をファイルへ保存
                            saveConnectedDevicName(mConnectionDevicename);
                        }
                    } else {
                        close();
                        dismissProgress();
                    }
                }
            };
            // claimDeviceを別スレッドで呼ぶ
            Thread claimDeviceThread = new Thread(claimDeviceRunnable);
            claimDeviceThread.start();
        }
    }

    /**
     * デバイス切断
     */
    private void deviceDisConnect() {
        // setDeviceEnabledが行われていれば
        if (isDeviceEnabled()) {
            setDeviceEnabled(false);
        }
        // claimDeviceが行われていれば
        if (isClaimed()) {
            releaseDevice();
        }
        // openが行われていれば
        if (TecRfidSuite.OPOS_S_CLOSED != getState()) {
            close();
        }
        mConnectedString = null;
        mFWverString = null;
    }

    /**
     * 表示更新
     */
    private void viewUpdate() {
        if (null != mViewHandler) {
            mViewRunnable = new Runnable() {
                @Override
                public void run() {
                    mTvDevice.setText(mConnectedString);
                    mTvFmver.setText(mFWverString);
                    //deviceValidate();
                    int batteryLevel = mBatteryLevel;
                    // 取得したバッテリー残量で表示更新
                    updateButtery(batteryLevel);
                }
            };
            mViewHandler.post(mViewRunnable);
        }
    }

    /**
     * バッテリー残量を更新
     *
     * @param batteryLevel
     */
    private void updateButtery(int batteryLevel) {
        // 画像の横長を取得
        int x = mButteryView.getWidth();
        // 画像の縦長を取得
        int y = mButteryView.getHeight();
        // batteryLevelの比率分

        if (batteryLevel == 100) {
            // 100パーならfillParent
            x = -1;
        } else {
            Double dx = Double.valueOf(x);
            Double d100 = Double.valueOf(100);
            Double dbattLv = Double.valueOf(batteryLevel);
            Double danswer = (dx / d100) * dbattLv;
            x = danswer.intValue();
        }
        Object ofLayout = findViewById(R.id.remaining_buttery);
        FrameLayout fLayout = null;
        if (ofLayout instanceof FrameLayout) {
            fLayout = (FrameLayout) ofLayout;
        } else {
            fLayout = new FrameLayout(MenuDeviceActivity.this);
        }
        ViewGroup.LayoutParams resize_param = fLayout.getLayoutParams();
        resize_param.width = x;
        resize_param.height = y;

        mButteryText.setText(String.valueOf(batteryLevel) + PERCENT);
    }

    /**
     * 最後に接続したMACアドレスをファイルに保存
     *
     * @param connectRequestAddress
     */
    private void saveConnectedAddress(String connectRequestAddress) {
        String filePath = mStoragePath + SAVE_CONNECTED_PATH;
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
            filePath = filePath + SAVE_CONNECTED_FILENAME;
            File file = new File(filePath);
            // ファイルがあれば作りなおし
            if (file.exists()) {
                boolean fileDel = file.delete();
                if (!fileDel) {
                    // 特にエラーは出さない
                    return;
                }
            }
            fos = new FileOutputStream(filePath, true);
            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
            bw.write(connectRequestAddress);
            bw.flush();
            bw.close();
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

    /**
     * 最後に接続したMACアドレスがあればファイルから読込
     *
     * @return 最後に接続したMACアドレス
     */
    private String readConnectedAddressFile() {
        String filePath = mStoragePath + SAVE_CONNECTED_PATH + SAVE_CONNECTED_FILENAME;
        File file = new File(filePath);
        String address = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            address = br.readLine();
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
        return address;
    }

    /**
     * アプリ設定ファイルを読み込む
     *
     * @return 読み込んだ情報
     */
    private void readInitSettingFile() {
        if(mStoragePath == null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                mStoragePath = getApplicationContext().getExternalFilesDir(null) + "";
            } else {
                mStoragePath = Environment.getExternalStorageDirectory() + "";
            }
        }
        String filePath = mStoragePath + INITSETTING_PATH + INITSETTING_FILENAME;
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
                    if (line.indexOf(KEY_AUTOFREQUENCYLIST + COMMA) != -1) {
                        index = line.indexOf(KEY_AUTOFREQUENCYLIST + COMMA);
                        // 先頭カンマのインデックスを格納
                        index += (KEY_AUTOFREQUENCYLIST).length();
                        // ex.(,1,2,3)
                        line = line.substring(index);
                        int endIndex = 0;
                        String value = null;
                        while (true) {
                            // 終端からカンマを探す
                            index = line.indexOf(COMMA, endIndex);
                            if (index != -1) {
                                // カンマから次のカンマを探す
                                endIndex = line.indexOf(COMMA, index + 1);
                                if (endIndex != -1) {
                                    // カンマからカンマまでを取得
                                    value = line.substring(index + 1, endIndex);
                                } else {
                                    // カンマから終端までを取得
                                    value = line.substring(index + 1);
                                }
                                mAutoFrequencyList.add(Integer.parseInt(value));
                                if (-1 == endIndex) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    } else if (line.indexOf(KEY_SDKLOGLEVEL + COMMA) != -1) {
                        index = line.indexOf(KEY_SDKLOGLEVEL + COMMA);
                        index += (KEY_SDKLOGLEVEL + COMMA).length();
                        line = line.substring(index);
                        int i = Integer.parseInt(line);
                        if (i > 0) {
                            mSDKLogLevel = i;
                        } else {
                            mSDKLogLevel = TecRfidSuite.LOG_LEVEL_INFO;
                        }
                    } else if (line.indexOf(KEY_SDKLOGSIZE + COMMA) != -1) {
                        index = line.indexOf(KEY_SDKLOGSIZE + COMMA);
                        index += (KEY_SDKLOGSIZE + COMMA).length();
                        line = line.substring(index);
                        int i = Integer.parseInt(line);
                        if (i > 0) {
                            mSDKLogSize = i;
                        } else {
                            mSDKLogSize = DEFAULT_LOGSIZE;
                        }
                    } else if (line.indexOf(KEY_THREE_CHOICES_FLAG_AB + COMMA) != -1) {
                        index = line.indexOf(KEY_THREE_CHOICES_FLAG_AB + COMMA);
                        index += (KEY_THREE_CHOICES_FLAG_AB + COMMA).length();
                        line = line.substring(index);
                        int i = Integer.parseInt(line);
                        if (i == 1) {
                            isThreeChoicesFlagAB = true;
                        } else {
                            isThreeChoicesFlagAB = false;
                        }
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

    /**
     * アプリ設定ファイルがない、または不正ファイルであれば、デフォルト値で設定ファイルを作成
     *
     */
    public static synchronized void createInitSettingFIle(String storagePath) {
        String filePath = storagePath + INITSETTING_PATH;
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
            filePath = filePath + INITSETTING_FILENAME;
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
            if (null != mPowerType && 0 != mPowerType.length()) {
                if (mPowerType.equals(JP1)) {
                    writeDate = KEY_AUTOFREQUENCYLIST + COMMA
                            + TecRfidSuite.FrequencyLowChannelTypeCh26 + COMMA
                            + TecRfidSuite.FrequencyLowChannelTypeCh30 + COMMA
                            + TecRfidSuite.FrequencyLowChannelTypeCh28 + COMMA
                            + TecRfidSuite.FrequencyLowChannelTypeCh32 + COMMA
                            + TecRfidSuite.FrequencyLowChannelTypeCh17 + COMMA
                            + TecRfidSuite.FrequencyLowChannelTypeCh11 + COMMA
                            + TecRfidSuite.FrequencyLowChannelTypeCh23 + COMMA
                            + TecRfidSuite.FrequencyLowChannelTypeCh27 + COMMA
                            + TecRfidSuite.FrequencyLowChannelTypeCh29 + COMMA
                            + TecRfidSuite.FrequencyLowChannelTypeCh31 + COMMA
                            + TecRfidSuite.FrequencyLowChannelTypeCh05 + NEWLINE + KEY_SDKLOGLEVEL + COMMA + TecRfidSuite.LOG_LEVEL_INFO
                            + NEWLINE + KEY_SDKLOGSIZE + COMMA + DEFAULT_LOGSIZE;
                }
                else {
                    writeDate = KEY_AUTOFREQUENCYLIST + COMMA
                            + +TecRfidSuite.FrequencyLicenseChannelTypeCh17 + COMMA
                            + TecRfidSuite.FrequencyLicenseChannelTypeCh11 + COMMA
                            + TecRfidSuite.FrequencyLicenseChannelTypeCh23 + COMMA
                            + TecRfidSuite.FrequencyLicenseChannelTypeCh05 + NEWLINE + KEY_SDKLOGLEVEL + COMMA + TecRfidSuite.LOG_LEVEL_INFO
                            + NEWLINE + KEY_SDKLOGSIZE + COMMA + DEFAULT_LOGSIZE;
                }
            } else {
                createNewFileAfterGetFWVer = true;
                writeDate = KEY_AUTOFREQUENCYLIST + COMMA
                        + +TecRfidSuite.FrequencyLicenseChannelTypeCh17 + COMMA
                        + TecRfidSuite.FrequencyLicenseChannelTypeCh11 + COMMA
                        + TecRfidSuite.FrequencyLicenseChannelTypeCh23 + COMMA
                        + TecRfidSuite.FrequencyLicenseChannelTypeCh05 + NEWLINE + KEY_SDKLOGLEVEL + COMMA + TecRfidSuite.LOG_LEVEL_INFO
                        + NEWLINE + KEY_SDKLOGSIZE + COMMA + DEFAULT_LOGSIZE;

            }
            writeDate = writeDate + NEWLINE + KEY_THREE_CHOICES_FLAG_AB + COMMA + DEFAULT_THREE_CHOICES_FLAG_AB;
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

    /**
     * API実行結果ダイアログ
     *
     * @param resultCode API実行結果
     */
    protected void resultDialog(int resultCode, int resultCodeExtended, String isProcess) {
        if (TecRfidSuite.OPOS_SUCCESS == resultCode) {
            return;
        } else {
            String message = null;
            // resultCodeExtendedがあれば
            if (NOT_RESULTCODEEXTENDED != resultCodeExtended) {
                message = isProcess + NEWLINE + getString(R.string.message_error_code) + ("" + resultCode) + NEWLINE + getString(R.string.message_error_code_extended) + ("" + resultCodeExtended);
            } else {
                message = isProcess + NEWLINE + getString(R.string.message_error_code) + ("" + resultCode);
            }
            // エラーコードダイアログ
            showDialog(getString(R.string.title_error), message, getString(R.string.btn_txt_ok), null);
        }
    }

    /** アクセス中のプログレス表示 */
    protected void showProgress() {
        if(mProgressBar == null) {
            mProgressBar = new ProgressBar(this,null,android.R.attr.progressBarStyleLarge);
            //スクリーンサイズを取得する
            int width;
            int height;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
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
            //ルートビューにProgressBarを貼り付ける
            ViewGroup rootView = (ViewGroup)getWindow().getDecorView();
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            mProgressBar.setPadding(width*3/8,height*3/8,width*3/8,height*3/8);
            rootView.addView(mProgressBar,params);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        isShowProgress = true;
    }

    /** アクセス中のプログレス消去 */
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
    public void onBackPressed() {
        if (!isShowProgress) {
            super.onBackPressed();
        }
        else {
            //ProgressBarを消す
            dismissProgress();
        }
    }
//@Override
//public void onBackPressed(){
//        Intent intent = new Intent(this, MenuAppActivity.class);
//        startActivity(intent);
//        finish();
//        super.onBackPressed();
//    }

    /**
     * エラーダイアログ表示
     *
     * @param title 表示タイトル
     * @param message 表示メッセージ
     * @param btn1Txt ボタン1
     * @param btn2Txt ボタン2(不要ならnull)
     */
    protected void showDialog(final String title, final String message, final String btn1Txt, final String btn2Txt) {
        if (null != mShowDialogHandler) {
            mShowDialogRunnable = new Runnable() {
                @Override
                public void run() {
                    mDialog = new AlertDialog.Builder(MenuDeviceActivity.this);
                    mDialog.setTitle(title);
                    mDialog.setMessage(message);
                    mDialog.setPositiveButton(btn1Txt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // なにもしない
                        }
                    });
                    if (null != btn2Txt) {
                        mDialog.setNegativeButton(btn2Txt, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // なにもしない
                            }
                        });
                    }
                    mDialog.show();
                }
            };
            mShowDialogHandler.post(mShowDialogRunnable);
        }
    }

    /** ストレージ書き込みパーミッション用リクエストコード */
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    /**
     * パーミッションをチェックし、許可を促すダイアログを表示する
     */
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            perExternalStorage = false;
            tmpList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //前回権限リクエスト拒否しているか
            if (ActivityCompat.shouldShowRequestPermissionRationale(MenuDeviceActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                rejectExternalStorage = true;
            }
        }
        //位置情報権限チェック
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            perAccessLocation = false;
            tmpList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            //前回権限リクエスト拒否しているか
            if (ActivityCompat.shouldShowRequestPermissionRationale(MenuDeviceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                rejectAccessLocation = true;
            }
        } else { // 位置情報の権限がある場合
            // LocationManager取得
            LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // 位置情報サービス有効チェック
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                // メッセージダイアログ表示
                showDialog(null, getString(R.string.permission_AccessLocation_use), getString(R.string.btn_txt_ok), null);
            }
        }
        //onClick用
        final ArrayList<String> listPermissions = tmpList;

        //ストレージ書き込み、位置情報のどちらかの権限がない、または両方とも権限がない場合、リクエストを行う
        if (!perExternalStorage || !perAccessLocation) {

            //前回権限リクエスト拒否しているか
            if (rejectExternalStorage || rejectAccessLocation) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MenuDeviceActivity.this);
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
                        ActivityCompat.requestPermissions(MenuDeviceActivity.this, listPermissions.toArray(new String[listPermissions.size()]),
                                REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                    }
                });
                dialog.show();
            } else {
                //パーミッションのリクエスト
                ActivityCompat.requestPermissions(MenuDeviceActivity.this, listPermissions.toArray(new String[listPermissions.size()]),
                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION:
                for (int i = 0; i < permissions.length; i++) {
                    switch (permissions[i]) {
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                //設定ファイル読み込み
                                readInitSettingFile();
                                //許可成功
                                showDialog(null, getString(R.string.permission_success), getString(R.string.btn_txt_ok), null);
                            } else {
                                if (!ActivityCompat.shouldShowRequestPermissionRationale(MenuDeviceActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    //許可失敗
                                    showDialog(null, getString(R.string.permission_denied), getString(R.string.btn_txt_ok), null);
                                } else {
                                    //許可失敗
                                    showDialog(null, getString(R.string.permission_failed), getString(R.string.btn_txt_ok), null);
                                }

                            }
                            break;
                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                //許可成功
                                showDialog(null, getString(R.string.permission_AccessLocation_success), getString(R.string.btn_txt_ok), null);
                            } else {
                                if (!ActivityCompat.shouldShowRequestPermissionRationale(MenuDeviceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                    //許可失敗
                                    showDialog(null, getString(R.string.permission_AccessLocation_denied), getString(R.string.btn_txt_ok), null);
                                } else {
                                    //許可失敗
                                    showDialog(null, getString(R.string.permission_AccessLocation_failed), getString(R.string.btn_txt_ok), null);
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

    /**
     * 最後に接続したデバイス名をファイルに保存
     *
     * @param connectRequestAddress
     */
    private void saveConnectedDevicName(String connectRequestAddress) {
        String filePath = mStoragePath + SAVE_CONNECTED_DEVNAME_PATH;
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
            filePath = filePath + SAVE_CONNECTED_DEVNAME_FILENAME;
            File file = new File(filePath);
            // ファイルがあれば作りなおし
            if (file.exists()) {
                boolean fileDel = file.delete();
                if (!fileDel) {
                    // 特にエラーは出さない
                    return;
                }
            }
            fos = new FileOutputStream(filePath, true);
            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
            bw.write(connectRequestAddress);
            bw.flush();
            bw.close();
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

    /**
     * 最後に接続したMACアドレスがあればファイルから読込
     *
     * @return 最後に接続したMACアドレス
     */
    private String readConnectedDeviceNameFile() {
        String filePath = mStoragePath + SAVE_CONNECTED_DEVNAME_PATH + SAVE_CONNECTED_DEVNAME_FILENAME;
        File file = new File(filePath);
        String adrress = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            adrress = br.readLine();
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
        return adrress;
    }

    //////////////////////////////////////////////////////////////////
    //
    //アプリ起動直後はopenを実行していないので、USB接続検知ができない
    //または、パーミッション許可待ちの状態で、USB接続失敗→BLE接続となってしまう。
    //対策として、自前でBroadcastReceiverを登録してUSB検知を行う（苦肉の策）
    //
    //////////////////////////////////////////////////////////////////
    private volatile UsbManager mUsbManager;
    private boolean mUsbPermissionGranted;
    private volatile Context mContext = MenuDeviceActivity.this;
    private static final String ACTION_USB_PERMISSION = "CONNECT.USB_PERMISSION";
    private static final int USB_VENDER_ID  = 2214;                // Device VendorID :0x08A6
    private static final int USB_PRODUCT_ID = 100;                 // Device ProductID:0x0064
    private int mVenderId = USB_VENDER_ID;
    private int mProductId = USB_PRODUCT_ID;
    private volatile UsbDevice mDevice;
    //USB権限許可ダイアログ表示フラグ
    private boolean isUSBPermissonDlgShow = false;

    public synchronized void init(){

        // 通信バッファ初期化
        mUsbPermissionGranted = false;

        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        // パーミッションインテントの受信登録
        mContext.registerReceiver(mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        mContext.registerReceiver(mUsbReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED));

        // パーミッションインテント発行
        PendingIntent permissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice tDev = deviceIterator.next();
            if(tDev.getVendorId() == mVenderId && tDev.getProductId() == mProductId){
                mDevice = tDev;
                if(mUsbManager.hasPermission(mDevice)){
                    //フラグオンしてしまう
                    mUsbPermissionGranted = true;
                }
                else {
                    mUsbManager.requestPermission(mDevice, permissionIntent);
                    isUSBPermissonDlgShow = true;
                }
                break;
            }
        }
    }

    /**
     * インテント受信処理<br>
     * パーミッションのチェック結果、およびUSB接続、切断を検知します。
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        mUsbPermissionGranted = true;
                        //未接続の場合のみ接続する
                        if(getCommunicationMode() == TecRfidSuite.CommunicationMode.COMMUNICATION_MODE_NONE.getInt()) {
                            mContext.unregisterReceiver(mUsbReceiver);
                            isUSBPermissonDlgShow = false;
                            // デバイス接続
                            mConnectionDevicename = "UF-3000";
                            deviceConnect("USB");
                        }
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                // パーミッションインテント発行
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device.getVendorId() == mVenderId && device.getProductId() == mProductId) {
                    PendingIntent permissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    mUsbManager.requestPermission(device, permissionIntent);
                }
            }
        }
    };
}