package com.jike.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jike.application.JikeApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/18.
 */
public class SystemUtils {
    public static Map<String, String> getSystemInfo() {
        Map<String, String> info = new HashMap<>();
        TelephonyManager tm = (TelephonyManager) JikeApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        //电话号码
        Log.d(SystemUtils.class.toString(), "phoneNumber : " + tm.getLine1Number());
        info.put("phoneNumber", tm.getLine1Number());
        //imei
        Log.d(SystemUtils.class.toString(), "imei : " + tm.getDeviceId());
        info.put("imei", tm.getDeviceId());
        //运营商
        Log.d(SystemUtils.class.toString(), "运营商 : " + tm.getNetworkOperatorName());
        info.put("operatorName", tm.getNetworkOperatorName());
        //sim卡序列号
        Log.d(SystemUtils.class.toString(), "simNumber : " + tm.getSimSerialNumber());
        info.put("simNumber", tm.getSimSerialNumber());
        //imsi
        Log.d(SystemUtils.class.toString(), "imsi : " + tm.getSubscriberId());
        info.put("imsi", tm.getSubscriberId());
        //
        Log.d(SystemUtils.class.toString(), "manufacturer : " + Build.MANUFACTURER);
        info.put("manufacturer", Build.MANUFACTURER);
        //brand
        Log.d(SystemUtils.class.toString(), "brand : " + Build.BRAND);
        info.put("brand", Build.BRAND);
        //model
        Log.d(SystemUtils.class.toString(), "model : " + Build.MODEL);
        info.put("model", Build.MODEL);
        //osVersion
        Log.d(SystemUtils.class.toString(), "osVersion : " + Build.VERSION.RELEASE);
        info.put("osVersion", Build.VERSION.RELEASE);
        return info;
    }

    public static int getVersionCode() {
        try {
            PackageManager manager = JikeApplication.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(JikeApplication.getContext().getPackageName(), 0);
            int code = info.versionCode;
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static String getVersionName() {
        try {
            PackageManager manager = JikeApplication.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(JikeApplication.getContext().getPackageName(), 0);
            String name = info.versionName;
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return "unknown";
        }
    }
}
