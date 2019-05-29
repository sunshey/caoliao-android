package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.HomeNoticeInfo;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.ui.contract.PublicNoticeContract;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 通告中心
 */

public class PublicNoticPresenter extends RxBasePresenter<PublicNoticeContract.View> implements PublicNoticeContract.Presenter<PublicNoticeContract.View> {

    /**
     * 获取通知列表
     */
    @Override
    public void getPublicNotices(int page) {
        if(isLoading) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_HOME_NOTICE_LIST());
        params.put("page",String.valueOf(page));
        params.put("page_size","20");
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_HOME_NOTICE_LIST() , new TypeToken<ResultInfo<ResultList<HomeNoticeInfo>>>() {}.getType(), params, getHeaders(),isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<HomeNoticeInfo>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showPublicNoticeError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<HomeNoticeInfo>> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_HOME_NOTICE);
                            if(null!=mView) mView.showPublicNotices(data.getData().getList());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showPublicNoticeEmpty();
                        }else{
                            if(null!=mView) mView.showPublicNoticeError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showPublicNoticeError(data.getCode(),data.getMsg());
                    }
                }else{
                    if(null!=mView) mView.showPublicNoticeError(-1,NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 获取通知详情
     * @param id
     */
    @Override
    public void getNoticeDetails(String id) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_HOME_NOTICE_DETAILS());
        params.put("announce_id",id);
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_HOME_NOTICE_DETAILS() , new TypeToken<ResultInfo<HomeNoticeInfo>>() {}.getType(), params, getHeaders(),isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<HomeNoticeInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showNoticeDetailError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<HomeNoticeInfo> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=data.getData()&&null!=data.getData()){
                            if(null!=mView) mView.showNoticeDetails(data.getData());
                        }else{
                            if(null!=mView) mView.showNoticeDetailError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showNoticeDetailError(data.getCode(),data.getMsg());
                    }
                }else{
                    if(null!=mView) mView.showNoticeDetailError(-1,NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }
}
