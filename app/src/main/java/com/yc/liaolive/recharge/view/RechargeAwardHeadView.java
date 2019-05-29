package com.yc.liaolive.recharge.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.model.GlideImageLoader;
import com.yc.liaolive.ui.contract.IndexHeaderContract;
import com.yc.liaolive.ui.presenter.IndexHeaderPresenter;
import com.yc.liaolive.util.Logger;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/6/30
 * 充值奖励头部
 */

public class RechargeAwardHeadView extends LinearLayout implements IndexHeaderContract.View {

    private static final String TAG = "RechargeAwardHeadView";
    private IndexHeaderPresenter mPresenter;
    private Banner mBanner;
    private List<BannerInfo> mBanners;

    public RechargeAwardHeadView(Context context) {
        super(context);
        init(context);
    }

    public RechargeAwardHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_rechage_award_head_layout,this);
        mPresenter = new IndexHeaderPresenter();
        mPresenter.attachView(this);
        setUserHead(((ImageView) findViewById(R.id.head_user_icon)), UserManager.getInstance().getAvatar());
        mBanner = (Banner) findViewById(R.id.head_banner);
        mBanner.setBannerAnimation(Transformer.Default);
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if(null!=mOnBannerItemClickListener&&null!=mBanners&&mBanners.size()>position){
                    mOnBannerItemClickListener.onItemClick(position,mBanners.get(position));
                }
            }
        });
        //先渲染缓存
        List<BannerInfo>  bannerInfos = (List<BannerInfo>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_HOME_BANNERS);
        if(null!=bannerInfos&&bannerInfos.size()>0){
            initBanner(bannerInfos);
        }
        mPresenter.getBanners();
    }

    /**
     * 初始化Banner
     * @param data
     */
    private void initBanner(List<BannerInfo> data){
        if(null==data||null==mBanner) return;
        this.mBanners=data;
        if(null!=mBanner) mBanner.stopAutoPlay();
        List<String> list=new ArrayList<>();
        for (BannerInfo datum : data) {
            list.add(datum.getImg());
        }
        mBanner.setImages(list).setImageLoader(new GlideImageLoader()).setDelayTime(3800).start();

        mBanner.start();
    }

    /**
     * 设置用户头像
     * @param imageView
     * @param avatar
     */
    private void setUserHead(ImageView imageView, String avatar) {
        Glide.with(getContext())
                .load(avatar)
                .error(R.drawable.ic_default_user_head)
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(getContext()))
                .into(imageView);
    }


    /**
     * 伪 onResume
     */
    public void onResume(){
        if(null!=mBanner) mBanner.startAutoPlay();
    }

    /**
     * 伪 onPause
     */
    public void onPause(){
        if(null!=mBanner) mBanner.stopAutoPlay();
    }

    /**
     * 伪 onDestroy
     */
    public void onDestroy(){
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mBanners) mBanners.clear(); mBanners=null;
        if(null!=mBanner) mBanner.stopAutoPlay(); mBanner=null;
        mOnBannerItemClickListener=null;
    }

    /**
     * 广告
     */
    public interface OnBannerItemClickListener{
        void onItemClick(int position,BannerInfo data);
    }

    private OnBannerItemClickListener mOnBannerItemClickListener;

    public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener) {
        mOnBannerItemClickListener = onBannerItemClickListener;
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showBannerResult(List<BannerInfo> data) {
        initBanner(data);
    }

    @Override
    public void showBannerResultEmpty() {
        findViewById(R.id.re_banner_group).setVisibility(GONE);
    }

    @Override
    public void showBannerResultError(int code, String errorMsg) {
        findViewById(R.id.re_banner_group).setVisibility(GONE);
    }

    @Override
    public void showRecommendAnchors(List<RoomList> data) {

    }

    @Override
    public void showRecommendEmpty() {

    }

    @Override
    public void showRecommendError(int code, String errorMsg) {

    }

    public interface OnUserClickListener{
        void onClickHead(String userID);
    }
    private OnUserClickListener mOnUserClickListener;

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        mOnUserClickListener = onUserClickListener;
    }
}
