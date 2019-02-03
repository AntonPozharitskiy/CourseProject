package com.example.asus.videocontent.PermissionChecker;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Validator {

    public static final int REQUEST_CODE_CAM = 1;
    public static boolean READ_GRANTED = false;

    public static void Requests(Activity context) {
        int camPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int hasWritePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (camPermission == PackageManager.PERMISSION_GRANTED && hasWritePermission == PackageManager.PERMISSION_GRANTED) {
            READ_GRANTED = true;
        } else {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_CAM);
        }
    }


}
