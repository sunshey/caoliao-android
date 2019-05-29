package com.yc.liaolive.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yc.liaolive.contants.Constant;
import com.zaaach.citypicker.model.HotCity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglin  on 2018/7/9 11:56.
 */
public class HotCityManager {

    private static final String TAG = "HotCityManager";
    private static List<HotCity> mCitys;

    public static List<HotCity> getCitys() {
        if (mCitys != null) {
            return mCitys;
        }
        try {
            mCitys = new Gson().fromJson(SharedPreferencesUtil.getInstance().getString(Constant.HOT_CITY),
                    new TypeToken<List<HotCity>>(){}.getType());
//                    JSON.parseArray(SharedPreferencesUtil.getInstance().getString(Constant.HOT_CITY), HotCity.class);
        } catch (Exception e) {
        }

        return mCitys;
    }

    private static void setCitys(List<HotCity> citys) {

        if (citys != null && citys.size() > 6) {
            citys.remove(0);
        }
        HotCityManager.mCitys = citys;
        try {
//            String json = JSON.toJSONString(citys);
            SharedPreferencesUtil.getInstance().putString(Constant.HOT_CITY,  new Gson().toJson(citys));
        } catch (Exception e) {
        }


    }

    public static void saveCity(final HotCity city) {

        if (mCitys == null) {
            mCitys = new ArrayList<>();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isAdd = true;
                for (HotCity mCity : mCitys) {
                    if (city.getName().equals(mCity.getName())) {
                        isAdd = false;
                        break;
                    }
                }
                if (isAdd) {
                    mCitys.add(city);
                    setCitys(mCitys);
                }
            }
        }).start();


    }
}
