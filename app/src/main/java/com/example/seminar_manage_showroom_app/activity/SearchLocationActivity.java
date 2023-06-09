package com.example.seminar_manage_showroom_app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.common.interfaces.NotifyForActivityInterface;
import java.util.ArrayList;

import com.example.seminar_manage_showroom_app.log.Log;

import jp.co.explorationrfid.DeviceSensorListener;
import jp.co.explorationrfid.ExplorationRfid;
import jp.co.explorationrfid.PickRadarView;
import jp.co.explorationrfid.PositionMoveEventListener;
import jp.co.explorationrfid.RssiEventListener;
import jp.co.explorationrfid.callback.EPCDataEvent;
import jp.co.explorationrfid.callback.FinCallback;
import jp.co.explorationrfid.callback.SettingEndEvent;
import jp.co.explorationrfid.callback.TriggerOffEvent;
import jp.co.explorationrfid.callback.TriggerOnEvent;
import jp.co.toshibatec.TecRfidSuite;

public class SearchLocationActivity extends LibAccessBaseActivity implements View.OnClickListener, PositionMoveEventListener,
        DeviceSensorListener, RssiEventListener, NotifyForActivityInterface {
    private ImageView mSearchStartBtn = null;
    private ImageView mSearchStopBtn = null;
    private TextView mEpcText = null;
    private PickRadarView mPickRadarView = null;
    private SurfaceView mRadarSurfaceView = null;
    private ExplorationRfid explorationRfid;
    private TecRfidSuite mLib = null;
    private int handyPower = 19;
    private int findPower = 9;
    private float absoluteAngle = 0;
    private float power = 0;
    private boolean isStartSearch = false;
    private boolean startPositionMoveEvent = false;
    private String searchTarget = null;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isSelectedEPC = false;
    private ArrayList<String> mEpcCodeList = null;
    private ArrayList<String> mExclusionList = null;
    /*------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.info(START);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Intent intent = getIntent();
        if (intent != null) {
            isSelectedEPC = intent.getBooleanExtra(KEY_SELECTED_EPC,false);
            if(isSelectedEPC) {
                searchTarget = intent.getStringExtra(KEY_TARGET);
            }
            else {
                mEpcCodeList = intent.getStringArrayListExtra(KEY_EPCLIST);
                mExclusionList = intent.getStringArrayListExtra(KEY_EXCLUSIONLIST);
            }
        }

        search_location.setListener(this);

        Object omEpcText = findViewById(R.id.epc_text);
        if (omEpcText instanceof TextView) {
            mEpcText = (TextView) omEpcText;
        } else {
            mEpcText = new TextView(SearchLocationActivity.this);
        }
        mEpcText.setText(searchTarget);
        //mEpcText.setText("E28069950000500AC144D55A");

        Object omSearchStartBtn = findViewById(R.id.search_start_btn);
        if (omSearchStartBtn instanceof ImageView) {
            mSearchStartBtn = (ImageView) omSearchStartBtn;
        } else {
            mSearchStartBtn = new ImageView(SearchLocationActivity.this);
        }
        mSearchStartBtn.setOnClickListener(this);

        Object omSearchStopBtn = findViewById(R.id.search_stop_btn);
        if (omSearchStopBtn instanceof ImageView) {
            mSearchStopBtn = (ImageView) omSearchStopBtn;
        } else {
            mSearchStopBtn = new ImageView(SearchLocationActivity.this);
        }
        mSearchStopBtn.setOnClickListener(this);


        Object omRadarSurfaceView = findViewById(R.id.radar_surfaceView);
        if (omRadarSurfaceView instanceof SurfaceView) {
            mRadarSurfaceView = (SurfaceView) omRadarSurfaceView;
        }


        settingFixFinderLiblary();
        Log.info(END);
    }

    @Override
    public void onClick(View v) {
            Log.info(START);
            if (v.equals(mSearchStartBtn)) {
                if (!(search_location.getInstance()).getDisconnectFlag()) {
                    if (!isStartSearch) {
                        isStartSearch = true;
                    }
                    else {
                        return;
                    }
                    showProgress();
                    explorationRfid.setPositionMoveListener(this);
                    explorationRfid.setDeviceSensorListener(this);
                    explorationRfid.setRssiListener(this);

                    mPickRadarView.clearRadar();

                    int offset = OFFSET_SIZE;
                        if (isSelectedEPC) {

                            searchStartWithCallback(offset);
                        }
                        else {

                            int selectSize = SELECT_SIZE;
                            mEpcText.setText("");

                            searchStartWithReadingTagCallback(mEpcCodeList, offset, selectSize, mExclusionList);
                        }
                        mPickRadarView.startRadar();

                } else {
                    search_location.getInstance().deviceReConnect(this);
                }
            }
            else if (v.equals(mSearchStopBtn)) {
                if (!(search_location.getInstance()).getDisconnectFlag()) {
                    if (!isStartSearch) {
                        return;
                    }
                    showProgress();
                    mPickRadarView.stopRadar();
                    searchStopWithCallback();
                } else {
                    search_location.getInstance().deviceReConnect(this);
                }
            }

    }

    private void settingFixFinderLiblary() {

        mLib = TecRfidSuite.getInstance();
        explorationRfid = new ExplorationRfid(getApplicationContext());
        explorationRfid.logLevel = 2;
        explorationRfid.logEnableConsol = true;
        explorationRfid.logEnableFile = true;
        explorationRfid.setLog();
        explorationRfid.continuitySuccessCount = 1;
        explorationRfid.continuityEndSuccessCount = 3;
        explorationRfid.continuityLostCount = 3;
        explorationRfid.limitRiseIncrement = 3;
        explorationRfid.readTimerInterval = 100;
        explorationRfid.volume = 0.4f;


        String packageName = getPackageName();
        mPickRadarView = new PickRadarView(this, mRadarSurfaceView, packageName);


        String SoundFileName = "android.resource://" + getPackageName() + "/";
        ArrayList<Uri> list = new ArrayList<Uri>();
        list.add(0, Uri.parse(SoundFileName + R.raw.rssisound3));
        list.add(1, Uri.parse(SoundFileName + R.raw.rssisound2));
        list.add(2, Uri.parse(SoundFileName + R.raw.rssisound1));
        if (!explorationRfid.setSoundFile(list)) {
            showDialog(getString(R.string.title_error), "Exceeded maximum number of music files", getString(R.string.btn_txt_ok), null);
            return;
        }

        SharedPreferences prefs = getSharedPreferences(SEARCH_PREFS, Context.MODE_PRIVATE);
        int radarDrawMode =prefs.getInt(SEARCH_RADAR_DRAW_MODE, 1);
        if(radarDrawMode ==1) {
            mPickRadarView.radarDrawMode =true;
        }
        else {
            mPickRadarView.radarDrawMode =false;
        }
        explorationRfid.fwMode =prefs.getInt(SEARCH_FW_MODE, 1);
        Log.info("fwMode="+explorationRfid.fwMode);
    }

    private void searchStartWithCallback(int offset) {
        Log.info("searchStartWithCallback");
        int ret = explorationRfid.searchStartWithCallback(mLib, offset, searchTarget, handyPower, findPower, new FinCallback() {

            @Override
            public void finCallback(int resultCode, Error error) {
                if ((search_location.getInstance()).getDisconnectFlag()) {
                    return;
                }
                dismissProgress();
                if (resultCode != TecRfidSuite.OPOS_SUCCESS) {
                    isStartSearch = false;
                    showDialog(getString(R.string.title_error), error.getMessage(), getString(R.string.btn_txt_ok),
                            null);
                }
            }

        }, new TriggerOnEvent() {

            @Override
            public void triggerOnEvent() {
                Log.info("triggerOnEvent");
            }

        }, new TriggerOffEvent() {

            @Override
            public void triggerOffEvent() {
                Log.info("triggerOffEvent");
            }
        });
        if (ret != TecRfidSuite.OPOS_SUCCESS) {
            dismissProgress();
            isStartSearch = false;
            showDialog(getString(R.string.title_error), "searchStartWithCallback failed　ret = "+ret, getString(R.string.btn_txt_ok),
                    null);
        }
    }

    private void searchStartWithReadingTagCallback(ArrayList<String> partNumberList, int offset, int selectSize,
                                                   ArrayList<String> exclusionList) {
        Log.debug("searchStartWithReadingTagCallback");
        int ret = explorationRfid.searchStartWithReadingTagCallback(mLib, partNumberList, offset, selectSize,
                exclusionList, handyPower, findPower, new SettingEndEvent() {

                    @Override
                    public void settingEndEvent() {
                        Log.info("settingEndEvent");
                        dismissProgress();
                    }
                }, new EPCDataEvent() {

                    @Override
                    public void dataEvent(final String resultEpcCode) {
                        Log.info("dataEvent resultEpcCode="+resultEpcCode);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (resultEpcCode != null) {
                                    mEpcText.setText(resultEpcCode);
                                    Log.info("Identified EPC:" + resultEpcCode);
                                }
                            }
                        });
                    }
                }, new FinCallback() {

                    @Override
                    public void finCallback(int resultCode, Error error) {
                        Log.info("finCallback  resultCode= "+resultCode);
                        if (resultCode != TecRfidSuite.OPOS_SUCCESS) {
                            dismissProgress();
                            isStartSearch = false;
                            showDialog(getString(R.string.title_error), error.getMessage(),
                                    getString(R.string.btn_txt_ok), null);
                        }
                    }

                }, new TriggerOnEvent() {

                    @Override
                    public void triggerOnEvent() {
                        Log.info("triggerOnEvent");
                    }

                }, new TriggerOffEvent() {

                    @Override
                    public void triggerOffEvent() {
                        Log.info("triggerOffEvent");
                    }
                });
        Log.info("ret = " + ret);
        System.out.println(ret);
        if(ret!=TecRfidSuite.OPOS_SUCCESS) {
            startPositionMoveEvent = false;
            isStartSearch = false;
            dismissProgress();
            showDialog(getString(R.string.title_error), "searchStartWithReadingTagCallback failed　ret = "+ret, getString(R.string.btn_txt_ok),
                    null);
        }
    }

    private void searchStopWithCallback() {
        Log.info("searchStopWithCallback");
        int ret = explorationRfid.searchStopWithCallback(new FinCallback() {

            @Override
            public void finCallback(int resultCode, Error error) {
                startPositionMoveEvent = false;
                isStartSearch = false;
                dismissProgress();
            }
        });
        if (ret != TecRfidSuite.OPOS_SUCCESS) {
            showDialog(getString(R.string.title_error), "searchStopWithCallback failed　ret = "+ret, getString(R.string.btn_txt_ok),
                    null);
        }
    }

    @Override
    public void rssiEvent(int rssi) {
        Log.info("rssiEvent");
        if ((search_location.getInstance()).getDisconnectFlag()) {
            return;
        }
        mPickRadarView.drawRssi(rssi);
    }

    @Override
    public void deviceSensorEvent(float realAngle) {
        Log.info("deviceSensorEvent");
        if ((search_location.getInstance()).getDisconnectFlag()) {
            return;
        }
        if (startPositionMoveEvent) {
            mPickRadarView.drawRadar(absoluteAngle, power, realAngle);
        }
    }

    @Override
    public void positionMoveEvent(float absoluteAngle, float power, float realAngle) {
        Log.info("positionMoveEvent");
        if ((search_location.getInstance()).getDisconnectFlag()) {
            return;
        }
        this.absoluteAngle = absoluteAngle;
        this.power = power;
        if (!startPositionMoveEvent) {
            startPositionMoveEvent = true;
        }
        mPickRadarView.drawRadar(absoluteAngle, power, realAngle);
    }

    @Override
    public void disconnectDevice(String title, String message, String btn1) {
        Log.info("disconnectDevice");
        dismissProgress();
        showDialog(title, message, btn1, null);
    }

    @Override
    protected void onDestroy() {
        Log.info(START);
        explorationRfid = null;
        mLib = null;
        Log.info(END);
        super.onDestroy();
    }
}
