package com.yc.liaolive.live.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.view.layout.DataChangeView;

/**
 * TinyHung@Outlook.com
 * 2018/8/23
 * 关注界面的底部View
 */

public class IndexFollowFooterView extends RelativeLayout {

    private DataChangeView mChangeView;

    public IndexFollowFooterView(Context context) {
        super(context);
        init(context);
    }

    public IndexFollowFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.index_follow_footer_layout,this);
        mChangeView = (DataChangeView) findViewById(R.id.view_loading_layout);
        mChangeView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mOnRefreshListener) mOnRefreshListener.onRefresh();
            }
        });
    }

    /**
     * 加载中
     */
    public void showLoadingView() {
        if(null!=mChangeView) mChangeView.showLoadingView();
    }

    /**
     * 停止加载中
     */
    public void stopLoadingView() {
        if(null!=mChangeView) mChangeView.stopLoading();
    }

    /**
     * 加载失败
     */
    public void showErrorView() {
        if(null!=mChangeView) mChangeView.showErrorView();
    }

    public void onDestroy(){
        mOnRefreshListener=null; mChangeView=null;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

}
