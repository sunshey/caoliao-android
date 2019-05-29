package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.IntegralTopInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.TopTableContract;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/6/6
 * 排行榜单
 */
public class TopTablePresenter extends RxBasePresenter<TopTableContract.View> implements TopTableContract.Presenter<TopTableContract.View> {

    @Override
    public void getTopTables(String homeUserid,String type) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_TOP_TABLE());
        params.put("home_userid",homeUserid);
        params.put("type", type);
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_TOP_TABLE(),
                new TypeToken<ResultInfo<IntegralTopInfo>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<IntegralTopInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showTopTbaleError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<IntegralTopInfo> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showTopTbaleList(data.getData().getList(),data.getData().getInfo());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showTopTbaleEmpty();
                        }else{
                            if(null!=mView) mView.showTopTbaleError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showTopTbaleError(data.getCode(),NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showTopTbaleError(data.getCode(), NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
