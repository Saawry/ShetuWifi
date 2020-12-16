package com.example.shetu.models;

import com.google.gson.annotations.SerializedName;

public class DfltResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private String data;

    public DfltResponse(String status, String data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
