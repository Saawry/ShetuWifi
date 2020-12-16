package com.example.shetu.activity;

import android.content.Context;

import com.example.shetu.storage.SharedPrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.view.View.Z;

public class CustomMethods {

    private Context mCtx;
    private static CustomMethods mInstance;
    private CustomMethods(Context mCtx) {
        this.mCtx = mCtx;
    }


    public static synchronized CustomMethods getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new CustomMethods(mCtx);
        }
        return mInstance;
    }

    public long GetRemainingTime(){
        Calendar c=Calendar.getInstance();
        long currentTime=c.getTimeInMillis();
        long targetTime=Long.parseLong(SharedPrefManager.getInstance(mCtx).getData("newOtpRequestTime"));
        return targetTime-currentTime;
    }

//    long difference = storedTime.getTime - currentTime.getTime();
//    long days =(int)(difference /(1000*60*60*24));
//    long hours =(int)((difference -(1000*60*60*24*days))/(1000*60*60));
//    long min =(int)(difference -(1000*60*60*24*days)-(1000*60*60*hours))/(1000*60);
//    long hoursActual =(hours< 0?-hours :hours);
}
