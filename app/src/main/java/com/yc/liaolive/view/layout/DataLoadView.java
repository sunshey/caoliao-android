package com.yc.liaolive.view.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yc.liaolive.R;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.view.refresh.LoadingIndicatorView;

/**
 * TinyHung@Outlook.com
 * 2018/10/12
 * 新动画用在RecyclerView上面的加载中、加载失败重试、数据为空状态切换
 */

public class DataLoadView extends RelativeLayout {

    private static final String TAG = "DataLoadView";
    private LoadingIndicatorView mLoadingView;
    private ImageView mErrorIcon;
    private View mErrprView;

    public DataLoadView(Context context) {
        super(context);
        init(context);
    }

    public DataLoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_list_loading, this);
        mLoadingView = (LoadingIndicatorView) findViewById(R.id.loading_view);
        mErrorIcon = (ImageView) findViewById(R.id.view_error);
        mErrprView = findViewById(R.id.ll_load_error);
        mErrprView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnRefreshListener) {
                    mOnRefreshListener.onRefresh();
                }
            }
        });
        mErrprView.setClickable(false);
    }

    /**
     * 固定LoadingView高度和RecyclerView高度一致
     * @param height
     */
    @SuppressLint("WrongViewCast")
    public void setLoadHeight(int height){
        if(null!=mErrorIcon){
            ViewGroup.LayoutParams layoutParams = mErrorIcon.getLayoutParams();
            layoutParams.width=height- ScreenUtils.dpToPxInt(15f);
            layoutParams.height=height- ScreenUtils.dpToPxInt(15f);
            mErrorIcon.setLayoutParams(layoutParams);
        }
    }

    /**
     * 开始加载
     */
    public void showLoadingView(){
        if(null!=mErrprView){
            mErrprView.setVisibility(INVISIBLE);
            mErrprView.setClickable(false);
        }
        if (null != mLoadingView ) mLoadingView.smoothToShow();
    }
    /**
     * 停止加载
     */
    public void stopLoading() {
        if (null != mLoadingView ) mLoadingView.smoothToHide();
    }

    /**
     * 显示错误状态
     * @param msg
     */
    public void showErrorLayout(String msg){
        if(null!=mLoadingView&&mLoadingView.getVisibility()!=GONE) {
            mLoadingView.hide();
            mLoadingView.setVisibility(GONE);
        }
        if(null!=mErrprView){
            mErrprView.setVisibility(VISIBLE);
            ((TextView) findViewById(R.id.view_content_tips)).setText(msg);
            mErrprView.setClickable(true);
        }
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void onDestroy() {
        stopLoading();
        mOnRefreshListener=null;
    }
}
