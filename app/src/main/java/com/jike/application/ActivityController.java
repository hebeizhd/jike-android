package com.jike.application;

import com.jike.activity.basic.BaseActivity;

import java.util.Stack;

/**
 * activity控制器
 * Created by Administrator on 2016/4/11.
 */
public class ActivityController {
    private static Stack<BaseActivity> activities = new Stack<>();
    public static void addActivity(BaseActivity activity){
        activities.push(activity);
    }
    public static void removeActivity(BaseActivity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for(;!activities.isEmpty();){
            BaseActivity activity = activities.remove(0);
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
    public static int size() {
        return activities.size();
    }

}
