package com.example.seminar_manage_showroom_app.common;

public class Config {
    //public static String HTTP_SERVER_SHOP = "http://192.168.1.56:8066";
    public static String HTTP_SERVER_SHOP = "http://192.168.2.10:8069";
    //public static String HTTP_SERVER_SHOP = null;
    /**
     * Method POST
     */
    public static final String METHOD_POST = "POST";
    /**
     * Property key
     */
    public static final String PROPERTY_KEY = "Content-Type";
    /**
     * Property value
     */
    //static final String PROPERTY_VALUE = "application/x-www-form-urlencoded";
    public static final String PROPERTY_VALUE = "application/json";
    /**
     * Property value post file
     */
    public static final String PROPERTY_VALUE_POST_FILE = "multipart/form-data";

    /**
     * Api key
     */
    public static final String DATABASE_NAME = "odoo_v2";
    /**
     * Api key value
     */
    public static final String API_KEY_VALUE = "aip_rtsa_20220516_1";
    /**
     * API code login
     */
    public static final String CODE_LOGIN = "1";
    /**
     * #HUYNHQUANGVINH API RFID TO JAN
     */
    //public static final String API_RFID_TO_JAN = HTTP_SERVER_SHOP + "/api/v2/rfids_to_jans";
    public static final String API_ODOO_GETMULTIPLEPRODUCT = "/inventory_controller/get_quant";
    public static final String API_LOGIN = "/web/session/authenticate";
    public static final String API_INFO_USER = "/inventory_controller/get_user_info";
    public static final String API_CREATE_PRODUCT = "/inventory_controller/creat_new_product";
    public static final String API_GET_ALL_PRODUCT = "/inventory_controller/get_info_all_product";
    public static final String API_EDIT_PROFILE_USER = "/inventory_controller/edit_user_info";
    public static final String API_LOGOUT = "/web/session/destroy";
    public static final String API_PAY_PRODUCT = "/inventory_controller/pay_product";

}
