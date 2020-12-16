package com.example.shetu.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.shetu.R;

public class AuthActivity extends AppCompatActivity {

    private Button AuthRegBtn,AuthLoginBtn,AuthProfileBtn,ApisBtn;
    private TextView toolBarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);


        toolBarTitle=findViewById(R.id.app_toolbar_title);
        toolBarTitle.setText("Authentication");


        AuthRegBtn = findViewById(R.id.auth_reg_btn);
        AuthLoginBtn = findViewById(R.id.auth_login_btn);
        AuthProfileBtn = findViewById(R.id.auth_profile_btn);
        ApisBtn = findViewById(R.id.auth_api_btn);

        AuthRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(AuthActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }
        });
        AuthLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(AuthActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        AuthProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(AuthActivity.this, ProfileActivity.class);
                startActivity(loginIntent);
            }
        });
        ApisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(AuthActivity.this, ApisActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}