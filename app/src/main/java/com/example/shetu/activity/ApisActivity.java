package com.example.shetu.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shetu.Assets.MyPackages;
import com.example.shetu.Assets.PackagesInfo;
import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.Assets.User;
import com.example.shetu.R;
import com.example.shetu.models.AllPkgInfoResponse;
import com.example.shetu.models.AuthUsersResponse;

import com.example.shetu.models.DfltResponse;
import com.example.shetu.models.PurchaseResponse;
import com.example.shetu.storage.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApisActivity extends AppCompatActivity {

    private TextView tvStatusCode, tv_tkn;
    private Button UserInfobtn, AllPkgbtn, Purchasebtn, MacStrorebtn, SessionStorebtn, NetStoreBtn,expBtn ;//GetNetBtn, DeleteNetbtn
    private String token;
    //private UsersAdapter adapter;
    private List<PackagesInfo> pkgList;
    private List<MyPackages> myPkgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apis);

        token = SharedPrefManager.getInstance(this).getToken();

        tvStatusCode = findViewById(R.id.tv_api_status_code);
        tv_tkn = findViewById(R.id.apis_tv_token);
        tv_tkn.setText(token);


        UserInfobtn = findViewById(R.id.api_user_info);
        AllPkgbtn = findViewById(R.id.api_all_pkg);
        Purchasebtn = findViewById(R.id.api_purchase);
        MacStrorebtn = findViewById(R.id.api_mac_store);
        SessionStorebtn = findViewById(R.id.api_session_store);
        NetStoreBtn = findViewById(R.id.api_net_store);
        expBtn = findViewById(R.id.expnd_test_btn);
//        DeleteNetbtn = findViewById(R.id.api_delete_network);

        UserInfobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserInfo();
            }
        });
        AllPkgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetAllPkgInfo();
            }
        });
        Purchasebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchasePkg();
            }
        });
        MacStrorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreMac();
            }
        });
        SessionStorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreSession();
            }
        });
        NetStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreNet();
            }
        });
        expBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent aIntent = new Intent(ApisActivity.this, MainActivity2.class);
                //aIntent.putExtra("Mobile", mobile);
                startActivity(aIntent);
                //Getnet();
            }
        });
