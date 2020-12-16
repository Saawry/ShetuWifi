package com.example.shetu.Assets;

public class UpdateCheck {
    private boolean force_update;
    private String url;

    public UpdateCheck(boolean force_update, String url) {
        this.force_update = force_update;
        this.url = url;
    }

    public boolean isForce_update() {
        return force_update;
    }

    public void setForce_update(boolean force_update) {
        this.force_update = force_update;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
