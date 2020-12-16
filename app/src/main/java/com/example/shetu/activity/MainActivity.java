package com.example.shetu.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.Assets.UpdateCheck;
import com.example.shetu.Assets.User;
import com.example.shetu.R;
import com.example.shetu.fragments.BuyPackagesFragment;
import com.example.shetu.fragments.MyPackagesFragment;
import com.example.shetu.fragments.home.HomeFragment;
import com.example.shetu.models.AuthUsersResponse;
import com.example.shetu.models.UpdateResponse;
import com.example.shetu.storage.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    private static final String TAG = MainActivity.class.getSimpleName();
    private FloatingActionButton fab;
    private ChipNavigationBar chipNavigationBar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);


        final FragmentManager[] fragmentManager = {getSupportFragmentManager()};
        fab = findViewById(R.id.fabx);
        fab.setVisibility(View.INVISIBLE);

        chipNavigationBar = findViewById(R.id.my_chip_bottom_nav);
        chipNavigationBar.setMenuResource(R.menu.bottom_menu);


        chipNavigationBar.showBadge(R.id.my_package_menu, 5);
        if (savedInstanceState == null) {
            FragmentManager frgm = getSupportFragmentManager();
            HomeFragment dshbrdFrgmnt = new HomeFragment();
            frgm.beginTransaction().replace(R.id.fragment_container, dshbrdFrgmnt).commit();
            chipNavigationBar.setItemSelected(R.id.dashboard_menu, true);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Notice Change", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent);
            }
        });


        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.dashboard_menu:
                        fragment = new HomeFragment();
                        break;
                    case R.id.my_package_menu:
                        fragment = new MyPackagesFragment();
                        break;
                    case R.id.buy_package_menu:
                        fragment = new BuyPackagesFragment();
                        break;
                }
                if (fragment != null) {
                    fragmentManager[0] = getSupportFragmentManager();
                    fragmentManager[0].beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                } else {
                    Log.e(TAG, "ErrorCreating Fragment");
                }
            }
        });


    }

    private void scanWifi() {
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(MainActivity.this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : results) {
                arrayList.add(scanResult.SSID + " - " + scanResult.capabilities);
                adapter.notifyDataSetChanged();
            }
        }
    };


    public boolean ConnectToNetworkWPA(String networkSSID, String password) {
        try {
            Toast.makeText(this, "in ", Toast.LENGTH_SHORT).show();
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain SSID in quotes

            conf.preSharedKey = "\"" + password + "\"";

            conf.status = WifiConfiguration.Status.ENABLED;

            Log.d("connecting", conf.SSID + " " + conf.preSharedKey);

            WifiManager wifiManagern = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManagern.addNetwork(conf);

            Log.d("after connecting", conf.SSID + " " + conf.preSharedKey);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManagern.disconnect();
                    wifiManagern.enableNetwork(i.networkId, true);
                    wifiManagern.reconnect();
                    Log.d("re connecting", i.SSID + " " + conf.preSharedKey);
                    //int linkSpeed= wifiInfo.getLinkSpeed();
                    break;
                }
            }


            //WiFi Connection success, return true
            return true;
        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        VerifyuserStatus();
    }

    private void VerifyuserStatus() {
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (SharedPrefManager.getInstance(this).isLoggedIn() && SharedPrefManager.getInstance(this).getUserName() == null) {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {//update local, load from local, data will be available both on and offline
            String token = SharedPrefManager.getInstance(this).getToken();
            Call<AuthUsersResponse> call = RetrofitClient.getInstance().getApi().getUserDetails("Bearer " + token);
            call.enqueue(new Callback<AuthUsersResponse>() {
                @Override
                public void onResponse(Call<AuthUsersResponse> call, Response<AuthUsersResponse> response) {
                    if (response.isSuccessful()) {
                        User user = response.body().getData();
                        User Nuser = new User(user.getId(), user.getName(), user.getMobile(), user.getMac_address(), user.getBirthdate(), user.getBalance(), user.getPurchase());
                        SharedPrefManager.getInstance(MainActivity.this).saveUser(Nuser);
                    }
                }

                @Override
                public void onFailure(Call<AuthUsersResponse> call, Throwable t) {
                }
            });

        }
        User user=null;
        try{
             user=SharedPrefManager.getInstance(MainActivity.this).getUser();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


}
//    PackageManager manager = this.getPackageManager();
//    PackageInfo info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
//Toast.makeText(this,
//        "PackageName = " + info.packageName + "\nVersionCode = "
//        + info.versionCode + "\nVersionName = "
//        + info.versionName + "\nPermissions = " + info.permissions, Toast.LENGTH_SHORT).show();