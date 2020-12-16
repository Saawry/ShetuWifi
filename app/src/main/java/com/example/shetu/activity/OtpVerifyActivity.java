package com.example.shetu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.R;
import com.example.shetu.models.OtpRqResponse;
import com.example.shetu.models.VerifyResponse;
import com.example.shetu.storage.SharedPrefManager;

import java.text.DecimalFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerifyActivity extends AppCompatActivity {


    private WifiInfo wifiInfo;
    private WifiManager wifiManager;
    private boolean sendAgainFlag = false;
    private EditText oTpEt;
    private TextView appBarTitle, tvWritecode, tvcodeMismatch, tvdidntGetCode, tvsendAgain, tvmobileNumber;
    private Button VerifyOtpBtn;
    private String otp, mobile, BSSID;
    private ProgressDialog progressDialog;
    private boolean newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        mobile = getIntent().getExtras().get("Mobile").toString();


        long tt = CustomMethods.getInstance(OtpVerifyActivity.this).GetRemainingTime();


        appBarTitle = findViewById(R.id.app_toolbar_title);
        appBarTitle.setText(R.string.verify_toolbar_title);

        VerifyOtpBtn = findViewById(R.id.verify_otp_btn);
        oTpEt = findViewById(R.id.login_otp_et);
        tvcodeMismatch = findViewById(R.id.login_tv_code_mismatch);
        tvdidntGetCode = findViewById(R.id.login_tv_didnt_get_code);
        tvmobileNumber = findViewById(R.id.login_tv_sent_phn);
        tvsendAgain = findViewById(R.id.login_tv_send_again);
        tvWritecode = findViewById(R.id.login_tv_write_login_code);


        tvmobileNumber.setText(mobile+getString(R.string.verify_msg_number1));

        tvsendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendAgainFlag){
                    SendOtpAgain(mobile);
                }
            }
        });

        new CountDownTimer(tt, 1000) {

            public void onTick(long millisUntilFinished) {
                long mint=millisUntilFinished/60000;
                int sec= (int) (millisUntilFinished%60000)/1000;

                //tvsendAgain.setText(getString(R.string.send_code_again_str) +" "+ millisUntilFinished / 1000);
                tvsendAgain.setText(getString(R.string.send_code_again_str) +" "+mint+":"+sec);
            }

            @SuppressLint("ResourceAsColor")
            public void onFinish() {
                sendAgainFlag = true;
                tvsendAgain.setText(getString(R.string.send_code_again_link_str));
                tvsendAgain.setTextColor(R.color.lgBlue);
            }
        }.start();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        BSSID = wifiInfo.getBSSID();


        progressDialog = new ProgressDialog(OtpVerifyActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);


        oTpEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable mEdit) {
                String s = mEdit.toString();
                if (s.length() == 6) {
                    VerifyOtpBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_blue_fill_back));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });



        VerifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = oTpEt.getText().toString();
                if (TextUtils.isEmpty(otp) || otp.length() < 6) {
                    Toast.makeText(OtpVerifyActivity.this, "Enter Otp...!", Toast.LENGTH_LONG).show();
                    return;
                }
                VerifyUser();
            }
        });


    }

    private void SendOtpAgain(final String mobile) {
//        progressDialog.setMessage("Requesting OTP...");
//        progressDialog.show();

        Call<OtpRqResponse> call = RetrofitClient.getInstance().getApi().requestOTP(mobile);
        call.enqueue(new Callback<OtpRqResponse>() {

            @Override
            public void onResponse(Call<OtpRqResponse> call, Response<OtpRqResponse> response) {

                if (response.code() == 200) {
                    sendAgainFlag = false;
                    tvcodeMismatch.setVisibility(View.INVISIBLE);
                    tvsendAgain.setText(getString(R.string.send_code_again_str));
                    tvsendAgain.setTextColor(getColor(R.color.black));
                    //progressDialog.dismiss();
                    Calendar c=Calendar.getInstance();
                    c.add(Calendar.MINUTE,3);
                    long currentTime=c.getTimeInMillis();
                    SharedPrefManager.getInstance(OtpVerifyActivity.this).removeData("newOtpRequestTime");
                    SharedPrefManager.getInstance(OtpVerifyActivity.this).saveData("newOtpRequestTime",String.valueOf(currentTime));
                } else if (response.code() == 429) {
                    tvcodeMismatch.setVisibility(View.INVISIBLE);
                    //progressDialog.dismiss();
//                    final Dialog dialog = new Dialog(LoginActivity.this, R.style.DialogTheme);
//                    dialog.setContentView(R.layout.dialog_already_sent_otp);
//                    Button btton =  dialog.findViewById(R.id.verify_existing_otp_btn);
//                    btton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            otpSent=true;
//                            LoginPhnEt.setText("");
//                            LoginPhnEt.setHint("OTP");
//                            LoginSendOtpBtn.setText("Verify");
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();
                    Toast.makeText(OtpVerifyActivity.this, "OTP sent already. Please try after 3 minutes.", Toast.LENGTH_SHORT).show();
                } else {
                    //progressDialog.dismiss();
                    tvcodeMismatch.setVisibility(View.VISIBLE);
                    Toast.makeText(OtpVerifyActivity.this, "e" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OtpRqResponse> call, Throwable t) {

                //progressDialog.dismiss();
                Toast.makeText(OtpVerifyActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //return success[0];

    }

    private void VerifyUser() {
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();

        Call<VerifyResponse> call = RetrofitClient.getInstance().getApi().Verify(mobile, otp, BSSID);
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {

                if (response.code() == 200) {

                    VerifyResponse verifyResponse = response.body();
                    newUser = verifyResponse.getIs_new_user();
                    String token = verifyResponse.getData();

                    SharedPrefManager.getInstance(OtpVerifyActivity.this)
                            .saveToken(token);

                    //progressDialog.dismiss();
                    Intent aIntent;
                    if (!newUser) {

                        aIntent = new Intent(OtpVerifyActivity.this, MainActivity.class);
                    } else {
                        aIntent = new Intent(OtpVerifyActivity.this, RegisterActivity.class);
                    }
                    aIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(aIntent);

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(OtpVerifyActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }

//                if (tkn!=null){
//                    try {
//                        JSONObject token = new JSONObject(tkn);
//                        Toast.makeText(LoginActivity.this, token.getString("data"), Toast.LENGTH_LONG).show();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }

            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(OtpVerifyActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}