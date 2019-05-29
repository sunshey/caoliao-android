package com.yc.liaolive.user.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.kaikai.securityhttp.utils.LogUtil;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMManager;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.LoginConfig;
import com.yc.liaolive.bean.NoticeContent;
import com.yc.liaolive.bean.NoticeMessage;
import com.yc.liaolive.bean.PersonCenterInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.ServerBean;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.bean.UploadObjectInfo;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.recharge.model.bean.VipRechargePoppupBean;
import com.yc.liaolive.ui.activity.ContentFragmentActivity;
import com.yc.liaolive.ui.presenter.UserServerPresenter;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

/**
 * TinyHung@outlook.com
 * 2018/5/25
 * 用户管理模块
 */

public class UserManager implements UserServerContract.View {

    public static final String TAG = UserManager.class.getSimpleName();
    private static UserManager mInstance;
    private String mUserId = "";
    private String mLoginToken;
    private String mTokenExp;
    private String mSdkAppID = "";
    private String mUserSig = "";
    private String mAccountType;
    private String mNickName;
    private String mUserAvatar;
    private String mFrontCover;
    private int userGradle = 0;
    private int userVip = 0;
    private String phone;//手机号码
    private double longitude = 116.23;//经度 默认北京
    private double latitude = 39.54;//纬度
    private String province = "北京";
    private UserServerPresenter mPresenter;
    private long diamonds;//钻石总数
    private long vipEndtime;//会员结束时间
    private int uploadImageCount;//相册最大上传数量
    private int uploadVideoCount;//视频最大上传数量
    private int identity_audit;//0：未参加或通过审核 1：审核中 2：审核通过
    private int level_integral;//用户等级
    private int chat_deplete;
    private String signature;
    private String position = "武汉";
    private int sex = 0;//0:male,1:female,-1:unknown
    private String height;//身高
    private String weight;//体重
    private String speciality;//个人介绍
    private String star;//星座
    private String label;//标签  以.分割  存id 索引
    private long points;//积分
    private int quite;//来电勿扰 0：未开启 1：已开启  2.1.06版本：0：离线  1：在线
    private int homeIndex=0;//后台配置的打开APP主页默认显示第几页
    private int userType;
    private int is_white;
    //用户是否购买了话费充值vip 0：未购买 1：已购买 只用于开通VIP页面显示0显示 1不显示
    private int vip_phone;
    private VipRechargePoppupBean popup_page; //用户领取话费弹窗
    private NoticeMessage mNoticeMessage;//直播间、私信、视频通话的公告信息
    private List<List<List<Integer>>> mGiftCountMeals;//礼物的档次
    private String verificationZhima="0";//芝麻认证是否开启 0：不需要认证 1：需要认证
    private String verificationPhone="0";//绑定手机号是否开启 0：不需要绑定 1：需要绑定
    private String searchBut="0";//搜索是否可用 0：不可用 1：可用
    private String pushBut="0";//上传ASMR是否可用 0：不可用 1：可用
    private String priMsgShow="0";//消息是否可用 0：不可用 1：可用
    private int mIsZhima;
    //照片控制器
    private List<String> mImageController;
    //视频控制器
    private List<String> mVideoController;
    private List<String> mChatController;
    private List<String> mAsmrController;
    private static ServerBean mServer;

    /**
     * 单例初始化
     *
     * @return
     */
    public static synchronized UserManager getInstance() {
        synchronized (UserManager.class) {
            if (null == mInstance) {
                mInstance = new UserManager();
            }
        }
        return mInstance;
    }

    /**
     * 构造
     */
    private UserManager() {
        try {
            loadUserInfo();
            mPresenter = new UserServerPresenter();
            mPresenter.attachView(this);
        }catch (RuntimeException e){

        }
    }

    /**
     * 更新用户信息
     *
     * @param data
     */
    public void setLoginUserInfo(FansInfo data) {
        setUserId(data.getUserid());//这里传入后台分发的用户名
        setNickName(data.getNickname());
        setSex(data.getSex());
        setAvatar(data.getAvatar());
        setPhone(data.getPhone());
        setUserVip(data.getVip());
        setUserGradle(data.getLevel_integral());
        setSignature(data.getSignature());
    }

