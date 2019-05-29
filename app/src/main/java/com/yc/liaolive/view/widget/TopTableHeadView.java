package com.yc.liaolive.view.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.model.GlideCircleTransform;

import java.util.List;
import java.util.Locale;

/**
 * TinyHung@Outlook.com
 * 2018/6/30
 * 积分排行榜单头部
 */

public class TopTableHeadView extends LinearLayout {

    private int mType;

    public TopTableHeadView(Context context) {
        super(context);
        init(context);
    }

    public TopTableHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_top_table_head_layout,this);
    }

    /**
     * 设置头部数据
     * @param data
     */
    public void setData(List<FansInfo> data){
        if(null==data) return;
        if(data.size()<=0) return;
        FansInfo topTableInfo = data.get(0);
        if(null!=topTableInfo){
            findViewById(R.id.ll_user_data1).setVisibility(VISIBLE);
            ImageView userAvatar = (ImageView) findViewById(R.id.view_user_icon1);
            ImageView view_user_gradle1 = (ImageView) findViewById(R.id.view_user_gradle1);
            ImageView view_vip_gradle1 = (ImageView) findViewById(R.id.view_vip_gradle1);
            MarqueeTextView view_item_nickname1 = (MarqueeTextView) findViewById(R.id.view_item_nickname1);
            ImageView view_user_sex1 = (ImageView) findViewById(R.id.view_user_sex1);
            TextView view_tv_total_points1 = (TextView) findViewById(R.id.view_tv_total_points1);
            setUserHead(userAvatar,topTableInfo.getAvatar());
            LiveUtils.setUserSex(view_user_sex1,topTableInfo.getSex());
            LiveUtils.setUserGradle(view_user_gradle1,topTableInfo.getLevel_integral());
            LiveUtils.setUserBlockVipGradle(view_vip_gradle1,topTableInfo.getVip());//设置用户vip等级
//            LiveUtils.setUserVipGradle(view_vip_gradle1,topTableInfo.getVip());
            setUserNickname(view_item_nickname1,topTableInfo.getNickname());
            setUserPoints(view_tv_total_points1,0==mType?topTableInfo.getTotal_points():topTableInfo.getDay_points());
            View userCover1 = findViewById(R.id.re_user_cover1);
            userCover1.setTag(topTableInfo.getUserid());
            userCover1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mOnUserClickListener) mOnUserClickListener.onClickHead((String) v.getTag());
                }
            });
        }
        if(data.size()>1){
            findViewById(R.id.ll_user_data2).setVisibility(VISIBLE);
            FansInfo topTableInfo1 = data.get(1);
            if(null!=topTableInfo1){
                ImageView userAvatar = (ImageView) findViewById(R.id.view_user_icon2);
                ImageView view_user_gradle1 = (ImageView) findViewById(R.id.view_user_gradle2);
                ImageView view_vip_gradle1 = (ImageView) findViewById(R.id.view_vip_gradle2);
                MarqueeTextView view_item_nickname1 = (MarqueeTextView) findViewById(R.id.view_item_nickname2);
                ImageView view_user_sex1 = (ImageView) findViewById(R.id.view_user_sex2);
                TextView view_tv_total_points1 = (TextView) findViewById(R.id.view_tv_total_points2);
                setUserHead(userAvatar,topTableInfo1.getAvatar());
                LiveUtils.setUserSex(view_user_sex1,topTableInfo1.getSex());
                LiveUtils.setUserGradle(view_user_gradle1,topTableInfo1.getLevel_integral());
                LiveUtils.setUserBlockVipGradle(view_vip_gradle1,topTableInfo1.getVip());//设置用户vip等级
                setUserNickname(view_item_nickname1,topTableInfo1.getNickname());
                setUserPoints(view_tv_total_points1,0==mType?topTableInfo1.getTotal_points():topTableInfo1.getDay_points());
                View userCover2 = findViewById(R.id.re_user_cover2);
                userCover2.setTag(topTableInfo1.getUserid());
                userCover2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=mOnUserClickListener) mOnUserClickListener.onClickHead((String) v.getTag());
                    }
                });
            }
        }
        if(data.size()>2){
            findViewById(R.id.ll_user_data3).setVisibility(VISIBLE);
            FansInfo topTableInfo2 = data.get(2);
            if(null!=topTableInfo2){
                ImageView userAvatar = (ImageView) findViewById(R.id.view_user_icon3);
                ImageView view_user_gradle1 = (ImageView) findViewById(R.id.view_user_gradle3);
                ImageView view_vip_gradle1 = (ImageView) findViewById(R.id.view_vip_gradle3);
                MarqueeTextView view_item_nickname1 = (MarqueeTextView) findViewById(R.id.view_item_nickname3);
                ImageView view_user_sex1 = (ImageView) findViewById(R.id.view_user_sex3);
                TextView view_tv_total_points1 = (TextView) findViewById(R.id.view_tv_total_points3);
                setUserHead(userAvatar,topTableInfo2.getAvatar());
                LiveUtils.setUserSex(view_user_sex1,topTableInfo2.getSex());
                LiveUtils.setUserGradle(view_user_gradle1,topTableInfo2.getLevel_integral());
                LiveUtils.setUserBlockVipGradle(view_vip_gradle1,topTableInfo2.getVip());//设置用户vip等级
                setUserNickname(view_item_nickname1,topTableInfo2.getNickname());
                setUserPoints(view_tv_total_points1,0==mType?topTableInfo2.getTotal_points():topTableInfo2.getDay_points());
                View userCover3 = findViewById(R.id.re_user_cover3);
                userCover3.setTag(topTableInfo2.getUserid());
                userCover3.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=mOnUserClickListener) mOnUserClickListener.onClickHead((String) v.getTag());
                    }
                });
            }
        }
    }

    /**
     * 积分
     * @param totalPointsTextView
     * @param totalPoints
     */
    private void setUserPoints(TextView totalPointsTextView, int totalPoints) {
        totalPointsTextView.setText(String.format(Locale.CHINA, "%d亲密度", totalPoints));
    }

    /**
     * 昵称
     * @param nickNameTextView
     * @param nickname
     */
    private void setUserNickname(MarqueeTextView nickNameTextView, String nickname) {
        nickNameTextView.setText(nickname);
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
     * 榜单的场景
     * @param type
     */
    public void setType(int type) {
        this.mType=type;
    }

    /**
     * 设置头部背景
     */
    public void setBackgroundRes(int res) {
        setBackgroundDra(getContext().getResources().getDrawable(res));
    }

    /**
     * 设置头部背景
     */
    public void setBackgroundDra(Drawable drawable) {
        if(null==drawable) return;
        findViewById(R.id.re_header_bg).setBackground(drawable);
    }

    public interface OnUserClickListener{
        void onClickHead(String userID);
    }
    private OnUserClickListener mOnUserClickListener;

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        mOnUserClickListener = onUserClickListener;
    }
}
