//package com.example.seminar_manage_showroom_app.common.function;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.widget.Toast;
//
//import com.example.libraryapp.common.Constants;
//
//
//public class SupModRfidCommon {
//
//    public static final Toast ToastMessage(Context context, String message) {
//        return Toast.makeText(context, message, Toast.LENGTH_LONG);
//    }
//
//    public static final AlertDialog showNotifyErrorDialog(Context context) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setCancelable(true);
//        builder.setTitle("サーバーエラー");
//        builder.setMessage("サーバーに接続できませんでした");
//        builder.setPositiveButton("「はい」",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // nothing
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        return dialog;
//    }
//
//    public static final AlertDialog showNotifyDialog(Context context, String title, String message) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.setPositiveButton("「はい」",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // nothing
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        return dialog;
//    }
//
//    public static boolean isStatusHttpOk(String output) {
//        switch (output) {
//            case Constants.STT_CODE_BAD_REQUEST:
//            case Constants.STT_CODE_UNAUTHORIZED:
//            case Constants.STT_CODE_FORBIDDEN:
//            case Constants.STT_CODE_NOT_FOUND:
//            case Constants.STT_CODE_Method_Not_Allowed:
//            case Constants.STT_CODE_Proxy_Authentication_Required:
//            case Constants.STT_CODE_REQUEST_TIMEOUT:
//            case Constants.STT_CODE_CONFLICT:
//            case Constants.STT_CODE_SERVER_ERROR:
//            case Constants.STT_CODE_Not_Implemented:
//            case Constants.STT_CODE_Bad_Gateway:
//            case Constants.STT_CODE_Service_Unavailable:
//            case Constants.STT_CODE_Gateway_Timeout:
//                return false;
//        }
//        return true;
//    }
//}
