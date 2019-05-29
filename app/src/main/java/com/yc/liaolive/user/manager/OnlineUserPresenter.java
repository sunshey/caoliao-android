package com.yc.liaolive.user.manager;

import android.app.Activity;

import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.tencent.TIMConversationType;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.ui.dialog.LoadingProgressView;
import com.yc.liaolive.user.IView.IOnlineUserView;
import com.yc.liaolive.user.model.OnlineUserEngine;
import com.yc.liaolive.user.model.bean.OnlineUserBean;
import com.yc.liaolive.util.CountDownTimer;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.manager.MakeCallManager;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * 在线用户列表 管理
 * Created by yangxueqin on 18/12/15.
 */
public class OnlineUserPresenter extends RxBasePresenter<IOnlineUserView> {

    private static final String TAG = "OnlineUserPresenter";

    private OnlineUserEngine mEngine;
    private LoadingProgressView mProgressView;

    //倒计时
    private CountDownTimer mCountDownTimer;

    private boolean has_more_page;

    private int type;

    private boolean canCallOut = true; //是否可以发送视频聊邀请

    private long call_time = 60; //呼叫用户间隔时间单位秒

    private int call_sum_num; //当天剩余可呼叫用户未接通总次数

    private String videoChatUserId; //视频聊对方userid

    public OnlineUserPresenter(Activity activity){
        mEngine = new OnlineUserEngine(activity);
        mProgressView = new LoadingProgressView(activity);
    }

