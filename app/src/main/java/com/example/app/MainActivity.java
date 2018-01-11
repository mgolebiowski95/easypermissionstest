package com.example.app;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity";

    private static final String[] REQUESTED_PERMISSIONS_ON_START = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int RC_REQUIRED_PERM = 125;

    private boolean hasCurrentPermissionsRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasCurrentPermissionsRequest = hasCurrentPermissionsRequest(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasCurrentPermissionsRequest) {
            if (!EasyPermissions.hasPermissions(this, REQUESTED_PERMISSIONS_ON_START)) {
                EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, RC_REQUIRED_PERM, REQUESTED_PERMISSIONS_ON_START)
                                                           .setRationale("To function properly, App needs your permission. Allow permission for App")
                                                           .setTheme(android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                                                           .build());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private boolean hasCurrentPermissionsRequest(Bundle savedInstanceState) {
        return savedInstanceState != null && savedInstanceState.getBoolean("android:hasCurrentPermissionsRequest", false);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (!EasyPermissions.hasPermissions(this, REQUESTED_PERMISSIONS_ON_START)) {
            Log.d(TAG, "onPermissionsDenied somePermissionPermanentlyDenied have not all the required permissions");
            new AppSettingsDialog.Builder(this)
                    .setThemeResId(android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                    .setNegativeButton("close app")
                    .setPositiveButton("settings")
                    .build().show();
        }
    }
}
