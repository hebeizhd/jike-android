package com.jike.application;

import android.app.Application;
import android.content.Context;

/**
 * 拉煤车全局工具
 * Created by Administrator on 2016/4/11.
 */
public class JikeApplication extends Application {
    private static Context context;
    private static String token;
    private static Integer statusColor;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        JikeApplication.token = token;
    }

    public static Integer getStatusColor() {
        return statusColor;
    }

    public static void setStatusColor(Integer statusColor) {
        JikeApplication.statusColor = statusColor;
    }
}
