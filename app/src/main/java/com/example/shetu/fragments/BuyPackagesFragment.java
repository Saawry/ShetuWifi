package com.example.shetu.fragments;

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

import com.example.shetu.Assets.PackagesInfo;
import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.R;
import com.example.shetu.adapters.PackageAdapter;
import com.example.shetu.models.AllPkgInfoResponse;
import com.example.shetu.storage.SharedPrefManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyPackagesFragment extends Fragment {

    private View view;
    private RecyclerView pkgRecycler;
    private PackageAdapter pkgAdapter;
    private List<PackagesInfo> pkgList;
    private String token;
    private FusedLocationProviderClient client;
    private Location lc;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        client = LocationServices.getFusedLocationProviderClient(getContext());
        view = inflater.inflate(R.layout.fragment_buy_packages, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        token = SharedPrefManager.getInstance(getContext()).getToken();
        pkgRecycler = view.findViewById(R.id.buy_pkg_recycler);
        pkgRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        Call<AllPkgInfoResponse> call = RetrofitClient.getInstance().getApi().getAllPkgDetails("Bearer " + token);
        call.enqueue(new Callback<AllPkgInfoResponse>() {
            @Override
            public void onResponse(Call<AllPkgInfoResponse> call, Response<AllPkgInfoResponse> response) {
                if (response.isSuccessful()) {


                    pkgList = response.body().getData();
                    pkgAdapter = new PackageAdapter(getActivity(), pkgList, client, lc);
                    pkgRecycler.setAdapter(pkgAdapter);


                } else {
                    Toast.makeText(getContext(), response.code() + " - " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AllPkgInfoResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}