    public void setLoginUserInfo(UserInfo data) {
        setUserId(data.getUserid());//这里传入后台分发的用户名
        setNickName(data.getNickname());
        setSex(data.getSex());
        setAvatar(data.getAvatar());
        setPhone(data.getPhone());
        setUserVip(data.getVip());
        setFrontCover(data.getFrontcover());
        setUserGradle(data.getLevel_integral());
        setChat_deplete(data.getChat_deplete());
        setSignature(data.getSignature());
        setIsZhima(data.getIs_zhima());
        if (!TextUtils.isEmpty(data.getPosition())) setPosition(data.getPosition());
        if (!TextUtils.isEmpty(data.getProvince())) setProvince(data.getProvince());
        setIdentity_audit(data.getIdentity_audit());
        setVip_phone(data.getVip_phone());
        setPopup_page(data.getPopup_page());
    }

    /**
     * 更新用户信息
     * @param data
     */
    public void setLoginUserInfo(PersonCenterInfo data) {
        setUserId(data.getUserid());//这里传入后台分发的用户名
        setNickName(data.getNickname());
        setSex(data.getSex());
        setAvatar(data.getAvatar());
        setUserVip(data.getVip());
        setPhone(data.getPhone());
        setLevel_integral(data.getLevel_integral());
        setUserGradle(data.getLevel_integral());
        setChat_deplete(data.getChat_deplete());
        setSignature(data.getSignature());
        setLabel(data.getLabel());
        setHeight(data.getHeight());
        setWeight(data.getWeight());
        setStar(data.getStar());
        setQuite(data.getQuite());
        setPoints(data.getPoints());
        setSpeciality(data.getSpeciality());//个性签名
        setIdentity_audit(data.getIdentity_audit());
        setFrontCover(data.getFrontcover());
        setUserType(data.getUser_type());
        setIs_white(data.getIs_white());
        setIsZhima(data.getIs_zhima());
        if (!TextUtils.isEmpty(data.getPosition())) setPosition(data.getPosition());
    }

