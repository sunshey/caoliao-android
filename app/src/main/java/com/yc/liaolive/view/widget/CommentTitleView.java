package com.yc.liaolive.view.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.StatusUtils;

/**
 * TinyHung@Outlook.com
 * 2018/5/29
 * 通用的标题栏
 */

public class CommentTitleView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "CommentTitleView";
    public static final int STYLE_LIGHT=0;//白底
    public static final int STYLE_COLOR=1;//彩底
    private int mTitleStyle=STYLE_LIGHT;//默认是白底样式
    private boolean isShowMoreTitle;
    private TextView moreTitle;
    private MarqueeTextView mTitleView;
    private AnimationDrawable mLoadAnimationDrawable;
    private long[] clickCount = new long[3];

    public CommentTitleView(Context context) {
        super(context);
        init(context,null);
    }

    public CommentTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context,R.layout.view_comment_title_layout,this);
        ImageView btnback = (ImageView) findViewById(R.id.view_btn_back);
        mTitleView = (MarqueeTextView) findViewById(R.id.view_title);
        moreTitle = (TextView) findViewById(R.id.view_more_title);
        TextView  viewBackTitle = (TextView) findViewById(R.id.view_back_title);
        ImageView btnMenu2 = (ImageView) findViewById(R.id.btn_menu2);
        ImageView btnMenu1 = (ImageView) findViewById(R.id.btn_menu1);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommentTitleView);
            //返回按钮
            boolean isShowBack = typedArray.getBoolean(R.styleable.CommentTitleView_commentShowBack, true);
            Drawable backDrawable = typedArray.getDrawable(R.styleable.CommentTitleView_commentBackRes);
            if(null!=backDrawable){
                btnback.setImageDrawable(backDrawable);
            }
            btnback.setVisibility(isShowBack?VISIBLE:GONE);
            Resources resources = getContext().getResources();
            //标题
            String titleText = typedArray.getString(R.styleable.CommentTitleView_commentTitle);
            int titleColor = typedArray.getColor(R.styleable.CommentTitleView_commentTitleColor,resources.getColor(R.color.coment_color));
            float titleSize = typedArray.getDimensionPixelSize(R.styleable.CommentTitleView_commentTitleSize, 18);
            //副标题
            String subTitleText = typedArray.getString(R.styleable.CommentTitleView_commentSubTitle);
            int subTitleColor = typedArray.getColor(R.styleable.CommentTitleView_commentSubTitleColor,resources.getColor(R.color.colorTextG6));
            float subTitleSize = typedArray.getDimensionPixelSize(R.styleable.CommentTitleView_commentSubTitleSize, 16);
            boolean isShowSubTitle = typedArray.getBoolean(R.styleable.CommentTitleView_commentShowSubTitle, false);
            //更多标题
            String moreTitleText = typedArray.getString(R.styleable.CommentTitleView_commentMoreTitle);
            int moreTitleColor = typedArray.getColor(R.styleable.CommentTitleView_commentMoreTitleColor,resources.getColor(R.color.colorTextG6));
            float moreTitleSize = typedArray.getDimensionPixelSize(R.styleable.CommentTitleView_commentTitleSize, 16);
            isShowMoreTitle = typedArray.getBoolean(R.styleable.CommentTitleView_commentShowMoreTitle, false);
            //扩展功能菜单1
            boolean rightMenu1 = typedArray.getBoolean(R.styleable.CommentTitleView_commentShowMenu1, false);//是否显示右侧菜单1
            Drawable rightMenuDrawable1 = typedArray.getDrawable(R.styleable.CommentTitleView_commentMenuSrc1);//右侧菜单1资源文件
            //扩展功能菜单1
            boolean rightMenu2 = typedArray.getBoolean(R.styleable.CommentTitleView_commentShowMenu2, false);//是否显示右侧菜单2
            Drawable rightMenuDrawable2 = typedArray.getDrawable(R.styleable.CommentTitleView_commentMenuSrc2);//右侧菜单2资源文件

            //标题
            mTitleView.setText(titleText);
            mTitleView.setTextColor(titleColor);
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,titleSize);
            //副标题
            viewBackTitle.setText(subTitleText);
            viewBackTitle.setTextColor(subTitleColor);
            viewBackTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP,subTitleSize);
            viewBackTitle.setVisibility(isShowSubTitle?VISIBLE:GONE);
            //更多标题
            moreTitle.setText(moreTitleText);
            moreTitle.setTextColor(moreTitleColor);
            moreTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP,moreTitleSize);
            moreTitle.setVisibility(isShowMoreTitle ?VISIBLE:GONE);

            btnback.setVisibility(isShowBack?VISIBLE:GONE);
            //菜单1
            btnMenu1.setImageDrawable(rightMenuDrawable1);
            btnMenu1.setVisibility(rightMenu1?VISIBLE:GONE);
            //菜单2
            btnMenu2.setImageDrawable(rightMenuDrawable2);
            btnMenu2.setVisibility(rightMenu2?VISIBLE:GONE);
            //主题样式,支持亮色和暗色两种，代码中调用setBtnBackBackground(Drawable drawable,boolean changSkin);changSkin:为true，即换肤
            mTitleStyle = typedArray.getInt(R.styleable.CommentTitleView_commentTitleStyle, STYLE_LIGHT);
            typedArray.recycle();
        }
        btnback.setOnClickListener(this);
        mTitleView.setOnClickListener(this);
        moreTitle.setOnClickListener(this);
        btnMenu1.setOnClickListener(this);
        btnMenu2.setOnClickListener(this);
        findViewById(R.id.status_bar_19).setVisibility(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP ? VISIBLE : GONE);
        //用户未指定自定义样式，使用默认的
        if(mTitleStyle<=0){
            btnMenu1.setColorFilter(Color.parseColor("#666666"));
            btnMenu2.setColorFilter(Color.parseColor("#666666"));
            findViewById(R.id.root_top_bar).setBackgroundColor(Color.parseColor("#FFFFFF"));
            StatusUtils.setStatusTextColor1(true,(Activity) getContext());//白色背景，黑色字体
            return;
        }
        setTitleBarStyle(mTitleStyle);
    }

    /**
     * 设置标题栏样式
     * @param titleStyle 0：白色黑字 1：彩底白字
     */
    public void setTitleBarStyle(int titleStyle) {
        ImageView btnback = (ImageView) findViewById(R.id.view_btn_back);//返回按钮
        MarqueeTextView titleView = (MarqueeTextView) findViewById(R.id.view_title);//标题
        TextView  viewSubTitle = (TextView) findViewById(R.id.view_back_title);//返回按钮文字
        TextView  moreTitle = (TextView) findViewById(R.id.view_more_title);//扩展更多
        ImageView btnMenu2 = (ImageView) findViewById(R.id.btn_menu2);//扩展按钮2
        ImageView btnMenu1 = (ImageView) findViewById(R.id.btn_menu1);//扩展按钮1
        Drawable drawable=getResources().getDrawable(R.color.white);
        if(titleStyle==STYLE_LIGHT){//白底黑字
            if(null!=btnback) btnback.setImageResource(R.drawable.ban_nav_menu_back_selector_black);
            if(null!=titleView) titleView.setTextColor(getContext().getResources().getColor(R.color.coment_color));
            if(null!=moreTitle) moreTitle.setTextColor(getContext().getResources().getColor(R.color.colorTabText));
            if(null!=viewSubTitle) viewSubTitle.setTextColor(getContext().getResources().getColor(R.color.colorTabText));
            if(null!=btnMenu1) btnMenu1.setColorFilter(getContext().getResources().getColor(R.color.colorTabText));
            if(null!=btnMenu2)btnMenu2.setColorFilter(getContext().getResources().getColor(R.color.colorTabText));
            drawable= getResources().getDrawable(R.color.white);
//            findViewById(R.id.view_line).setVisibility(VISIBLE);
            StatusUtils.setStatusTextColor1(true,(Activity) getContext());//白色背景，黑色字体
        }else if(titleStyle==STYLE_COLOR){//彩底白字
            if(null!=btnback) btnback.setImageResource(R.drawable.ban_nav_menu_back_selector_white);
            if(null!=titleView) titleView.setTextColor(getContext().getResources().getColor(R.color.white));
            if(null!=moreTitle) moreTitle.setTextColor(getContext().getResources().getColor(R.color.white));
            if(null!=viewSubTitle) viewSubTitle.setTextColor(getContext().getResources().getColor(R.color.white));
            if(null!=btnMenu1) btnMenu1.setColorFilter(Color.WHITE);
            if(null!=btnMenu2)btnMenu2.setColorFilter(Color.WHITE);
            drawable= getResources().getDrawable(R.drawable.home_top_bar_bg_shape);
//            findViewById(R.id.view_line).setVisibility(GONE);
            StatusUtils.setStatusTextColor1(false,(Activity) getContext());//透明背景，白色字体
        }
        findViewById(R.id.root_top_bar).setBackground(drawable);
        this.mTitleStyle=titleStyle;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_btn_back:
                if(null!=mOnTitleClickListener) mOnTitleClickListener.onBack(v);
                break;
            case R.id.view_more_title:
                if(null!=mOnTitleClickListener) mOnTitleClickListener.onMoreTitleClick(v);
                break;
            case R.id.view_title:
                if(null!=clickCount&&null!=mOnTitleClickListener){
                    System.arraycopy(clickCount,1,clickCount,0,clickCount.length - 1);
                    clickCount[clickCount.length - 1] = SystemClock.uptimeMillis();
                    if (clickCount[0] >= (clickCount[clickCount.length - 1] - 1000)) {
                        if(null!=mOnTitleClickListener) mOnTitleClickListener.onTitleClick(v,false);
                        return;
                    }
                    if(null!=mOnTitleClickListener) mOnTitleClickListener.onTitleClick(v,true);
                }
                break;
            //右侧菜单2
            case R.id.btn_menu2:
                if(null!=mOnTitleClickListener) mOnTitleClickListener.onMenuClick2(v);
                break;
            //右侧菜单1
            case R.id.btn_menu1:
                if(null!=mOnTitleClickListener) mOnTitleClickListener.onMenuClick1(v);
                break;
        }
    }

    /**
     * 显示更多标题
     * @param showMoreTitle
     */
    public void setShowMoreTitle(boolean showMoreTitle) {
        isShowMoreTitle = showMoreTitle;
        moreTitle.setVisibility(isShowMoreTitle ?VISIBLE:GONE);
    }

    /**
     * 标题设置
     * @param title
     */
    public void setTitle(String title){
        MarqueeTextView titleView = (MarqueeTextView) findViewById(R.id.view_title);
        if(null!=titleView) titleView.setText(title);
    }

    public void setTitleColor(int color){
        MarqueeTextView titleView = (MarqueeTextView) findViewById(R.id.view_title);
        if(null!=titleView) titleView.setTextColor(color);
    }

    public void setTitleSize(float textSize){
        MarqueeTextView titleView = (MarqueeTextView) findViewById(R.id.view_title);
        if(null!=titleView) titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
    }

    /**
     * 副标题设置
     * @param title
     */
    public void setSubTitle(String title){
        TextView titleView = (TextView) findViewById(R.id.view_back_title);
        if(null!=titleView) titleView.setText(title);
    }

    public void setSubTitleColor(int color){
        TextView titleView = (TextView) findViewById(R.id.view_back_title);
        if(null!=titleView) titleView.setTextColor(color);
    }

    public void setSubTitleSize(float textSize){
        TextView titleView = (TextView) findViewById(R.id.view_back_title);
        if(null!=titleView) titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
    }

    /**
     * 更多标题设置
     * @param title
     */
    public void setMoreTitle(String title){
        TextView titleView = (TextView) findViewById(R.id.view_more_title);
        if(null!=titleView) titleView.setText(title);
    }

    public void setMoreTitleColor(int color){
        TextView titleView = (TextView) findViewById(R.id.view_more_title);
        if(null!=titleView) titleView.setTextColor(color);
    }

    public void setMoreTitleSize(float textSize){
        TextView titleView = (TextView) findViewById(R.id.view_more_title);
        if(null!=titleView) titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
    }

    /**
     * 右侧菜单1设置
     * @param resID
     */
    public void setMenu1Res(int resID){
        ImageView btnMenu1 = (ImageView) findViewById(R.id.btn_menu1);
        if(null!=btnMenu1)btnMenu1.setImageResource(resID);
    }

    public void setMenu1Drawable(Drawable drawable){
        if(null!=drawable) {
            ImageView btnMenu1 = (ImageView) findViewById(R.id.btn_menu1);
            if(null!=btnMenu1)btnMenu1.setImageDrawable(drawable);
        }
    }

    /**
     * 右侧菜单设置
     * @param resID
     */
    public void setMenu2Res(int resID){
        ImageView btnMenu2 = (ImageView) findViewById(R.id.btn_menu2);
        if(null!=btnMenu2)btnMenu2.setImageResource(resID);
    }

    public void setMenuDrawable(Drawable drawable){
        if(null!=drawable) {
            ImageView btnMenu2 = (ImageView) findViewById(R.id.btn_menu2);
            if(null!=btnMenu2)btnMenu2.setImageDrawable(drawable);
        }
    }

    /**
     * 设置背景透明度
     * @param alaph
     */
    public void setBackgroundAlaph(int alaph){
        Drawable background = this.getBackground();
        if(null!=background) background.mutate().setAlpha(alaph);
    }

    /**
     * 设置背景
     * @param drawable
     */
    public void setTitleBackground(Drawable drawable){
        setTitleBackground(drawable,false);
    }

    public void setTitleBackground(@DrawableRes int resid){
        setTitleBackground(resid,false);
    }

    /**
     * 显示右边更多标题
     * @param flag
     */
    public void showMoreTitle(boolean flag) {
        findViewById(R.id.view_more_title).setVisibility(flag?VISIBLE:GONE);
    }

    /**
     * 设置背景
     * @param drawable
     * @param changSkin 是否根据背景颜色智能换肤
     */
    public void setTitleBackground(Drawable drawable,boolean changSkin){
        if(null==drawable)return;
        this.setBackground(drawable);
        if(changSkin){
            BitmapDrawable bd = (BitmapDrawable) drawable;
            if(null!=bd){
                Bitmap bitmap = bd.getBitmap();
                setIntelligentSkin(bitmap,(ImageView) findViewById(R.id.btn_menu1));
                setIntelligentSkin(bitmap,(ImageView) findViewById(R.id.btn_menu2));
                setIntelligentSkin(bitmap,(TextView) findViewById(R.id.view_more_title));
                setIntelligentSkin(bitmap,(TextView) findViewById(R.id.view_back_title));
            }
        }
    }

    /**
     * 设置背景
     * @param resid
     * @param changSkin 是否根据背景颜色智能换肤
     */
    public void setTitleBackground(@DrawableRes int resid,boolean changSkin){
        if(0==resid)return;
        this.setBackgroundResource(resid);
        if(changSkin){
            Drawable drawable = getResources().getDrawable(resid);
            if(null!=drawable){
                BitmapDrawable bd = (BitmapDrawable) drawable;
                if(null!=bd){
                    Bitmap bitmap = bd.getBitmap();
                    setIntelligentSkin(bitmap,(ImageView) findViewById(R.id.btn_menu1));
                    setIntelligentSkin(bitmap,(ImageView) findViewById(R.id.btn_menu2));
                    setIntelligentSkin(bitmap,(TextView) findViewById(R.id.view_more_title));
                    setIntelligentSkin(bitmap,(TextView) findViewById(R.id.view_back_title));
                }
            }
        }
    }

    /**
     * 设置标题栏上层View颜色取反
     * @param bitmap
     * @param view
     */
    private void setIntelligentSkin(Bitmap bitmap, View view) {
        if(null==bitmap) return;
        if(null==view) return;
        float x = view.getX();
        float y = view.getY();
        int pixel = bitmap.getPixel((int)x,(int)y);
        int redValue = Color.red(pixel);
        int blueValue = Color.blue(pixel);
        int greenValue = Color.green(pixel);
        //将上述获取的颜色取反，获得新颜色
        int color = Color.rgb(Math.abs(redValue - 255),Math.abs(greenValue - 255),Math.abs(blueValue-255));
        //设置新颜色
        if(view instanceof ImageView){
            ((ImageView) view).setColorFilter(color);
        }else if(view instanceof TextView){
            ((TextView) view).setTextColor(color);
        }
    }

    /**
     * 设置占位图层高度
     * @param heightDIP
     */
    @SuppressLint("WrongViewCast")
    public void setStatusBarHeight(float heightDIP){
        findViewById(R.id.status_bar).getLayoutParams().height= ScreenUtils.dpToPxInt(heightDIP);
        findViewById(R.id.status_bar_19).getLayoutParams().height= ScreenUtils.dpToPxInt(heightDIP);
    }

    /**
     * 是否显示占位的状态栏背景色条
     * @param showStatusBar
     */
    public void showStatusBar(boolean showStatusBar){
        View statusBar = findViewById(R.id.status_bar_19);
        if(null==statusBar) return;
        if(showStatusBar){
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) statusBar.setVisibility(VISIBLE);
        }else{
            statusBar.setVisibility(GONE);
        }
    }

    /**
     * 开始加载中
     */
    public void startLoadingView() {
        if(null==findViewById(R.id.view_loading_view)) return;
        ImageView loadingView = (ImageView) findViewById(R.id.view_loading_view);
        loadingView.setVisibility(VISIBLE);
        loadingView.setImageResource(R.drawable.loading_anim);
        if(null==mLoadAnimationDrawable){
            mLoadAnimationDrawable = (AnimationDrawable) loadingView.getDrawable();
            mLoadAnimationDrawable.start();
        }
    }

    /**
     * 结束加载中
     */
    public void stopLoadingView() {
        if(null!=mLoadAnimationDrawable&&mLoadAnimationDrawable.isRunning()){
            mLoadAnimationDrawable.stop();
        }
        mLoadAnimationDrawable=null;
        if(null==findViewById(R.id.view_loading_view)) return;
        ImageView loadingView = (ImageView) findViewById(R.id.view_loading_view);
        loadingView.setImageResource(0);
        loadingView.setVisibility(GONE);
    }

    public void onDestroy(){
        mOnTitleClickListener=null;
        stopLoadingView();
    }

    public void setTitleAlpha(float alpha) {
        if(null!=mTitleView) mTitleView.setAlpha(alpha/255);
    }

    public void showTitle(int flag) {
        findViewById(R.id.view_bar_parent).setVisibility(flag);
    }

    /**
     * 接口改成抽象类，实现时候可以自定义实现关心的回调函数
     */
    public abstract static class OnTitleClickListener{
        public void onBack(View v){}
        public void onTitleClick(View v,boolean doubleClick){}
        public void onMoreTitleClick(View v){}
        public void onMenuClick1(View v){}
        public void onMenuClick2(View v){}
    }

    private OnTitleClickListener mOnTitleClickListener;

    public OnTitleClickListener getOnTitleClickListener() {
        return mOnTitleClickListener;
    }

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener) {
        mOnTitleClickListener = onTitleClickListener;
    }
}