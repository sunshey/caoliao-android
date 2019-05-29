package com.yc.liaolive.live.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialogFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.databinding.FragmentLiveErrorBinding;

/**
 *  TinyHung@outlook.com
 *  2016/5/16
 *  直播结束后的详情
 */
public class LiveErrorFragment extends BaseDialogFragment<FragmentLiveErrorBinding,RxBasePresenter> {

    private String mContent;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_live_error;
    }

    @Override
    protected void initViews() {
        bindingView.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveErrorFragment.this.dismiss();
            }
        });
        bindingView.tvContent.setText(mContent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mContent = arguments.getString("content");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(null!=mOnDissmissListener) mOnDissmissListener.onDissmiss();
    }

    public interface OnDissmissListener{
        void onDissmiss();
    }

    private OnDissmissListener mOnDissmissListener;

    public void setOnDissmissListener(OnDissmissListener onDissmissListener) {
        mOnDissmissListener = onDissmissListener;
    }
}
