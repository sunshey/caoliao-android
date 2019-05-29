package com.yc.liaolive.search.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.databinding.ActivitySearchBinding;
import com.yc.liaolive.interfaces.AttachFirendCliskListener;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.ui.activity.LiveRoomPullActivity;
import com.yc.liaolive.search.adapter.SearchResultListAdapter;
import com.yc.liaolive.search.ui.contract.SearchContract;
import com.yc.liaolive.search.ui.presenter.SearchPresenter;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.CommonUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.layout.DataChangeView;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/4
 * 搜索
 */

public class SearchActivity extends BaseActivity <ActivitySearchBinding> implements AttachFirendCliskListener,  SearchContract.View {

    private SearchResultListAdapter mAdapter;
    private SearchPresenter mPresenter;
    private String mCurrentKey;
    private int mPage;
    private DataChangeView mDataChangeView;
    private InputMethodManager manager;

    public static void start(Activity context, View view) {
        Intent intent=new Intent(context,SearchActivity.class);
        if(null!=view){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, CommonUtils.getString(R.string.transition_movie_img));//与xml文件对应
            ActivityCompat.startActivity(context,intent, options.toBundle());
        }else{
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mPresenter = new SearchPresenter();
        mPresenter.attachView(this);
        manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public void initViews() {
        View.OnClickListener onCLickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_search:
                    case R.id.ic_search:
                        search();
                        break;
                    case R.id.btn_clean:
                        bindingView.etInput.setText("");
                        break;
                    case R.id.btn_back:
                        onBackPressed();
                        break;
                }

            }
        };
        bindingView.btnBack.setOnClickListener(onCLickListener);
        bindingView.btnClean.setOnClickListener(onCLickListener);
        bindingView.btnSearch.setOnClickListener(onCLickListener);
        bindingView.icSearch.setOnClickListener(onCLickListener);
        bindingView.etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(null!=s&&s.length()>0){
                    bindingView.btnClean.setVisibility(View.VISIBLE);
                    bindingView.btnSearch.setBackgroundResource(R.drawable.bt_bg_app_style_radius_noimal10);
                }else{
                    bindingView.btnClean.setVisibility(View.INVISIBLE);
                    bindingView.btnSearch.setBackgroundResource(R.drawable.bt_bg_gray_radius_noimal10);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bindingView.etInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    search();
                }
                return false;
            }
        });
    }

    /**
     * 开始搜索
     */
    private void search() {
        if(null==bindingView) return;
        if (null!=manager&&manager.isActive()) {
            manager.hideSoftInputFromWindow(bindingView.etInput.getApplicationWindowToken(), 0);
        }
        String key = bindingView.etInput.getText().toString().trim();
        if(TextUtils.isEmpty(key)) return;
        this.mCurrentKey=key;
        if(null!=mAdapter) mAdapter.setNewData(null);
        mPage=1;
        if(null!=mDataChangeView) mDataChangeView.showLoadingView();
        showProgressDialog("搜索中，请稍后..",true);
        if(null!=mPresenter) mPresenter.search(key,mPage);
    }


    @Override
    public void initData() {
        bindingView.recylerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        mAdapter = new SearchResultListAdapter(null,this);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!= mAdapter){
                    List<UserInfo> data = mAdapter.getData();
                    if(null!=data&&data.size()>=10&&null!=mPresenter&&!mPresenter.isLoading()&&null!=mCurrentKey){
                        mPage++;
                        mPresenter.search(mCurrentKey,mPage);
                    }else{
                        bindingView.recylerView.post(new Runnable() {
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
        }, bindingView.recylerView);
        //占位布局
        mDataChangeView = new DataChangeView(SearchActivity.this);
        mDataChangeView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDataChangeView.showLoadingView();
                if(null!=mPresenter&&null!=mCurrentKey){
                    mPage=1;
                    mPresenter.search(mCurrentKey,mPage);
                }
            }
        });
        mDataChangeView.showEmptyView("输入用户昵称或ID号搜索",R.drawable.ic_list_empty_icon);
        mAdapter.setEmptyView(mDataChangeView);
        bindingView.recylerView.setAdapter(mAdapter);
    }

    @Override
    public void onUserHeadClick(String userID, View view) {
        PersonCenterActivity.start(SearchActivity.this,userID);
    }

    @Override
    public void onItemClick(int position, String userID, View view) {
        PersonCenterActivity.start(SearchActivity.this,userID);
    }

    @Override
    public void onUserStateClick(FansInfo userInfo) {
        if(null!=userInfo){
            RoomExtra roomExtra=new RoomExtra();
            roomExtra.setUserid(userInfo.getUserid());
            roomExtra.setNickname(userInfo.getNickname());
            roomExtra.setAvatar(userInfo.getAvatar());
            roomExtra.setFrontcover(userInfo.getAvatar());
            LiveRoomPullActivity.start(SearchActivity.this, roomExtra);
        }
    }

    @Override
    public void showSearchResul(List<UserInfo> data) {
        closeProgressDialog();
        if(null!=mDataChangeView) mDataChangeView.stopLoading();
        if(null!=mAdapter){
            mAdapter.loadMoreComplete();
            if(1==mPage){
                mAdapter.setNewData(data);
            }else{
                mAdapter.addData(data);
            }
        }
    }

    @Override
    public void showSearchEmpty(String key) {
        closeProgressDialog();
        if(null!=mAdapter) mAdapter.loadMoreEnd();
        if(null!=mDataChangeView) mDataChangeView.showEmptyView("未搜索到与["+key+"]相关的用户",R.drawable.ic_list_empty_icon);
    }

    @Override
    public void showSearchError(int code, String errorMsg) {
        closeProgressDialog();
        if(null!=mAdapter) mAdapter.loadMoreFail();
        if(null==mAdapter.getData()||mAdapter.getData().size()<=0){
            if(null!=mDataChangeView) mDataChangeView.showErrorView(errorMsg);
        }else{
            if(null!=mDataChangeView) mDataChangeView.stopLoading();
        }
        if(mPage>0) mPage--;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null!=manager&&manager.isActive()) {
            manager.hideSoftInputFromWindow(bindingView.etInput.getApplicationWindowToken(), 0);
        }
        if(null!=mDataChangeView) mDataChangeView.onDestroy();
        if(null!=mPresenter) mPresenter.detachView();
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }
}
