package com.yc.liaolive.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.bean.AppConfigInfo;
import com.yc.liaolive.bean.LogApi;
import com.yc.liaolive.bean.LogInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.pay.model.AlipayInterimEngine;
import com.yc.liaolive.user.manager.UserManager;
import org.json.JSONObject;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * TinyHung@Outlook.com
 * 2018/8/9
 * 日志记录
 */

public class LogRecordUtils {

    private static final String TAG = "LogRecordUtils";
    public static final int LEVE_PAY=1;
    public static final int LEVE_USER=2;
    private static LogRecordUtils mInstance;
    private ConnectivityManager mConnectionManager;
    private WifiManager mWifi;

    public static synchronized LogRecordUtils getInstance(){
        synchronized (LogRecordUtils.class){
            if(null==mInstance){
                mInstance=new LogRecordUtils();
            }
            return mInstance;
        }
    }

    public LogRecordUtils(){
        //获取网络连接管理者
        mConnectionManager = (ConnectivityManager) VideoApplication.getInstance().getApplicationContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        mWifi = (WifiManager) VideoApplication.getInstance().getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
    }

    /**
     * 写入一条日志信息
     * @param code 响应体
     * @param message 异常消息
     * @param url 报错URL
     */
    public void putLog(String url,int code,String message){
        if(!VideoApplication.TEST){
            List<LogInfo> logInfos= (List<LogInfo>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.SP_LOG_INFO);
            if(null==logInfos) logInfos=new ArrayList<>();
            if(null==mConnectionManager) mConnectionManager= (ConnectivityManager) VideoApplication.getInstance().getApplicationContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
            if(null==mWifi) mWifi= (WifiManager) VideoApplication.getInstance().getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
            NetworkInfo networkInfo = mConnectionManager.getActiveNetworkInfo();
            LogInfo logInfo=new LogInfo();
            try {
                //设备基础信息
                logInfo.setBrand(Build.BRAND);
                logInfo.setModel(Build.MODEL);
                logInfo.setSdkInt(Build.VERSION.SDK_INT);
                logInfo.setImeil(VideoApplication.mUuid);
                logInfo.setVersionName(Utils.getVersion());
                logInfo.setNetworkType(networkInfo.getType());
                logInfo.setUserid(UserManager.getInstance().getUserId());
                logInfo.setErrCode(code);
                logInfo.setErrMessage(message);
                logInfo.setRequstUrl(url);
                logInfos.add(logInfo);
            }catch (RuntimeException e){

            }catch (Exception e){

            }finally {
                ApplicationManager.getInstance().getCacheExample().remove(Constant.SP_LOG_INFO);
                ApplicationManager.getInstance().getCacheExample().put(Constant.SP_LOG_INFO, (Serializable) logInfos);
            }
        }
    }


