package com.jike.entity;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/4/13.
 */
public class QueryParam extends HashMap<String,String>{
    public String toQueryString(){
        StringBuilder builder = new StringBuilder();
        for(Entry<String,String> entry : this.entrySet()){
            if (builder.length() == 0){
                builder.append(entry.getKey() + "=" + entry.getValue());
            }else{
                builder.append("&" + entry.getKey() + "=" + entry.getValue());
            }
        }
        return builder.toString();
    }
    public String toJsonString(){
        return new Gson().toJson(this);
    }
}
