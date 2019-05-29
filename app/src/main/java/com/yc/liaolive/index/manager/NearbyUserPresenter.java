package com.yc.liaolive.index.manager;

import android.app.Activity;

import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.tencent.TIMConversationType;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.index.contract.INearbyUserView;
import com.yc.liaolive.index.model.NearbyUserEngine;
import com.yc.liaolive.index.model.bean.NearbyUserBean;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.util.ToastUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 附近用户 管理
 * Created by yangxueqin on 19/1/18.
 */
public class NearbyUserPresenter extends RxBasePresenter<INearbyUserView> {

    private static final String TAG = "OnlineUserPresenter";

    private NearbyUserEngine mEngine;
//    private LoadingProgressView mProgressView;

    private boolean has_more_page;

    public NearbyUserPresenter(Activity activity){
        mEngine = new NearbyUserEngine(activity);
//        mProgressView = new LoadingProgressView(activity);
    }

    /**
     * 获取在线用户数据
     * @param page 当前页码
     */
    public void getUserListData(final int page) {

        Subscription subscription = mEngine.getListData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<NearbyUserBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showErrorView();
                    }

                    @Override
                    public void onNext(ResultInfo<NearbyUserBean> data) {
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
    private void handlerFirstPage(ResultInfo<NearbyUserBean> data) {
        if (null != data) {
            if (data.getCode() == HttpConfig.STATUS_OK) {
                NearbyUserBean info = data.getData();
                if (info == null) {
                    has_more_page = false;
                    mView.showError(NetContants.NET_REQUST_JSON_ERROR);
                } else if (info.getList() == null || info.getList().size() == 0) {
                    has_more_page = false;
                    mView.showListEmpty();
                } else {
                    mView.setDataView(info);
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
    private void handlerNotFirstPage(ResultInfo<NearbyUserBean> data) {
        if (null != data) {
            if (data.getCode() == HttpConfig.STATUS_OK) {
                NearbyUserBean info = data.getData();
                if (info == null || info.getList() == null || info.getList().size() == 0) {
                    has_more_page = false;
                } else {
                    mView.setDataView(info);
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


    /**
     * 跳转私聊
     * @param mToUserid 用户id
     * @param nickname 用户昵称
     */
    public void clickTalkMsg (String mToUserid, String nickname) {
        ChatActivity.navToChat(mToUserid, nickname, TIMConversationType.C2C);
    }

}
