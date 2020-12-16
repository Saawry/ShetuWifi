package com.example.shetu.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shetu.R;
import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.models.VerifyResponse;
import com.example.shetu.storage.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneVerifyActivity extends AppCompatActivity {

    private Button VerifyOtpBtn;
    private EditText VerifyOtpEt;
    private TextView appBarTitle,TokenTv;
    private String mobile,otp;
    private ProgressDialog progressDialog;

    private String BSSID;
    private WifiInfo wifiInfo;
    private WifiManager wifiManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        progressDialog = new ProgressDialog(PhoneVerifyActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);

        appBarTitle =findViewById(R.id.app_toolbar_title);
        TokenTv =findViewById(R.id.verify_phn_token);
        appBarTitle.setText("Verify");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        BSSID=wifiInfo.getBSSID();

        mobile=getIntent().getExtras().get("Mobile").toString();
//        mac_address=getIntent().getExtras().get("Mac").toString();

        //mac_address="121234";
        //mobile="+8801930997511";

        VerifyOtpBtn =findViewById(R.id.verify_phn_verify_btn);
        VerifyOtpEt =findViewById(R.id.verify_phn_otp_et);

        VerifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp=VerifyOtpEt.getText().toString();
                if (TextUtils.isEmpty(otp)){
                    Toast.makeText(PhoneVerifyActivity.this, "Enter OTP Code", Toast.LENGTH_SHORT).show();
                }else{
                    VerifyUser();
                }
            }
        });

    }

    private void VerifyUser() {
        progressDialog.setMessage("Requesting OTP...");
        progressDialog.show();
        Call<VerifyResponse> call= RetrofitClient.getInstance().getApi().Verify(mobile,otp,BSSID);
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {

                if (response.isSuccessful()){
                    //progressDialog.dismiss();
                    Toast.makeText(PhoneVerifyActivity.this, response.code() + " : " + response.message(), Toast.LENGTH_SHORT).show();

                    SharedPrefManager.getInstance(PhoneVerifyActivity.this)
                            .saveToken(response.body().getData());

                    Intent aIntent = new Intent(PhoneVerifyActivity.this, MainActivity2.class);
                    aIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(aIntent);

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(PhoneVerifyActivity.this, response.code() + " : " + response.message(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PhoneVerifyActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}