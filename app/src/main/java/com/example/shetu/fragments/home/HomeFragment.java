
package com.example.shetu.fragments.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;

import com.example.shetu.Assets.UpdateCheck;
import com.example.shetu.activity.MainActivity;
import com.example.shetu.activity.ProfileActivity;
import com.example.shetu.models.UpdateResponse;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.TextView;
import android.widget.Toast;

import com.example.shetu.Assets.LocalNetDetails;
import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.R;
import com.example.shetu.fragments.BuyPackagesFragment;
import com.example.shetu.models.DfltResponse;
import com.example.shetu.models.GetnetResponse;
import com.example.shetu.storage.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment {

    private static final int FINE_LOC_RQ_CD=1;
    private static final int COARSE_LOC_RQ_CD=2;
    private String BSSID;
    //private WifiInfo wifiInfo;
    //private WifiManager wifiManager;

    private View view;

    private String snid = "Shetu";

    private ImageView connectImgView;
    private Button connectbtn;

    private boolean SNconnected = false;

    private String ssid, password;
    private int wifiNetId;
    //private WifiConfiguration wifiConfig;

    private TextView blncTv, pkgnameTv, validityTv, buyPkgTv, tvAreaCover, tvUses;


    @SuppressLint("WifiManagerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        view = inflater.inflate(R.layout.fragment_home, container, false);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

        //WifiManager.calculateSignalLevel(int);




        connectImgView = view.findViewById(R.id.connect_image_btn);
        connectbtn = view.findViewById(R.id.home_fragment_connectivity_btn);

        blncTv = view.findViewById(R.id.home_fragment_balance_tv);
        buyPkgTv = view.findViewById(R.id.home_frag_buy_pkg_lnk);
        pkgnameTv = view.findViewById(R.id.home_fragment_pkg_name_tv);
        validityTv = view.findViewById(R.id.home_fragment_validity_tv);
        tvAreaCover = view.findViewById(R.id.frg_home_area_coverage_tv);
        tvUses = view.findViewById(R.id.frg_home_area_coverage_tv);

        GetPermission();
       //WifiScannerResult();

        ShetuNetworkCoverage();


//        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        SharedPrefManager.getInstance(getContext()).getThisKeyvalue("balance");
        blncTv.setText(SharedPrefManager.getInstance(getContext()).getThisKeyvalue("balance"));
        pkgnameTv.setText(SharedPrefManager.getInstance(getContext()).getThisKeyvalue("currentPkg"));
        validityTv.setText(SharedPrefManager.getInstance(getContext()).getThisKeyvalue("pkgValidity"));

        SNconnected = ShetuIsConnected();//must return boolean value

        if (SNconnected) {
            //Toast.makeText(getContext(), "connected to setu", Toast.LENGTH_SHORT).show();
            connectImgView.setImageResource(R.drawable.connected_img);
            connectbtn.setText("Disconnect");
        } else {
            //Toast.makeText(getContext(), " not connected to setu", Toast.LENGTH_SHORT).show();
            connectImgView.setImageResource(R.drawable.disconnected_img);
            connectbtn.setText("Connect Now");
        }

        buyPkgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
                //ShowBuyPkgFrg();
            }
        });


        connectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for update, else connectivity, else connect

                SharedPrefManager.getInstance(getContext()).removeData("newSSid");
                SharedPrefManager.getInstance(getContext()).removeData("newPass");
                WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }
                boolean nic = NetworksIsConnected();
                if (!nic) {
                    Toast.makeText(getContext(), "Please turn on any network Connection", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!SNconnected) {
                    GetLocalNetInfo();

                    boolean fl = ConnectToWifi();
                    //StoreMac();
                    if (fl) {
                        connectbtn.setText("Disconnect");
                        connectImgView.setImageResource(R.drawable.connected_img);
                    }
                } else {
                    //StoreSession();
                    ForgetNet();
                    connectbtn.setText("Connect now");
                    connectImgView.setImageResource(R.drawable.disconnected_img);
                }
            }

        });
        return view;
    }


    private void WifiScannerResult() {
        final WifiManager wifiManager = (WifiManager)
                getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    //scanSuccess();
                    List<ScanResult> results = wifiManager.getScanResults();
                } else {
                    // scan failure handling

                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (success) {
            // scan failure handling
            List<ScanResult> results2 = wifiManager.getScanResults();
        }

    }


    private void ShetuNetworkCoverage() {

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {

                boolean success2 = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success2) {
                    onScanSuccess();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(wifiScanReceiver, intentFilter);


    }

    private void onScanSuccess() {
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        List<ScanResult> results = wifiManager.getScanResults();
        for (ScanResult sn : results) {
            if (sn.SSID.contains("Shetu") || sn.SSID.contains("SHETU") || sn.SSID.contains("Decoders Squad")) {
                //Toast.makeText(getContext(), "Inside Area Coverage", Toast.LENGTH_SHORT).show();
                tvAreaCover.setText(R.string.disconnect_msg_str1);
                //tvAreaCover.setTextColor(getResources().getColor(R.color.Primary, Resources.Theme.AppThemeLight));
                tvUses.setText(R.string.disconnect_msg_str2);
                break;
            } else {
                tvAreaCover.setText(R.string.disconnect_msg_str1);
            }
        }


    }

    private void ShowBuyPkgFrg() {

        Fragment fragment = new BuyPackagesFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.nav_host_fragment, fragment, "hmm");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

//        NavController navController = getActivity().Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_buy_packages);
    }

    private boolean NetworksIsConnected() {




        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return   activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        //activeNetwork.equals(true);
        //        if (v) {
//            Toast.makeText(getContext(), "net connected", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return false;
//        if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//           String CS= "phnData";
//        }
//        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//           String CS= "wifi";
//        }
    }

    private boolean ShetuIsConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                WifiManager wmr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wi = wmr.getConnectionInfo();
                if (wi.getSSID().contains(snid)) {
                    //Toast.makeText(getContext(), "contains "+wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            //return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    private void GetLocalNetInfo() {

        Call<GetnetResponse> call = RetrofitClient.getInstance().getApi().GetNetwork();
        call.enqueue(new Callback<GetnetResponse>() {
            @Override
            public void onResponse(Call<GetnetResponse> call, Response<GetnetResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        LocalNetDetails networkInfo = response.body().getData();

                        SharedPrefManager.getInstance(getContext()).saveData("newSSid", networkInfo.getSsid());
                        SharedPrefManager.getInstance(getContext()).saveData("newPass", networkInfo.getSsid());
                        //Toast.makeText(getContext(), xxxx+zzzzzz, Toast.LENGTH_SHORT).show();
                        //Snackbar.make(v, ssid+"--"+password, Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                    }
                } else {
                    //Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetnetResponse> call, Throwable t) {
                //Toast.makeText(getContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean ConnectToWifi() {
        String abc = SharedPrefManager.getInstance(getContext()).getData("newSSid");
        String cba = SharedPrefManager.getInstance(getContext()).getData("newPass");

        WifiConfiguration wifiConfig;
        WifiManager wmr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wi = wmr.getConnectionInfo();

        wmr.setWifiEnabled(true);
        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", "Shetu-1");
        wifiConfig.preSharedKey = String.format("\"%s\"", "shetu1234");
//        wifiConfig.SSID = String.format("\"%s\"", abc);
//        wifiConfig.preSharedKey = String.format("\"%s\"", cba);
        //Toast.makeText(getContext(), "SSID: " + wifiConfig.SSID + " pass: " + wifiConfig.preSharedKey, Toast.LENGTH_SHORT).show();
        try {
            wifiNetId = wmr.addNetwork(wifiConfig);
            wmr.disconnect();
            wmr.enableNetwork(wifiNetId, true);
            wmr.reconnect();
            //Toast.makeText(getContext(), "SSID: " + wi.getSSID(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        String sid = wi.getSSID();
        if (sid.contains("Shetu")) {
            SNconnected = true;
            return true;
        }

        return false;

    }

    private void ForgetNet() {

        String abc = SharedPrefManager.getInstance(getContext()).getData("newSSid");
        String cba = SharedPrefManager.getInstance(getContext()).getData("newPass");

        WifiManager wmr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wi = wmr.getConnectionInfo();
        WifiConfiguration wifiConfig;
        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", abc);
        wifiConfig.preSharedKey = String.format("\"%s\"", cba);
        wmr.disconnect();
        wmr.removeNetwork(wifiNetId);
        wmr.setWifiEnabled(false);


        //Toast.makeText(getContext(), "SSID: " + wifiConfig.SSID + " pass: " + wifiConfig.preSharedKey, Toast.LENGTH_SHORT).show();
        wifiNetId = wmr.addNetwork(wifiConfig);
        SNconnected = false;

    }

    private void StoreMac() {
        WifiManager wmr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wi = wmr.getConnectionInfo();
        BSSID = wi.getBSSID();
        String token = SharedPrefManager.getInstance(getContext()).getToken();
        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().StoreMac("Bearer " + token, BSSID);
        call.enqueue(new Callback<DfltResponse>() {
            @Override
            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
                if (!response.isSuccessful()) {
                    //Toast.makeText(getContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DfltResponse> call, Throwable t) {
                //Toast.makeText(getContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void StoreSession() {
        WifiManager wmr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wi = wmr.getConnectionInfo();
        BSSID = wi.getBSSID();
        //local time and mac
        Call<DfltResponse> call = RetrofitClient.getInstance().getApi().StoreSession("00000", BSSID, "2020-09-21 20:58:06", "2020-09-21 23:00:11", "100");
        call.enqueue(new Callback<DfltResponse>() {
            @Override
            public void onResponse(Call<DfltResponse> call, Response<DfltResponse> response) {
                if (response.code() != 200) {
                    //Toast.makeText(getContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DfltResponse> call, Throwable t) {
                //Toast.makeText(getContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GetPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, 1);
            }


        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                //Already allowed//code to flow
//                ShetuNetworkCoverage();
//            }
//            requestPermissions(new String[]{ACCESS_COARSE_LOCATION}, 2);
//        }

    }

    private void CheckVersionUpdate() {
        final boolean[] flag = {false};
        final String[] url = new String[1];
        Call<UpdateResponse> call = RetrofitClient.getInstance().getApi().UpdateCheck();
        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                if (response.isSuccessful()) {
                    UpdateCheck ur = response.body().getData();
                    flag[0] = ur.isForce_update();
                    url[0] = ur.getUrl();
                    if (flag[0]){
                        //Toast.makeText(getContext(), "Update Available", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {

            }
        });
        if (flag[0]) {
            ShowAlertDialog(url[0], flag[0]);
        }
    }

    private void ShowAlertDialog(final String url, final boolean flag) {
        final Dialog dialog;
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.update_alert_dialog);
        Button update = dialog.findViewById(R.id.alert_positive_btn);
        Button later = dialog.findViewById(R.id.alert_negative_btn);
        if (flag) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            later.setVisibility(View.INVISIBLE);
        }
        // if button is clicked, close the custom dialog
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Accepted Now//code in flow
                //ShetuNetworkCoverage();
            } else {
                //denied//explain deny loss
                boolean showRationale = shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION);
                if (!showRationale) {
                    // user also CHECKED "never ask again"
                    new AlertDialog.Builder(getContext())
                            .setMessage("Allow permission from Settings")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, 1);
                                }
                            })
//                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
                            .setCancelable(false)
                            .create()
                            .show();

                } else {
                    new AlertDialog.Builder(getContext())
                            .setMessage("You need to allow permission to use Shetu")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    requestPermissions(permissions, requestCode);
                                }
                            }).show();
                }
            }

        }
            else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //GetPermission();
    }

}