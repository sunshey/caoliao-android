package com.yc.liaolive.manager;

import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/26 15:50
 * 统一将多个Activity添加至栈中，达到条件用于统一销毁
 */
public class ActivityCollectorManager {


    private static List<AppCompatActivity> mCompatActivityList=new ArrayList<>();

    /**
     * 添加一个Activity
     * @param appCompatActivity
     */
    public static void addActivity(AppCompatActivity appCompatActivity){
        mCompatActivityList.add(appCompatActivity);
    }

    /**
     * 销毁一个Activity
     * @param appCompatActivity
     */
    public static void removeActivity(AppCompatActivity appCompatActivity){
        try {
            mCompatActivityList.remove(appCompatActivity);
        }catch (RuntimeException e){
        }catch (Exception e){
        }
    }

    /**
     * 销毁所有Activity
     */
    public static void finlishAllActivity(){
        if(null!=mCompatActivityList){
            for (AppCompatActivity appCompatActivity : mCompatActivityList) {
                appCompatActivity.finish();
            }
            mCompatActivityList.clear();
        }
    }
}
