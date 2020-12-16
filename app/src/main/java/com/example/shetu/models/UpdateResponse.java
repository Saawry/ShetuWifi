package com.example.shetu.models;

import com.example.shetu.Assets.UpdateCheck;

public class UpdateResponse {

    private UpdateCheck data;
    private String status;

    public UpdateResponse(UpdateCheck data, String status) {
        this.data = data;
        this.status = status;
    }

    public UpdateCheck getData() {
        return data;
    }

    public void setData(UpdateCheck data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
