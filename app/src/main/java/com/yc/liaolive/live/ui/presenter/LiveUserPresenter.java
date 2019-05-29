package com.yc.liaolive.live.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.FollowState;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.live.ui.contract.LiveUserContract;
import com.yc.liaolive.util.Logger;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/6/6
 * 用户信息
 */
public class LiveUserPresenter extends RxBasePresenter<LiveUserContract.View> implements LiveUserContract.Presenter<LiveUserContract.View> {

    private static final String TAG = "LiveUserPresenter";

    /**
     * 获取对用户的关注状态和关注用户
     * @param userID 自己的ID
     * @param friendUserID 好友的ID
     * @param status 状态 0：取关 1：关注  2: 查询关注状态
     */
    @Override
    public void followUser(String userID,String friendUserID,int status) {

        Map<String, String> prames = getDefaultPrames(NetContants.getInstance().URL_FOLLOW_USER());
        prames.put("userid", String.valueOf(userID));
        prames.put("attentid", friendUserID);
        prames.put("op", String.valueOf(status));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FOLLOW_USER(),
                new TypeToken<ResultInfo<FollowState>>(){}.getType(), prames,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<FollowState>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showFollowUserError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<FollowState> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&data.getData().getIs_attent()>-1){
                            //获取关注状态成功
                            if(null!=mView) mView.showIsFollow(data.getData().getIs_attent());
                        }else{
                            //关注、取关成功
                            if(null!=mView) mView.showFollowUserResult(data.getData().toString());
                        }
                    }else{
                        if(null!=mView) mView.showFollowUserError(data.getCode(),NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showFollowUserError(data.getCode(), NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取用户基本资料
     * @param userID
     */
    @Override
    public void getUserDetsils(String userID) {

        Map<String, String> prames = getDefaultPrames(NetContants.getInstance().URL_PERSONAL_CENTER());
        prames.put("to_userid", userID);
        prames.put("data_more", "0");//只获取基础数据
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_PERSONAL_CENTER(),
                new TypeToken<ResultInfo<FansInfo>>(){}.getType(), prames,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<FansInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showUserDetsilsError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<FansInfo> data) {
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
