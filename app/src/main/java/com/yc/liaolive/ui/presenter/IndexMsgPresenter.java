package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.msg.model.bean.CallMessageInfo;
import com.yc.liaolive.ui.contract.IndexMsgContract;
import com.yc.liaolive.user.manager.UserManager;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/10/16
 * 我的通话、预约、积分、钻石 记录
 */
public class IndexMsgPresenter extends RxBasePresenter<IndexMsgContract.View> implements IndexMsgContract.Presenter<IndexMsgContract.View> {

    private boolean getIndexMsg;

    public boolean isGetIndexMsg() {
        return getIndexMsg;
    }

    /**
     * 获取消息数据
     */
    @Override
    public void getMessageIndexList() {
        if(getIndexMsg) return;
        getIndexMsg=true;

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_MSG_MENU());
        params.put("userid",UserManager.getInstance().getUserId());
//        params.put("api_version","20181221");

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_MSG_MENU(),
                new TypeToken<ResultInfo<ResultList<CallMessageInfo>>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultInfo<ResultList<CallMessageInfo>>>() {
            @Override
            public void call(ResultInfo<ResultList<CallMessageInfo>> data) {
                getIndexMsg=false;
                if (null != data) {
                    if (null != data.getData()) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if(null!=mView){
                                if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() > 0) {
                                    for (int i = 0; i < data.getData().getList().size(); i++) {
                                        CallMessageInfo callMessageInfo = data.getData().getList().get(i);
                                        if(6==callMessageInfo.getId()){
                                            callMessageInfo.setItemType(1);
                                        }
                                    }
                                    mView.showListResult(data.getData().getList());
                                } else if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() <= 0) {
                                    mView.showListResultEmpty();
                                } else {
                                    mView.showListResultError(-1, NetContants.NET_REQUST_JSON_ERROR);
                                }
                            }
                        } else {
                            if (null != mView) mView.showListResultError(data.getCode(), NetContants.getErrorMsg(data));
                        }
                    } else {
                        if (null != mView) mView.showListResultError(-1, NetContants.NET_REQUST_ERROR);
                    }
                } else {
                    if (null != mView) mView.showListResultError(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取用户通话、预约、积分、钻石 等记录
     * @param url
     * @param itemType
     * @param last_id
     * @param state
     */
    @Override
    public void getCallNotesList(String url,final int itemType,long last_id,int state) {

        if(isLoading) return;
        isLoading=true;

        Map<String, String> params = getDefaultPrames(url);
        params.put("last_id",String.valueOf(last_id));
        params.put("userid", UserManager.getInstance().getUserId());
        params.put("state",String.valueOf(state));
        params.put("pageSize",String.valueOf(NetContants.PAGE_SIZE));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(url,
                new TypeToken<ResultInfo<ResultList<CallMessageInfo>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<CallMessageInfo>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showListResultError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<CallMessageInfo>> data) {
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
