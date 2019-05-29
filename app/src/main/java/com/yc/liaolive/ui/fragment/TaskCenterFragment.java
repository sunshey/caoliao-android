package com.yc.liaolive.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.OlderExtra;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.bean.VipListInfo;
import com.yc.liaolive.databinding.FragmentListLayoutBinding;
import com.yc.liaolive.pay.PayConfig;
import com.yc.liaolive.pay.PayUtils;
import com.yc.liaolive.pay.alipay.IPayCallback;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.pay.model.bean.CheckOrderBean;
import com.yc.liaolive.recharge.manager.BuyVipPresenter;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;
import com.yc.liaolive.ui.adapter.TaskListAdapter;
import com.yc.liaolive.ui.contract.BuyVipContract;
import com.yc.liaolive.ui.contract.TaskCenterContract;
import com.yc.liaolive.ui.dialog.CommenNoticeDialog;
import com.yc.liaolive.ui.dialog.FirstChargeDialog;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.ui.presenter.TaskCenterPresenter;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.PayWebView;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/6
 * 任务 礼包中心
 */

public class TaskCenterFragment extends BaseFragment <FragmentListLayoutBinding,TaskCenterPresenter> implements TaskCenterContract.View, BuyVipContract.View {

    private static final String TAG = "TaskCenterFragment";
    private TaskListAdapter mAdapter;
    private BuyVipPresenter mRechargePresenter;
    private int payway;
    private DataChangeView mDataChangeView;

