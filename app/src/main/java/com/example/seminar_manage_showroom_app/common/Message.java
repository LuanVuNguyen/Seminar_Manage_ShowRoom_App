package com.example.seminar_manage_showroom_app.common;

/**
 * Message and error code
 *
 * @author cong-pv
 * @since 2019-03-01
 */

public class Message {

    /**
     * Code 200
     */
    public static final String CODE_200 = "200";

    /**
     * Code 401
     */
    public static final String CODE_401 = "401";

    /**
     * Code 402
     */
    public static final String CODE_402 = "402";

    /**
     * Code 404
     */
    public static final String CODE_404 = "404";

    /**
     * Code 500
     */
    public static final String CODE_500 = "500";

    /**
     * Message 401
     */
    public static final String MESSAGE_401 = "ユーザとパスワードが不正です。";

    /**
     * Message 402
     */
    public static final String MESSAGE_402 = "未登録端末です。";

    /**
     * Message 404
     */
    public static final String MESSAGE_404_Backup = "サービスが見つかりません。";

    public static final String MESSAGE_404 = "APIKEYが一致しません。";

    /**
     * Message 500
     */
    public static final String MESSAGE_500 = "インターナルサーバエラー。";

    /**
     * Message result empty
     */
    public static final String MESSAGE_RESULT_EMPTY = "戻りデータがありません。";

    /**
     * Message select  Yes
     */
    public static final String MESSAGE_SELECT_YES = "YES";

    /**
     * Message select No
     */
    public static final String MESSAGE_SELECT_NO = "NO";

    /**
     * Message when download data
     */
    public static final String MESSAGE_DOWNLOAD_DATA_SCREEN = "データダウンロード中ー。。。";

    /**
     * Message when import data
     */
    public static final String MESSAGE_IMPORT_DATA_SCREEN = "データ取り込み中ー。。。";

    /**
     * Message when loading data
     */
    public static final String MESSAGE_LOADING_SCREEN = "ロード中。。。";

    /**
     * Message when upload log to GCS
     */
    public static final String MESSAGE_UPLOAD_LOG_SCREEN = "ログファイルをアップロードしています。。。";

    /**
     * Message when upload csv to GCS
     */
    public static final String MESSAGE_UPLOAD_CSV_SCREEN = "ファイルをアップロードしています。";
    public static final String MESSAGE_NO_DATA = "送信データがありません。";

    /**
     * Message when download apk from GCS
     */
    public static final String MESSAGE_CONFIRM_UPDATE = "新規バージョンができました。\nダウンロードしたいですか。";
    public static final String MESSAGE_DOWNLOAD = "ダウンロードしています。\nお待ちください。";
    public static final String MESSAGE_UPDATE = "UPDATING....";

    public static final String MESSAGE_CONFIRM_UPLOAD = "新規バージョンをダウンロードする前にデータベースのデータをすべてサーバに送信しなければならない。\nすぐに送信したいですか。";
    public static final String MESSAGE_UPLOAD = "データを送信しています。\nお待ち下さい。";

    public static final String MESSAGE_CONFIRM_LOGOUT = "ログアウトしますか。";

    /**
     * show message when click button logout
     */
    public static final String MESSAGE_LOGOUT = "ログアウト。";

    /**
     * Check input empty
     */
    public static final String MESSAGE_CHECK_INPUT_EMPTY = "%sを入力してください。";

    /**
     * Message connect to network
     */
    public static final String MESSAGE_NETWORK_ERR = "ネットワークに\n接続できませんでした。";

    /**
     * Message wrong password
     */
    public static final String MESSAGE_PASSWORD_ERR = "パスワードが不正です。";

    /**
     * Message not register devices
     */
    public static final String MESSAGE_NOT_REGISTER_DEVICES = "未登録端末です。";

    /**
     * Message reload
     */
    public static final String MESSAGE_RELOAD = "\nリロードを行います。よろしいですか？";

    /**
     * Message TAG Activity start
     */
    public static final String MESSAGE_ACTIVITY_START = "処理開始。";

    /**
     * Message Activity end
     */
    public static final String MESSAGE_ACTIVITY_END = "処理完了。";

    /**
     * Login Activity Name
     */
    public static final String LOGIN_ACTIVITY_NAME = "ログイン画面";

    /**
     * SCANNER ACTIVITY Name
     */
    public static final String SCANNER_ACTIVITY_NAME = "バーコードスキャン用カメラ画面";

