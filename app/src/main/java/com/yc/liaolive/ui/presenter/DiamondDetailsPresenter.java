package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.DiamondInfoWrapper;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.DiamondDetailsContact;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2019/1/25
 * 钻石详情
 */

public class DiamondDetailsPresenter extends RxBasePresenter<DiamondDetailsContact.View> implements DiamondDetailsContact.Presenter<DiamondDetailsContact.View> {

    /**
     * 获取钻石详情
     * @param toUserID 被查看对象
     * @param typeID 3：积分 4：钻石
     * @param assetsType 0：全部 1：支出 2：收入
     * @param page 页数
     */
    @Override
    public void getDaimondDetails(String toUserID,String typeID, int assetsType, int page) {
        if(isLoading()) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_PERSONAL_DETAILS_LIST());
        params.put("userid", toUserID);
        params.put("type", typeID);
        params.put("assets_type", String.valueOf(assetsType));
        params.put("page", String.valueOf(page));
        params.put("page_size", "100");

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
                        if(null!=mView) mView.showDiamondError(-1,e.getMessage());
                    }

                    @Override
                    public void onNext(ResultInfo<DiamondInfoWrapper> data) {
                        isLoading = false;
                        if (null != mView) {
                            if (null != data && null != data.getData()) {
                                if (NetContants.API_RESULT_CODE == data.getCode()) {
                                    if (null != data.getData().getInfo()) {
                                        mView.showDiamondInfo(data.getData().getInfo());
                                    }
                                    if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() > 0) {
                                        mView.showDiamondDetails(data.getData().getList());
                                    } else if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() <= 0) {
                                        mView.showDiamondInfo(null);
                                    } else {
                                        mView.showDiamondError(data.getCode(), data.getMsg());
                                    }
                                } else {
                                    mView.showDiamondError(data.getCode(), data.getMsg());
                                }
                            } else {
                                mView.showDiamondError(-1, "加载失败，请重试");
                            }
                        }
                    }
                });
        addSubscrebe(subscribe);
    }
}