    public void setUserId(String userid) {
        this.mUserId = userid;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserGradle(int userGradle) {
        this.userGradle = userGradle;
    }

    public int getUserGradle() {
        return userGradle;
    }

    public void setUserVip(int userVip) {
        this.userVip = userVip;
    }

    public int getUserVip() {
        return userVip;
    }

    public String getSignature() {
        return signature;
    }

    public void setLocationLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLocationLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setIdentity_audit(int identity_audit) {
        this.identity_audit = identity_audit;
    }

    public int getIdentity_audit() {
        return identity_audit;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getNickname() {
        return TextUtils.isEmpty(mNickName) ? mUserId : mNickName;
    }

    public String getUserSig() {
        return mUserSig;
    }

    public long getDiamonds() {
        return diamonds;
    }

    public void setDiamonds(long diamonds) {
        this.diamonds = diamonds;
    }

    public void setVipEndtime(long vipEndtime) {
        this.vipEndtime = vipEndtime;
    }

    public long getVipEndtime() {
        return vipEndtime;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public String getAvatar() {
        return mUserAvatar;
    }

    public void setAvatar(String avatar) {
        mUserAvatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public String getFrontCover() {
        return mFrontCover;
    }

    public void setFrontCover(String pic) {
        mFrontCover = pic;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public int getQuite() {
        return quite;
    }

    public void setQuite(int quite) {
        this.quite = quite;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSDKAppID() {
        return mSdkAppID;
    }

    public String getAccountType() {
        return mAccountType;
    }

    public int getChat_deplete() {
        return chat_deplete;
    }

    public void setChat_deplete(int chat_deplete) {
        this.chat_deplete = chat_deplete;
    }

    public void setHomeIndex(int homeIndex) {
        this.homeIndex=homeIndex;
    }

    public int getHomeIndex() {
        return homeIndex;
    }

    public void setUploadImageCount(int uploadImageCount) {
        this.uploadImageCount=uploadImageCount;
    }

    public int getUploadImageCount() {
        if(uploadImageCount<=0) return 8;
        return uploadImageCount;
    }

    public int getUploadVideoCount() {
        if(uploadVideoCount<=0) return 8;
        return uploadVideoCount;
    }


    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getLevel_integral() {
        return level_integral;
    }

    public void setLevel_integral(int level_integral) {
        this.level_integral = level_integral;
    }

    public String getmLoginToken() {
        return mLoginToken;
    }

    public int getVip_phone() {
        return vip_phone;
    }

    public void setVip_phone(int vip_phone) {
        this.vip_phone = vip_phone;
    }

    public VipRechargePoppupBean getPopup_page() {
        return popup_page;
    }

    public void setPopup_page(VipRechargePoppupBean popup_page) {
        this.popup_page = popup_page;
    }

    public int getIs_white() {
        return is_white;
    }

    public void setIs_white(int is_white) {
        this.is_white = is_white;
    }

    public void setIsZhima(int isZhima) {
        mIsZhima = isZhima;
    }

    public int getIsZhima() {
        return mIsZhima;
    }

    public void setImageController(List<String> imageController) {
        mImageController = imageController;
    }

    public void setVideoController(List<String> videoController) {
        mVideoController = videoController;
    }

    public List<String> getImageController() {
        return mImageController;
    }

    public List<String> getVideoController() {
        return mVideoController;
    }

    public void setChatController(List<String> chatController) {
        mChatController = chatController;
    }

    public List<String> getChatController() {
        return mChatController;
    }

    public List<String> getAsmrController() {
        return mAsmrController;
    }

    public void setAsmrController(List<String> asmrController) {
        mAsmrController = asmrController;
    }

    /**
     * 更新客服资料
     * @param server
     */
    public void setServer(ServerBean server) {
        mServer = server;
    }

    public ServerBean getServer() {
        return mServer;
    }

    /**
     * 返回客服ID
     * @return
     */
    public String getServerIdentify(){
        if(null!=mServer){
            return mServer.getServer_identify();
        }
//        return "1000030";
        return "";
    }

    public enum NoticeType{
        Live,
        Chat,
        PrivateLive
    }

    /**
     * 返回各个场景下的公告信息
     * @param type
     * @return
     */
    public NoticeContent getNoticeMessage(NoticeType type) {
        switch (type) {
            case Live:
                if(null!=mNoticeMessage&&null!=mNoticeMessage.getRoom()){
                    return mNoticeMessage.getRoom();
                }
                return new NoticeContent(Constant.MSG_NOTICE_ROOM,"#FF7575");
            case Chat:
                if(null!=mNoticeMessage&&null!=mNoticeMessage.getMessage()){
                    return mNoticeMessage.getMessage();
                }
                return new NoticeContent(Constant.MSG_NOTICE_CHAT,"#000000");
            case PrivateLive:
                if(null!=mNoticeMessage&&null!=mNoticeMessage.getVideo_chat()){
                    return mNoticeMessage.getVideo_chat();
                }
                return new NoticeContent(Constant.MSG_NOTICE_PRIVATE_ROOM,"#FF7575");

        }
        return new NoticeContent(Constant.MSG_NOTICE_ROOM,"#");
    }

    public void setNoticeMessage(NoticeMessage mNoticeMessage) {
        this.mNoticeMessage = mNoticeMessage;
    }

    public List<List<List<Integer>>> getGiftCountMeals() {
        return mGiftCountMeals;
    }

    public void setGiftCountMeals(List<List<List<Integer>>> mGiftCountMeals) {
        this.mGiftCountMeals = mGiftCountMeals;
    }

    /**
     * 是否强制绑定手机号
     * @return
     */
    public boolean isVerificationPhone() {
        if(TextUtils.isEmpty(verificationPhone)) return false;
        return verificationPhone.equals("1");
    }

    /**
     * 是否强制实名认证
     * @return
     */
    public boolean isVerificationZhima() {
        if(TextUtils.isEmpty(verificationZhima)) return false;
        return verificationZhima.equals("1");
    }

    /**
     * 搜索是否可用
     * @return
     */
    public boolean isSearchAvailable() {
        if(TextUtils.isEmpty(searchBut)) return false;
        return searchBut.equals("1");
    }

    /**
     * 消息是否可用
     * @return
     */
    public boolean isChatAvailable() {
        if(TextUtils.isEmpty(priMsgShow)) return false;
        return priMsgShow.equals("1");
    }

    /**
     * 上传ASMR是否可用
     * @return
     */
    public boolean isPostAsmrAvailable() {
        if(TextUtils.isEmpty(pushBut)) return false;
        return pushBut.equals("1");
    }


    public void setPostAsmrBut(String postAsmrBut) {
        this.pushBut = postAsmrBut;
    }



    public void setChatAvailable(String chatAvailable) {
        this.priMsgShow = chatAvailable;
    }

    public void setSearchBut(String searchBut) {
        this.searchBut = searchBut;
    }

    /**
     * 是否存在登录记录
     * @return
     */
    public boolean isUserLogin() {
        return !TextUtils.isEmpty(mUserId) && !TextUtils.isEmpty(mLoginToken);
    }

    /**
     * 是否已经登录至自有服务器
     *
     * @return
     */
    public boolean isLogin() {
        return !TextUtils.isEmpty(mUserId) && !TextUtils.isEmpty(mUserSig);
    }

    /**
     * 同步用户信息至腾讯IM
     */
    public void syncUserInfoToIM(){
        if(!TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())&&!TextUtils.isEmpty(mNickName)){
            if(!TextUtils.isEmpty(mNickName)) TIMFriendshipManager.getInstance().setNickName(mNickName, null);
            if(!TextUtils.isEmpty(mUserAvatar)) TIMFriendshipManager.getInstance().setFaceUrl(mUserAvatar,null);
            TIMFriendshipManager.getInstance().setGender(0 == sex ? TIMFriendGenderType.Male : TIMFriendGenderType.Female, null);
            TIMFriendshipManager.getInstance().setLevel(userVip, null);
            TIMFriendshipManager.getInstance().setRole(userType,null);
            if(!TextUtils.isEmpty(position)) TIMFriendshipManager.getInstance().setLocation(position,null);
            if(!TextUtils.isEmpty(signature)) TIMFriendshipManager.getInstance().setSelfSignature(signature,null);
        }
    }

    /**
     * 用户是否已经认证
     * @return
     */
    public boolean isAuthenState() {
        if (2 == identity_audit) {
            return true;
        }
        return false;
    }

    /**
     * 用户是否已经认证
     *
     * @return
     */
    public boolean isAuthenState(Context context) {
        //已认证,直接进房间
        if (2 == identity_audit) {
            return true;
        }
        ContentFragmentActivity.start(context, Constant.FRAGMENT_TYPE_TASK_AUTHEN, "实名认证", null);
        return false;
    }

    /**
     * 是否未认证？
     * @return
     */
    public boolean isUncertified() {
        //未认证,直接进房间
        if (0 == identity_audit) {
            return true;
        }
        ToastUtils.showCenterToast(getAuthenticationState());
        return false;
    }

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}

    /**
     * 自动登录
     *
     * @param callBackListener
     */
    public void autoLogin(final UserServerContract.OnNetCallBackListener callBackListener) {
        loginServer(mUserId, callBackListener);
    }


    /**
     * 登录至服务器
     * @param userID
     */
    public void loginServer(String userID, final UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter)
            mPresenter.login(userID, new UserServerContract.OnNetCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    if (null != object && object instanceof LoginConfig) {
                        LoginConfig loginConfig = (LoginConfig) object;
                        UserManager.this.verificationZhima=loginConfig.getVerification_zhima();
                        UserManager.this.verificationPhone=loginConfig.getVerification_phone();
                        if(1==loginConfig.getVip_coin()&&null!=loginConfig.getPopup_page()&&null!=loginConfig.getPopup_page().getList_coin()){
                            AppEngine.getInstance().setVipListCoin(loginConfig.getPopup_page().getList_coin());
                        }
                        try {
                            if (null != loginConfig.getRoomservice_sign()) {
                                mUserId = loginConfig.getRoomservice_sign().getUserID();
                                mUserSig = loginConfig.getRoomservice_sign().getUserSig();
                                mAccountType = loginConfig.getRoomservice_sign().getAccountType();
                                mSdkAppID = loginConfig.getRoomservice_sign().getSdkAppID();
                            }
                            loginConfig.getRoomservice_sign().setUserName(mNickName);
                            loginConfig.getRoomservice_sign().setUserHead(mUserAvatar);
                            if (null != callBackListener) callBackListener.onSuccess(loginConfig);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (null != callBackListener) {
                                callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    if (null != callBackListener) {
                        callBackListener.onFailure(code, errorMsg);
                    }
                }
            });
    }

    /**
     * 手机号码注册
     * @param code             验证码
     * @param phone            手机号码
     * @param zone             区号
     * @param callBackListener 回调
     */
    public void registerByPhoone(String code, String phone, String zone, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.registerByPhoone(code, phone, zone, callBackListener);
    }

    /**
     * 第三方账号注册
     * @param openid
     * @param access_token
     * @param account_type
     * @param callBackListener
     */
    public void registerByOther(String openid, String access_token, String account_type, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.registerByOther(openid, access_token, account_type, callBackListener);
    }

    public boolean isSetExcumeMode(){
        if(null!=mPresenter){
            return mPresenter.isSetExcumeMode();
        }
        return false;
    }
    /**
     * 设置勿扰模式
     * @param userID
     * @param quite 0：解除勿扰状态 1：开启来电勿扰
     * @param callBackListener
     */
    public void setExcuseMode(String userID, int quite,UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.setExcuseMode(userID,quite,callBackListener);
    }

    /**
     * 进入客服会话
     * @param identify
     * @param callBackListener
     */
    public void enterConversation(String identify, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.enterConversation(identify,callBackListener);
    }

    /**
     * 获取基本用户信息
     * @param userID
     * @param toUserID
     * @param callBackListener
     */
    public void getLifeUserData(final String userID, final String toUserID, final UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.getUserFullData(userID, toUserID,0, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if (null != callBackListener) callBackListener.onSuccess(object);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if (null != callBackListener) callBackListener.onFailure(code, errorMsg);
            }
        });
    }

    /**
     * 获取全量用户信息
     * @param userID
     * @param toUserID
     * @param callBackListener
     */
    public void getFullUserData(final String userID, final String toUserID, final UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.getUserFullData(userID, toUserID,1, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if (null != object && object instanceof UserInfo) {
                    UserInfo userInfo = (UserInfo) object;
                    if(userID.equals(toUserID)){
                        UserManager.getInstance().setDiamonds(userInfo.getPintai_coin() + userInfo.getRmb_coin());
                        UserManager.getInstance().setVipEndtime(userInfo.getVip_end_time() * 1000);
                    }
                    if (null != callBackListener) callBackListener.onSuccess(object);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if (null != callBackListener) callBackListener.onFailure(code, errorMsg);
            }
        });
    }

    /**
     * 更新用户信息
     *
     * @param nickName
     * @param avatar
     * @param frontcover
     * @param sex
     * @param callBackListener
     */
    public void uploadUserInfo(String nickName, String avatar, String frontcover, int sex, final UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.uploadUserInfo(nickName, avatar, frontcover, sex, callBackListener);
    }

    /**
     * 获取标签
     * @param type 1：主播标签库 2：用户评价标签库
     * @param callBackListener
     */
    public void getTags(int type,final UserServerContract.OnNetCallBackListener callBackListener){
        if (null != mPresenter) mPresenter.getTags(type,callBackListener);
    }

    /**
     * 关注用户
     * @param toUserID
     * @param status
     * @param callBackListener
     */
    public void followUser(String toUserID, int status, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.followUser(toUserID, status, callBackListener);
    }

    /**
     * 举报用户
     * @param toUserID
     * @param callBackListener
     */
    public void reportUser(String toUserID, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.reportUser(toUserID, callBackListener);
    }

    /**
     * 举报多媒体文件
     * @param toUserID
     * @param id
     * @param type
     * @param callBackListener
     */
    public void reportMediaFile(String toUserID, long id, int type, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.reportMediaFile(toUserID,id,type,callBackListener);
    }

    /**
     * 添加用户至黑名单
     * @param toUserID
     * @param status
     * @param callBackListener
     */
    public void addBlackList(String toUserID, int status, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.addBlackList(toUserID, status, callBackListener);
    }

    /**
     * 获取配置的HOST
     *
     * @param callBackListener
     */
    public void getServerHost(UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.getServerHost(callBackListener);
    }

    /**
     * 将好友从黑名单移除
     *
     * @param toUserID
     * @param callBackListener
     */
    public void removeBlackList(String toUserID, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.removeBlackList(toUserID, callBackListener);
    }

    /**
     * 检查用户是否在黑名单列表中
     *
     * @param blackUserID
     * @param callBackListener
     */
    public void isBlackList(String blackUserID, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.isBlackList(blackUserID, callBackListener);
    }

    /**
     * 绑定手机号
     * @param phone
     * @param code
     * @param zone
     * @param callBackListener
     */
    public void bindPhone(String phone, String code, String zone, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.bindPhone(phone, code, zone, callBackListener);
    }

    /**
     * 获取新手任务奖励
     *
     * @param type
     * @param callBackListener
     */
    public void getTasks(String type, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.getTasks(type, callBackListener);
    }

    /**
     * 获取直播间任务
     * @param callBackListener
     */
    public void getRoomTasks(UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.getRoomTasks(callBackListener);
    }

    /**
     * 上报直播间心跳
     * @param roomID
     * @param roomScen
     * @param identity
     * @param callBackListener
     */
    public void postRoomHeartState(String roomID, String roomScen, int identity, UserServerContract.OnNetCallBackListener callBackListener){
        if (null != mPresenter) mPresenter.postRoomHeartState(roomID,roomScen,identity,callBackListener);
    }

    /**
     * 领取任务奖励
     *
     * @param task_id
     * @param callBackListener
     */
    public void drawTaskAward(int task_id, final UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.drawTaskAward(task_id, callBackListener);
    }

    /**
     * 领取任务奖励
     *
     * @param taskInfo
     * @param callBackListener
     */
    public void drawTaskAward(TaskInfo taskInfo, final UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.drawTaskAward(taskInfo, callBackListener);
    }

    /**
     * 分享上报
     *
     * @param chare_userid
     * @param platform
     * @param callBackListener
     */
    public void shareStatistics(String chare_userid, int platform, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.shareStatistics(chare_userid, platform, callBackListener);
    }

    /**
     * 设备激活
     * @param params
     * @param callBackListener
     */
    public void activationDevice(Map<String, String> params,UserServerContract.OnNetCallBackListener callBackListener){
        if(null!=mPresenter) mPresenter.activationDevice(params,callBackListener);
    }

    /**
     * 视频分享上报
     * @param videoInfo 视频信息
     * @param platform
     * @param callBackListener
     */
    public void shareVideoPost(PrivateMedia videoInfo, int platform, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.shareVideoPost(videoInfo, platform, callBackListener);
    }

    /**
     * 视频播放上报
     * @param videoInfo 视频信息
     * @param callBackListener
     */
    public void previewVideoPost(PrivateMedia videoInfo, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.previewVideoPost(videoInfo, callBackListener);
    }

    /**
     * 上报用户位置
     *
     * @param longitude
     * @param latitude
     * @param radius
     * @param location
     * @param callBackListener
     */
    public void uploadLocation(double longitude, double latitude, float radius, String location, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.uploadLocation(longitude, latitude, radius, location, callBackListener);
    }

    /**
     * 获取广告位
     * @param callBackListener
     */
    public void getBanners(UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.getBanners(callBackListener);
    }

    /**
     * 游客身份注册
     * @param callBackListener
     */
    public void visitorRegister(UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.visitorRegister(callBackListener);
    }

    /**
     * 绑定第三方平台账号
     * @param platformId 账号类型 参考 Constant.LOGIN_TYPE_QQ 类型
     * @param openid
     * @param access_token
     * @param callBackListener
     */
    public void onBindPlatformAccount(final int platformId, String openid, String access_token, String phone, String code,UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.onBindPlatformAccount(platformId,openid,access_token,phone,code,callBackListener);
    }

    /**
     * 解绑第三方平台账号
     * @param platfromID 账号类型 参考 Constant.LOGIN_TYPE_QQ 类型
     * @param callBackListener
     */
    public void onUnBindPlatformAccount(int platfromID,UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.onUnBindPlatformAccount(platfromID,callBackListener);
    }

    /**
     * 查询用户的第三方账号绑定状态
     * @param callBackListener
     */
    public void queryBindState(UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.queryBindState(callBackListener);
    }

    /**
     * 登出
     * @param callBackListener
     */
    private void loginOut(UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mUserId && null != mPresenter) {
            mPresenter.loginOut(mUserId, callBackListener);
        }
    }

