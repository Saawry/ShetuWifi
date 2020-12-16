package com.example.shetu.models;

import com.example.shetu.Assets.PackagesInfo;

import java.util.List;

public class AllPkgInfoResponse {

    private List<PackagesInfo> data;
    private String status;

    public AllPkgInfoResponse(List<PackagesInfo> data, String status) {
        this.data = data;
        this.status = status;
    }

    public List<PackagesInfo> getData() {
        return data;
    }

    public void setData(List<PackagesInfo> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
