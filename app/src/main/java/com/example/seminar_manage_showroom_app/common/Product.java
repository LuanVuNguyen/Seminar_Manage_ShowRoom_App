package com.example.seminar_manage_showroom_app.common;

public class Product {
    private String name;
    private String x_RFID_PRODUCT;

    public Product(String name, String x_RFID_PRODUCT) {
        this.name = name;
        this.x_RFID_PRODUCT = x_RFID_PRODUCT;
    }

    public String getName() {
        return name;
    }

    public String getX_RFID_PRODUCT() {
        return x_RFID_PRODUCT;
    }
}