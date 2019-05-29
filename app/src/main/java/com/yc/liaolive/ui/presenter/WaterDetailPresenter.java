package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.RechargeFillInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.WaterDetailContract;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/6/6
 * 流水明细
 */
public class WaterDetailPresenter extends RxBasePresenter<WaterDetailContract.View> implements WaterDetailContract.Presenter<WaterDetailContract.View> {

    @Override
    public void getUserWaterDetsils(String userID) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_PERSONAL());
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_PERSONAL(),
                new TypeToken<ResultInfo<RechargeFillInfo>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<RechargeFillInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showUserDetsilsError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<RechargeFillInfo> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()){
                            if(null!=mView) mView.showUserDetsilsResult(data.getData());
                        }else{
                            if(null!=mView) mView.showUserDetsilsError(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showUserDetsilsError(data.getCode(),NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showUserDetsilsError(data.getCode(), NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