    /**
     * 付费获得多媒体预览权限
     * @param privateMedia
     * @param homeUserID
     * @param buyChanner 购买渠道
     * @param callBackListener
     */
    public void buyMedia(PrivateMedia privateMedia, String homeUserID,String buyChanner,UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.buyMedia(privateMedia,homeUserID,buyChanner,callBackListener);
    }

    /**
     * 付费获得多媒体预览权限
     * @param fileID
     * @param homeUserID
     * @param buyChanner 购买渠道
     * @param is_buy 0：询问用户 >0 不需要询问
     * @param callBackListener
     */
    public void browseMediaFile(long fileID, String homeUserID,String buyChanner,int is_buy,UserServerContract.OnCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.browseMediaFile(fileID,homeUserID,buyChanner,is_buy,callBackListener);
    }


    /**
     * 付费发送消息
     * @param identify 接收者
     * @param content 内容
     * @param annexType 自定义消息类别 0：纯文本 1：多媒体
     * @param conversationType 会话类型 0：C2C 1:GROUP
     */
    public void sendMsg(String identify, String content, int annexType, int conversationType,UserServerContract.OnSendMessageCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.sendMsg(identify,content,annexType,conversationType,callBackListener);
    }

    /**
     * 上传文件鉴权
     * @param data 鉴权对象
     * @param callBackListener
     */
    public void uploadFileAuthentication(UploadObjectInfo data,UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.uploadFileAuthentication(data,callBackListener);
    }

