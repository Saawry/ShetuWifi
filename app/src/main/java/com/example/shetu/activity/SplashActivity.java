package com.example.shetu.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.Assets.UpdateCheck;
import com.example.shetu.R;
import com.example.shetu.models.UpdateResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private String url;
    private boolean flag = false;
    private Dialog dialog;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        final Handler mHandler = new Handler();
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2500);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    //Intent mainIntent = new Intent(SplashActivity.this, TestExpand.class);
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        };
        thread.start();
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                try{
//                    sleep(2500);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }finally {
//
//                    mHandler.post(new Runnable() {
//                        public void run(){
//                            //Be sure to pass your Activity class, not the Thread
//                            open();
//                            //... setup dialog and show
//                        }
//                    });
//
//
//
//
////                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
////                    startActivity(mainIntent);
//                }
//            }
//        };
//
//        thread.start();
    }


    public void open() {//View view


        dialog = new Dialog(SplashActivity.this);
        dialog.setContentView(R.layout.update_alert_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button update = dialog.findViewById(R.id.alert_positive_btn);
        Button later = dialog.findViewById(R.id.alert_negative_btn);
        // if button is clicked, close the custom dialog
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aIntent = new Intent(SplashActivity.this, MainActivity.class);
                aIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(aIntent);
            }
        });

        dialog.show();

        alertDialogBuilder = new AlertDialog.Builder(this);/////
        alertDialogBuilder.setMessage("Update to newer version");
        alertDialogBuilder.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                        //Toast.makeText(SplashActivity.this,"You clicked yes ",Toast.LENGTH_LONG).show();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                Intent aIntent = new Intent(SplashActivity.this, MainActivity2.class);
                aIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(aIntent);
            }
        });

        alertDialog = alertDialogBuilder.create();///////
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        //alertDialog.show();
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Call<UpdateResponse> call = RetrofitClient.getInstance().getApi().UpdateCheck();
//        call.enqueue(new Callback<UpdateResponse>() {
//            @Override
//            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
//                if (response.isSuccessful()) {
//                    UpdateCheck ur = response.body().getData();
//                    flag = ur.isForce_update();
//                    url = ur.getUrl();
//                    open();
//                    Toast.makeText(SplashActivity.this, "Update Available", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UpdateResponse> call, Throwable t) {
//
//            }
//        });
//        if (flag) {
//
//        }
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //dialog.dismiss();
        //finish();
    }
}