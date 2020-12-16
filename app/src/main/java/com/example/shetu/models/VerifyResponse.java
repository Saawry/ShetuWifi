package com.example.shetu.models;


public class VerifyResponse {

    private boolean is_new_user;

    private String data;
    private String status;

    public VerifyResponse(boolean is_new_user, String data, String status) {
        this.is_new_user = is_new_user;
        this.data = data;
        this.status = status;
    }

    public boolean getIs_new_user() {
        return is_new_user;
    }

    public String getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }
}
