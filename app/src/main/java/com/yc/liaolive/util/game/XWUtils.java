package com.yc.liaolive.util.game;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.yc.liaolive.util.game.view.GameWebActivity;
public class XWUtils {

    private static final String TAG = "XWUtils";
    private static final String KEY_SHARED_PREFERENCES_NAME = "xian_wan_common_config";

    private static final String KEY_APP_ID = "appId";
    private static final String KEY_APP_SECRET = "app_secret";
    private static final String KEY_APP_SIGN = "app_sign";
    private static final String KEY_APP_AD_ID = "app_adId";
    private static final String KEY_ACTIONBAR_TITLE = "actionbar_title";
    private static final String KEY_ACTIONBAR_BG_COLOR = "actionbar_bg_clolor";
    private static final String KEY_ACTIONBAR_TITLE_COLOR = "actionbar_title_clolor";

    private static final String KEY_APP_H5_URL = "app_h5_url";
    private static final String default_list_url = "https://h5.51xianwan.com/try/try_list_plus.aspx?";
    private static final String default_detail_url = "https://h5.51xianwan.com/try/try_cpl_plus.aspx?";

    private String titleBGColorString = "#FF5200";          //actionBar背景 颜色
    private String titleTextColorString = "#FFFFFF";        //actionbar 标题颜色
    private int tileBGColor = Color.parseColor(titleBGColorString);
    private int tileTextColor = Color.parseColor(titleTextColorString);

    private String appId;               //渠道appid
    private String appSecret;           //用户密钥
    private String appSign;             //用户userId
    private String baseUrl;
    private String adId;                //广告Id

    private String title ;

    private int mode = 0;               // 展示形式 0: AD列表 1:AD详情

    private static volatile XWUtils sInst = null;

    private Context mContext = null;

    private XWUtils(Context context) {
        this.mContext = context;
    }

    public static XWUtils getInstance(Context context) {
        XWUtils inst = sInst;
        if (inst == null) {
            synchronized (XWUtils.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new XWUtils(context.getApplicationContext());
                    sInst = inst;
                }
            }
        }
        return inst;
    }

    public void init(String appid, String secret, String sign) {
        if (TextUtils.isEmpty(appid) || TextUtils.isEmpty(secret) || TextUtils.isEmpty(sign)) {
            Toast.makeText(mContext, "请检查参数", Toast.LENGTH_SHORT).show();
        } else {
            appId = appid;
            appSecret = secret;
            appSign = sign;
            saveValue(KEY_APP_ID, appid);
            saveValue(KEY_APP_SECRET, secret);
            saveValue(KEY_APP_SIGN, sign);
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mod) {
        mode = mod;
    }

    public String getAdId() {
        if (adId == null) {
            adId = getValue(KEY_APP_AD_ID, null);
        }
        return adId;
    }



    public void setAdId(String adId) {

        if (TextUtils.isEmpty(adId)) {
            return;
        }
        this.adId = adId;
        saveValue(KEY_APP_AD_ID, adId);
    }

    public String getTitle() {
        if (title == null) {
            title = getValue(KEY_ACTIONBAR_TITLE, "闲玩");
        }
        return title;
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        this.title = title;
        saveValue(KEY_ACTIONBAR_TITLE, title);
    }
    public void setBaseUrl(String h5url) {
        if (TextUtils.isEmpty(h5url)) {
            return;
        }
        this.baseUrl = h5url;
        saveValue(KEY_APP_H5_URL, h5url);
    }

    public String getBaseUrl() {
        if (mode == 0) {
            baseUrl = default_list_url;
        } else {
            baseUrl = default_detail_url;
        }
        return baseUrl;
    }

    public void setAppId(String appid) {
        if (TextUtils.isEmpty(appid)) {
            return;
        }
        this.appId = appid;
        saveValue(KEY_APP_ID, appid);
    }

    public String getAppId() {
        if (appId == null) {
            appId = getValue(KEY_APP_ID, null);
        }
        return appId;
    }

    public void setAppSecret(String secret) {
        if (TextUtils.isEmpty(secret)) {
            return;
        }
        this.appSecret = secret;
        saveValue(KEY_APP_SECRET, secret);
    }

    public String getAppSecret() {
        if (appSecret == null) {
            appSecret = getValue(KEY_APP_SECRET, null);
        }
        return appSecret;
    }

    public void setAppSign(String sign) {
        if (TextUtils.isEmpty(sign)) {
            return;
        }
        this.appSign = sign;
        saveValue(KEY_APP_SIGN, sign);
    }

    public String getAppSign() {
        if (appSign == null) {
            appSign = getValue(KEY_APP_SIGN, null);
        }
        return appSign;
    }

    public void jumpToAd() {
        if (getAppId() == null)
            throw new IllegalArgumentException("appid can not be null");
        if (getAppSecret() == null)
            throw new IllegalArgumentException("aoosecret can not be null");
        if (getAppSign() == null)
            throw new IllegalArgumentException("appsign can not be null");
        Intent intent = new Intent(mContext, GameWebActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(intent);
    }

    public String getTitleBGColorString() {
        if (titleBGColorString == null) {
            titleBGColorString = getValue(KEY_ACTIONBAR_BG_COLOR, "#FA6B24");
        }
        return titleBGColorString;
    }

    public void setTitleBGColorString(String colorString) {
        this.titleBGColorString = colorString;
        if (titleBGColorString != null) {
            saveValue(KEY_ACTIONBAR_BG_COLOR, titleBGColorString);
        }
    }

    public String getTitleTextColorString() {
        if (titleTextColorString == null) {
            titleTextColorString = getValue(KEY_ACTIONBAR_TITLE_COLOR, "#FFFFFF");
        }
        return titleTextColorString;
    }

    public void setTitleTextColorString(String titleTextColorString) {
        this.titleTextColorString = titleTextColorString;
        if (titleTextColorString != null) {
            saveValue(KEY_ACTIONBAR_TITLE_COLOR, titleTextColorString);
        }
    }

    public int getTileBGColor() {
        return Color.parseColor(getTitleBGColorString());
    }

    public int getTileTextColor() {
        return Color.parseColor(getTitleTextColorString());
    }


    public void saveValue(String key, String value) {
        SharedPreferences sp = mContext.getApplicationContext().getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getValue(String key, String defValue) {
        SharedPreferences sp = mContext.getApplicationContext().getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public static String getAppIdByXML(Context context) {
        Context appContext = context.getApplicationContext();
        try {
            PackageManager packageManager = appContext.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null) {
                String var4 = applicationInfo.metaData.getString("XWAN_APPID");
                if (var4 != null) {
                    return var4.trim();
                }

                Log.e(TAG, "getAppId failed. the applicationinfo is null!");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Could not read XWAN_APPID meta-data from AndroidManifest.xml.", e);
        }

        return null;
    }

    public static String getAppSecretByXML(Context context) {
        Context appContext = context.getApplicationContext();
        try {
            PackageManager packageManager = appContext.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null) {
                String var4 = applicationInfo.metaData.getString("XWAN_APPSECRET");
                if (var4 != null) {
                    return var4.trim();
                }

                Log.e(TAG, "getAppSecret failed. the applicationinfo is null!");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Could not read XWAN_APPSECRET meta-data from AndroidManifest.xml.", e);
        }

        return null;
    }
}
