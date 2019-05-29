package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.bean.RoomTaskDataInfo;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.TaskRoomContract;

import org.json.JSONObject;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 任务中心
 */

public class RoomTaskPresenter extends RxBasePresenter<TaskRoomContract.View> implements TaskRoomContract.Presenter<TaskRoomContract.View> {


    private boolean isGet=false;
    private boolean isGetTask=false;

    public boolean isGet() {
        return isGet;
    }


    public boolean isGetTask() {
        return isGetTask;
    }

    /**
     * 获取直播间任务列表
     */
    @Override
    public void getTasks() {
        if(isGetTask) return;
        isGetTask=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_TASK());
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_TASK() ,
                new TypeToken<ResultInfo<ResultList<RoomTaskDataInfo>>>() {}.getType(), params, getHeaders(),isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<RoomTaskDataInfo>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                isGetTask=false;
                if(null!=mView) mView.showTaskError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<RoomTaskDataInfo>> data) {
                isGetTask=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showTasks(data.getData().getList());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showTaskEmpty();
                        }else{
                            if(null!=mView) mView.showTaskError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showTaskError(data.getCode(),data.getMsg());
                    }
                }else{
                    if(null!=mView) mView.showTaskError(-1,NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 领取任务奖励
     * @param taskInfo
     * @param callBackListener
     */
    @Override
    public void getTaskDraw(final TaskInfo taskInfo, final TaskRoomContract.OnCallBackListener callBackListener) {
        if(isGet) return;
        isGet=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_TASK_GET());
        params.put("task_id", String.valueOf(taskInfo.getApp_id()));
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_TASK_GET() ,
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params, getHeaders(),
                isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
                isGet=false;
            }

            @Override
            public void onError(Throwable e) {
                isGet=false;
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                isGet=false;
                if(null!=callBackListener){
                    if(null!=data){
                        if(NetContants.API_RESULT_CODE==data.getCode()){
                            callBackListener.onSuccess(taskInfo);
                        }else{
                            callBackListener.onFailure(data.getCode(),data.getMsg());
                        }
                    }else{
                        callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }
}
