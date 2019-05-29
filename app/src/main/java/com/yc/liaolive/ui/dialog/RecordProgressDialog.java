package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogTranscodingBinding;
import com.yc.liaolive.util.Utils;

/**
 * TinyHung@outlook.com
 * 2017-06-25 19:19
 * 居中样式的进度条
 */

public class RecordProgressDialog extends BaseDialog<DialogTranscodingBinding> {

    public static final int SHOW_MODE1=1;//有数字的进度条
    public static final int SHOW_MODE2=2;//没有数字的进度条

    private boolean isBack;

    public void setBack(boolean back) {
        isBack = back;
    }

    public RecordProgressDialog(Activity context) {
        super(context, R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_transcoding);
        Utils.setDialogWidth(this);
        setCancelable(false);
        bindingView.circleProgressbar.setProgress(0);
    }

    @Override
    public void initViews() {

    }

    public void setTipsMessage(String tips){
        if(null!=bindingView) bindingView.tvLoadingMessage.setText(tips);
    }

    public void setProgress(int progress){
        if(null!=bindingView)bindingView.circleProgressbar.setProgressNotInUiThread(progress);
    }

    /**
     * 设置显示模式
     * @param mode
     */
    public void setMode(int mode){
        if(null==bindingView) return;
        if(SHOW_MODE2==mode){
            bindingView.circleProgressbar.setVisibility(View.INVISIBLE);
            bindingView.progress.setVisibility(View.VISIBLE);
        }else if(SHOW_MODE1==mode){
            bindingView.progress.setVisibility(View.INVISIBLE);
            bindingView.circleProgressbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void show() {
        super.show();
        if(null!=bindingView){
            bindingView.circleProgressbar.setProgress(0);
        }
    }

    @Override
    public void dismiss() {
        if(null!=bindingView){
            bindingView.circleProgressbar.setProgress(0);
        }
        super.dismiss();
    }

    public void setMax(int progress) {
        bindingView.circleProgressbar.setMaxProgress(progress);
    }




    public interface  OnDialogBackListener{
        void onBack();
    }
    private OnDialogBackListener mOnDialogBackListener;

    public void setOnDialogBackListener(OnDialogBackListener onDialogBackListener) {
        mOnDialogBackListener = onDialogBackListener;
    }


    /**
     * 将用户按下返回键时间传递出去
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!isBack){
                if(mOnDialogBackListener!=null){
                    mOnDialogBackListener.onBack();
                }
                return false;
            }else{
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