//        DeleteNetbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DeleteNet();
//            }
//        });
    }

    private void GetUserInfo() {

        Call<AuthUsersResponse> call = RetrofitClient.getInstance().getApi().getUserDetails("Bearer " + token);
        call.enqueue(new Callback<AuthUsersResponse>() {
            @Override
            public void onResponse(Call<AuthUsersResponse> call, Response<AuthUsersResponse> response) {
                if (response.isSuccessful()) {

                    User user = response.body().getData();

                    //myPkgList = user.getPurchases();

                    int counter=0;
                    for (MyPackages MPkgs : myPkgList) {
//                        counter++;
//                        String content = "";
//                        content += "id :" + MPkgs.getId() + "\n";
//                        content += "user_id :" + MPkgs.getUser_id() + "\n";
//                        content += "package_id :" + MPkgs.getPackage_id() + "\n";
//                        content += "expire_in :" + MPkgs.getExpire_in() + "\n";
//                        content += "latitude :" + MPkgs.getLatitude() + "\n";
//                        content += "longitude :" + MPkgs.getLongitude() + "\n";
//                        content += "created_at :" + MPkgs.getCreated_at() + "\n";
//                        content += "updated_at :" + MPkgs.getUpdated_at() + "\n";
//                        tvStatusCode.append(content);
                    }

                    String s1="Id: "+user.getId()+"\n";
                    String s2="Name: "+user.getName()+"\n";
                    String s3="Mobile: "+user.getMobile()+"\n";
                    String s4="Mac : "+user.getMac_address()+"\n";
                    String s5="Balance: "+user.getBalance()+"\n";
                    String s6="Active Pkgs: "+counter;
                    tv_tkn.setText(s1+s2+s3+s4+s5+s6);
                }
            }

            @Override
            public void onFailure(Call<AuthUsersResponse> call, Throwable t) {

            }
        });
    }


    private void GetAllPkgInfo() {
        Call<AllPkgInfoResponse> call = RetrofitClient.getInstance().getApi().getAllPkgDetails("Bearer " + token);
        call.enqueue(new Callback<AllPkgInfoResponse>() {
            @Override
            public void onResponse(Call<AllPkgInfoResponse> call, Response<AllPkgInfoResponse> response) {
                pkgList = response.body().getData();
                //tvStatusCode.setText(String.valueOf(response.code()));
                for (PackagesInfo pkgs : pkgList) {
                    String content = "";
                    content += "id: " + pkgs.getId() + "\n";
                    content += "name: " + pkgs.getName() + "\n";
                    content += "duration: " + pkgs.getDuration() + "\n";
                    content += "price: " + pkgs.getPrice() + "\n";
                    tvStatusCode.append(content);
                }

            }

            @Override
            public void onFailure(Call<AllPkgInfoResponse> call, Throwable t) {

            }
        });
    }

    private void PurchasePkg() {
        Call<PurchaseResponse> call = RetrofitClient.getInstance().getApi().PurchasePkg("1","Bearer " + token, "99.99999", "99.99999","0");
        call.enqueue(new Callback<PurchaseResponse>() {
            @Override
            public void onResponse(Call<PurchaseResponse> call, Response<PurchaseResponse> response) {
                if (response.isSuccessful()){
                    String message=response.body().getMessage();
                    Toast.makeText(ApisActivity.this, message, Toast.LENGTH_SHORT).show();
                    User user=response.body().getData();

                    //myPkgList = user.getPurchases();

                    int counter=0;
                    for (MyPackages MPkgs : myPkgList) {
//                        counter++;
//                        String content = "";
//                        content += "id :" + MPkgs.getId() + "\n";
//                        content += "user_id :" + MPkgs.getUser_id() + "\n";
//                        content += "package_id :" + MPkgs.getPackage_id() + "\n";
//                        content += "expire_in :" + MPkgs.getExpire_in() + "\n";
//                        content += "latitude :" + MPkgs.getLatitude() + "\n";
//                        content += "longitude :" + MPkgs.getLongitude() + "\n";
//                        content += "created_at :" + MPkgs.getCreated_at() + "\n";
//                        content += "updated_at :" + MPkgs.getUpdated_at() + "\n";
//                        tvStatusCode.append(content);
                    }

                    String s1="Id: "+user.getId()+"\n";
                    String s2="Name: "+user.getName()+"\n";
                    String s3="Mobile: "+user.getMobile()+"\n";
                    String s4="Mac : "+user.getMac_address()+"\n";
                    String s5="Balance: "+user.getBalance()+"\n";
                    String s6="Active Pkgs: "+counter;
                    tv_tkn.setText(s1+s2+s3+s4+s5+s6);
                }


            }

            @Override
            public void onFailure(Call<PurchaseResponse> call, Throwable t) {

            }
        });
    }

    private void StoreSession() {
        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().StoreSession("00000", "123456", "2020-09-21 20:58:06", "2020-09-21 23:00:11", "100");
        call.enqueue(new Callback<DfltResponse>() {
            @Override
            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
                if (response.isSuccessful()) {
                    tvStatusCode.setText("Session " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DfltResponse> call, Throwable t) {

            }
        });

    }

    private void StoreMac() {
        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().StoreMac("Bearer " + token, "123456");
        call.enqueue(new Callback<DfltResponse>() {
            @Override
            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
                if (response.isSuccessful()) {
                    tvStatusCode.setText("Store mac " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DfltResponse> call, Throwable t) {

            }
        });
    }

    private void StoreNet() {
        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().StoreNetwork("Wifi Name", "12345678");
        call.enqueue(new Callback<DfltResponse>() {
            @Override
            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
                if (response.isSuccessful()) {
                    tvStatusCode.setText("Store network " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DfltResponse> call, Throwable t) {

            }
        });

    }

    private void Getnet() {
//        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().GetNetwork();
//        call.enqueue(new Callback<DfltResponse>() {
//            @Override
//            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
//                if (response.isSuccessful()){
//                    tvStatusCode.setText("Get network "+String.valueOf(response.code()));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DfltResponse> call, Throwable t) {
//
//            }
//        });
    }


    private void DeleteNet() {
//        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().DeleteNetwork();
//        call.enqueue(new Callback<DfltResponse>() {
//            @Override
//            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
//                if (response.isSuccessful()){
//                    tvStatusCode.setText("Delete network "+String.valueOf(response.code()));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DfltResponse> call, Throwable t) {
//
//            }
//        });
    }
}