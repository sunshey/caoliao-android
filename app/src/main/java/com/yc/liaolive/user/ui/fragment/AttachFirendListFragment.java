package com.yc.liaolive.user.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentRecyclerListBinding;
import com.yc.liaolive.interfaces.AttachFirendCliskListener;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.ui.activity.LiveRoomPullActivity;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.AttachFirendActivity;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.ui.adapter.AttachFirendListAdapter;
import com.yc.liaolive.ui.contract.AttachFirendContract;
import com.yc.liaolive.ui.presenter.AttachFirendPresenter;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@outlook.com
 * 2018/6/12 14:44
 * 粉丝、关注、好友
 * mObjectType：1：访问者用户本身，0：访问别人的
 * index：0：关注，1：粉丝 2：好友
 * mUrl：请求数据的路由
 */

public class AttachFirendListFragment extends BaseFragment<FragmentRecyclerListBinding,AttachFirendPresenter> implements AttachFirendContract.View,AttachFirendCliskListener, Observer {

    private int mPage=0;
    private AttachFirendListAdapter mAdapter;
    private String mAuthorID;
    private int mObjectType;//调用本界面的对象
    private String mUrl;//路由
    private int mIndex;//角标
    private boolean isRefresh=true;
    private DataChangeView mEmptyView;

    /**
     * 创造实例
     * @param authorID
     * @param url
     * @param index 实例化的界面角标
     * @return
     */
    public static AttachFirendListFragment newInstance(String authorID,String url,int index){
        AttachFirendListFragment attachFirendListFragment =new AttachFirendListFragment();
        Bundle bundle=new Bundle();
        bundle.putString(Constant.KEY_AUTHOR_ID,authorID);
        bundle.putString(Constant.KEY_URL,url);
        bundle.putInt(Constant.KEY_INDEX,index);
        attachFirendListFragment.setArguments(bundle);
        return attachFirendListFragment;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=bindingView&&null!=mPresenter&&!mPresenter.isLoading()){
            mPage=0;
            mEmptyView.showLoadingView();
            loadLists();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取出参数
        Bundle arguments = getArguments();
        if(null!=arguments) {
            mAuthorID = arguments.getString(Constant.KEY_AUTHOR_ID);
            mUrl = arguments.getString(Constant.KEY_URL);
            mIndex = arguments.getInt(Constant.KEY_INDEX,0);
            mObjectType=null!=mAuthorID&&TextUtils.equals(mAuthorID, UserManager.getInstance().getUserId())?1:0;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recycler_list;
    }

    @Override
    protected void initViews() {
        //1：我的粉丝
        bindingView.recyerView.setLayoutManager(new IndexLinLayoutManager(getActivity()));
        mAdapter = new AttachFirendListAdapter(null,this);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!= mAdapter){
                    List<FansInfo> data = mAdapter.getData();
                    if(null!=data&&data.size()>=10&&null!= mPresenter &&!mPresenter.isLoading()){
                        bindingView.swiperLayout.setRefreshing(false);
                        loadLists();
                    }else{
                        bindingView.recyerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!Utils.isCheckNetwork()){
                                    mAdapter.loadMoreFail();//加载失败
                                }else{
                                    mAdapter.loadMoreEnd();//加载为空
                                }
                            }
                        });
                    }
                }
            }
        }, bindingView.recyerView);
        //占位布局
        mEmptyView = new DataChangeView(getActivity());
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=0;
                mEmptyView.showLoadingView();
                loadLists();
            }
        });
        mAdapter.setEmptyView(mEmptyView);
        bindingView.recyerView.setAdapter(mAdapter);
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=0;
                loadLists();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new AttachFirendPresenter();
        mPresenter.attachView(this);
        ApplicationManager.getInstance().addObserver(this);
        //仅当此实例化Fragment为第一个的时候主动加载数据
        AttachFirendActivity activity = (AttachFirendActivity) getActivity();
        if(null!=activity&&activity.getCurrentItem()==mIndex){
            if(null!=mEmptyView) mEmptyView.showLoadingView();
            mPage=0;
            loadLists();
        }
    }

    @Override
    public void onDestroy() {
        if(null!= mEmptyView) mEmptyView.onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
        super.onDestroy();
    }

    /**
     * 加载粉丝列表
     */
    private void loadLists() {
        mPage++;
        mPresenter.getAttachFirends(mUrl,mAuthorID,mPage+"");
    }


    //======================================加载数据回调==============================================
    /**
     * 数据加载成功
     * @param data
     */
    @Override
    public void showList(List<FansInfo> data) {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!= mEmptyView) mEmptyView.stopLoading();
        if(null!= mAdapter){
            mAdapter.loadMoreComplete();//加载完成
            //替换为全新数据
            if(1==mPage){
                //只缓存自己的粉丝
                mAdapter.setNewData(data);
            }else{
                mAdapter.addData(data);
            }
        }
    }

    /**
     * 数据为空
     * @param data
     */
    @Override
    public void showListEmpty(String data) {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!= mEmptyView) mEmptyView.showEmptyView(mIndex==0?"不好意思，您还没有关注的人哦~":mIndex==1?"您的粉丝空空如也~":"您还未添加好友~",R.drawable.ic_list_empty_icon);
        if(null!= mAdapter){
            mAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            if(1==mPage){
                mAdapter.setNewData(null);
            }
        }
        //还原当前的页数
        if (mPage > 0) {
            mPage--;
        }
    }

    /**
     * 获取数据失败
     * @param code
     * @param data
     */
    @Override
    public void showListError(int code,String data) {
        ToastUtils.showCenterToast(data);
        if(1==mPage){
            if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        }
        if(null!= mAdapter){
            mAdapter.loadMoreFail();
            List<FansInfo> dataList = mAdapter.getData();
            if(mPage==1&&null==dataList||dataList.size()<=0){
                if(null!= mEmptyView) mEmptyView.showErrorView(data);
            }
        }
        if(mPage>0){
            mPage--;
        }
    }

    @Override
    public void showErrorView() {
        closeProgressDialog();
    }

    @Override
    public void complete() {

    }

    //==========================================点击事件=============================================

    /**
     * 用户头像点击
     * @param userID
     * @param view
     */
    @Override
    public void onUserHeadClick(String userID, View view) {
        PersonCenterActivity.start(getActivity(),userID);
    }

    /**
     * 条目点击
     * @param position
     * @param userID
     * @param view
     */
    @Override
    public void onItemClick(int position, String userID, View view) {
        PersonCenterActivity.start(getActivity(),userID);
    }

    /**
     * 状态
     * @param userInfo
     */
    @Override
    public void onUserStateClick(final FansInfo userInfo) {
        if(null==userInfo) return;
        //在房间中无需理会
        if(2==userInfo.getIdentity()) return;
        LiveRoomPullActivity activity = LiveRoomPullActivity.getInstance();
        if(null!=activity)activity.finish();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RoomExtra roomExtra=new RoomExtra();
                roomExtra.setUserid(userInfo.getUserid());
                roomExtra.setNickname(userInfo.getNickname());
                roomExtra.setAvatar(userInfo.getAvatar());
                roomExtra.setFrontcover(userInfo.getAvatar());
                LiveRoomPullActivity.start(getActivity(), roomExtra);
            }
        },200);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg&&arg instanceof String){
            String cmd= (String) arg;
            //关注列表发生了变化
            if(0==mIndex&&Constant.OBSERVER_CMD_FOLLOW_CHANGE.equalsIgnoreCase(cmd)){
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mPage=0;
                    loadLists();
                }
            }
        }
    }
}