    /**
     * 修改用户信息
     * @param paramsKey 参数KEY
     * @param keyContent VALUE
     * @param callBackListener
     */
    public void modityUserData(String paramsKey,String keyContent,UserServerContract.OnNetCallBackListener callBackListener){
        if (null != mPresenter) mPresenter.modityUserData(paramsKey,keyContent,callBackListener);
    }

    /**
     * 写入静态数据日志
     * @param actionType 10001  支付  10002 视频通话
     * @param object
     * @param callBackListener
     */
    public void postActionState(int actionType, ActionLogInfo object, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.postActionState(actionType,object,callBackListener);
    }

    /**
     * 写入静态路由日志
     * @param actionType 10001  支付  10002 视频通话
     * @param object
     * @param callBackListener
     */
    public void postHostState(int actionType, ActionLogInfo object, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.postHostState(actionType,object,callBackListener);
    }


    /**
     * 创建直播房间
     * @param callBackListener
     */
    public void createRoom(UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.createRoom(callBackListener);
    }

    /**
     * 获取直播间任务列表
     * @param callBackListener
     */
    public void getRoomPopupTasks(UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.getRoomPopupTasks(callBackListener);
    }

    /**
     * 用户挂断推送的视频邀请后触发自动回复
     * @param anchorId
     * @param userId
     * @param state
     * @param callBackListener
     */
    public void sendHangupReceiptMsg(String anchorId, String userId, int state, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.sendHangupReceiptMsg(anchorId,userId,state,callBackListener);
    }

