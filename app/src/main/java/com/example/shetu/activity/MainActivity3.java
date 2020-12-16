package com.example.shetu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.shetu.R;
import com.example.shetu.fragments.BuyPackagesFragment;
import com.example.shetu.fragments.MyPackagesFragment;
import com.example.shetu.fragments.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity3 extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FloatingActionButton fab;
    private ChipNavigationBar chipNavigationBar;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        final FragmentManager[] fragmentManager = {getSupportFragmentManager()};
        fab=findViewById(R.id.fab_vertical);
        fab.setVisibility(View.VISIBLE);

        chipNavigationBar=findViewById(R.id.my_chip_bottom_nav_vertical);
        chipNavigationBar.setMenuResource(R.menu.bottom_menu);

        chipNavigationBar.showBadge(R.id.my_package_menu, 5);
        if (savedInstanceState==null){
            FragmentManager frgm=getSupportFragmentManager();
            HomeFragment dshbrdFrgmnt=new HomeFragment();
            frgm.beginTransaction().replace(R.id.fragment_container_vertical,dshbrdFrgmnt).commit();
            chipNavigationBar.setItemSelected(R.id.dashboard_menu,true);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Notice Change", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent=new Intent(MainActivity3.this, MainActivity.class);
                startActivity(intent);
            }
        });

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment=null;
                switch (i){
                    case R.id.dashboard_menu:
                        fragment=new HomeFragment();
                        break;
                    case R.id.my_package_menu:
                        fragment=new MyPackagesFragment();
                        break;
                    case R.id.buy_package_menu:
                        fragment=new BuyPackagesFragment();
                        break;
                }
                if (fragment!=null){
                    fragmentManager[0] =getSupportFragmentManager();
                    fragmentManager[0].beginTransaction()
                            .replace(R.id.fragment_container_vertical,fragment)
                            .commit();
                }else{
                    Log.e(TAG,"ErrorCreating Fragment");
                }
            }
        });
    }
}