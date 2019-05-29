package com.yc.liaolive.videocall.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.media.manager.LiveVideoPlayerManager;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.listsner.LiveCallInterface;
import com.yc.liaolive.videocall.manager.AudioPlayerManager;
import com.yc.liaolive.videocall.manager.CallVibratorManager;

/**
 * TinyHung@Outlook.com
 * 2018/11/14
 * 视频通话-呼出
 */

public class LiveCallOutLayout extends FrameLayout implements LiveCallInterface {

    private View mTipsView;
    private LiveVideoPlayerManager mPlayerManager;
    private OnFunctionListener mOnFunctionListener;

    public LiveCallOutLayout(@NonNull Context context) {
        this(context,null);
    }

    public LiveCallOutLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_call_out_layout,this);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mTipsView&&mTipsView.getVisibility()!=GONE) mTipsView.setVisibility(GONE);
            }
        });
        mTipsView = (View) findViewById(R.id.view_call_tips);
        TextView cancel = findViewById(R.id.view_btn_cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mTipsView&&mTipsView.getVisibility()!=GONE){
                    mTipsView.setVisibility(GONE);
                    return;
                }
                if(null!=mOnFunctionListener) mOnFunctionListener.onRejectCall();
            }
        });
    }


    @Override
    public void onCreate(CallExtraInfo callUserInfo, int userIndetity) {
        if (null != callUserInfo) {
            ImageView sendeeAvatar = findViewById(R.id.view_sendee_avatar);
            TextView sendeeNickName = findViewById(R.id.view_sendee_name);
            TextView mackCallPrice = findViewById(R.id.view_call_out_price);
            sendeeNickName.setText(callUserInfo.getToNickName());
            mackCallPrice.setText(Html.fromHtml(1 == userIndetity ? "每分钟<font color='#FF7575'>支出</font>" + callUserInfo.getPrice() + "钻石" : "每分钟<font color='#FF7575'>收益</font>" + callUserInfo.getPrice() + "积分"));
            //主播头像
            Glide.with(getContext())
                    .load(callUserInfo.getToAvatar())
                    .error(R.drawable.ic_default_user_head)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .dontAnimate()
                    .transform(new GlideCircleTransform(getContext()))
                    .into(sendeeAvatar);

            //只有用户方才显示呼叫提示
            if(1== userIndetity) AnimationUtil.visibTransparentView(mTipsView,1000);
            //主播封面不为空
            if(!TextUtils.isEmpty(callUserInfo.getAnchorFront())){
                Glide.with(getContext())
                        .load(callUserInfo.getAnchorFront())
                        .thumbnail(0.1f)
                        .dontAnimate()
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .into(((ImageView) findViewById(R.id.view_video_cover)));
            }
            //主播视频不为空
            if(!TextUtils.isEmpty(callUserInfo.getVideoPath())){
                mPlayerManager = (LiveVideoPlayerManager) findViewById(R.id.video_view);
                mPlayerManager.setLooping(true);
                mPlayerManager.setMuteMode(true);
                mPlayerManager.startPlay(callUserInfo.getVideoPath(),true);
            }

            AudioPlayerManager.getInstance().startPlayer();
            CallVibratorManager.getInstance().onStart();
        }
    }

    @Override
    public void setOnFunctionListener(LiveCallInterface.OnFunctionListener onFunctionListener) {
        this.mOnFunctionListener=onFunctionListener;
    }

    @Override
    public void setNewMessage(String message) {
        if(null!=findViewById(R.id.view_connect)) ((TextView) findViewById(R.id.view_connect)).setText(message);
    }

    @Override
    public void onDestroy() {
        if (null!= mPlayerManager){
            mPlayerManager.onDestroy();
            mPlayerManager=null;
        }
        mTipsView=null;mOnFunctionListener=null;
        AudioPlayerManager.getInstance().stopPlayer();
        CallVibratorManager.getInstance().onStop();
    }
}