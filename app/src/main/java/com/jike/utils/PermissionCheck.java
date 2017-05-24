package com.jike.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.jike.activity.basic.BaseActivity;
import com.jike.application.JikeApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/16.
 */
public class PermissionCheck {
    public static final String READ_SDCARD_PERMISSION_TYPE = "read_sdcard";
    public static final String WRITER_SDCARD_PERMISSION_TYPE = "write_sdcard";
    public static final String READ_AND_WRITE_SDCARD_PERMISSION_TYPE = "read_and_write_sdcard";
    public static final String CAMERA_PERMISSION_TYPE = "camera";
    public static final String CONTACT_PERMISSION_TYPE = "contact";

    public static void checkPermission(BaseActivity activity, String type, int requestCode) {
        List<String> list = getPermissionList(type,activity,requestCode);

        if (!list.isEmpty()) {
            //申请权限
            ActivityCompat.requestPermissions(activity, list.toArray(new String[list.size()]), requestCode);
        }

    }
    public static List<String> getPermissionList(String type,ActivityCompat.OnRequestPermissionsResultCallback callback,int requestCode){
        List<String> list = new ArrayList<>();
        switch (type) {
            case READ_SDCARD_PERMISSION_TYPE:
                if (ContextCompat.checkSelfPermission(JikeApplication.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    if (callback != null) {
                        callback.onRequestPermissionsResult(requestCode, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new int[]{PackageManager.PERMISSION_GRANTED});
                    }
                }
                break;
            case WRITER_SDCARD_PERMISSION_TYPE:
                if (ContextCompat.checkSelfPermission(JikeApplication.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    if (callback != null) {
                        callback.onRequestPermissionsResult(requestCode, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new int[]{PackageManager.PERMISSION_GRANTED});
                    }
                }
                break;
            case CAMERA_PERMISSION_TYPE:
                if (ContextCompat.checkSelfPermission(JikeApplication.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    list.add(Manifest.permission.CAMERA);
                } else {
                    if (callback != null) {
                        callback.onRequestPermissionsResult(requestCode, new String[]{Manifest.permission.CAMERA}, new int[]{PackageManager.PERMISSION_GRANTED});
                    }
                }

                break;
            case CONTACT_PERMISSION_TYPE:
                if (ContextCompat.checkSelfPermission(JikeApplication.getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    list.add(Manifest.permission.READ_CONTACTS);
                } else {
                    if (callback != null) {
                        callback.onRequestPermissionsResult(requestCode, new String[]{Manifest.permission.READ_CONTACTS}, new int[]{PackageManager.PERMISSION_GRANTED});
                    }
                }
                break;
            case READ_AND_WRITE_SDCARD_PERMISSION_TYPE:
                boolean canRead = true, canWrite = true;
                if (ContextCompat.checkSelfPermission(JikeApplication.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    canRead = false;
                }
                if (ContextCompat.checkSelfPermission(JikeApplication.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    canWrite = false;
                }
                if (canRead && canWrite) {
                    if (callback != null) {
                        callback.onRequestPermissionsResult(requestCode, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new int[]{PackageManager.PERMISSION_GRANTED});
                    }
                }
                break;
        }
        return  list;
    }

    public interface CallBack {
        void onSuccess(int resultCode);
    }
}
