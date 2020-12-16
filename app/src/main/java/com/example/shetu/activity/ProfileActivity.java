package com.example.shetu.activity;

import com.cooltechworks.views.ScratchImageView;
import com.example.shetu.models.AuthUsersResponse;
import com.example.shetu.models.DfltResponse;
import com.example.shetu.storage.SharedPrefManager;

import android.content.Intent;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.Assets.User;
import com.example.shetu.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvN,tvM,tvDob,tvB,tvCn;
    private Button ProfileLogoutBtn;
    private String token;
    //private String mobile, name, balance,dob,pkgCntr,token;

    //private Token token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        token=SharedPrefManager.getInstance(ProfileActivity.this).getToken();

        tvN= findViewById(R.id.pro_name_tv);
        tvM= findViewById(R.id.pro_mobile_tv);
        tvB= findViewById(R.id.pro_balance_tv);
        tvDob= findViewById(R.id.pro_dob_tv);
        tvCn= findViewById(R.id.pro_pkg_cntr_tv);
        ProfileLogoutBtn = findViewById(R.id.prof_logout_btn);


        ScratchImageView scratchImageView = new ScratchImageView(this);

        scratchImageView.setRevealListener(new ScratchImageView.IRevealListener() {
            @Override
            public void onRevealed(ScratchImageView tv) {
                // on reveal
                Toast.makeText(ProfileActivity.this, "Congrats...!!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRevealPercentChangedListener(ScratchImageView siv, float percent) {
                // on image percent reveal
            }
        });


        String bdtSign = getString (R.string.bdt_symbol);

        User user = SharedPrefManager.getInstance(this).getUser();

        tvN.setText(user.getName());
        tvM.setText(user.getMobile());
        tvB.setText("Balance: "+bdtSign+user.getBalance());
        tvCn.setText(user.getPurchase());
        tvDob.setText(user.getBirthdate());
        ProfileLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CrashDemo();
                LogoutUser();
            }
        });

    }

    private void CrashDemo() {

        throw new RuntimeException("Test Crash");


    }

    private void LogoutUser() {

        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().logout("Bearer " + token);
        call.enqueue(new Callback<DfltResponse>() {
            @Override
            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
                if (response.isSuccessful()) {
                    SharedPrefManager.getInstance(ProfileActivity.this).clear();
                    Toast.makeText(ProfileActivity.this, String.valueOf(response.message()), Toast.LENGTH_SHORT).show();
                    Intent aIntent = new Intent(ProfileActivity.this,LoginActivity.class);
                    //Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(aIntent);
                }
            }

            @Override
            public void onFailure(Call<DfltResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,t.getMessage(),Toast.LENGTH_SHORT);
            }
        });

    }
    private void GetUserInfo() {

        Call<AuthUsersResponse> call = RetrofitClient.getInstance().getApi().getUserDetails("Bearer " + token);
        call.enqueue(new Callback<AuthUsersResponse>() {
            @Override
            public void onResponse(Call<AuthUsersResponse> call, Response<AuthUsersResponse> response) {
                if (response.isSuccessful()) {

                    User user = response.body().getData();

                    String n=user.getName();
                    String m=user.getMobile();
                    String dob=user.getBirthdate();
                    String b=user.getBalance();
                    String c="Active Packages: "+user.getPurchase();

                    User Nuser=new User(user.getId(),user.getName(),user.getMobile(),user.getMac_address(),user.getBirthdate(),user.getBalance(),user.getPurchase());
                    SharedPrefManager.getInstance(ProfileActivity.this).saveUser(Nuser);

                    //Gson gson= new Gson();
                    //String gsonStr = gson.toJson(Nuser,UserInfo.class);
                    //SharedPrefManager.getInstance(getContext()).saveData("cuser",gsonStr);
                    //User nuser=new User(user.getId(),s2,s3,user.getMac_address(),s5);
                    //SharedPrefManager.getInstance(getContext()).saveUser(Nuser);

                    tvN.setText(n);
                    tvM.setText(m);
                    String bdtSign = getString (R.string.bdt_symbol);
                    tvB.setText("Balance: "+bdtSign+b);
                    tvCn.setText("Active Packages"+c);
                    tvDob.setText(dob);

                }
            }

            @Override
            public void onFailure(Call<AuthUsersResponse> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        token=SharedPrefManager.getInstance(ProfileActivity.this).getToken();
        GetUserInfo();
    }
}