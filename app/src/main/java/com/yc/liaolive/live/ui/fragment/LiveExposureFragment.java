package com.yc.liaolive.live.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import com.yc.liaolive.R;

/**
 *  TinyHung@outlook.com
 *  2018/8/3
 *  直播曝光
 */

public class LiveExposureFragment extends android.support.v4.app.DialogFragment {

    private static final String TAG = LiveExposureFragment.class.getSimpleName();
    private static int mProgress;

    public static LiveExposureFragment getInstance(int progress){
        mProgress=progress;
        return new LiveExposureFragment();
    }

    public interface OnBeautyParamsChangeListener{
        void onParamsChange(int progress);
    }

    private OnBeautyParamsChangeListener mBeautyParamsChangeListener;

    public LiveExposureFragment setBeautyParamsChangeListener(OnBeautyParamsChangeListener beautyParamsChangeListener) {
        mBeautyParamsChangeListener = beautyParamsChangeListener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_exposure_area);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消
        SeekBar exposureSeekbar = (SeekBar) dialog.findViewById(R.id.exposure_seekbar);
        exposureSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mBeautyParamsChangeListener instanceof OnBeautyParamsChangeListener){
                    mBeautyParamsChangeListener.onParamsChange(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        exposureSeekbar.setProgress(mProgress * exposureSeekbar.getMax() / 9);
        dialog.findViewById(R.id.root_view).getBackground().setAlpha(230);
        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);
        return dialog;
    }
}