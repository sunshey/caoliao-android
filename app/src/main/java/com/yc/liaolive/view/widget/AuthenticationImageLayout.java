package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.yc.liaolive.R;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2018/10/17
 * 身份认证 图片选取
 */

public class AuthenticationImageLayout extends FrameLayout {

    private static final String TAG = "AuthenticationImageLayout";
    private ImageView mViewCover;
    private TextView mViewTitle;

    public AuthenticationImageLayout(@NonNull Context context) {
        this(context,null);
    }

    public AuthenticationImageLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_authentication_image_layout,this);
        mViewCover = (ImageView) findViewById(R.id.view_cover);
        mViewTitle = (TextView) findViewById(R.id.view_title);
        //添加照片
        findViewById(R.id.view_root_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnItemClickListener) mOnItemClickListener.onClickkAdd(AuthenticationImageLayout.this);
            }
        });
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AuthenticationImageLayout);
            String title = typedArray.getString(R.styleable.AuthenticationImageLayout_authTitle);
            mViewTitle.setText(title);
            Drawable drawable = typedArray.getDrawable(R.styleable.AuthenticationImageLayout_authDefaultIcon);
            if(null!=drawable){
                mViewCover.setImageDrawable(drawable);
            }
            typedArray.recycle();
        }
    }

    /**
     * 设置新的图片
     * @param imageUrl
     */
    public void setImageData(String imageUrl){
        if(null==imageUrl) return;
        if(null!=mViewCover){
            Glide.with(getContext())
                    .load(imageUrl)
                    .error(R.drawable.signature_front)
                    .placeholder(mViewCover.getDrawable())
                    .dontAnimate()
                    .skipMemoryCache(true)
                    .into(mViewCover);
            this.setTag(imageUrl);
            View viewShade = findViewById(R.id.view_shade);
            if(null!=viewShade&&viewShade.getVisibility()!=GONE) viewShade.setVisibility(GONE);
        }
    }

    /**
     * 设置图片控件宽高
     * @param width
     * @param height
     */
    public void setImageLayoutParams(int width,int height){
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.view_image_layout);
        if(null!=relativeLayout){
            ViewGroup.LayoutParams layoutParams = relativeLayout.getLayoutParams();
            layoutParams.width=width;
            layoutParams.height=height;
            relativeLayout.setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        if(null!=mViewTitle) mViewTitle.setText(title);
    }

    /**
     * 获取已设置完成的身份证照片信息
     * @return
     */
    public String getImageUrl(){
        return ((String) getTag());
    }

    public interface OnItemClickListener{
        void onClickkAdd(View view);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
