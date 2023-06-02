
package com.example.seminar_manage_showroom_app.log;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.SyncFailedException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import jp.co.toshibatec.TecRfidSuite;

public class Log {

    private static String LOGDIR = Environment.getExternalStorageDirectory().getPath() + "/TEC/tool/Log/";
    private static String SDFILE = LOGDIR + "SearchSampleToolLog.txt";
    private static String SDFILE_BACK = LOGDIR + "SearchSampleToolLog.bak";
    private static int nowLevel = TecRfidSuite.LOG_LEVEL_NONE;
    private static int logOutPut = 0;
    private static int maxFileSize = 1024 * 10;
    private static final int MAXLOGSIZE = 1024 * 999;
    private static final int MINLOGSIZE = 1024;

    /**
     * ストレージパス取得
     * @param storagePath
     */
    public static void setStoragePath(String storagePath){
        LOGDIR = storagePath + "/TEC/tool/Log/";
        SDFILE = LOGDIR + "SearchSampleToolLog.txt";
        SDFILE_BACK = LOGDIR + "SearchSampleToolLog.bak";
    }
    /**
     * @param nowLevel セットする nowLevel
     */
    public static void setNowLevel(int nowLevel) {
        Log.nowLevel = nowLevel;
    }

    /**
     * @param logOutPut セットする logOutPut
     */
    public static void setLogOutPut(int logOutPut) {
        Log.logOutPut = logOutPut;
    }

    /**
     * @param maxFileSize セットする maxFileSize
     */
    public static void setMaxFileSize(int maxFileSize) {
        if (maxFileSize > MAXLOGSIZE) {
            Log.maxFileSize = MAXLOGSIZE;
        } else if (maxFileSize < MINLOGSIZE) {
            Log.maxFileSize = MINLOGSIZE;
        } else {
            Log.maxFileSize = maxFileSize;
        }
    }

    /**
     * エラーログを出力する
     *
     * @param text ログテキスト
     */
    static public void error(String text) {
        print(TecRfidSuite.LOG_LEVEL_ERROR, "ERROR : " + text);
    }

    /**
     * Infoログを出力する
     *
     * @param text ログテキスト
     */
    public static void info(String text) {
        print(TecRfidSuite.LOG_LEVEL_INFO, "INFO  : " + text);
    }

    /**
     * デバッグログを出力する
     *
     * @param text ログテキスト
     */
    static public void debug(String text) {
        print(TecRfidSuite.LOG_LEVEL_DEBUG, "DEBUG : " + text);
    }

    static private void print(int printLevel, String text) {
        if (nowLevel == TecRfidSuite.LOG_LEVEL_NONE) {
            return;
        }
        if (nowLevel < printLevel) {
            return;
        }

        text = makeMessage(text);

        if (logOutPut == 0) {
            // コンソールに出力
            writeToConsole(printLevel, text);

            // ファイルにも出力
            writeToSD(text);
        } else if (logOutPut == 1) {
            // コンソールに出力
            writeToConsole(printLevel, text);
        } else {
            // ファイルに出力
            writeToSD(text);
        }
    }

    private static String makeMessage(String text) {
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        return ste[3].getMethodName() + "(" + ste[3].getFileName() + ":" + ste[3].getLineNumber() + ") " + text;
    }

    private static void writeToConsole(int printLevel, String text) {
        switch (printLevel) {
        case TecRfidSuite.LOG_LEVEL_ERROR:
            android.util.Log.e("Log", text);
            break;
        case TecRfidSuite.LOG_LEVEL_INFO:
            android.util.Log.i("Log", text);
            break;
        case TecRfidSuite.LOG_LEVEL_DEBUG:
            android.util.Log.d("Log", text);
            break;
        default:
            break;
        }
    }

    // SDに書く
    private static void writeToSD(String text) {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        // 書き込みデータを作る
        String writeStr = "" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
                + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE)
                + ":" + cal.get(Calendar.SECOND) + "." + cal.get(Calendar.MILLISECOND) + "\t" + text + "\n";

        BufferedWriter bw = null;
        FileOutputStream file = null;
        try {
            try {
                try {
                    mkdir_p(LOGDIR);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                // サイズチェック
                logSizeCheck(writeStr.getBytes("UTF-8").length);

                file = new FileOutputStream(SDFILE, true);
                bw = new BufferedWriter(new OutputStreamWriter(file, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                if (bw != null) {
                    bw.append(writeStr);
                    bw.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (file != null) {
                    file.getFD().sync();
                }
            } catch (SyncFailedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void logSizeCheck(int writeSize) {
        // 最大ログサイズを超えそうなら書き込むログファイルをチェンジして、
        // 古いログを削除する
        File logFile = new File(SDFILE);
        if (logFile.exists() == false) {
            // ファイルが無ければ何もしない。
            return;
        }

        if (logFile.length() + writeSize < maxFileSize * 1024L) {
            // 現在のファイルサイズ＋書き込みサイズが、指定の最大ログサイズより小さい場合は何もしない。
            return;
        }

        // Bakファイルがあれば削除する
        File logFileBak = new File(SDFILE_BACK);
        if (logFileBak.exists()) {
            if (logFileBak.delete() == false) {
                android.util.Log.e("Log", "bakファイルデリート失敗");
            }
        }

        // ファイル名をBakに変更
        if (logFile.renameTo(logFileBak) == false) {
            android.util.Log.e("Log", "bakファイルリネーム失敗");
        }
    }

    private static boolean mkdir_p(File dir) throws IOException {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("File.mkdirs() failed.");
            }
            return true;
        } else if (!dir.isDirectory()) {
            throw new IOException("Cannot create path. " + dir.toString() + " already exists and is not a directory.");
        } else {
            return false;
        }
    }

    private static boolean mkdir_p(String dir) throws IOException {
        return mkdir_p(new File(dir));
    }
}
