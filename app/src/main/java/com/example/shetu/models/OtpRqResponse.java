package com.example.shetu.models;

public class OtpRqResponse {

    private String message, status;

    public OtpRqResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }


    public String getMessage() {
        return message;
    }


    public String getStatus() {
        return status;
    }

}
