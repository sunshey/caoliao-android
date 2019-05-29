package com.yc.liaolive.media.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2018/10/25
 * 播放器广告
 */

public class PlayerAdLayout  extends FrameLayout{

    private static final String TAG = "PlayerAdLayout";

    private BannerInfo mBannerInfo;

    public PlayerAdLayout(@NonNull Context context) {
        this(context,null);
    }

    public PlayerAdLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_ad_layout,this);
        OnClickListener onClickListener=new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.view_ad_back:
                        if(null!=mOnAdClickListener) mOnAdClickListener.onBack(v);
                        break;
                    case R.id.view_btn_open_vip:
                    case R.id.root_adview_layout:
                        startActivity();
                }
            }
        };
        this.setOnClickListener(onClickListener);
        findViewById(R.id.view_ad_back).setOnClickListener(onClickListener);
        findViewById(R.id.root_adview_layout).setOnClickListener(onClickListener);
        findViewById(R.id.view_btn_open_vip).setOnClickListener(onClickListener);
    }

    /**
     * 广告初始化
     * @param privateMedia
     */
    public void init(PrivateMedia privateMedia){
        if(null==privateMedia||null==privateMedia.getBanners()) return;
        if(privateMedia.getBanners().size()>0){
            BannerInfo bannerInfo = privateMedia.getBanners().get(0);
            this.mBannerInfo=bannerInfo;
            Glide.with(getContext())
                    .load(mBannerInfo.getPlayimg())
                    .placeholder(R.drawable.bg_live_transit)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .skipMemoryCache(true)
                    .into((ImageView) findViewById(R.id.view_ad_cover));
        }
    }

    /**
     * 广告初始化
     * @param roomExtra
     */
    public void init(RoomExtra roomExtra){
        if(null==roomExtra||null==roomExtra.getBanners()) return;
        if(roomExtra.getBanners().size()>0){
            BannerInfo bannerInfo = roomExtra.getBanners().get(0);
            this.mBannerInfo=bannerInfo;
            Glide.with(getContext())
                    .load(mBannerInfo.getPlayimg())
                    .placeholder(R.drawable.bg_live_transit)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .skipMemoryCache(true)
                    .into((ImageView) findViewById(R.id.view_ad_cover));
        }
    }


    /**
     * 广告初始化
     * @param roomList
     */
    public void init(RoomList roomList){
        if(null==roomList||null==roomList.getBanners()) return;
        if(roomList.getBanners().size()>0){
            BannerInfo bannerInfo = roomList.getBanners().get(0);
            this.mBannerInfo=bannerInfo;
            Glide.with(getContext())
                    .load(mBannerInfo.getPlayimg())
                    .placeholder(R.drawable.bg_live_transit)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .skipMemoryCache(true)
                    .into((ImageView) findViewById(R.id.view_ad_cover));
        }
    }

    /**
     * 开启活动
     */
    private void startActivity() {
        if(null!=mBannerInfo){
            if (!TextUtils.isEmpty(mBannerInfo.getJump_url())) {
                CaoliaoController.start(mBannerInfo.getJump_url(),true,"detail_");
            }
        }
    }

    public interface OnAdClickListener{
        void onBack(View view);
    }

    private OnAdClickListener mOnAdClickListener;

    public void setOnAdClickListener(OnAdClickListener onAdClickListener) {
        mOnAdClickListener = onAdClickListener;
    }
}
