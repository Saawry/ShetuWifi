package com.example.shetu.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM;
import static androidx.security.crypto.MasterKeys.AES256_GCM_SPEC;

public class EncryptedSharedPrefmanager {
    // Although you can define your own key generation parameter specification, it's
// recommended that you use the value specified here.


    private static final String SHARED_PREF_NAME = "my_encrypted_shared_preff";

    private static EncryptedSharedPrefmanager mInstance;
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

    private EncryptedSharedPrefmanager(Context mCtx) {
        this.mCtx = mCtx;
    }


    public static synchronized EncryptedSharedPrefmanager getInstance(Context mCtx) {
        if (mInstance == null) {


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
// Edit the user's shared preferences...
            sharedPrefsEditor.apply();


        }
        return mInstance;
    }
}
