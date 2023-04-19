
package com.example.seminar_manage_showroom_app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Insets;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.seminar_manage_showroom_app.R;
import com.example.seminar_manage_showroom_app.adapter.MisreadingPreventionSettingsTable;
import com.example.seminar_manage_showroom_app.common.CustomAsyncTask;
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
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import jp.co.toshibatec.TecRfidSuite;
import jp.co.toshibatec.callback.FlagABCallback;
import jp.co.toshibatec.callback.FrequencyCallback;
import jp.co.toshibatec.callback.MisreadingPreventionSettingsCallback;
import jp.co.toshibatec.callback.PowerCallback;
import jp.co.toshibatec.callback.QValueCallback;
import jp.co.toshibatec.callback.ResultCallback;
import jp.co.toshibatec.callback.SavingEnergyCallback;
import jp.co.toshibatec.callback.TagReadModeCallback;
import jp.co.toshibatec.callback.TriggerSwModeCallback;


public class SettingActivity extends Activity implements View.OnClickListener, NotifyForActivityInterface {
	/** 開くボタン */
	private ImageView mOpenBtn = null;
	/** 保存ボタン */
	private ImageView mSaveBtn = null;
	/** 設定読込ボタン */
	private ImageView mGetSettingsBtn = null;
	/** 設定送信ボタン */
	private ImageView mSendSettingsBtn = null;
	/** クリアボタン */
	private ImageView mResetBtn = null;

    /** 待機用セマフォ */
	private Semaphore mSemaphore;
	/** 設定読込用タスク */
	private ReadSettingDataTask mReadSettingDataTask = null;
	/** 設定書込み用タスク */
	private WriteSettingDataTask mWriteSettingDataTask = null;
    /** 設定読込用タスク Android11対応 */
    private CustomReadSettingDataTask mCustomReadSettingDataTask = null;
    /** 設定書込み用タスク Android11対応 */
    private CustomWriteSettingDataTask mCustomWriteSettingDataTask = null;
    /** 表示更新用ハンドラー */
    private Handler mViewHandler = new Handler(Looper.getMainLooper());
    /** 表示更新用ランナブル */
    private Runnable mViewRunnable = null;
    /** ライブラリアクセス中プログレス */
    private ProgressBar mProgressBar = null;
    /** プログレス表示フラグ */
    private boolean isShowProgress = false;
    /** プログレスディスミス用ハンドラー */
    private Handler mDissmissProgressHandler = new Handler(Looper.getMainLooper());
    /** プログレスディスミス用ランナブル */
    private Runnable mDissmissProgressRunnable = null;
    /** ダイアログ */
    private AlertDialog.Builder mDialog = null;
    /** ダイアログ用ハンドラー */
    private Handler mShowDialogHandler = new Handler(Looper.getMainLooper());
    /** ダイアログ用ランナブル */
    private Runnable mShowDialogRunnable = null;

    /** JP1 or JP2 or JP3 or JP4 or JP5 or JP6 */
    private String mPowerType = "";
    /** JP1(特小) */
    public static final String JP1 = "JP1";
    public static final String JP2 = "JP2";
    public static final String JP3 = "JP3";
    public static final String JP4 = "JP4";
    public static final String JP5 = "JP5";
    public static final String JP6 = "JP6";
    /** ファイルから読み込んで受け取ったAutoFrequencyList */
    private ArrayList<Integer> mRecieveAutoFrequencyList = null;
    /** 設定の読込、送信に失敗したかのフラグ */
    private boolean mSettingsFalied = false;

    /** 送信出力 */
	private int sendPowerLevel;
    /** 送信Q値 */
	private int sendQvalue;
    /** 送信周波数 */
	private int sendFrequencyChannel;
    /** 送信トリガモード */
	private int sendTrigMode;
    /** 送信用省電力設定 */
	private int sendEnergy;
    /** 送信用ミラー副搬送波設定*/
	private int sendTagReadMillerType;
    /** 送信用タグ速度設定 */
	private int sendTagReadSpeedType;
    /** 送信用フラグAB設定 */
	private int sendFlagAB;
    /** 送信用読取識別番号 */
	private int sendMisreadingPreventionSettings;
	/** 出力リスト */
	private TextView mTextPower = null;
	/** Q値リスト */
	private TextView mTextQValue = null;
	/** 周波数リスト */
	private TextView mTextFrequency = null;
	/** トリガースイッチリスト */
	private TextView mTextTriggerSw = null;
	/** 省電力モードリスト */
	private TextView mTextEnergySaving = null;
	/** 読取モードリスト */
	private TextView mTextTagReadMode = null;
	/** フラグABリスト */
	private TextView mTextFlagAB = null;
	/** 読取識別番号リスト */
	private TextView mTextMisreadingPreventionSettings = null;
	/** power */
	private final int POWER = 0;
	/** QValue */
	private final int QVALUE = 1;
	/** frequency */
	private final int FREQUENCY = 2;
	/** triggerSW */
	private final int TRIGGERSW = 3;
	/** energySaving */
	private final int ENERGYSAVING = 4;
	/** tagReadMode */
	private final int READMODE = 5;
	/** FlagAB */
	private final int FLAG_AB = 6;
	/** MisreadingPreventionSettings */
	private final int MISREADING_PPREVENTIO_SETTINGS = 7;

    /** 設定ファイル保存ディレクトリパス */
    private final String SAVE_SETTING_PATH = "/TEC/tool/setting/";
    /** 出力ダイアログ */
    private final int DIALOG_POWER = 1;
    /** powerタイトル */
    private String TITLE_POWER = "出力";
    /** 　powerリスト用ArrayList　 */
    private ArrayList<String> mPowerList = new ArrayList<String>();
    /** POWER用定数 */
    private final String POWER_0 = "0 : 1mW";
    /** POWER用定数 */
    private final String POWER_1 = "1 : 1.3mW";
    /** POWER用定数 */
    private final String POWER_2 = "2 : 1.7mW";
    /** POWER用定数 */
    private final String POWER_3 = "3 : 2.1mW";
    /** POWER用定数 */
    private final String POWER_4 = "4 : 2.8mW";
    /** POWER用定数 */
    private final String POWER_5 = "5 : 3.6mW";
    /** POWER用定数 */
    private final String POWER_6 = "6 : 4.6mW";
    /** POWER用定数 */
    private final String POWER_7 = "7 : 5.9mW";
    /** POWER用定数 */
    private final String POWER_8 = "8 : 7.6mW";
    /** POWER用定数 */
    private final String POWER_9 = "9 : 9.8mW";
    /** POWER用定数 */
    private final String POWER_10 = "10 : 13mW";
    /** POWER用定数 */
    private final String POWER_11 = "11 : 16mW";
    /** POWER用定数 */
    private final String POWER_12 = "12 : 21mW";
    /** POWER用定数 */
    private final String POWER_13 = "13 : 27mW";
    /** POWER用定数 */
    private final String POWER_14 = "14 : 35mW";
    /** POWER用定数 */
    private final String POWER_15 = "15 : 45mW";
    /** POWER用定数 */
    private final String POWER_16 = "16 : 58mW";
    /** POWER用定数 */
    private final String POWER_17 = "17 : 75mW";
    /** POWER用定数 */
    private final String POWER_18 = "18 : 97mW";
    /** POWER用定数 */
    private final String POWER_19 = "19 : 125mW";
    /** POWER用定数 */
    private final String POWER_20 = "20 : 152mW";
    /** POWER用定数 */
    private final String POWER_21 = "21 : 186mW";
    /** POWER用定数 */
    private final String POWER_22 = "22 : 226mW";
    /** POWER用定数 */
    private final String POWER_23 = "23 : 276mW";
    /** POWER用定数 */
    private final String POWER_24 = "24 : 336mW";
    /** POWER用定数 */
    private final String POWER_25 = "25 : 410mW";
    /** POWER用定数 */
    private final String POWER_26 = "26 : 500mW";

    /** Q値ダイアログ */
    private final int DIALOG_QVALUE = 2;
    /** QValueタイトル */
    private String TITLE_QVALUE = "Q-VALUE";
    /** 　QValueリスト用ArrayList　 */
    private ArrayList<String> mQvalueList = new ArrayList<String>();
    /** QVALUE用定数 */
    private final String QVALUE_0 = "0 : 1tag";
    /** QVALUE用定数 */
    private final String QVALUE_1 = "1 : 1-4tags";
    /** QVALUE用定数 */
    private final String QVALUE_2 = "2 : 1-8tags";
    /** QVALUE用定数 */
    private final String QVALUE_3 = "3 : 1-16tags";
    /** QVALUE用定数 */
    private final String QVALUE_4 = "4 : 1-32tags";
    /** QVALUE用定数 */
    private final String QVALUE_5 = "5 : 1-64tags";
    /** QVALUE用定数 */
    private final String QVALUE_6 = "6 : 1-128tags";
    /** QVALUE用定数 */
    private final String QVALUE_7 = "7 : 1-256tags";
    /** QVALUE用定数 */
    private final String QVALUE_8 = "8 : 1-512tags";
    /** QVALUE用定数 */
    private final String QVALUE_9 = "9 : 1-1024tags";
    /** QVALUE用定数 */
    private final String QVALUE_10 = "10 : 1-2048tags";

    /** 周波数ダイアログ */
    private final int DIALOG_FREQUENCY = 3;
    /** frequencyタイトル */
    private String TITLE_FREQUENCY = "frequency";
    /** 　frequencyリスト用ArrayList　 */
    private ArrayList<String> mFrequencyList = new ArrayList<String>();
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_AUTO = "Auto";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_5CH = "5ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_11CH = "11ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_17CH = "17ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_23CH = "23ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_24CH = "24ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_25CH = "25ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_26CH = "26ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_27CH = "27ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_28CH = "28ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_29CH = "29ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_30CH = "30ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_31CH = "31ch";
    /** FREQUENCY用定数 */
    public static final String FREQUENCY_32CH = "32ch";

    /** trigger switch dialog */
    private final int DIALOG_TRIGGERSW = 4;
    /** triggerSW title */
    private String TITLE_TRIGGERSW = "trigger switch";
    /** ArrayList for triggerSW list */
    private ArrayList<String> mTriggerswList = new ArrayList<String>();
    /** Constant for TRIGGERSW */
    private final String TRIGGERSW_REALTIME = "ON while pressed";
    /** Constant for TRIGGERSW */
    private final String TRIGGERSW_HOLD = "Hold mode";
    /** Constant for TRIGGERSW */
    private final String TRIGGERSW_NONE = "Trigger switch disabled";

    /** sleep transition time dialog */
    private final int DIALOG_SLEEPTIME = 5;
    /** sleeptime title */
    private String TITLE_ENERGY = "Power saving sleep";
    /** ArrayList for sleeptime list */
    private ArrayList<String> mEnergyList = new ArrayList<String>();
    /** SLEEPTIME用定数 */
    private final String ENERGY_OFF = "no power sleep";
    /** SLEEPTIME用定数 */
    private final String ENERGY_ON = "Sleep after 3 seconds of radio wave stop";

