package com.example.shetu.models;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    @SerializedName("error")
    private boolean err;

    @SerializedName("message")
    private String msg;

    public RegisterResponse(boolean err, String msg) {
        this.err = err;
        this.msg = msg;
    }

    public boolean isErr() {
        return err;
    }

    public String getMsg() {
        return msg;
    }
}
