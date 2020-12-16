package com.example.shetu.fragments;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

import com.example.shetu.Assets.MyPackages;
import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.R;
import com.example.shetu.adapters.MyPackageAdapter;
import com.example.shetu.models.ActivePackagesResponse;
import com.example.shetu.storage.SharedPrefManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPackagesFragment extends Fragment {

    private View view;
    private RecyclerView pkgRecycler;
    private MyPackageAdapter pkgAdapter;
    private List<MyPackages> mypkgList;
    private String token;
    private ProgressDialog progressDialog;
    private FusedLocationProviderClient client;
    private Location lc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        client = LocationServices.getFusedLocationProviderClient(getContext());
        view = inflater.inflate(R.layout.fragment_my_packages, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Packages...");
        progressDialog.show();
        token = SharedPrefManager.getInstance(getContext()).getToken();
        pkgRecycler = view.findViewById(R.id.my_pkg_recycler);
        pkgRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        Call<ActivePackagesResponse> call = RetrofitClient.getInstance().getApi().GetMyActivepPkgs("Bearer " + token);
        call.enqueue(new Callback<ActivePackagesResponse>() {
            @Override
            public void onResponse(Call<ActivePackagesResponse> call, Response<ActivePackagesResponse> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    mypkgList = response.body().getData();

                    pkgAdapter = new MyPackageAdapter(getActivity(), mypkgList,client,lc);
                    pkgRecycler.setAdapter(pkgAdapter);
                    progressDialog.dismiss();
                }else{
                    //Toast.makeText(getContext(), response.code()+" - "+response.message(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ActivePackagesResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}