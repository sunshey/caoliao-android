package com.yc.liaolive.ui.presenter;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.GoagalInfo;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.BuildConfig;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.bean.AttentInfo;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.BuyMediaInfo;
import com.yc.liaolive.bean.CreateRoomInfo;
import com.yc.liaolive.bean.HostInfo;
import com.yc.liaolive.bean.LoginConfig;
import com.yc.liaolive.bean.MediaFileInfo;
import com.yc.liaolive.bean.PersonCenterInfo;
import com.yc.liaolive.bean.PlatfromAccountInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.bean.RoomTaskDataInfo;
import com.yc.liaolive.bean.TagInfo;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.bean.UploadAuthenticationInfo;
import com.yc.liaolive.bean.UploadObjectInfo;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.live.bean.GiftTypeInfo;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.HostManager;
import com.yc.liaolive.recharge.model.bean.VipPopupData;
import com.yc.liaolive.start.manager.AppManager;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.bean.VerificationInfo;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ChannelUtls;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * TinyHung@outlook.com
 * 2018/5/23 10:53
 * 用户模块相关的API
 */
public class UserServerPresenter extends RxBasePresenter<UserServerContract.View> implements UserServerContract.Presenter<UserServerContract.View> {

    /**
     * 手机号码注册
     *
     * @param code
     * @param phone
     * @param zone
     * @param callBackListener
     */
    @Override
    public void registerByPhoone(String code, String phone, String zone, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_PHONE_REGISTER());
        params.put("code", code);
        params.put("phone", phone);
        params.put("zone", zone);
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_PHONE_REGISTER(),
                new TypeToken<ResultInfo<UserInfo>>() {}.getType(), params, isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<UserInfo>>() {
            @Override
            public void onCompleted() {
                if (null != mView) mView.complete();
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,"注册失败："+NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<UserInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        //新用户
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if (null != data.getData() && !TextUtils.isEmpty(data.getData().getUserid())) {
//                                String clToken = data.getResponse().header("cltoken");
//                                String clTokenExp = data.getResponse().header("cltokenexp");
//                                Logger.d(TAG,"clToken:"+clToken+",clTokenExp:"+clTokenExp);
                                UserManager.getInstance().saveUserToken(data.getData().getUserid(), data.getResponse());
                                UserManager.getInstance().setLoginUserInfo(data.getData());
                                callBackListener.onSuccess(data.getCode());
                            } else {
                                callBackListener.onFailure(-1, NetContants.NET_REQUST_JSON_ERROR);
                            }
                            //用户已存在
                        } else if (NetContants.API_RESULT_USER_EXIST == data.getCode()) {
                            if (null != data.getData() && !TextUtils.isEmpty(data.getData().getUserid())) {
                                UserManager.getInstance().saveUserToken(data.getData().getUserid(), data.getResponse());
                                UserManager.getInstance().setLoginUserInfo(data.getData());
                                callBackListener.onSuccess(data.getCode());
                            } else {
                                callBackListener.onFailure(-1, NetContants.NET_REQUST_JSON_ERROR);
                            }
                        } else {
                            String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
                            String content="用户手机号码注册失败。"+"errorCode:"+data.getCode()+",errorMsg:"+data.getMsg()+",appSign:"+appSign;
                            LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_USER,content,appSign);
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 第三方注册
     *
     * @param openid
     * @param access_token
     * @param account_type
     * @param callBackListener
     */
    @Override
    public void registerByOther(String openid, String access_token, String account_type, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_OTHER_REGISTER());
        params.put("openid", openid);
        params.put("app_id", Constant.LOGIN_QQ_KEY);
        params.put("access_token", access_token);
        params.put("account_type", account_type);

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_OTHER_REGISTER(),
                new TypeToken<ResultInfo<UserInfo>>() {}.getType(), params, isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<UserInfo>>() {
            @Override
            public void onCompleted() {
                if (null != mView) mView.complete();
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,"注册失败："+NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<UserInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if (null != data.getData() && !TextUtils.isEmpty(data.getData().getUserid())) {
                                UserManager.getInstance().saveUserToken(data.getData().getUserid(), data.getResponse());
                                UserManager.getInstance().setLoginUserInfo(data.getData());
                                callBackListener.onSuccess(data.getData());
                            } else {
                                callBackListener.onFailure(-1, NetContants.NET_REQUST_JSON_ERROR);
                            }
                        } else {
                            String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
                            String content="用户第三方账号注册失败，平台："+account_type+"。"+"errorCode:"+data.getCode()+",errorMsg:"+data.getMsg()+",appSign:"+appSign;
                            LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_USER,content,appSign);
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(data.getCode(), NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 用户登录
     *
     * @param userID
     * @param callBackListener
     */
    @Override
    public void login(String userID,final UserServerContract.OnNetCallBackListener callBackListener) {
        getDefaultPrames(NetContants.getInstance().URL_LOGIN());
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_LOGIN(),
                new TypeToken<ResultInfo<LoginConfig>>() {}.getType(), null, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<LoginConfig>>() {
            @Override
            public void onCompleted() {
                if (null != mView) mView.complete();
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,"登录失败："+NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<LoginConfig> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            LoginConfig config = data.getData();
                            if (null != config) {
                                UserManager.getInstance().setVip_phone(config.getVip_phone());
                                UserManager.getInstance().setPopup_page(config.getPopup_page());
                                callBackListener.onSuccess(config);
                            } else {
                                callBackListener.onFailure(-1, NetContants.NET_REQUST_JSON_ERROR);
                            }
                            //用户已存在
                        } else {
                            String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
                            String content="用户登录失败。"+"errorCode:"+data.getCode()+",errorMsg:"+data.getMsg()+",appSign:"+appSign;
                            LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_USER,content,appSign);
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(data.getCode(), NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取用户详细信息
     *
     * @param userID
     * @param toUserID
     * @param dataMore 1：全部的用户信息 0：局部用户信息
     * @param callBackListener
     */
    @Override
    public void getUserFullData(String userID, String toUserID, int dataMore ,final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_PERSONAL_CENTER());
        params.put("userid", userID);
        params.put("to_userid", toUserID);
        params.put("data_more", String.valueOf(dataMore));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_PERSONAL_CENTER(), new TypeToken<ResultInfo<UserInfo>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<UserInfo>>() {
            @Override
            public void onCompleted() {
                if (null != mView) mView.complete();
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,"获取用户信息失败："+NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<UserInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if (null != data.getData() && !TextUtils.isEmpty(data.getData().getUserid())) {
                                callBackListener.onSuccess(data.getData());
                            } else {
                                callBackListener.onFailure(-1, NetContants.NET_REQUST_JSON_ERROR);
                            }
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(data.getCode(), NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 更新用户信息
     *
     * @param nickName
     * @param avatar
     * @param frontcover
     */
    @Override
    public void uploadUserInfo(String nickName, String avatar, String frontcover, int sex, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_UPLOAD_USER_INFO());
        params.put("userid", UserManager.getInstance().getUserId());
        if (!TextUtils.isEmpty(nickName)) params.put("nickname", nickName);
        params.put("sex", String.valueOf(sex));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_UPLOAD_USER_INFO(), new TypeToken<ResultInfo<UserInfo>>() {
        }.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<UserInfo>>() {
            @Override
            public void onCompleted() {
                if (null != mView) mView.complete();
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,"更新用户信息失败："+NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<UserInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getMsg());
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(data.getCode(), NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 账号登出
     * @param userId
     * @param callBackListener
     */
    @Override
    public void loginOut(String userId, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_LOGIN_OUT());
        params.put("userid", userId);

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_LOGIN_OUT(),
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
                if (null != mView) mView.complete();
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,"更新用户信息失败："+NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getMsg());
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(data.getCode(), NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    private boolean isFollowing=false;
    /**
     * 关注用户
     *
     * @param toUserID 对方ID
     * @param status 状态 0：取关 1：关注  2: 查询关注状态
     * @param callBackListener
     */
    @Override
    public void followUser(String toUserID, int status, final UserServerContract.OnNetCallBackListener callBackListener) {
        if(TextUtils.isEmpty(toUserID)){
            if(null!=callBackListener) callBackListener.onFailure(-1,(2==status?"查询":"关注")+"对象错误");
            return;
        }
        if(isFollowing){
            if(null!=callBackListener) callBackListener.onFailure(-1,"点击频率过快~");
            return;
        }
        if(status!=2&&toUserID.equals(UserManager.getInstance().getUserId())){
            if(null!=callBackListener) callBackListener.onFailure(-1,"你时刻都在关注你自己");
            return;
        }
        isFollowing=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FOLLOW_USER());
        params.put("attentid", toUserID);
        params.put("userid", UserManager.getInstance().getUserId());
        params.put("op", String.valueOf(status));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FOLLOW_USER(), new TypeToken<ResultInfo<AttentInfo>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<AttentInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isFollowing=false;
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<AttentInfo> data) {
                isFollowing=false;
                if (null != callBackListener) {
                    if(null!=data){
                        if(NetContants.API_RESULT_CODE == data.getCode()){
                            if(null!=data.getData()&&data.getData().getIs_attent()>-1){
                                //获取关注状态成功
                                callBackListener.onSuccess(data.getData());
                            }else{
                                //关注、取关成功
                                callBackListener.onSuccess(null);
                            }
                        }else{
                            callBackListener.onFailure(data.getCode(),NetContants.getErrorMsg(data));
                        }
                    }else{
                        callBackListener.onFailure(data.getCode(), NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 举报用户
     *
     * @param toUserID
     * @param callBackListener
     */
    @Override
    public void reportUser(String toUserID, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_REPORT());
        params.put("report_userid", toUserID);
        params.put("content", "我把你举报了，来打我呀");

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_REPORT(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 举报附件
     * @param toUserID
     * @param id
     * @param type
     * @param callBackListener
     */
    private boolean isReportUsering=false;
    @Override
    public void reportMediaFile(String toUserID,long id, int type, final UserServerContract.OnNetCallBackListener callBackListener) {
        if(isReportUsering){
            if(null!=callBackListener) callBackListener.onFailure(-2,"点击频率过快~");
            return;
        }
        isReportUsering=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_REPORT());
        params.put("report_userid", toUserID);
        params.put("content", "内容涉嫌违规");
        params.put("attachment_id",String.valueOf(id));
        params.put("type", String.valueOf(type));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_REPORT(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isReportUsering=false;
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                isReportUsering=false;
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 添加用户至黑名单
     *
     * @param toUserID
     * @param status
     * @param callBackListener
     */
    @Override
    public void addBlackList(String toUserID, int status, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_ADD_BLACK());
        params.put("black_userid", toUserID);
        params.put("state", String.valueOf(status));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_ADD_BLACK(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 将用户从黑名单移除
     *
     * @param toUserID
     * @param callBackListener
     */
    @Override
    public void removeBlackList(String toUserID, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_REMOVE_BLACKLIST());
        params.put("black_userid", toUserID);
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_REMOVE_BLACKLIST(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(1);
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 检查用户是否在黑名单内
     *
     * @param toUserID
     * @param callBackListener
     */
    @Override
    public void isBlackList(String toUserID, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_IS_BLACK());
        params.put("black_userid", toUserID);
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_IS_BLACK(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if (null != data.getData()) {
                                JSONObject jsonObject = data.getData();
                                if (null != jsonObject && jsonObject.length() > 0) {
                                    callBackListener.onSuccess(jsonObject.optInt("is_black"));
                                } else {
                                    callBackListener.onFailure(data.getCode(), NetContants.NET_REQUST_JSON_ERROR);
                                }
                            }
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 绑定手机号
     *
     * @param phone
     * @param code
     * @param zone
     * @param callBackListener
     */
    @Override
    public void bindPhone(String phone, String code, String zone, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_BIND_MOBILE());
        params.put("phone", phone);
        params.put("zone", zone);
        params.put("code", code);

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_BIND_MOBILE(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 分享上报
     *
     * @param charUserID       被分享对象
     * @param platform         平台
     * @param callBackListener
     */
    @Override
    public void shareStatistics(String charUserID, int platform, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_SHARE_REPORT());
        params.put("chare_userid", charUserID);
        params.put("platform", String.valueOf(platform));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_SHARE_REPORT(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 分享视频上报
     * @param videoInfo
     * @param platform
     * @param callBackListener
     */
    @Override
    public void shareVideoPost(final PrivateMedia videoInfo, int platform, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_SHARE_VIDEO());
        params.put("chare_userid", videoInfo.getUserid());
        params.put("video_id", String.valueOf(videoInfo.getId()));
        params.put("platform", String.valueOf(platform));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_SHARE_VIDEO(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            videoInfo.share_number++;
                            callBackListener.onSuccess(videoInfo);
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 预览视频上报
     * @param videoInfo
     * @param callBackListener
     */
    @Override
    public void previewVideoPost(final PrivateMedia videoInfo, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_SHARE_VIDEO());
        params.put("chare_userid", videoInfo.getUserid());
        params.put("video_id",String.valueOf(videoInfo.getId()));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_SHARE_VIDEO(),
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            videoInfo.browse_number++;
                            callBackListener.onSuccess(videoInfo);
                        } else {
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 查询新手任务奖励
     *
     * @param type
     * @param callBackListener
     */
    @Override
    public void getTasks(String type, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_TASK_CENTER_LIST());
        params.put("type", type);

        final Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_TASK_CENTER_LIST(), new TypeToken<ResultInfo<ResultList<TaskInfo>>>() {
        }.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<TaskInfo>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<TaskInfo>> data) {
                if (null != data) {
                    if (NetContants.API_RESULT_CODE == data.getCode()) {
                        if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() > 0) {
                            callBackListener.onSuccess(data.getData().getList());
                        } else if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() <= 0) {
                            callBackListener.onFailure(101, "暂无新手奖励");
                        } else {
                            callBackListener.onFailure(-1, NetContants.NET_REQUST_JSON_ERROR);
                        }
                    } else {
                        callBackListener.onFailure(data.getCode(), data.getMsg());
                    }
                } else {
                    callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 获取直播间任务
     * @param callBackListener
     */
    @Override
    public void getRoomTasks(final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_TASK());
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_TASK() , new TypeToken<ResultInfo<ResultList<RoomTaskDataInfo>>>() {}.getType(), params, getHeaders(),isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<RoomTaskDataInfo>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<RoomTaskDataInfo>> data) {
                if (null != data) {
                    if (NetContants.API_RESULT_CODE == data.getCode()) {
                        if (null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0) {
                            callBackListener.onSuccess(data.getData().getList());
                        } else if (null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0) {
                            callBackListener.onFailure(101, "暂无任务可做");
                        } else {
                            callBackListener.onFailure(-1, NetContants.NET_REQUST_JSON_ERROR);
                        }
                    } else {
                        callBackListener.onFailure(data.getCode(), data.getMsg());
                    }
                } else {
                    callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 领取奖励
     * @param task_id
     * @param callBackListener 结果回调
     */
    @Override
    public void drawTaskAward(int task_id, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_TASK_GET());
        params.put("task_id", String.valueOf(task_id));
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_TASK_GET(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != data) {
                    if (NetContants.API_RESULT_CODE == data.getCode()) {
                        if (null != callBackListener) callBackListener.onSuccess("领取成功");
                    } else {
                        if (null != callBackListener)
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                    }
                } else {
                    if (null != callBackListener)
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 领取奖励
     * @param taskInfo
     * @param callBackListener 结果回调
     */
    @Override
    public void drawTaskAward(final TaskInfo taskInfo, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_TASK_GET());
        params.put("task_id", String.valueOf(taskInfo.getApp_id()));

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_TASK_GET(),
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != data) {
                    if (NetContants.API_RESULT_CODE == data.getCode()) {
                        if (null != callBackListener) callBackListener.onSuccess(taskInfo);
                    } else {
                        if (null != callBackListener)
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                    }
                } else {
                    if (null != callBackListener)
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 上报用户位置
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 半径
     * @param location 城市
     * @param callBackListener
     */
    @Override
    public void uploadLocation(double longitude, double latitude, float radius,String location, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_LOCATION());
        params.put("longitude", String.valueOf(longitude));
        params.put("latitude", String.valueOf(latitude));
        params.put("location", location);
        params.put("radius", String.valueOf(radius));

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_LOCATION(),
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params, getHeaders(),
                isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != data) {
                    if (NetContants.API_RESULT_CODE == data.getCode()) {
                        if (null != callBackListener) callBackListener.onSuccess(data.getData());
                    } else {
                        if (null != callBackListener)
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                    }
                } else {
                    if (null != callBackListener)
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 获取Host
     *
     * @param callBackListener
     */
    @Override

    public void getServerHost(final UserServerContract.OnNetCallBackListener callBackListener) {
        String host;
        if (BuildConfig.FLAVOR.contains("ttvideo")) {
            host = NetContants.SERVER_URL_TTVIDEO;
        } else {
            host = NetContants.SERVER_URL;
        }

        Map<String, String> params = getDefaultPrames(host);
        params.put("agent_id", ChannelUtls.getInstance().getAgentId());

        Subscription subscription = HttpCoreEngin.get(mContext).rxget(
                NetContants.SERVER_URL, new TypeToken<ResultInfo<HostInfo>>() {}.getType(), params,false)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<HostInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<HostInfo> data) {
                if (null != data) {
                    if (NetContants.API_RESULT_CODE == data.getCode()) {
                        HostInfo info = data.getData();
                        if (null != info) {
                            AppManager.getInstance().setUrlBlackList(info.getBlack_url_list());
                            HostManager.getInstance().setHostUrl(info.getDomain());
                            UserManager.getInstance().setHomeIndex(info.getHomeIndex());
                            UserManager.getInstance().setChatAvailable(String.valueOf(info.getPri_msg_show()));
                            VideoApplication.getInstance().setIndexFragmentMenus(info.getMenus());
//                            if(null!=info.getServer()){
//                                VideoApplication.getInstance().setServer(info.getServer());
//                            }
                            ApplicationManager.getInstance().getCacheExample().put("init_info", info, Constant.CACHE_TIME);
                        }
                        if (null != callBackListener) callBackListener.onSuccess(info);
                    } else {
                        if (null != callBackListener) callBackListener.onFailure(data.getCode(), data.getMsg());
                    }
                } else {
                    if (null != callBackListener) callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 获取广告位
     * @param callBackListener
     */
    @Override
    public void getBanners(final UserServerContract.OnNetCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_HOME_BANNER());
        params.put("type_id", "1");
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_HOME_BANNER(), new TypeToken<ResultInfo<ResultList<BannerInfo>>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<BannerInfo>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<BannerInfo>> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            callBackListener.onSuccess(data.getData().getList());
                        }else{
                            callBackListener.onFailure(-1, NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if (null != callBackListener) callBackListener.onFailure(data.getCode(), data.getMsg());
                    }
                }else{
                    if (null != callBackListener) callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 绑定用户到个推
     * @param cid
     * @param callBackListener
     */
    @Override
    public void bindUserToGetui(String cid, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_BIND_CID());
        params.put("clientid",cid);

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_BIND_CID(), new TypeToken<ResultInfo<HostInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<HostInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<HostInfo> data) {
                if (null != data) {
                    if (NetContants.API_RESULT_CODE == data.getCode()) {
                        if (null != callBackListener) callBackListener.onSuccess(data.getData());
                    } else {
                        if (null != callBackListener) callBackListener.onFailure(data.getCode(), data.getMsg());
                    }
                } else {
                    if (null != callBackListener) callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 进入客服会话
     * @param identify
     * @param callBackListener
     */
    @Override
    public void enterConversation(String identify, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_GREET_ENTER());
        params.put("account_id", identify);
        params.put("receiver",UserManager.getInstance().getUserId());

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_GREET_ENTER(),
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != data) {
                    if (NetContants.API_RESULT_CODE == data.getCode()) {
                        if(null!=callBackListener) callBackListener.onSuccess(data.getData());
                    } else {
                        if (null != callBackListener) callBackListener.onFailure(data.getCode(), data.getMsg());
                    }
                } else {
                    if (null != callBackListener) callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 游客注册
     * @param callBackListener
     */
    @Override
    public void visitorRegister(final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_GUEST_LOGIN());
        params.put("equipment", GoagalInfo.get().getUUID(AppEngine.getApplication().getApplicationContext()));
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_GUEST_LOGIN(), new TypeToken<ResultInfo<UserInfo>>() {}.getType(), params,null, isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<UserInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<UserInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        //新用户
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if (null != data.getData() && !TextUtils.isEmpty(data.getData().getUserid())) {
//                                String clToken = data.getResponse().header("cltoken");
//                                String clTokenExp = data.getResponse().header("cltokenexp");
                                UserManager.getInstance().saveUserToken(data.getData().getUserid(), data.getResponse());
                                UserManager.getInstance().setLoginUserInfo(data.getData());
                                callBackListener.onSuccess(data);
                            } else {
                                callBackListener.onFailure(-1, NetContants.NET_REQUST_JSON_ERROR);
                            }
                        //用户已存在
                        } else if (NetContants.API_RESULT_USER_EXIST == data.getCode()) {
                            if (null != data.getData() && !TextUtils.isEmpty(data.getData().getUserid())) {
                                UserManager.getInstance().saveUserToken(data.getData().getUserid(), data.getResponse());
                                UserManager.getInstance().setLoginUserInfo(data.getData());
                                callBackListener.onSuccess(data);
                            } else {
                                callBackListener.onFailure(-1, NetContants.NET_REQUST_JSON_ERROR);
                            }
                        } else {
                            String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
                            String content="用户游客身份注册失败。"+"errorCode:"+data.getCode()+",errorMsg:"+data.getMsg()+",appSign:"+appSign;
                            LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_USER,content,appSign);
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 设备激活
     * @param params
     * @param callBackListener
     */
    @Override
    public void activationDevice(Map<String, String> params, final UserServerContract.OnNetCallBackListener callBackListener) {
        Map<String, String> prames = getDefaultPrames(NetContants.getInstance().URL_ACTIVATION());
        prames.putAll(params);
        Subscription subscription = HttpCoreEngin.get(AppEngine.getApplication()).rxpost(NetContants.getInstance().URL_ACTIVATION(),
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if(null!=data&&NetContants.API_RESULT_CODE==data.getCode()){
                    SharedPreferencesUtil.getInstance().putInt(Constant.SP_FIRST_ACTIVATION, 1);
                    if(null!=callBackListener) callBackListener.onSuccess(data.getMsg());
                }else{
                    if(null!=data){
                        if(null!=callBackListener) callBackListener.onFailure(data.getCode(),data.getMsg());
                    }else{
                        if(null!=callBackListener) callBackListener.onFailure(-1,"激活失败");
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 绑定第三方平台账号
     * @param platformId 平台账号类型  1：QQ 2：微信 4：手机
     * @param openid
     * @param access_token
     * @param phone
     * @param code
     * @param callBackListener
     */
    @Override
    public void onBindPlatformAccount(final int platformId, String openid, String access_token, String phone, String code, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_OTHER_PLATFORM_BIND());
        params.put("openid", openid);
        params.put("app_id", Constant.LOGIN_QQ_KEY);
        params.put("access_token", access_token);
        params.put("phone", phone);
        params.put("code", code);
        params.put("zone", "86");
        params.put("account_type", String.valueOf(platformId));

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_OTHER_PLATFORM_BIND(), new TypeToken<ResultInfo<UserInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<UserInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<UserInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        //新用户
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(platformId);
                            //用户已存在
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 解绑第三方平台账号
     * @param platformId 平台账号类型  1：QQ 2：微信 4：手机
     * @param callBackListener
     */
    @Override
    public void onUnBindPlatformAccount(int platformId, final UserServerContract.OnNetCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_OTHER_PLATFORM_UNBIND());
        params.put("platform_id", String.valueOf(platformId));
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_OTHER_PLATFORM_UNBIND(), new TypeToken<ResultInfo<UserInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<UserInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<UserInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data);
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 查询绑定状态
     * @param callBackListener
     */
    @Override
    public void queryBindState(final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_PLATFORM_ACCOUNT_QUERY());

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_PLATFORM_ACCOUNT_QUERY(), new TypeToken<ResultInfo<ResultList<PlatfromAccountInfo>>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<PlatfromAccountInfo>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<PlatfromAccountInfo>> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if(null!=data&&null!=data.getData()&&null!=data.getData().getList()){
                                callBackListener.onSuccess(data.getData().getList());
                            }else{
                                callBackListener.onSuccess(null);
                            }
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 购买多媒体
     * @param privateMedia
     * @param homeUserID
     * @param buyChanner
     * @param callBackListener
     */
    @Override
    public void buyMedia(final PrivateMedia privateMedia, String homeUserID, String buyChanner,final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FILE_BUY());
        params.put("buy_source",buyChanner);
        params.put("id",String.valueOf(privateMedia.getId()));

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FILE_BUY(), new TypeToken<ResultInfo<BuyMediaInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<BuyMediaInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<BuyMediaInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if(null!=data.getData()&&!TextUtils.isEmpty(data.getData().getFile_path())){
                                // TODO: 2018/9/17 覆盖原有URL
                                privateMedia.setFile_path(data.getData().getFile_path());
                                VideoApplication.getInstance().setMineRefresh(true);
                                callBackListener.onSuccess(privateMedia);
                            }else{
                                callBackListener.onFailure(data.getCode(), data.getMsg());
                            }
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 浏览视频、图片等多媒体文件
     * @param fileID
     * @param homeUserID
     * @param buyChanner
     * @param is_buy
     * @param callBackListener
     */
    @Override
    public void browseMediaFile(final long fileID, String homeUserID, String buyChanner, int is_buy,final UserServerContract.OnCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FILE_BROWSE());
        params.put("source",buyChanner);
        params.put("file_id",String.valueOf(fileID));
        params.put("is_buy",String.valueOf(is_buy));

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FILE_BROWSE(), new TypeToken<ResultInfo<MediaFileInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<MediaFileInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<MediaFileInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            VideoApplication.getInstance().setMineRefresh(true);
                            if(null!=data.getData()&&!TextUtils.isEmpty(data.getData().getFile_path())){
                                // TODO: 2018/11/21 模拟数据
//                                List<FansInfo> fansInfos=new ArrayList<>();
//                                for (int i = 0; i < 10; i++) {
//                                    FansInfo fansInfo=new FansInfo();
//                                    fansInfo.setVip(12);
//                                    fansInfo.setSex(1);
//                                    fansInfo.setAvatar("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1763044357,684998663&fm=27&gp=0.jpg");
//                                    fansInfos.add(fansInfo);
//                                }
//                                data.getData().setGift_rank(fansInfos);
//                                List<MediaGiftInfo> mediaGiftInfos=new ArrayList<>();
//                                for (int i = 0; i < 10; i++) {
//                                    MediaGiftInfo mediaGiftInfo=new MediaGiftInfo();
//                                    mediaGiftInfo.setGift_count("123");
//                                    mediaGiftInfo.setGift_id("4522132");
//                                    mediaGiftInfo.setAvatar(UserManager.getInstance().getAvatar());
//                                    mediaGiftInfo.setNikcname(UserManager.getInstance().getNickname());
//                                    mediaGiftInfo.setAvatar("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1763044357,684998663&fm=27&gp=0.jpg");
//                                    mediaGiftInfo.setAccept_nikcname("刘备");
//                                    mediaGiftInfo.setAccept_userid("55467697");
//                                    mediaGiftInfos.add(mediaGiftInfo);
//                                }
//                                data.getData().setGift_info(mediaGiftInfos);
                                callBackListener.onSuccess(data.getCode(),data.getData(),data.getMsg());
                            }else{
                                callBackListener.onFailure(data.getCode(), data.getMsg());
                            }
                        }else{
                            if(null!=data.getData()){
                                callBackListener.onSuccess(data.getCode(),data.getData(),data.getMsg());
                            }else{
                                callBackListener.onFailure(data.getCode(), data.getMsg());
                            }
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 上传文件鉴权
     * @param uploadObjectInfo 鉴权对象
     * @param callBackListene
     */
    @Override
    public void uploadFileAuthentication(final UploadObjectInfo uploadObjectInfo, final UserServerContract.OnNetCallBackListener callBackListene) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FILE_UPLOAD_AUTHENTICATION());
        params.put("fileMd5",null==uploadObjectInfo?"":uploadObjectInfo.getFileMd5());
        params.put("userid",UserManager.getInstance().getUserId());
        params.put("file_type",String.valueOf(uploadObjectInfo.getFileSourceType()));

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FILE_UPLOAD_AUTHENTICATION(), new TypeToken<ResultInfo<UploadAuthenticationInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<UploadAuthenticationInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListene) callBackListene.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<UploadAuthenticationInfo> data) {
                if (null != callBackListene) {
                    if (null != data) {
                        //文件不存在，可以继续上传
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            data.getData().setUploadInfo(uploadObjectInfo);
                            callBackListene.onSuccess(data.getData());
                        }else{
                            callBackListene.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListene.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 发送私信
     * @param identify 接收人用户ID
     * @param content 发送内容
     * @param annexType 消息类型
     * @param conversationType 会话类型
     * @param callBackListener
     */
    @Override
    public void sendMsg(String identify, final String content, int annexType, int conversationType, final UserServerContract.OnSendMessageCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_SEND_PRIVATE_MSG());
        params.put("receive_userid",identify);
        params.put("userid",UserManager.getInstance().getUserId());
        params.put("content",content);
        params.put("annex_type",String.valueOf(annexType));
        params.put("type",String.valueOf(conversationType));

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_SEND_PRIVATE_MSG(),
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,null,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            VideoApplication.getInstance().setMineRefresh(true);
                            callBackListener.onSuccess(data.getData());
                        }else{
                            if(NetContants.API_RESULT_ARREARAGE_CODE==data.getCode()){
                                //如果是发送消息因为会员的关系，还原消息到输入框
                                callBackListener.onFailure(data.getCode(), content,data.getMsg());
                            }else{
                                callBackListener.onFailure(data.getCode(), content,data.getMsg());
                            }
                        }
                    } else {
                        callBackListener.onFailure(-1, null,NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    private boolean isSetExcumeMode;
    public boolean isSetExcumeMode() {
        return isSetExcumeMode;
    }

    /**
     * 设置勿扰模式
     * @param quite 0：勿扰关闭 1：勿扰开启
     * @param callBackListener
     */
    @Override
    public void setExcuseMode(String userID, int quite, final UserServerContract.OnNetCallBackListener callBackListener) {
        if(isSetExcumeMode) return;
        isSetExcumeMode=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_QUITE());
        params.put("quite",String.valueOf(quite));
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_QUITE(),
                new TypeToken<ResultInfo<PersonCenterInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<PersonCenterInfo>>() {
            @Override
            public void onCompleted() {
            }


            @Override
            public void onError(Throwable e) {
                isSetExcumeMode=false;
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<PersonCenterInfo> data) {
                isSetExcumeMode=false;
                if (null != data) {
                    if (NetContants.API_RESULT_CODE == data.getCode()) {
                        if(null!=data.getData()){
                            UserManager.getInstance().setQuite(data.getData().getQuite());
                            VideoApplication.getInstance().setMineRefresh(true);
                            if(null != callBackListener) callBackListener.onSuccess(quite);
                        }else{
                            if(null != callBackListener) callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    }else{
                        if(null != callBackListener) callBackListener.onFailure(data.getCode(), data.getMsg());
                    }
                } else {
                    if(null != callBackListener) callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 修改用户信息
     * @param paramsKey
     * @param keyContent
     * @param callBackListener
     */
    @Override
    public void modityUserData(String paramsKey, final String keyContent, final UserServerContract.OnNetCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_UPLOAD_USER_INFO());
        params.put(paramsKey,keyContent);
        params.put("isResult","1");//告诉服务器不需要返回用户信息
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_UPLOAD_USER_INFO(),
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(keyContent);
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 获取标签库
     * @param type 1：主播标签库 2：用户评价标签库
     * @param callBackListener
     */
    @Override
    public void getTags(int type, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_TAGS());
        params.put("type",String.valueOf(type));

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_TAGS(),
                new TypeToken<ResultInfo<ResultList<TagInfo>>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<TagInfo>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<TagInfo>> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if(null!=data.getData()&&null!=data.getData().getList()){
                                callBackListener.onSuccess(data.getData().getList());
                            }else{
                                callBackListener.onFailure(data.getCode(), "标签为空");
                            }
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 上报日志信息
     * @param actionType  场景类型 10001 ：支付  10002 ：视频通话  10007 : 网络请求失败日志
     * @param actionLogInfo 日志信息
     * @param callBackListener
     */
    @Override
    public void postActionState(int actionType, ActionLogInfo actionLogInfo, final UserServerContract.OnNetCallBackListener callBackListener) {
        Map<String,String> params=new HashMap<>();
        params.put("actionType",String.valueOf(actionType));
        params.put("userid",UserManager.getInstance().getUserId());
        actionLogInfo.setVersion(Utils.getVersion());
        actionLogInfo.setImeil(VideoApplication.mUuid);
        actionLogInfo.setAgent_id(ChannelUtls.getInstance().getAgentId());
        actionLogInfo.setBrand(Build.BRAND);
        actionLogInfo.setModel(Build.MODEL);
        actionLogInfo.setActionType(actionType);
        if(null==mConnectionManager) mConnectionManager= (ConnectivityManager) VideoApplication.getInstance().getApplicationContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectionManager.getActiveNetworkInfo();
        if(null!=networkInfo){
            actionLogInfo.setNetworkType(networkInfo.getType());
        }
        String json = new Gson().toJson(actionLogInfo, new TypeToken<ActionLogInfo>() {}.getType());
        try {
            params.put("params", URLEncoder.encode(json,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpCoreEngin.get(VideoApplication.getInstance().getApplicationContext()).rxget(
                NetContants.getInstance().URL_LOG_ACTION(), new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params, false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultInfo<JSONObject>>() {
            @Override
            public void call(ResultInfo<JSONObject> result) {
                if(null!=callBackListener){
                    if(null!=result){
                        if(NetContants.API_RESULT_CODE==result.getCode()){
                            callBackListener.onSuccess(null);
                        }else{
                            callBackListener.onFailure(result.getCode(),result.getMsg());
                        }
                    }else{
                        callBackListener.onFailure(-1,"上报失败");
                    }
                }
            }
        });
    }

    /**
     * 上报路由日志
     * @param actionType  场景类型 10001 ：支付  10002 ：视频通话  10007 : 网络请求失败日志
     * @param actionLogInfo 日志信息
     * @param callBackListener
     */
    @Override
    public void postHostState(int actionType, ActionLogInfo actionLogInfo, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String,String> params=new HashMap<>();
        params.put("actionType",String.valueOf(actionType));
        params.put("userid",UserManager.getInstance().getUserId());
        actionLogInfo.setVersion(Utils.getVersion());
        actionLogInfo.setImeil(VideoApplication.mUuid);
        actionLogInfo.setAgent_id(ChannelUtls.getInstance().getAgentId());
        actionLogInfo.setBrand(Build.BRAND);
        actionLogInfo.setModel(Build.MODEL);
        actionLogInfo.setActionType(actionType);
        if(null==mConnectionManager) mConnectionManager= (ConnectivityManager) VideoApplication.getInstance().getApplicationContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectionManager.getActiveNetworkInfo();
        if(null!=networkInfo){
            actionLogInfo.setNetworkType(networkInfo.getType());
        }
        String json = new Gson().toJson(actionLogInfo, new TypeToken<ActionLogInfo>() {}.getType());
        try {
            params.put("params", URLEncoder.encode(json,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpCoreEngin.get(VideoApplication.getInstance().getApplicationContext()).rxget(
                NetContants.getInstance().URL_LOG_UPLOAD(), new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params, false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultInfo<JSONObject>>() {
                    @Override
                    public void call(ResultInfo<JSONObject> result) {
                        if(null!=callBackListener){
                            if(null!=result){
                                if(NetContants.API_RESULT_CODE==result.getCode()){
                                    callBackListener.onSuccess(null);
                                }else{
                                    callBackListener.onFailure(result.getCode(),result.getMsg());
                                }
                            }else{
                                callBackListener.onFailure(-1,"上报失败");
                            }
                        }
                    }
                });
    }

    /**
     * 创建房间
     * @param callBackListener
     */
    @Override
    public void createRoom(final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_GET_ROOMID());
//        params.put("api_version", "20181107");

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_GET_ROOMID(), new TypeToken<ResultInfo<CreateRoomInfo>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<CreateRoomInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<CreateRoomInfo> data) {
                if(null!=callBackListener){
                    if(null!=data){
                        if(data.getCode()==NetContants.API_RESULT_CODE){
                            if(null!=data.getData()){
                                callBackListener.onSuccess(data.getData());
                            }else{
                                callBackListener.onFailure(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                            }
                        }else{
                            callBackListener.onFailure(data.getCode(),data.getMsg());
                        }
                    }else{
                        callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取房间任务
     * @param callBackListener
     */
    @Override
    public void getRoomPopupTasks(final UserServerContract.OnNetCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_GET_ROOM_TASK());
        params.put("userid",UserManager.getInstance().getUserId());
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_GET_ROOM_TASK(), new TypeToken<ResultInfo<ResultList<BannerInfo>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<BannerInfo>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<BannerInfo>> data) {
                if(null!=callBackListener){
                    if(null!=data){
                        if(data.getCode()==NetContants.API_RESULT_CODE){
                            if(null!=data.getData()){
                                try {
                                    callBackListener.onSuccess(data.getData().getList());
                                }catch (RuntimeException e){
                                    callBackListener.onSuccess(null);
                                }
                            }else{
                                callBackListener.onFailure(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                            }
                        }else{
                            callBackListener.onFailure(data.getCode(),data.getMsg());
                        }
                    }else{
                        callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 上报直播间心跳
     * @param roomID
     * @param roomScen
     * @param identity
     * @param callBackListener
     */
    @Override
    public void postRoomHeartState(String roomID, String roomScen, int identity, UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_HEARTBEAT());
        params.put("userid",UserManager.getInstance().getUserId());
        params.put("room_id",roomID);
        params.put("scene",roomScen);//应用场景
        params.put("identity",String.valueOf(identity));//用户身份

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_HEARTBEAT()+"?userid="+UserManager.getInstance().getUserId(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResultInfo<JSONObject> data) {
                    }
                });
        addSubscrebe(subscribe);
    }

    /**
     * 推送的视频邀请，用户挂断了主播的通话邀请
     * @param anchorId
     * @param userId
     * @param state
     * @param callBackListener
     */
    @Override
    public void sendHangupReceiptMsg(String anchorId, String userId, int state, final UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_UP_INVITE_VIDEO_CHAT_STATE());
        params.put("userid",userId);
        params.put("anchorid",anchorId);
        params.put("state",String.valueOf(state));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_UP_INVITE_VIDEO_CHAT_STATE(), new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if(null!=callBackListener){
                    if(null!=data){
                        if(data.getCode()==NetContants.API_RESULT_CODE){
                            callBackListener.onSuccess(data.getData());
                        }else{
                            callBackListener.onFailure(data.getCode(),data.getMsg());
                        }
                    }else{
                        callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 根据场景获取礼物分类列表
     * @param sourceApiType 0:通用 1：一对一视频通话 2：1对多大厅直播间
     * @param callBackListener
     */
    @Override
    public void getGiftTypeList(final int sourceApiType, final UserServerContract.OnNetCallBackListener callBackListener) {
        
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_GIFT_TYPE());
        params.put("scene_api_type",String.valueOf(sourceApiType));

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_GIFT_TYPE() , new TypeToken<ResultInfo<ResultList<GiftTypeInfo>>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).subscribe(new Subscriber<ResultInfo<ResultList<GiftTypeInfo>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<GiftTypeInfo>> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()){
                            ApplicationManager.getInstance().putGiftTypeList(sourceApiType,data.getData().getList());
                            if(null!=callBackListener) callBackListener.onSuccess(data.getData().getList());
                        }else{
                            ApplicationManager.getInstance().putGiftTypeList(sourceApiType,null);
                            if(null!=callBackListener) callBackListener.onFailure(data.getCode(),data.getMsg());
                        }
                    }else{
                        if(null!=callBackListener) callBackListener.onFailure(data.getCode(),data.getMsg());
                    }
                }else{
                    if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 派发验证码
     * @param country
     * @param phone
     * @param callBackListener
     */
    @Override
    public void getVerificationCode(String country, String phone, UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_VERIFICATION_CODE());
        params.put("country",country);
        params.put("phone",phone);

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_VERIFICATION_CODE() , new TypeToken<ResultInfo<VerificationInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).subscribe(new Subscriber<ResultInfo<VerificationInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<VerificationInfo> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=data.getData()&&null!=data.getData()){
                            if(null!=callBackListener) callBackListener.onSuccess(data.getData());
                        }else{
                            if(null!=callBackListener) callBackListener.onFailure(data.getCode(),data.getMsg());
                        }
                    }else{
                        if(null!=callBackListener) callBackListener.onFailure(data.getCode(),data.getMsg());
                    }
                }else{
                    if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    @Override
    public void queryActivitys(int typeID, String order_sn,UserServerContract.OnNetCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ACTIVITY_LIST());
        params.put("userid",UserManager.getInstance().getUserId());
        params.put("activity_id",String.valueOf(typeID));
        params.put("order_sn",order_sn);

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ACTIVITY_LIST() , new TypeToken<ResultInfo<VipPopupData>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).subscribe(new Subscriber<ResultInfo<VipPopupData>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<VipPopupData> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=data.getData()&&null!=data.getData()){
                            if(null!=callBackListener) callBackListener.onSuccess(data.getData());
                        }else{
                            if(null!=callBackListener) callBackListener.onFailure(data.getCode(),data.getMsg());
                        }
                    }else{
                        if(null!=callBackListener) callBackListener.onFailure(data.getCode(),data.getMsg());
                    }
                }else{
                    if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    private boolean isCheckedIng=false;

    public boolean isCheckedIng() {
        return isCheckedIng;
    }

    /**
     * 校验文件上传权限
     * @param fileType
     * @param callBackListener
     */
    @Override
    public void checkedUploadFilePermission(String fileType, UserServerContract.OnNetCallBackListener callBackListener) {
        if(isCheckedIng){
            return;
        }
        isCheckedIng=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_CHECKED_UPLOAD());
        params.put("userid",UserManager.getInstance().getUserId());
        params.put("file_type",fileType);

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_CHECKED_UPLOAD() , new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isCheckedIng=false;
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                isCheckedIng=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=callBackListener) callBackListener.onSuccess(data.getMsg());
                    }else{
                        if(null!=callBackListener) callBackListener.onFailure(data.getCode(),data.getMsg());
                    }
                }else{
                    if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }
}
