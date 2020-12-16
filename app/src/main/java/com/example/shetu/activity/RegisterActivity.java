package com.example.shetu.activity;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shetu.R;
import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.models.DfltResponse;
import com.example.shetu.storage.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private String name, dob, token;
    private Button RegUpdateBtn;
    private TextView RegDobEt, appBarTitle;
    private EditText RegNameEt;
    private ProgressDialog progressDialog;
    private Toolbar regToolbar;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);


        regToolbar = findViewById(R.id.reg_toolbar);
        regToolbar.setVisibility(View.VISIBLE);
        appBarTitle = findViewById(R.id.app_toolbar_title);
        appBarTitle.setText(R.string.update_info_toolbar_title);

        RegUpdateBtn = findViewById(R.id.reg_register_btn);

        RegNameEt = findViewById(R.id.reg_name_et);
        RegDobEt = findViewById(R.id.reg_dobrth_tv);


        final DatePickerDialog date = new DatePickerDialog(RegisterActivity.this, R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd.MM.yyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                RegDobEt.setText(sdf.format(myCalendar.getTime()));
                updateLabel();
            }
        }, 2000, 01, 01);

        RegDobEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date.show();
            }
        });


        RegUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                token = SharedPrefManager.getInstance(RegisterActivity.this).getToken();
                name = RegNameEt.getText().toString();
                dob = RegDobEt.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(RegisterActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(dob) || dob.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Enter Valid Date of Birth", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(token)) {
                    Toast.makeText(RegisterActivity.this, "Error, Verify OTP again", Toast.LENGTH_SHORT).show();
                    return;
                }
                UpdateUserInfo();
            }
        });
    }

    private void UpdateUserInfo() {
        progressDialog.setMessage("Updating");
        progressDialog.show();
        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().UpdateInfo("Bearer " + token, name, dob);
        call.enqueue(new Callback<DfltResponse>() {
            @Override
            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    SharedPrefManager.getInstance(RegisterActivity.this).saveData("name", name);
                    SendToMainActivity();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, response.code() + ": " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DfltResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void SendToMainActivity() {
        Intent aIntent = new Intent(RegisterActivity.this, ScrachCardActivity.class);
        startActivity(aIntent);
    }

    private void updateLabel() {
        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        RegDobEt.setText(sdf.format(myCalendar.getTime()));
    }
}