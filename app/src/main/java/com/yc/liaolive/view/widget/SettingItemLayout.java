package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.util.ScreenUtils;

/**
 * TinyHung@Outlook.com
 * 2019/1/12
 * 设置中心ITEM
 */

public class SettingItemLayout extends LinearLayout{

    private TextView mTitleView;
    private SwitchButton mSwitchButton;
    private RelativeLayout mBtnItem;

    public SettingItemLayout(Context context) {
        this(context,null);
    }

    public SettingItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_setting_item_layout,this);
        mTitleView = (TextView) findViewById(R.id.view_setting_item_text);
        mSwitchButton = (SwitchButton) findViewById(R.id.setting_switch_btn);
        View viewLine =  findViewById(R.id.setting_item_line);
        mBtnItem = (RelativeLayout) findViewById(R.id.btn_online_setting);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItemLayout);
            String title = typedArray.getString(R.styleable.SettingItemLayout_itemSettingTitle);
            int titleColor = typedArray.getInt(R.styleable.SettingItemLayout_itemSettingTitleColor, Color.parseColor("#ff313131"));
            boolean showLine = typedArray.getBoolean(R.styleable.SettingItemLayout_itemSettingShowLine, true);
            int itemHeight = typedArray.getDimensionPixelSize(R.styleable.SettingItemLayout_itemSettingItemHeight, ScreenUtils.dpToPxInt(45f));//Item高度
            mBtnItem.getLayoutParams().height=itemHeight;
            mTitleView.setText(title);
            mTitleView.setTextColor(titleColor);
            String textOff = typedArray.getString(R.styleable.SettingItemLayout_itemSettingItemTextOff);
            String textOn = typedArray.getString(R.styleable.SettingItemLayout_itemSettingItemTextOn);
            mSwitchButton.setText(textOn,textOff);
            viewLine.setVisibility(showLine?VISIBLE:GONE);
            typedArray.recycle();
        }
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(null!=mOnSettingSwitchListener) mOnSettingSwitchListener.onCheckedChanged(buttonView,isChecked);
            }
        });
    }

    /**
     * 是否选中
     * @param isChecked
     */
    public void setChecked(boolean isChecked) {
        if(null!=mSwitchButton) mSwitchButton.setChecked(isChecked);
    }

    /**
     * 是否禁用
     * @param isEnabled
     */
    public void setSwitchEnabled(boolean isEnabled){
        if(null!=mSwitchButton) mSwitchButton.setEnabled(isEnabled);
    }

    public void setItemClickable(boolean isClickable) {
        if(isClickable&&null!=mBtnItem){
            mBtnItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mSwitchButton) mSwitchButton.setChecked(!mSwitchButton.isChecked());
                }
            });
        }
    }

    public interface OnSettingSwitchListener{
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }

    private OnSettingSwitchListener mOnSettingSwitchListener;

    public void setOnSettingSwitchListener(OnSettingSwitchListener onSettingSwitchListener) {
        mOnSettingSwitchListener = onSettingSwitchListener;
    }
}
