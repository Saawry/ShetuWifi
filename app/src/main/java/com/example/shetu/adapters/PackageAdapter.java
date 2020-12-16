package com.example.shetu.adapters;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shetu.Assets.PackagesInfo;
import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.R;

import com.example.shetu.models.PurchaseResponse;
import com.example.shetu.storage.SharedPrefManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {

    private Context mCtx;
    private List<PackagesInfo> pkgList;
    private FusedLocationProviderClient client;
    private Location lc;

    public PackageAdapter(Context mCtx, List<PackagesInfo> pkgList, FusedLocationProviderClient client, Location lc) {
        this.mCtx = mCtx;
        this.pkgList = pkgList;
        this.client = client;
        this.lc = lc;
    }
    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.my_pkg_layout, viewGroup, false);
        client = LocationServices.getFusedLocationProviderClient(mCtx);
        return new PackageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final PackageViewHolder packageViewHolder, int position) {

        final PackagesInfo pkgs = pkgList.get(position);

        Random r = new Random();
        int red=r.nextInt(255 - 0 + 1)+0;
        int green=r.nextInt(255 - 0 + 1)+0;
        int blue=r.nextInt(255 - 0 + 1)+0;
        packageViewHolder.tvPkgname.setTextColor(Color.rgb(red,green,blue));

        packageViewHolder.tvPkgname.setText(pkgs.getName());
        packageViewHolder.tvPkgPrice.setText("৳ "+pkgs.getPrice());
        packageViewHolder.tvPkgDuration.setText("Validity: "+pkgs.getDuration());


        packageViewHolder.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final Dialog dialog = new Dialog(mCtx);
                packageViewHolder.btnBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean ic = InsideCoverage();
                        if (ic) {
                            GetGeoLocation();
                            Toast.makeText(mCtx, "lc "+lc, Toast.LENGTH_SHORT).show();
                            if (lc!= null) {
                                buyPkg(pkgs);
                            }
//                            else {
//                                Toast.makeText(mCtx, "Sorry,Turn on location and allow permission", Toast.LENGTH_LONG).show();
//                            }
                        } else {
                            Toast.makeText(mCtx, "Sorry, You are out of Network Coverage", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });
    }

    private boolean InsideCoverage() {
        return true;
    }

    private void GetGeoLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mCtx, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //Toast.makeText(mCtx, "loc "+location.getLatitude(), Toast.LENGTH_SHORT).show();
                        lc =location;
                    }
                });
            }else{
                do {
                    requestPermission(new String[]{ACCESS_FINE_LOCATION}, 1);
                    requestPermission(new String[]{ACCESS_COARSE_LOCATION}, 2);
                } while (ContextCompat.checkSelfPermission(mCtx, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mCtx, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
            }


        } else {
            //version bellow 6//code to flow
        }

    }







    private void buyPkg(final PackagesInfo pkgs) {

        final Dialog dialog = new Dialog(mCtx, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_confirm_purchase);
        Button btton =  dialog.findViewById(R.id.purchase_alert_purchase_btn);
        TextView tvpn =  dialog.findViewById(R.id.purchase_alert_pkg_name);
        TextView tvpp =  dialog.findViewById(R.id.purchase_alert_pkg_price);
        TextView tvpd =  dialog.findViewById(R.id.purchase_alert_pkg_validity);
        final Switch renewSwtch =  dialog.findViewById(R.id.purchase_alert_renew_switch);

        tvpn.setText(pkgs.getName());
        tvpp.setText(pkgs.getPrice());
        tvpd.setText(pkgs.getDuration());
        final String[] renewStatus = {"0"};
        renewSwtch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    renewStatus[0] ="1";
                }

            }
        });

        btton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                demoBuyMethod(v,pkgs.getId(),renewStatus[0]);

            }
        });
        dialog.show();

    }

    private void demoBuyMethod(final View view,String pkgId,String renSts) {

        final ProgressDialog progressDialog = new ProgressDialog(mCtx,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Buying Package "+pkgId);
        progressDialog.show();

        String token= SharedPrefManager.getInstance(mCtx).getToken();

        Call<PurchaseResponse> call = RetrofitClient.getInstance().getApi().PurchasePkg(pkgId,"Bearer " + token, String.valueOf(lc.getLatitude()), String.valueOf(lc.getLongitude()),renSts);
        call.enqueue(new Callback<PurchaseResponse>() {
            @Override
            public void onResponse(Call<PurchaseResponse> call, Response<PurchaseResponse> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    String message=response.body().getMessage();
                    Toast.makeText(mCtx, message, Toast.LENGTH_SHORT).show();

                    //final Dialog dialog = new Dialog(mCtx);
                    final Dialog dialog = new Dialog(mCtx, R.style.DialogTheme);
                    dialog.setContentView(R.layout.dialog_success);
                    Button btton =  dialog.findViewById(R.id.purchase_success_home_btn);
                    btton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });
                    dialog.show();

//                    Snackbar.make(view, response.message(), Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(mCtx, response.message()+" "+response.body(), Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                    final Dialog dialog = new Dialog(mCtx, R.style.DialogTheme);
//                    dialog.setContentView(R.layout.dialog_success);
//                    Button btton =  dialog.findViewById(R.id.purchase_success_home_btn);
//                    ImageView img =  dialog.findViewById(R.id.purchase_success_status_image);
//                    TextView tv=dialog.findViewById(R.id.success_tv1);
//                    TextView tv2=dialog.findViewById(R.id.success_tv2);
//
//                    tv2.setVisibility(View.VISIBLE);
//                    img.setImageResource(R.drawable.error_sign);
//                    tv.setText(R.string.internet_error);
//
//                    btton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//
//                        }
//                    });
//                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<PurchaseResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(mCtx, "exp "+t.getMessage(), Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//                final Dialog dialog = new Dialog(mCtx);
//                dialog.setContentView(R.layout.dialog_success);
//                Button btton =  dialog.findViewById(R.id.purchase_success_home_btn);
//                ImageView img =  dialog.findViewById(R.id.purchase_success_status_image);
//                TextView tv=dialog.findViewById(R.id.success_tv2);
//
//                tv.setVisibility(View.VISIBLE);
//                img.setVisibility(View.VISIBLE);
//
//                btton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//
//                    }
//                });
//                dialog.show();
//                Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pkgList.size();
    }

    class PackageViewHolder extends RecyclerView.ViewHolder {

        TextView tvPkgname, tvPkgPrice, tvPkgDuration,tvPkgCreatedAt,tvPkgExpiresAt;
        Button btnBuy;


        public PackageViewHolder(View itemView) {
            super(itemView);

            tvPkgname = itemView.findViewById(R.id.pkg_card_pkg_name);
            tvPkgPrice = itemView.findViewById(R.id.pkg_card_pkg_price);
            tvPkgDuration = itemView.findViewById(R.id.pkg_card_pkg_validity);

            tvPkgCreatedAt = itemView.findViewById(R.id.pkg_card_created_at_tv);
            tvPkgExpiresAt = itemView.findViewById(R.id.pkg_card_expires_at_tv);

            btnBuy = itemView.findViewById(R.id.pkg_card_buy_btn);

        }
    }
    private void requestPermission(String[] strings, int i){
        ActivityCompat.requestPermissions((Activity) mCtx, new String[]{ACCESS_FINE_LOCATION}, 1);
    }
}
