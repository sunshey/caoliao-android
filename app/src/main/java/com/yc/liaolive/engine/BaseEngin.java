package com.yc.liaolive.engine;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.GoagalInfo;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.kaikai.securityhttp.net.entry.Response;
import com.kaikai.securityhttp.net.entry.UpFileInfo;
import com.kaikai.securityhttp.net.impls.OKHttpRequest;
import com.kaikai.securityhttp.utils.FileUtil;
import com.kaikai.securityhttp.utils.LogUtil;
import com.kaikai.securityhttp.utils.PathUtil;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.bean.HttpLogSendBean;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.start.manager.VersionCheckManager;
import com.yc.liaolive.start.model.VersionCheckData;
import com.yc.liaolive.start.model.bean.UpdataApkInfo;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Utils;
import org.apache.http.conn.ConnectTimeoutException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zhangkai on 2017/4/28.
 */

public abstract class BaseEngin<T> {

    private Context mContext;

    public BaseEngin(Context context) {
        this.mContext = context;
    }

    //< 同步请求get 1
    private ResultInfo<T> get(Type type, Map<String, String> params, Map<String, String> headers, boolean isEncryptResponse) {
        ResultInfo<T> resultInfo = null;
        String url = getUrl();
        try {
            Response response = OKHttpRequest.getImpl().get(url, params, headers, isEncryptResponse);
            if (200 == response.code) {
                resultInfo = getResultInfo(response.body, type);
                if (NetContants.API_RESULT_APP_SHOULD_UPDATE == resultInfo.getCode()) {
                    handleUpdateResult(resultInfo);
                }
            } else {
                resultInfo = new ResultInfo<>();
            }
            resultInfo.setHttpCode(response.code);
            resultInfo.setHttpMessage(response.response.message());
            resultInfo.setResponse(response.response);
            if (200 != response.code) {
                resultInfo.setMessage("网络请求失败，请稍后重试"+response.code);
                sendErrorLog(response.code, response.response.message(), url);
            }
        } catch (Exception e) {
            LogUtil.msg("异常->" + e, LogUtil.W);
            resultInfo = new ResultInfo<>();
            return handleErrorNetWork(resultInfo, e, url);
        }
        return resultInfo;
    }

    //< 同步请求rxjava get 3
    public Observable<ResultInfo<T>> rxget(final Type type, final Map<String, String> params, final Map<String, String> headers, final boolean
            isEncryptResponse) {
        return Observable.just("").map(new Func1<Object, ResultInfo<T>>() {
            @Override
            public ResultInfo<T> call(Object o) {
                return get(type, params, headers, isEncryptResponse);
            }
        }).subscribeOn(Schedulers.newThread()).onErrorReturn(new Func1<Throwable, ResultInfo<T>>() {
            @Override
            public ResultInfo<T> call(Throwable throwable) {
                LogUtil.msg(throwable.getMessage());
                return null;
            }
        });
    }

    //< 同步请求rxjava get 2
    public Observable<ResultInfo<T>> rxget(final Type type, final Map<String, String> params, final boolean
            isEncryptResponse) {
        return rxget(type, params, null, isEncryptResponse);
    }

    //< 同步请求rxjava get 1
    public Observable<ResultInfo<T>> rxget(final Type type, final boolean isEncryptResponse) {
        return rxget(type, null, isEncryptResponse);
    }

    //< 同步请求post 1
    private ResultInfo<T> post(Type type, Map<String, String> params, Map<String, String> headers, boolean
            isrsa, boolean iszip, boolean isEncryptResponse) {
        if (params == null) {
            params = new HashMap<>();
        }
        ResultInfo<T> resultInfo = null;
        String url = getUrl();
        try {
            Response response = OKHttpRequest.getImpl().post(url, params, headers, isrsa, iszip, isEncryptResponse);
            if (200 == response.code) {
                resultInfo = getResultInfo(response.body, type);
                if (NetContants.API_RESULT_APP_SHOULD_UPDATE == resultInfo.getCode()) {
                    handleUpdateResult(resultInfo);
                }
            } else {
                resultInfo = new ResultInfo<>();
            }
            resultInfo.setHttpCode(response.code);
            resultInfo.setHttpMessage(response.response.message());
            resultInfo.setResponse(response.response);
            if (isrsa && publicKeyError(resultInfo, response.body)) {
                return post(type, params, headers, isrsa, iszip, isEncryptResponse);
            }
            if (200 != response.code) {
                resultInfo.setMessage("网络请求失败，请稍后重试"+response.code);
                sendErrorLog(response.code, response.response.message(), url);
            }
        } catch (Exception e) {
            resultInfo = new ResultInfo<>();
            LogUtil.msg(url +" 异常->" + e, LogUtil.W);
            return handleErrorNetWork(resultInfo, e, url);
        }
        return resultInfo;
    }


