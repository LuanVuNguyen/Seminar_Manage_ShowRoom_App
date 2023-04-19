package com.example.seminar_manage_showroom_app.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seminar_manage_showroom_app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.co.toshibatec.TecRfidSuite;
import jp.co.toshibatec.callback.DataEventHandler;
import jp.co.toshibatec.callback.ErrorEventHandler;
import jp.co.toshibatec.callback.ResultCallback;
import jp.co.toshibatec.model.TagPack;

public class InventoryActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView btn_menu,btn_start,btn_stop,btn_clear,btn_exportfile;
    private Runnable mDissmissProgressRunnable = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        init();
    }
    private void init()
    {
        btn_menu = (ImageView) findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(this);

        btn_start = (ImageView) findViewById(R.id.btn_startscan);
        btn_start.setOnClickListener(this);

        btn_stop = (ImageView) findViewById(R.id.btn_stopscan);
        btn_stop.setOnClickListener(this);

        btn_clear = (ImageView) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);

        btn_exportfile = (ImageView) findViewById(R.id.btn_exportcsv);
        btn_exportfile.setOnClickListener(this);
    }

    private void unvisible()
    {
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_clear.getVisibility()==View.GONE){
                    btn_menu.setImageResource(R.drawable.btn_close);
                    btn_clear.setVisibility(View.VISIBLE);
                    btn_start.setVisibility(View.VISIBLE);
                    btn_stop.setVisibility(View.VISIBLE);
                    btn_exportfile.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_menu.setImageResource(R.drawable.btn_menu);
                    btn_clear.setVisibility(View.GONE);
                    btn_start.setVisibility(View.GONE);
                    btn_stop.setVisibility(View.GONE);
                    btn_exportfile.setVisibility(View.GONE);
                }
            }
        });
    }
    private void showToast(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(InventoryActivity.this,s+"",Toast.LENGTH_LONG).show();
            }
        });
    }
    private boolean mIsStartReadTags = false;
    private static final TecRfidSuite mLib = TecRfidSuite.getInstance();
    private String mFilterID = "00000000";
    /** 読み取るタグを決定する際に論理積を行うためのビットパターン */
    private String mFiltermask = "00000000";
    /** timeout */
    private int mStartReadTagsTimeout = 10000;

    private void startReadtag(){
        if (!mIsStartReadTags){
            btn_start.setImageResource(R.drawable.btn_play_bur);
            btn_stop.setImageResource(R.drawable.btn_top);
            showToast("Start scan!!!");
            mIsStartReadTags = true;
            if (TecRfidSuite.OPOS_SUCCESS != mLib.startReadTags(mFilterID, mFiltermask, mStartReadTagsTimeout, mDataEvent, mErrorEvent)){
                // エラー表示
                //showDialog(getString(R.string.title_error), getString(R.string.message_processfailed_stopReadTags), getString(R.string.btn_txt_ok), null);
            }
            // setDataEventEnabledを失敗した場合
            if (TecRfidSuite.OPOS_SUCCESS != mLib.setDataEventEnabled(true)){
                // エラー表示
                //showDialog(getString(R.string.title_error), getString(R.string.message_processfailed_setDataEventEnabled), getString(R.string.btn_txt_ok), null);
            }
        }
    }
    private void stopReadtag()
    {
        if (mIsStartReadTags)
        {
            btn_start.setImageResource(R.drawable.btn_play);
            btn_stop.setImageResource(R.drawable.btn_top_bur);
            showToast("Stop scan!!!");
            // stopReadTagsを成功した場合
            if (TecRfidSuite.OPOS_SUCCESS == mLib.stopReadTags(mStopReadTagsResultCallback)) {
                // プログレスバーを表示
            } else{
                // エラー表示
                //showDialog(getString(R.string.title_error), getString(R.string.message_processfailed_stopReadTags), getString(R.string.btn_txt_ok), null);
            }
        }
    }

    private static final int CARRIERSENSEERROR = 19;
    /** 電波出力禁止エラー */
    private static final int WAVEOUTPUTBLOCKERROR = 21;
    /** タグデータバッファフルエラー */
    private static final int TAGDATAFULLBUFFERERROR = 65;
    private ArrayList<String> mShowReadData = new ArrayList<String>();
    private Boolean Check=true;
    private void soundBeep() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
        toneGenerator.release();
    }
    private class UpdateReadTagDataTask extends AsyncTask<String, String, Long> {

        @Override
        protected void onPostExecute(Long result) {
            // setDataEventEnabledを失敗した場合
            if (TecRfidSuite.OPOS_SUCCESS != mLib.setDataEventEnabled(true)){
                // エラー表示
                // showDialog(getString(R.string.title_error), getString(R.string.message_processfailed_setDataEventEnabled), getString(R.string.btn_txt_ok), null);
                System.out.println("LUAN: "+result);
            }
            super.onPostExecute(result);
        }
        @Override
        protected void onProgressUpdate(String... values) {
            // 追加・更新しない
            int size = mShowReadData.size();
            // リストビューに追加する
            for (int i = 0; i < values.length; i++) {
                if(null != values[i] && 0 != values[i].length()){
                    if (Check.equals(true)) {
                        if (mShowReadData.indexOf(values[i]) == -1) {
                            size = mShowReadData.size();
                            // アダプターへデータを追加
                            mShowReadData.add(values[i]);
                        }
                    }
                    else {
                        size = mShowReadData.size();
                        //System.out.println("Tommycheckvalues3: "+values[i]);
                        // アダプターへデータを追加
                        mShowReadData.add(values[i]);
                    }
                }
            }
            // アダプターを更新
        }

        @SuppressLint("WrongThread")
        @Override
        protected Long doInBackground(String... params) {

//            ArrayList<String> a = new ArrayList<String>();
//            // 重複排除にチェックがはいっていれば
//            if (Check.equals(true)) {
//                // 新しい読取タグデータ分ループ
//                for (int i = 0; i < mReadData.size(); i++) {
//                    // 重複していなければ
//                    if (-1 == mShowReadData.indexOf(mReadData.get(i))) {
//                        System.out.println("Tommycheckvalues1: "+mReadData.get(i));
//                        //jsonArraytoshiba.put(mReadData.get(i).toUpperCase());
//                        //System.out.println(jsonArraytoshiba);
//                        if (jsonArraytoshiba.length() != 0) {
//                            new HttpPostRfid(ScanDataActivity.this).execute(Config.CODE_LOGIN,Config.HTTP_SERVER_SHOP+Config.API_ODOO_GETMULTIPLEPRODUCT, jsonArraytoshiba.toString());
//
//                        }
//
//                        a.add(mReadData.get(i));
//                        // 50個追加された場合
//                        if (a.size() >= 50) {
//                            // 表示更新
//                            publishProgress(a.toArray(new String[a.size()]));
//                            a.clear();
//                            // ビープ音鳴音
//                            soundBeep();
//                        }
//                    }
//                }
//            }
//            else {
//                // 新しい読取タグデータ分ループ
//                for (int i = 0; i < mReadData.size(); i++) {
//                    a.add(mReadData.get(i));
//                    // 50個追加された場合
//                    if (a.size() >= 50) {
//                        // 表示更新
//                        publishProgress(a.toArray(new String[a.size()]));
//                        a.clear();
//                        // ビープ音鳴音
//                        soundBeep();
//                    }
//                }
//            }
//            // 50個未満で表示更新が済んでないタグ情報がある場合
//            if(!a.isEmpty()){
//                // 表示更新
//                publishProgress(a.toArray(new String[a.size()]));
//                a.clear();
//                // ビープ音鳴音
//                soundBeep();
//            }
//
//            // 読取分のが表示更新が済んだので、クリア
//            mReadData.clear();
            return null;
        }
    }
    private ErrorEventHandler mErrorEvent = new ErrorEventHandler() {
        @Override
        public void onEvent(int resultCode, int resultCodeExtended) {
            // startReadTagsが失敗した場合
            if (TecRfidSuite.OPOS_SUCCESS != resultCode){
                if (resultCodeExtended != CARRIERSENSEERROR && resultCodeExtended != WAVEOUTPUTBLOCKERROR && resultCodeExtended != TAGDATAFULLBUFFERERROR) {
                    // エラー表示
                    //showDialog(getString(R.string.title_error), getString(R.string.message_processfailed_startReadTags), getString(R.string.btn_txt_ok), null);
                }
            }
        }
    };
    private boolean isReadBackPress = false;
    private ResultCallback mStopReadTagsResultCallback = new ResultCallback() {
        @Override
        public void onCallback(int resultCode, int resultCodeExtended) {
            // stopReadTagsが失敗した場合
            if (TecRfidSuite.OPOS_SUCCESS != resultCode){
                // エラー表示
                //showDialog(getString(R.string.title_error), getString(R.string.message_processfailed_stopReadTags), getString(R.string.btn_txt_ok), null);
            }
            // プログレスバーを消去
            dismissProgress();
            mIsStartReadTags = false;
            // buttonValid();
            // 読取テスト中にバックキーが押下された場合
            if(isReadBackPress){
                isReadBackPress = false;
                finish();
            }
        }
    };


    private UpdateReadTagDataTask mUpdateReadTagDataTask = null;
    private String tommycheckkey="";
    private ArrayList<String> mReadData = new ArrayList<String>();
    private DataEventHandler mDataEvent = new DataEventHandler() {
        @Override
        public void onEvent(HashMap<String, TagPack> tagList) {
            for (Map.Entry<String, TagPack> e : tagList.entrySet()) {
                // 受信データからタグ情報を取得
                String key = e.getKey();
                // 追加


                mReadData.add(key);
                tommycheckkey=tommycheckkey+key;
                System.out.println("Vuluan"+tommycheckkey);


            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
//                mCustomUpdateReadTagDataTask = new CustomUpdateReadTagDataTask();
//                mCustomUpdateReadTagDataTask.execute("");
            } else {
                mUpdateReadTagDataTask = new UpdateReadTagDataTask();
                mUpdateReadTagDataTask.execute("");
            }

        }
    };
    private Handler mDissmissProgressHandler = new Handler(Looper.getMainLooper());
    private boolean isShowProgress = false;
    /**
     * ライブラリアクセス中プログレス
     */
    private ProgressBar mProgressBar = null;
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
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_menu:
            {
                unvisible();
                break;
            }
            case R.id.btn_startscan:
            {
                startReadtag();
                break;
            }
            case R.id.btn_stopscan:
            {
                stopReadtag();
                break;
            }
        }
    }
}
