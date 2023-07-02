package com.example.seminar_manage_showroom_app.connect;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.seminar_manage_showroom_app.api.HttpPostRfidScan;
import com.example.seminar_manage_showroom_app.api.HttpPostRfidSearch;
import com.example.seminar_manage_showroom_app.common.Config;
import com.example.seminar_manage_showroom_app.common.interfaces.Callable;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ConnectThreadSearch extends Thread {
    Callable mCallable;
    UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothSocket bTSocket;
    private HashSet<String> listTag = new HashSet<>();
    private static boolean isConnection = false;
    public static Thread thread;

    public static String rfid ="" ;
    Set<String> setCustom = new HashSet<>();
    public boolean connect(BluetoothDevice bTDevice, Fragment mContext, Callable callable) {
        this.mCallable = callable;
        BluetoothSocket temp = null;
        try{
            temp = bTDevice.createRfcommSocketToServiceRecord(mUUID);
            bTSocket = temp;
        } catch (IOException e) {
            Log.d("CONNECTTHREAD", "Could not create RFCOMM socket:" + e.toString());
            return false;
        }
        try {
            bTSocket.connect();
            isConnection = true;
            if(isConnection == true){
                callable.call(true);
            }
            thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (isConnection) {
                        try {

                            // length buffer
                            byte[] buffer = new byte[2048];

                            // stop the process for 1 second to catch the signal from the user
                            try {
                                Thread.sleep(500);
                            } catch (Exception e) {
                            }

                            InputStream inputStream = bTSocket.getInputStream();

                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                            while(reader.ready()){
                                String line  = reader.readLine();
                                if(line.contains("~eT")) {
                                    setCustom.add(line.substring(7));
                                }
                            }
                            JSONArray jsonArray;
                            if(!setCustom.isEmpty()) {
                                try {
                                    jsonArray = new JSONArray();
                                    for (String i : setCustom) {
                                        jsonArray.put(i);
                                    }
                                    if (jsonArray.length() != 0) {
                                        new HttpPostRfidSearch(mContext).execute(Config.CODE_LOGIN,Config.HTTP_SERVER_SHOP+Config.API_ODOO_GETMULTIPLEPRODUCT, jsonArray.toString());
                                        setCustom.clear();
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            isConnection = false;
                            System.out.println("Error read data: " + e.toString());
                        }
                    }
                }
            });
            thread.start();
        } catch (IOException e) {
            Log.d("CONNECTTHREAD", "Could not connect: " + e.toString());
            try {
                bTSocket.close();
            } catch (IOException close) {
                Log.d("Try close socket", "Could not close connection:" + e.toString());
                return false;
            }
        }

        return true;
    }

    public boolean cancel() {
        try {
            if(isConnection==true){
                isConnection = false;
                bTSocket.close();
            }
        } catch (IOException e) {
            Log.d("CONNECTTHREAD", "Could not close connection:" + e.toString());
            return false;
        }
        return true;
    }

}

