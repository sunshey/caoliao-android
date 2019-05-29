package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.DiamondInfoWrapper;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.AssetContract;
import com.yc.liaolive.user.manager.UserManager;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2017/10/20
 * 积分、钻石 记录
 */
public class AssetPresenter extends RxBasePresenter<AssetContract.View> implements AssetContract.Presenter<AssetContract.View> {


    /**
     * 获取用户资产
     * @param typeID
     * @param itemType
     * @param page
     */

    @Override
    public void getAssetsList(String typeID, final int itemType, int page) {
        if(isLoading) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_PERSONAL_DETAILS_LIST());
        params.put("userid", UserManager.getInstance().getUserId());
        params.put("page_size",String.valueOf(NetContants.PAGE_SIZE));
        params.put("type", typeID);
        params.put("page", String.valueOf(page));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_PERSONAL_DETAILS_LIST(),
                new TypeToken<ResultInfo<DiamondInfoWrapper>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<DiamondInfoWrapper>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showListResultError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<DiamondInfoWrapper> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            for (int i = 0; i < data.getData().getList().size(); i++) {
                                data.getData().getList().get(i).setItemType(itemType);
                            }
                            if(null!=mView) mView.showListResult(data.getData().getList());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showListResultEmpty();
                        }else{
                            if(null!=mView) mView.showListResultError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showListResultError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showListResultError(-1,  NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