    /** フラグAB三選択肢表示有無 */
    private boolean isThreeChoicesFlagAB = false;
    /** フラグABダイアログ */
    private final int DIALOG_FLAG_AB = 9;
    /** フラグABタイトル */
    private String TITLE_FLAG_AB = "フラグAB";
    /** フラグABリスト用ArrayList */
    private ArrayList<String> mFlagABList = new ArrayList<String>();
    /** フラグAB用定数 Aのみ */
    private final String VALUE_FLAG_A = "Flag AB";
    /** フラグAB用定数 Bのみ*/
    private final String VALUE_FLAG_B = "B only";
    /** フラグAB用定数 A、B両方*/
    private final String VALUE_FLAG_AB = "Both A and B";
    /** フラグAB設定書込み用 */
    private final String KEY_FLAG_AB = "FlagAB";
    /** 読取識別番号書込み用 */
    private final String KEY_MISREADINGPREVENTIONSETTINGS = "MisreadingPreventionSettings";

    /** 読取モードリスト用ArrayList　 */
    private ArrayList<String> mTagReadModeList = new ArrayList<String>();
    /** 読取モード設定 */
    private int mTagReadMode = TagReadModeType.MILLER4_MEDIUM_SPEED_TYPE.getInt();
    /** 読取モードダイアログ */
    private final int DIALOG_TAG_READ_MODE = 8;
    /** 読取モードタイトル */
    private String TITLE_TAG_READ_MODE = "reading mode";

    /** ストレージパス保存用 */
    private String mStoragePath;

    /** タグ読取モードタイプ */
    public enum TagReadModeType {
        /** Miller2　中速 */
        MILLER2_MEDIUM_SPEED_TYPE("Miller2:Medium speed",0),
        /** Miller2 high speed */
        MILLER2_HIGH_SPEED_TYPE("Miller2:High Speed",1),
        /** Miller4 medium speed */
        MILLER4_MEDIUM_SPEED_TYPE("Miller4:Medium speed",2),
        /** Miller4 high speed */
        MILLER4_HIGH_SPEED_TYPE("Miller4:High Speed",3),
        /** Miller8 medium speed */
        MILLER8_MEDIUM_SPEED_TYPE("Miller8:Medium speed",4),
        /** Miller8 high speed */
        MILLER8_HIGH_SPEED_TYPE("Miller8:High Speed",5);
        private final String text;
        private final int id;
        private TagReadModeType(final String text,final int id) {
            this.text = text;
            this.id = id;
        }

        /** Get tag reading mode type display
         * @return String value */
        public String getString() {
            return this. text;
        }

        /** Get tag read mode type value
         * @return int value */
        public int getInt() {
            return this. id;
        }
    }

    /** Tag reading mode type (version with misreading prevention function) */
    public enum TagReadModeTypeTruncate {
        /** FM0 */
        FM0_SPEED_TYPE("FM0",0),
        /** FM0 medium speed */
        FM0_MEDIUM_SPEED_TYPE("FM0:Medium speed",1),
        /** Miller2 slow speed */
        MILLER2_LOW_SPEED_TYPE("Miller2:Low Speed",2),
        /** Miller2 high speed */
        MILLER2_HIGH_SPEED_TYPE("Miller2:High Speed",3),
        /** Miller4 low speed */
        MILLER4_LOW_SPEED_TYPE("Miller4:Low Speed",4),
        /** Miller4 high speed */
        MILLER4_HIGH_SPEED_TYPE("Miller4:High Speed",5),
        /** Miller8 low speed */
        MILLER8_LOW_SPEED_TYPE("Miller8:Low Speed",6),
        /** Miller8 high speed */
        MILLER8_HIGH_SPEED_TYPE("Miller8:High Speed",7);

        private final String text;
        private final int id;
        TagReadModeTypeTruncate(final String text, final int id) {
            this. text = text;
            this. id = id;
        }
        /** Get tag reading mode type display
         * @return String value */
        public String getString() {
            return this. text;
        }

        /** Get tag read mode type value
         * @return int value */
        public int getInt() {
            return this. id;
        }
    }
    /** ArrayList for reading identification number list */
    private ArrayList<String> mMisreadingPreventionSettingsList = new ArrayList<String>();
    /** Read identification number dialog */
    private final int DIALOG_MISREADING_PREVENTINON_SETTIGS = 10;
    /** Reading identification number title */
    private String TITLE_MISREADING_PREVENTINON_SETTIGS = "Reading 1 number";

    /** Selected dialog type */
    private int mDialogMode = 0;
    /** Selected dialog item */
    private int mSelectItem = 0;

    /** For writing text */
    private final String COMMA = ",";
    /** For writing text */
    private final String NEWLINE = "\n";
    /** For writing text */
    private final String KEY_POWER = "Power";
    /** For writing text */
    private final String KEY_VALUEQ = "ValueQ";
    /** For writing text */
    private final String KEY_FREQUENCY = "Frequency";
    /** For writing text */
    private final String KEY_TRIGGER = "Trigger";
    /** For writing text */
    private final String KEY_ENERGYSAVING = "EnergySaving";
    /** for reading mode text writing */
    private final String KEY_TAGREADMODE = "TagReadMode";
    /** device type */
    public String mDeviceType = null;
    /** UF-3000 device type */
    public static final String DEVICE_TYPE_UF3000 = "UF-3000";
/**
 * argument for getPower
 */
    /** Transmission output level acquisition callback */
    private PowerCallback mPowerCallback = new PowerCallback() {
        @Override
        public void onCallback(int powerLevel, int resultCode, int resultCodeExtended) {
            mSemaphore.release();
            if (TecRfidSuite.OPOS_SUCCESS == resultCode) {
                if (mPowerType.equals(JP1) && powerLevel>TecRfidSuite.LowRangePower125mW) {
                    mPowerLevel = TecRfidSuite.LowRangePower125mW;
                } else if (!mPowerType.equals(JP1) && powerLevel>TecRfidSuite.HighRangePower500mW) {
                    mPowerLevel = TecRfidSuite.HighRangePower500mW;
                } else {
                    mPowerLevel = powerLevel;
                }
                viewUpdate(POWER);
            } else {
                mSettingsFalied = true;
            }
        }
    };

/**
 * argument for setPower
 */
    /** Transmit power level */
    private int mPowerLevel = 0;
    /** Callback for communication execution result */
    private ResultCallback mSetPowerResultCallback = new ResultCallback() {
        @Override
        public void onCallback(int resultCode, int resultCodeExtended) {
            mSemaphore.release();
            if (TecRfidSuite.OPOS_SUCCESS != resultCode) {
                mSettingsFalied = true;
            }
        }
    };

/**
 * Argument for getQValue
 */
    /** Callback for communication execution result */
    private QValueCallback mQValueCallback = new QValueCallback() {
        @Override
        public void onCallback(int qValue, int resultCode, int resultCodeExtended) {
            mSemaphore.release();
            if (TecRfidSuite.OPOS_SUCCESS == resultCode) {
                mQValue = qValue;
                viewUpdate(QVALUE);
            } else {
                mSettingsFalied = true;
            }
        }
    };

/**
 * Argument for setQValue
 */
    /** Q-value */
    private int mQValue = 0;
    /** Callback for communication execution result */
    private ResultCallback mSetQValueResultCallback = new ResultCallback() {
        @Override
        public void onCallback(int resultCode, int resultCodeExtended) {
            mSemaphore.release();
            if (TecRfidSuite.OPOS_SUCCESS != resultCode) {
                mSettingsFalied = true;
            }
        }
    };

	/**
	 * getFrequency用引数
	 */
	/** 非同期取得結果コールバック */
    private FrequencyCallback mFrequencyCallback = new FrequencyCallback() {
        @Override
        public void onCallback(int frequencyChannel, ArrayList<Integer> autoFrequencyList, int resultCode,
                               int resultCodeExtended) {
            mSemaphore.release();
            if (TecRfidSuite.OPOS_SUCCESS == resultCode) {
                if (frequencyChannel == TecRfidSuite.FrequencyChannelTypeAuto) {
                    mFrequencyChannel = 0;
                    setDeviceAutoFrequencySetting(autoFrequencyList);
                } else {
                    if ((mPowerType.equals(JP1)||mPowerType.equals(JP4))
                            && frequencyChannel>TecRfidSuite.FrequencyLowChannelTypeCh32) {
                        mFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh32 ;
                    } else if ((mPowerType.equals(JP3)||mPowerType.equals(JP6))
                            && frequencyChannel>TecRfidSuite.FrequencyHighChannelTypeCh25 ) {
                        mFrequencyChannel = TecRfidSuite.FrequencyHighChannelTypeCh25 ;
                    } else if ((mPowerType.equals(JP2)||mPowerType.equals(JP5))
                            && frequencyChannel>TecRfidSuite.FrequencyLicenseChannelTypeCh23 ) {
                        mFrequencyChannel = TecRfidSuite.FrequencyLicenseChannelTypeCh23 - 1;
                    }
                    else {
                        if (mPowerType.equals(JP2)||mPowerType.equals(JP5)) {
                            mFrequencyChannel = frequencyChannel - 1; // 免許局(JP2/JP5)の場合、先頭のAUTOが無いので、-1する。
                        } else {
                            mFrequencyChannel = frequencyChannel;
                        }
                    }
                }
                viewUpdate(FREQUENCY);
            } else {
                mSettingsFalied = true;
            }
        }
    };

	/**
	 * setFrequency用引数
	 */
	/** 周波数チャネル */
	private int mFrequencyChannel = TecRfidSuite.FrequencyChannelTypeAuto;
	/** 自動用周波数チャネル切り替えリスト(Auto時のみ) */
	private ArrayList<Integer> mAutoFrequencyList = new ArrayList<Integer>();
	/** 通信実行結果のコールバック */
	private ResultCallback mSetFrequencyResultCallback = new ResultCallback() {
		@Override
		public void onCallback(int resultCode, int resultCodeExtended) {
			mSemaphore.release();
			if (TecRfidSuite.OPOS_SUCCESS != resultCode) {
				mSettingsFalied = true;
			}
		}
	};

	/**
	 * getTriggerSwMode用引数
	 */
	/** トリガースイッチモード取得結果コールバック */
	private TriggerSwModeCallback mTriggerSwModeCallback = new TriggerSwModeCallback() {
		@Override
		public void onCallback(int trigMode, int resultCode, int resultCodeExtended) {
			mSemaphore.release();
			if (TecRfidSuite.OPOS_SUCCESS == resultCode) {
				mTrigMode = trigMode - 1;
				viewUpdate(TRIGGERSW);
			} else {
				mSettingsFalied = true;
			}
		}
	};

	/**
	 * setTriggerSwMode用引数
	 */
	/** トリガースイッチモード設定 */
	private int mTrigMode = TecRfidSuite.TrigModeTypeNone;
	/** 通信実行結果のコールバック */
	private ResultCallback mSetTriggerSwModeResultCallback = new ResultCallback() {
		@Override
		public void onCallback(int resultCode, int resultCodeExtended) {
			mSemaphore.release();
			if (TecRfidSuite.OPOS_SUCCESS != resultCode) {
				mSettingsFalied = true;
			}
		}
	};

	/**
	 * getSavingEnergy用引数
	 */
	/** 非同期取得結果コールバック */
	private SavingEnergyCallback mSavingEnergyCallback = new SavingEnergyCallback() {
		@Override
		public void onCallback(int energy, int resultCode, int resultCodeExtended) {
			mSemaphore.release();
			if (TecRfidSuite.OPOS_SUCCESS == resultCode) {
				mEnergy = energy;
				viewUpdate(ENERGYSAVING);
			} else {
				mSettingsFalied = true;
			}
		}
	};