    //< 同步请求rxjava post 2
    public Observable<ResultInfo<T>> rxpost(final Type type, final Map<String, String>
            params, final boolean isrsa, final boolean iszip, final boolean isEncryptResponse) {
        return rxpost(type, params, null, isrsa, iszip, isEncryptResponse);
    }


    //< 同步请求rxjava post 1
    public Observable<ResultInfo<T>> rxpost(final Type type, final Map<String, String>
            params, final Map<String, String>
                                                    headers, final boolean isrsa, final boolean iszip, final boolean isEncryptResponse) {
        return Observable.just("").map(new Func1<Object, ResultInfo<T>>() {
            @Override
            public ResultInfo<T> call(Object o) {
                return post(type, params, headers, isrsa, iszip, isEncryptResponse);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).onErrorReturn(new Func1<Throwable, ResultInfo<T>>() {
            @Override
            public ResultInfo<T> call(Throwable throwable) {
                LogUtil.msg(throwable.getMessage());
                return null;
            }
        });
    }

    //< 同步请求rxjava post string
    public Observable<String> rxpost(final Map<String, String> header, final MediaType type, final String
            body) {
        return Observable.just("").map(new Func1<Object, String>() {
            @Override
            public String call(Object o) {
                String data = "";
                try {
                    Response response = OKHttpRequest.getImpl().post(getUrl(), header, type, body);
                    if (response != null) {
                        data = response.body;
                    }
                } catch (Exception e) {
                    LogUtil.msg("异常->" + e, LogUtil.W);
                }
                return data;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).onErrorReturn(new Func1<Throwable, String>() {
            @Override
            public String call(Throwable throwable) {
                LogUtil.msg(throwable.getMessage());
                return null;
            }
        });
    }

    //< 同步请求rxjava post string no header
    public Observable<String> rxpost(final MediaType type, final String
            body) {
        return rxpost(null, type, body);
    }

    //< 同步请求rxjava post json
    public Observable<String> rxpost(final Map<String, String> header, final String
            json) {
        return rxpost(header, MediaType.parse("application/json; charset=utf-8"), json);
    }

    //< 同步请求rxjava post json no header
    public Observable<String> rxpost(final String
                                             json) {
        return rxpost(null, MediaType.parse("application/json; charset=utf-8"), json);
    }


    //< 同步请求uploadFile 1
    public T uploadFile(Type type, UpFileInfo
            upFileInfo, Map<String, String>
                                params, Map<String, String> headers, boolean isEncryptResponse) {
        if (params == null) {
            params = new HashMap<>();
        }
        T resultInfo = null;
        try {
            Response response = OKHttpRequest.getImpl().uploadFile(getUrl(), upFileInfo, params,
                    headers, isEncryptResponse);
            resultInfo = new Gson().fromJson(response.body, type);
        } catch (Exception e) {
            LogUtil.msg("异常->" + e, LogUtil.W);
        }
        return resultInfo;
    }

    //< 异步请求rxuploadFile 2
    public Observable<T> rxuploadFile(final Type type, final UpFileInfo upFileInfo, final Map<String, String>
            params, final boolean isEncryptResponse) {
        return rxuploadFile(type, upFileInfo, params, null, isEncryptResponse);
    }

    //< 异步请求rxuploadFile 1
    public Observable<T> rxuploadFile(final Type type, final UpFileInfo upFileInfo, final Map<String, String>
            params, final Map<String, String>
                                              headers, final boolean isEncryptResponse) {
        return Observable.just("").map(new Func1<String, T>() {
            @Override
            public T call(String s) {
                return uploadFile(type, upFileInfo, params, headers, isEncryptResponse);
            }
        }).subscribeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers
                .mainThread()).onErrorReturn(new Func1<Throwable, T>() {
            @Override
            public T call(Throwable throwable) {
                LogUtil.msg(throwable.getMessage());
                return null;
            }
        });
    }

    private ResultInfo<T> getResultInfo(String body, Type type) {
        ResultInfo<T> resultInfo;
        try {
            if (type != null) {
                resultInfo = new Gson().fromJson(body, type);
            } else {
                resultInfo = new Gson().fromJson(body, new TypeToken<ResultInfo<T>>() {}.getType());
            }
        }catch (Exception e){
            return new ResultInfo<>();
        }
        return resultInfo;
    }

    public abstract String getUrl();

    private boolean publicKeyError(ResultInfo<T> resultInfo, String body) {
        if (resultInfo != null) {
            if (resultInfo.getCode() == HttpConfig.PUBLICKEY_ERROR) {
                ResultInfo<GoagalInfo> resultInfoPE = new Gson().fromJson(body, new
                        TypeToken<ResultInfo<GoagalInfo>>() {}.getType());
                if (resultInfoPE.getData() != null && resultInfoPE.getData().getPublicKeyString() != null) {
                    GoagalInfo.get().publicKey = GoagalInfo.get().getPublicKey(resultInfoPE.getData().getPublicKeyString());
                    LogUtil.msg("公钥出错->" + GoagalInfo.get().publicKey);
                    String name = "rsa_public_key.pem";
                    FileUtil.writeInfoToFile(GoagalInfo.get().publicKey, PathUtil.getConfigPath(mContext), name);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 处理版本升级
     * 如果直接返回update的对象，则直接显示升级弹窗，如果不是，就请求版本升级接口
     * @param resultInfo
     */
    private void handleUpdateResult(ResultInfo<T> resultInfo) {
        if (resultInfo.getData() != null && resultInfo.getData() instanceof UpdataApkInfo) {
            VersionCheckManager.getInstance().checkVersion((UpdataApkInfo) resultInfo.getData(), false);
        } else {
            VersionCheckData.checkedVerstion(0, new UserServerContract.OnNetCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    if(null != object && object instanceof UpdataApkInfo){
                        UpdataApkInfo updataApkInfo = (UpdataApkInfo) object;
                        VersionCheckManager.getInstance().checkVersion(updataApkInfo, false);
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {

                }
            });
        }
    }

    /**
     * 处理网络请求失败情况
     * @param resultInfo
     * @param e
     * @return
     */
    private ResultInfo<T> handleErrorNetWork(ResultInfo<T> resultInfo, Exception e, String url) {
        if (!Utils.isNetWorkAvaliable()){
            resultInfo.setHttpCode(-1);
            resultInfo.setMessage("网络未连接");
        } else {
            resultInfo.setHttpCode(0);
            String errorMsg;
            if (e instanceof SocketTimeoutException) {
                errorMsg = "服务器响应的超时";
            } else if (e instanceof ConnectTimeoutException) {
                errorMsg = "服务器请求超时";
            } else if (e instanceof JsonParseException) {
                errorMsg = "json解析失败";
            } else {
                errorMsg = "网络请求失败";
            }
            resultInfo.setMessage(errorMsg + "，请稍后重试");
            sendErrorLog(0, errorMsg, url);
        }
        return resultInfo;
    }

    /**
     * 上传请求失败日志
     */
    private void sendErrorLog (int httpCode, String message, String requestUrl) {
        if (!requestUrl.endsWith("chatlogs.json")) {
            ActionLogInfo<HttpLogSendBean> actionLogInfo = new ActionLogInfo<>();
            HttpLogSendBean bean = new HttpLogSendBean();
            bean.setHttpCode(httpCode);
            bean.setMessage(message);
            bean.setRequestUrl(requestUrl);
            actionLogInfo.setData(bean);
            UserManager.getInstance().postActionState(NetContants.POST_ACTION_TYPE_REQUST_FAILD, actionLogInfo,null);
        }
    }

}