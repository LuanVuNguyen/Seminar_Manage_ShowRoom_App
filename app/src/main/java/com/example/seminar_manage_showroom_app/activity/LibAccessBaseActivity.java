package com.example.seminar_manage_showroom_app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Insets;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ProgressBar;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.log.Log;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.toshibatec.TecRfidSuite;
import jp.co.toshibatec.callback.ConnectionEventHandler;

public class LibAccessBaseActivity extends Activity {

    /** ログ出力用 */
    public static final String START = "Start";
    public static final String END = "End";
    /** TecRfidSuiteライブラリ */
    private TecRfidSuite mLib = null;
    /** ライブラリアクセス中プログレス */
    private ProgressBar mProgressBar = null;
    /** プログレス表示フラグ */
    private boolean isShowProgress = false;
    /** ダイアログ */
    private AlertDialog.Builder mDialog = null;
    /** ダイアログ用ハンドラー */
    private Handler mShowDialogHandler = new Handler(Looper.getMainLooper());
    /** ダイアログ用ランナブル */
    private Runnable mShowDialogRunnable = null;
    /** プログレスディスミス用ハンドラー */
    private Handler mDissmissProgressHandler = new Handler(Looper.getMainLooper());
    /** プログレスディスミス用ランナブル */
    private Runnable mDissmissProgressRunnable = null;
    /** API実行結果 */
    private int mResultCode = 0;
    /** resultCodeExtendedがないとき設定 */
    protected static final int NOT_RESULTCODEEXTENDED = -1;
    /** 改行 */
    private static final String NEWLINE = "\n";
    /** 探索対象(EPC) */
    public static final String KEY_TARGET = "target";
    /** EPC指定検索か */
    public static final String KEY_SELECTED_EPC = "isSelectedEPC";
    /** 探索対象リスト */
    public static final String KEY_EPCLIST = "epclist";
    /** 除外対象リスト */
    public static final String KEY_EXCLUSIONLIST = "exclusionlist";
    /**　フィルタサイズ */
    public static final int SELECT_SIZE = 58;
    /** オフセットサイズ */
    public static final int OFFSET_SIZE = 32;
    /** 描画モード設定KEY  */
    public static final String SEARCH_RADAR_DRAW_MODE = "Config_Radar_DrawMode";
    /** FWモード設定KEY */
    public static final String SEARCH_FW_MODE = "Config_FWMode";
    public static final String SEARCH_PREFS = "serchprefs";
    /** ローディング表示中のActivity */
    private Activity mShowingActivity = null;
    /** ストレージパス */
    protected String mStoragePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            mStoragePath = getApplicationContext().getExternalFilesDir(null) + "";
        } else {
            mStoragePath = Environment.getExternalStorageDirectory() + "";
        }
        Log.setStoragePath(mStoragePath);
        Log.info(START);
        super.onCreate(savedInstanceState);
        // TecRfidSuiteライブラリインスタンスを取得
        mLib = TecRfidSuite.getInstance();
        Log.info(END);
    }

    @Override
    protected void onDestroy() {
        Log.info(START);
        dismissProgress();
        mShowDialogHandler.removeCallbacks(mShowDialogRunnable);
        mShowDialogHandler = null;
        mShowDialogRunnable = null;
        mDissmissProgressHandler.removeCallbacks(mDissmissProgressRunnable);
        mDissmissProgressHandler = null;
        mDissmissProgressRunnable = null;
        Log.info(END);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.info(START);
        if (!isShowProgress) {
            super.onBackPressed();
        }
        else {
            //ProgressBarを消す
            dismissProgress();
        }
        Log.info(END);
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
        mResultCode = mLib.open(deviceName);
        resultDialog(mResultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_open));
        // ログレベル
        hm.put(TecRfidSuite.OptionPackKeyLogLevel, logLevel);
        // ログサイズ
        hm.put(TecRfidSuite.OptionPackKeyLogFileSize, logSize);
        mLib.setOptions(hm);
        return mResultCode;
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
        mResultCode = mLib.open(deviceName, context);
        resultDialog(mResultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_open));
        // ログレベル
        hm.put(TecRfidSuite.OptionPackKeyLogLevel, logLevel);
        // ログサイズ
        hm.put(TecRfidSuite.OptionPackKeyLogFileSize, logSize);
        mLib.setOptions(hm);
        return mResultCode;
    }

    /**
     * open(再接続用)
     *
     * @param deviceName オープンするデバイス名
     */
    protected int openReconnect(String deviceName) {
        mResultCode = mLib.open(deviceName);
        return mResultCode;
    }

    /**
     * open(再接続用 UF-3000対応)
     *
     * @param deviceName オープンするデバイス名
     * @param context アプリのコンテキスト
     * @return
     */
    protected int openReconnect(String deviceName, Context context) {
        mResultCode = mLib.open(deviceName, context);
        return mResultCode;
    }

    /**
     * claimDevice
     *
     * @param connectionString UF-2200の場合は、BluetoothのMACアドレスを指定
     * @param callback 切断検知コールバック
     */
    protected int claimDevice(String connectionString, ConnectionEventHandler callback) {
        mResultCode = mLib.claimDevice(connectionString, callback);
        resultDialog(mResultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_claimDevice));
        return mResultCode;
    }

    /**
     * claimDevice(再接続用)
     *
     * @param connectionString UF-2200の場合は、BluetoothのMACアドレスを指定
     * @param callback 切断検知コールバック
     */
    protected int claimDeviceReconnect(String connectionString, ConnectionEventHandler callback) {
        mResultCode = mLib.claimDevice(connectionString, callback);
        return mResultCode;
    }

    /**
     * close
     */
    protected void close() {
        mResultCode = mLib.close();
        resultDialog(mResultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_close));
    }

    /**
     * close(再接続用)
     */
    protected void closeReconnect() {
        mResultCode = mLib.close();
    }

    /**
     * releaseDevice
     */
    protected int releaseDevice() {
        mResultCode = mLib.releaseDevice();
        resultDialog(mResultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_releaseDevice));
        return mResultCode;
    }

    /**
     * setDeviceEnabled
     *
     * @param deviceEnabled
     */
    protected int setDeviceEnabled(boolean deviceEnabled) {
        mResultCode = mLib.setDeviceEnabled(deviceEnabled);
        resultDialog(mResultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_setDevaiceEnabled));
        return mResultCode;
    }

    /**
     * setDeviceEnabled(再接続用)
     *
     * @param deviceEnabled
     */
    protected int setDeviceEnabledReconnect(boolean deviceEnabled) {
        mResultCode = mLib.setDeviceEnabled(deviceEnabled);
        return mResultCode;
    }

    /**
     * GetBluetoothList
     *
     * @param adressArray MACアドレスのリスト 空のアレイ領域を指定すると、String型の配列が返る
     */
    protected int getBluetoothList(ArrayList<String> adressArray) {
        mResultCode = mLib.getBluetoothList(adressArray);
        resultDialog(mResultCode, NOT_RESULTCODEEXTENDED, getString(R.string.message_processfailed_getBluetoothList));
        return mResultCode;
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
                message = isProcess + NEWLINE + getString(R.string.message_error_code) + ("" + resultCode) + NEWLINE
                        + getString(R.string.message_error_code_extended) + ("" + resultCodeExtended);
            } else {
                message = isProcess + NEWLINE + getString(R.string.message_error_code) + ("" + resultCode);
            }
            // エラーコードダイアログ
            showDialog(getString(R.string.title_error), message, getString(R.string.btn_txt_ok), null);
        }
    }

    /** アクセス中のプログレス表示 */
    protected void showProgress() {
        showProgressBar(this);
    }

    /** アクセス中のプログレス表示 */
    protected void showProgress(Context context) {
        showProgressBar(context);
    }

    /**
     * ローディングを表示する
     *
     * @param context コンテキスト
     */
    private void showProgressBar(Context context) {
        if(mProgressBar == null || mShowingActivity !=(Activity) context) {
            mProgressBar = new ProgressBar(context,null,android.R.attr.progressBarStyleLarge);
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
            mShowingActivity = (Activity) context;
            ViewGroup rootView = (ViewGroup)mShowingActivity.getWindow().getDecorView();
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            mProgressBar.setPadding(width*3/8,height*3/8,width*3/8,height*3/8);
            rootView.addView(mProgressBar,params);
        }
        mShowingActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        isShowProgress = true;
    }

    /** アクセス中のプログレス消去 */
    protected void dismissProgress() {
        mDissmissProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if(mShowingActivity !=null) {
                    mShowingActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
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

    /**
     * エラーダイアログ表示
     *
     * @param title 表示タイトル
     * @param message 表示メッセージ
     * @param btn1Txt ボタン1
     * @param btn2Txt ボタン2(不要ならnull)
     */
    protected void showDialog(final String title, final String message, final String btn1Txt, final String btn2Txt) {
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
    protected void showDialog(final String title, final String message, final String btn1Txt, final String btn2Txt,
            final Runnable positiveRun, final Runnable negativeRun) {
        if (null != mShowDialogHandler) {
            mShowDialogRunnable = new Runnable() {
                @Override
                public void run() {
                    mDialog = new AlertDialog.Builder(LibAccessBaseActivity.this);
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

    /**
     * リーダライタとの通信モードを取得します。
     * @return 通信モード
     */
    protected int getCommunicationMode(){
        return mLib.getCommunicationMode();
    }
}