    /**
     * 上传日志信息
     */
    public void upload() {
        if(!VideoApplication.TEST){
            List<LogInfo> logInfos = (List<LogInfo>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.SP_LOG_INFO);
            if (null != logInfos) {
//            String paramsStr = JSONArray.toJSONString(logInfos);
                for (LogInfo logInfo : logInfos) {
                    Map<String, String> params=getParams(logInfo);
                    HttpCoreEngin.get(VideoApplication.getInstance().getApplicationContext()).rxget(NetContants.getInstance().URL_LOG_UPLOAD(),
                            new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params, false)
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(ResultInfo<JSONObject> data) {
                            if(NetContants.API_RESULT_CODE==data.getCode()){
                            }
                        }
                    });
                }
                //删除缓存存在的日志信息
                ApplicationManager.getInstance().getCacheExample().remove(Constant.SP_LOG_INFO);
            }
        }
    }

    private Map<String, String> getParams(LogInfo logInfo) {
        Map<String,String> params=new HashMap<>();
        try {
            params.put("userid",logInfo.getUserid());
            params.put("brand",logInfo.getBrand());
            params.put("model",logInfo.getModel());
            params.put("sdkInt",String.valueOf(logInfo.getSdkInt()));
            params.put("versionName",String.valueOf(logInfo.getVersionName()));
            params.put("imeil",logInfo.getImeil());
            params.put("networkType",String.valueOf(logInfo.getNetworkType()));//网络类型 1:WIFI  0：4G
            params.put("requstUrl",logInfo.getRequstUrl());
            params.put("errCode",String.valueOf(logInfo.getErrCode()));
            params.put("errMessage",logInfo.getErrMessage());
            return params;
        }catch (RuntimeException e){

        }
        return params;
    }

    /**
     * 上报视频通话日志状态
     * @param requstUrl
     * @param roomName
     * @param taskTime
     * @param currentTime
     * @param errCode
     * @param errInfo
     */
    public void postCallLogs(String requstUrl, String roomName,long taskTime,long currentTime,int errCode, String errInfo) {
        LogApi simpli=new LogApi();
        simpli.setErrCode(errCode);
        simpli.setErrMessage(errInfo);
        simpli.setRequstUrl(requstUrl);
        simpli.setRequstTime(taskTime);
        simpli.setCurrentTime(currentTime);
        simpli.setRoomName(roomName);
        ActionLogInfo<LogApi> actionLogInfo=new ActionLogInfo();
        actionLogInfo.setData(simpli);
        UserManager.getInstance().postActionState(NetContants.POST_ACTION_TYPE_CALL_POST,actionLogInfo,null);
    }

    /**
     * 上报系统级的错误日志信息
     * @param leve 日志等级 1：支付、下订单  2：登录、注册
     * @param message 错误描述
     * @param appSign 应用签名
     */
    public void postSystemErrorMessage(int leve,String message,String appSign){

        Map<String,String> params=new HashMap<>();
        params.put("leve",leve+"");
        params.put("msg",message);
        params.put("app_sign",appSign);
        params.put("userid",UserManager.getInstance().getUserId());
        AppConfigInfo configInfo= InitUtils.get().getConfigInfo(AppEngine.getApplication());
        if(null!=configInfo){
            params.put("site_id",configInfo.getSite_id());
            params.put("soft_id",configInfo.getSoft_id());
            params.put("node_id",configInfo.getNode_id());
            params.put("node_url",configInfo.getNode_url());
        }
        params.put("app_version", String.valueOf(Utils.getVersionCode()));
        params.put("agent_id", ChannelUtls.getInstance().getAgentId());
        params.put("app_name",AppEngine.getApplication().getResources().getString(R.string.app_name));
        Logger.d(TAG,"postSystemErrorMessage-->params:"+params.toString());
        HttpCoreEngin.get(VideoApplication.getInstance().getApplicationContext())
                .rxpost(NetContants.getInstance().URL_REPORT_DEADLY(), new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params, true,true,true)
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if(NetContants.API_RESULT_CODE==data.getCode()){
                    Logger.d(TAG,"postSystemErrorMessage-->OK");
                }
            }
        });
    }

    /**
     * 取消订单
     * @param order_sn
     */
    public void cancelOeder(String order_sn) {
        new AlipayInterimEngine(AppEngine.getApplication().getApplicationContext()).orderCancle(order_sn)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ResultInfo data) {
                        if(null!=data&&data.getCode()==NetContants.API_RESULT_CODE){

                            Logger.d(TAG,"cancelOeder-->OK");
                        }
                    }
                });
    }

    /**
     * MD5加密
     * @param byteStr 需要加密的内容
     * @return 返回 byteStr的md5值
     */
    public String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer("");
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
//            return Base64.encodeToString(byteArray,Base64.NO_WRAP);
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

    /**
     * 获取app签名md5值,与“keytool -list -keystore D:\Desktop\app_key”‘keytool -printcert
     * file D:\Desktop\CERT.RSA’获取的md5值一样
     */
    public String getAppSignToMd5(Context context) {
        if(!TextUtils.isEmpty(VideoApplication.getInstance().getAppSignToMd5())){
            return VideoApplication.getInstance().getAppSignToMd5();
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = encryptionMD5(sign.toByteArray());
            VideoApplication.getInstance().setAppSignToMd5(signStr);
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}