    @Override
    protected void initViews() {
        bindingView.recylerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mAdapter = new TaskListAdapter(null);
        mAdapter.setOnGiftChangedListener(new TaskListAdapter.OnGiftChangedListener() {
            @Override
            public void onDraw(final View view, int position, final TaskInfo taskInfo) {
                if(null==taskInfo) return;
                if(1==taskInfo.getComplete()) return;//已完成
                //去完成任务
                if(1==taskInfo.getIs_get()) {
                    startActivity(taskInfo);
                //去领取领取任务
                }else{
                    if(null!=mPresenter&&!mPresenter.isGet()) mPresenter.getTaskDraw(String.valueOf(taskInfo.getApp_id()), new RxBasePresenter.OnResqustCallBackListener() {
                        @Override
                        public void onSuccess(String data) {
                            taskInfo.setIs_get(1);
                            if(null!=view&&view instanceof TextView){
                                TextView textView=(TextView)view;
                                textView.setText("已领取");
                                textView.setBackgroundResource(R.drawable.bt_bg_app_gray_radius_noimal);
                            }else{
                                mAdapter.notifyDataSetChanged();
                            }
                            CommenNoticeDialog.getInstance(getActivity()).setTipsData("领取通知",data,"确定").setOnSubmitClickListener(new CommenNoticeDialog.OnSubmitClickListener() {
                                @Override
                                public void onSubmit() {
                                    //处理确认点击事件
                                }
                            }).show();
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            ToastUtils.showCenterToast(errorMsg);
                        }
                    });
                }
            }
        });

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mAdapter.loadMoreEnd();
            }
        },bindingView.recylerView);

        //占位布局
        mDataChangeView = new DataChangeView(getActivity());
        mDataChangeView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isGetTask()){
                    mDataChangeView.showLoadingView();
                    mPresenter.getTasks("0");
                }
            }
        });
        mDataChangeView.showLoadingView();
        mAdapter.setEmptyView(mDataChangeView);
        bindingView.recylerView.setAdapter(mAdapter);
        //刷新监听
        bindingView.swiperLayout.getResources().getColor(R.color.black);
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isGetTask()){
                    mPresenter.getTasks("0");
                }else{
                    bindingView.swiperLayout.setRefreshing(false);
                    ToastUtils.showCenterToast("正在刷新..");
                }
            }
        });

        bindingView.payWebView.setOnFunctionListener(new PayWebView.OnFunctionListener() {
            @Override
            public void weXinPay(String url) {
                openWxpay(url);
            }

            @Override
            public void aliPay(String url) {
                openAlipay(url);
            }
        });
    }

    /**
     * 打开活动
     * @param taskInfo
     */
    private void startActivity(TaskInfo taskInfo){
        if(TextUtils.isEmpty(taskInfo.getActivity())) return;
        if("com.yc.liaolive.ui.activity.RechargeActivity".equalsIgnoreCase(taskInfo.getActivity())){
            //首充任务
            FirstChargeDialog.getInstance(getActivity()).setOnSelectedListener(new FirstChargeDialog.OnSelectedListener() {
                @Override
                public void onSelected(int payType) {
                    createRecharge(payType);
                }
            }).show();
            return;
        }
        Class clazz;
        try {
            clazz = Class.forName(taskInfo.getActivity());
            Intent intent=new Intent(getActivity(),clazz);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null!=bindingView){
            String olderSn = bindingView.payWebView.getOlderSn();
            if(!TextUtils.isEmpty(olderSn)){
                VideoApplication.getInstance().setMineRefresh(true);
                if(null!=mRechargePresenter) {
                    mRechargePresenter.setCount(3);//还原查询次数
                    mRechargePresenter.checkOrder(olderSn);
                }
            }
        }
    }

    /**
     * 准备充值
     * @param payType
     */
    private void createRecharge(int payType) {
        payway=payType;
        RechargeGoodsInfo rechargeGoodsInfo=new RechargeGoodsInfo();
        rechargeGoodsInfo.setId(3);
        rechargeGoodsInfo.setName("每日充值");
        rechargeGoodsInfo.setPrice("30.00");
        List<OlderExtra> olderExtras = new ArrayList<>();
        OlderExtra extra = new OlderExtra();
        extra.setGood_id(String.valueOf(3));
        extra.setNum(1);
        olderExtras.add(extra);
//        String s1 = com.alibaba.fastjson.JSONArray.toJSONString(olderExtras);
        if(null!=mRechargePresenter){
            mRechargePresenter.createOrder(0 == payType ? PayConfig.ali_pay : PayConfig.wx_pay, new Gson().toJson(olderExtras), rechargeGoodsInfo);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new TaskCenterPresenter();
        mPresenter.attachView(this);
        mPresenter.getTasks("0");
        mRechargePresenter = new BuyVipPresenter(getActivity());
        mRechargePresenter.attachView(this);
    }

    /**
     * 开始支付流程
     * @param order
     */
    private void pay(OrderInfo order) {
        if (order.getPayway_info() != null && 2 == order.getPayway_info().getTrade_type()) {
            //H5支付
            if(null != bindingView) {
                bindingView.payWebView.starPlay(order.getCharge_order_sn(), order.getPayurl(), order.getPayway_info().getAuth_domain());
            }
        } else if(!TextUtils.isEmpty(order.getPayurl()) && !order.getPayurl().startsWith("alipay_sdk")){
            //微信支付
            if(order.getPayurl().startsWith("weixin://")){
                openWxpay(order.getPayurl());
                if(null!=bindingView) bindingView.payWebView.setOlderSn(order.getCharge_order_sn());
            }
        }else{
            if(null!=bindingView) bindingView.payWebView.setOlderSn("");
            PayUtils.getInstance().get(getActivity()).pay(payway, order, new IPayCallback() {
                @Override
                public void onSuccess(OrderInfo orderInfo) {
                    VideoApplication.getInstance().setMineRefresh(true);
                    if(null!=mRechargePresenter) mRechargePresenter.checkOrder(orderInfo.getCharge_order_sn());
                }

                @Override
                public void onFailure(OrderInfo orderInfo) {
                    if(null!=mRechargePresenter) mRechargePresenter.dissmis();
                }

                @Override
                public void onCancel(OrderInfo orderInfo) {
                    if(null!=mRechargePresenter) mRechargePresenter.dissmis();
                }
            });
        }
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showTasks(List<TaskInfo> data) {
        if(null!=mDataChangeView) mDataChangeView.stopLoading();
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mAdapter) mAdapter.setNewData(data);
    }

    @Override
    public void showTaskEmpty() {
        if(null!=mDataChangeView) mDataChangeView.showEmptyView(false);
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
    }

    @Override
    public void showTaskError(int code, String errorMsg) {
        ToastUtils.showCenterToast(errorMsg);
        if(null!=mDataChangeView) mDataChangeView.showErrorView();
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
    }

    @Override
    public void showGetTaskResult(String data) {

    }

    @Override
    public void showGetTaskError(int code, String errorMsg) {
        ToastUtils.showCenterToast(errorMsg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=bindingView) bindingView.payWebView.onDestroy();
        if(null!=mDataChangeView) mDataChangeView.onDestroy();
        if(null!=mRechargePresenter) mRechargePresenter.detachView();
        if(null!=mPresenter) mPresenter.detachView();
        payway=0;
    }

    @Override
    public void showOrderSuccess(OrderInfo data, String rechargeGoodsInfo) {
        pay(data);
    }

    @Override
    public void showCreateOlderError(int code, String errorMsg) {
        closeProgressDialog();
    }

    @Override
    public void showCantPayError(int code, String msg) {
        QuireDialog.getInstance(getActivity()).showCloseBtn(false)
                .showTitle(false)
                .setSubmitTitleText("确定")
                .setContentText(msg)
                .setCancelTitleVisible(View.GONE)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        super.onConsent();
                        if(null!=mRechargePresenter) mRechargePresenter.dissmis();
                    }
                }).setDialogCanceledOnTouchOutside(false).setDialogCancelable(false).show();
    }

    @Override
    public void showRechardeResult(CheckOrderBean data) {
        if(null!=bindingView) bindingView.payWebView.setTag(null);
        if(null!=mRechargePresenter) mRechargePresenter.dissmis();
        ToastUtils.showCenterToast("交易成功");
        //支付成功，自动检查任务
        if(null!=bindingView){
            bindingView.swiperLayout.setRefreshing(true);
        }
        if(null!=mPresenter) mPresenter.getTasks("0");
    }

    @Override
    public void showRechardeError(int code, String msg) {
        closeProgressDialog();
    }

    @Override
    public void showVipLits(VipListInfo vipListInfo) {

    }


    @Override
    public void hide() {

    }

    @Override
    public void showNoNet() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showNoData() {

    }
}
