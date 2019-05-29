package com.yc.liaolive.live.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.bean.RoomTaskDataInfo;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityRoomTaskBinding;
import com.yc.liaolive.live.manager.RoomTaskManager;
import com.yc.liaolive.manager.PlatformAccountBindHelp;
import com.yc.liaolive.ui.adapter.RoomTaskListAdapter;
import com.yc.liaolive.ui.contract.TaskRoomContract;
import com.yc.liaolive.ui.presenter.RoomTaskPresenter;
import com.yc.liaolive.user.manager.BindMobileManager;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.ModifyDataInfoActivity;
import com.yc.liaolive.util.DataFactory;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.layout.DataChangeView;

import java.util.List;

import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2018/11/29
 * 直播间任务中心
 */

public class RoomTaskActivity extends TopBaseActivity implements TaskRoomContract.View {

    private static final String TAG = "RoomTaskActivity";
    private ActivityRoomTaskBinding bindingView;
    private RoomTaskPresenter mPresenter;
    private RoomTaskListAdapter mAdapter;
    private DataChangeView mDataChangeView;
    public static final int ACTION_GIFT=-1;
    public static final int ACTION_SHARE=-2;
    private int action=0;

    public static void start(Context context) {
        Intent intent=new Intent(context,RoomTaskActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_room_task);
        setActivityLayoutParams();
        setFinishOnTouchOutside(true);//允许点击外部关闭Activity
        initViews();
        mPresenter = new RoomTaskPresenter();
        mPresenter.attachView(this);
        mPresenter.getTasks();
    }

    private void initViews() {
        mAdapter = new RoomTaskListAdapter(null);
        bindingView.recylerView.setLayoutManager(new LinearLayoutManager(RoomTaskActivity.this,LinearLayoutManager.VERTICAL,false));
        mAdapter.setOnGiftChangedListener(new RoomTaskListAdapter.OnGiftChangedListener() {
            @Override
            public void onDraw(final View view, int pisition, final TaskInfo taskInfo) {
                if(null==taskInfo) return;
                if(1==taskInfo.getComplete()) return;
                TextView textView=(TextView)view;
                String string = textView.getText().toString();
                //重复操作
                if(TextUtils.equals("已领取",string)){
                    ToastUtils.showCenterToast("不可重复领取");
                    return;
                }
                //前往领取任务
                if(0==taskInfo.getIs_get()){
                    showProgressDialog("领取中...",true);
                    if(null!=mPresenter&&!mPresenter.isGet()) mPresenter.getTaskDraw(taskInfo, new TaskRoomContract.OnCallBackListener() {
                        @Override
                        public void onSuccess(Object object) {
                            closeProgressDialog();
                            if(null!=object&&object instanceof TaskInfo) {
                                TaskInfo taskInfo = (TaskInfo) object;
                                taskInfo.setIs_get(1);
                                taskInfo.setComplete(1);
                                if (null != view && view instanceof TextView) {
                                    TextView textView = (TextView) view;
                                    textView.setText("已领取");
                                    textView.setBackgroundResource(R.drawable.bt_bg_app_gray_radius_noimal);
                                    VideoApplication.getInstance().setMineRefresh(true);
                                    action=taskInfo.getCoin();
                                    finish();
                                } else {
                                    mAdapter.notifyDataSetChanged();
                                }
                            }else{
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            closeProgressDialog();
                            ToastUtils.showCenterToast(errorMsg);
                        }
                    });
                    return;
                }
                //前往完成任务
                startTask(taskInfo);
            }
        });
        //占位布局
        mDataChangeView = new DataChangeView(RoomTaskActivity.this);
        mDataChangeView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter){
                    mDataChangeView.showLoadingView();
                    mPresenter.getTasks();
                }
            }
        });
        mDataChangeView.showLoadingView();
        mAdapter.setEmptyView(mDataChangeView);
        bindingView.recylerView.setAdapter(mAdapter);
    }

    /**
     * 前往完成任务
     * @param taskInfo
     */
    private void startTask(TaskInfo taskInfo) {
        switch (taskInfo.getApp_id()) {
            case TaskInfo.TASK_ACTION_MODIFY_NAME:
                ModifyDataInfoActivity.start(RoomTaskActivity.this,getResources().getString(R.string.edit_nickname), UserManager.getInstance().getNickname(),12,null,Constant.MODITUTY_KEY_NICKNAME);
                break;
            case TaskInfo.TASK_ACTION_BIND_PHONE:
                BindMobileManager.getInstance().goBingMobile(false).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                     if(null!=aBoolean&&aBoolean&&null!=mPresenter){
                         mPresenter.getTasks();
                     }
                    }
                });
                break;
            case TaskInfo.TASK_ACTION_BIND_QQ:
                bindPlatfromAccount(Constant.LOGIN_TYPE_QQ);
                break;
            case TaskInfo.TASK_ACTION_BIND_WEXIN:
                bindPlatfromAccount(Constant.LOGIN_TYPE_WEXIN);
                break;
            case TaskInfo.TASK_ACTION_LOOK_LIVE:
                finish();
                break;
            case TaskInfo.TASK_ACTION_SEND_GIFT:
                action=ACTION_GIFT;
                finish();
                break;
            case TaskInfo.TASK_ACTION_SHARE:
                action=ACTION_SHARE;
                finish();
                break;
        }
    }

    /**
     * 绑定第三方账号
     * @param platfromID
     */
    private void bindPlatfromAccount(int platfromID) {
        PlatformAccountBindHelp.getInstance().attachActivity(RoomTaskActivity.this).setOnBindChangedListener(new PlatformAccountBindHelp.OnBindChangedListener() {
            @Override
            public void onSuccess(int platfromID,String content) {
                if(null!=mPresenter) mPresenter.getTasks();
            }

            @Override
            public void onFailure(int code, String errorMsg) {

            }
        }).onBindPlatformAccount(platfromID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constant.REGISTER_MODIFY_NICKNAME_REQUST==requestCode&&resultCode==Constant.REGISTER_MODIFY_NICKNAME_RESULT){
            if(null!=mPresenter) mPresenter.getTasks();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mDataChangeView) mDataChangeView.stopLoading();
        if(null!=mAdapter) mAdapter.setNewData(null);
        mDataChangeView=null;mAdapter=null;
        RoomTaskManager.getInstance().getRoomTaskSubject().onNext(action);
        RoomTaskManager.getInstance().getRoomTaskSubject().onCompleted();
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showTasks(List<RoomTaskDataInfo> data) {
        if(null!=mDataChangeView) mDataChangeView.stopLoading();
        List<TaskInfo> taskInfos= DataFactory.shortList(data);
        if(null!=mAdapter) mAdapter.setNewData(taskInfos);
    }

    @Override
    public void showTaskEmpty() {
        if(null!=mDataChangeView) mDataChangeView.showEmptyView(false);
        if(null!=mAdapter) mAdapter.setNewData(null);
    }

    @Override
    public void showTaskError(int code, String errorMsg) {
        if(null!=mDataChangeView) mDataChangeView.showErrorView();
    }
}
