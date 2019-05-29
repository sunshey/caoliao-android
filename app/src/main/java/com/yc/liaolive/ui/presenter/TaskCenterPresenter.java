package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.R;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.TaskCenterContract;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 任务中心
 */

public class TaskCenterPresenter extends RxBasePresenter<TaskCenterContract.View> implements TaskCenterContract.Presenter<TaskCenterContract.View> {


    private boolean isGet=false;
    private boolean isGetTask=false;

    public boolean isGet() {
        return isGet;
    }


    public boolean isGetTask() {
        return isGetTask;
    }

    /**
     * 获取任务奖励
     * @param type 0: 首页 1：直播间
     */
    @Override
    public void getTasks(String type) {

        if(isGetTask) return;
        isGetTask=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_TASK_CENTER_LIST());
        params.put("type",type);

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_TASK_CENTER_LIST() ,
                new TypeToken<ResultInfo<ResultList<TaskInfo>>>() {}.getType(), params, getHeaders(),isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<TaskInfo>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                isGetTask=false;
                if(null!=mView) mView.showTaskError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<TaskInfo>> data) {
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
     * 获取充值奖励
     */
    @Override
    public void getRechargeTasks() {
        List<TaskInfo> list=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            TaskInfo info3=new TaskInfo();
            info3.setIcon(R.drawable.ic_task_share);
            info3.setName("累计充值奖励"+(100+i)+"元");
            info3.setDesp("可领取100钻.");
            info3.setId(i);
            list.add(info3);
        }
        if(null!=mView) mView.showTasks(list);
    }

    /**
     * 领取奖励
     * @param task_id
     * @param callBackListene 结果回调
     */
    @Override
    public void getTaskDraw(String task_id, final RxBasePresenter.OnResqustCallBackListener callBackListene) {

        if(isGet) return;
        isGet=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_TASK_GET());
        params.put("task_id", task_id);
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_TASK_GET() ,
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params, getHeaders(),isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
                isGet=false;
            }

            @Override
            public void onError(Throwable e) {
                isGet=false;
                if(null!=mView) mView.showGetTaskError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                isGet=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=callBackListene) callBackListene.onSuccess("恭喜您！已成功领取礼包！");
                        if(null!=mView) mView.showGetTaskResult(data.getData().toString());
                    }else{
                        if(null!=mView) mView.showGetTaskError(data.getCode(),data.getMsg());
                    }
                }else{
                    if(null!=mView) mView.showGetTaskError(-1,NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }
}
