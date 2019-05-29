package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.util.ScreenUtils;

/**
 * TinyHung@Outlook.com
 * 2018/10/17
 * 通用的 ITEM栏目
 */

public class CommenItemLayout  extends LinearLayout{

    private ImageView mItemIcon;
    private TextView mTitleView;
    private TextView mMoreTitleView;
    private ImageView mMoreIcon;

    public CommenItemLayout(Context context) {
        this(context,null);
    }

    public CommenItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_commen_item_layout,this);
        mItemIcon = (ImageView) findViewById(R.id.view_item_icon);
        mTitleView = (TextView) findViewById(R.id.view_item_title);
        mMoreTitleView = (TextView) findViewById(R.id.view_more_title);
        mMoreIcon = (ImageView) findViewById(R.id.view_more_icon);
        View viewLine =  findViewById(R.id.view_line);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommenItemLayout);
            Resources resources = getContext().getResources();
            String title = typedArray.getString(R.styleable.CommenItemLayout_itemTitle);
            int titleColor = typedArray.getInt(R.styleable.CommenItemLayout_itemTitleColor,resources.getColor(R.color.coment_color));
            float titleSize = typedArray.getDimensionPixelSize(R.styleable.CommenItemLayout_itemTitleSize, 16);
            String moreTitle = typedArray.getString(R.styleable.CommenItemLayout_itemMoreTitle);
            int moreTitleColor = typedArray.getInt(R.styleable.CommenItemLayout_itemMoreTitleColor, resources.getColor(R.color.gray));
            float moreTitleSize = typedArray.getDimensionPixelSize(R.styleable.CommenItemLayout_itemTitleSize, 14);
            boolean showLine = typedArray.getBoolean(R.styleable.CommenItemLayout_itemShowLine, false);
            boolean showMore = typedArray.getBoolean(R.styleable.CommenItemLayout_itemShowMoreIcon, true);
            boolean showIcon = typedArray.getBoolean(R.styleable.CommenItemLayout_commentShowIcon, false);
            mItemIcon.setVisibility(showIcon?VISIBLE:GONE);
            mTitleView.setText(title);
            mTitleView.setTextColor(titleColor);
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,titleSize);

            mMoreTitleView.setText(moreTitle);
            mMoreTitleView.setTextColor(moreTitleColor);
            mMoreTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,moreTitleSize);
            Drawable itemIconDrawable = typedArray.getDrawable(R.styleable.CommenItemLayout_itemIcon);
            if(null!=itemIconDrawable){
                mItemIcon.setBackground(itemIconDrawable);
            }
            viewLine.setVisibility(showLine?VISIBLE:GONE);
            Drawable drawable = typedArray.getDrawable(R.styleable.CommenItemLayout_itemMoreTitleIcon);
            mMoreIcon.setVisibility(showMore?VISIBLE:GONE);
            if(null!=drawable){
                mMoreIcon.setImageDrawable(drawable);
            }
            int itemHeight = typedArray.getDimensionPixelSize(R.styleable.CommenItemLayout_commentItemHeight, ScreenUtils.dpToPxInt(48f));
            LinearLayout itemView = (LinearLayout) findViewById(R.id.view_root_item);
            itemView.getLayoutParams().height=itemHeight;

            typedArray.recycle();
        }
    }

    public void setItemTitle(String title){
        if(null!=mTitleView) mTitleView.setText(title);
    }

    public void setItemTitleColor(int color){
        if(null!=mTitleView) mTitleView.setTextColor(color);
    }

    public void setItemTitleSize(int size){
        if(null!=mTitleView) mTitleView.setTextSize(size);
    }

    public void setItemIcon(int resID){
        if(null!=mItemIcon) mItemIcon.setImageResource(resID);
    }

    public void setItemIcon(String path){
        if(null!=mItemIcon) {
            Glide.with(getContext())
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(mItemIcon);
        }
    }
    public void setItemMoreTitle(String title){
        if(null!=mMoreTitleView) mMoreTitleView.setText(title);
    }
    public void setItemMoreTitleColor(int color){
        if(null!=mMoreTitleView) mMoreTitleView.setTextColor(color);
    }

    public void setItemMoreTitleSize(int size){
        if(null!=mMoreTitleView) mMoreTitleView.setTextSize(size);
    }

    public String getTitleTextContent(){
        if(null!=mTitleView) return mTitleView.getText().toString();
        return "";
    }

    public String getMoreTextContent(){
        if(null!=mMoreTitleView) return mMoreTitleView.getText().toString();
        return "";
    }

    public void showLine(boolean isLine) {
        View viewLine =  findViewById(R.id.view_line);
        if(null!=viewLine){
            viewLine.setVisibility(isLine?VISIBLE:GONE);
        }
    }

    public void showLine(boolean isLine,boolean showTopBottomSpace) {
        View viewLine =  findViewById(R.id.view_line);
        if(null!=viewLine){
            viewLine.setVisibility(isLine?VISIBLE:GONE);
        }
        showTopSpate(showTopBottomSpace);
        showBottomSpate(showTopBottomSpace);
    }

    public void showTopSpate(boolean flag){
        findViewById(R.id.view_top_space).setVisibility(flag?VISIBLE:GONE);
    }

    public void showBottomSpate(boolean flag){
        findViewById(R.id.view_bottom_space).setVisibility(flag?VISIBLE:GONE);
    }
}