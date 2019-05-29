package com.yc.liaolive.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yc.liaolive.R;
import com.yc.liaolive.index.ui.MainActivity;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@outlook.com
 * 2017/6/20
 * 定位管理
 */

public class LocationHelper {

    private static String TAG = "LocationHelper";
    private int iniSDK = 0;
    private static LocationHelper mInstance;
    private LocationClient mLocationClient;

    public static LocationHelper getInstance() {
        synchronized (LocationHelper.class) {
            if (null == mInstance) {
                mInstance = new LocationHelper();
            }
        }
        return mInstance;
    }

    /**
     * 开始单场次定位
     *
     * @param context
     * @param sdk_ini Android 版本
     */
    public void start(Context context, int sdk_ini) {
        this.iniSDK = sdk_ini;
        //获取定位
        mLocationClient = new LocationClient(context);
        //声明LocationClient类
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                double latitude = bdLocation.getLatitude();    //获取纬度信息
                double longitude = bdLocation.getLongitude();    //获取经度信息
                float radius = bdLocation.getRadius();    //获取定位精度，默认值为0.0f
//                String coorType = bdLocation.getCoorType();
                //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
//                int errorCode = bdLocation.getLocType();
                //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
                UserManager.getInstance().setLocationLongitude(longitude);
                UserManager.getInstance().setLocationLatitude(latitude);
                if (!TextUtils.isEmpty(bdLocation.getCity())) {
                    UserManager.getInstance().setPosition(bdLocation.getCity().replace("市", ""));
                }
                UserManager.getInstance().setProvince(bdLocation.getProvince());

                UserManager.getInstance().uploadLocation(longitude, latitude, radius, bdLocation.getCity(), new UserServerContract.OnNetCallBackListener() {
                    @Override
                    public void onSuccess(Object object) {
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                    }
                });
                stop();
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        option.setCoorType("gcj02");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(0);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(false);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        //Android 8.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLocationClient.start();
            //开启前台定位服务：
            Notification.Builder builder = new Notification.Builder(context);
            //获取一个Notification构造器
            Intent nfIntent = new Intent(context, MainActivity.class);
            builder.setContentIntent(PendingIntent.getActivity(context, 0, nfIntent, 0)) // 设置PendingIntent
                    .setContentTitle("正在进行后台定位") // 设置下拉列表里的标题
                    .setSmallIcon(R.drawable.ic_launcher) // 设置状态栏内的小图标
                    .setContentText("后台定位通知") // 设置上下文内容
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
            Notification notification = null;
            notification = builder.build();
            notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
            mLocationClient.enableLocInForeground(1001, notification);// 调起前台定位
            //Android 7.0
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            mLocationClient.restart();
            //Android 6.0 及以下
        } else {
            mLocationClient.start();
        }
    }

    /**
     * 结束
     */
    public void stop() {
        if (null != mLocationClient) {
            if (iniSDK >= Build.VERSION_CODES.O) {
                //停止前台定位服务：
                mLocationClient.disableLocInForeground(true);// 关闭前台定位，同时移除通知栏
            } else {
                mLocationClient.stop();
            }
            mLocationClient = null;
        }
    }
}
