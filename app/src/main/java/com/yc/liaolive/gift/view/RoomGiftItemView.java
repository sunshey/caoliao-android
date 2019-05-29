package com.yc.liaolive.gift.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
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
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.gift.listener.OnFunctionListener;
import com.yc.liaolive.gift.manager.RoomGiftGroupManager;
import com.yc.liaolive.gift.ui.dialog.LiveGiftDialog;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.util.LiveChatUserGradleSpan;
import com.yc.liaolive.live.view.GradualTextView;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.ui.fragment.LiveUserDetailsFragment;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.view.widget.MarqueeTextView;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

 /**
 * TinyHung@Outlook.com
 * 2018/8/16
 * 直播间普通礼物条目
 */

public class RoomGiftItemView extends FrameLayout implements Observer {

    private static final String TAG = "RoomGiftItemView";
    private TranslateAnimation mItemInAnim;//礼物进场
    private TranslateAnimation mItemOutAnim;//礼物出场
     private Animator mItemNumScale;
     private Context mContext;
     private Map<String,Integer> mGiftCountBadge =null;//礼物数量cache标记,用来记录当前正在播放的动画是否在20秒内连击送出的缓存数量；如果是，累加数量
     private boolean isCleaning=false;//是否正处入清除View状态
     private long CLEAN_MILLIS =3000;//3秒后清除自己
     private int mApiType;

     public RoomGiftItemView(Context context) {
        super(context);
        init(context);
    }

