package com.kaikai.securityhttp.domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.kaikai.securityhttp.utils.FileUtil;
import com.kaikai.securityhttp.utils.LogUtil;
import com.kaikai.securityhttp.utils.PathUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by zhangkai on 16/9/19.
 */
public class GoagalInfo {

    private static final String PUBLIC_KEY = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA5KaI8l7xplShIEB0PwgmMRX/3uGG9BDLPN6wbMmkkO7H1mIOXWB/Jdcl4/IMEuUDvUQyv3P+erJwZ1rvNstohXdhp2G7IqOzH6d3bj3Z6vBvsXP1ee1SgqUNrjX2dn02hMJ2Swt4ry3n3wEWusaWmev4CSteSKGHhBn5j2Z5B+CBOqPzKPp2Hh23jnIH8LSbXmW0q85a851BPwmgGEan5HBPq04QUjo6SQsW/7dLaaAXfUTYETe0HnpLaimcHl741ftGyrQvpkmqF93WiZZXwlcDHSprf8yW0L0KA5jIwq7qBeu/H/H5vm6yVD5zvUIsD7htX0tIcXeMVAmMXFLX35duvYDpTYgO+DsMgk2Q666j6OcEDVWNBDqGHc+uPvYzVF6wb3w3qbsqTnD0qb/pWxpEdgK2BMVz+IPwdP6hDsDRc67LVftYqHJLKAfQt5T6uRImDizGzhhfIfJwGQxI7TeJq0xWIwB+KDUbFPfTcq0RkaJ2C5cKIx08c7lYhrsPXbW+J/W4M5ZErbwcdj12hrfV8TPx/RgpJcq82otrNthI3f4QdG4POUhdgSx4TvoGMTk6CnrJwALqkGl8OTfPKojOucENSxcA4ERtBw4It8/X39Mk0aqa8/YBDSDDjb+gCu/Em4yYvrattNebBC1zulK9uJIXxVPi5tNd7KlwLRMCAwEAAQ==";
    public String publicKey;

    public ChannelInfo channelInfo = null;
    public PackageInfo packageInfo = null;

    public String uuid = "";
    public String channel = "default";

    public String configPath = "";

    public Object extra;

    private static GoagalInfo goagalInfo = new GoagalInfo();

    public static GoagalInfo get() {
        return goagalInfo;
    }

    static {
        try{
            System.loadLibrary("caoliao");
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

//    public native static String getPublicKey();

    public String getUUID(Context context){
        if(!TextUtils.isEmpty(uuid)&&uuid.length()>0){
            return uuid;
        }
        return getUid(context);
    }

    public void init(Context context) {

        configPath = PathUtil.getConfigPath(context);

        String result1 = null;
        String result2 = null;
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zf = null;
        try {
            zf = new ZipFile(sourceDir);
            ZipEntry ze1 = zf.getEntry("META-INF/gamechannel.json");
            InputStream in1 = zf.getInputStream(ze1);
            result1 = FileUtil.readString(in1);
            LogUtil.msg("渠道->" + result1);

            ZipEntry ze2 = zf.getEntry("META-INF/rsa_public_key.pem");
            InputStream in2 = zf.getInputStream(ze2);
            result2 = FileUtil.readString(in2);
            LogUtil.msg("公钥->" + result2);
        } catch (Exception e) {
            LogUtil.msg("apk中gamechannel或rsa_public_key文件不存在", LogUtil.W);
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
            }
        }

        String name = gamechannelFilename;
        if (result1 != null) {
            FileUtil.writeInfoToFile(result1, configPath, name);
        } else {
            result1 = FileUtil.readInfoFromFile(configPath, name);
        }

        if (result1 != null) {
            channel = result1;
        }

        name = rasPublickeylFilename;
        if (result2 != null) {
            publicKey = getPublicKey(result2);
            FileUtil.writeInfoToFile(result2, configPath, name);
        } else {
            result2 = FileUtil.readInfoFromFile(configPath, name);
            if (result2 != null) {
                publicKey = getPublicKey(result2);
            }
        }

        channelInfo = getChannelInfo();
        uuid = getUid(context);
        packageInfo = getPackageInfo(context);

    }

    private String rasPublickeylFilename = "rsa_public_key.pem";
    private String gamechannelFilename = "gamechannel.json";

    public GoagalInfo setRasPublickeylFilename(String rasPublickeylFilename) {
        this.rasPublickeylFilename = rasPublickeylFilename;
        return this;
    }

    public GoagalInfo setGamechannelFilename(String gamechannelFilename) {
        this.gamechannelFilename = gamechannelFilename;
        return this;
    }

    private ChannelInfo getChannelInfo() {
        try {
            return new Gson().fromJson(channel, ChannelInfo.class);
        } catch (Exception e) {
            LogUtil.msg("渠道信息解析错误->" + e.getMessage());
        }
        return null;
    }

    private String getPublicKey(InputStream in) {
        String result = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if (mLine.startsWith("----")) {
                    continue;
                }
                result += mLine;
            }
        } catch (Exception e) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {
                }
            }
        }
        return result;
    }

    public String getPublicKeyString() {
//        publicKey = getPublicKey();
        publicKey = getPublicKey(PUBLIC_KEY);
        return publicKey;
    }

    public String getPublicKey(String key) {
        return key.replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\r", "")
                .replace("\n", "");
    }

    @SuppressLint("MissingPermission")
    public String getUid(Context context) {
        String imeil="";
        try {
            imeil = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if(TextUtils.isEmpty(imeil)){
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                imeil = telephonyManager.getDeviceId();
            }
            if (TextUtils.isEmpty(imeil) || imeil.equals("02:00:00:00:00:00")) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                imeil = wInfo.getMacAddress();
            }
            if (TextUtils.isEmpty(imeil)) {
                imeil = "";
            }
            return imeil;
        }catch (RuntimeException e){

        }
        if(TextUtils.isEmpty(imeil)) imeil="";
        return imeil;
    }


    public PackageInfo getPackageInfo(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo;
        } catch (Exception e) {
        }
        return null;
    }
}
