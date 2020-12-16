package com.example.shetu.adapters;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shetu.Assets.MyPackages;
import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.R;
import com.example.shetu.models.DfltResponse;
import com.example.shetu.models.PurchaseResponse;
import com.example.shetu.storage.SharedPrefManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPackageAdapter extends RecyclerView.Adapter<MyPackageAdapter.MyPackageViewHolder> {
    private Context mCtx;
    private List<MyPackages> pkgList;
    private FusedLocationProviderClient client;
    Location lc;

    public MyPackageAdapter(Context mCtx, List<MyPackages> pkgList, FusedLocationProviderClient client, Location lc) {
        this.mCtx = mCtx;
        this.pkgList = pkgList;
        this.client = client;
        this.lc = lc;
    }

//    public MyPackageAdapter(Context mCtx, List<MyPackages> pkgList) {
//        this.mCtx = mCtx;
//        this.pkgList = pkgList;
//    }

    @NonNull
    @Override
    public MyPackageAdapter.MyPackageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.my_pkg_layout, viewGroup, false);
        client = LocationServices.getFusedLocationProviderClient(mCtx);
        return new MyPackageAdapter.MyPackageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyPackageAdapter.MyPackageViewHolder packageViewHolder, int position) {
        final MyPackages pkgs = pkgList.get(position);


        Random r = new Random();
        int red = r.nextInt(255 - 0 + 1) + 0;
        int green = r.nextInt(255 - 0 + 1) + 0;
        int blue = r.nextInt(255 - 0 + 1) + 0;

//        GradientDrawable draw = new GradientDrawable();
//        draw.setShape(GradientDrawable.RECTANGLE);
//        draw.setColor(Color.rgb(red,green,blue));
//        packageViewHolder.itemView.setBackground(draw);///////////////item view is view name
        packageViewHolder.tvPkgname.setTextColor(Color.rgb(red, green, blue));///////////////item view is view name

        //packageViewHolder.tvPkgCreatedAt.setText("Purchase Date: "+pkgs.getCreated_at());
        packageViewHolder.tvPkgExpiresAt.setText("Valid Till: " + pkgs.getExpire_in());
        packageViewHolder.tvPkgname.setText(pkgs.getName());
        packageViewHolder.tvPkgPrice.setText("৳" + pkgs.getPrice());
        packageViewHolder.tvPkgDuration.setText("Validity " + pkgs.getDuration()+" days");
        final String[] flg = {pkgs.getAuto_renew()};
        if (flg[0].equals("1")){
            packageViewHolder.autoRenewSwch.setActivated(true);
        }

        ///set switch based on response
        ///to buy, check ssid list wheather in shetu coverage

        packageViewHolder.autoRenewSwch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoRenewStateChange(pkgs.getId());
                if (flg[0].equals("1")){
                    flg[0] ="0";
                    packageViewHolder.autoRenewSwch.setActivated(false);
                }else{
                    flg[0] ="1";
                    packageViewHolder.autoRenewSwch.setActivated(true);
                }
            }
        });
