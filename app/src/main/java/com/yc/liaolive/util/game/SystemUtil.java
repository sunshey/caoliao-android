package com.yc.liaolive.util.game;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

public class SystemUtil
{

    public static enum NetState{NET_NO,NET_2G,NET_3G,NET_4G,NET_WIFI,NET_UNKNOWN,NET_MOBILE};
    /** 
     * 检查系统中是否安装了某个应用 
     *  
     * @param context
     *            你懂的
     * @param packageName
     *            应用的包名 
     * @return true 表示已安装，否则返回false
     */
    public static boolean isInstalled(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> installedList = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        Iterator<PackageInfo> iterator = installedList.iterator();
  
        PackageInfo info;  
        String name;  
        while(iterator.hasNext())  
        {  
            info = iterator.next();  
            name = info.packageName;  
            if(name.equals(packageName))  
            {  
                return true;  
            }  
        }  
        return false;  
    }

    public static boolean isApkInstalled(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static boolean isInstalled(String packageName) {
        String packages = "package:" + packageName;
        long ss = System.currentTimeMillis();
        Process process = null;
        BufferedReader bis = null;
        try {
            process = Runtime.getRuntime().exec("pm list packages -3");//adb shell pm list package -3
            bis = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = bis.readLine()) != null) {
                if(line != null && line.equals(packages)){
                    bis = null;
                    process.destroy();
                    return true;
                }
            }
        } catch (IOException e) {
            Log.i("IOException", "isInstalled: "+e);
        }finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }
    public static boolean hasSD(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     *
     * @param string
     * @return
     */

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
//                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
//            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }


    public static NetState getNetType(Context context){
        NetState stateCode = NetState.NET_NO;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    stateCode = NetState.NET_WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager. NETWORK_TYPE_IDEN:
                            stateCode = NetState.NET_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            stateCode = NetState.NET_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            stateCode = NetState.NET_4G;
                            break;
                        default:
                            stateCode = NetState.NET_UNKNOWN;
                    }
                    break;
                default:
                    stateCode = NetState.NET_UNKNOWN;
            }
        }
        return stateCode;
    }

    public static NetState getNetWorkType(Context context){
        NetState stateCode = NetState.NET_NO;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnectedOrConnecting()) {
                    switch (ni.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            stateCode = NetState.NET_WIFI;
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            stateCode = NetState.NET_MOBILE;
                            break;
                        default:
                            stateCode = NetState.NET_UNKNOWN;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return stateCode;
    }
}  