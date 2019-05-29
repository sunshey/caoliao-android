package com.yc.liaolive.view.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.media.view.MediaEmptyLayout;
import com.yc.liaolive.view.refresh.LoadingIndicatorView;

/**
 * TinyHung@Outlook.com
 * 2018/3/18
 * 通用的加载中，数据为空、加载失败、刷新重试 控件
 */

public class DataChangeView extends RelativeLayout implements View.OnClickListener {

    private ImageView mImageView;
    private TextView mTextView;
    private TextView descTV;
    private LoadingIndicatorView mIndicatorView;
    private View mContentView;
    private MediaEmptyLayout mMediaEmptyLayout;

    public DataChangeView(Context context) {
        super(context);
        init(context);
    }

    public DataChangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_list_empty, this);
        mImageView = findViewById(R.id.iv_view_icon);
        mTextView = findViewById(R.id.tv_view_content);
        descTV = findViewById(R.id.tv_view_desc);
        this.setOnClickListener(this);
        this.setClickable(false);
        mIndicatorView = (LoadingIndicatorView) findViewById(R.id.view_loading_view);
        mIndicatorView.hide();//默认是未加载状态
    }

    /**
     * 设置加载中动画颜色
     * @param color
     */
    public void setLoadingColor(int color){
        if(null!=mIndicatorView) mIndicatorView.setIndicatorColor(color);
    }

    /**
     * 加载中状态
     */
    public void showLoadingView(){
        this.setVisibility(View.VISIBLE);
        this.setClickable(false);
        if(null!=mImageView) mImageView.setImageResource(0);
        if(null!=mTextView) mTextView.setText("");
        if(null!=mIndicatorView&&mIndicatorView.getVisibility()!=VISIBLE) mIndicatorView.smoothToShow();
    }

    /**
     * 数据为空状态
     * @param content  要显示的文本
     * @param srcResID icon
     */
    public void showEmptyView(String content, int srcResID) {
        showEmptyState(content, null, srcResID);
    }

    public void showEmptyView(int content, int srcResID) {
        showEmptyState(getContext().getResources().getString(content), null, srcResID);
    }

    public void showEmptyView(boolean flag) {
        showEmptyState("没有数据", null, R.drawable.ic_list_empty_icon);
    }

    public void showEmptyView() {
        showEmptyState("没有数据", null, R.drawable.ic_list_empty_icon);
    }

    public void showEmptyState(String content, String desc, int srcResID) {
        this.setClickable(false);
        stopLoading();
        if (null != mTextView) mTextView.setText(content);
        if (descTV != null) {
            if (TextUtils.isEmpty(desc)) {
                descTV.setVisibility(View.GONE);
            } else {
                descTV.setVisibility(View.VISIBLE);
                descTV.setText(desc);
            }
        }

        if (null != mImageView) {
            if (0 != srcResID) {
                mImageView.setImageResource(srcResID);
            } else {
                mImageView.setImageResource(R.drawable.ic_list_empty_icon);
            }
        }
    }

    /**
     * 停止加载状态
     */
    public void stopLoading() {
        if (null != mIndicatorView&&mIndicatorView.getVisibility()==VISIBLE) mIndicatorView.hide();
    }

    /**
     * 重置所有状态
     */
    public void reset(){
        if (null != mIndicatorView&&mIndicatorView.getVisibility()==VISIBLE) mIndicatorView.smoothToHide();
        if(null!=mImageView) mImageView.setImageResource(0);
        if(null!=mTextView) mTextView.setText("");
    }

    /**
     * 加载失败状态
     * @param content  要显示的文本
     * @param srcResID icon
     */
    public void showErrorView(String content, int srcResID) {
        showErrorState(content, srcResID);
    }

    public void showErrorView(int content, int srcResID) {
        showErrorState(getContext().getResources().getString(content), srcResID);
    }

    public void showErrorView(String content) {
        showErrorState(content, R.drawable.ic_net_error);
    }

    public void showErrorView() {
        showErrorState(getResources().getString(R.string.net_error), R.drawable.ic_net_error);
    }

    public void showErrorState(String content, int srcResID) {
        stopLoading();
        if (null != mTextView) mTextView.setText(content);
        if (null != mImageView) {
            if (0 != srcResID) {
                mImageView.setImageResource(srcResID);
            } else {
                mImageView.setImageResource(R.drawable.ic_net_error);
            }
        }
        this.setClickable(true);
    }

    /**
     * 分离界面应用
     * @param contentView
     * @param msg
     */
    public void showLoading(View contentView, String msg) {
        this.mContentView=contentView;
        if(null!=mContentView) contentView.setVisibility(GONE);
        showLoadingView();
    }

    public void hide() {
        reset();
        if(null!=mContentView) mContentView.setVisibility(VISIBLE);
    }

    public void showNoData() {
        if(null!=mContentView) mContentView.setVisibility(GONE);
        showEmptyView();
    }

    public void showNoNet(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener=onRefreshListener;
        showErrorView();
    }

    @Override
    public void onClick(View v) {
        if (null != mOnRefreshListener) {
            mOnRefreshListener.onRefresh();
        }
    }

    @SuppressLint("WrongViewCast")
    public void setHeight(int heigth) {
        findViewById(R.id.view_root_view).getLayoutParams().height=heigth;
    }

    /**
     * 添加占位界面
     * @param mode
     * @param onFuctionListener
     */
    public void showMediaEmpty(int mode,OnFuctionListener onFuctionListener){
        if(null!=mMediaEmptyLayout){
            this.removeView(mMediaEmptyLayout);
            mMediaEmptyLayout=null;
        }
        mMediaEmptyLayout = new MediaEmptyLayout(getContext());
        mMediaEmptyLayout.setMode(mode);
        mMediaEmptyLayout.setOnFuctionListener(new MediaEmptyLayout.OnFuctionListener() {
            @Override
            public void onSubmit() {
                if(null!=onFuctionListener) onFuctionListener.onSubmit();
            }
        });
        this.addView(mMediaEmptyLayout,new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 移除占位界面
     */
    public void removeMediaEmpty(){
        if(null!=mMediaEmptyLayout){
            this.removeView(mMediaEmptyLayout);
            mMediaEmptyLayout=null;
        }
    }

    public interface OnFuctionListener{
        void onSubmit();
    }

    private OnFuctionListener mOnFuctionListener;

    public void setOnFuctionListener(OnFuctionListener onFuctionListener) {
        mOnFuctionListener = onFuctionListener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void onDestroy() {
        reset();
        mTextView = null;mImageView = null;mIndicatorView=null;mOnRefreshListener = null;
    }
}