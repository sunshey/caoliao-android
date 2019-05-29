package com.yc.liaolive.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.yc.liaolive.AppEngine;
import com.yc.liaolive.BuildConfig;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.FollowVideoList;
import com.yc.liaolive.bean.SearchResultInfo;
import com.yc.liaolive.bean.VideoDetailsMenu;
import com.yc.liaolive.contants.NetContants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TinyHung@outlook.com
 * 2017/3/17 23:09
 */

public class Utils {

    private static final String TAG = "Utils";

    public static int setDialogWidth(Dialog context) {
        Window window = context.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        WindowManager systemService = (WindowManager) context.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        systemService.getDefaultDisplay().getMetrics(displayMetrics);
        int hight = LinearLayout.LayoutParams.WRAP_CONTENT;
        attributes.height = hight;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int screenWidth = systemService.getDefaultDisplay().getWidth();
        if (screenWidth <= 720) {
            attributes.width = screenWidth - 100;
        } else if (screenWidth > 720 && screenWidth < 1100) {
            attributes.width = screenWidth - 200;
        } else if (screenWidth > 1100 && screenWidth < 1500) {
            attributes.width = screenWidth - 280;
        } else {
            attributes.width = screenWidth - 200;
        }
        attributes.gravity = Gravity.CENTER;
        return attributes.width;
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat();

    public static String formatW(int vaule) {
        if (vaule >= 10000) {
            float l = vaule / 10000.0f;

            return format(l, "#.#'W'");
        }
        return String.valueOf(vaule);
    }

    public static String format(float vaule, String pattern) {
        decimalFormat.applyPattern(pattern);
        return decimalFormat.format(vaule);
    }


    public static void setDialogWidth(Dialog context, int unWidth) {
        Window window = context.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) context.getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics = new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);
        int hight = LinearLayout.LayoutParams.MATCH_PARENT;//取出布局的高度
        attributes.height = hight;
        int screenWidth = systemService.getDefaultDisplay().getWidth();
        attributes.width = screenWidth - unWidth;
        attributes.gravity = Gravity.CENTER;
    }


    /**
     * 切割字符串，去除开头不需要的
     *
     * @param url
     * @param param
     * @return
     */
    public static String cutImageUrl(String url, String param) {
        if (url.startsWith(param)) {
            String substring = url.substring(param.length(), url.length());
            return url.substring(param.length(), url.length());
        }
        return null;
    }

    /**
     * 生成 min 到 max之间的随机数,包含 min max
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandomNum(int min, int max) {
        return min + (int) (Math.random() * max);
    }

    public static void setActivityDialogWidth(Activity context) {
        Window window = context.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics = new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        int hight = LinearLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height = hight;
        int screenWidth = systemService.getDefaultDisplay().getWidth();
        if (screenWidth <= 720) {
            attributes.width = screenWidth - ScreenUtils.dpToPxInt(40f);
        } else if (screenWidth > 720 && screenWidth < 1100) {
            attributes.width = screenWidth - ScreenUtils.dpToPxInt(80f);
        } else if (screenWidth > 1100 && screenWidth < 1500) {
            attributes.width = screenWidth -  ScreenUtils.dpToPxInt(120f);
        } else {
            attributes.width = screenWidth -  ScreenUtils.dpToPxInt(180f);
        }
        attributes.gravity = Gravity.CENTER;
    }

    /**
     * 获取内部版本号
     *
     * @return
     */
    public static int getVersionCode() {//获取版本号(内部识别号)
//        try {
//            PackageInfo pi = VideoApplication.getInstance().getPackageManager().getPackageInfo(VideoApplication.getInstance().getPackageName(), 0);
//            return pi.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return 0;
//        }
        return BuildConfig.VERSION_CODE;
    }

    /**
     * 设备识别码
     *
     * @param context
     * @return
     */
    public static String getDeviceID(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        return mTm.getDeviceId();
    }

    /**
     * 获取当前设备是否有网
     *
     * @return
     */
    public static boolean isCheckNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前设备是否有网
     *
     * @return
     */
    public static boolean isCheckNetwork() {
        ConnectivityManager cm = (ConnectivityManager) VideoApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 获取网络类型
     * -1:错误
     * 1：WIFI
     * 2：3G
     *
     * @return
     */

    public static int getNetworkType() {
        try {
            ConnectivityManager connectivity = (ConnectivityManager)
                    VideoApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                            return NetContants.NETWORK_STATE_WIFI;
                        } else {
                            return NetContants.NETWORK_STATE_3G;
                        }
                    } else {
                        return NetContants.NETWORK_STATE_NO_CONNECTION;
                    }
                } else {
                    return NetContants.NETWORK_STATE_NO_CONNECTION;
                }
            }
        } catch (Exception e) {

            return NetContants.NETWORK_STATE_ERROR;
        }
        return NetContants.NETWORK_STATE_ERROR;
    }

    /**
     * 判断网络是否连接
     * @return
     */
    public static boolean isNetWorkAvaliable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                VideoApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;
            }
        }catch (Exception e) {

        }
        return false;
    }

    /**
     * 使用浏览器打开链接
     */
    public static void openLink(Context context, String content) {
        Uri issuesUrl = Uri.parse(content);
        Intent intent = new Intent(Intent.ACTION_VIEW, issuesUrl);
        context.startActivity(intent);
    }


    /**
     * 将数据转换为万为单位
     *
     * @param no
     * @return
     */

    public static String formatWan(long no) {
        double n = (double) no / 10000;
        return changeDouble(n) + "万";
    }

    public static String formatWan(long no, boolean round) {
        if (round && no <= 10000) return String.valueOf(no);
        double n = (double) no / 10000;
        return changeDouble(n) + "万";
    }

    public static double changeDouble(Double dou) {
        try {
            NumberFormat nf = new DecimalFormat("0.0 ");
            dou = Double.parseDouble(nf.format(dou));
            return dou;
        }catch (RuntimeException e){

        }
        return dou;
    }

    public static double changeDouble(float num) {
        double parseDouble = 0.0;
        try {
            NumberFormat nf = new DecimalFormat("0.00");
            parseDouble = Double.parseDouble(nf.format(num));
            return parseDouble;
        } catch (Exception e) {
            return parseDouble;
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = VideoApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = VideoApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕的高
     *
     * @return
     */
    public static int getScreenHeight() {
        DisplayMetrics dm = AppEngine.getApplication().getResources().getDisplayMetrics();
        if (dm.widthPixels > dm.heightPixels) {
            return dm.widthPixels;
        } else {
            return dm.heightPixels;
        }
    }

    /**
     * 获取屏幕的宽
     *
     * @return
     */
    public static int getScreenWidth() {
        DisplayMetrics dm = AppEngine.getApplication().getResources().getDisplayMetrics();
        if (dm.widthPixels > dm.heightPixels) {
            return dm.heightPixels;
        } else {
            return dm.widthPixels;
        }
    }


    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        String imei = telephonyManager.getDeviceId();
        return imei;
    }


    /**
     * 保留两位小数
     *
     * @param percent
     * @return
     */
    public static float float2(float percent) {
        BigDecimal b = new BigDecimal(percent);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 获取应用的包名
     *
     * @param context
     * @return
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    /**
     * 通过Url切割文件名
     *
     * @param url
     * @return
     */
    public static String getFileName(String url) {
        if(TextUtils.isEmpty(url)){
            return "";
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 通过Url切割后缀名
     *
     * @param url
     * @return
     */
    public static String getFilePostName(String url) {
        return url.substring(url.lastIndexOf(".") + 1);
    }

    /**
     * 获取.APK文件的包信息
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static int getAPKPathVerstion(Context context, File apkPath) {
        int versionCode = 0;
        if (apkPath.exists() && ZipUtil.isArchiveFile(apkPath)) {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                versionCode = packageInfo.versionCode;
            } else {
                versionCode = 0;
            }

            return versionCode;
        } else {
            return versionCode;
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath
     * @return
     */
    public static boolean deleteFiledeleteFile(File sPath) {

        boolean flag = false;
        // 路径为文件且不为空则进行删除
        if (null != sPath && sPath.exists() && sPath.isFile()) {
            sPath.delete();
            flag = true;
        }
        return flag;
    }

    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null && files.length > 0) {
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    /**
     * 本机SD卡是否可用
     *
     * @return
     */
    public static boolean hasSdCard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        }
        return false;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
//        try {
//            PackageManager manager = VideoApplication.getInstance().getApplicationContext().getPackageManager();
//            PackageInfo info = manager.getPackageInfo(VideoApplication.getInstance().getApplicationContext().getPackageName(), 0);
//            String version = info.versionName;
//            return version;
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }

        return BuildConfig.VERSION_NAME;
    }

    /**
     * 判断是否是常用11位数手机号
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }

    /**
     * 是否是6位数字验证码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isNumberCode(String phoneNumber) {
        Pattern p = Pattern.compile("^\\d{4}$");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }

    /**
     * 提取短信中的验证码4位
     *
     * @param smsBody
     * @return
     */
    public static String getAuthCodeFromSms(String smsBody) {
        Pattern pattern = Pattern.compile("\\d{4}");
        Matcher matcher = pattern.matcher(smsBody);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 6-16位密码正则判断
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPassword(String phoneNumber) {
        Pattern p = Pattern.compile("^([0-9]|[a-zA-Z]){6,16}$");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }


    /**
     * 验证15位或18位身份证
     *
     * @param number
     * @return
     */
    public static boolean isIdentityNum(String number) {
        String partern = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}[0-9Xx]$)";
        Pattern p = Pattern.compile(partern);
        Matcher matcher = p.matcher(number);
        return matcher.matches();
    }


    /**
     * 将String写入本地
     *
     * @param response
     * @param filePath
     */
    public static void writeString(String response, File filePath) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            byte[] bytes = response.getBytes();
            try {
                fileOutputStream.write(bytes);
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测输入文本框内容中的话题是否超过限制个数,正则表达式"#[^#]+#"
     *
     * @return
     */
    public static int exceedTopicCount(String content) {

        List<String> hashtags = getHashtags(content);

        if (null != hashtags && hashtags.size() > 0) {
            for (String hashtag : hashtags) {
                Log.d("Utils", "exceedTopicCount: hashtag=" + hashtag);
            }
            return hashtags.size();
        }
        return 0;
    }


    private static final Pattern hashtagPattern =
            Pattern.compile("#[^#]+#");

    private static String removeHashtags(String text) {
        Matcher matcher;
        String newTweet = text.trim();
        String cleanedText = "";
        while (!newTweet.equals(cleanedText)) {
            cleanedText = newTweet;
            matcher = hashtagPattern.matcher(cleanedText);
            newTweet = matcher.replaceAll("");
            newTweet = newTweet.trim();
        }
        return cleanedText;
    }


    /**
     *  // 定义正则表达式
     private static final String AT = "@[\u4e00-\u9fa5\\w]+";// @人
     private static final String TOPIC = "#[\u4e00-\u9fa5\\w]+#";// ##话题
     private static final String EMOJI = "\\[[\u4e00-\u9fa5\\w]+\\]";// 表情
     private static final String URL = "http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";// url
     */
    /**
     * 将文本内容中的带有话题关键字内容提取出来封装进集合
     *
     * @param content
     * @return
     */
    public static Map<String, Map<Integer, Integer>> hashtags(String content) {

        //话题关键字Key 开始角标 结束角标
        Map<String, Map<Integer, Integer>> topicMaps = new HashMap<>();
        List<String> hashtagSet = new ArrayList<String>();
        Matcher matcher = hashtagPattern.matcher(content);
        while (matcher.find()) {
            int matchStart = matcher.start();
            int matchEnd = matcher.end();
            String tmpHashtag = content.substring(matchStart, matchEnd);

            hashtagSet.add(tmpHashtag);


            Map<Integer, Integer> map = new HashMap<>();//记录话题的开始位置和结束位置
            map.put(matchStart, matchEnd);
            topicMaps.put(tmpHashtag, map);

            content = content.replace(tmpHashtag, "");

            matcher = hashtagPattern.matcher(content);
        }
        if (null != hashtagSet && hashtagSet.size() > 0) {

        }
        return topicMaps;
    }


    public static List<String> getHashtags(String content) {
        List<String> hashtagSet = new ArrayList<String>();
        Matcher matcher = hashtagPattern.matcher(content);
        while (matcher.find()) {
            int matchStart = matcher.start();
            int matchEnd = matcher.end();
            String tmpHashtag = content.substring(matchStart, matchEnd);
            hashtagSet.add(tmpHashtag);
            content = content.replace(tmpHashtag, "");
            matcher = hashtagPattern.matcher(content);
        }
        return hashtagSet;
    }

    /**
     * 判断输入框内字符串是否包含新的话题字符
     *
     * @param content
     * @param topic
     * @return
     */
    public static boolean topicCountEquals(String content, String topic) {
        List<String> hashtags = getHashtags(content);
        if (null != hashtags && hashtags.size() > 0) {
            for (String hashtag : hashtags) {
                if (TextUtils.equals(hashtag, topic)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将字符串中有话题的字段加上标签所需的颜色
     *
     * @param content
     */
    public static String changeTopic(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        List<String> hashtags = getHashtags(content);
        String newDesp = content;
        if (null != hashtags && hashtags.size() > 0) {
            String topic1 = hashtags.get(0);
            String newContent = content.replace(topic1, "<font color='#FD7004'>" + topic1 + "</font>");
            newDesp = newContent;
            if (hashtags.size() > 1) {
                String topic2 = hashtags.get(1);
                String newConten2 = newContent.replace(topic2, "<font color='#FD7004'>" + topic2 + "</font>");
                newDesp = newConten2;
            }
        }
        return newDesp;
    }

    /**
     * 提取话题中的正文本
     *
     * @param stringExtra
     * @return
     */
    public static String slipTopic(String stringExtra) {
        if (TextUtils.isEmpty(stringExtra)) {
            return null;
        }
        if (stringExtra.startsWith("#")) {
            return stringExtra.substring(1, stringExtra.length() - 1);
        }
        return stringExtra;
    }


    /**
     * 切割字符串
     *
     * @param content 要切割的内容
     * @param count   最大保留长度
     * @return
     */
    public static String subString(String content, int count) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        if (content.length() <= count) {
            return content;
        }
        return content.substring(0, count);
    }


    public static int getNotificationID() {
        long num = System.currentTimeMillis();
        String s = String.valueOf(num);
        if (s.length() >= 8) {
            String substring = s.substring(7, s.length() - 1);
            return Integer.parseInt(substring);
        }
        return 0;
    }

    /**
     * 比较两个集合中是否有不一样的新的数据
     *
     * @param oldList 本地缓存的
     * @param newList 新的，这里的新数据是不会为空或者长度不会为0的
     *                返回结果是否相等
     */
    public static int compareToDataHasNewData(List<FollowVideoList.DataBean.ListsBean> oldList, List<FollowVideoList.DataBean.ListsBean> newList) {

        if (null == oldList || oldList.size() <= 0) {
            return newList == null ? 0 : newList.size();
        }
        if (null == newList || newList.size() <= 0) {
            return 0;
        }
        try {
            for (int i = 0; i < oldList.size(); i++) {
                for (int i1 = 0; i1 < newList.size(); i1++) {
                    if (TextUtils.equals(oldList.get(i).getVideo_id(), newList.get(i1).getVideo_id())) {
                        newList.remove(i1);
                    }
                }
            }
        } catch (Exception e) {

        }
        return newList.size();
    }


    public boolean equalList(List list1, List list2) {
        if (list1.size() != list2.size())
            return false;
        for (Object object : list1) {
            if (!list2.contains(object))
                return false;
        }
        return true;

    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(android.app.Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertActivityToTranslucentAfterL(activity);
        } else {
            convertActivityToTranslucentBeforeL(activity);
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms before Android 5.0
     */
    public static void convertActivityToTranslucentBeforeL(Activity activity) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(activity, new Object[]{
                    null
            });
        } catch (Throwable t) {
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms after Android 5.0
     */
    private static void convertActivityToTranslucentAfterL(Activity activity) {
        try {
            Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
            getActivityOptions.setAccessible(true);
            Object options = getActivityOptions.invoke(activity);

            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz, ActivityOptions.class);
            convertToTranslucent.setAccessible(true);
            convertToTranslucent.invoke(activity, null, options);
        } catch (Throwable t) {
        }
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String mdStr = bigInt.toString(16);
        int slen = 32 - mdStr.length();
        for (int i = 0; i < slen; i++) {
            mdStr = 0 + mdStr;
        }
        return mdStr;
    }


    public static String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;
        FileInputStream in = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    //改变bitmap尺寸的方法
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 返回app运行状态
     * 1:程序在前台运行
     * 2:程序在后台运行
     * 3:程序未启动
     * 注意：需要配置权限<uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static int getAppSatus(Context context, String pageName) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(20);
        //判断程序是否在栈顶
        if (list.get(0).topActivity.getPackageName().equals(pageName)) {
            return 1;
        } else {
            //判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(pageName)) {
                    return 2;
                }
            }
            return 3;//栈里找不到，返回3
        }
    }

    /**
     * 检查两个数组是否都为空或者长度都为0
     *
     * @param mvideo_list
     * @param user_list
     * @return
     */
    public static boolean changeListVolume(List<SearchResultInfo.DataBean.VideoListBean> mvideo_list, List<SearchResultInfo.DataBean.UserListBean> user_list) {

        if (null != mvideo_list && mvideo_list.size() > 0) {
            return true;
        }

        if (null != user_list && user_list.size() > 0) {
            return true;
        }
        return false;
    }

    public static boolean checkPermission(Context context, String permName, String pkgName) {
        PackageManager pm = context.getPackageManager();
        if (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permName, pkgName)) {
            System.out.println(pkgName + "has permission : " + permName);
            return true;
        } else {
            //PackageManager.PERMISSION_DENIED == pm.checkPermission(permName, pkgName)
            System.out.println(pkgName + "not has permission : " + permName);
            return false;
        }
    }

    /**
     * 只截取保留字符串最后5位数
     *
     * @param content
     * @return
     */
    public static int substring(String content) {
        if (TextUtils.isEmpty(content)) return 0;
        if (content.length() < 5) {
            return Integer.parseInt(content.substring(0, content.length()));
        }
        return Integer.parseInt(content.substring(content.length() - 5, content.length()));
    }

    /**
     * 安装apk
     */
    public static void installApk(File filePath) {
        if (null != filePath && filePath.exists() && filePath.isFile() && 0 != Utils.getAPKPathVerstion(VideoApplication.getInstance(), filePath)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(Uri.parse("file://" + filePath.toString()),
                    "application/vnd.android.package-archive");
            VideoApplication.getInstance().getApplicationContext().startActivity(intent);
        }
    }

    /**
     * 将数字转变为以万为单位
     *
     * @param number
     * @return
     */
    public static String changeNumberFormString(String number) {
        if (TextUtils.isEmpty(number)) return "";
        int intNumber = Integer.parseInt(number);
        if (intNumber < 10000) {
            return intNumber + "";
        } else if (intNumber == 10000) {
            return "1.0万";
        } else {
            return save2number(intNumber / 10000) + "万";
        }
    }

    /**
     * 四舍五入保留两位小数点
     *
     * @param number
     * @return
     */
    public static double save2number(double number) {
        // 方式一：
//        double f = 3.1516;
//        BigDecimal b = new BigDecimal(f);
//        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        // 方法二 #.00 表示两位小数 #.0000四位小数 以此类推…
        return Double.parseDouble(new java.text.DecimalFormat("#.00").format(number));
    }


    /**
     * 去除最后一个“/”
     *
     * @param filterFolderPath
     * @return
     */
    public static String subFolderEnd(String filterFolderPath) {
        if (TextUtils.isEmpty(filterFolderPath)) return filterFolderPath;
        if (filterFolderPath.endsWith("/")) {
            return filterFolderPath.substring(0, filterFolderPath.length() - 1);
        }
        return filterFolderPath;
    }

    /**
     * 用于格式化duration为HH:MM:SS格式
     *
     * @param duration
     * @return
     */
    public static String formatDurationForHMS(long duration) {
        long second = duration / 1000;
        long minute = second / 60;
        long hour = minute / 60;
        second = second % 60;
        minute = minute % 60;
        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
    }

    public static boolean isFileToMp3(String absolutePath) {
        if (!TextUtils.isEmpty(absolutePath)) {
            return absolutePath.endsWith(".mp3") || absolutePath.endsWith(".MP3");
        }
        return false;
    }

    public static boolean isFileToMp4(String absolutePath) {
        if (!TextUtils.isEmpty(absolutePath)) {
            return absolutePath.endsWith(".mp4") || absolutePath.endsWith(".MP4");
        }
        return false;
    }

    /**
     * 对数值的取反操作
     *
     * @param alpha 取反前数值
     * @param minVar 最小取值
     * @param maxVar 最大取值
     * 返回值为最终取值数值
     */
    public static float[] vars = null;

    public static float absValue(float alpha, int minVar, int maxVar) {
        if (null == vars) {
            vars = new float[maxVar];
            for (int i = minVar; i < maxVar; i++) {
                vars[i] = i;
            }
        }
        if (null != vars && vars.length > 0) {
            return vars.length - alpha;
        }
        return 0;
    }

    /**
     * 将没有后缀的地址
     *
     * @param fileName
     * @return
     */
    public static String rexVideoPath(String fileName) {
        if (null != fileName && fileName.length() > 0) {
            if (fileName.endsWith(".mp4") || fileName.endsWith(".MP4")) {
                return fileName;
            }
            return fileName + ".mp4";
        }
        return fileName;
    }

    public static String getReservedSession() {
        Random rand = new Random();//生成随机数
        String cardNnumer = "";
        for (int a = 0; a < 6; a++) {
            cardNnumer += rand.nextInt(10);//生成6位数字
        }
        return cardNnumer;
    }

    public static float absVakue(float maxVar, float var) {
        try {
            return maxVar - var;
        }catch (RuntimeException e){
            return 0f;
        }
    }

    public static String getBindPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) return "";
        if (phone.length() >= 11) {
            String substring = phone.substring(3, phone.length() - 4);
            return phone.replace(substring, "****");
        }
        return "";
    }

    public static String getSubstringContent(String content, int start, int end) {
        if (null != content && content.length() > 0) {
            if (content.length() < start || content.length() < end) {
                return content;
            }
            return content.substring(start, end);
        }
        return content;
    }

    /**
     * 初始化单聊界面菜单
     *
     * @param isFollow 是否关注
     * @param mIsBlack 此用户是否在黑名单中
     * @return
     */
    public static List<VideoDetailsMenu> createChatC2CMenu(int isFollow, int mIsBlack) {
        List<VideoDetailsMenu> list = new ArrayList<>();

        VideoDetailsMenu videoDetailsMenu = new VideoDetailsMenu();
        videoDetailsMenu.setItemID(1);
        videoDetailsMenu.setTextColor("#FF000000");
        videoDetailsMenu.setItemName("亲密度");
        list.add(videoDetailsMenu);

        VideoDetailsMenu videoDetailsMenu2 = new VideoDetailsMenu();
        videoDetailsMenu2.setItemID(2);
        videoDetailsMenu2.setTextColor("#FF000000");
        videoDetailsMenu2.setItemName("查看资料");
        list.add(videoDetailsMenu2);

        if (0 == isFollow) {
            VideoDetailsMenu videoDetailsMenu3 = new VideoDetailsMenu();
            videoDetailsMenu3.setItemID(3);
            videoDetailsMenu3.setTextColor("#FF000000");
            videoDetailsMenu3.setItemName("添加关注");
            list.add(videoDetailsMenu3);
        }

        if (0 == mIsBlack) {
            VideoDetailsMenu videoDetailsMenu4 = new VideoDetailsMenu();
            videoDetailsMenu4.setItemID(4);
            videoDetailsMenu4.setTextColor("#FF000000");
            videoDetailsMenu4.setItemName("添加至黑名单");
            list.add(videoDetailsMenu4);
        } else {
            VideoDetailsMenu videoDetailsMenu4 = new VideoDetailsMenu();
            videoDetailsMenu4.setItemID(7);
            videoDetailsMenu4.setTextColor("#FF000000");
            videoDetailsMenu4.setItemName("从黑名单中移除");
            list.add(videoDetailsMenu4);
        }


        VideoDetailsMenu videoDetailsMenu5 = new VideoDetailsMenu();
        videoDetailsMenu5.setItemID(5);
        videoDetailsMenu5.setTextColor("#FF000000");
        videoDetailsMenu5.setItemName("清空聊天记录");
        list.add(videoDetailsMenu5);

        VideoDetailsMenu videoDetailsMenu6 = new VideoDetailsMenu();
        videoDetailsMenu6.setItemID(6);
        videoDetailsMenu6.setTextColor("#FF000000");
        videoDetailsMenu6.setItemName("举报");
        list.add(videoDetailsMenu6);

        return list;
    }

    public static List<BannerInfo> slipListBackThree(List<BannerInfo> bannerInfos) {
        if (null == bannerInfos) return bannerInfos;
        if (bannerInfos.size() <= 3) return bannerInfos;
        List<BannerInfo> bannerInfos1 = new ArrayList<>();
        for (int i = 0; i < bannerInfos.size(); i++) {
            bannerInfos1.add(bannerInfos.get(i));
            if (bannerInfos1.size() >= 3) {
                break;
            }
        }
        return bannerInfos1;
    }

    /**
     * 获取String的MD5值
     * @param info 字符串
     * @return 该字符串的MD5值 返回值为十六进制的32位长度的字符串
     */
    public static String getMD5(String info) {
        try {
            //获取 MessageDigest 对象，参数为 MD5 字符串，表示这是一个 MD5 算法（其他还有 SHA1 算法等）：
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //update(byte[])方法，输入原数据
            //类似StringBuilder对象的append()方法，追加模式，属于一个累计更改的过程
            md5.update(info.getBytes("UTF-8"));
            //digest()被调用后,MessageDigest对象就被重置，即不能连续再次调用该方法计算原数据的MD5值。可以手动调用reset()方法重置输入源。
            //digest()返回值16位长度的哈希值，由byte[]承接
            byte[] md5Array = md5.digest();
            //byte[]通常我们会转化为十六进制的32位长度的字符串来使用,本文会介绍三种常用的转换方法
            return new BigInteger(1, md5Array).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }


    public static String getSize(int size) {
        //获取到的size为：1705230
        int GB = 1024 * 1024 * 1024;//定义GB的计算常量
        int MB = 1024 * 1024;//定义MB的计算常量
        int KB = 1024;//定义KB的计算常量
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String resultSize;
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = df.format(size / (float) GB) + "GB";
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = df.format(size / (float) MB) + "MB";
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = df.format(size / (float) KB) + "KB";
        } else {
            resultSize = size + "B";
        }
        return resultSize;
    }

    public static Bitmap getBitmapForDeawable(Drawable drawable) {
        try {
            if(null!=drawable){
                BitmapDrawable bitmapDrawable= (BitmapDrawable) drawable;
                return bitmapDrawable.getBitmap();
            }
        }catch (RuntimeException e){

        }
        return null;
    }

    /**
     * 截取手机号显示
     * @param phone
     * @param start
     * @param end
     * @return
     */
    public static String submitPhone(String phone, int start, int end) {
        if(!TextUtils.isEmpty(phone)&&phone.length()>end){
            try {
                StringBuilder newPhone=new StringBuilder();
                newPhone.append(phone.substring(0,start));
                newPhone.append("****");
                newPhone.append(phone.substring(end,phone.length()));
                return newPhone.toString();
            }catch (RuntimeException e){
                return phone;
            }
        }
        return phone;
    }

    /**
     * 获取CPU型号
     *
     * @return
     */
    public static String getCpuName() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr);
            while ((str2 = localBufferedReader.readLine()) != null) {
                if (str2.contains("Hardware")) {
                    return str2.split(":")[1];
                }
            }
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return null;
    }

    public static void copyString(Context context,String identify) {
        try {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(identify);
        }catch (RuntimeException e){

        }
    }

    /**
     * 是否定位
     * @param position
     * @param lastItemPosition
     * @param firstItemPosition
     * @return
     */
    public static boolean isRefresh(int position,int lastItemPosition, int firstItemPosition) {
        if(position==lastItemPosition||position==firstItemPosition){
            return false;
        }
        if(position<firstItemPosition||position>lastItemPosition){
            return true;
        }
        return false;
    }

    /**
     * 判断是否有SDcard
     *
     * @return
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 屏幕是否上锁
     * @return true:  1、屏幕是黑的 2、目前正处于解锁状态  false:未锁屏
     */
    public static boolean isScreenLock(Context context){
        try {
            KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return mKeyguardManager.inKeyguardRestrictedInputMode();
        }catch (RuntimeException e){
            return true;
        }
    }

    public static String formatHostUrl(String url) {
        if(TextUtils.isEmpty(url)) return "";
        if(url.contains("?")){
            String substring = url.substring(0, url.lastIndexOf("?"));
            return substring;
        }
        return url;
    }

    /**
     * 返回此毫秒值是否过期
     * @param millis true：已过期 false：未过期
     * @return
     */
    public static boolean isExprie(long millis) {
        long currentMillis = System.currentTimeMillis();
        return millis<currentMillis;
    }

    /**
     * 获取URL ActivityName
     * @param jumpUrl
     * @return
     */
    public static String getClassName(String jumpUrl){
        if(jumpUrl.contains("?")){
            String substring = jumpUrl.substring(0,jumpUrl.lastIndexOf("?"));
            return substring;
        }
        return jumpUrl;
    }

    /**
     * 获取URL参数
     * @param jumpUrl
     * @return
     */
    public static Map<String, String> getParamsExtra(String jumpUrl) {
        if(TextUtils.isEmpty(jumpUrl)) return null;
        String content=jumpUrl;
        if(jumpUrl.contains("?")){
            content = jumpUrl.substring(jumpUrl.lastIndexOf("?")-1,jumpUrl.length());
        }
        Map<String,String> params=new HashMap<>();
        String[] split = content.split("[?]");
        if (split.length == 2 && !"".equals(split[1].trim())) {
           String[] parameters = split[1].split("&");
             if (parameters != null && parameters.length != 0) {
               for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i] != null && parameters[i].trim().contains("=")) {
                        String[] split2 = parameters[i].split("=");
                        if(split2.length==1){
                        params.put(split2[0], "");
                        }else if(split2.length==2){
                            if(!"".equals(split2[0].trim())){
                                params.put(split2[0], split2[1]);
                            }
                        }
                    }
                }
            }
        }
        return params;
    }
}