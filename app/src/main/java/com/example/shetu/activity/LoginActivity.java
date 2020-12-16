package com.example.shetu.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shetu.R;
import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.models.OtpRqResponse;
import com.example.shetu.models.VerifyResponse;
import com.example.shetu.storage.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    private String mobile;
    private Button LoginSendOtpBtn;

    private TextView tvwelcome, tvyourphn, tvPhn;
    //private CircleImageView appBarLogo;
    private EditText LoginPhnEt;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);


        LoginSendOtpBtn = findViewById(R.id.login_send_otp_btn);
        LoginPhnEt = findViewById(R.id.login_phn_et);


        tvwelcome = findViewById(R.id.login_tv_welcome);
        tvyourphn = findViewById(R.id.login_tv_your_phn);
        tvPhn = findViewById(R.id.login_tv_enter_phn);


        LoginSendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = LoginPhnEt.getText().toString();
                if (TextUtils.isEmpty(mobile) || mobile.length() != 11) {
                    Toast.makeText(LoginActivity.this, "Enter Valid Mobile Number...!", Toast.LENGTH_LONG).show();
                } else {
                    RequestOtp(mobile);
                }
            }
        });


        LoginPhnEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable mEdit) {
                String s = mEdit.toString();
                if (s.length() == 11) {
                    LoginSendOtpBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_blue_fill_back));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


    }

    private void RequestOtp(final String mobile) {
//        progressDialog.setMessage("Requesting OTP...");
//        progressDialog.show();

        Call<OtpRqResponse> call = RetrofitClient.getInstance().getApi().requestOTP(mobile);
        call.enqueue(new Callback<OtpRqResponse>() {

            @Override
            public void onResponse(Call<OtpRqResponse> call, Response<OtpRqResponse> response) {

                if (response.code() == 200) {

                    //progressDialog.dismiss();
                    Calendar c=Calendar.getInstance();
                    c.add(Calendar.MINUTE,3);
                    long currentTime=c.getTimeInMillis();
                    SharedPrefManager.getInstance(LoginActivity.this).removeData("newOtpRequestTime");
                    SharedPrefManager.getInstance(LoginActivity.this).saveData("newOtpRequestTime",String.valueOf(currentTime));
                    Intent intent = new Intent(LoginActivity.this, OtpVerifyActivity.class);
                    intent.putExtra("Mobile", mobile);
                    startActivity(intent);
                } else if (response.code() == 429) {

                    //progressDialog.dismiss();
//                    Intent intent = new Intent(LoginActivity.this, OtpVerifyActivity.class);
//                    intent.putExtra("Mobile", mobile);
//                    startActivity(intent);
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
                    Toast.makeText(LoginActivity.this, "OTP sent already. Please try after 3 minutes.", Toast.LENGTH_SHORT).show();
                } else {

                    //progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "e" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OtpRqResponse> call, Throwable t) {

                //progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //return success[0];

    }


}