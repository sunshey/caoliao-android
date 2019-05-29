package com.yc.liaolive.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.yc.liaolive.VideoApplication;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import static com.umeng.socialize.utils.ContextUtil.getPackageName;
import static com.umeng.socialize.utils.DeviceConfig.context;


/**
 * TinyHung@Outlook.com
 * 2017/8/28.
 */

public class SystemUtils {

    public static final String TAG = "SystemUtils";

    /**
     * 获取配置的渠道号
     * @param channerKey
     * @return
     */
    public static String getChannerByKey(String channerKey){
        try {
            PackageManager pm = VideoApplication.getInstance().getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(VideoApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(channerKey);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return null;
    }

    @SuppressWarnings("static-access")
    public static void startVibrator(int millisecond) {
        Vibrator vibrator = (Vibrator) VideoApplication.getInstance().getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(millisecond);
    }


    /**
     * 设置手机的移动数据
     */
    public static void setMobileData(Context pContext, boolean pBoolean) {
        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;

            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);

            method.invoke(mConnectivityManager, pBoolean);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("移动数据设置错误: " + e.toString());
        }
    }

    /**
     * 返回手机移动数据的状态
     *
     * @param pContext
     * @param arg
     *            默认填null
     * @return true 连接 false 未连接
     */
    public static boolean getMobileDataState(Context pContext, Object[] arg) {

        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }

            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);

            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

            return isOpen;

        } catch (Exception e) {
            // TODO: handle exception

            System.out.println("得到移动数据状态出错");
            return false;
        }

    }


    public static int getNumCores() {

        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Print exception
            e.printStackTrace();
            //Default to return 1 core
            return 1;
        }
    }

    public static String getTopActivity() {
        ActivityManager manager = (ActivityManager) VideoApplication.getInstance().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo cinfo = runningTasks.get(0);
        ComponentName component = cinfo.topActivity;
        return component.getClassName();
    }

    public static void openWLAN() {
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) VideoApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {//wifiManager.setWifiEnabled(true);打开WIFI
                wifiManager.setWifiEnabled(true);
            }
        } catch (Exception e) {

        }
    }

    public static String getLocastHostIP() {
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) VideoApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (wifiManager.isWifiEnabled()) {//wifiManager.setWifiEnabled(true);打开WIFI
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                return intToIp(ipAddress);
            } else {
                if (Utils.isCheckNetwork()) {
                    return getLocalIpAddress();
                } else {
//                    wifiManager.setWifiEnabled(true);
//                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                    int ipAddress = wifiInfo.getIpAddress();
//                    return intToIp(ipAddress);
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取本机IP地址，如果没有网络，强行打开WIFI
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return "0";
    }


    private static String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    /**
     * 获取地理位置
     * @return
     */
    public static String[] getLocation() {
        //获取地理位置管理器
        LocationManager locationManager = (LocationManager) VideoApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (null != providers && providers.size() > 0) {
            String locationProvider = null;
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                //如果是GPS
                locationProvider = LocationManager.GPS_PROVIDER;
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                //如果是Network
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {

            }
            if (null != locationProvider) {
                //获取Location
                if (ActivityCompat.checkSelfPermission(VideoApplication.getInstance().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(VideoApplication.getInstance().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                Location location = locationManager.getLastKnownLocation(locationProvider);
                if(location!=null){
                    //不为空
                    String[] strings=new String[2];
                    strings[0]=location.getLongitude()+"";
                    strings[1]=location.getLatitude()+"";
                    return strings;
                }
            }
        }
        return null;
    }


    private static SystemUtils mInstance;

    public synchronized static SystemUtils getInstance() {
        synchronized (SystemUtils.class){
            if(null==mInstance){
                mInstance=new SystemUtils();
            }
        }
        return mInstance;
    }

    public void startAppDetailsInfoActivity(Activity context, int requestCode) {
        String SCHEME = "package";
        /**
         * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
         */
        String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
        /**
         * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
         */
        String APP_PKG_NAME_22 = "pkg";
        /**
         * InstalledAppDetails所在包名
         */
        String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
        /**
         * InstalledAppDetails类名
         */
        String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
        try {
            Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
            if(appIntent != null){
                context.startActivityForResult(appIntent,requestCode);
                return;
            }
            // oppo 点击设置图标>应用权限管理>按应用程序管理>我的app>我信任该应用
            //      点击权限隐私>自启动管理>我的app
            appIntent =context.getPackageManager().getLaunchIntentForPackage("com.oppo.safe");
            if(appIntent != null){
                context.startActivityForResult(appIntent,requestCode);
                return;
            }

            Intent intent = new Intent();
            final int apiLevel = Build.VERSION.SDK_INT;
            if (apiLevel >= 9) {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(SCHEME, getPackageName(), null);
                intent.setData(uri);
            } else {
                final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
                intent.putExtra(appPkgName, getPackageName());
            }
            context.startActivityForResult(intent,requestCode);
        }catch (Exception e){
            ToastUtils.showCenterToast("打开权限设置界面错误！请到应用管理界面手动开启权限！");
        }
    }

    public static boolean isClsRunning(String pkg, String cls, Context context) {
        ActivityManager am =(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        ActivityManager.RunningTaskInfo task = tasks.get(0);
        if (task != null) {
            return TextUtils.equals(task.topActivity.getPackageName(), pkg) && TextUtils.equals(task.topActivity.getClassName(), cls);
        }
        return false;
    }
    public static  int getStatusBarHeight(Context context){
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if(null==runningApps) return null;
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
