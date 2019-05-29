package com.yc.liaolive.mine.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import com.tencent.TIMConversationType;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.CreateRoomInfo;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.PersonCenterInfo;
import com.yc.liaolive.bean.TabMineUserInfo;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.FragmentIndexMineBinding;
import com.yc.liaolive.index.ui.MainActivity;
import com.yc.liaolive.live.ui.activity.LiveRoomPusherActivity;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.mine.adapter.IndexMineAdapter;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.msg.view.ListEmptyFooterView;
import com.yc.liaolive.ui.contract.PersonCenterContract;
import com.yc.liaolive.ui.dialog.CommenNoticeDialog;
import com.yc.liaolive.ui.dialog.LoginAwardDialog;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.ui.presenter.PersonCenterPresenter;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.UserAuthenticationActivity;
import com.yc.liaolive.user.ui.ZhimaAuthentiActivity;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.IndexMineHeaderLayout;
import com.yc.liaolive.view.widget.SwitchButton;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * TinyHung@Outlook.com
 * 2018/5/24
 * 首页-个人中心
 */

public class IndexMineFragment extends BaseFragment<FragmentIndexMineBinding, PersonCenterPresenter> implements Observer, PersonCenterContract.View {

    private static final String TAG = "IndexMineFragment";
    private IndexMineAdapter mAdapter;
    private boolean isGetTask = false;
    private IndexMineHeaderLayout mHeaderLayout;
    private DataChangeView mEmptyView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index_mine;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApplicationManager.getInstance().addObserver(this);
        mPresenter = new PersonCenterPresenter(getActivity());
        mPresenter.attachView(this);
        mPresenter.getPersonCenterInfo(UserManager.getInstance().getUserId());
        mPresenter.getItemList();
    }

    @Override
    protected void initViews() {
        bindingView.recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new IndexMineAdapter(null);
        mHeaderLayout = new IndexMineHeaderLayout(getActivity());
        mAdapter.addHeaderView(mHeaderLayout);
        mAdapter.addFooterView(new ListEmptyFooterView(getActivity()));
        //条目点击事件
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null==view.getTag()) return;
                TabMineUserInfo indexMineUserInfo= (TabMineUserInfo) view.getTag();
                startActivity(indexMineUserInfo);
            }
        });
        //条目中子控件点击事件
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    //在线设置
                    case R.id.item_tab_layout:
                        changedExcumeMode(view);
                        break;
                }
            }
        });
        //添加占位布局
        mEmptyView = new DataChangeView(getActivity());
        mEmptyView.showLoadingView();
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEmptyView.showLoadingView();
                mPresenter.getPersonCenterInfo(UserManager.getInstance().getUserId());
                mPresenter.getItemList();
            }
        });
        mAdapter.setEmptyView(mEmptyView);

        ListEmptyFooterView listEmptyFooterView = new ListEmptyFooterView(getActivity());
        listEmptyFooterView.showEmptyView(true);
        //添加一个底部
        mAdapter.addFooterView(listEmptyFooterView);
        bindingView.recylerView.setAdapter(mAdapter);
