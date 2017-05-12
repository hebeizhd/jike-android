package com.jike.utils;

/**
 * Created by Administrator on 2016/4/13.
 */
public class Constants {
    /**
     * socket 端口号
     */
    public static final String API_URL_ROOT = "http://sj.jikejiazhuang.com";
    public static final String DEALER_MOBILE_URL_ROOT = "http://sj.jikejiazhuang.com/jike-mobile";
    //public static final String API_URL_ROOT = "http://192.168.0.14:8089";
    //public static final String DEALER_MOBILE_URL_ROOT = "http://192.168.0.14:8080/jike-mobile";
    public static final String IMAGE_URL_ROOT = "http://jk-images.oss-cn-shanghai.aliyuncs.com";
    public static final String LOCAL_FILE_ROOT = "/file/";

    public static final int LOCAL_RATE = 1000000;

    public static final String SUCCESS_CODE = "0";
    public static final String FAIL_CODE = "1";
    public static final String LOGIN_INVALIDATE = "9";
    public static final int SUCCESS_INTEGER_CODE = -1;
    public static final int FAIL_INTEGER_CODE = 0;

    public static final String HTTP_ERROR_MESSAGE = "连接超时,请检查网络设置";
    public static final String LOGIN_INVALIDATE_MESSAGE = "登录失效，请重新登陆";




    //请求登陆页面code
    public static final int START_REQUEST_CODE_LOGIN = 9999;

    public static String getUrl(String url) {
        return Constants.API_URL_ROOT + "/" + url;
    }
}
