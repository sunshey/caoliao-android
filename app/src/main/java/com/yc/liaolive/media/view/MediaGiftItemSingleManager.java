package com.yc.liaolive.media.view;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
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
import com.yc.liaolive.gift.view.DrawBigMulitAnimationView;
import com.yc.liaolive.live.util.LiveChatUserGradleSpan;
import com.yc.liaolive.gift.view.DrawSmallMulitAnimationView;
import com.yc.liaolive.live.view.GradualTextView;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.bean.MediaGiftInfo;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.ui.fragment.LiveUserDetailsFragment;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.view.widget.MarqueeTextView;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.TreeMap;

/**
 * TinyHung@Outlook.com
 * 2018/11/20
 * 多媒体普通礼物动画预览管理器
 */

public class MediaGiftItemSingleManager extends FrameLayout implements Observer {

    private static final String TAG = "MediaGiftItemLocationChildView";
    private TranslateAnimation mItemInAnim;//礼物进场
    private TranslateAnimation mItemOutAnim;//礼物出场
    private Context mContext;
    private Map<String,Integer> mGiftCountBadge =null;//礼物数量cache标记,用来记录当前正在播放的动画是否在20秒内连击送出的缓存数量；如果是，累加数量
    private boolean isCleaning=false;//是否正处入清除View状态
    private long CLEAN_MILLIS =3000;//3秒后清除自己
    private Queue<MediaGiftInfo> mGroupGiftQueue;//父队列，负责统一接收任务，心跳线程中分发任务给子队列
    private View mViewItem;
    private boolean mDisabledScroll;//是否禁用数量文字滚动动画

    public MediaGiftItemSingleManager(Context context) {
        super(context);
        init(context);
    }

    public MediaGiftItemSingleManager(Context context, AttributeSet attrs) {
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
        ApplicationManager.getInstance().addObserver(this);
    }

    /**
     * 设置自动清空时间
     * @param cleanMilliss
     */
    public void setCleanMilliss(long cleanMilliss){
        this.CLEAN_MILLIS =cleanMilliss;
    }

    /**
     * 添加批量动画至待播放队列
     * @param mediaGiftInfos
     */
    public void addGiftListsToTask(List<MediaGiftInfo> mediaGiftInfos){
        if(null!=mediaGiftInfos&&mediaGiftInfos.size()>0){
            if(null==mGroupGiftQueue) mGroupGiftQueue=new ArrayDeque<>();
            mGroupGiftQueue.addAll(mediaGiftInfos);
            startAutoPlayer(false);
        }
    }

    /**
     * 添加礼物动画至播放队列
     * @param mediaGiftInfo
     */
    public void addGiftToTask(MediaGiftInfo mediaGiftInfo) {
        if(null==mediaGiftInfo) return;
        if(null==mGroupGiftQueue) mGroupGiftQueue=new ArrayDeque<>();
        //礼物的唯一标识
        String tag=mediaGiftInfo.getUserid()+mediaGiftInfo.getGift_id()+mediaGiftInfo.getAccept_userid();
        //当前礼物正在前台展示
        if(null!=getTag()&&TextUtils.equals(tag, (String) getTag())){
            boolean result = addGiftItem(tag, mediaGiftInfo);
            if(!result){
                mGroupGiftQueue.add(mediaGiftInfo);
            }
            //未实例化或者正在队列等待
        }else{
            if(null!=getTag()){
                //检测队列中是否存在相同的礼物
                boolean isAdd=true;
                for (MediaGiftInfo giftInfo : mGroupGiftQueue) {
                    if(tag.equals(giftInfo.getUserid()+giftInfo.getGift_id()+giftInfo.getAccept_userid())){
                        int count = Integer.parseInt(giftInfo.getGift_count()) + Integer.parseInt(mediaGiftInfo.getGift_count());
                        giftInfo.setGift_count(String.valueOf(count));
                        isAdd=false;
                        break;
                    }
                }
                if(isAdd) mGroupGiftQueue.add(mediaGiftInfo);
            }else{
                mGroupGiftQueue.add(mediaGiftInfo);
                startAutoPlayer(false);
            }
        }
    }

