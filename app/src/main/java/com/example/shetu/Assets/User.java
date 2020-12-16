package com.example.shetu.Assets;

import java.util.List;

public class User {

    private String  id,name, mobile,mac_address,birthdate,balance,purchase;

    public User(String id, String name, String mobile, String mac_address, String birthdate, String balance, String purchase) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.mac_address = mac_address;
        this.birthdate = birthdate;
        this.balance = balance;
        this.purchase = purchase;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getPurchase() {
        return purchase;
    }

    public void setPurchase(String purchase) {
        this.purchase = purchase;
    }


}
