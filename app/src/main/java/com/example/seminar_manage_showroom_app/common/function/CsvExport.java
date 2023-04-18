//package com.example.seminar_manage_showroom_app.common.function;
//
//import android.content.Context;
//import android.os.Build;
//import android.os.Environment;
//
//import androidx.annotation.RequiresApi;
//
//import com.example.libraryapp.common.Config;
//import com.example.libraryapp.common.entities.InforProductEntity;
//import com.example.libraryapp.common.interfaces.Callable;
//import com.example.libraryapp.database.SQLiteDatabaseHandler;
//
//import com.example.libraryapp.Thread.HttpPostBase64;
//import com.example.libraryapp.common.Config;
//import com.opencsv.CSVWriter;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.Date;
//import java.util.List;
//import androidx.annotation.RequiresApi;
//
//import jxl.write.Label;
//
//public class CsvExport {
//    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
//    static SQLiteDatabaseHandler db;
//    static String file_name;
//    static String csvDir="/storage/emulated/";
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static void writeData(Context context, String[] header, String type, Callable callable){
//        db = new SQLiteDatabaseHandler(context);
//        List<String[]> csvData = new ArrayList<String[]>();
//
//        File sd = Environment.getExternalStorageDirectory();
//        File sdd=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//
//        csvData.add(header);
//
//        if(header.length==4){
//            file_name="/inventory_data";
//            for(InforProductEntity p : db.getAllProductsinvbyType("inventory")){
//                //System.out.println("luannnnnn"+ p);
//                String[] row = new String[]{p.getRfidCode(),String.valueOf(p.getBarcodeCD1()), p.getGoodName(), String.valueOf(p.getQuantity())};
//                csvData.add(row);
//            }
//        }else {
//            switch(type){
//                case "incoming":
//                    file_name="/incoming_data";
//                    for (InforProductEntity p : db.getAllProductsinvbyType("incoming")) {
//                        String[] row = new String[]{p.getSerial(), p.getInventoryName(), p.getRfidCode(), p.getGoodName(), String.valueOf(p.getQuantity())};
//                        csvData.add(row);
//                    }
//                    break;
//                case "outgoing":
//                    file_name="/outgoing_data";
//                    for (InforProductEntity p : db.getAllProductsinvbyType("outgoing")) {
//                    String[] row = new String[]{p.getSerial(), p.getInventoryName(), p.getRfidCode(), p.getGoodName(), String.valueOf(p.getQuantity()), p.getBarcodeCD1()};
//                    csvData.add(row);
//                }
//                    break;
//            }
//        }
//        System.out.println(csvData);
//        File directory = new File(sdd.getAbsolutePath()+file_name);
//
//        //create directory if not exist
//        if (!directory.isDirectory()) {
//            boolean rs = directory.mkdirs();
//            System.out.println(rs);
//        }
//
//        String csvFile = "/inventory_smartactive_"+sdf.format(new Date(System.currentTimeMillis()))+".csv";
//        String fileName = directory+csvFile;
//        System.out.println("tommy"+fileName);
//        File file = new File(fileName);
//        //FileWriter outputfile = new FileWriter(file);
//        CSVWriter writer = null;
//        try {
//            writer = new CSVWriter(new FileWriter(file));
//            writer.writeAll(csvData);
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        callable.call(true);
//        //endcode Base64
//        try {
//            String csv_base=encodeCSV(fileName);
//            new HttpPostBase64(context).execute(Config.CODE_LOGIN, Config.HTTP_SERVER_SHOP+Config.API_ODOO_CREATEINVENTORY, csv_base.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private static String encodeCSV(String path) throws Exception {
//        String csvString = null;
//        try {
//            FileInputStream fileInputStream = new FileInputStream(path);
//            byte[] data = readAllBytes(fileInputStream);
//            csvString = Base64.getEncoder().encodeToString(data);
//
//        } catch (Exception e) {
//            System.out.println("VULUAN: " + e.getMessage());
//        } finally {
//            return csvString;
//        }
//    }
//    public static byte[] readAllBytes(InputStream in) throws IOException {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        copyAllBytes(in, out);/*w w  w . ja  v  a 2s. co m*/
//        return out.toByteArray();
//    }
//
//    public static int copyAllBytes(InputStream in, OutputStream out)
//            throws IOException {
//        int byteCount = 0;
//        byte[] buffer = new byte[4096];
//        while (true) {
//            int read = in.read(buffer);
//            if (read == -1) {
//                break;
//            }
//            out.write(buffer, 0, read);
//            byteCount += read;
//        }
//        return byteCount;
//    }
//}
//
