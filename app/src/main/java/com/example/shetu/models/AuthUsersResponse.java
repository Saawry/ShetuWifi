package com.example.shetu.models;

import com.example.shetu.Assets.User;

public class AuthUsersResponse {

    private User data;
    private String status;

    public AuthUsersResponse(User data, String status) {
        this.data = data;
        this.status = status;
    }

    public User getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

}