//        packageViewHolder.autoRenewSwch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                if (isChecked) {
//                    packageViewHolder.autoRenewSwch.setChecked(false);
//                    AutoRenewStateChange(pkgs.getId());
//                    packageViewHolder.autoRenewSwch.toggle();
//                }
//
//            }
//        });


        packageViewHolder.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //loading bar
                boolean ic = InsideCoverage();
                if (ic) {
                     GetGeoLocation();
                    if (lc != null) {
                        buyPkg(pkgs);
                    } else {
                        Toast.makeText(mCtx, "Sorry,Turn on location and allow permission", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    Toast.makeText(mCtx, "Sorry, You are out of Network Coverage", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void buyPkg(final MyPackages pkgs) {
        //final Dialog dialog = new Dialog(mCtx);
        final Dialog dialog = new Dialog(mCtx, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_confirm_purchase);
        Button btton = dialog.findViewById(R.id.purchase_alert_purchase_btn);
        TextView tvpn = dialog.findViewById(R.id.purchase_alert_pkg_name);
        TextView tvpp = dialog.findViewById(R.id.purchase_alert_pkg_price);
        TextView tvpd = dialog.findViewById(R.id.purchase_alert_pkg_validity);
        final Switch renewSwtch = dialog.findViewById(R.id.purchase_alert_renew_switch);

        tvpn.setText(pkgs.getName());
        tvpp.setText(pkgs.getPrice());
        tvpd.setText(pkgs.getExpire_in());
        final String[] renewStatus = {"0"};

        renewSwtch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(mCtx, "Switched", Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    renewStatus[0] = "1";
                } else {
                    renewStatus[0] = "0";
                }

            }
        });
        btton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                demoBuyMethod(v, pkgs.getId(), renewStatus[0]);

            }
        });
        dialog.show();
    }

    private void GetGeoLocation() {

        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                lc=location;
            }
        });

    }

    private boolean InsideCoverage() {
        return true;
    }

    private void AutoRenewStateChange(String id) {
        String token= SharedPrefManager.getInstance(mCtx).getToken();
        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().SetRenuePkg(id,"Bearer " + token);
        call.enqueue(new Callback<DfltResponse>() {
            @Override
            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
                Toast.makeText(mCtx, "Changed Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DfltResponse> call, Throwable t) {
                Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void demoBuyMethod(final View view,String pkgId,String renSts) {

        final ProgressDialog progressDialog = new ProgressDialog(mCtx,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Buying Package "+pkgId);
        progressDialog.show();


        String token= SharedPrefManager.getInstance(mCtx).getToken();

        Call<PurchaseResponse> call = RetrofitClient.getInstance().getApi().PurchasePkg(pkgId,"Bearer " + token, String.valueOf(lc.getLatitude()), String.valueOf(lc.getLongitude()), renSts);
        call.enqueue(new Callback<PurchaseResponse>() {
            @Override
            public void onResponse(Call<PurchaseResponse> call, Response<PurchaseResponse> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    String message=response.body().getMessage();
                    Toast.makeText(mCtx, message, Toast.LENGTH_SHORT).show();

//                    Snackbar.make(view, response.message(), Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
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
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(mCtx, "ss  "+response.message()+" "+response.body(), Toast.LENGTH_SHORT).show();
                    //final Dialog dialog = new Dialog(mCtx);
//                    final Dialog dialog = new Dialog(mCtx, R.style.DialogTheme);
//                    dialog.setContentView(R.layout.dialog_success);
//                    Button btton =  dialog.findViewById(R.id.purchase_success_home_btn);
//                    ImageView img =  dialog.findViewById(R.id.purchase_success_status_image);
//                    TextView tv=dialog.findViewById(R.id.success_tv1);
//                    TextView tv2=dialog.findViewById(R.id.success_tv2);
//
//
//                    tv2.setVisibility(View.VISIBLE);
//                    img.setImageResource(R.drawable.error_sign);
//                    tv.setText(R.string.internet_error);
//
//                    btton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
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

    class MyPackageViewHolder extends RecyclerView.ViewHolder {

        TextView tvPkgname, tvPkgPrice, tvPkgDuration,tvPkgCreatedAt,tvPkgExpiresAt;
        Button btnBuy;
        Switch autoRenewSwch;
        ImageView hl1,hl2;

        public MyPackageViewHolder(View itemView) {
            super(itemView);

            hl1 = itemView.findViewById(R.id.hl_1);
            hl2 = itemView.findViewById(R.id.hl_2);
            autoRenewSwch = itemView.findViewById(R.id.pkg_renew_switch);

            tvPkgname = itemView.findViewById(R.id.pkg_card_pkg_name);
            tvPkgPrice = itemView.findViewById(R.id.pkg_card_pkg_price);
            tvPkgDuration = itemView.findViewById(R.id.pkg_card_pkg_validity);

            tvPkgCreatedAt = itemView.findViewById(R.id.pkg_card_created_at_tv);
            tvPkgExpiresAt = itemView.findViewById(R.id.pkg_card_expires_at_tv);

            btnBuy = itemView.findViewById(R.id.pkg_card_buy_btn);


            tvPkgCreatedAt.setVisibility(View.VISIBLE);
            tvPkgExpiresAt.setVisibility(View.VISIBLE);
            hl1.setVisibility(View.VISIBLE);
            hl2.setVisibility(View.VISIBLE);
            autoRenewSwch.setVisibility(View.VISIBLE);
            btnBuy.setVisibility(View.VISIBLE);

        }
    }
}
