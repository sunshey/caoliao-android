package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.bean.LogApi;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.IndexVideoListContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.SharedPreferencesUtil;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/9/25
 * 首页视频列表
 */
public class IndexVideoListPresenter extends RxBasePresenter<IndexVideoListContract.View> implements IndexVideoListContract.Presenter<IndexVideoListContract.View> {

    /**
     * 获取首页数据
     * @param url API
     * @param page 页数
     * @param fileType 文件类型 0：照片 1：视频
     * @param source 0：时间排序 1：浏览排序 2：喜欢排序 3加密多媒体文件 4推荐的多媒体文件
     */
    @Override
    public void getVideoLists(final String url, final int page, int fileType, final String source) {
        if(isLoading) return;
        isLoading=true;
        final long requstTime=System.currentTimeMillis();
        Map<String, String> params = getDefaultPrames(url);
        params.put("file_type",String.valueOf(fileType));
        params.put("source",source);
        params.put("page",String.valueOf(page));
        params.put("allow_ad",String.valueOf(0));//主页的数据默认都需要配置广告

        final Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(url,
                new TypeToken<ResultInfo<ResultList<PrivateMedia>>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultInfo<ResultList<PrivateMedia>>>() {
            @Override
            public void call(ResultInfo<ResultList<PrivateMedia>> data) {
                isLoading=false;
                if(null!=mView){
                    long nowTime=0;
                    long startMillis=0;
                    if(1==page && "4".equals(source)){
                        nowTime = System.currentTimeMillis();
                        //启动时间
                        startMillis = SharedPreferencesUtil.getInstance().getLong(Constant.START_APP_TIME, 0);
                    }
                    String state="success";
                    if(null!=data){
                        if(data.getCode()==NetContants.API_RESULT_CODE){
                            if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() > 0) {
//                                for (PrivateMedia privateMedia : data.getData().getList()) {
//                                    privateMedia.setFile_path("http://sc.wk2.com/upload/music/9/2017-11-17/5a0e4b51c34e7.mp3");
//                                }
                                mView.showLiveVideos(data.getData().getList());
                            } else if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() <= 0) {
                                mView.showVideoEmpty();
                            } else {
                                state="fail";
                                mView.showVideoError(-1, NetContants.NET_REQUST_JSON_ERROR);
                            }
                        }else{
                            if (null != mView) mView.showVideoError(data.getCode(), NetContants.getErrorMsg(data));
                        }
                    }else{
                        state="fail";
                        if (null != mView) mView.showVideoError(-1, NetContants.NET_REQUST_ERROR);
                    }
                    //主页小视频，才上报加载状态
                    if(1==page&& "4".equals(source) &&startMillis>0){
                        LogApi logInfo=new LogApi();
                        logInfo.setRequstUrl(url);
                        logInfo.setTotalTime(0==startMillis?0:nowTime-startMillis);
                        logInfo.setRequstTime(nowTime-requstTime);
                        logInfo.setState(state);
                        ActionLogInfo<LogApi> actionLogInfo=new ActionLogInfo();
                        actionLogInfo.setData(logInfo);
                        UserManager.getInstance().postActionState(NetContants.POST_ACTION_TYPE_INDEX,actionLogInfo,null);
                        SharedPreferencesUtil.getInstance().remove(Constant.START_APP_TIME);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