	/**
	 * setSavingEnergy用引数
	 */
	/** スリープ有無 */
	private int mEnergy = 0;
	/** 通信実行結果のコールバック */
	private ResultCallback mSetSavingEnergyResultCallback = new ResultCallback() {
		@Override
		public void onCallback(int resultCode, int resultCodeExtended) {
			mSemaphore.release();
			if (TecRfidSuite.OPOS_SUCCESS != resultCode) {
				mSettingsFalied = true;
			}
		}
	};

	/** getTagReadMod結果コールバック */
	private TagReadModeCallback mTagReadModeCallback = new TagReadModeCallback() {
		@Override
		public void onCallback(int tagSpeed,int millerSubCarrier, int resultCode, int resultCodeExtended) {
			mSemaphore.release();
			if (TecRfidSuite.OPOS_SUCCESS == resultCode) {
				mTagReadMode = getTagReadMode(tagSpeed,millerSubCarrier);
				viewUpdate(READMODE);
			} else {
				mSettingsFalied = true;
			}
		}
	};

	/**
	 * setTagReadMode用引数
	 */
	/** 通信実行結果のコールバック */
	private ResultCallback mSetTagReadModeCallback = new ResultCallback() {
		@Override
		public void onCallback(int resultCode, int resultCodeExtended) {
			mSemaphore.release();
			if (TecRfidSuite.OPOS_SUCCESS != resultCode) {
				mSettingsFalied = true;
			}
		}
	};

	/**
	 * SetFlagAB用引数
	 */
	/** FlagAB */
	private int mFlagAB = TecRfidSuite.FlagA;
	/** 通信実行結果のコールバック */
	private ResultCallback mSetFlagABResultCallback = new ResultCallback() {
		@Override
		public void onCallback(int resultCode, int resultCodeExtended) {
			mSemaphore.release();
			if (TecRfidSuite.OPOS_SUCCESS != resultCode) {
				mSettingsFalied = true;
			}
		}
	};

	/**
	 * getFlagAB用引数
	 */
	/** 通信実行結果のコールバック */
	private FlagABCallback mGetFlagABCallback = new FlagABCallback() {
		@Override
		public void onCallback(int flagAB, int resultCode, int resultCodeExtended) {
			mSemaphore.release();
			if (TecRfidSuite.OPOS_SUCCESS == resultCode) {
				if (flagAB < mFlagABList.size()) {
					mFlagAB = flagAB;
					viewUpdate(FLAG_AB);
				}
				else {
					mSettingsFalied = true;
				}
			} else {
				mSettingsFalied = true;
			}
		}
	};
    /**
     * getMisreadingPreventionSettings用引数
     */
    /** 読取識別番号 */
    private int mReadingId = 0;

    /** 通信実行結果のコールバック */
    private MisreadingPreventionSettingsCallback mGetMisreadingPreventionSettingsCallback = new MisreadingPreventionSettingsCallback() {
        @Override
        public void onCallback(int id, int resultCode, int resultCodeExtended) {
            mSemaphore.release();
            if (TecRfidSuite.OPOS_SUCCESS == resultCode) {
            	mReadingId = id;
				viewUpdate(MISREADING_PPREVENTIO_SETTINGS);
            }
            else {
                mSettingsFalied = true;
            }
        }
    };

    /**
     * setMisreadingPreventionSettings用引数
     */
    /** 通信実行結果のコールバック */
    private ResultCallback mSetMisreadingPreventionSettingsCallback = new ResultCallback() {
        @Override
        public void onCallback(int resultCode, int resultCodeExtended) {
            mSemaphore.release();
            if (TecRfidSuite.OPOS_SUCCESS != resultCode) {
                mSettingsFalied = true;
            }
        }
    };

    /**
     * saveMemory用引数
     */
    /** 通信実行結果のコールバック */
    private ResultCallback mSaveMemoryResultCallback = new ResultCallback() {
        @Override
        public void onCallback(int resultCode, int resultCodeExtended) {
            mSemaphore.release();
            if (TecRfidSuite.OPOS_SUCCESS != resultCode) {
                mSettingsFalied = true;
            }
        }
    };


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

        mDeviceType = getIntent().getStringExtra(MenuDeviceActivity.KEY_DEVICENAME);

		//機種タイプ取得
		mPowerType = getIntent().getStringExtra(MenuDeviceActivity.KEY_POWERTYPE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            mStoragePath = getApplicationContext().getExternalFilesDir(null) + "";
        } else {
            mStoragePath = Environment.getExternalStorageDirectory() + "";
        }

		Object omOpenBtn = findViewById(R.id.open);
		if (omOpenBtn instanceof ImageView) {
			mOpenBtn = (ImageView) omOpenBtn;
		} else {
			mOpenBtn = new ImageView(SettingActivity.this);
		}
		mOpenBtn.setOnClickListener(this);

		Object omSaveBtn = findViewById(R.id.save);
		if (omSaveBtn instanceof ImageView) {
			mSaveBtn = (ImageView) omSaveBtn;
		} else {
			mSaveBtn = new ImageView(SettingActivity.this);
		}
		mSaveBtn.setOnClickListener(this);

		Object omGetSettingsBtn = findViewById(R.id.getsettings);
		if (omGetSettingsBtn instanceof ImageView) {
			mGetSettingsBtn = (ImageView) omGetSettingsBtn;
		} else {
			mGetSettingsBtn = new ImageView(SettingActivity.this);
		}
		mGetSettingsBtn.setOnClickListener(this);

		Object omSendSettingsBtn = findViewById(R.id.sendsettings);
		if (omSendSettingsBtn instanceof ImageView) {
			mSendSettingsBtn = (ImageView) omSendSettingsBtn;
		} else {
			mSendSettingsBtn = new ImageView(SettingActivity.this);
		}
		mSendSettingsBtn.setOnClickListener(this);

		Object omResetBtn = findViewById(R.id.reset);
		if (omResetBtn instanceof ImageView) {
			mResetBtn = (ImageView) omResetBtn;
		} else {
			mResetBtn = new ImageView(SettingActivity.this);
		}
		mResetBtn.setOnClickListener(this);

		Object omTextPower = findViewById(R.id.lv_power);
		if (omTextPower instanceof TextView) {
			mTextPower = (TextView) omTextPower;
		} else {
			mTextPower = new TextView(SettingActivity.this);
		}
		mTextPower.setOnClickListener(this);

		Object omTextQValue = findViewById(R.id.lv_qvalue);
		if (omTextQValue instanceof TextView) {
			mTextQValue = (TextView) omTextQValue;
		} else {
			mTextQValue = new TextView(SettingActivity.this);
		}
		mTextQValue.setOnClickListener(this);

		Object omTextFrequency = findViewById(R.id.lv_frequency);
		if (omTextFrequency instanceof TextView) {
			mTextFrequency = (TextView) omTextFrequency;
		} else {
			mTextFrequency = new TextView(SettingActivity.this);
		}
		mTextFrequency.setOnClickListener(this);

		Object omTextTriggerSw = findViewById(R.id.lv_triggersw);
		if (omTextTriggerSw instanceof TextView) {
			mTextTriggerSw = (TextView) omTextTriggerSw;
		} else {
			mTextTriggerSw = new TextView(SettingActivity.this);
		}
		mTextTriggerSw.setOnClickListener(this);

		Object omTextEnergySaving = findViewById(R.id.lv_sleeptime);
		if (omTextEnergySaving instanceof TextView) {
			mTextEnergySaving = (TextView) omTextEnergySaving;
		} else {
			mTextEnergySaving = new TextView(SettingActivity.this);
		}
		mTextEnergySaving.setOnClickListener(this);

		Object omTextTagReadMode = findViewById(R.id.lv_tagreadmode);
		if (omTextTagReadMode instanceof TextView) {
			mTextTagReadMode = (TextView) omTextTagReadMode;
		} else {
			mTextTagReadMode = new TextView(SettingActivity.this);
		}
		if (MenuDeviceActivity.getSDKLibrary().getIsAvailableTagReadMode()==TecRfidSuite.TagReadModeDecision.AVAILABLE_TAGREADMODE_STATE.getInt()) {
			mTextTagReadMode.setOnClickListener(this);
		}
		else{
			findViewById(R.id.tagreadmode_layout).setVisibility(View.GONE);
		}

		Object omTextFlagAB = findViewById(R.id.lv_flag_ab);
		if (omTextFlagAB instanceof TextView) {
			mTextFlagAB = (TextView) omTextFlagAB;
		} else {
			mTextFlagAB = new TextView(SettingActivity.this);
		}
		mTextFlagAB.setOnClickListener(this);
        Object omTextMisreadingPreventionSettings = findViewById(R.id.lv_misreading_prevention_settings);
        if (omTextMisreadingPreventionSettings instanceof TextView) {
            mTextMisreadingPreventionSettings = (TextView) omTextMisreadingPreventionSettings;
        } else {
            mTextMisreadingPreventionSettings = new TextView(SettingActivity.this);
        }
        if (MenuDeviceActivity.getSDKLibrary().getIsAvailableTruncate()==TecRfidSuite.TruncateDecision.AVAILABLE_TRUNCATE_STATE.getInt()) {
            mTextMisreadingPreventionSettings.setOnClickListener(this);
        }
        else{
            //誤読防止機能未対応FWの場合、読取識別番号ボタンを使用不可にする
            mTextMisreadingPreventionSettings.setEnabled(false);
        }

		getActionBar().setDisplayHomeAsUpEnabled(true);

		// リスナーを登録
        MenuDeviceActivity.setListener(this);

        // 自動周波数リスト取得
		mRecieveAutoFrequencyList = getIntent().getIntegerArrayListExtra(MenuDeviceActivity.KEY_AUTOFREQUENCYLIST);

		//フラグAB表示設定取得
		isThreeChoicesFlagAB = getIntent().getBooleanExtra(MenuDeviceActivity.KEY_THREE_CHOICES_FLAG_AB,false);

		//リストデータ追加
		prepareList();

        // autoFrequencyListを作成
        createAutoFrequencyList();

		// 初期値を取得
		initSettings();

