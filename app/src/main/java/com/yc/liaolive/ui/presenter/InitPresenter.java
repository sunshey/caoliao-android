package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.AllGiftInfo;
import com.yc.liaolive.bean.HomeNoticeInfo;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.index.ui.MainActivity;
import com.yc.liaolive.ui.contract.InitContract;
import com.yc.liaolive.util.Logger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2017/6/1 15:00
 * 初始化
 */
public class InitPresenter extends RxBasePresenter<InitContract.View> implements InitContract.Presenter<InitContract.View> {

    /**
     * 获取系统通告,如果有数据，且有未读的消息，提示用户有新消息
     */
    @Override
    public void init(MainActivity context) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_HOME_NOTICE_LIST());
        params.put("page", "1");
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_HOME_NOTICE_LIST(), new TypeToken<ResultInfo<ResultList<HomeNoticeInfo>>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<HomeNoticeInfo>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                List<HomeNoticeInfo> cacheNotices = (List<HomeNoticeInfo>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_HOME_NOTICE);
                if (null != cacheNotices) {
                    boolean isExieNoRead = true;
                    for (HomeNoticeInfo homeNoticeInfo : cacheNotices) {
                        if (!homeNoticeInfo.isRead()) {
                            isExieNoRead = false;
                            break;
                        }
                    }
                    if (!isExieNoRead)
                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_HAS_NEW_MESSAGE);
                }
            }

            @Override
            public void onNext(ResultInfo<ResultList<HomeNoticeInfo>> data) {
                if (null != data && NetContants.API_RESULT_CODE == data.getCode()) {
                    if (null != data.getData() && null != data.getData().getList()) {
                        //新的网络获取的
                        List<HomeNoticeInfo> newNoticeInfo = data.getData().getList();
                        //本地缓存的
                        List<HomeNoticeInfo> cacheNotices = (List<HomeNoticeInfo>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_HOME_NOTICE);
                        Map<Integer, HomeNoticeInfo> infoMap = new HashMap<>();
                        //1：将本地缓存数据封装进Map
                        if (null != cacheNotices) {
                            for (HomeNoticeInfo cacheNotice : cacheNotices) {
                                infoMap.put(cacheNotice.getAnnounce_id(), cacheNotice);
                            }
                        }

                        //2：将网络数据封装进Map，去重
                        for (HomeNoticeInfo homeNoticeInfo : newNoticeInfo) {
                            boolean exit = false;
                            Iterator<Map.Entry<Integer, HomeNoticeInfo>> iterator = infoMap.entrySet().iterator();
                            while (iterator.hasNext()) {
                                if (iterator.next().getKey().equals(homeNoticeInfo.getAnnounce_id())) {
                                    exit = true;
                                    break;
                                }
                            }
                            if (!exit) {
                                infoMap.put(homeNoticeInfo.getAnnounce_id(), homeNoticeInfo);
                            }
                        }
                        //3：将所有合并的数据重新替代本地缓存
                        Iterator<Map.Entry<Integer, HomeNoticeInfo>> iterator = infoMap.entrySet().iterator();
                        List<HomeNoticeInfo> newCacheNotice = new ArrayList<>();
                        while (iterator.hasNext()) {
                            newCacheNotice.add(iterator.next().getValue());
                        }
                        //4：刷新本地缓存
                        ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_HOME_NOTICE);
                        ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_HOME_NOTICE, (Serializable) newCacheNotice);
                        //5：判断是否有未读的消息
                        boolean isExieNoRead = true;
                        for (HomeNoticeInfo homeNoticeInfo : newCacheNotice) {
                            if (!homeNoticeInfo.isRead()) {
                                isExieNoRead = false;
                                break;
                            }
                        }
                        if (!isExieNoRead)
                            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_HAS_NEW_MESSAGE);
                    } else {
                        ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_HOME_NOTICE);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }
    
    /**
     * 获取全部的礼物
     * 直接在当前线程处理
     * @param callBackListener
     */
    @Override
    public void getAllGift(final InitContract.OnCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_ALL_GIFT());
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_ALL_GIFT() , new TypeToken<ResultInfo<AllGiftInfo>>() {}.getType(), params, getHeaders(),isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<AllGiftInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<AllGiftInfo> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=data.getData()){
                            if(null!=callBackListener) callBackListener.onSuccess(data.getData());
                        }else{
                            if(null!=callBackListener) callBackListener.onFailure(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=callBackListener) callBackListener.onFailure(data.getCode(),data.getMsg());
                    }
                }else{
                    if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }
}
