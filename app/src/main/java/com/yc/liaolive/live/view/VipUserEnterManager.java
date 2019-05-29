package com.yc.liaolive.live.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.gift.listener.OnFunctionListener;
import com.yc.liaolive.ui.fragment.LiveUserDetailsFragment;
import com.yc.liaolive.util.Logger;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * TinyHung@Outlook.com
 * 2018/12/15
 * 会员进场
 */

public class VipUserEnterManager extends LinearLayout{

    private static final String TAG = "VipUserEnterManager";
    private Queue<FansInfo> mVipUserQueue;//会员用户队列
    private boolean isExecute;//是否正在执行
    private AnimationSet mVipUserInAni;//会员进场
    private long CLEAN_MILLIS =5000;//5秒后清除自己
    private AnimationSet mVipUserOutAni;
    private int mIdentityType;

    public VipUserEnterManager(Context context) {
        this(context,null);
    }

    public VipUserEnterManager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_vip_enter_layout,this);
        //VIP进场相关
        mVipUserInAni = (AnimationSet) AnimationUtils.loadAnimation(getContext(), R.anim.vip_enter);
        mVipUserInAni.setInterpolator(new OvershootInterpolator());//强烈抖动BounceInterpolator
        mVipUserOutAni = (AnimationSet) AnimationUtils.loadAnimation(getContext(), R.anim.vip_out);
        mVipUserOutAni.setInterpolator(new LinearInterpolator());//匀速动画
    }

    /**
     * 绑定身份场景
     * @param identityType
     */
    public void setIdentityType(int identityType) {
        mIdentityType = identityType;
    }
    /**
     * 添加一个会员进场信息
     * @param fansInfo
     */
    public void addUserToTask(FansInfo fansInfo){
        if(null==fansInfo) return;
        if(null==mVipUserQueue) mVipUserQueue=new ArrayDeque<>();
        for (FansInfo info : mVipUserQueue) {
            if(info.getUserid().equals(fansInfo.getUserid())) {
                return;
            }
        }
        mVipUserQueue.add(fansInfo);
        startAutoTask();
    }

    public void setCLEAN_MILLIS(long CLEAN_MILLIS) {
        this.CLEAN_MILLIS = CLEAN_MILLIS;
    }

    /**
     * 执行会员进场动画播放
     */
    private void startAutoTask() {
        if(isExecute) return;
        if(null!=mVipUserQueue&&mVipUserQueue.size()>0&&null==this.getTag()){
            isExecute=true;
            FansInfo poll = mVipUserQueue.poll();
            createVipEnterAnimationView(poll);
        }
    }

    /**
     * 添加会员进场View实例并填充数据
     * @param fansInfo
     */
    private void createVipEnterAnimationView(FansInfo fansInfo) {
        if (null!=getContext()) {
            View drawView = LayoutInflater.from(getContext()).inflate(R.layout.live_vip_item_layout, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            drawView.setLayoutParams(lp);
            TextView vipEnter = (TextView) drawView.findViewById(R.id.view_vip_enter);
            vipEnter.setText(Html.fromHtml("<font color='#6DEAFB'>"+fansInfo.getNickname()+"  </font><font color='#FFFFFF'>华丽登场！</font>"));

            drawView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null==VipUserEnterManager.this.getTag()) return;
                    if(null!=getContext()) LiveUserDetailsFragment.newInstance((FansInfo) VipUserEnterManager.this.getTag(),mIdentityType,null).setOnFunctionClickListener(new LiveUserDetailsFragment.OnFunctionClickListener() {
                        @Override
                        public void onSendGift(FansInfo userInfo) {
                            super.onSendGift(userInfo);
                            if(null!=mOnFunctionListener) mOnFunctionListener.onSendGift(userInfo);
                        }
                    }).show(((FragmentActivity)getContext()).getSupportFragmentManager(),"userDetsils");
                }
            });
            VipUserEnterManager.this.addView(drawView,0);
            VipUserEnterManager.this.setTag(fansInfo);
            //刷新该view
            if(null!=mVipUserInAni) drawView.startAnimation(mVipUserInAni);
            //5秒后清除自己
            VipUserEnterManager.this.postDelayed(cleanGiftRunnable, CLEAN_MILLIS);
        }
    }


    /**
     * 执行会员进场动画播放
     */
    public void executeVipEnterAnimation() {
        //先清除已经可能存在的礼物动画
        removeVipEnterView(new OnRemoveViewCallBack() {
            @Override
            public void onRemoveEnd() {
                isExecute=false;
                startAutoTask();
            }
        });
    }

    private Runnable cleanGiftRunnable=new Runnable() {
        @Override
        public void run() {
            executeVipEnterAnimation();
        }
    };


    /**
     * 删除会员用户进场动画view
     * @param callBack 删除View完成回调
     */
    private void removeVipEnterView(final OnRemoveViewCallBack callBack) {
        if(null!= getTag()&&null!= mVipUserOutAni){
            if(getChildCount()<=0){
                if(null!=callBack) callBack.onRemoveEnd();
                return;
            }
            View childAt = getChildAt(0);
            if(null==childAt){
                if(null!=callBack) callBack.onRemoveEnd();
                return;
            }
            mVipUserOutAni.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    new android.os.Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            VipUserEnterManager.this.removeAllViews();
                            VipUserEnterManager.this.setTag(null);
                            if(null!=callBack) callBack.onRemoveEnd();
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            childAt.startAnimation(mVipUserOutAni);
        }else{
            if(null!=callBack) callBack.onRemoveEnd();
        }
    }

    public void onReset(){
        VipUserEnterManager.this.removeCallbacks(cleanGiftRunnable);
        isExecute=false;
        this.removeAllViews();
    }

    public void onDestroy(){
        VipUserEnterManager.this.removeCallbacks(cleanGiftRunnable);
        isExecute=false;
        this.removeAllViews();
    }

    /**
     * View出场结束回调
     */
    private interface OnRemoveViewCallBack{
        void onRemoveEnd();
    }

    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }
}
