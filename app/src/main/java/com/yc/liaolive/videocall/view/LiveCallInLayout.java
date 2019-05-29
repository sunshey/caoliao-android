package com.yc.liaolive.videocall.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.listsner.LiveCallInterface;
import com.yc.liaolive.videocall.manager.AudioPlayerManager;
import com.yc.liaolive.videocall.manager.CallVibratorManager;

/**
 * TinyHung@Outlook.com
 * 2018/11/22
 * 视频通话-呼入
 */

public class LiveCallInLayout extends FrameLayout implements LiveCallInterface {

    private TextView mBtnAccpet;
    private TextView mBtnReject;
    private OnFunctionListener mOnFunctionListener;

    public LiveCallInLayout(@NonNull Context context) {
        this(context,null);
    }

    public LiveCallInLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_call_in_layout,this);
        OnClickListener onClickListener=new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.view_call_tips).getVisibility()==VISIBLE){
                    findViewById(R.id.view_call_tips).setVisibility(GONE);
                }
                switch (v.getId()) {
                    case R.id.view_root_view:
                        break;
                    case R.id.btn_accept:
                        if(null!=mOnFunctionListener) mOnFunctionListener.onAcceptCall();
                        break;
                    case R.id.btn_reject:
                        if(null!=mOnFunctionListener) mOnFunctionListener.onRejectCall();
                        break;
                }
            }
        };
        findViewById(R.id.view_root_view).setOnClickListener(onClickListener);
        mBtnAccpet = findViewById(R.id.btn_accept);
        mBtnReject = findViewById(R.id.btn_reject);
        mBtnAccpet.setOnClickListener(onClickListener);
        mBtnReject.setOnClickListener(onClickListener);
    }

    @Override
    public void onCreate(CallExtraInfo callUserInfo, int userIndetity) {
        if(null!=callUserInfo){
            ImageView toUserAvatar = findViewById(R.id.view_user_head);
            TextView toUserName = findViewById(R.id.view_user_name);
            TextView mackCallPrice = findViewById(R.id.view_tv_price);
            TextView callDesc = findViewById(R.id.view_tv_desc);

            toUserName.setText(callUserInfo.getToNickName());
            mackCallPrice.setText(Html.fromHtml(1==userIndetity?"每分钟<font color='#FF7575'>支出</font>"+callUserInfo.getPrice()+"钻石":"每分钟<font color='#FF7575'>收益</font>"+callUserInfo.getPrice()+"积分"));
            callDesc.setText("新的视频通话");

            Glide.with(getContext())
                    .load(callUserInfo.getToAvatar())
                    .error(R.drawable.ic_default_user_head)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .dontAnimate()
                    .transform(new GlideCircleTransform(getContext()))
                    .into(toUserAvatar);

            AudioPlayerManager.getInstance().startPlayer();
            CallVibratorManager.getInstance().onStart();
        }
    }

    @Override
    public void setOnFunctionListener(LiveCallInterface.OnFunctionListener onFunctionListener) {
        this.mOnFunctionListener=onFunctionListener;
    }

    @Override
    public void setNewMessage(String message) {}

    @Override
    public void onDestroy() {
        mOnFunctionListener=null;mBtnAccpet=null;mBtnReject=null;
        AudioPlayerManager.getInstance().stopPlayer();
        CallVibratorManager.getInstance().onStop();
    }

    public void onStart() {
        AudioPlayerManager.getInstance().startPlayer();
        CallVibratorManager.getInstance().onStart();
    }

    public void onReset(){
        AudioPlayerManager.getInstance().stopPlayer();
        CallVibratorManager.getInstance().onStop();
    }

    //========================================自定义扩展=============================================

    public void setHeadImg(String headImg){
        ImageView imageView = (ImageView) findViewById(R.id.view_user_head);
        //主播头像
        Glide.with(getContext())
                .load(headImg)
                .error(R.drawable.ic_default_user_head)
                .dontAnimate()
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(getContext()))
                .into(imageView);
    }

    public void setNickname(String nickname){
        ((TextView) findViewById(R.id.view_user_name)).setText(nickname);
    }

    public void setDesc(String desc){
        TextView descText = findViewById(R.id.view_tv_desc);
        if (TextUtils.isEmpty(desc)) {
            descText.setVisibility(View.INVISIBLE);
        } else{
            descText.setVisibility(View.VISIBLE);
            descText.setText(desc);
        }
    }

    /**
     * 头像右侧下方文字
     * @param tips "邀请你视频聊天"
     */
    public void setTips(String tips){
        ((TextView) findViewById(R.id.view_tv_price)).setText(tips);
    }

    /**
     * 头像右侧下方文字
     * @param tips "每分钟收入、支付xxx钻石"
     */
    public void setTips(Spanned tips){
        ((TextView) findViewById(R.id.view_tv_price)).setText(tips);
    }

    public void setAcceptText(String acceptText){
        ((TextView) findViewById(R.id.btn_accept)).setText(acceptText);
    }

    public void setRejectText(String rejectText){
        ((TextView) findViewById(R.id.btn_reject)).setText(rejectText);
    }

    public void showCallTipsView(boolean flag){
        findViewById(R.id.view_call_tips).setVisibility(flag?VISIBLE:GONE);
    }

    public void setNickNameTextSize(int size) {
        ((TextView) findViewById(R.id.view_user_name)).setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
    }
}