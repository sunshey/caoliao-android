package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.AttachFirendContract;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2017/6/1 15:00
 * 用户关注、粉丝、好友
 */
public class AttachFirendPresenter extends RxBasePresenter<AttachFirendContract.View> implements AttachFirendContract.Presenter<AttachFirendContract.View> {

    private boolean isLoading;

    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 获取粉丝列表
     * @param url 路由
     * @param userID
     * @param page
     */
    @Override
    public void getAttachFirends(String url, String userID, String page) {
        if(isLoading) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(url);
        params.put("page",page);
        params.put("page_size",String.valueOf(NetContants.PAGE_SIZE));
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(url , new TypeToken<ResultInfo<ResultList<FansInfo>>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<FansInfo>>>() {
            @Override
            public void onCompleted() {
                isLoading=false;
            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showListError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<FansInfo>> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showList(data.getData().getList());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showListEmpty("没有更多了");
                        }else{
                            if(null!=mView) mView.showListError(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showListError(data.getCode(),NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showListError(-1,NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }
}