    /**
     *  添加组件至此动画ITEM
     * @param tag 防止重复的唯一标识
     * @param msgInfo 动画元素
     * @return 返回添加状态，如果添加失败了，应当返回至总任务队列中重新等待分配，避免丢失
     */
    public boolean addGiftItem(String tag, MediaGiftInfo msgInfo){
        //如果条目正在移除，拦截
        if(isCleaning) return false;
        //1.检查是否重复加入
        mViewItem = this.findViewWithTag(tag);
        //2.全新的入场初始化
        if (null== mViewItem) {
            //2.1添加一个礼物动画Item到容器中
            mViewItem = createNewGuftView();
            if(null== mViewItem) return true;
            //2.2初始化
            MarqueeTextView tvGiftName = (MarqueeTextView) mViewItem.findViewById(R.id.view_tv_gift_name);
            MarqueeTextView tvGiftDesp = (MarqueeTextView) mViewItem.findViewById(R.id.view_tv_gift_desp);
            final GradualTextView giftNum = (GradualTextView) mViewItem.findViewById(R.id.view_gift_num);
            //500倍率动画
            DrawBigMulitAnimationView animationView = (DrawBigMulitAnimationView) mViewItem.findViewById(R.id.view_draw_icon);
            if(null==mGiftCountBadge) mGiftCountBadge=new TreeMap<>();
            //2.3判断是否此礼物是否是在20秒内连击送出的礼物
            int allGiftCount=Integer.parseInt(msgInfo.getGift_count());
            allGiftCount=(allGiftCount+getCacheCount(tag));
            //设置礼物数量
            if(!mDisabledScroll&&allGiftCount>1){
                giftNum.setNumberWithAnim(allGiftCount);
            }else{
                SpannableStringBuilder stringBuilder = LiveChatUserGradleSpan.giftNumFromat(String.valueOf(allGiftCount));
                giftNum.setText(stringBuilder);
            }
            //2.4更新最新的缓存池数量
            mGiftCountBadge.put(tag,allGiftCount);
            if(UserManager.getInstance().getUserId().equals(msgInfo.getUserid())){
                mViewItem.findViewById(R.id.gift_item_bg).setBackgroundResource(R.drawable.gift_item_bg_purple_shape);
            }else{
                mViewItem.findViewById(R.id.gift_item_bg).setBackgroundResource(R.drawable.gift_item_bg_shape);
            }
            //2.6给数量控件设置标记,下次直接找粗此控件复用
            giftNum.setTag(allGiftCount);
            tvGiftName.setText(msgInfo.getNikcname());
            //礼物消息
            tvGiftDesp.setText(Html.fromHtml("<font color='#FFFFFF'>"+msgInfo.getAccept_nikcname()+"</font>"+msgInfo.getGift_title()));
            //2.7中奖处理
            if(msgInfo.getDrawTimes()>0){
                if(msgInfo.getDrawTimes()>=500){
                    animationView.start(msgInfo.getDrawTimes());
                }else{
                    DrawSmallMulitAnimationView tvDrawTimes = (DrawSmallMulitAnimationView) mViewItem.findViewById(R.id.tv_draw_times);
                    tvDrawTimes.setText("恭喜获得x"+msgInfo.getDrawTimes()+"倍");
                }
                // TODO: 2018/8/28 模拟机子赠送的礼物每次都指定金币掉落动画
                if(msgInfo.getDrawTimes()>=100&&UserManager.getInstance().getUserId().equals(msgInfo.getUserid())){
                    int[] position = new int[2];
                    getLocationInWindow(position);
                    animationView.setLocationStartPosition(position);
                    animationView.startGoldAnimation();
                }
            }
            ImageView giftUserIcon = (ImageView) mViewItem.findViewById(R.id.view_gift_user_icon);//用户icon
            RelativeLayout userIconView = (RelativeLayout) mViewItem.findViewById(R.id.view_room_user_icon);//用户iconView
            ImageView ivFiftIcon = mViewItem.findViewById(R.id.view_iv_gift_icon);//礼物icon
            ivFiftIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

            userIconView.setTag(msgInfo.getUserid());
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

            Glide.with(getContext())
                    .load(msgInfo.getAvatar())
                    .error(R.drawable.ic_default_user_head)
                    .crossFade()//渐变
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(mContext))
                    .into(giftUserIcon);
            Glide.with(getContext()).load(msgInfo.getGift_src())
                    .error(R.drawable.ic_default_gift_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(ivFiftIcon);

            //2.8设置view标识,3秒内复用
            mViewItem.setTag(tag);
            setTag(tag);
            //2.9将礼物的View添加到礼物的ViewGroup中
            addView(mViewItem);
            //3.0开始执行显示礼物的动画
            if(null!= mItemInAnim){
                mItemInAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        AnimationUtil.start(giftNum);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
                mViewItem.startAnimation(mItemInAnim);
            }
            if(null!=cleanGiftItemRunnable) MediaGiftItemSingleManager.this.postDelayed(cleanGiftItemRunnable, CLEAN_MILLIS);

            return true;
        }
        //3.此动画View已被添加,直接改变内容
        if(null!=cleanGiftItemRunnable) MediaGiftItemSingleManager.this.removeCallbacks(cleanGiftItemRunnable);
        //3.1只是中奖动画
        if(msgInfo.getDrawTimes()>0){
            //500倍率动画
            DrawBigMulitAnimationView animationView = (DrawBigMulitAnimationView) mViewItem.findViewById(R.id.view_draw_icon);
            if(msgInfo.getDrawTimes()>=500){
                animationView.start(msgInfo.getDrawTimes());
            }else{
                DrawSmallMulitAnimationView tvDrawTimes = (DrawSmallMulitAnimationView) mViewItem.findViewById(R.id.tv_draw_times);
                tvDrawTimes.startText("恭喜获得x "+msgInfo.getDrawTimes()+"倍");
            }

            if(msgInfo.getDrawTimes()>=100&&UserManager.getInstance().getUserId().equals(msgInfo.getUserid())){
                int[] position = new int[2];
                getLocationInWindow(position);
                animationView.setLocationStartPosition(position);
                animationView.startGoldAnimation();
            }
            //3.2普通的赠送礼物
        }else{
            GradualTextView giftNum = (GradualTextView) mViewItem.findViewById(R.id.view_gift_num);
            int showNum = (Integer) giftNum.getTag()+Integer.parseInt(msgInfo.getGift_count());
            SpannableStringBuilder stringBuilder = LiveChatUserGradleSpan.giftNumFromat(String.valueOf(showNum));
            giftNum.setText(stringBuilder);
            //更新最新的缓存池数量
            if(null!=mGiftCountBadge) mGiftCountBadge.put(tag,showNum);
            giftNum.setTag(showNum);
            AnimationUtil.start(giftNum);
        }
        if(null!=cleanGiftItemRunnable) MediaGiftItemSingleManager.this.postDelayed(cleanGiftItemRunnable, CLEAN_MILLIS);

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
        if(null!=cleanGiftItemRunnable) MediaGiftItemSingleManager.this.removeCallbacks(cleanGiftItemRunnable);
        View view = LayoutInflater.from(mContext).inflate(R.layout.live_gif_item_layout, null);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
            if(null!=textView) textView.clearAnimator();
            //小倍率中奖动画
            DrawSmallMulitAnimationView tvDrawTimes = (DrawSmallMulitAnimationView) findViewById(R.id.tv_draw_times);
            if(null!=tvDrawTimes) {
                tvDrawTimes.setText("");
                tvDrawTimes.onDestroy();
            }
            //用户头像URL
            RelativeLayout userIconView = (RelativeLayout)findViewById(R.id.view_room_user_icon);
            if(null!=userIconView) userIconView.setTag(null);
            //大倍率中奖动画
            DrawBigMulitAnimationView animationView = (DrawBigMulitAnimationView) findViewById(R.id.view_draw_icon);
            if(null!=animationView) animationView.onDestroy();

            mItemOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) {
                    MediaGiftItemSingleManager.this.setTag(null);
                    MediaGiftItemSingleManager.this.removeAllViews();
                    isCleaning=false;
                    startAutoPlayer(true);
                }
                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
            isCleaning=true;
            if(null!= mItemOutAnim) MediaGiftItemSingleManager.this.startAnimation(mItemOutAnim);
        }else{
            MediaGiftItemSingleManager.this.setTag(null);
            MediaGiftItemSingleManager.this.removeAllViews();
            isCleaning=false;
        }
    }

    /**
     * 开始礼物动画播放
     * @param isAutoPlayer 是否自动衔接
     */
    private void startAutoPlayer(boolean isAutoPlayer) {
        if(null!=mGroupGiftQueue&&mGroupGiftQueue.size()>0&&null==getTag()){
            MediaGiftInfo poll = mGroupGiftQueue.poll();
            if(null!=poll){
                String tag=poll.getUserid()+poll.getGift_id()+poll.getAccept_userid();
                boolean flag = addGiftItem(tag, poll);
                if(!flag){
                    mGroupGiftQueue.add(poll);
                }
            }
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

    /**
     * 是否禁用数量文字滚动
     * @param disabledScroll
     */
    public void setDisabledScroll(boolean disabledScroll) {
        this.mDisabledScroll=disabledScroll;
    }


    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }

    /**
     * 重置
     */
    public void onReset(){
        removeAllViews();
        if(null!=cleanGiftItemRunnable) this.removeCallbacks(cleanGiftItemRunnable);
        if(null!=mViewItem) this.removeView(mViewItem);
        if(null != mGiftCountBadge) mGiftCountBadge.clear();
        if(null!=mGroupGiftQueue) mGroupGiftQueue.clear();
        mViewItem=null;
        ApplicationManager.getInstance().removeObserver(this);
    }

    /**
     * 对应生命周期调用
     */
    public void onDestroy(){
        this.removeAllViews();
        if(null!=cleanGiftItemRunnable) this.removeCallbacks(cleanGiftItemRunnable);
        mContext=null; isCleaning=false;
        if (null != mItemInAnim) mItemInAnim.cancel();
        mItemInAnim = null;
        if (null != mItemOutAnim) mItemOutAnim.cancel();
        mItemOutAnim = null;
        if(null != mGiftCountBadge) mGiftCountBadge.clear();
        mGiftCountBadge=null;
        if(null!=mGroupGiftQueue) mGroupGiftQueue.clear();
        mGroupGiftQueue=null;mOnFunctionListener=null;cleanGiftItemRunnable=null;
        ApplicationManager.getInstance().removeObserver(this);
    }
}
