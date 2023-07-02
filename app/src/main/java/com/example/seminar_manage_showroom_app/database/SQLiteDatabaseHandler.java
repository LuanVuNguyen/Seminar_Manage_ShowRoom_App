package com.example.seminar_manage_showroom_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.seminar_manage_showroom_app.common.entities.InforBookEntity;
import com.example.seminar_manage_showroom_app.common.entities.InforMemberEntity;
import com.example.seminar_manage_showroom_app.common.entities.InforProductEntity;
import com.example.seminar_manage_showroom_app.common.interfaces.Callable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {


    private Callable callable;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "library_db";

    // Country table name
    private static final String TABLE_PRODUCT= "book_info";
    //table product_info
    private static final String TABLE_PRODUCT2= "product_info";
    private static String SHELF_CODE = "ShelfCode";
    private static String GOOD_NAME = "goodName";
    private static String BARCODE_01 = "BarcodeCD1";
    private static String BARCODE_02 = "BarcodeCD2";
    private static String BASE_PRICE = "BasePrice";
    private static String TAX_INCLUDE_PRICE = "TaxIncludePrice";
    private static String QUANTITY = "Quantity";

    private static String RFID_CODE = "RfidCode";
    private static String TYPE_TABLE= "TypeProduct";
    private static String DATE="Date";
    private static String IVT_NAME="InventoryName";
    private static String SERIAL_CODE="Serial";
    private static String ID="id";
    // Country Table Columns names
    private static String BOOK_TITLE = "bookTitle";
    private static String ISBN13 = "Isbn13";
    private static String RFIDCODE = "Rfid";
    private static String AUTHOR= "Author";
    private static String CATEGORIES="Category";
    private static final String TABLE_PRODUCT1 = "member_info";
    private static String RFIDMEMBER = "RFIDMEMBER";
    private static String NAME = "NAME";
    private static String MEMBERID = "MEMBERID";
    private static String GENDER = "GENDER";
    private static String MEMBERSHIP = "MEMBERSHIP";
    private static String CONTACT = "CONTACT";

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COUNTRY_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "( "
                + RFIDCODE + " TEXT primary key,"
                + BOOK_TITLE + " TEXT,"
                + ISBN13 + " TEXT,"
                + AUTHOR + " TEXT,"
                + CATEGORIES + " TEXT"+ ")";
        String TABLE2="CREATE TABLE " + TABLE_PRODUCT1+ "("
                + RFIDMEMBER + " TEXT PRIMARY KEY,"
                + NAME + " TEXT,"
                + MEMBERID + " TEXT,"
                + GENDER + " TEXT,"
                + MEMBERSHIP + " TEXT,"
                + CONTACT + " TEXT"+ ")";
        String TABLE3 = "CREATE TABLE " + TABLE_PRODUCT2 + "("
                + RFID_CODE + " TEXT PRIMARY KEY,"
                + GOOD_NAME + " TEXT,"
                + TYPE_TABLE + " TEXT,"
                + DATE + " TEXT,"
                + IVT_NAME + " TEXT,"
                + SERIAL_CODE + " TEXT,"
                + BARCODE_01 + " TEXT,"
                + BARCODE_02 + " TEXT,"
                + BASE_PRICE + " INTEGER,"
                + TAX_INCLUDE_PRICE + " INTEGER,"
                + QUANTITY + " INTEGER,"
                + CATEGORIES + " TEXT"+ ")";
        db.execSQL(CREATE_COUNTRY_TABLE);
        db.execSQL(TABLE2);
        db.execSQL(TABLE3);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT2);
        // Create tables again
        onCreate(db);
    }
    public void deleteTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new country
    public void insertProduct(InforBookEntity product) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(BOOK_TITLE, product.getBooktitle());
            values.put(AUTHOR, product.getAuthor());
            values.put(ISBN13, product.getIsbn13());
            values.put(CATEGORIES, product.getCategories());
            values.put(RFIDCODE, product.getRfidCode());
            // Inserting Row
            db.insert(TABLE_PRODUCT, null, values);
            db.close(); // Closing database connection
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public List<InforBookEntity> getAllProducts() {
        List<InforBookEntity> pList = new ArrayList<InforBookEntity>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                InforBookEntity productEntity = new InforBookEntity();
                productEntity.setRfidCode(cursor.getString(0));
                productEntity.setBooktitle(cursor.getString(1));
                productEntity.setIsbn13(cursor.getString(2));
                productEntity.setAuthor(cursor.getString(3));
                productEntity.setCategories(cursor.getString(4));
                // Adding country to list
                pList.add(productEntity);
            } while (cursor.moveToNext());
        }

        // return country list
        return pList;
    }
    public List<InforProductEntity> getAllProductsinv() {
        List<InforProductEntity> pList = new ArrayList<InforProductEntity>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT2;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                InforProductEntity productEntity = new InforProductEntity();
                productEntity.setRfidCode(cursor.getString(0));
                productEntity.setGoodName(cursor.getString(1));
                productEntity.setTypeProduct(cursor.getString(2));
                productEntity.setDate(cursor.getString(3));
                productEntity.setInventoryName(cursor.getString(4));
                productEntity.setSerial(cursor.getString(5));
                productEntity.setBarcodeCD1(cursor.getString(6));
                productEntity.setBarcodeCD2(cursor.getString(7));
                productEntity.setBasePrice(Integer.parseInt(cursor.getString(8)));
                productEntity.setTaxIncludePrice(Integer.parseInt(cursor.getString(9)));
                productEntity.setQuantity(Integer.parseInt(cursor.getString(10)));
                // Adding country to list
                pList.add(productEntity);
            } while (cursor.moveToNext());
        }

        // return country list
        return pList;
    }
    public List<InforProductEntity> getAllProductsinvbyType(String type) {
        List<InforProductEntity> pList = new ArrayList<InforProductEntity>();
        // Select All Query

        //String selectQuery = "SELECT  * FROM '" + TABLE_PRODUCT+"'WHERE'"+TYPE_TABLE+"'='"+type+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT2, new String[] { RFID_CODE,
                        GOOD_NAME, TYPE_TABLE,DATE,IVT_NAME,SERIAL_CODE,BARCODE_01, BARCODE_02, BASE_PRICE, TAX_INCLUDE_PRICE, QUANTITY,CATEGORIES}, TYPE_TABLE + " like ?",
                new String[] { String.valueOf("%"+type+"%") }, null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                InforProductEntity productEntity = new InforProductEntity();
                productEntity.setRfidCode(cursor.getString(0));
                productEntity.setGoodName(cursor.getString(1));
                productEntity.setTypeProduct(cursor.getString(2));
                productEntity.setDate(cursor.getString(3));
                productEntity.setInventoryName(cursor.getString(4));
                productEntity.setSerial(cursor.getString(5));
                productEntity.setBarcodeCD1(cursor.getString(6));
                productEntity.setBarcodeCD2(cursor.getString(7));
                productEntity.setBasePrice(Integer.parseInt(cursor.getString(8)));
                productEntity.setTaxIncludePrice(Integer.parseInt(cursor.getString(9)));
                productEntity.setQuantity(Integer.parseInt(cursor.getString(10)));
                productEntity.setCategory(cursor.getString(11));
                // Adding country to list
                pList.add(productEntity);
            } while (cursor.moveToNext());
        }

        // return country list
        return pList;
    }
    public int getProductsinvbyTypeCount(String type) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_PRODUCT2 + " WHERE "+ TYPE_TABLE +" like ?",new String[]{ String.valueOf("%"+type+"%")});
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }
    public List<InforMemberEntity> getMember() {
        List<InforMemberEntity> pList = new ArrayList<InforMemberEntity>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT1;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                InforMemberEntity memberEntity = new InforMemberEntity();
                memberEntity.setRfid(cursor.getString(0));
                memberEntity.setMember_ID(cursor.getString(1));
                memberEntity.setName(cursor.getString(2));
                memberEntity.setGender(cursor.getString(3));
                memberEntity.setCurrent_membership(cursor.getString(4));
                memberEntity.setContact(cursor.getString(5));
                // Adding country to list
                pList.add(memberEntity);
            } while (cursor.moveToNext());
        }

        // return country list
        return pList;
    }
    public void deleteAllProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT,null,null);
        db.close();
    }
    public void deleteMember() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT1,null,null);
        db.close();
    }
    public List<InforBookEntity> getAllRfid() {
        List<InforBookEntity> pList = new ArrayList<InforBookEntity>();
        // Select All Query
        String selectQuery = "SELECT "+ RFIDCODE + " FROM " + TABLE_PRODUCT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                InforBookEntity productEntity = new InforBookEntity();
                productEntity.setRfidCode(cursor.getString(0));
                // Adding country to list
                pList.add(productEntity);
            } while (cursor.moveToNext());
        }

        // return country list
        return pList;
    }

    // Adding new country
    public void insertAllProducts(LinkedList<InforBookEntity> products) {
        SQLiteDatabase db = this.getWritableDatabase();

        for(InforBookEntity product : products){
            ContentValues values = new ContentValues();
            values.put(RFIDCODE, product.getRfidCode());
            values.put(BOOK_TITLE, product.getBooktitle());
            values.put(ISBN13, product.getIsbn13());
            values.put(CATEGORIES, product.getCategories());
            values.put(AUTHOR, product.getAuthor());

            // Inserting Row
            db.insert(TABLE_PRODUCT, null, values);
        }
        db.close(); // Closing database connection
    }

    // Adding new country
    public void insertMember(LinkedList<InforMemberEntity> members) {
        SQLiteDatabase db = this.getWritableDatabase();

        for(InforMemberEntity member : members){
            ContentValues values = new ContentValues();
            values.put(RFIDMEMBER, member.getRfid());
            values.put(NAME, member.getName());
            values.put(MEMBERID, member.getMember_ID());
            values.put(GENDER, member.getGender());
            values.put(MEMBERSHIP, member.getCurrent_membership());
            values.put(CONTACT, member.getContact());

            // Inserting Row
            db.insert(TABLE_PRODUCT1, null, values);
        }
        db.close(); // Closing database connection
    }
    public void insertAllProductsinvCallBack(LinkedList<InforProductEntity> products, Callable callable) {
        this.callable = callable;
        SQLiteDatabase db = this.getWritableDatabase();
        for(InforProductEntity product : products){
            ContentValues values = new ContentValues();
            values.put(RFID_CODE, product.getRfidCode());
            values.put(GOOD_NAME, product.getGoodName());
            values.put(TYPE_TABLE, product.getTypeProduct());
            values.put(DATE, product.getDate());
            values.put(IVT_NAME, product.getCategory());
            values.put(SERIAL_CODE, product.getSerial());
            values.put(BARCODE_01, product.getBarcodeCD1());
            values.put(BARCODE_02, product.getBarcodeCD2());
            values.put(BASE_PRICE, product.getBasePrice());
            values.put(TAX_INCLUDE_PRICE, product.getTaxIncludePrice());
            values.put(QUANTITY, product.getQuantity());
            values.put(CATEGORIES, product.getCategory());
            // Inserting Row
            db.insert(TABLE_PRODUCT2, null, values);
        }
        callable.call(true);
        db.close(); // Closing database connection
    }
    public void insertAllProductsinv(LinkedList<InforProductEntity> products) {
        SQLiteDatabase db = this.getWritableDatabase();

        for(InforProductEntity product : products){
            ContentValues values = new ContentValues();
            values.put(RFID_CODE, product.getRfidCode());
            values.put(GOOD_NAME, product.getGoodName());
            values.put(TYPE_TABLE, product.getTypeProduct());
            values.put(DATE, product.getDate());
            values.put(IVT_NAME, product.getCategory());
            values.put(SERIAL_CODE, product.getSerial());
            values.put(BARCODE_01, product.getBarcodeCD1());
            values.put(BARCODE_02, product.getBarcodeCD2());
            values.put(BASE_PRICE, product.getBasePrice());
            values.put(TAX_INCLUDE_PRICE, product.getTaxIncludePrice());
            values.put(QUANTITY, product.getQuantity());
            values.put(CATEGORIES, product.getCategory());
            // Inserting Row
            db.insert(TABLE_PRODUCT2, null, values);
        }
        db.close(); // Closing database connection
    }
    public void deleteAllProductsinvbyTypeTable(String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT2, TYPE_TABLE + " like ?",
                new String[] { String.valueOf("%"+type+"%") });
        db.close();
    }
}