package com.example.shetu.models;

import android.net.NetworkInfo;

import com.example.shetu.Assets.LocalNetDetails;

public class GetnetResponse {

    private LocalNetDetails data;
    private String status;


    public GetnetResponse(LocalNetDetails data, String status) {
        this.data = data;
        this.status = status;
    }

    public LocalNetDetails getData() {
        return data;
    }

    public void setData(LocalNetDetails data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