    /**
     * 获取在线用户数据
     * @param page 当前页码
     * @param type 0 在线用户  1在线VIP用户
     * @param login_time 最后一条用户登录时间
     * @param userid 最后一条用户id
     */
    public void getOnlineUserListData(final int page, int type, String login_time, String userid) {
        if (null != mProgressView && !mProgressView.isShowing()) {
            mProgressView.showMessage("加载中...");
        }
        Subscription subscription = mEngine.getListData(login_time, userid, type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<OnlineUserBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(null!=mProgressView) mProgressView.dismiss();
                    }

                    @Override
                    public void onNext(ResultInfo<OnlineUserBean> data) {
                        mProgressView.dismiss();
                        if(null != mView){
                            if (page == 1) {
                                handlerFirstPage(data);
                            } else {
                                handlerNotFirstPage(data);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 处理首页数据
     */
    private void handlerFirstPage(ResultInfo<OnlineUserBean> data) {
        if (null != data) {
            if (data.getCode() == HttpConfig.STATUS_OK) {
                OnlineUserBean info = data.getData();
                if (info == null) {
                    has_more_page = false;
                    mView.showError(NetContants.NET_REQUST_JSON_ERROR);
                } else if (info.getList() == null || info.getList().size() == 0) {
                    has_more_page = false;
                    mView.showListEmpty();
                } else {
                    mView.setDataView(info);
                    if (type == 1) {
                        setHeadDate(info);
                        long preStartTime = SharedPreferencesUtil.getInstance().getLong("START_COUNT_DOWN_TIME");
                        //上次开始倒计时时间在call_time之内，继续开启倒计时
                        long leftTime = System.currentTimeMillis() - preStartTime;
                        if (leftTime < call_time * 1000) {
                            mView.setCallLeftTimeData(call_time - leftTime / 1000);
                        }
                    }
                    has_more_page = "1".equals(info.getHas_more_data());
                }
                if (!has_more_page) {
                    mView.setListEnd(true);
                } else {
                    mView.setListEnd(false);
                }
            } else {
                mView.showError(NetContants.getErrorMsg(data));
            }
        } else {
            mView.showError(NetContants.NET_REQUST_ERROR);
        }
    }

    /**
     * 处理非首页数据
     */
    private void handlerNotFirstPage(ResultInfo<OnlineUserBean> data) {
        if (null != data) {
            if (data.getCode() == HttpConfig.STATUS_OK) {
                OnlineUserBean info = data.getData();
                if (info == null || info.getList() == null || info.getList().size() == 0) {
                    has_more_page = false;
                } else {
                    mView.setDataView(info);
                    if (type == 1) {
                        setHeadDate(info);
                    }
                    has_more_page = "1".equals(info.getHas_more_data());
                }
                if (!has_more_page) {
                    mView.setListEnd(true);
                } else {
                    mView.setListEnd(false);
                }
            } else {
                ToastUtils.showCenterToast(data.getMsg());
            }
        } else {
            ToastUtils.showCenterToast("你的网络好像不太给力\n请稍后再试");
        }
    }

    private void setHeadDate (OnlineUserBean userBean) {
        try {
            call_sum_num = Integer.parseInt(userBean.getCall_sum_num());
            call_time = Long.parseLong(userBean.getCall_time());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        mView.setCallLeftCountView(call_sum_num);
    }

    /**
     * 跳转私聊
     * @param mToUserid 用户id
     * @param nickname 用户昵称
     */
    public void clickTalkMsg (String mToUserid, String nickname) {
        ChatActivity.navToChat(mToUserid, nickname, TIMConversationType.C2C);
    }

    /**
     * 视频聊
     * @param mToUserid
     * @param nickname
     * @param avatar
     */
    public void clickTalkVideo (String mToUserid, String nickname, String avatar) {
        this.videoChatUserId = mToUserid;
        CallExtraInfo callExtraInfo = new CallExtraInfo();
        callExtraInfo.setToUserID(mToUserid);
        callExtraInfo.setToNickName(nickname);
        callExtraInfo.setToAvatar(avatar);
        callExtraInfo.setRecevierID("anchor_call");
        MakeCallManager.getInstance().attachActivity((mView.getDependType())).mackCall(callExtraInfo, 1);
    }

    /**
     * 启动倒计时 点击列表中的视频聊调用
     */
    public void startCallCountDown () {
        //保存当前开始倒计时的时间，用于下次进入页面时继续倒计时
        SharedPreferencesUtil.getInstance().putLong("START_COUNT_DOWN_TIME", System.currentTimeMillis());
        mView.setCallLeftTimeData(call_time);
    }

    /**
     * 初始化呼叫用户间隔时间倒计时监听器
     */
    public void initCallCountTimeListener() {
        CallTimeManager.getInstance().getmEventBus()
                .registerType(CountDownTimer.CountTimeInfo.class)
//                .compose(this.<CountDownTimer.CountTimeInfo>bindUntilEvent(ActivityEvent.DESTROY))
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (mCountDownTimer != null) {
                            mCountDownTimer.cancel();
                        }
                    }
                })
                .subscribe(new Action1<CountDownTimer.CountTimeInfo>() {

                    @Override public void call(CountDownTimer.CountTimeInfo countTimeInfo) {
                        switch (countTimeInfo.state) {
                            // 计时中
                            case CountDownTimer.STATE_COUNTING:
                                if (mView != null) {
                                    mView.setLeftTimeView(countTimeInfo.second);
                                }
                                break;
                            // 取消
                            case CountDownTimer.STATE_CANCEL:
                                break;
                            // 完成
                            case CountDownTimer.STATE_ONCOMPLETED:
                                canCallOut = true;
                                if (mView != null) {
                                    mView.setLeftTimeComplete();
                                }
                                break;
                        }
                    }
                });
    }

    public void startCountDown (long mTime) {
        if (mTime > 0) {
            if (mCountDownTimer != null) {//如果有倒计时，先停止之前的倒计时
                mCountDownTimer.cancel();
            }
            canCallOut = false;
            mCountDownTimer =
                    new CountDownTimer(CallTimeManager.getInstance().getmEventBus(), mTime,
                            100);
            mCountDownTimer.initAndStart();
        }
    }

    @Override public void detachView() {
        super.detachView();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCall_time() {
        return call_time;
    }

    public void setCall_time(long call_time) {
        this.call_time = call_time;
    }

    public int getCall_sum_num() {
        return call_sum_num;
    }

    public void setCall_sum_num(int call_sum_num) {
        this.call_sum_num = call_sum_num;
    }

    public boolean isCanCallOut() {
        return canCallOut;
    }

    public void setCanCallOut(boolean canCallOut) {
        this.canCallOut = canCallOut;
    }

    public String getVideoChatUserId() {
        return videoChatUserId;
    }

}
