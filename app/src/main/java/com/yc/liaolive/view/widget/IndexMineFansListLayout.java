package com.yc.liaolive.view.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.util.Logger;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/20
 * 粉丝榜单
 */

public class IndexMineFansListLayout extends FrameLayout {

    private static final String TAG = "IndexMineFansListLayout";
    private ImageView userIcon1;
    private ImageView userIcon2;
    private ImageView userIcon3;
    private AnimationSet mItemInAnim;

    public IndexMineFansListLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public IndexMineFansListLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_index_top_list,this);
        userIcon1=(ImageView) findViewById(R.id.view_user_icon1);
        userIcon2=(ImageView) findViewById(R.id.view_user_icon2);
        userIcon3=(ImageView) findViewById(R.id.view_user_icon3);
        mItemInAnim = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.user_face_enter);
    }

    /**
     * 设置用户信息
     * @param fansInfos
     */
    public void setUserData(List<FansInfo> fansInfos){
        if(null==fansInfos) return;
        if(null!=userIcon1) userIcon1.setImageResource(R.drawable.ic_fans_top_empty);
        if(null!=userIcon2) userIcon2.setImageResource(R.drawable.ic_fans_top_empty);
        if(null!=userIcon3) userIcon3.setImageResource(R.drawable.ic_fans_top_empty);

        if(fansInfos.size()>0&&null!=userIcon1){
            FansInfo fansInfo = fansInfos.get(0);
            Glide.with(getContext()).load(fansInfo.getAvatar()).crossFade().placeholder(R.drawable.ic_default_user_head).error(R.drawable.ic_default_user_head).animate(R.anim.item_alpha_in)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().skipMemoryCache(true).transform(new GlideCircleTransform(getContext())).into(userIcon1);
        }
        if(fansInfos.size()>1&&null!=userIcon2){
            FansInfo fansInfo = fansInfos.get(1);
            Glide.with(getContext()).load(fansInfo.getAvatar()).crossFade().placeholder(R.drawable.ic_default_user_head).error(R.drawable.ic_default_user_head).animate(R.anim.item_alpha_in)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().skipMemoryCache(true).transform(new GlideCircleTransform(getContext())).into(userIcon2);
        }
        if(fansInfos.size()>2&&null!=userIcon3){
            FansInfo fansInfo = fansInfos.get(2);
            Glide.with(getContext()).load(fansInfo.getAvatar()).crossFade().placeholder(R.drawable.ic_default_user_head).error(R.drawable.ic_default_user_head).animate(R.anim.item_alpha_in)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().skipMemoryCache(true).transform(new GlideCircleTransform(getContext())).into(userIcon3);
        }
    }

    /**
     * 开始动画
     */
    public void startFansListAnimation(){
        if(null==userIcon1||null==userIcon2||null==userIcon3) return;
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(userIcon1, "translationX", 0.0f, 1f).setDuration(500);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(userIcon2, "translationX", 0.0f, 0.8f).setDuration(500);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(userIcon3, "translationX", 0.0f, 0.7f).setDuration(500);

        AnimatorSet animSet1 = new AnimatorSet();
        animSet1.playTogether(animator1);
        animSet1.setInterpolator(new BounceInterpolator());
        animSet1.start();

        AnimatorSet animSet2 = new AnimatorSet();
        animSet2.playTogether(animator2);
        animSet2.setStartDelay(250);
        animSet2.setInterpolator(new BounceInterpolator());
        animSet2.start();

        AnimatorSet animSet3 = new AnimatorSet();
        animSet3.playTogether(animator3);
        animSet3.setStartDelay(500);
        animSet3.setInterpolator(new BounceInterpolator());
        animSet3.start();
    }
}
