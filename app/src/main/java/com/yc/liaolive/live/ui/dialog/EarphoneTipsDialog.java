package com.yc.liaolive.live.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogEarphoneTipsBinding;
import com.yc.liaolive.util.ScreenUtils;

/**
 *  戴上耳机享受 提示弹窗
 *  Created by yangxueqin on 2019/3/6.
 */
public class EarphoneTipsDialog extends BaseDialog<DialogEarphoneTipsBinding> {

    private static final String TAG = "EarphoneTipsDialog";
    private String imgUrl;

    /**
     * 入口
     * @return
     */
    public static EarphoneTipsDialog newInstance(Activity activity, String imgUrl){
        return new EarphoneTipsDialog(activity, imgUrl);
    }

    private EarphoneTipsDialog(Activity activity, String imgUrl) {
        super(activity);
        this.imgUrl = imgUrl;
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_earphone_tips);
        setCanceledOnTouchOutside(false);
        //替换系统默认的背景颜色
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(ScreenUtils.dpToPxInt(80f));
    }

    @Override
    public void initViews() {
        if(!TextUtils.isEmpty(imgUrl)){
            if(imgUrl.endsWith(".GIF")||imgUrl.endsWith(".gif")){
                Glide.with(getContext())
                        .load(imgUrl)
                        .asGif()
                        .placeholder(R.drawable.ic_default_headset_image)
                        .error(R.drawable.ic_default_headset_image)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(bindingView.tipsImage);
            }else{
                Glide.with(getContext())
                        .load(imgUrl)
                        .asBitmap()
                        .placeholder(R.drawable.ic_earphone_tips_bg)
                        .error(R.drawable.ic_earphone_tips_bg)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(bindingView.tipsImage);
            }
        }
        bindingView.tipsClose.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                dismiss();
            }
        });
    }

    public void setLayoutParams(int unWidth) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        systemService.getDefaultDisplay().getMetrics(displayMetrics);
        int hight = LinearLayout.LayoutParams.MATCH_PARENT;
        attributes.height = hight;
        int screenWidth = systemService.getDefaultDisplay().getWidth();
        attributes.width = screenWidth - unWidth;
        attributes.gravity = Gravity.CENTER;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=bindingView){
            bindingView.tipsImage.setImageResource(0);
        }
    }
}