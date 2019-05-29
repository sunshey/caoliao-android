package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.index.model.bean.OneListBean;
import com.yc.liaolive.ui.contract.IndexListContract;
import com.yc.liaolive.util.LogRecordUtils;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 首页 热门、附近
 */
public class IndexListPresenter extends RxBasePresenter<IndexListContract.View> implements IndexListContract.Presenter<IndexListContract.View> {

    @Override
    public void getLiveLists(final String lastUserID, String type, int page) {
        if(isLoading) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_4());
        params.put("last_userid",lastUserID);
        params.put("page_size", "20");
        params.put("type", type);
        params.put("page", String.valueOf(page));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_4(),
                new TypeToken<ResultInfo<OneListBean>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultInfo<OneListBean>>() {
            @Override
            public void call(ResultInfo<OneListBean> data) {
                isLoading=false;
                if (null != data) {
                    if (null != data.getData()) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if(null!=mView){
                                if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() > 0) {
                                    mView.showLiveRooms(data.getData());
                                } else if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() <= 0) {
                                    mView.showLiveRoomEmpty();
                                } else {
                                    mView.showLiveRoomError(-1, NetContants.NET_REQUST_JSON_ERROR);
                                }
                            }
                        } else {
                            LogRecordUtils.getInstance().putLog(NetContants.getInstance().URL_ROOM_3(), data.getCode(), data.getMsg());
                            if (null != mView) mView.showLiveRoomError(data.getCode(), NetContants.getErrorMsg(data));
                        }
                    } else {
                        //请求失败
                        if (null != data.getResponse()) {
                            LogRecordUtils.getInstance().putLog(NetContants.getInstance().URL_ROOM_3(), data.getResponse().code(), data.getResponse().message());
                        }
                        if (null != mView) mView.showLiveRoomError(-1, NetContants.NET_REQUST_ERROR);
                    }
                } else {
                    if (null != mView) mView.showLiveRoomError(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}