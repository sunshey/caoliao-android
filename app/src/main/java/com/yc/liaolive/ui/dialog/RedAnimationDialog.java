package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogRedAniBinding;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.GoldWireLayout;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TinyHung@Outlook.com
 * 2018/8/25
 * 红包领取动画，从金币处洒落至右下角
 */

public class RedAnimationDialog extends BaseDialog<DialogRedAniBinding> {

    private final Handler mHandler;
    private boolean isRuning;
    private int count;
    private int[] mContentPosition;
    private TimerTask mTask;
    private Timer timer;
    public static final int COUNT=100;

    public static RedAnimationDialog getInstance(Activity context, String content){
        return new RedAnimationDialog(context,content);
    }

    public static RedAnimationDialog getInstance(Activity context, int content){
        return new RedAnimationDialog(context,String.format("成功获得%d个钻石",content));
    }

    public RedAnimationDialog(@NonNull Activity context,String content) {
        super(context, R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_red_ani);
        //替换系统默认的背景颜色
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //全屏
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setCanceledOnTouchOutside(false);//禁止
        setCancelable(false);
        mHandler = new Handler();
        bindingView.digTvContent.setText(content);
    }

    @Override
    public void initViews() {
        //红包领取
        bindingView.digBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //父布局在窗口的位置
                mContentPosition = new int[2];
                bindingView.frameContentView.getLocationInWindow(mContentPosition);
                start();
            }
        });
    }

    /**
     * 开始执行
     */
    private void start(){
        if(isRuning) return;
        isRuning=true;
        //起始位置位于父容器中的 X：23-430 Y:80-190 之间
        mTask = new TimerTask() {
            @Override
            public void run() {
                if(null!=mHandler) mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            GoldWireLayout goldWireLayout = new GoldWireLayout(getContext());
                            int randomNum = Utils.getRandomNum(23, 430);
                            goldWireLayout.setStartPosition(new Point((mContentPosition[0]+randomNum), (mContentPosition[1]+120)));
//                        ViewGroup rootView = (ViewGroup) mActivity.getWindow().getDecorView();
                            bindingView.glodContent.addView(goldWireLayout);
                            int endPosition[] = new int[2];
                            endPosition[0]= (ScreenUtils.getScreenWidth()/2-ScreenUtils.dpToPxInt(5f));
                            endPosition[1]= (ScreenUtils.getScreenHeight()-ScreenUtils.dpToPxInt(60f));
                            goldWireLayout.setEndPosition(new Point(endPosition[0], endPosition[1]));
                            goldWireLayout.startBeizerAnimation(randomNum);
                            count++;
                            if(count>=COUNT){
                                stopTask();
                            }
                        }catch (RuntimeException e){

                        }
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(mTask, 0, 10);
    }

    private void stopTask() {
        if(null!=timer) timer.cancel();
        timer=null;mTask=null;
        isRuning=false; count=0;
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RedAnimationDialog.this.dismiss();
            }
        },1100);
    }
}
