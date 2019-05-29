package com.yc.liaolive.user.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.utils.security.Base64;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.user.bean.ZhimaParams;
import com.yc.liaolive.user.bean.ZhimaResult;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.contract.ZhimaContract;
import com.yc.liaolive.util.Logger;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@Outlook.com
 * 2019/1/25
 * 芝麻认证
 */

public class ZhimaPresenter extends RxBasePresenter<ZhimaContract.View> implements ZhimaContract.Presenter<ZhimaContract.View> {

    /**
     * 获取芝麻认证参数
     * @param nickName 姓名
     * @param number 身份证号
     * @param jumpUrl 回跳地址
     */
    @Override
    public void getZhimaParams(String nickName,String number,String jumpUrl) {
        if(isLoading()) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_ZHIMA_PARAMS());
        params.put("userid",UserManager.getInstance().getUserId());
        params.put("cert_name", nickName);
        params.put("cert_no",number);
        params.put("jump_url", jumpUrl);

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_ZHIMA_PARAMS(),
                new TypeToken<ResultInfo<ZhimaParams>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ZhimaParams>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading=false;
                        if(null!=mView) mView.showZhimaParamsError(-1,NetContants.NET_REQUST_ERROR);
                    }

                    @Override
                    public void onNext(ResultInfo<ZhimaParams> data) {
                        isLoading=false;
                        if(null!=data){
                            if(NetContants.API_RESULT_CODE == data.getCode()){
                                if(null!=data.getData()){
                                    if(null!=mView) mView.showZhimaParams(data.getData());
                                }else{
                                    if(null!=mView) mView.showZhimaParamsError(data.getCode(),data.getMsg());
                                }
                            }else{
                                if(null!=mView) mView.showZhimaParamsError(data.getCode(), NetContants.getErrorMsg(data));
                            }
                        }else{
                            if(null!=mView) mView.showZhimaParamsError(-1,  NetContants.NET_REQUST_ERROR);
                        }
                    }
                });
        addSubscrebe(subscribe);
    }

    /**
     * 校验芝麻认证结果
     * @param paramsContent
     */
    @Override
    public void checkedZhimaResult(String paramsContent) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_ZHIMA_RESULT());
        params.put("userid",UserManager.getInstance().getUserId());
        try {
            String encode = Base64.encode(paramsContent.getBytes("UTF-8"));
            params.put("content",encode);
            Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_ZHIMA_RESULT(),
                    new TypeToken<ResultInfo<ZhimaResult>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResultInfo<ZhimaResult>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(null!=mView) mView.showCheckedZhimaError(-1,NetContants.NET_REQUST_ERROR);
                        }

                        @Override
                        public void onNext(ResultInfo<ZhimaResult> data) {
                            if(null!=data){
                                if(NetContants.API_RESULT_CODE == data.getCode()){
                                    if(null!=data.getData()){
                                        if(null!=mView) mView.showCheckedZhimaResult(data.getData());
                                    }else{
                                        if(null!=mView) mView.showCheckedZhimaError(data.getCode(),data.getMsg());
                                    }
                                }else{
                                    if(null!=mView) mView.showCheckedZhimaError(data.getCode(), NetContants.getErrorMsg(data));
                                }
                            }else{
                                if(null!=mView) mView.showCheckedZhimaError(-1,  NetContants.NET_REQUST_ERROR);
                            }
                        }
                    });
            addSubscrebe(subscribe);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if(null!=mView) mView.showCheckedZhimaError(-1,  "参数编码错误");
        }
    }
}