    /**
     * UNLOCK ACTIVITY Name
     */
    public static final String UNLOCK_ACTIVITY_NAME = "ロック解除画面";

    /**
     * Message TAG Activity move
     */
    public static final String MESSAGE_ACTIVITY_MOVE = "%sから%sに遷移する。";

    /**
     * Login Activity tag
     */
    public static final String TAG_LOGIN_ACTIVITY = "ログイン画面：";

    /**
     * SCANNER ACTIVITY tag
     */
    public static final String TAG_SCANNER_ACTIVITY = "バーコードスキャン用カメラ画面：";

    /**
     * UNLOCK ACTIVITY tag
     */
    public static final String TAG_UNLOCK_ACTIVITY = "ロック解除画面：";

    /**
     * Message use when login success
     */
    public static final String MESSAGE_LOGIN_SUCCESS = "ロードに成功しました。（ユーザ：%s、店舗コード：%s、サーバー名：%s）";

    /**
     * Message loading data from server
     */
    public static final String LOADING_DATA_FROM_SERVER = "サーバからのロード中。";

    /**
     * Message complete loading data from server
     */
    public static final String LOADING_DATA_FROM_SERVER_SUCCESS = "サーバからのロード完了。";

    /**
     * Message data count summary
     */
    public static final String MESSAGE_LOADING_DATA_NUMBER = "データが%s件ロードできました。";


    public static final String MESSAGE_RETRY = "リトライ";

    public static final String MESSAGE_CANCEL = "キャンセル";

    public static final String MESSAGE_USER_ID = "ユーザID";
    public static final String MESSAGE_PASSWORD = "パスワード";

    public static final String MESSAGE_CONFIRM_DELETE_RECORD = "このレコードを削除しますか?";
    public static final String MESSAGE_CONFIRM_DELETE_ALL = " Are you sure you want to delete all data?？";


    public static final String MESSAGE_CONFIRM_INSERT_DATABASE ="20レコードを超えてスキャンできました。\n データベースに格納して継続します。";

    // #HUYNHQUANGVINH message limit insert record database
    public static final String MESSAGE_CONFIRM_LIMIT_INSERT_DATABASE ="レコードを超えてスキャンできました。\n データベースに格納して継続します。";

    public static final String MESSAGE_PROCESS_INSERT_DATABASE ="データベースに格納しています。";
    public static final String MESSAGE_YES = "ＯＫ";
    public static final String NOTIFICATION_NO_RECORD_IN_DATABASE = "検索対象商品がありません。";
    public static final String NOTIFICATION_NO_RECORD_IN_LIST_VIEW = "商品がないので格納できません。";
    public static final String NOTIFICATION_BARCODE_INVALID = "スキャンしたバーコードが不正です。";
    public static final String NOTIFICATION_MAGAZINE_CODE_INVALID = "再度スキャンしてください\n雑誌コード%s";
    public static final String NOTIFICATION_BARCODE_CONFIRM = "最新の商品をスキャン完了しません。";
    public static final String NOTIFICATION_LIMITED_DATA = "登録件数が1万件となりました \n送信の必要がありますので、データを格納します。";
    public static final String NOTIFICATION_SEND_DATA = "スキャンデータが上限の１万件に達成しました。\nデータを送信する必要があります。送信しますか?";

    public static final String NOTIFICATION_USER_NOT_CONNECT_TO_WEB = "ログインできません。";

    public static final String MESSAGE_CONFIRM_CHANGE_MODE = "Do you want to change mode?";
    public static final String MESSAGE_CONFIRM_REGISTER_DATA = "未登録データ。 保存しますか？";
    public static final String MESSAGE_CONFIRM_OVER_DATA = "Data limit reached __ records. You must save this data to continue.";
    public static final String MESSAGE_CONFIRM_EXPORT_DATA = "do you want to export the data?";
    public static final String MESSAGE_CONFIRM_REMOVE_ADD_DATA = "Delete all data after export??";
    public static final String MESSAGE_CONFIRM_SAVE_DATA = "この動作を行う前に現在のスキャンデータを格納してください。\n 登録しますか。";
    public static final String NOT_REGISTER_DATA = "NO";
    public static final String YES_REGISTER_DATA = "YES";

    public static final String MESSAGE_CONFIRM_DELETE_ALL_SEARCH = "検索されたデータを全削除します。\n よろしいですか？";  // SA-228 - ADD START

}
