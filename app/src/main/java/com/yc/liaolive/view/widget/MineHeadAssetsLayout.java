package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/10/23
 * 主页个人中心资产
 */

public class MineHeadAssetsLayout extends LinearLayout {

    private TextView mAssetsTitle;
    private TextView mAssetsSubTitlw;

    public MineHeadAssetsLayout(Context context) {
        this(context,null);
        init(context,null);
    }

    public MineHeadAssetsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_mine_assets_latyout,this);
        ImageView assetsIcon = (ImageView) findViewById(R.id.view_assets_icon);
        mAssetsTitle = (TextView) findViewById(R.id.view_assets_title);
        mAssetsSubTitlw = (TextView) findViewById(R.id.view_assets_price);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MineHeadAssets);
            String title = typedArray.getString(R.styleable.MineHeadAssets_assetsItemTitle);
            String subTitle = typedArray.getString(R.styleable.MineHeadAssets_assetsItemSubTitle);
            Drawable drawable = typedArray.getDrawable(R.styleable.MineHeadAssets_assetsItemIcon);
            int titleColor = typedArray.getColor(R.styleable.MineHeadAssets_assetsItemTitleColor, Color.parseColor("#555555"));
            int titleSize = typedArray.getInt(R.styleable.MineHeadAssets_assetsItemTitleSize, 15);
            int subTitleColor = typedArray.getColor(R.styleable.MineHeadAssets_assetsItemSubTitleColor, Color.parseColor("#555555"));
            int subTitleSize = typedArray.getInt(R.styleable.MineHeadAssets_assetsItemSubTitleSize, 15);
            mAssetsTitle.setText(title);
            mAssetsTitle.setTextColor(titleColor);
            mAssetsTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP,titleSize);

            mAssetsSubTitlw.setText(subTitle);
            mAssetsSubTitlw.setTextColor(subTitleColor);
            mAssetsSubTitlw.setTextSize(TypedValue.COMPLEX_UNIT_DIP,subTitleSize);
            if(null!=drawable) assetsIcon.setImageDrawable(drawable);
            typedArray.recycle();
        }
    }

    public void setItemTitle(String title){
        if(null!=mAssetsTitle) mAssetsTitle.setText(title);
    }

    public void setItemSubTitle(String title){
        if(null!=mAssetsSubTitlw) mAssetsSubTitlw.setText(title);
    }

    public void setItemSubTitleColor(int color){
        if(null!=mAssetsSubTitlw) mAssetsSubTitlw.setTextColor(color);
    }
}
