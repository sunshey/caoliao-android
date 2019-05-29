package com.yc.liaolive.user.IView;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.bean.UploadObjectInfo;
import java.util.Map;

/**
 * TinyHung@outlook.com
 * 2018/8/5 10:53
 * 用户相关的API
 */

public interface UserServerContract {

    /**
     * 普通回调
     */
    interface OnNetCallBackListener{
        void onSuccess(Object object);
        void onFailure(int code, String errorMsg);
    }

    /**
     * 获取房间信息回调
     */
    interface OnCallBackListener{
        void onSuccess(int code,Object object,String msg);
        void onFailure(int code, String errorMsg);
    }

    /**
     * 发送私信消息回调
     */
    interface OnSendMessageCallBackListener{
        void onSuccess(Object object);
        void onFailure(int code, String content,String errorMsg);
    }


    interface View extends BaseContract.BaseView {

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //手机号码注册
        void registerByPhoone(String code,String phone,String zone,OnNetCallBackListener callBackListener);

        //第三方注册
        void registerByOther(String openid, String access_token,String account_type,OnNetCallBackListener callBackListener);

        //登录
        void login(String userID,OnNetCallBackListener callBackListener);

        //获取用户详细信息
        void getUserFullData(String userID, String toUserID,int dataMore,OnNetCallBackListener callBackListener);

        //更新用户信息
        void uploadUserInfo(String nickName, String avatar,String frontCover,int sex,UserServerContract.OnNetCallBackListener callBackListener);

        //账号登出
        void loginOut(String userId, OnNetCallBackListener callBackListener);

        //关注
        void followUser(String toUserID, int status,OnNetCallBackListener callBackListener);

        //举报用户
        void reportUser(String toUserID, OnNetCallBackListener callBackListener);

        //举报多媒体文件
        void reportMediaFile(String toUserID, long id,int type,OnNetCallBackListener callBackListener);

        //将用户加入黑名单
        void addBlackList(String toUserID, int status,OnNetCallBackListener callBackListener);

        //将用户从黑名单移除
        void removeBlackList(String toUserID, OnNetCallBackListener callBackListener);

        //检查用户是否在黑名单内
        void isBlackList(String toUserID,OnNetCallBackListener callBackListener);

        //绑定手机号
        void bindPhone(String phone,String code,String zone,OnNetCallBackListener callBackListener);

        //分享上报
        void shareStatistics(String charUserID,int platform,OnNetCallBackListener callBackListener);

        //视频分享上报
        void shareVideoPost(PrivateMedia videoInfo, int platform, OnNetCallBackListener callBackListener);

        //预览视频上报
        void previewVideoPost(PrivateMedia videoInfo, OnNetCallBackListener callBackListener);

        //查询新手任务奖励
        void getTasks(String type,OnNetCallBackListener callBackListener);

        //查询直播间任务
        void getRoomTasks(OnNetCallBackListener callBackListener);

        //兑换奖励
        void drawTaskAward(int task_id, OnNetCallBackListener callBackListener);

        //兑换奖励
        void drawTaskAward(TaskInfo taskInfo, OnNetCallBackListener callBackListener);

        //上报用户位置
        void uploadLocation(double longitude,double latitude,float radius,String location,OnNetCallBackListener callBackListener);

        //获取配置域名
        void getServerHost(OnNetCallBackListener callBackListener);

        //获取首页广告
        void getBanners(OnNetCallBackListener callBackListener);

        //绑定用户到个推后台
        void bindUserToGetui(String cid,OnNetCallBackListener callBackListener);

        //进入客服会话
        void enterConversation(String identify, OnNetCallBackListener callBackListener);

        //游客注册
        void visitorRegister(OnNetCallBackListener callBackListener);

        //设备激活
        void activationDevice(Map<String, String> params, OnNetCallBackListener callBackListener);

        //绑定第三方平台账号
        void onBindPlatformAccount(int platformId, String openid, String access_token,String phone,String code,OnNetCallBackListener callBackListener);

        //解绑第三方平台账号
        void onUnBindPlatformAccount(int platformId,OnNetCallBackListener callBackListener);

        //查询绑定状态
        void queryBindState(OnNetCallBackListener callBackListener);

        //购买多媒体
        void buyMedia(PrivateMedia privateMedia, String homeUserID, String buyChanner,OnNetCallBackListener callBackListener);

        //浏览多媒体文件
        void browseMediaFile(long fileID, String homeUserID, String buyChanner,int is_buy,OnCallBackListener callBackListener);

        //用户上传文件鉴权
        void uploadFileAuthentication(UploadObjectInfo data, OnNetCallBackListener callBackListene);

        //发送私信
        void sendMsg(String identify, String content, int annexType, int conversationType, OnSendMessageCallBackListener callBackListener);

        //设置勿扰模式
        void setExcuseMode(String userID,int quite, UserServerContract.OnNetCallBackListener callBackListener);

        //修改用户信息
        void modityUserData(String paramsKey, String keyContent, OnNetCallBackListener callBackListene);

        //获取标签库
        void getTags(int type, OnNetCallBackListener callBackListener);

        //上报支付状态、视频通话信息
        void postActionState(int actionType, ActionLogInfo actionLogInfo, OnNetCallBackListener callBackListener);

        //上报路由日志
        void postHostState(int actionType, ActionLogInfo actionLogInfo, OnNetCallBackListener callBackListener);

        //创建直播间
        void createRoom(OnNetCallBackListener callBackListener);

        //获取直播间任务
        void getRoomPopupTasks(OnNetCallBackListener callBackListener);

        //上报直播间心跳状态
        void postRoomHeartState(String roomID, String roomScen, int identity,OnNetCallBackListener callBackListener);

        //推送的视频邀请，用户挂断了主播的通话邀请
        void sendHangupReceiptMsg(String anchorId, String userId, int state, OnNetCallBackListener callBackListener);
        
        //获取礼物类别列表
        void getGiftTypeList(int sourceApiType,OnNetCallBackListener callBackListener);

        //派发验证码
        void getVerificationCode(String country, String phone, OnNetCallBackListener callBackListener);

        //查询活动
        void queryActivitys(int typeID, String order_sn,OnNetCallBackListener callBackListener);

        void checkedUploadFilePermission(String fileType, OnNetCallBackListener callBackListener);
    }
}
