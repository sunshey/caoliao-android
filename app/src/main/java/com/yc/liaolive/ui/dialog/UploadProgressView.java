package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogLoadProgressLayoutBinding;
import com.yc.liaolive.util.Utils;
/**
 * TinyHung@outlook.com
 * 2017/3/25 15:16
 * 上传圆形进度条
 */
public class UploadProgressView extends BaseDialog <DialogLoadProgressLayoutBinding>{
    private boolean isBack=false;
    public UploadProgressView(Activity context, boolean isShowProgress) {
        super(context, R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_load_progress_layout);
        if(isShowProgress){
            bindingView.circleProgressbar.setVisibility(View.VISIBLE);
        }else{
            bindingView.circleProgressbar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void initViews() {
        Utils.setDialogWidth(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    /**
     * 初始化进度条设置
     */
    public void initProgressBar() {
        bindingView.circleProgressbar.setVisibility(View.VISIBLE);
        bindingView.circleProgressbar.setProgress(0);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=bindingView){
            bindingView.circleProgressbar.setVisibility(View.VISIBLE);
            bindingView.circleProgressbar.setProgress(0);
        }
    }

    /**
     * 设置文字
     * @param message
     */
    public void setMessage(String message){
        bindingView.tvLoadingMessage.setText(message);
        if(null!=bindingView) bindingView.circleProgressbar.setVisibility(View.VISIBLE);
    }



    /**
     * 设置当前进度
     * @param progress
     */
    public void setProgressNotInUiThread(int progress) {
        if(null!=bindingView){
            bindingView.circleProgressbar.setProgressNotInUiThread(progress);
        }
    }

    public void setProgress(int progress){
        if(null!=bindingView){
            bindingView.circleProgressbar.setProgress(progress);
        }
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

    public void setMax(int maxProgress) {
        if(null!=bindingView) bindingView.circleProgressbar.setMaxProgress(maxProgress);
    }

    public interface  OnDialogBackListener{
        void onBack();
    }
    private OnDialogBackListener mOnDialogBackListener;
    public void setOnDialogBackListener(OnDialogBackListener onDialogBackListener) {
        mOnDialogBackListener = onDialogBackListener;
    }
}
