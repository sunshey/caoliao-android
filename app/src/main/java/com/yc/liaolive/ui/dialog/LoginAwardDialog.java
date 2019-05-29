package com.yc.liaolive.ui.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.content.Context;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogLoginAwardBinding;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.view.gold.FlakeView;

/**
 * TinyHung@Outlook.com
 * 2018/8/10
 * 登录奖励
 */

public class LoginAwardDialog extends BaseDialog<DialogLoginAwardBinding> {

    private static final String TAG = "LoginAwardDialog";
    private final int mMonery;
    public String mContent;
    private FlakeView mFlakeView;

    public static LoginAwardDialog getInstance(Activity activity) {
        return new LoginAwardDialog(activity,"",0);
    }

    /**
     * @param activity
     * @param content
     * @return
     */
    public static LoginAwardDialog getInstance(Activity activity, String content) {
        return new LoginAwardDialog(activity,content,0);
    }

    /**
     *
     * @param activity
     * @param content
     * @param monery 获得奖励钻石
     * @return
     */
    public static LoginAwardDialog getInstance(Activity activity, String content,int monery) {
        return new LoginAwardDialog(activity,content,monery);
    }

    public LoginAwardDialog(@NonNull Activity context, String content,int monery) {
        super(context, R.style.CenterDialogAnimationStyle);
        this.mContent=content;
        this.mMonery=monery;
        setContentView(R.layout.dialog_login_award);
        //替换系统默认的背景颜色
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //全屏
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setCanceledOnTouchOutside(false);//禁止
        VideoApplication.getInstance().setMineRefresh(true);//首页需要刷新
    }

    @Override
    public void initViews() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //关闭
                    case R.id.btn_close:
                        LoginAwardDialog.this.dismiss();
                        break;
                    case R.id.dig_tv_submit:
                        startAnimation();
                        break;

                }
            }
        };
        if(!TextUtils.isEmpty(mContent))bindingView.digTvContent.setText(mContent);
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.digTvSubmit.setOnClickListener(onClickListener);
    }

    public LoginAwardDialog setBg(int resID){
        if(null!=bindingView) bindingView.dialogBg.setImageResource(resID);
        return this;
    }

    /**
     * 开始播放动画
     */
    private void startAnimation() {
        this.setCancelable(false);//禁止手动关闭弹窗]
        if(null==getActivity()) return;
        final int screenHeight = ScreenUtils.getScreenHeight();
        final int screenWidth = ScreenUtils.getScreenWidth();
        //用户控制视图 --透明--
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(bindingView.llUserView, "alpha", 1.0f, 0.0f).setDuration(500);
        alphaAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bindingView.tvMoneryCount.setText("+"+mMonery);
                mFlakeView = new FlakeView(getActivity());
                mFlakeView.addFlakes(108);
                mFlakeView.setLayerType(View.LAYER_TYPE_NONE, null);
                FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
                mFlakeView.setLayoutParams(layoutParams);
                try {
                    //直接修改系统音量为最大音量
                    AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),AudioManager.FLAG_PLAY_SOUND);

                }catch (RuntimeException e){

                }catch (Exception e){

                }finally {
                    try {
                        MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.shake);
                        player.start();
                    }catch (RuntimeException e){

                    }catch (Exception e){

                    }
                }
                bindingView.frameLayout.addView(mFlakeView);

                //--金币数字放大--
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(bindingView.tvMoneryCount, "scaleX", 1.0f, 1.6f).setDuration(600);//X方向放大
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(bindingView.tvMoneryCount, "scaleY", 1.0f, 1.6f).setDuration(600);//Y方向放大
                AnimatorSet animSet1 = new AnimatorSet();
                animSet1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //--金币数字右下角平滑移动--
                        ObjectAnimator animator3 = ObjectAnimator.ofFloat(bindingView.tvMoneryCount, "translationY", 0.0f, (screenHeight/2) / 1f).setDuration(2000);
                        ObjectAnimator animator4 = ObjectAnimator.ofFloat(bindingView.tvMoneryCount, "translationX", 0.0f, (screenWidth/2) / 1f).setDuration(2000);
                        ObjectAnimator animator5 = ObjectAnimator.ofFloat(bindingView.tvMoneryCount, "scaleX", 1.6f, 0.5f).setDuration(2000);//X方向缩小
                        ObjectAnimator animator6 = ObjectAnimator.ofFloat(bindingView.tvMoneryCount, "scaleY", 1.6f, 0.5f).setDuration(2000);//Y方向缩小
                        AnimatorSet animSet2 = new AnimatorSet();
                        animSet2.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                LoginAwardDialog.this.dismiss();//所有的动画结束了
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                super.onAnimationRepeat(animation);
                            }
                        });
                        animSet2.setStartDelay(1000);
                        animSet2.playTogether(animator3,animator4,animator5,animator6);
                        animSet2.start();
                    }
                });
                animSet1.setInterpolator(new BounceInterpolator());//抖动
                //开始播放金币放大动画
                animSet1.playTogether(animator1,animator2);
                animSet1.start();
            }
        });
        alphaAnimation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mFlakeView) mFlakeView.onDestroy();
        mFlakeView=null;
    }
}