    public RoomGiftItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext=context;
        //礼物Item入场动画
        mItemInAnim = (TranslateAnimation) AnimationUtils.loadAnimation(context, R.anim.gift_room_lite_item_enter);
        mItemInAnim.setInterpolator(new LinearInterpolator());//回弹 BounceInterpolator 、匀速执行 LinearInterpolator 、加速 AccelerateInterpolator
        //礼物Item出场动画
        mItemOutAnim = (TranslateAnimation) AnimationUtils.loadAnimation(context, R.anim.gift_room_lite_item_out);
        mItemOutAnim.setInterpolator(new AccelerateInterpolator());//加速消失
        //礼物数字动画
        mItemNumScale = AnimatorInflater.loadAnimator(context, R.animator.gift_room_lite_num_scalex);
        mItemNumScale.setInterpolator(new LinearInterpolator());
        //监听本地Cache缓存是否需要清空
        ApplicationManager.getInstance().addObserver(this);
    }

     /**
      * 应用场景
      * @param apiType
      */
     public void setApiType(int apiType) {
         mApiType = apiType;
     }

    /**
     * 设置自动清空时间
     * @param cleanMilliss
     */
    public void setCleanMilliss(long cleanMilliss){
        this.CLEAN_MILLIS =cleanMilliss;
    }

    /**
     * 添加组件至此动画ITEM
     * @param tagID 防止重复的唯一标识
     * @param msgInfo 动画元素
     * @return 返回添加状态，如果添加失败了，应当返回至总任务队列中重新等待分配，避免丢失
     * Prize_level :1:小于5倍的奖励 2：大于5倍小于1000000积分的中奖 3：超级大奖
     */
    public boolean addGiftItem(String tagID, CustomMsgInfo msgInfo){
        //如果条目正在移除，拦截
        if(isCleaning) return false;
        //1.检查是否重复加入
        View viewItem = this.findViewWithTag(tagID);
        //2.全新的入场初始化
        if (null == viewItem) {
            //2.1添加一个礼物动画Item到容器中
            viewItem = createNewGuftView();
            if(null==viewItem) return true;
            //2.2初始化
            MarqueeTextView tvGiftName = (MarqueeTextView) viewItem.findViewById(R.id.view_tv_gift_name);
            MarqueeTextView tvGiftDesp = (MarqueeTextView) viewItem.findViewById(R.id.view_tv_gift_desp);
            final GradualTextView giftNum = (GradualTextView) viewItem.findViewById(R.id.view_gift_num);
            //500倍率动画
            DrawBigMulitAnimationView animationView = (DrawBigMulitAnimationView) viewItem.findViewById(R.id.view_draw_icon);
            if(null==mGiftCountBadge) mGiftCountBadge = new TreeMap<>();
            //2.3判断是否此礼物是否是在20秒内连击送出的礼物
            int allGiftCount = msgInfo.getGift().getCount();
            allGiftCount = allGiftCount + getCacheCount(tagID);
            //设置礼物数量
            if(allGiftCount>1){
                giftNum.setNumberWithAnim(allGiftCount);
            }else{
                SpannableStringBuilder stringBuilder = LiveChatUserGradleSpan.giftNumFromat(String.valueOf(allGiftCount));
                giftNum.setText(stringBuilder);
            }
            //2.4更新最新的缓存池数量
            mGiftCountBadge.put(tagID,allGiftCount);
            if(UserManager.getInstance().getUserId().equals(msgInfo.getSendUserID())){
                viewItem.findViewById(R.id.gift_item_bg).setBackgroundResource(R.drawable.gift_item_bg_purple_shape);
            }else{
                viewItem.findViewById(R.id.gift_item_bg).setBackgroundResource(R.drawable.gift_item_bg_shape);
            }
            //2.6给数量控件设置标记,下次直接找粗此控件复用
            giftNum.setTag(allGiftCount);
            tvGiftName.setText(msgInfo.getSendUserName());
            //礼物消息
            tvGiftDesp.setText(msgInfo.getAccapUserName());
            //2.7中奖处理
            if(msgInfo.getGift().getDrawTimes()>0){
                //小于5倍的奖励
                if(msgInfo.getGift().getPrize_level()==1){
                    DrawSmallMulitAnimationView tvDrawTimes = (DrawSmallMulitAnimationView) viewItem.findViewById(R.id.tv_draw_times);
                    tvDrawTimes.setBackgroundResource(R.drawable.room_draw_level_bg);
                    tvDrawTimes.startText("中奖"+msgInfo.getGift().getDrawTimes()+"倍!");
                }else if(msgInfo.getGift().getPrize_level()==2){
                    //大于五倍，小于于1000000中奖积分的动画
                    animationView.start(msgInfo.getGift().getDrawTimes());
                    //100倍金币掉落动画
                    if(TextUtils.equals(UserManager.getInstance().getUserId(),msgInfo.getSendUserID())){
                        int[] position = new int[2];
                        getLocationInWindow(position);
                        animationView.setLocationStartPosition(position);
                        animationView.startGoldAnimation();
                    }
                }
            }
            ImageView giftUserIcon = (ImageView)viewItem.findViewById(R.id.view_gift_user_icon);//用户icon
            RelativeLayout userIconView = (RelativeLayout)viewItem.findViewById(R.id.view_room_user_icon);//用户iconView
            ImageView ivFiftIcon = viewItem.findViewById(R.id.view_iv_gift_icon);//礼物icon
            ivFiftIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            userIconView.setTag(msgInfo.getSendUserID());
            userIconView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null==v.getTag()) return;
                    if(null!=getContext()) LiveUserDetailsFragment.newInstance((String) v.getTag()).setOnFunctionClickListener(new LiveUserDetailsFragment.OnFunctionClickListener() {
                        @Override
                        public void onSendGift(FansInfo userInfo) {
                            super.onSendGift(userInfo);
                            if(null!=mOnFunctionListener) mOnFunctionListener.onSendGift(userInfo);
                        }
                    }).show(((FragmentActivity) getContext()).getSupportFragmentManager(),"userDetsils");
                }
            });

            try {
                Glide.with(getContext())
                        .load(msgInfo.getSendUserHead())
                        .error(R.drawable.ic_default_user_head)
                        .crossFade()//渐变
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .transform(new GlideCircleTransform(mContext))
                        .into(giftUserIcon);
                if(null!= msgInfo.getGift()){
                    Glide.with(getContext()).load( msgInfo.getGift().getSrc())
                            .error(R.drawable.ic_default_gift_icon)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                            .centerCrop()//中心点缩放
                            .skipMemoryCache(true)//跳过内存缓存
                            .into(ivFiftIcon);
                }else{
                    ivFiftIcon.setImageResource(R.drawable.ic_default_gift_icon);
                }

            }catch (RuntimeException e){

            }finally {
                //2.8设置view标识,3秒内复用
                viewItem.setTag(tagID);
                setTag(tagID);
                //2.9将礼物的View添加到礼物的ViewGroup中
                addView(viewItem);
                //3.0开始执行显示礼物的动画
                if(null!= mItemInAnim){
                    mItemInAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) { }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(mApiType!= LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM&&null!=mItemNumScale){
                                mItemNumScale.setTarget(giftNum);
                                mItemNumScale.start();
                            }
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) { }
                    });
                    viewItem.startAnimation(mItemInAnim);
                }
                if(null!=cleanGiftItemRunnable) RoomGiftItemView.this.postDelayed(cleanGiftItemRunnable, CLEAN_MILLIS);
            }
        //3.此动画View已被添加,直接改变内容
        } else {
            if(null!=cleanGiftItemRunnable) RoomGiftItemView.this.removeCallbacks(cleanGiftItemRunnable);
            //3.1只是中奖动画
            if(msgInfo.getGift().getDrawTimes()>0){
                //超级大奖+倍率动画
                DrawBigMulitAnimationView animationView = (DrawBigMulitAnimationView) viewItem.findViewById(R.id.view_draw_icon);
                //小于5倍的奖励
                if(msgInfo.getGift().getPrize_level()==1){
                    DrawSmallMulitAnimationView tvDrawTimes = (DrawSmallMulitAnimationView) viewItem.findViewById(R.id.tv_draw_times);
                    tvDrawTimes.setBackgroundResource(R.drawable.room_draw_level_bg);
                    tvDrawTimes.startText("中奖"+msgInfo.getGift().getDrawTimes()+"倍!");
                }else if(msgInfo.getGift().getPrize_level()==2){
                    //大于五倍，小于于1000000中奖积分的动画
                    animationView.start(msgInfo.getGift().getDrawTimes());
                    //100倍金币掉落动画
                    if(TextUtils.equals(UserManager.getInstance().getUserId(),msgInfo.getSendUserID())){
                        int[] position = new int[2];
                        getLocationInWindow(position);
                        animationView.setLocationStartPosition(position);
                        animationView.startGoldAnimation();
                    }
                }
            } else {
                //3.2普通的赠送礼物
                GradualTextView giftNum = (GradualTextView) viewItem.findViewById(R.id.view_gift_num);
                int showNum = (Integer) giftNum.getTag()+msgInfo.getGift().getCount();
                SpannableStringBuilder stringBuilder = LiveChatUserGradleSpan.giftNumFromat(String.valueOf(showNum));
                giftNum.setText(stringBuilder);
                //更新最新的缓存池数量
                if(null!=mGiftCountBadge) mGiftCountBadge.put(tagID,showNum);
                giftNum.setTag(showNum);
                if(mApiType!= LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM&&null!=mItemNumScale){
                    mItemNumScale.setTarget(giftNum);
                    mItemNumScale.start();
                }
            }
            if(null!=cleanGiftItemRunnable) RoomGiftItemView.this.postDelayed(cleanGiftItemRunnable, CLEAN_MILLIS);
        }
        return true;
    }

     /**
     * 派生一个全新的礼物ItemView
     * @return
     */
    private View createNewGuftView() {
        if(null==mContext) return null;
        this.removeAllViews();
        //定时三秒后移除自己
        if(null!=cleanGiftItemRunnable) RoomGiftItemView.this.removeCallbacks(cleanGiftItemRunnable);
        View view = LayoutInflater.from(mContext).inflate(R.layout.live_gif_item_layout, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 10;
        view.setLayoutParams(lp);
        return view;
    }

    /**
     * 移除组件元素
     */
    public synchronized void removeGiftView() {
        if(null!= mItemOutAnim &&getChildCount()>0){
            //礼物个数
            GradualTextView textView = (GradualTextView) findViewById(R.id.view_gift_num);
            if(null!=textView){
                textView.clearAnimator();
                textView.clearAnimation();
            }
            //小倍率中奖动画
            DrawSmallMulitAnimationView tvDrawTimes = (DrawSmallMulitAnimationView) findViewById(R.id.tv_draw_times);
            if(null!=tvDrawTimes) {
                tvDrawTimes.setText("");
                tvDrawTimes.setBackgroundResource(0);
                tvDrawTimes.onDestroy();
                tvDrawTimes.clearAnimation();
            }
            //用户头像URL
            RelativeLayout userIconView = (RelativeLayout)findViewById(R.id.view_room_user_icon);
            if(null!=userIconView) userIconView.setTag(null);
            //大倍率中奖动画
            DrawBigMulitAnimationView animationView = (DrawBigMulitAnimationView) findViewById(R.id.view_draw_icon);
            if(null!=animationView){
                animationView.onDestroy();
                animationView.clearAnimation();
            }
            mItemOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) {
                    RoomGiftItemView.this.setTag(null);
                    RoomGiftItemView.this.clearAnimation();
                    RoomGiftItemView.this.removeAllViews();
                    isCleaning=false;
                }
                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
            isCleaning=true;
            if(null!= mItemOutAnim) RoomGiftItemView.this.startAnimation(mItemOutAnim);
        }else{
            RoomGiftItemView.this.setTag(null);
            RoomGiftItemView.this.clearAnimation();
            RoomGiftItemView.this.removeAllViews();
            isCleaning=false;
        }
    }

    /**
     * 返回缓存池中可能存在的相同的礼物&&相同的人赠送的个数，此Cache最长时间维持20秒，在每次用户点击了不同的礼物时候清空cache
     * @param tag
     * @return
     */
    private int getCacheCount(String tag) {
        if(null==tag) return 0;
        if(null== mGiftCountBadge) return 0;
        try {
            return mGiftCountBadge.get(tag);
        }catch (NullPointerException e){// TODO: 2018/6/25 这个地方很奇怪，先强行抛异常吧
            return 0;
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
     * 清空缓存池
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg&&arg instanceof Integer){
            int cmd= (int) arg;
            if(cmd== RoomGiftGroupManager.CANCLE_GIFT_CACHE){
                if(null!= mGiftCountBadge) mGiftCountBadge.clear();
            }
        }
    }

    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }

    /**
     * 对应生命周期调用
     */
    public void onDestroy(){
        this.removeAllViews();
        ApplicationManager.getInstance().removeObserver(this);
        mContext=null; isCleaning=false;
        if (null != mItemInAnim) mItemInAnim.cancel();
        mItemInAnim = null;
        if (null != mItemOutAnim) mItemOutAnim.cancel();
        mItemOutAnim = null;
        if(null != mGiftCountBadge) mGiftCountBadge.clear();
        mGiftCountBadge=null;
        if(null!=cleanGiftItemRunnable) this.removeCallbacks(cleanGiftItemRunnable);
        mOnFunctionListener=null;
        if(null!=mItemNumScale) mItemNumScale.cancel();
        mItemNumScale=null;
    }
 }