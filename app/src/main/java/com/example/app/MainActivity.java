package com.example.app;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.RationaleDialogFragmentCompat;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity";

    private static final String[] REQUESTED_PERMISSIONS_ON_START = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int RC_REQUIRED_PERM = 125;

    private boolean waiting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        waiting = hasCurrentPermissionsRequest(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EasyPermissions.hasPermissions(this, REQUESTED_PERMISSIONS_ON_START)) {
            if (!waiting) {
                boolean b = shouldShowRequestPermissionRationale(this, REQUESTED_PERMISSIONS_ON_START);
                if (b) {
                    if (!isShowed(getSupportFragmentManager(), RationaleDialogFragmentCompat.TAG)) { // TODO too need check AppSettingDialog is shown
                        RationaleDialogFragmentCompat rationaleDialogFragmentCompat = RationaleDialogFragmentCompat.newInstance("To function properly, App needs your permission. Allow permission for App",
                                                                                                                                "ok",
                                                                                                                                "cancel",
                                                                                                                                -1,
                                                                                                                                RC_REQUIRED_PERM,
                                                                                                                                REQUESTED_PERMISSIONS_ON_START);
                        rationaleDialogFragmentCompat.show(getSupportFragmentManager(), RationaleDialogFragmentCompat.TAG);
                    }
                } else {
                    waiting = true;
                    ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS_ON_START, RC_REQUIRED_PERM);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        waiting = false;
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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

    private boolean hasCurrentPermissionsRequest(Bundle savedInstanceState) {
        return savedInstanceState != null && savedInstanceState.getBoolean("android:waiting", false);
    }

    private boolean shouldShowRequestPermissionRationale(Activity activity, String[] permissions) {
        boolean b = false;
        for (String permission : permissions)
            b |= ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        return b;
    }

    private boolean isShowed(FragmentManager fragmentManager, String tag) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        return fragment != null && !fragment.isDetached() && fragment.isVisible();

    }
}
