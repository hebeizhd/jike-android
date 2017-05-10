package com.jike.utils;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.jike.entity.QueryParam;
import com.jike.entity.Result;

import org.apache.commons.lang3.StringUtils;


/**
 * 查询工具类
 * Created by Administrator on 2016/4/15.
 */
public class QueryTask<T> extends AsyncTask<QueryParam,Integer,Result<T>> {
    private CallBack handler;
    private String url;
    private Class<T> clazz;
    /**
     * 构造方法
     * @param handler 当前消息处理
     * @param url 请求url
     */
    public QueryTask(CallBack handler, String url, Class<T> clazz) {
        this.handler = handler;
        this.url = url;
        this.clazz = clazz;
    }

    @Override
    protected void onPreExecute() {
        if(handler != null){
            handler.onPreExecute();
        }
    }
    @Override
    protected Result<T> doInBackground(QueryParam... params) {
        String url = HttpUtils.getUrl(this.url);
        QueryParam param = null;
        if(params == null || params.length == 0 ){
            param  = new QueryParam();
        }else{
            param = params[0];
        }
        String result = HttpUtils.doPost(url, param);
        if(StringUtils.isEmpty(result)) {
            return null;
        }
        Result<T> resultObject = Result.fromJson(result,this.clazz);
        return resultObject;
    }
    @Override
    protected void onPostExecute(final Result<T> result) {
        if(handler != null){
            handler.onCancelExecute();
        }
        if(result == null) {
            handler.onFail(Constants.HTTP_ERROR_MESSAGE);
        } else {
            //请求成功
            if (Constants.SUCCESS_CODE.equals(result.getCode())) {
                if(handler != null){
                    handler.onSuccess(result.getData());
                }
            } else if(Constants.LOGIN_INVALIDATE.equals(result.getCode())){
            } else{
                if(handler != null){
                    handler.onFail(result.getMsg());
                }
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.i("TAG", "------" + values[0]);
    }
    public static abstract class CallBack<T>{
        public void onPreExecute(){
        }
        public void onCancelExecute(){
        }
        public void onSuccess(T t){}
        public void onFail(String msg){
        }
    }
}
