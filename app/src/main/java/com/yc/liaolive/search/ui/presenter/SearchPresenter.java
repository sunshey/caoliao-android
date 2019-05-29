package com.yc.liaolive.search.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.search.ui.contract.SearchContract;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 搜索
 */

public class SearchPresenter extends RxBasePresenter<SearchContract.View> implements SearchContract.Presenter<SearchContract.View> {


    @Override
    public void search(final String key,int page) {
        if(isLoading) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_SEARCH());
        params.put("keyword",key);
        params.put("page",String.valueOf(page));
        params.put("page_size","10");
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_SEARCH() , new TypeToken<ResultInfo<ResultList<UserInfo>>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<UserInfo>>>() {
            @Override
            public void onCompleted() {
                isLoading=false;
            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showSearchError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<UserInfo>> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showSearchResul(data.getData().getList());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showSearchEmpty(key);
                        }else{
                            if(null!=mView) mView.showSearchError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showSearchError(data.getCode(),data.getMsg());
                    }
                }else{
                    if(null!=mView) mView.showSearchError(-1,NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }
}
