package com.yc.liaolive.msg.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.msg.adapter.IndexMsgHeaderAdapter;
import com.yc.liaolive.msg.model.bean.CallMessageInfo;
import com.yc.liaolive.ui.activity.ContentFragmentActivity;
import com.yc.liaolive.ui.contract.IndexMsgContract;
import com.yc.liaolive.ui.presenter.IndexMsgPresenter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.OnlineUserActivity;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/16
 * 消息列表头部
 */

public class IndexMsgHeaderView extends LinearLayout implements IndexMsgContract.View {

    private IndexMsgPresenter mPresenter;
    private IndexMsgHeaderAdapter mAdapter;

    public IndexMsgHeaderView(@NonNull Context context) {
        this(context,null);
    }

    public IndexMsgHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_mag_header_layout,this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.view_recycler_view);
        recyclerView.setLayoutManager(new IndexLinLayoutManager(getContext(),IndexLinLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        List<CallMessageInfo> callMessageInfos=new ArrayList<>();
        //当前APP非客服用户，添加客服一栏
        if(!TextUtils.equals(UserManager.getInstance().getUserId(),UserManager.getInstance().getServerIdentify())){
            CallMessageInfo callMessageInfo = new CallMessageInfo();
            callMessageInfo.setId(6);
            callMessageInfo.setItemType(1);
            callMessageInfos.add(callMessageInfo);
        }
        mAdapter = new IndexMsgHeaderAdapter(callMessageInfos);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()){
                    CallMessageInfo info = (CallMessageInfo) view.getTag();
                    startActivity(info);
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
//        mPresenter = new IndexMsgPresenter();
//        mPresenter.attachView(this);
//        mPresenter.getMessageIndexList();
    }

    //1 我的通话 2我的预约  3我的钻石 4我的积分
    private void startActivity(CallMessageInfo info) {
        if(0==info.getItemType()){
            switch ((int) info.getId()) {
                //通话记录
                case Constant.INDEX_MSG_ITEM_CALL:
                    cleanNum(info.getId());
                    ContentFragmentActivity.start(getContext(),Constant.FRAGMENT_TYPE_MY_CALL,"我的通话",null, NetContants.getInstance().URL_GET_CALL_LET_LIST());
                    break;
                //我的钻石
                case Constant.INDEX_MSG_ITEM_MONERY:
                    ContentFragmentActivity.start(getContext(),Constant.FRAGMENT_TYPE_MY_MONERY,"我的钻石","4",null,"查看钻石余额");
                    break;
                //我的积分
                case Constant.INDEX_MSG_ITEM_INTEGRAL:
                    ContentFragmentActivity.start(getContext(),Constant.FRAGMENT_TYPE_MY_INTEGRAL,"我的积分","3",null,"查看积分余额");
                    break;
                //我的预约
                case Constant.INDEX_MSG_ITEM_MAKE:
                    cleanNum(info.getId());
                    ContentFragmentActivity.start(getContext(),Constant.FRAGMENT_TYPE_MY_MAKE,"我的预约",null,NetContants.getInstance().URL_RESEVER_LIST());
                    break;
                //用户列表
                case Constant.INDEX_MSG_ITEM_ONLINEUSER:
                    CaoliaoController.startActivity(OnlineUserActivity.class.getName());
                    break;
            }
        }
    }

    /**
     * 清除消息气泡
     * @param id
     */
    private void cleanNum(long id) {
        List<CallMessageInfo> data = mAdapter.getData();
        for (CallMessageInfo datum : data) {
            if(datum.getId()==id){
                datum.setNum(0);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }


    @Override
    public void showListResult(List<CallMessageInfo> data) {
        if(null!=mAdapter) mAdapter.setNewData(data);
        if(null!=mOnRefereshListener) mOnRefereshListener.onRereshFinish();
    }

    @Override
    public void showListResultEmpty() {
        if(null!=mAdapter) mAdapter.setNewData(null);
        if(null!=mOnRefereshListener) mOnRefereshListener.onRereshFinish();
    }

    @Override
    public void showListResultError(int code, String errorMsg) {
        if(null!=mOnRefereshListener) mOnRefereshListener.onRereshFinish();
    }

    public void onDestroy(){
        if(null!=mPresenter) mPresenter.detachView();
    }


    public void onResume() {
        if(null!=mPresenter&&!mPresenter.isGetIndexMsg()) mPresenter.getMessageIndexList();
    }

    public void onPause() {

    }
    //刷新事件回调
    public interface OnRefereshListener{
        void onRereshFinish();
    }
    private OnRefereshListener mOnRefereshListener;

    public void setOnRefereshListener(OnRefereshListener onRefereshListener) {
        mOnRefereshListener = onRefereshListener;
    }
}
