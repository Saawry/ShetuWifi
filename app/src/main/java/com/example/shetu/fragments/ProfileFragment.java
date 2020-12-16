package com.example.shetu.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.Assets.User;
import com.example.shetu.R;
import com.example.shetu.activity.MainActivity2;
import com.example.shetu.models.AuthUsersResponse;
import com.example.shetu.models.DfltResponse;
import com.example.shetu.storage.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private Button LogoutBtn;
    private View view;
    private String token;
    private TextView tvN,tvM,tvDob,tvB,tvCn;
    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        view=inflater.inflate(R.layout.profile_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);

        LogoutBtn= view.findViewById(R.id.pro_fragment_logout_btn);

        tvN= view.findViewById(R.id.pro_fragment_name_tv);
        tvM= view.findViewById(R.id.pro_fragment_mobile_tv);
        tvB= view.findViewById(R.id.pro_fragment_balance_tv);
        tvDob= view.findViewById(R.id.pro_fragment_dob_tv);
        tvCn= view.findViewById(R.id.pro_fragment_pkg_cntr_tv);

        token = SharedPrefManager.getInstance(getContext()).getToken();

        GetUserInfo();


        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Logging Out...");
                progressDialog.show();
                StoreSession();

            }
        });

    }

    private void LogoutUser() {

        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().logout("Bearer " + token);
        call.enqueue(new Callback<DfltResponse>() {
            @Override
            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
                if (response.isSuccessful()) {

                    SharedPrefManager.getInstance(getActivity()).removeValue("token");///try clear if doesnt work
                    //SharedPrefManager.getInstance(getActivity()).clear();
                    //keep data for easier login suggest and to skip reg activity
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Logged out.", Toast.LENGTH_SHORT).show();
                    Intent aIntent = new Intent(getActivity(), MainActivity2.class);
                    aIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(aIntent);
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DfltResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });


//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("text/plain");
//        RequestBody body = RequestBody.create(mediaType, "");
//        Request request = new Request.Builder()
//                .url("http://100.25.223.254:8080/v1/logout")
//                .method("DELETE", body)
//                .addHeader("Authorization", "Bearer "+token).build();
//        try {
//            okhttp3.Response response = client.newCall(request).execute();
//            if (response.isSuccessful()){
//                SharedPrefManager.getInstance(ProfileActivity.this).clear();
//                Toast.makeText(this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void StoreSession() {
        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().StoreSession("00000", "123456", "2020-09-21 20:58:06", "2020-09-21 23:00:11", "100");
        call.enqueue(new Callback<DfltResponse>() {
            @Override
            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
                if (response.isSuccessful()) {
                    LogoutUser();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DfltResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void GetUserInfo() {
        progressDialog.setMessage("Getting User Info...");
        progressDialog.show();
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
                    SharedPrefManager.getInstance(getContext()).saveUser(Nuser);

                    //Gson gson= new Gson();
                    //String gsonStr = gson.toJson(Nuser,UserInfo.class);
                    //SharedPrefManager.getInstance(getContext()).saveData("cuser",gsonStr);
                    //User nuser=new User(user.getId(),s2,s3,user.getMac_address(),s5);
                    //SharedPrefManager.getInstance(getContext()).saveUser(Nuser);

                    tvN.setText(n);
                    tvM.setText(m);
                    String bdtSign = getString (R.string.bdt_symbol);
                    tvB.setText("Balance: "+bdtSign+b);
                    tvCn.setText(c);
                    tvDob.setText(dob);
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthUsersResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}