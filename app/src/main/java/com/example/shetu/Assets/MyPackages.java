package com.example.shetu.Assets;

public class MyPackages {
    private String id,name,price,expire_in, duration, auto_renew;

    public MyPackages(String id, String name, String price, String expire_in, String duration, String auto_renew) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.expire_in = expire_in;
        this.duration = duration;
        this.auto_renew = auto_renew;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExpire_in() {
        return expire_in;
    }

    public void setExpire_in(String expire_in) {
        this.expire_in = expire_in;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAuto_renew() {
        return auto_renew;
    }

    public void setAuto_renew(String auto_renew) {
        this.auto_renew = auto_renew;
    }
}