    /**
     * 根据场景获取礼物分类列表
     * @param sourceApiType
     * @param callBackListener
     */
    public void getGiftTypeList(int sourceApiType, UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.getGiftTypeList(sourceApiType,callBackListener);
    }

    /**
     * 派发手机号码验证码
     * @param country
     * @param phone
     * @param callBackListener
     */
    public void getVerificationCode(String country, String phone,UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.getVerificationCode(country,phone,callBackListener);
    }

    /**
     * 查询活动
     * @param typeID
     * @param order_sn
     * @param callBackListener
     */
    public void queryActivitys(int typeID,String order_sn,UserServerContract.OnNetCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.queryActivitys(typeID,order_sn,callBackListener);
    }

    /**
     * 用户的文件上传权限
     * @param type
     * @param callBackListener
     */
    public void checkedUploadFilePermission(String type,UserServerContract.OnNetCallBackListener callBackListener) {
        if(null!=mPresenter) mPresenter.checkedUploadFilePermission(type,callBackListener);
    }

    /**
     * 注销用户，清空用户信息
     */
    public void logout(UserServerContract.OnNetCallBackListener callBackListener) {
        mUserId = null;
        mFrontCover = null;
        mUserAvatar = null;
        clearUserInfo();
        loginOut(callBackListener);
    }