		// デバイスの設定を取得
		getSettings();

	}

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        onBackPressed();
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mTextPower)) {
            mDialogMode = DIALOG_POWER;
            createSelectDialog(this, mPowerList, TITLE_POWER);
        } else if (view.equals(mTextQValue)) {
            mDialogMode = DIALOG_QVALUE;
            createSelectDialog(this, mQvalueList, TITLE_QVALUE);
        } else if (view.equals(mTextFrequency)) {
            mDialogMode = DIALOG_FREQUENCY;
            createSelectDialog(this, mFrequencyList, TITLE_FREQUENCY);
        } else if (view.equals(mTextTriggerSw)) {
            mDialogMode = DIALOG_TRIGGERSW;
            createSelectDialog(this, mTriggerswList, TITLE_TRIGGERSW);
        } else if (view.equals(mTextEnergySaving)) {
            mDialogMode = DIALOG_SLEEPTIME;
            createSelectDialog(this, mEnergyList, TITLE_ENERGY);
        } else if (view.equals(mTextTagReadMode)) {
            mDialogMode = DIALOG_TAG_READ_MODE;
            createSelectDialog(this, mTagReadModeList, TITLE_TAG_READ_MODE);
        } else if (view.equals(mTextMisreadingPreventionSettings)) {
            mDialogMode = DIALOG_MISREADING_PREVENTINON_SETTIGS;
            createSelectDialog(this, mMisreadingPreventionSettingsList, TITLE_MISREADING_PREVENTINON_SETTIGS);
        } else if (view.equals(mTextFlagAB)) {
            mDialogMode = DIALOG_FLAG_AB;
            createSelectDialog(this, mFlagABList, TITLE_FLAG_AB);
        }
        // 開くボタン押下
        else if (view.equals(mOpenBtn)) {
            showSaveSettings();
        }
        // 保存ボタン押下
        else if (view.equals(mSaveBtn)) {
            saveSettingText();
        }
        // 設定読込ボタン押下
        else if (view.equals(mGetSettingsBtn)) {
            getSettings();

        }
        // 設定送信ボタン押下
        else if (view.equals(mSendSettingsBtn)) {
            sendSettings(mPowerLevel, mQValue, mFrequencyChannel, mTrigMode, mEnergy, mTagReadMode,mFlagAB,mReadingId);

        }
        // 初期設定ボタン押下
        else if (view.equals(mResetBtn)) {
            initSettings();
        }
    }

    @Override
    public void disconnectDevice(String title, String message, String btn1) {
        dismissProgress();
        // エラー表示
        showDialog(title, message, btn1, null,true);
    }

	@Override
	protected void onDestroy() {
		mViewHandler.removeCallbacks(mViewRunnable);
		mViewHandler = null;
		mViewRunnable = null;
		mPowerCallback = null;
		mSetPowerResultCallback = null;
		mQValueCallback = null;
		mSetQValueResultCallback = null;
		mFrequencyCallback = null;
		
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (mCustomReadSettingDataTask != null) {
                mCustomReadSettingDataTask.cancel(true);
                mCustomReadSettingDataTask = null;
            }
        } else{
            if (mReadSettingDataTask != null) {
                if (mReadSettingDataTask.getStatus() == Status.RUNNING) {
                    mReadSettingDataTask.cancel(true);
                }
                mReadSettingDataTask = null;
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (mCustomWriteSettingDataTask != null) {
                mCustomWriteSettingDataTask.cancel(true);
                mCustomWriteSettingDataTask = null;
            }
        } else{
            if (mWriteSettingDataTask != null) {
                if (mWriteSettingDataTask.getStatus() == Status.RUNNING) {
                    mWriteSettingDataTask.cancel(true);
                }
                mWriteSettingDataTask = null;
            }
        }
		mSetFrequencyResultCallback = null;
		mTriggerSwModeCallback = null;
		mSetTriggerSwModeResultCallback = null;
		mSavingEnergyCallback = null;
		mSetSavingEnergyResultCallback = null;
		mSaveMemoryResultCallback = null;
		mSetFlagABResultCallback = null;
		mGetFlagABCallback = null;
        mGetMisreadingPreventionSettingsCallback = null;
        mSetMisreadingPreventionSettingsCallback = null;
        MenuDeviceActivity.setListener(null);
		super.onDestroy();
	}

	/**
	 * リストを使用する準備
	 */
	private void prepareList() {
		//出力リスト
		mPowerList.add(POWER_0);
		mPowerList.add(POWER_1);
		mPowerList.add(POWER_2);
		mPowerList.add(POWER_3);
		mPowerList.add(POWER_4);
		mPowerList.add(POWER_5);
		mPowerList.add(POWER_6);
		mPowerList.add(POWER_7);
		mPowerList.add(POWER_8);
		mPowerList.add(POWER_9);
		mPowerList.add(POWER_10);
		mPowerList.add(POWER_11);
		mPowerList.add(POWER_12);
		mPowerList.add(POWER_13);
		mPowerList.add(POWER_14);
		mPowerList.add(POWER_15);
		mPowerList.add(POWER_16);
		mPowerList.add(POWER_17);
		mPowerList.add(POWER_18);
		mPowerList.add(POWER_19);
		if(!mPowerType.equals(JP1)) {
			mPowerList.add(POWER_20);
			mPowerList.add(POWER_21);
			mPowerList.add(POWER_22);
			mPowerList.add(POWER_23);
			mPowerList.add(POWER_24);
			mPowerList.add(POWER_25);
			mPowerList.add(POWER_26);
		}

		//Q値リスト
		mQvalueList.add(QVALUE_0);
		mQvalueList.add(QVALUE_1);
		mQvalueList.add(QVALUE_2);
		mQvalueList.add(QVALUE_3);
		mQvalueList.add(QVALUE_4);
		mQvalueList.add(QVALUE_5);
		mQvalueList.add(QVALUE_6);
		mQvalueList.add(QVALUE_7);
		mQvalueList.add(QVALUE_8);
		mQvalueList.add(QVALUE_9);
		mQvalueList.add(QVALUE_10);

		//周波数リスト
        if ((!mPowerType.equals(JP2))&&(!mPowerType.equals(JP5))) {
            mFrequencyList.add(FREQUENCY_AUTO);
        }
		mFrequencyList.add(FREQUENCY_5CH);
		mFrequencyList.add(FREQUENCY_11CH);
		mFrequencyList.add(FREQUENCY_17CH);
		mFrequencyList.add(FREQUENCY_23CH);
		if((!mPowerType.equals(JP2))&&(!mPowerType.equals(JP5))) {
            mFrequencyList.add(FREQUENCY_24CH);
            mFrequencyList.add(FREQUENCY_25CH);
            if(mPowerType.equals(JP1)) {
                mFrequencyList.add(FREQUENCY_26CH);
                mFrequencyList.add(FREQUENCY_27CH);
                mFrequencyList.add(FREQUENCY_28CH);
                mFrequencyList.add(FREQUENCY_29CH);
                mFrequencyList.add(FREQUENCY_30CH);
                mFrequencyList.add(FREQUENCY_31CH);
                mFrequencyList.add(FREQUENCY_32CH);
            }
        }

		//トリガーモードリスト
		mTriggerswList.add(TRIGGERSW_REALTIME);
		mTriggerswList.add(TRIGGERSW_HOLD);
		mTriggerswList.add(TRIGGERSW_NONE);

		//省電力設定リスト
		mEnergyList.add(ENERGY_OFF);
		mEnergyList.add(ENERGY_ON);

		//タグ読取モードリスト
		if(MenuDeviceActivity.getSDKLibrary().getIsAvailableTruncate()==TecRfidSuite.TruncateDecision.AVAILABLE_TRUNCATE_STATE.getInt()) {
			//誤読防止機能対応版
			mTagReadModeList.add(TagReadModeTypeTruncate.FM0_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeTypeTruncate.FM0_MEDIUM_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeTypeTruncate.MILLER2_LOW_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeTypeTruncate.MILLER2_HIGH_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeTypeTruncate.MILLER4_LOW_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeTypeTruncate.MILLER4_HIGH_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeTypeTruncate.MILLER8_LOW_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeTypeTruncate.MILLER8_HIGH_SPEED_TYPE.getString());
		}
		else{
			//誤読防止機能未対応
			mTagReadModeList.add(TagReadModeType.MILLER2_MEDIUM_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeType.MILLER2_HIGH_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeType.MILLER4_MEDIUM_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeType.MILLER4_HIGH_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeType.MILLER8_MEDIUM_SPEED_TYPE.getString());
			mTagReadModeList.add(TagReadModeType.MILLER8_HIGH_SPEED_TYPE.getString());
		}

		//フラグABリスト
		mFlagABList.add(VALUE_FLAG_AB);
		mFlagABList.add(VALUE_FLAG_A);
		if (isThreeChoicesFlagAB) {
			mFlagABList.add(VALUE_FLAG_B);
		}

		//読取識別番号リスト
        int[][] table = MisreadingPreventionSettingsTable.getMisreadingPreventionSettingsTable();
        for(int i = 0; i <table.length; i++) {
            if(i == 0){
                mMisreadingPreventionSettingsList.add("invalid");
            }
            else {
                mMisreadingPreventionSettingsList.add(String.valueOf(table[i][MisreadingPreventionSettingsTable.INDEX_ID]));
            }
        }
	}

	/**
	 * 初期値を設定
	 */
	private void initSettings() {
		mPowerLevel = TecRfidSuite.HighRangePower125mW;
		if (mPowerType.equals(JP1)) {
			mPowerLevel = TecRfidSuite.LowRangePower125mW;
		}
        mQValue = 4;
        mFrequencyChannel = 0;
        mTrigMode = TecRfidSuite.TrigModeTypeNone-1;
		mEnergy = TecRfidSuite.ENG_SAV;
		mTagReadMode = TagReadModeType.MILLER4_MEDIUM_SPEED_TYPE.getInt();
        if(MenuDeviceActivity.getSDKLibrary().getIsAvailableTruncate()==TecRfidSuite.TruncateDecision.AVAILABLE_TRUNCATE_STATE.getInt()) {
            mTagReadMode = TagReadModeTypeTruncate.MILLER4_LOW_SPEED_TYPE.getInt();
        }
		mFlagAB = TecRfidSuite.FlagA;
		mReadingId = 0;
        saveAutoFrequencySetting("");
		updateSettings();
	}

	/**
	 * 現在の設定を表示
	 */
	private void updateSettings() {
		mTextPower.setText(mPowerList.get(mPowerLevel));
		mTextQValue.setText(mQvalueList.get(mQValue));
		mTextFrequency.setText(mFrequencyList.get(mFrequencyChannel));
		mTextTriggerSw.setText(mTriggerswList.get(mTrigMode));
		mTextEnergySaving.setText(mEnergyList.get(mEnergy));
		if(MenuDeviceActivity.getSDKLibrary().getIsAvailableTagReadMode()==TecRfidSuite.TagReadModeDecision.AVAILABLE_TAGREADMODE_STATE.getInt()) {
			mTextTagReadMode.setText(mTagReadModeList.get(mTagReadMode));
		}
		mTextFlagAB.setText(mFlagABList.get(mFlagAB));
		mTextMisreadingPreventionSettings.setText(mMisreadingPreventionSettingsList.get(mReadingId));
		if(mReadingId == 0) {
			//読取識別番号が「無効」の場合のみ、読取モードボタンを使用可にする
			mTextTagReadMode.setEnabled(true);
		}
		else{
			//読取識別番号が「無効」以外の場合、読取モードボタンを使用不可にする
			mTextTagReadMode.setEnabled(false);
		}
	}

	/**
	 * autoFrequencyListを作成
	 */
	private void createAutoFrequencyList() {
		int recieve = 0;
		if (mPowerType.equals(JP1)) {
			for (int i = 0; i < mRecieveAutoFrequencyList.size(); i++) {
				recieve = mRecieveAutoFrequencyList.get(i);
				if (TecRfidSuite.FrequencyLowChannelTypeCh05 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh05);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh11 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh11);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh17 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh17);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh23 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh23);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh24 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh24);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh25 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh25);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh26 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh26);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh27 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh27);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh28 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh28);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh29 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh29);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh30 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh30);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh31 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh31);
				} else if (TecRfidSuite.FrequencyLowChannelTypeCh32 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh32);
				}
			}
		} else  {
			for (int i = 0; i < mRecieveAutoFrequencyList.size(); i++) {
				recieve = mRecieveAutoFrequencyList.get(i);
				if (TecRfidSuite.FrequencyHighChannelTypeCh05 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh05);
				} else if (TecRfidSuite.FrequencyHighChannelTypeCh11 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh11);
				} else if (TecRfidSuite.FrequencyHighChannelTypeCh17 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh17);
				} else if (TecRfidSuite.FrequencyHighChannelTypeCh23 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh23);
				} else if (TecRfidSuite.FrequencyHighChannelTypeCh24 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh24);
				} else if (TecRfidSuite.FrequencyHighChannelTypeCh25 == recieve) {
					mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh25);
				}
			}
		}
		// ファイル不正などで、AutoFrequencyListが設定できなかった場合、デフォルト値を設定
		if (0 == mAutoFrequencyList.size()) {
			createDefaultAutoFrequencyList();
            MenuDeviceActivity.createInitSettingFIle(mStoragePath);
		}
	}

	/**
	 * autoFrequencyListをデフォルトで設定作成
	 */
	private void createDefaultAutoFrequencyList() {
        if (mPowerType.equals(JP1)) {
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh26);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh30);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh28);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh32);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh17);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh11);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh23);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh27);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh29);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh31);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh05);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
        }
        else if(mPowerType.equals(JP3)) {
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh17);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh11);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh23);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh05);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
        }
        else if(mPowerType.equals(JP4)) {
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh26);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh28);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh32);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh17);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh11);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh23);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh29);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh31);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyLowChannelTypeCh05);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
        }
        else if(mPowerType.equals(JP6)) {
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh25);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh17);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh11);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh23);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh05);
            mAutoFrequencyList.add(TecRfidSuite.FrequencyHighChannelTypeCh24);
        }
	}

	/**
	 * 表示更新
	 */
	private void viewUpdate(final int setting) {
		mViewRunnable = new Runnable() {
			@Override
			public void run() {
				if (POWER == setting) {
					mTextPower.setText(mPowerList.get(mPowerLevel));
				} else if (QVALUE == setting) {
					mTextQValue.setText(mQvalueList.get(mQValue));
				} else if (FREQUENCY == setting) {
					mTextFrequency.setText(mFrequencyList.get(mFrequencyChannel));
				} else if (TRIGGERSW == setting) {
					mTextTriggerSw.setText(mTriggerswList.get(mTrigMode));
				} else if (ENERGYSAVING == setting) {
					mTextEnergySaving.setText(mEnergyList.get(mEnergy));
				} else if (READMODE == setting) {
					mTextTagReadMode.setText(mTagReadModeList.get(mTagReadMode));
				} else if (FLAG_AB == setting) {
					mTextFlagAB.setText(mFlagABList.get(mFlagAB));
				}
                else if (MISREADING_PPREVENTIO_SETTINGS == setting) {
                    mTextMisreadingPreventionSettings.setText(mMisreadingPreventionSettingsList.get(mReadingId));
                    if(mReadingId == 0) {
                        //読取識別番号が「無効」の場合のみ、読取モードボタンを使用可にする
                        mTextTagReadMode.setEnabled(true);
                    }
                    else{
                        //読取識別番号が「無効」以外の場合、読取モードボタンを使用不可にする
                        mTextTagReadMode.setEnabled(false);
                    }
                }
			}
		};
		mViewHandler.post(mViewRunnable);
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
		if (DIALOG_POWER == mDialogMode) {
			mSelectItem = mPowerLevel;
		} else if (DIALOG_QVALUE == mDialogMode) {
			mSelectItem = mQValue;
		} else if (DIALOG_FREQUENCY == mDialogMode) {
			mSelectItem = mFrequencyChannel;
		} else if (DIALOG_TRIGGERSW == mDialogMode) {
			mSelectItem = mTrigMode;
		} else if (DIALOG_SLEEPTIME == mDialogMode) {
			mSelectItem = mEnergy;
		} else if (DIALOG_FLAG_AB == mDialogMode) {
			mSelectItem = mFlagAB;
		} else if (DIALOG_TAG_READ_MODE == mDialogMode) {
			mSelectItem = mTagReadMode;
		}
        else if (DIALOG_MISREADING_PREVENTINON_SETTIGS == mDialogMode) {
            mSelectItem = mReadingId;
        }
		builder.setSingleChoiceItems(items, mSelectItem, mItemListener);

		// 決定・キャンセル用にボタンも配置 //
		builder.setPositiveButton(getString(R.string.btn_txt_set), mButtonListener);
		builder.setNeutralButton(getString(R.string.btn_txt_cancel), mButtonListener);

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * どのアイテムが選択されているか
	 */
	DialogInterface.OnClickListener mItemListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			mSelectItem = which;
		}
	};

	/**
	 * ボタンのリスナー
	 */
	DialogInterface.OnClickListener mButtonListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:
				if (DIALOG_POWER == mDialogMode) {
					mTextPower.setText(mPowerList.get(mSelectItem));
					mPowerLevel = mSelectItem;
				} else if (DIALOG_QVALUE == mDialogMode) {
					mTextQValue.setText(mQvalueList.get(mSelectItem));
					mQValue = mSelectItem;
				} else if (DIALOG_FREQUENCY == mDialogMode) {
                    mTextFrequency.setText(mFrequencyList.get(mSelectItem));
                    mFrequencyChannel = mSelectItem;
                    if(mFrequencyList.get(mSelectItem).equals(FREQUENCY_AUTO)) {
                        Intent intent = new Intent(SettingActivity.this, FrequencySelectActivity.class);
                        intent.putExtra(MenuDeviceActivity.KEY_POWERTYPE, mPowerType);
                        intent.putExtra(MenuDeviceActivity.KEY_DEVICENAME, mDeviceType);
                        startActivity(intent);
                    }
				} else if (DIALOG_TRIGGERSW == mDialogMode) {
					mTextTriggerSw.setText(mTriggerswList.get(mSelectItem));
					mTrigMode = mSelectItem;
				} else if (DIALOG_SLEEPTIME == mDialogMode) {
					mTextEnergySaving.setText(mEnergyList.get(mSelectItem));
					mEnergy = mSelectItem;
				} else if (DIALOG_TAG_READ_MODE == mDialogMode) {
					mTextTagReadMode.setText(mTagReadModeList.get(mSelectItem));
					mTagReadMode = mSelectItem;
				} else if (DIALOG_FLAG_AB == mDialogMode) {
					mTextFlagAB.setText(mFlagABList.get(mSelectItem));
					mFlagAB = mSelectItem;
				}
                else if (DIALOG_MISREADING_PREVENTINON_SETTIGS == mDialogMode) {
                    mTextMisreadingPreventionSettings.setText(mMisreadingPreventionSettingsList.get(mSelectItem));
                    mReadingId = mSelectItem;
                    if(mReadingId == 0) {
                        //読取識別番号が「無効」の場合のみ、読取モードボタンを使用可にする
                        mTextTagReadMode.setEnabled(true);
                    }
                    else{
                        //読取識別番号が「無効」以外の場合、読取モードボタンを使用不可にする
                        mTextTagReadMode.setEnabled(false);
                    }
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
	 * テキスト形式の設定ファイルをSDへ保存
	 */
	private void saveSettingText() {
		// ファイル名入力用エディットテキスト
		final EditText editView = new EditText(SettingActivity.this);
		new AlertDialog.Builder(SettingActivity.this).setTitle(getString(R.string.title_savedialog))
		// setViewにてビューを設定します。
				.setView(editView).setPositiveButton(getString(R.string.btn_txt_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                        String filePath = mStoragePath + SAVE_SETTING_PATH;
						File file = new File(filePath);
						boolean isCreate = file.mkdirs();
						// ディレクトリがないとき
						if (!isCreate && !file.exists()) {
							// エラー表示
							showDialog(getString(R.string.title_savesetting), getString(R.string.message_savesetting_error), getString(R.string.btn_txt_ok), null);
							return;
						}

						final String newFilePath = filePath + editView.getText().toString() + ".txt";
						final File newFile = new File(newFilePath);
						// 同名ファイルがある場合
						if (newFile.exists()) {
							// 上書きするか表示
							showDialog(getString(R.string.title_savesetting), getString(R.string.message_savesetting_override), getString(R.string.btn_txt_ok), getString(R.string.btn_txt_cancel), new Runnable() {
								@Override
								public void run() {
									// OKボタン押下
									// ファイルを削除
									boolean fileDir = newFile.delete();
									if (fileDir) {
										createNewFile(newFilePath);
									} else {
										// エラー表示
										showDialog(getString(R.string.title_savesetting), getString(R.string.message_savesetting_error), getString(R.string.btn_txt_ok), null);
									}
								}
							}, new Runnable() {
								@Override
								public void run() {
									// キャンセルボタン押下
									return;
								}
							});
						} else {
							createNewFile(newFilePath);
						}
					}
				}).setNegativeButton(getString(R.string.btn_txt_cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                        // なにもしない
					}
				}).show();
	}

	/**
	 * 新しいファイルを作成
	 *
	 * @param filePath 新規ファイルパス
	 */
	private void createNewFile(String filePath) {
		FileOutputStream fos = null;
		BufferedWriter bw = null;
        OutputStreamWriter osw = null;
        try {
			// 作成したファイルが PC で見えるように認識させる。
			MediaScannerConnection.scanFile(this, new String[] { filePath }, null, null);

			fos = new FileOutputStream(filePath, true);
            osw = new OutputStreamWriter(fos, "UTF-8");
			bw = new BufferedWriter(osw);
			String str = KEY_POWER + COMMA + mPowerLevel + NEWLINE + KEY_VALUEQ + COMMA + mQValue + NEWLINE
					+ KEY_FREQUENCY + COMMA + mFrequencyChannel + NEWLINE + KEY_TRIGGER + COMMA + mTrigMode
					+ NEWLINE + KEY_ENERGYSAVING + COMMA + mEnergy + NEWLINE;
			if(MenuDeviceActivity.getSDKLibrary().getIsAvailableTagReadMode()==TecRfidSuite.TagReadModeDecision.AVAILABLE_TAGREADMODE_STATE.getInt()) {
				str +=  KEY_TAGREADMODE + COMMA + mTagReadMode + NEWLINE;
			}
			str +=  KEY_FLAG_AB + COMMA + mFlagAB + NEWLINE;
            if(MenuDeviceActivity.getSDKLibrary().getIsAvailableTruncate()==TecRfidSuite.TruncateDecision.AVAILABLE_TRUNCATE_STATE.getInt()) {
                str +=  KEY_MISREADINGPREVENTIONSETTINGS + COMMA + mReadingId + NEWLINE;
            }
            str +=  FrequencySelectActivity.AUTO_FREQUENCY_CHANNEL_KEY + COMMA + getAutoFrequencyFileSaveSetting() + NEWLINE;
			bw.write(str);
			bw.flush();
			bw.close();
			showDialog(getString(R.string.title_savesetting), getString(R.string.message_savefile), getString(R.string.btn_txt_ok), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// エラー表示
			showDialog(getString(R.string.title_savesetting), getString(R.string.message_savesetting_error), getString(R.string.btn_txt_ok), null);
		} catch (IOException e) {
			e.printStackTrace();
			showDialog(getString(R.string.title_savesetting), getString(R.string.message_savesetting_error), getString(R.string.btn_txt_ok), null);
		} finally {
			try {
				if (null != bw) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		}
	}

	/**
	 * SDに保存されている設定ファイルを表示
	 */
	private void showSaveSettings() {
		// ファイルの一覧を検索するディレクトリパスを指定する
        String path = mStoragePath + SAVE_SETTING_PATH;

		// 選択ボックスで表示するファイル名のリストを作成
		File dir = new File(path);
		final File[] files = dir.listFiles();
		if (files==null) {
			showDialog(getString(R.string.title_opensetting), getString(R.string.message_setting_notexist_error), getString(R.string.btn_txt_ok), null);
			return;
		}
		if (0 == files.length) {
			showDialog(getString(R.string.title_opensetting), getString(R.string.message_setting_notexist_error), getString(R.string.btn_txt_ok), null);
			return;
		}
		final String[] str_items;
		str_items = new String[files.length + 1];
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			str_items[i] = file.getName();
		}
		str_items[files.length] = getString(R.string.btn_txt_cancel);

		// ファイルの選択ボックスを表示
		new AlertDialog.Builder(this).setTitle(getString(R.string.title_opensetting)).setItems(str_items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				if (which < files.length) {
					String filePath = files[which].toString();
					readSaveSettings(filePath);
				}
			}
		}).show();
	}

	/**
	 * 指定された設定ファイルを読み込み設定に反映
	 *
	 * @param filepath 指定ファイルパス
	 */
	private void readSaveSettings(String filepath) {
		BufferedReader br = null;
		InputStreamReader in = null;
		FileInputStream is = null;
		try {
			is = new FileInputStream(filepath);
			in = new InputStreamReader(is, "UTF-8");
			br = new BufferedReader(in);
			int index = 0;
			while (br.ready()) {
				String line = br.readLine();
				if (null != line) {
					if (line.indexOf(KEY_POWER + COMMA) != -1) {
						index = line.indexOf(KEY_POWER + COMMA);
						index += (KEY_POWER + COMMA).length();
						line = line.substring(index);
						try {
							int i = Integer.parseInt(line);
							if (i < mPowerList.size()) {
								mPowerLevel = i;
							}
						} catch (NumberFormatException nfex) {
                            nfex.printStackTrace();
						}
					} else if (line.indexOf(KEY_VALUEQ + COMMA) != -1) {
						index = line.indexOf(KEY_VALUEQ + COMMA);
						index += (KEY_VALUEQ + COMMA).length();
						line = line.substring(index);
						try {
							int i = Integer.parseInt(line);
							if (i < mQvalueList.size()) {
								mQValue = i;
							}
						} catch (NumberFormatException nfex) {
                            nfex.printStackTrace();
						}
					} else if (line.indexOf(KEY_FREQUENCY + COMMA) != -1) {
						index = line.indexOf(KEY_FREQUENCY + COMMA);
						index += (KEY_FREQUENCY + COMMA).length();
						line = line.substring(index);
						try {
							int i = Integer.parseInt(line);
							if (i < mFrequencyList.size()) {
								mFrequencyChannel = i;
							}
						} catch (NumberFormatException nfex) {
                            nfex.printStackTrace();
						}
					} else if (line.indexOf(KEY_TRIGGER + COMMA) != -1) {
						index = line.indexOf(KEY_TRIGGER + COMMA);
						index += (KEY_TRIGGER + COMMA).length();
						line = line.substring(index);
						try {
							int i = Integer.parseInt(line);
							if (i < mTriggerswList.size()) {
								mTrigMode = i;
							}
						} catch (NumberFormatException nfex) {
                            nfex.printStackTrace();
						}
					} else if (line.indexOf(KEY_ENERGYSAVING + COMMA) != -1) {
						index = line.indexOf(KEY_ENERGYSAVING + COMMA);
						index += (KEY_ENERGYSAVING + COMMA).length();
						line = line.substring(index);
						try {
							int i = Integer.parseInt(line);
							if (i < mEnergyList.size()) {
								mEnergy = i;
							}
						} catch (NumberFormatException nfex) {
                            nfex.printStackTrace();
						}
					} else if (line.indexOf(KEY_TAGREADMODE + COMMA) != -1) {
						index = line.indexOf(KEY_TAGREADMODE + COMMA);
						index += (KEY_TAGREADMODE + COMMA).length();
						line = line.substring(index);
						try {
							int i = Integer.parseInt(line);
							if (i < mTagReadModeList.size()) {
								mTagReadMode = i;
							}
						} catch (NumberFormatException nfex) {
                            nfex.printStackTrace();
						}
					} else if (line.indexOf(KEY_FLAG_AB + COMMA) != -1) {
						index = line.indexOf(KEY_FLAG_AB + COMMA);
						index += (KEY_FLAG_AB + COMMA).length();
						line = line.substring(index);
						try {
							int i = Integer.parseInt(line);
							if (i >= mFlagABList.size()) {
								// FlagAB設定値が範囲外の場合、エラーにする
								showDialog(getString(R.string.title_readsetting),
										getString(R.string.message_readsetting_out_of_range_error),
										getString(R.string.btn_txt_ok), null);
								break;
							}
							else {
								mFlagAB = i;
							}
						} catch (NumberFormatException nfex) {
                            nfex.printStackTrace();
						}
                    } else if (line.indexOf(KEY_MISREADINGPREVENTIONSETTINGS + COMMA) != -1) {
                        if(MenuDeviceActivity.getSDKLibrary().getIsAvailableTruncate()==TecRfidSuite.TruncateDecision.AVAILABLE_TRUNCATE_STATE.getInt()) {
                            index = line.indexOf(KEY_MISREADINGPREVENTIONSETTINGS + COMMA);
                            index += (KEY_MISREADINGPREVENTIONSETTINGS + COMMA).length();
                            line = line.substring(index);
                            try {
                                int i = Integer.parseInt(line);
                                if (i < mMisreadingPreventionSettingsList.size()) {
                                    mReadingId = i;
                                }
                            } catch (NumberFormatException nfex) {
                                nfex.printStackTrace();
                            }
                        }
                    } else if (line.indexOf(FrequencySelectActivity.AUTO_FREQUENCY_CHANNEL_KEY + COMMA) != -1) {
                        index = line.indexOf(FrequencySelectActivity.AUTO_FREQUENCY_CHANNEL_KEY + COMMA);
                        index += (FrequencySelectActivity.AUTO_FREQUENCY_CHANNEL_KEY + COMMA).length();
                        line = line.substring(index);
                        saveAutoFrequencySetting(line);
					} else {
						showDialog(getString(R.string.title_readsetting), getString(R.string.message_readsetting_error), getString(R.string.btn_txt_ok), null);
						break;
					}
				}
				updateSettings();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			showDialog(getString(R.string.title_opensetting), getString(R.string.message_opensetting_error), getString(R.string.btn_txt_ok), null);
		} catch (IOException e) {
			e.printStackTrace();
			showDialog(getString(R.string.title_opensetting), getString(R.string.message_opensetting_error), getString(R.string.btn_txt_ok), null);
		} finally {
			try {
				if (null != br) {
					br.close();
				}
				if (null != in) {
					in.close();
				}
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * デバイスから設定を読み込む
	 */
	private void getSettings() {
		showProgress();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            mCustomReadSettingDataTask = new CustomReadSettingDataTask();
            mCustomReadSettingDataTask.execute("");
        } else {
            mReadSettingDataTask = new ReadSettingDataTask();
            mReadSettingDataTask.execute("");
        }
	}

	/**
	 * 設定読込用タスク
	 */
	private class ReadSettingDataTask extends AsyncTask<String, String, Long> {

		@Override
		protected void onPostExecute(Long result) {
			dismissProgress();
			if (result != (long) TecRfidSuite.OPOS_SUCCESS) {
				showDialog(null, getString(R.string.message_processfailed_getSettings), getString(R.string.btn_txt_ok),
						null);
			} else if (mSettingsFalied) {
				mSettingsFalied = false;
				showDialog(null, getString(R.string.message_processfailed_getSettings), getString(R.string.btn_txt_ok),
						null);
			}
			super.onPostExecute(result);
		}

		@Override
		protected Long doInBackground(String... params) {
			int ret = ReadSettingData();
			return (long) ret;
		}

		@Override
		protected void onCancelled() {
			dismissProgress();
			super.onCancelled();
		}
	}
    /**
     * 設定読込用タスク
     */
    private class CustomReadSettingDataTask extends CustomAsyncTask<String, String, Long> {

        @Override
        protected void onPostExecute(Long result) {
            dismissProgress();
            if (result != (long) TecRfidSuite.OPOS_SUCCESS) {
                showDialog(null, getString(R.string.message_processfailed_getSettings), getString(R.string.btn_txt_ok),
                        null);
            } else if (mSettingsFalied) {
                mSettingsFalied = false;
                showDialog(null, getString(R.string.message_processfailed_getSettings), getString(R.string.btn_txt_ok),
                        null);
            }
            super.onPostExecute(result);
        }

        @Override
        protected Long doInBackground(String... params) {
            int ret = ReadSettingData();
            return (long) ret;
        }

        @Override
        protected void onCancelled() {
            dismissProgress();
            super.onCancelled();
        }
    }

	/**
	 * 設定読込処理
	 */
	private int ReadSettingData() {
		mSettingsFalied = false;
		mSemaphore = new Semaphore(0);

		//出力設定取得
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().getPower(mPowerCallback)) {
			mSettingsFalied = true;
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;

		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

		//Q値設定取得
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().getQValue(mQValueCallback)) {
			mSettingsFalied = true;
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

        //周波数設定取得
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().getFrequency(mFrequencyCallback)) {
			mSettingsFalied = true;
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

        //トリガモード設定取得
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().getTriggerSwMode(mTriggerSwModeCallback)) {
			mSettingsFalied = true;
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

        //省電力設定取得
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().getSavingEnergy(mSavingEnergyCallback)) {
			mSettingsFalied = true;
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

        //読取モード設定取得
		if(MenuDeviceActivity.getSDKLibrary().getIsAvailableTagReadMode()==TecRfidSuite.TagReadModeDecision.AVAILABLE_TAGREADMODE_STATE.getInt()) {
			mSemaphore = new Semaphore(0);
			if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().getTagReadMode(mTagReadModeCallback)) {
				mSettingsFalied = true;
				mSemaphore.release();
				return TecRfidSuite.OPOS_E_FAILURE;
			}
			try {
                mSemaphore.acquire();
			} catch (InterruptedException e) {
                e.printStackTrace();
			}
		}

        //フラグAB設定取得
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().getFlagAB(mGetFlagABCallback)) {
			mSettingsFalied = true;
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

		//読取識別番号取得処理
        if(MenuDeviceActivity.getSDKLibrary().getIsAvailableTruncate()==TecRfidSuite.TruncateDecision.AVAILABLE_TRUNCATE_STATE.getInt()) {
            mSemaphore = new Semaphore(0);
            if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().getMisreadingPreventionSettings(mGetMisreadingPreventionSettingsCallback)) {
                mSettingsFalied = true;
                mSemaphore.release();
                return TecRfidSuite.OPOS_E_FAILURE;
            }
            try {
                mSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

		return TecRfidSuite.OPOS_SUCCESS;
	}

	/**
	 * タグ読取モード取得処理
	 * @param tagSpeed タグ速度設定
	 * @param millerSubCarrier ミラー副搬送波設定
	 * @return　int 読取モード
	 */
	private int getTagReadMode(final int tagSpeed,final int millerSubCarrier) {
        int readmode = TagReadModeType.MILLER4_MEDIUM_SPEED_TYPE.getInt();
        if (MenuDeviceActivity.getSDKLibrary().getIsAvailableTruncate()==TecRfidSuite.TruncateDecision.AVAILABLE_TRUNCATE_STATE.getInt()) {
            if (tagSpeed == TecRfidSuite.TagReadSpeedType.FM0_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt()) {
                //FM0
                readmode = TagReadModeTypeTruncate.FM0_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.FM0_MEDIUM_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt()) {
                //FM0:中速
                readmode = TagReadModeTypeTruncate.FM0_MEDIUM_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.LOW_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER2_TYPE.getInt()) {
                //Miller2:低速
                readmode = TagReadModeTypeTruncate.MILLER2_LOW_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER2_TYPE.getInt()) {
                //Miller2:低速
                readmode = TagReadModeTypeTruncate.MILLER2_HIGH_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.LOW_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt()) {
                //Miller4:低速
                readmode = TagReadModeTypeTruncate.MILLER4_LOW_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt()) {
                //Miller4:低速
                readmode = TagReadModeTypeTruncate.MILLER4_HIGH_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.LOW_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER8_TYPE.getInt()) {
                //Miller8:低速
                readmode = TagReadModeTypeTruncate.MILLER8_LOW_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER8_TYPE.getInt()) {
                //Miller8:低速
                readmode = TagReadModeTypeTruncate.MILLER8_HIGH_SPEED_TYPE.getInt();
            }
            //Miller2:中速に設定されている場合、Miller2:低速で表示する
            //Miller4:中速に設定されている場合、Miller4:低速で表示する
            //Miller8:中速に設定されている場合、Miller8:低速で表示する
            else if (tagSpeed == TecRfidSuite.TagReadSpeedType.MEDIUM_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER2_TYPE.getInt()) {
                readmode = TagReadModeTypeTruncate.MILLER2_LOW_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.MEDIUM_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt()) {
                readmode = TagReadModeTypeTruncate.MILLER4_LOW_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.MEDIUM_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER8_TYPE.getInt()) {
                readmode = TagReadModeTypeTruncate.MILLER8_LOW_SPEED_TYPE.getInt();
            }
        }
        else {
            if (tagSpeed == TecRfidSuite.TagReadSpeedType.MEDIUM_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER2_TYPE.getInt()) {
                readmode = TagReadModeType.MILLER2_MEDIUM_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER2_TYPE.getInt()) {
                readmode = TagReadModeType.MILLER2_HIGH_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.MEDIUM_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt()) {
                readmode = TagReadModeType.MILLER4_MEDIUM_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt()) {
                readmode = TagReadModeType.MILLER4_HIGH_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.MEDIUM_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER8_TYPE.getInt()) {
                readmode = TagReadModeType.MILLER8_MEDIUM_SPEED_TYPE.getInt();
            } else if (tagSpeed == TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt() && millerSubCarrier == TecRfidSuite.TagReadMillerType.MILLER8_TYPE.getInt()) {
                readmode = TagReadModeType.MILLER8_HIGH_SPEED_TYPE.getInt();
            }
        }
        return readmode;
	}

	/**
	 * デバイスへ設定を送信する
	 *
	 * @param powerLevel
	 * @param qValue
	 * @param frequencyChannel
	 * @param trigMode
	 * @param energy
	 * @param tagReadMode
     * @param flagAB
     * @param misreadingPrevantionSettings
     *
	 */
    private void sendSettings(int powerLevel, int qValue, int frequencyChannel, int trigMode, int energy, int tagReadMode, int flagAB, int misreadingPrevantionSettings) {
		sendPowerLevel = getSendPowerLevel(powerLevel);
		sendQvalue = qValue;
		sendFrequencyChannel = getSendFrequencyChannel(frequencyChannel);
		sendTrigMode = getSendTrigMode(trigMode);
		sendEnergy = getSendEnergy(energy);
		setSendTagReadMode(tagReadMode);
		sendFlagAB = flagAB;
        sendMisreadingPreventionSettings = misreadingPrevantionSettings;
        setSendAutoFrequencySetting();
		showProgress();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            mCustomWriteSettingDataTask = new CustomWriteSettingDataTask();
            mCustomWriteSettingDataTask.execute("");
        } else {
            mWriteSettingDataTask = new WriteSettingDataTask();
            mWriteSettingDataTask.execute("");
        }
	}

	/**
	 * 設定書込み用タスク
	 */
	private class WriteSettingDataTask extends AsyncTask<String, String, Long> {

		@Override
		protected void onPostExecute(Long result) {
			dismissProgress();
			if (result != (long) TecRfidSuite.OPOS_SUCCESS) {
				showDialog(null, getString(R.string.message_processfailed_sendSettings), getString(R.string.btn_txt_ok),
						null);
			} else if (mSettingsFalied) {
				mSettingsFalied = false;
				showDialog(null, getString(R.string.message_processfailed_sendSettings), getString(R.string.btn_txt_ok),
						null);
			} else {
				showDialog(getString(R.string.title_sendsetting),
						getString(R.string.message_processsuccess_setSettings), getString(R.string.btn_txt_ok), null);
			}
		}

		@Override
		protected Long doInBackground(String... params) {
			int ret = WriteSettingData();
			return (long) ret;
		}

		@Override
		protected void onCancelled() {
			dismissProgress();
			super.onCancelled();
		}
	}
    /**
     * 設定書込み用タスク
     */
    private class CustomWriteSettingDataTask extends CustomAsyncTask<String, String, Long> {

        @Override
        protected void onPostExecute(Long result) {
            dismissProgress();
            if (result != (long) TecRfidSuite.OPOS_SUCCESS) {
                showDialog(null, getString(R.string.message_processfailed_sendSettings), getString(R.string.btn_txt_ok),
                        null);
            } else if (mSettingsFalied) {
                mSettingsFalied = false;
                showDialog(null, getString(R.string.message_processfailed_sendSettings), getString(R.string.btn_txt_ok),
                        null);
            } else {
                showDialog(getString(R.string.title_sendsetting),
                        getString(R.string.message_processsuccess_setSettings), getString(R.string.btn_txt_ok), null);
            }
        }

        @Override
        protected Long doInBackground(String... params) {
            int ret = WriteSettingData();
            return (long) ret;
        }

        @Override
        protected void onCancelled() {
            dismissProgress();
            super.onCancelled();
        }
    }

	/**
	 * 設定書込み処理
	 */
	private int WriteSettingData() {
		mSettingsFalied = false;
		//出力設定書込み
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().setPower(sendPowerLevel, mSetPowerResultCallback)) {
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

        //Q値設定書込み
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().setQValue(sendQvalue, mSetQValueResultCallback)) {
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

        //周波数設定書込み
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().setFrequency(sendFrequencyChannel, mAutoFrequencyList,
				mSetFrequencyResultCallback)) {
			mSettingsFalied = true;
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

        //トリガモード設定書込み
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().setTriggerSwMode(sendTrigMode, mSetTriggerSwModeResultCallback)) {
			mSettingsFalied = true;
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

        //省電力設定書込み
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().setSavingEnergy(sendEnergy, mSetSavingEnergyResultCallback)) {
			mSettingsFalied = true;
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

        //読取モード設定書込み
		if(MenuDeviceActivity.getSDKLibrary().getIsAvailableTagReadMode()==TecRfidSuite.TagReadModeDecision.AVAILABLE_TAGREADMODE_STATE.getInt()) {
			mSemaphore = new Semaphore(0);
			if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().setTagReadMode(sendTagReadSpeedType,sendTagReadMillerType,
					mSetTagReadModeCallback)) {
				mSettingsFalied = true;
				mSemaphore.release();
				return TecRfidSuite.OPOS_E_FAILURE;
			}
			try {
                mSemaphore.acquire();
			} catch (InterruptedException e) {
                e.printStackTrace();
			}
		}

        //フラグAB設定書込み
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().setFlagAB(sendFlagAB, mSetFlagABResultCallback)) {
			mSettingsFalied = true;
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}

        //読取識別番号設定書込み
        if(MenuDeviceActivity.getSDKLibrary().getIsAvailableTruncate()==TecRfidSuite.TruncateDecision.AVAILABLE_TRUNCATE_STATE.getInt()) {
            mSemaphore = new Semaphore(0);
            if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().setMisreadingPreventionSettings(sendMisreadingPreventionSettings, mSetMisreadingPreventionSettingsCallback)) {
                mSettingsFalied = true;
                mSemaphore.release();
                return TecRfidSuite.OPOS_E_FAILURE;
            }
            try {
                mSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //設定保存
		mSemaphore = new Semaphore(0);
		if (TecRfidSuite.OPOS_SUCCESS != MenuDeviceActivity.getSDKLibrary().saveMemory(mSaveMemoryResultCallback)) {
			mSettingsFalied = true;
			mSemaphore.release();
			return TecRfidSuite.OPOS_E_FAILURE;
		}
		try {
            mSemaphore.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
		}
		return TecRfidSuite.OPOS_SUCCESS;
	}


	/**
	 * 送信する出力を取得
	 *
	 * @param powerLevel
	 * @return 送信する出力
	 */
	private int getSendPowerLevel(int powerLevel) {
		int sendPowerLevel = TecRfidSuite.LowRangePower1mW;
		if (mPowerList.get(powerLevel).equals(POWER_0)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower1mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower1mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_1)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower1_3mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower1_3mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_2)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower1_7mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower1_7mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_3)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower2_1mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower2_1mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_4)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower2_8mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower2_8mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_5)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower3_6mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower3_6mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_6)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower4_6mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower4_6mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_7)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower5_9mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower5_9mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_8)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower7_6mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower7_6mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_9)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower9_8mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower9_8mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_10)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower13mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower13mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_11)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower16mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower16mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_12)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower21mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower21mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_13)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower27mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower27mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_14)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower35mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower35mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_15)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower45mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower45mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_16)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower58mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower58mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_17)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower75mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower75mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_18)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower97mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower97mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_19)) {
			if (mPowerType.equals(JP1)) {
				sendPowerLevel = TecRfidSuite.LowRangePower125mW;
			}
			else {
				sendPowerLevel = TecRfidSuite.HighRangePower125mW;
			}
		} else if (mPowerList.get(powerLevel).equals(POWER_20)) {
			sendPowerLevel = TecRfidSuite.HighRangePower152mW;
		} else if (mPowerList.get(powerLevel).equals(POWER_21)) {
			sendPowerLevel = TecRfidSuite.HighRangePower186mW;
		} else if (mPowerList.get(powerLevel).equals(POWER_22)) {
			sendPowerLevel = TecRfidSuite.HighRangePower226mW;
		} else if (mPowerList.get(powerLevel).equals(POWER_23)) {
			sendPowerLevel = TecRfidSuite.HighRangePower276mW;
		} else if (mPowerList.get(powerLevel).equals(POWER_24)) {
			sendPowerLevel = TecRfidSuite.HighRangePower336mW;
		} else if (mPowerList.get(powerLevel).equals(POWER_25)) {
			sendPowerLevel = TecRfidSuite.HighRangePower410mW;
		} else if (mPowerList.get(powerLevel).equals(POWER_26)) {
			sendPowerLevel = TecRfidSuite.HighRangePower500mW;
		}

		return sendPowerLevel;
	}

	/**
	 * 送信する周波数を取得
	 *
	 * @param frequencyChannel
	 * @return 送信する周波数
	 */
	private int getSendFrequencyChannel(int frequencyChannel) {
		int sendFrequencyChannel = TecRfidSuite.FrequencyChannelTypeAuto;
		if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_AUTO)) {
			sendFrequencyChannel = TecRfidSuite.FrequencyChannelTypeAuto;
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_5CH)) {
			if (mPowerType.equals(JP1)) {
				sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh05;
			} else {
				sendFrequencyChannel = TecRfidSuite.FrequencyHighChannelTypeCh05;
			}
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_11CH)) {
			if (mPowerType.equals(JP1)) {
				sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh11;
			} else {
				sendFrequencyChannel = TecRfidSuite.FrequencyHighChannelTypeCh11;
			}
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_17CH)) {
			if (mPowerType.equals(JP1)) {
				sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh17;
			} else {
				sendFrequencyChannel = TecRfidSuite.FrequencyHighChannelTypeCh17;
			}
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_23CH)) {
			if (mPowerType.equals(JP1)) {
				sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh23;
			} else {
				sendFrequencyChannel = TecRfidSuite.FrequencyHighChannelTypeCh23;
			}
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_24CH)) {
			if (mPowerType.equals(JP1)) {
				sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh24;
			} else {
				sendFrequencyChannel = TecRfidSuite.FrequencyHighChannelTypeCh24;
			}
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_25CH)) {
			if (mPowerType.equals(JP1)) {
				sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh25;
			} else {
				sendFrequencyChannel = TecRfidSuite.FrequencyHighChannelTypeCh25;
			}
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_26CH)) {
			sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh26;
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_27CH)) {
			sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh27;
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_28CH)) {
			sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh28;
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_29CH)) {
			sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh29;
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_30CH)) {
			sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh30;
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_31CH)) {
			sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh31;
		} else if (mFrequencyList.get(frequencyChannel).equals(FREQUENCY_32CH)) {
			sendFrequencyChannel = TecRfidSuite.FrequencyLowChannelTypeCh32;
		}
		return sendFrequencyChannel;
	}

	/**
	 * 送信するトリガースイッチモードを取得
	 *
	 * @param trigMode
	 * @return 送信するトリガースイッチモード
	 */
	private int getSendTrigMode(int trigMode) {
		int sendTrigMode = TecRfidSuite.TrigModeTypeNone;
		if (mTriggerswList.get(trigMode).equals(TRIGGERSW_REALTIME)) {
			sendTrigMode = TecRfidSuite.TrigModeTypeRealtime;
		} else if (mTriggerswList.get(trigMode).equals(TRIGGERSW_HOLD)) {
			sendTrigMode = TecRfidSuite.TrigModeTypeHold;
		} else if (mTriggerswList.get(trigMode).equals(TRIGGERSW_NONE)) {
			sendTrigMode = TecRfidSuite.TrigModeTypeNone;
		}
		return sendTrigMode;
	}

	private int getSendEnergy(int energy) {
		int sendEnergy = TecRfidSuite.ENG_NON;
		if (mEnergyList.get(energy).equals(ENERGY_OFF)) {
			sendEnergy = TecRfidSuite.ENG_NON;
		} else if (mEnergyList.get(energy).equals(ENERGY_ON)) {
			sendEnergy = TecRfidSuite.ENG_SAV;
		}
		return sendEnergy;
	}

	/**
	 * 送信するタグ読取モード設定値を設定
	 *
	 * @param tagReadMode
	 * @return 送信するタグ読取モード
	 */
	private void setSendTagReadMode(int tagReadMode) {
        if (MenuDeviceActivity.getSDKLibrary().getIsAvailableTruncate()==TecRfidSuite.TruncateDecision.AVAILABLE_TRUNCATE_STATE.getInt()) {
            if (mTagReadModeList.get(tagReadMode).equals(TagReadModeTypeTruncate.FM0_SPEED_TYPE.getString())) {
                //FM0
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.FM0_TYPE.getInt();
            }
            else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeTypeTruncate.FM0_MEDIUM_SPEED_TYPE.getString())) {
                //FM0:中速
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.FM0_MEDIUM_SPEED_TYPE.getInt();
            }
            else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeTypeTruncate.MILLER2_LOW_SPEED_TYPE.getString())) {
                //Miller2:低速
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER2_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.LOW_SPEED_TYPE.getInt();
            }
            else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeTypeTruncate.MILLER2_HIGH_SPEED_TYPE.getString())) {
                //Miller2:高速
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER2_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt();
            }
            else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeTypeTruncate.MILLER4_LOW_SPEED_TYPE.getString())) {
                //Miller4:低速
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.LOW_SPEED_TYPE.getInt();
            }
            else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeTypeTruncate.MILLER4_HIGH_SPEED_TYPE.getString())) {
                //Miller4:高速
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt();
            }
            else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeTypeTruncate.MILLER8_LOW_SPEED_TYPE.getString())) {
                //Miller8:低速
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER8_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.LOW_SPEED_TYPE.getInt();
            }
            else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeTypeTruncate.MILLER8_HIGH_SPEED_TYPE.getString())) {
                //Miller8:高速
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER8_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt();
            }
        }
        else {
            if (mTagReadModeList.get(tagReadMode).equals(TagReadModeType.MILLER2_MEDIUM_SPEED_TYPE.getString())) {
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER2_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.MEDIUM_SPEED_TYPE.getInt();
            } else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeType.MILLER2_HIGH_SPEED_TYPE.getString())) {
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER2_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt();
            } else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeType.MILLER4_MEDIUM_SPEED_TYPE.getString())) {
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.MEDIUM_SPEED_TYPE.getInt();
            } else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeType.MILLER4_HIGH_SPEED_TYPE.getString())) {
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt();
            } else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeType.MILLER8_MEDIUM_SPEED_TYPE.getString())) {
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER8_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.MEDIUM_SPEED_TYPE.getInt();
            } else if (mTagReadModeList.get(tagReadMode).equals(TagReadModeType.MILLER8_HIGH_SPEED_TYPE.getString())) {
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER8_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.HIGH_SPEED_TYPE.getInt();
            } else {
                //default
                sendTagReadMillerType = TecRfidSuite.TagReadMillerType.MILLER4_TYPE.getInt();
                sendTagReadSpeedType = TecRfidSuite.TagReadSpeedType.MEDIUM_SPEED_TYPE.getInt();
            }
        }
	}

    /**
     * ファイル保存用自動周波数設定を取得
     *
     * @return 自動周波数設定
     */
    private String getAutoFrequencyFileSaveSetting() {
        StringBuilder autoFrequency = new StringBuilder();
        mAutoFrequencyList.clear();
        try {
            SharedPreferences prefs = getSharedPreferences(FrequencySelectActivity.SETTING_PREFS, Context.MODE_PRIVATE);
            autoFrequency.append(prefs.getString(FrequencySelectActivity.AUTO_FREQUENCY_CHANNEL_KEY, ""));
            if(autoFrequency.length()==0) {
                createDefaultAutoFrequencyList();
                for(int i=0;i<mAutoFrequencyList.size();i++) {
                    if(i==0) {
                        autoFrequency.append(mAutoFrequencyList.get(i));
                    }
                    else {
                        autoFrequency.append(COMMA);
                        autoFrequency.append(mAutoFrequencyList.get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return autoFrequency.toString();
    }
    /**
     * デバイス設定用の自動周波数を設定
     *
     */
    private void setSendAutoFrequencySetting() {
        String autoFrequency = "";
        mAutoFrequencyList.clear();
        try {
            SharedPreferences prefs = getSharedPreferences(FrequencySelectActivity.SETTING_PREFS, Context.MODE_PRIVATE);
            autoFrequency = prefs.getString(FrequencySelectActivity.AUTO_FREQUENCY_CHANNEL_KEY, "");
            //設定なしの場合、デフォルト設定
            if(autoFrequency.length()==0) {
                createDefaultAutoFrequencyList();
                ArrayList<Integer> autoFrequencyList = new ArrayList<Integer>();
                for(int i=0;i<mAutoFrequencyList.size();i++) {
                    Integer val = mAutoFrequencyList.get(i);
                    if(val != FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP) {
                        autoFrequencyList.add(val);
                    }
                }
                mAutoFrequencyList = autoFrequencyList;
            }
            else {
                String[] list = autoFrequency.split(COMMA, 0);
                for(int i=0;i<list.length;i++) {
                    Integer val = Integer.parseInt(list[i]);
                    if(val != FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP) {
                        mAutoFrequencyList.add(val);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 自動周波数設定を保存する
     *
     * @param autoFrequency 自動周波数設定
     */
    private void saveAutoFrequencySetting(String autoFrequency) {
        try {
            SharedPreferences prefs = getSharedPreferences(FrequencySelectActivity.SETTING_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            if(autoFrequency.length() == 0) {
                editor.remove(FrequencySelectActivity.AUTO_FREQUENCY_CHANNEL_KEY).commit();
            }
            else {
                editor.putString(FrequencySelectActivity.AUTO_FREQUENCY_CHANNEL_KEY,autoFrequency);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * デバイスから取得した自動周波数設定を設定
     *
     * @param autoFrequencyList デバイスから取得した自動周波数設定
     */
    private void setDeviceAutoFrequencySetting(ArrayList<Integer> autoFrequencyList) {
        if(autoFrequencyList == null) {
            return;
        }
        StringBuilder autoFrequency = new StringBuilder();
        while (autoFrequencyList.size()!=12) {
            autoFrequencyList.add(FrequencySelectActivity.FREQUENCY_CHANNEL_SKIP);
        }
        for(int i=0;i<autoFrequencyList.size();i++) {
            if(i==0) {
                autoFrequency.append(autoFrequencyList.get(i));
            }
            else {
                autoFrequency.append(COMMA);
                autoFrequency.append(autoFrequencyList.get(i));
            }
        }
        saveAutoFrequencySetting(autoFrequency.toString());
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
	private void showDialog(final String title, final String message, final String btn1Txt, final String btn2Txt, final Runnable positiveRun,  final Runnable negativeRun) {
		if (null != mShowDialogHandler) {
			mShowDialogRunnable = new Runnable() {
				@Override
				public void run() {
					mDialog = new AlertDialog.Builder(SettingActivity.this);
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
	 * エラーダイアログ表示
	 *
	 * @param title 表示タイトル
	 * @param message 表示メッセージ
	 * @param btn1Txt ボタン1
	 * @param btn2Txt ボタン2(不要ならnull)
	 * @param isBack 前の画面に戻るか
	 */
	private void showDialog(final String title, final String message, final String btn1Txt, final String btn2Txt, final boolean isBack) {
		if (null != mShowDialogHandler) {
			mShowDialogRunnable = new Runnable() {
				@Override
				public void run() {
					mDialog = new AlertDialog.Builder(SettingActivity.this);
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
					if(isBack) {
						mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
								finish();
							}
						});
					}
					mDialog.show();
				}
			};
			mShowDialogHandler.post(mShowDialogRunnable);
		}
	}
}
