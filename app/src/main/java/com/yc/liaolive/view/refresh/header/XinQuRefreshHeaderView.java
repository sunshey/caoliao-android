package com.yc.liaolive.view.refresh.header;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.view.refresh.listener.OnRefreshHeaderListener;

/**
 * TinyHung@Outlook.com
 * 2017/9/28.
 * 自定义下拉刷新头部
 * 支持：下拉动画，刷新动画，完成后数据显示
 * 必须实现OnRefreshHeaderListener监听器
 */

public class XinQuRefreshHeaderView extends RelativeLayout implements OnRefreshHeaderListener {

    private static final String TAG = "XinQuRefreshHeaderView";
    private TextView mTv_loading_tips;
    private AnimationDrawable mAnimationDrawable;
    private ImageView mIv_loading_view;
    private LinearLayout mLl_refresh_header;
    private TextView mTv_refresh_tips;
    private int mImageHeight;
    private int mImageWidth;
    private LinearLayout mLl_refresh_tips;
    private ImageView mIv_refresh_icon;

    public XinQuRefreshHeaderView(Context context) {
        this(context,null);
    }

    public XinQuRefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.xinqu_refresh_header_layout,this);
        //刷新中的状态
        mLl_refresh_header = (LinearLayout) findViewById(R.id.ll_refresh_header);//刷新的根布局
        mIv_loading_view = (ImageView) findViewById(R.id.iv_loading_view);//加载中的动画
        mAnimationDrawable = (AnimationDrawable) mIv_loading_view.getDrawable();
        mTv_loading_tips = (TextView) findViewById(R.id.tv_loading_tips);//加载中提示语
        //刷新之后的状态
        mLl_refresh_tips = (LinearLayout) findViewById(R.id.ll_refresh_tips);
        mIv_refresh_icon = (ImageView) findViewById(R.id.iv_refresh_icon);
        mTv_refresh_tips = (TextView) findViewById(R.id.tv_refresh_tips);
        mImageHeight= ScreenUtils.dpToPxInt(55);
        mImageWidth= ScreenUtils.dpToPxInt(55);
    }

    /**
     * 第一次下拉调用
     */
    @Override
    public void onThuchPull() {
        if(null!=mLl_refresh_tips&&mLl_refresh_tips.getVisibility()!= View.GONE){
            mLl_refresh_tips.setVisibility(View.GONE);
        }
        if(null!= mLl_refresh_header && mLl_refresh_header.getVisibility()!= View.VISIBLE){
            mLl_refresh_header.setVisibility(View.VISIBLE);
        }
        if(null!=mTv_loading_tips){
            if(mTv_loading_tips.getVisibility()!=VISIBLE){
                mTv_loading_tips.setVisibility(VISIBLE);
            }
            mTv_loading_tips.setText("下拉刷新");
        }
    }

    /**
     * 持续下拉调用
     */
    @Override
    public void onPositionChange(float scrollTop, float dragDistance, float dragPercent) {

        if(null!= mLl_refresh_header && mLl_refresh_header.getVisibility()!= View.VISIBLE){
            mLl_refresh_header.setVisibility(View.VISIBLE);
        }
        //松手刷新
        if(dragPercent>=1.0){
            mTv_loading_tips.setText("松手刷新");
            //下拉刷新
        }else{
            mTv_loading_tips.setText("下拉刷新");
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mIv_loading_view.getLayoutParams();
        layoutParams.height= (int) ((dragPercent*100)*mImageHeight/100);
        layoutParams.width= (int) ((dragPercent*100)*mImageHeight/100);
        mIv_loading_view.setLayoutParams(layoutParams);
    }

    /**
     * 正在刷新中
     */
    @Override
    public void onRefreshing() {
        //设置文字
        if(null!=mTv_loading_tips){
            mTv_loading_tips.setVisibility(View.VISIBLE);
            mTv_loading_tips.setText("努力加载中");
        }

        //防止不是原大小
        if(null!=mIv_loading_view){
            if(mIv_loading_view.getVisibility()!= View.VISIBLE){
                mIv_loading_view.setVisibility(View.VISIBLE);
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mIv_loading_view.getLayoutParams();
            layoutParams.height= (int) ((1.0*100)*mImageHeight/100);
            layoutParams.width= (int) ((1.0*100)*mImageHeight/100);
            mIv_loading_view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 刷新开始
     */
    @Override
    public void onRefreshStart() {
        //播放加载中动画
        if(null!=mAnimationDrawable&&!mAnimationDrawable.isRunning()){
            mAnimationDrawable.start();
        }
    }

    /**
     * 刷新结束，包括成功和失败
     */
    @Override
    public void onRefreshComplete() {
        mTv_loading_tips.setText("刷新完成");
    }

    /**
     * 刷新结束
     */
    @Override
    public void onRefreshEnd() {
        //播放加载中动画
        if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()){
            mAnimationDrawable.stop();
        }
    }

    /**
     * 刷新成功结果
     * @param newCount
     */
    @Override
    public void onRefreshNewCount(int newCount) {
        if(0!=newCount&&newCount>0){
            mLl_refresh_header.setVisibility(View.GONE);
            mIv_refresh_icon.setImageResource(R.drawable.iv_refresh_header_success);
            mTv_refresh_tips.setTextColor(getResources().getColor(R.color.record_text_color));
            mTv_refresh_tips.setText("新趣为你推荐了"+newCount+"部作品");
            showCountTipsView(mLl_refresh_tips,true);
        }
    }

    /**
     * 刷新失败
     */
    @Override
    public void onRefreshError() {
        mLl_refresh_header.setVisibility(View.GONE);
        mIv_refresh_icon.setImageResource(R.drawable.iv_refresh_header_error);
        mTv_refresh_tips.setTextColor(getResources().getColor(R.color.red));
        mTv_refresh_tips.setText("好尴尬，刷新失败");
        showCountTipsView(mLl_refresh_tips,true);
    }

    /**
     * 还原
     */
    @Override
    public void onReset() {
        if(null!=mLl_refresh_tips&&mLl_refresh_tips.getVisibility()!= View.GONE){
            mLl_refresh_tips.setVisibility(View.GONE);
        }
        if(null!= mLl_refresh_header && mLl_refresh_header.getVisibility()!= View.VISIBLE){
            mLl_refresh_header.setVisibility(View.VISIBLE);
        }
        if(null!=mTv_loading_tips){
            mTv_loading_tips.setText("下拉刷新");
        }
    }

    /**
     * 显示提示框
     * @param view 要显示的对象
     * @param isShow 显示/隐藏
     */
    private void showCountTipsView(View view, boolean isShow) {
        if(isShow){
            view.setVisibility(VISIBLE);
            ScaleAnimation scaleAnimation = AnimationUtil.moveThisScaleViewToBig();
            view.startAnimation(scaleAnimation);
        }
    }
}
