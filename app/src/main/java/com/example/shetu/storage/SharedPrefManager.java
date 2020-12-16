package com.example.shetu.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.shetu.Assets.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "my_shared_preff";

    private static SharedPrefManager mInstance;
    private Context mCtx;

    private static String masterKeys;

    {
        try {
            masterKeys = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private SharedPrefManager(Context mCtx) {
        this.mCtx = mCtx;
    }


    public static synchronized SharedPrefManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(mCtx);
        }
        return mInstance;
    }



    public void SaveWifiSSID(String ssid){
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "EncSharedPrefsFile",
                    masterKeys,
                    mCtx,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();

        sharedPrefsEditor.putString("token", ssid);
        sharedPrefsEditor.apply();
    }

    public String getWifiSSID(String key){
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "EncSharedPrefsFile",
                    masterKeys,
                    mCtx,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sharedPreferences.getString(key,null);
    }

    public void saveToken(String token){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("token", token);
        editor.apply();
    }

    public void saveData(String key, String value){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public String getData(String keyCode){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String un=sharedPreferences.getString(keyCode,null);
        return un;
    }

    public void removeData(String key) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }
    public void saveUser(User user) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //User user1=new User();

        editor.putString("id", user.getId());
        editor.putString("name", user.getName());
        editor.putString("mobile", user.getMobile());
        editor.putString("mac_address", user.getMac_address());
        editor.putString("birthdate", user.getBalance());
        editor.putString("balance", user.getBalance());
        editor.putString("purchase", user.getBalance());

//        editor.putString("id", user.getId());
//        editor.putString("name", user.getName());
//        editor.putString("mobile", user.getMobile());
//        editor.putString("mac_address", user.getMac_address());
//        editor.putString("balance", user.getBalance());
//        editor.putString("otp", user.getOtp());
//        editor.putString("otp_duration", user.getOtp_duration());
//        editor.putString("created_at", user.getCreated_at());
//        editor.putString("updated_at", user.getUpdated_at());

        editor.apply();

    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String lFlag=sharedPreferences.getString("token",null);
        if (TextUtils.isEmpty(lFlag)){
            return false;
        }else return true;
        //return sharedPreferences.getInt("id", -1) != -1;
    }

    public String getUserName(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String un=sharedPreferences.getString("name",null);
        return un;
    }
    public String getThisKeyvalue(String key){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String value=sharedPreferences.getString(key,null);
        return value;
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        User user = new User(
                sharedPreferences.getString("id", null),
                sharedPreferences.getString("name", null),
                sharedPreferences.getString("mobile", null),
                sharedPreferences.getString("mac_address", null),
                sharedPreferences.getString("birthdate", null),
                sharedPreferences.getString("balance", null),
                sharedPreferences.getString("purchase", null)
        );
//        return new User(
//                sharedPreferences.getString("id", null),
//                sharedPreferences.getString("name", null),
//                sharedPreferences.getString("mobile", null),
//                sharedPreferences.getString("mac_address", null),
//                sharedPreferences.getString("balance", null),
//                sharedPreferences.getString("otp", null),
//                sharedPreferences.getString("otp_duration", null),
//                sharedPreferences.getString("created_at", null),
//                sharedPreferences.getString("updated_at", null)
//        );
        return user;
    }

    public String getToken() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString("token", null);
    }
    public void removeValue(String key) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public void clear() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
