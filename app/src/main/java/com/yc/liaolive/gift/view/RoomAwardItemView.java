package com.yc.liaolive.gift.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.gift.listener.OnFunctionListener;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.ui.fragment.LiveUserDetailsFragment;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.view.widget.MarqueeTextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
* TinyHung@Outlook.com
* 2018/12/15
* 直播间中奖信息告示条目，默认条目不复用
* 可调用setMultiplex(boolean flag);标记条目是否复用
*/

public class RoomAwardItemView extends FrameLayout {

    private static final String TAG = "RoomAwardItemView";
    private int mIdentityType;
    private boolean isCleaning=false;//是否正处入清除View状态
    private long CLEAN_MILLIS =2500;//2.5秒后清除自己
    private TranslateAnimation mItemInAnim;
    private TranslateAnimation mItemOutAnim;
    private boolean mMultiplex;

    public RoomAwardItemView(Context context) {
        super(context);
        init(context);
    }

    public RoomAwardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //中奖信息Item入场动画
        mItemInAnim = (TranslateAnimation) AnimationUtils.loadAnimation(context, R.anim.gift_room_lite_item_enter);
        mItemInAnim.setInterpolator(new LinearInterpolator());//回弹 BounceInterpolator 、匀速执行 LinearInterpolator 、加速 AccelerateInterpolator
        //礼物Item出场动画
        mItemOutAnim = (TranslateAnimation) AnimationUtils.loadAnimation(context, R.anim.gift_room_lite_item_out);
        mItemOutAnim.setInterpolator(new AccelerateInterpolator());//加速消失
    }

    /**
     * 动画条目是否复用
     * @param multiplex
     */
    public void setMultiplex(boolean multiplex) {
        mMultiplex = multiplex;
    }

    /**
     * 绑定用户身份
     *
     * @param identityType
     */
    public void setIdentityType(int identityType) {
        mIdentityType = identityType;
    }

    /**
     * 添加组件至此动画ITEM
     * @param tagID 防止重复的唯一标识
     * @param msgInfo 动画元素
     * @return 返回添加状态，如果添加失败了，应当返回至总任务队列中重新等待分配，避免丢失
     */
    public boolean addGiftItem(String tagID, CustomMsgInfo msgInfo) {
        Logger.d(TAG, "addGiftItem---TAG:" + tagID + ",isCleaning:" + isCleaning);
        //如果条目正在移除，拦截
        if (isCleaning) return false;
        //1.检查是否重复加入
        View viewItem = this.findViewWithTag(tagID);
        //2.全新的入场初始化
        if (null == viewItem) {
            Logger.d(TAG,"新View");
            //2.1添加一个礼物动画Item到容器中
            viewItem = createNewAwardView();
            if (null == viewItem) return true;
            //自己中奖了
            if (UserManager.getInstance().getUserId().equals(msgInfo.getSendUserID())) {
                viewItem.findViewById(R.id.gift_item_bg).setBackgroundResource(R.drawable.award_item_bg_purple_shape);
            } else {
                viewItem.findViewById(R.id.gift_item_bg).setBackgroundResource(R.drawable.award_item_bg_red_shape);
            }
            String nickName = "未知用户";
            try {
                nickName = URLDecoder.decode(null == msgInfo.getSendUserName() ? "未知用户" : msgInfo.getSendUserName().replaceAll("%", "%25"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } finally {
                ((MarqueeTextView) viewItem.findViewById(R.id.view_tv_nickname)).setText(nickName);
                ((TextView) viewItem.findViewById(R.id.view_tv_gift_title)).setText(Html.fromHtml("送出的<font color='#FFFCAC'>“" + msgInfo.getGift().getTitle() + "”"));
                ((TextView) viewItem.findViewById(R.id.view_award_desp)).setText(Html.fromHtml("喜中</font>"
                        + "<font color='#FF7575'> " + msgInfo.getGift().getDrawIntegral() + " </font><font color='#FFFCAC'>钻石</font>"));
                ImageView headImage = (ImageView) viewItem.findViewById(R.id.view_gift_user_icon);
                Glide.with(getContext())
                        .load(msgInfo.getSendUserHead())
                        .error(R.drawable.ic_default_user_head)
                        .crossFade()//渐变
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .transform(new GlideCircleTransform(getContext()))
                        .into(headImage);
                View giftItemBg = viewItem.findViewById(R.id.gift_item_bg);
                giftItemBg.setTag(msgInfo.getSendUserID());
                giftItemBg.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LiveUserDetailsFragment.newInstance(((String) v.getTag()), mIdentityType, LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID()).setOnFunctionClickListener(new LiveUserDetailsFragment.OnFunctionClickListener() {
                            @Override
                            public void onSendGift(FansInfo userInfo) {
                                //接收对象
                                if (null != mOnFunctionListener)
                                    mOnFunctionListener.onSendGift(userInfo);
                            }
                        }).show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "userinfo");
                    }
                });
                //2.8设置view标识,3秒内复用
                viewItem.setTag(tagID);
                setTag(tagID);
                //2.9将礼物的View添加到礼物的ViewGroup中
                addView(viewItem);
                if(null!= mItemInAnim) viewItem.startAnimation(mItemInAnim);
                if (null != cleanGiftItemRunnable) RoomAwardItemView.this.postDelayed(cleanGiftItemRunnable, CLEAN_MILLIS);
            }
            return true;
        }else{
            Logger.d(TAG,"动画已存在");
            if(null!=cleanGiftItemRunnable) RoomAwardItemView.this.removeCallbacks(cleanGiftItemRunnable);
            String nickName = "未知用户";
            try {
                nickName = URLDecoder.decode(null == msgInfo.getSendUserName() ? "未知用户" : msgInfo.getSendUserName().replaceAll("%", "%25"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }finally {
                ((MarqueeTextView) viewItem.findViewById(R.id.view_tv_nickname)).setText(nickName);
                ((TextView) viewItem.findViewById(R.id.view_tv_gift_title)).setText(Html.fromHtml("送出的<font color='#FFFCAC'>“" + msgInfo.getGift().getTitle() + "”"));
                ((TextView) viewItem.findViewById(R.id.view_award_desp)).setText(Html.fromHtml("喜中</font>"
                        + "<font color='#FF7575'> " + msgInfo.getGift().getDrawIntegral() + " </font><font color='#FFFCAC'>钻石</font>"));
                if(null!=cleanGiftItemRunnable) RoomAwardItemView.this.postDelayed(cleanGiftItemRunnable, CLEAN_MILLIS);
            }
            return true;
        }
    }



    /**
     * 时间到 移除自己
     */
    private Runnable cleanGiftItemRunnable=new Runnable() {
        @Override
        public void run() {
            removeGiftView();
        }
    };

    /**
     * 派生一个全新的礼物ItemView
     *
     * @return
     */
    private View createNewAwardView() {
        this.removeAllViews();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.live_award_item_layout, null);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 10;
        view.setLayoutParams(lp);
        return view;
    }

    /**
     * 移除组件元素
     */
    public synchronized void removeGiftView() {
        Logger.d(TAG,"removeGiftView:count："+getChildCount());
        if(null!= mItemOutAnim &&getChildCount()>0){
            mItemOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) {
                    Logger.d(TAG,"动画移除："+animation);
                    RoomAwardItemView.this.setTag(null);
                    RoomAwardItemView.this.removeAllViews();
                    isCleaning=false;
                }
                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
            isCleaning=true;
            if(null!= mItemOutAnim) RoomAwardItemView.this.startAnimation(mItemOutAnim);
        }else{
            RoomAwardItemView.this.setTag(null);
            RoomAwardItemView.this.removeAllViews();
            isCleaning=false;
        }
    }

    /**
     * 对应生命周期调用
     */
    public void onDestroy() {
        this.removeAllViews();
        if (null != mItemInAnim) mItemInAnim.cancel();
        if (null != mItemOutAnim) mItemOutAnim.cancel();
        if(null!=cleanGiftItemRunnable) this.removeCallbacks(cleanGiftItemRunnable);
        mItemOutAnim = null;isCleaning=false;mItemInAnim = null;
    }

    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }
}
