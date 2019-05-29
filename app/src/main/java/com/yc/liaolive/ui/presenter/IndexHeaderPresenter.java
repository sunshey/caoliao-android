package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.ui.contract.IndexHeaderContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * TinyHung@outlook.com
 * 2018/5/24
 * 首页
 */
public class IndexHeaderPresenter extends RxBasePresenter<IndexHeaderContract.View> implements IndexHeaderContract.Presenter<IndexHeaderContract.View> {

    private boolean isGetRecommend=false;

    public boolean isGetRecommend() {
        return isGetRecommend;
    }

    /**
     * 获取广告位
     */
    @Override
    public void getBanners() {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_HOME_BANNER());
        params.put("type_id", "1");
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_HOME_BANNER(),
                new TypeToken<ResultInfo<ResultList<BannerInfo>>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<BannerInfo>>>() {
            @Override
            public void onCompleted() {
                if (null != mView) mView.complete();
            }

            @Override
            public void onError(Throwable e) {
                if (null != mView) mView.showRecommendError(-1, NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<BannerInfo>> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showBannerResult(data.getData().getList());
                            ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_HOME_BANNERS);
                            ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_HOME_BANNERS, (Serializable) data.getData().getList());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showBannerResultEmpty();
                            ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_HOME_BANNERS);
                        }else{
                            if(null!=mView) mView.showBannerResultError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showBannerResultError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                }else{
                    if (null != mView) mView.showBannerResultError(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取推荐位的主播
     * @param type
     * @param page
     */
    @Override
    public void getRecommendAnchor(String type, int page) {

        if(isGetRecommend) return;
        isGetRecommend=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_RECOMMEND());
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_RECOMMEND(),
                new TypeToken<ResultInfo<ResultList<RoomList>>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<RoomList>>>() {
            @Override
            public void onCompleted() {
                if (null != mView) mView.complete();
            }

            @Override
            public void onError(Throwable e) {
                isGetRecommend=false;
                if (null != mView) mView.showRecommendError(-1, NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<RoomList>> data) {
                isGetRecommend=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(data.getData().getList().size()>3){
                                List<RoomList> slipList=new ArrayList<>();
                                for (RoomList roomList : data.getData().getList()) {
                                    slipList.add(roomList);
                                    if(slipList.size()>=3){
                                        break;
                                    }
                                }
                                if(null!=mView) mView.showRecommendAnchors(slipList);
                                return;
                            }
                            if(null!=mView) mView.showRecommendAnchors(data.getData().getList());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showRecommendEmpty();
                        }else{
                            if(null!=mView) mView.showRecommendError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showRecommendError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                }else{
                    if (null != mView) mView.showRecommendError(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
