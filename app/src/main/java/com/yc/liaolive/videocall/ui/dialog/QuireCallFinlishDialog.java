package com.yc.liaolive.videocall.ui.dialog;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.databinding.DialogCallFinlishBinding;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.DateUtil;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.bean.CallCloseExtra;
import com.yc.liaolive.videocall.bean.CallResultInfo;
import com.yc.liaolive.videocall.listsner.OnVideoCallBackListener;
import com.yc.liaolive.videocall.manager.MakeCallManager;

/**
 * TinyHung@Outlook.com
 * 2018/11/9
 * 视频通话已结束
 */

public class QuireCallFinlishDialog extends BaseDialog<DialogCallFinlishBinding> {

    private static final String TAG = "QuireCallFinlishDialog";
    private  AnimationDrawable mAnimationDrawable;

    public static QuireCallFinlishDialog getInstance(Activity context) {
        return new QuireCallFinlishDialog(context);
    }

    public QuireCallFinlishDialog(@NonNull Activity context) {
        super(context,R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_call_finlish);
        Utils.setDialogWidth(this);
        bindingView.llDataView.setVisibility(View.INVISIBLE);
        bindingView.llLoadView.setVisibility(View.VISIBLE);
        mAnimationDrawable = (AnimationDrawable) bindingView.ivLoadingIcon.getDrawable();
        mAnimationDrawable.start();
    }

    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuireCallFinlishDialog.this.dismiss();
            }
        };
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.btnSubmit.setOnClickListener(onClickListener);
    }

    /**
     * 绑定用户信息
     * @param callCloseExtra
     * @return
     */
    public QuireCallFinlishDialog setUserData(CallCloseExtra callCloseExtra) {
        if(null!=callCloseExtra&&null!=bindingView){
            if(TextUtils.isEmpty(callCloseExtra.getToAvatar())){
                UserManager.getInstance().getLifeUserData(UserManager.getInstance().getUserId(), callCloseExtra.getToUserID(), new UserServerContract.OnNetCallBackListener() {
                    @Override
                    public void onSuccess(Object object) {
                        if(null!=object && object instanceof UserInfo){
                            UserInfo userInfo = (UserInfo) object;
                            if(null!=bindingView) {
                                bindingView.oneselfNickname.setText(userInfo.getNickname());
                                LiveUtils.setUserBlockVipGradle(bindingView.oneselfVipGradle,userInfo.getVip());//设置用户vip等级
                                Glide.with(getActivity())
                                        .load(userInfo.getAvatar())
                                        .placeholder(R.drawable.ic_default_user_head)
                                        .error(R.drawable.ic_default_user_head)
                                        .animate(R.anim.item_alpha_in)//加载中动画
                                        .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存源资源和转换后的资源
                                        .centerCrop()//中心点缩放
                                        .skipMemoryCache(true)//跳过内存缓存
                                        .transform(new GlideCircleTransform(getContext()))
                                        .into(bindingView.tipsUserHead);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {

                    }
                });
                return this;
            }
            if(null!=bindingView) {
                bindingView.oneselfNickname.setText(callCloseExtra.getToNickName());
                LiveUtils.setUserBlockVipGradle(bindingView.oneselfVipGradle,0);//设置用户vip等级
                Glide.with(getActivity())
                        .load(callCloseExtra.getToAvatar())
                        .placeholder(R.drawable.ic_default_user_head)
                        .error(R.drawable.ic_default_user_head)
                        .animate(R.anim.item_alpha_in)//加载中动画
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .transform(new GlideCircleTransform(getContext()))
                        .into(bindingView.tipsUserHead);
            }
        }
        return this;
    }

    /**
     * 设置附属的信息
     * @param idType  发起人 1：用户 2：主播
     * @param roomID 所在房间
     */
    public QuireCallFinlishDialog getCallData(int idType,String roomID){
        if(null!=bindingView){
            bindingView.tipsIntegralIcon.setImageResource(1==idType?R.drawable.ic_call_finlish_diamond:R.drawable.ic_call_finlish_integral);
            //获取服务端最新结算信息
            MakeCallManager.getInstance().queryCallData(roomID, new OnVideoCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()){
                        mAnimationDrawable.stop();
                        mAnimationDrawable=null;
                    }
                    if(null!=bindingView&&null!=object && object instanceof CallResultInfo){
                        CallResultInfo callResultInfo= (CallResultInfo) object;
                        long callDurtion = Long.parseLong(callResultInfo.getDurtion());
                        bindingView.tipsDurtion.setText("通话:"+ DateUtil.timeFormat(callDurtion));
                        bindingView.tipsIntegral.setText((1==idType?"消费:":"收入")+callResultInfo.getChat_paid_fee()+(1==idType?"钻石":"积分"));
                    }
                    if(null!=bindingView){
                        bindingView.llLoadView.setVisibility(View.INVISIBLE);
                        bindingView.llDataView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()){
                        mAnimationDrawable.stop();
                        mAnimationDrawable=null;
                    }
                    if(null!=bindingView){
                        bindingView.ivLoadingIcon.setImageResource(0);
                        bindingView.ivLoadingIcon.setVisibility(View.GONE);
                        bindingView.tvMsgContent.setText("网络请求失败，请到【我的积分】查询");
                        bindingView.llLoadView.setVisibility(View.VISIBLE);
                        bindingView.llDataView.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        return this;
    }

    /**
     * 是否显示关闭按钮
     * @param flag
     * @return
     */
    public QuireCallFinlishDialog showCloseBtn(boolean flag){
        if(null!=bindingView){
            bindingView.btnClose.setVisibility(flag?View.VISIBLE:View.GONE);
        }
        return this;
    }

    /**
     * 是否允许按下返回键关闭弹窗
     * @param isCancelable
     * @return
     */
    public QuireCallFinlishDialog setDialogCancelable(boolean isCancelable){
        this.setCancelable(isCancelable);
        return this;
    }

    /**
     * 是否允许触摸边界关闭此弹窗
     * @param isCanceledOnTouchOutside
     * @return
     */
    public QuireCallFinlishDialog setDialogCanceledOnTouchOutside(boolean isCanceledOnTouchOutside){
        this.setCanceledOnTouchOutside(false);
        return this;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()){
            mAnimationDrawable.stop();
            mAnimationDrawable=null;
        }
        if(null!=mOnSubmitClickListener) mOnSubmitClickListener.onDissmiss();
    }

    public abstract static class OnSubmitClickListener{
        public void onSubmit(){}
        public void onDissmiss(){}
    }

    private OnSubmitClickListener mOnSubmitClickListener;

    public QuireCallFinlishDialog setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener) {
        mOnSubmitClickListener = onSubmitClickListener;
        return this;
    }
}
