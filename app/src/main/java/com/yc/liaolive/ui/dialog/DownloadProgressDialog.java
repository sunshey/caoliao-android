package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.view.KeyEvent;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogDownloadTranscodingBinding;
import com.yc.liaolive.util.Utils;
/**
 * TinyHung@outlook.com
 * 2017-06-25 19:19
 * 居中样式的下载进度条
 */
public class DownloadProgressDialog extends BaseDialog<DialogDownloadTranscodingBinding> {

    private boolean isBack=false;

    public DownloadProgressDialog(Activity context) {
        super(context, R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_download_transcoding);
        Utils.setDialogWidth(this);
        setCancelable(false);
        bindingView.circleProgressbar.setProgress(0);
    }

    @Override
    public void initViews() {

    }

    public void setTipsMessage(String tips){
        bindingView.tvLoadingMessage.setText(tips);
    }

    public void setProgress(int progress){
        if(null!=bindingView)bindingView.circleProgressbar.setProgressNotInUiThread(progress);
    }


    public void  setBack(boolean flag){
        this.isBack=flag;
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
        super.dismiss();
    }

    public void setMax(int progress) {
        if(null!=bindingView) bindingView.circleProgressbar.setMaxProgress(progress);
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
