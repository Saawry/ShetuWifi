package com.example.shetu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shetu.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class TestExpand extends AppCompatActivity {

    private Button t1;
    private boolean vgbl;
    private String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_expand);
        t1 = findViewById(R.id.te_tv);

        vgbl = false;
        t1.setText("permission");
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPermission2();
            }
        });


    }


    private void GetPermission2() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(this, PERMISSIONS)) {

                requestPermissions(PERMISSIONS, 1);
                //ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }
    }


//    private void GetPermission() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(TestExpand.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                //Already allowed//code to flow
//            }
//            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, 1);
//        } else {
//            //version bellow 6//code to flow
//        }
//    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Accepted Now//code in flow
            } else {
                //denied//explain deny loss
                boolean showRationale = shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION);
                if (!showRationale) {
                    // user also CHECKED "never ask again"
                    Toast.makeText(this, "not rational", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(TestExpand.this)
                            .setMessage("Allow permission from Settings")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getApplication().getPackageName(), null);
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
                    new AlertDialog.Builder(TestExpand.this)
                            .setMessage("You need to allow permission to use Shetu")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //call following method if permission is must here
                                    requestPermissions(permissions,requestCode);
                                }
                            }).show();
                }
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


}

