package com.example.shetu.models;

import com.example.shetu.Assets.MyPackages;
import com.example.shetu.Assets.PackagesInfo;

import java.util.List;

public class ActivePackagesResponse {

    private List<MyPackages> data;
    private String status;

    public ActivePackagesResponse(List<MyPackages> data, String status) {
        this.data = data;
        this.status = status;
    }

    public List<MyPackages> getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }
}
