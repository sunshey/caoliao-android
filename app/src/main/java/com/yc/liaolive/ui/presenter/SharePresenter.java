package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.ShareContract;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/6/6
 * 分享
 */
public class SharePresenter extends RxBasePresenter<ShareContract.View> implements ShareContract.Presenter<ShareContract.View> {

    /**
     * 获取WEBURL
     * @param roomID
     */
    @Override
    public void getWebUrl(String roomID) {
        Map<String,String> params=new HashMap<>();
        params.put("room_id", String.valueOf(roomID));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().UEL_WEB_URL(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showWebResultError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=mView) mView.showWebResult(data.getMsg());
                    }else{
                        if(null!=mView) mView.showWebResultError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showWebResultError(data.getCode(), NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
