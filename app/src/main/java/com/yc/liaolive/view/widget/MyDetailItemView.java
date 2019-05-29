package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.imcore.IFileTrans;
import com.yc.liaolive.R;

/**
 * Created by wanglin  on 2018/7/2 16:18.
 */
public class MyDetailItemView extends FrameLayout {

    private Drawable mDrawable;
    private String mTitle;
    private String mSolidTitle;
    private String mStrokeTitle;

    private boolean mIsSolid;

    private String mPercent;
    private TextView tvItemTitle;
    private ImageView ivItemIcon;
    private TextView tvItemPercent;
    private TextView tvItemStrokeTitle;
    private TextView tvSolidTitle;

    public MyDetailItemView(@NonNull Context context) {
        super(context);

        TypedArray ta = context.obtainStyledAttributes(R.styleable.MyDetailItemView);
        try {
            mDrawable = ta.getDrawable(R.styleable.MyDetailItemView_item_icon);
            mTitle = ta.getString(R.styleable.MyDetailItemView_item_title);
            mSolidTitle = ta.getString(R.styleable.MyDetailItemView_item_solid_text);
            mStrokeTitle = ta.getString(R.styleable.MyDetailItemView_item_stroke_text);
            mIsSolid = ta.getBoolean(R.styleable.MyDetailItemView_item_is_solid, false);
            initView(context);
        } finally {
            ta.recycle();
        }

    }

    public MyDetailItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyDetailItemView);
        try {

            mDrawable = ta.getDrawable(R.styleable.MyDetailItemView_item_icon);
            mTitle = ta.getString(R.styleable.MyDetailItemView_item_title);
            mSolidTitle = ta.getString(R.styleable.MyDetailItemView_item_solid_text);
            mStrokeTitle = ta.getString(R.styleable.MyDetailItemView_item_stroke_text);
            mIsSolid = ta.getBoolean(R.styleable.MyDetailItemView_item_is_solid, false);
            initView(context);
        } finally {
            ta.recycle();
        }

    }


    private void initView(Context context) {
        View.inflate(context, R.layout.my_detail_item_view, this);
        tvItemTitle = findViewById(R.id.item_title);
        ivItemIcon = findViewById(R.id.item_icon);
        LinearLayout solidContainer = findViewById(R.id.ll_solid_container);
        tvItemPercent = findViewById(R.id.item_percent);
        LinearLayout strokeContainer = findViewById(R.id.ll_stroke_container);
        tvItemStrokeTitle = findViewById(R.id.item_stroke_title);
        tvSolidTitle = findViewById(R.id.tv_solid_title);
        if (!TextUtils.isEmpty(mTitle)) {
            tvItemTitle.setText(mTitle);
        }
        if (mDrawable != null) {
            ivItemIcon.setImageDrawable(mDrawable);
        }
        if (!TextUtils.isEmpty(mSolidTitle)) {
            tvSolidTitle.setText(mSolidTitle);
        }
        if (mIsSolid) {
            solidContainer.setVisibility(VISIBLE);
            tvItemPercent.setVisibility(GONE);
            strokeContainer.setVisibility(GONE);
        } else {
            if (TextUtils.isEmpty(mStrokeTitle)) {
                solidContainer.setVisibility(GONE);
                tvItemPercent.setVisibility(VISIBLE);
                strokeContainer.setVisibility(GONE);
                tvItemPercent.setText(getmPercent());
            } else {
                solidContainer.setVisibility(GONE);
                tvItemPercent.setVisibility(GONE);
                strokeContainer.setVisibility(VISIBLE);
                tvItemStrokeTitle.setText(mStrokeTitle);
            }
        }
        solidContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });
        strokeContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });

    }

    public String getmPercent() {
        return mPercent;
    }

    public void setmPercent(String mPercent) {
        this.mPercent = mPercent;
    }

    public Drawable getmDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        this.mDrawable = drawable;
        ivItemIcon.setImageDrawable(drawable);
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
        tvItemTitle.setText(mTitle);
    }

    public String getmSolidTitle() {
        return mSolidTitle;
    }

    public void setSolidTitle(String mSolidTitle) {
        this.mSolidTitle = mSolidTitle;
        tvSolidTitle.setText(mSolidTitle);
    }

    private onBtnClickListener mListener;

    public void setOnBtnClickListener(MyDetailItemView.onBtnClickListener onBtnClickListener) {
        this.mListener = onBtnClickListener;
    }

    public interface onBtnClickListener {
        void onClick();
    }

}