    /**
     * 用户登出，清除所有用户相关的数据
     */
    public void cleanUserInfo() {
        mUserId = null;mFrontCover = null; mUserAvatar = null; mNickName = null;mUserSig=null;
        mSdkAppID = null;sex = 0;phone = null;mLoginToken = null;mTokenExp=null;
        mAccountType=null;userGradle=0;userVip=0;longitude=116.23;latitude=39.54;province="北京";
        diamonds=0;vipEndtime=0;uploadImageCount=0;identity_audit=0;level_integral=0;chat_deplete=0;
        signature=null;position=null;sex=0;height=null;weight=null;speciality=null;star=null;label=null;
        points=0;quite=0;userType=0;
        clearUserInfo();
        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_UNLOGIN);//通知个人中心清空用户数据
    }

    /**
     * 获取用户缓存信息
     */
    private void loadUserInfo() {
        mUserId = SharedPreferencesUtil.getInstance().getString("user_id");
        mLoginToken = SharedPreferencesUtil.getInstance().getString("cltoken");


        mTokenExp = SharedPreferencesUtil.getInstance().getString("cltokenexp");
        LogUtil.msg("mLoginToken:  " + mLoginToken+",mTokenExp:"+mTokenExp);

        //初始化缓存的用户昵称和头像，用户ID，昵称，头像是必须缓存的，其他信息可以先不急着获取,特殊场景下需要先得到用户昵称和头像
        if (null != SharedPreferencesUtil.getInstance().getString("nickname")) {
            this.mNickName = SharedPreferencesUtil.getInstance().getString("nickname");
        }
        if (null != SharedPreferencesUtil.getInstance().getString("avatar")) {
            this.mUserAvatar = SharedPreferencesUtil.getInstance().getString("avatar");
        }
    }

    /**
     * 保存用户登录必须信息
     *
     * @param userid
     * @param response
     */
    public void saveUserToken(String userid, Response response) {
        if (null == response) return;
        mUserId=userid;
        mLoginToken=response.header("cltoken");
        mTokenExp=response.header("cltokenexp");
        SharedPreferencesUtil.getInstance().putString("user_id", userid);
        SharedPreferencesUtil.getInstance().putString("cltoken", response.header("cltoken"));
        SharedPreferencesUtil.getInstance().putString("cltokenexp", response.header("cltokenexp"));
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("cltoken",TextUtils.isEmpty(mLoginToken)? SharedPreferencesUtil.getInstance().getString("cltoken"):mLoginToken);
        headers.put("cltokenexp", TextUtils.isEmpty(mTokenExp)?SharedPreferencesUtil.getInstance().getString("cltokenexp"):mTokenExp);
        return headers;
    }

    /**
     * 清除用户缓存信息
     */
    private void clearUserInfo() {
        SharedPreferencesUtil.getInstance().putString("user_id", "");
        SharedPreferencesUtil.getInstance().putString("cltoken", "");
        SharedPreferencesUtil.getInstance().putString("cltokenexp", "");
    }

    public String getAuthenticationState() {
        String result = "未认证";

        if (identity_audit == 0) {
            result = "未认证";
        } else if (identity_audit == 1) {
            result = "身份信息正在审核中";
        } else if (identity_audit == 2) {
            result = "审核通过";
        } else if (identity_audit == 3) {
            result = "审核不通过";
        }
        return result;
    }

    public void onDestroy() {
        if (null != mPresenter) mPresenter.detachView();
    }

    /**
     * 激活
     */
    public void activation(Context context) {
        try {
            TelephonyManager phone = (TelephonyManager) context.getSystemService(android.content.Context.TELEPHONY_SERVICE);
//        获取网络连接管理者
            ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
            //获取网络的状态信息，有下面三种方式
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            String cpuName = Utils.getCpuName();
            long totalMemory = getTotalMemory();
            int screenWidth = ScreenUtils.getScreenWidth();
            int screenHeight = ScreenUtils.getScreenHeight();

            String md5 = Utils.getMD5(android.os.Build.BRAND + android.os.Build.MODEL + Build.VERSION.RELEASE + cpuName + screenWidth + screenHeight + Build.SERIAL);
            LogUtil.msg("md5:" + md5);
            Map<String, String> paramsDevice = new HashMap<>();
            paramsDevice.put("app_id", "0");
            paramsDevice.put("brand", android.os.Build.BRAND);
            paramsDevice.put("product_type", android.os.Build.MODEL);
            paramsDevice.put("operator", phone.getSimOperatorName());
            paramsDevice.put("system_version", Build.VERSION.RELEASE);
            paramsDevice.put("memory_stick", String.valueOf(totalMemory));
            paramsDevice.put("cpu", cpuName);
            paramsDevice.put("px_width", String.valueOf(screenWidth));
            paramsDevice.put("px_height", String.valueOf(screenHeight));
            paramsDevice.put("network", String.valueOf(networkInfo.getType()));
            paramsDevice.put("serial", Build.SERIAL);
            paramsDevice.put("imeil",VideoApplication.mUuid);
            paramsDevice.put("equipment_id", md5);
            //激活设备
            activationDevice(paramsDevice, null);

        } catch (RuntimeException e) {

        } catch (Exception e) {

        }
    }

    //获取总内存的大小
    private long getTotalMemory() {
//        MemTotal:         341780 kB
        try {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            //包装一个一行行读取的流
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            //取到所有的内存信息
            String memTotal = bufferedReader.readLine();

            StringBuffer sb = new StringBuffer();

            for (char c : memTotal.toCharArray()) {

                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            //为了方便格式化 所以乘以1024
            long totalMemory = Long.parseLong(sb.toString()) * 1024;

            return totalMemory;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean isVip() {
        if (vipEndtime > 0 && System.currentTimeMillis() < vipEndtime) {
            return true;
        }
        return false;
    }
}