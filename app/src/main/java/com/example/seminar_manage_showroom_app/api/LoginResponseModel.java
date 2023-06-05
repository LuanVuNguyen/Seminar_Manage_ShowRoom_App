package com.example.seminar_manage_showroom_app.api;
import com.google.gson.annotations.SerializedName;

public class LoginResponseModel {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // Getters and setters for the fields

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
