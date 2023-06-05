package com.example.seminar_manage_showroom_app.api;
import com.google.gson.annotations.SerializedName;

public class LoginRequestBodyModel {
    @SerializedName("jsonrpc")
    private String jsonrpc;

    @SerializedName("params")
    private Params params;

    // Constructor, getters, and setters

    public LoginRequestBodyModel(String jsonrpc, String db, String login, String password) {
        this.jsonrpc = jsonrpc;
        this.params = new Params(db, login, password);
    }

    public static class Params {
        @SerializedName("db")
        private String db;

        @SerializedName("login")
        private String login;

        @SerializedName("password")
        private String password;

        public Params(String db, String login, String password) {
            this.db = db;
            this.login = login;
            this.password = password;
        }
    }
}
