package com.jike.utils;

/**
 * 网络工具类
 *
 * @author malinkang
 */

public class HttpUtils {
    private static final int TIMEOUT_IN_MILLIONS = 5000;

    public static String getUrl(String url) {
        return Constants.API_URL_ROOT + "/" + url;
    }

}