//        //标题栏处理，预留最小依附阈值，剩余的计算滑动阈值
//        bindingView.llTopBar.setBackgroundResource(R.drawable.home_top_bar_bg_shape);
//        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        bindingView.collapseToolbar.measure(width, width);
//        //滚动高度的阈值应该是总高度-toolbar-statusHeight
//        bindingView.llTopBar.measure(width, width);
//        //滚动高度的阈值应该是总高度-toolbar-bindingView.llTopBar的高度
//        mReTopBarHeight = bindingView.collapseToolbar.getMeasuredHeight() - bindingView.llTopBar.getMeasuredHeight();
//        //设置最小的停靠距离
//        bindingView.collapseToolbar.setMinimumHeight(bindingView.llTopBar.getMeasuredHeight() + ScreenUtils.dpToPxInt(10f));
//       //滚动的监听
//        bindingView.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                int abs = Math.abs(verticalOffset);
//                float scale = (float) abs / mReTopBarHeight;
//                float alpha = (scale * 255);
//                //标题栏背景颜色
//                bindingView.llTopBar.getBackground().setAlpha((int) alpha);
//                //标题栏
//                bindingView.topTitle.setAlpha(scale);
//                if (abs == appBarLayout.getTotalScrollRange()) {
//                    mAdapter.setCorner(false);
//                } else {
//                    mAdapter.setCorner(true);
//                }
//                bindingView.btnEdit.setColorFilter(ColorUtils.caculateColor(getResources().getColor(R.color.white), getResources().getColor(R.color.black), scale));
//                //用户信息图层网上滑动，所以是相反的透明度,两倍的速度渐变
//                if (abs >= mReTopBarHeight / 2) {
//                    //标题栏黑色字体
//                    setStatusBarBg(false);
//                } else {
//                    //标题栏白色字体
//                    setStatusBarBg(true);
//                }
//            }
//        });

        //下拉刷新
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getPersonCenterInfo(UserManager.getInstance().getUserId());
                mPresenter.getItemList();
            }
        });
    }

    /**
     * 勿扰状态更新
     */
    private void changedExcumeMode(final View itemView) {

        if(!UserManager.getInstance().isSetExcumeMode()){
            showProgressDialog("设置中,请稍后...");
            int excuse=(1==UserManager.getInstance().getQuite()?0:1);
            UserManager.getInstance().setExcuseMode(UserManager.getInstance().getUserId(),excuse, new UserServerContract.OnNetCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    closeProgressDialog();
                    String msg=(1==UserManager.getInstance().getQuite()?"已关闭视频通话":"已开启视频通话");
                    ToastUtils.showCenterToast(msg);
                    VideoCallManager.getInstance().setCallStatus(0==UserManager.getInstance().getQuite()? VideoCallManager.CallStatus.CALL_FREE:VideoCallManager.CallStatus.CALL_OFFLINE);
                    if(null!=itemView){
                        SwitchButton switchButton = itemView.findViewById(R.id.item_switch_btn);
                        if(null != switchButton) switchButton.setChecked(0==UserManager.getInstance().getQuite());
                        if(null!=mPresenter) mPresenter.getItemList();
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    closeProgressDialog();
                    //用户需要芝麻认证
                    if(NetContants.API_RESULT_NO_BIND_ZHIMA==code){
                        showVerificationZhima(errorMsg);
                        return;
                    }
                    ToastUtils.showCenterToast(errorMsg);
                }
            });
        }
    }

    /**
     * 路由新的活动
     * @param item
     */
    private void startActivity(TabMineUserInfo item) {
        if(!TextUtils.isEmpty(item.getJump_url())){
            //跳转的地址
            String aClass = Utils.getClassName(item.getJump_url());
            //参数
            Map<String,String> paramsMap = Utils.getParamsExtra(item.getJump_url());
            //小额贷SDK或原生版
            if("3".equals(item.getType())){
                CaoliaoController.start(item.getJump_url(),true,"mine_");
                return;
            }
            //WEB，小额贷WEB版
            if("4".equals(item.getType())){
                CaoliaoController.start(item.getJump_url(),true,"dai_");
                return;
            }
            //直播间
            if(TextUtils.equals(Constant.CLASS_NAME_LIVE_PUSHER,aClass)){
                if(!UserManager.getInstance().isAuthenState()){
                    ToastUtils.showCenterToast("请先认证主播身份或等待身份审核通过后开始直播");
                    return;
                }
                startPublish();
                return;
            }
            //美颜
            if(TextUtils.equals(Constant.CLASS_NAME_BEAUTY_SETTING,aClass)){
                if(null!=getActivity()&&getActivity() instanceof MainActivity){
                    MainActivity activity = (MainActivity) getActivity();
                    activity.requstPermissions();
                }
                return;
            }
            //客服
            if(TextUtils.equals(Constant.CLASS_NAME_SERVER,aClass)){
                ChatActivity.navToChat(getActivity(), UserManager.getInstance().getServerIdentify(), true, TIMConversationType.C2C);
                return;
            }
            //主播认证
            if(TextUtils.equals(Constant.CLASS_NAME_AUTHENTICATION,aClass)){
                if(UserManager.getInstance().isAuthenState()){
                    ToastUtils.showCenterToast("已通过主播认证");
                    return;
                }
                if(1==UserManager.getInstance().getIdentity_audit()){
                    ToastUtils.showCenterToast("正在审核中");
                    return;
                }
                startActivity(new Intent(getActivity(),UserAuthenticationActivity.class));
                return;
            }
            //设置中心
            if(TextUtils.equals(Constant.CLASS_NAME_SETTING,aClass)){
                if(null!=getActivity()&&getActivity() instanceof MainActivity){
                    ((MainActivity) getActivity()).startSetting();
                }
                return;
            }
            try {
                //其他携带参数的普通跳转
                Intent intent=new Intent();
                intent.setClassName(getActivity(),aClass);
                if(null!=paramsMap){
                    Set<String> strings = paramsMap.keySet();
                    for (String key : strings) {
                        String value = paramsMap.get(key);
                        intent.putExtra(key,value);
                    }
                }
                startActivity(intent);
            }catch (RuntimeException e){

            }
        }
    }

    public void startCameraPreview() {
//        RXPermissionManager.getInstance(getContext())
//                .requestForPermission(RXPermissionManager.PERMISSION_CAMERA)
//                .compose(RXPermissionManager.getInstance(getContext())
//                        .defultHandler(getActivity()))
//                .subscribe(new Action1<Boolean>() {
//                    @Override
//                    public void call(Boolean aBoolean) {
//                        if (aBoolean) {
//                            CaoliaoController.startActivity(BeautySettingActivity.class.getName());
//                        } else {
//                            RXPermissionManager.getInstance(getContext()).showRejectDialog(getActivity(),
//                                    "没有获取到摄像头权限，无法进入设置美颜。请在应用权限中打开权限", true);
//                        }
//                    }
//                });
    }

    /**
     * 来自首页的刷新
     * 刷新状态需要时间显示，等待动画完成再开始刷新
     */
    @Override
    public void fromMainUpdata() {
        super.fromMainUpdata();
        if (null == bindingView) return;
        if (null != mPresenter && !mPresenter.isLoading()) {
            bindingView.recylerView.scrollToPosition(0);
            bindingView.swiperLayout.setRefreshing(true);
            mPresenter.getPersonCenterInfo(UserManager.getInstance().getUserId());
            mPresenter.getItemList();
        }
    }

    /**
     * 开启直播间
     */
    private void startPublish() {
        showProgressDialog("请稍后...");
        UserManager.getInstance().createRoom(new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null==getActivity()) return;
                if(!getActivity().isFinishing()) {
                    closeProgressDialog();
                    if (null != object && object instanceof CreateRoomInfo) {
                        CreateRoomInfo createRoomInfo = (CreateRoomInfo) object;
                        LiveRoomPusherActivity.statrPublish(getActivity(), createRoomInfo);
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if(null==getActivity()) return;
                if(!getActivity().isFinishing()){
                    closeProgressDialog();
                    //如果用户被封禁直播权限了
                    if(code==Constant.REQUST_RESULT_CODE_ROOM_CLODE){
                        CommenNoticeDialog.getInstance(getActivity()).setTipsData("创建直播间失败", errorMsg, "关闭").show();
                        return;
                    }
                    //用户需要芝麻认证
                    if(NetContants.API_RESULT_NO_BIND_ZHIMA==code){
                        showVerificationZhima(errorMsg);
                        return;
                    }
                    ToastUtils.showCenterToast(errorMsg);
                }
            }
        });
    }

    /**
     * 芝麻认证对话框
     * @param errorMsg
     */
    private void showVerificationZhima(String errorMsg) {
        if(null!=getActivity()){
            QuireDialog.getInstance(getActivity())
                    .setTitleText("温馨提示")
                    .setContentText(errorMsg)
                    .setSubmitTitleText("去认证")
                    .setCancelTitleText("取消")
                    .setDialogCanceledOnTouchOutside(true)
                    .setDialogCancelable(true)
                    .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                        @Override
                        public void onConsent() {
                            CaoliaoController.startActivity(ZhimaAuthentiActivity.class.getName());
                        }

                        @Override
                        public void onRefuse() {

                        }
                    }).show();
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (VideoApplication.getInstance().isMineRefresh() && null != mPresenter) {
            mPresenter.getPersonCenterInfo(UserManager.getInstance().getUserId());
            mPresenter.getItemList();
            VideoApplication.getInstance().setMineRefresh(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
        if (null != mPresenter) mPresenter.detachView();
        isGetTask = false;
    }

    /**
     * 检查用户任务
     */
    private void checkedUserTask() {
        isGetTask = true;
        //查询用户的新手奖励任务是否存在
        UserManager.getInstance().getTasks("2", new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null!=object && object instanceof List){
                    List<TaskInfo> taskInfos= (List<TaskInfo>) object;
                    //会员任务
                    for (TaskInfo task : taskInfos) {
                        if (Constant.APP_TASK_VIP == task.getApp_id()) {
                            //未完成并且为领取的状态下自动领取新奖励
                            if (0 == task.getComplete() && 0 == task.getIs_get()) {
                                UserManager.getInstance().drawTaskAward(task, new UserServerContract.OnNetCallBackListener() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        if (null != object && object instanceof TaskInfo) {
                                            TaskInfo taskInfo = (TaskInfo) object;
                                            LoginAwardDialog.getInstance(getActivity(), "今日的" + taskInfo.getCoin() + "钻石已送达!", taskInfo.getCoin()).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int code, String errorMsg) {
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {

            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        if (null != arg) {
            if (arg instanceof Integer) {
                Integer integer = (Integer) arg;
                if (integer == Constant.OBSERVABLE_ACTION_UNLOGIN) {
                    //用户登出了
                }
            } else if (arg instanceof FansInfo) {
                FansInfo fansInfo = (FansInfo) arg;
                UserManager.getInstance().setLoginUserInfo(fansInfo);
                if(null!=mHeaderLayout) mHeaderLayout.setUserData(fansInfo);
                if (!isGetTask) {
                    checkedUserTask();
                }
            } else if (arg instanceof String) {
                String success = (String) arg;
                if (Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED_NET.equals(success)) {
                    mPresenter.getPersonCenterInfo(UserManager.getInstance().getUserId());
                    mPresenter.getItemList();
                    //身份审核信息已通过
                }else if(Constant.OBSERVER_CMD_IDENTITY_AUTHENTICATION_SUCCESS.equals(success)){
                    if(null!=mPresenter&&null!=getActivity()){
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mPresenter.getPersonCenterInfo(UserManager.getInstance().getUserId());
                                    mPresenter.getItemList();
                                }
                            });
                        }catch (RuntimeException e){

                        }
                    }
                }
            }
        }
    }

    /**
     * 用户信息回调
     * @param data
     */
    @Override
    public void showPersonInfo(final PersonCenterInfo data) {
        if(null == bindingView) return;
        if(null!=mEmptyView) mEmptyView.stopLoading();
        bindingView.swiperLayout.setRefreshing(false);
        VideoCallManager.getInstance().setCallStatus(0==data.getQuite()? VideoCallManager.CallStatus.CALL_FREE:VideoCallManager.CallStatus.CALL_OFFLINE);
        UserManager.getInstance().setLoginUserInfo(data);
        UserManager.getInstance().setUploadImageCount(data.getImage_max_length());
        UserManager.getInstance().setDiamonds(data.getPintai_coin() + data.getRmb_coin());
        UserManager.getInstance().setVipEndtime(data.getVip_end_time() * 1000);
        if(null!=mHeaderLayout) mHeaderLayout.setUserData(data);
        UserManager.getInstance().syncUserInfoToIM();
        if (!isGetTask) {
            checkedUserTask();
        }
    }

    /**
     * 获取用户信息失败了
     * @param code
     * @param msg
     */
    @Override
    public void showPersonInfoError(int code, String msg) {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);

    }

    @Override
    public void showPersonList(List<TabMineUserInfo> data) {
        if (mAdapter != null) mAdapter.setNewData(data);
    }

    @Override
    public void showPersonListError(int code, String msg) {
        if(null!=mEmptyView&&null!=mAdapter&&mAdapter.getData().size()<=0) mEmptyView.showErrorView(msg);
    }

    @Override
    public void showErrorView() {}
    @Override
    public void complete() {}
}