package com.example.shetu.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import com.example.shetu.Assets.RetrofitClient;
import com.example.shetu.Assets.User;
import com.example.shetu.R;
import com.example.shetu.models.AuthUsersResponse;
import com.example.shetu.storage.SharedPrefManager;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;///
    private AppBarConfiguration mAppBarConfiguration;
    private TextView navMobileTv, navBalanceTv;
    private String BSSID;
    private WifiInfo wifiInfo;
    private WifiManager wifiManager;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        BSSID = wifiInfo.getBSSID();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Shetu");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Notice Change", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        //navigationView.setItemIconTintList(null);

        //mDrawerToggle.setHomeAsUpIndicator(R.drawable.oval_2);

//        mDrawerToggle = new ActionBarDrawerToggle(this, drawer,
//                R.drawable.btn_back_border, // Navigation menu toggle icon
//                R.string.navigation_drawer_open, // Navigation drawer open description
//                R.string.navigation_drawer_close // Navigation drawer close description
//        );


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_my_packages, R.id.nav_buy_packages, R.id.nav_profile)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        View headerView = navigationView.getHeaderView(0);
//        TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);

        navMobileTv = headerView.findViewById(R.id.header_mobile_tv);
        navBalanceTv = headerView.findViewById(R.id.header_balance_tv);

        //navMobileTv.setText(user.getMobile());
        //navBalanceTv.setText("Balance: " + user.getBalance());


        //try this  for more option which doesn't have fragment
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                NavMenuItemSelector(menuItem);
//                return false;
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (SharedPrefManager.getInstance(this).getUserName() == null) {
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
                        navMobileTv.setText(user.getName());
                        navBalanceTv.setText("Balance: " + R.string.bdt_symbol + user.getBalance());

                        User Nuser = new User(user.getId(), user.getName(), user.getMobile(), user.getMac_address(), user.getBirthdate(), user.getBalance(), user.getPurchase());
                        SharedPrefManager.getInstance(MainActivity2.this).saveUser(Nuser);

                    }
                }

                @Override
                public void onFailure(Call<AuthUsersResponse> call, Throwable t) {
                }
            });

            try {
                User userString = SharedPrefManager.getInstance(MainActivity2.this).getUser();
//                String userString = SharedPrefManager.getInstance(MainActivity2.this).getData();
//                Gson gson = new Gson();
//                User userInfo = gson.fromJson(userString, User.class);
                //Toast.makeText(this, userInfo.getMobile()+" "+userInfo.getBalance(), Toast.LENGTH_SHORT).show();

                navMobileTv.setText(userString.getName());
                navBalanceTv.setText(userString.getMobile());
                //navBalanceTv.setText("Balance: " +R.string.bdt_symbol+ userString.getBalance());
            } catch (Exception ne) {
                //Toast.makeText(this, ne.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {

        //super.onBackPressed();

        int count = getFragmentManager().getBackStackEntryCount();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fr = fragmentManager.findFragmentByTag("hmm");

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        }
        //else {//if fragment id 0
//            //onStart();
////            Fragment fragment = new HomeFragment();
////            //Toast.makeText(this, String.valueOf(fragmentManager.getPrimaryNavigationFragment().getId()+" "+count), Toast.LENGTH_SHORT).show();
////
////
////            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
////            fragmentTransaction.addToBackStack(null);
////            fragmentTransaction.commit();
////            super.onBackPressed();
//        }

//        if (count > 0) {
//            getFragmentManager().popBackStack();
//            Intent i = new Intent(this, MainActivity2.class);
//            startActivity(i);
//            MainActivity2.this.overridePendingTransition(0, 0);
//
//        }

//        if (count > 0) {//&& home fragment
//            FragmentManager fragmentManager = this.getSupportFragmentManager();
//            String ss= String.valueOf(fragmentManager.getBackStackEntryAt(1));
//            if (ss.equals("ShowBuyPkgFrg")){
//                Toast.makeText(this, "ShowBuyPkgFrg", Toast.LENGTH_SHORT).show();
//                Fragment fragment = new HomeFragment();
//                //FragmentManager fragmentManager = this.getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
//
//                fragmentTransaction.commit();
//                return;
//            }
//            getFragmentManager().popBackStack();
//
//
//            //additional code
//        } else


//        if (count==0){
//            //getSupportFragmentManager().popBackStackImmediate();
//            Toast.makeText(this, String.valueOf(count), Toast.LENGTH_SHORT).show();
//            Fragment fragment = new HomeFragment();
//                FragmentManager fragmentManager = this.getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//               fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
//
//
//              fragmentTransaction.commit();
//            //super.onBackPressed();
//        }


    }

}