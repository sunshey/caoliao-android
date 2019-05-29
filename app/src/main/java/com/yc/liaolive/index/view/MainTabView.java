package com.yc.liaolive.index.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;

/**
 * TinyHung@Outlook.com
 * 2018/8/18
 * 首页单个Tab
 */

public class MainTabView extends RelativeLayout {

    private static final String TAG = "MainTabView";
    private TextView mTextView;
    private ImageView icon;
    private TextView numPoint;
    private String imgUrl;
    private String imgUrlSelelcted;

    public MainTabView(Context context) {
        super(context);
        init(context,null);
    }

    public MainTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_main_tab_layout,this);
        mTextView = (TextView) findViewById(R.id.view_btn_text);
        icon = findViewById(R.id.view_btn_icon);
        numPoint = findViewById(R.id.view_tab_msg_count);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MainTabView);
            String title = typedArray.getString(R.styleable.MainTabView_mainTabText);
            int textSize = typedArray.getInt(R.styleable.MainTabView_mainTabSize, 10);
            boolean selected = typedArray.getBoolean(R.styleable.MainTabView_mainTabSelected, false);
            mTextView.setText(title);
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
            Drawable drawable = typedArray.getDrawable(R.styleable.MainTabView_mainTabIcon);
            icon.setImageDrawable(drawable);
            setTabSelected(selected);
            typedArray.recycle();
        }
    }

    /**
     * 设置tab数据
     * @param label 文案
     * @param imgUrl icon 图标地址
     * @param imgUrlSelelcted  icon选中地址
     */
    public void setTabContent (String label, String imgUrl, String imgUrlSelelcted) {
        mTextView.setText(label);
        this.imgUrl = imgUrl;
        this.imgUrlSelelcted = imgUrlSelelcted;
        try {
            if(null!=getContext()){
                Glide.with(getContext())
                        .load(imgUrl)
                        .centerCrop()
                        .skipMemoryCache(false)//跳过内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(icon);
            }
        }catch (RuntimeException e){

        }
    }

    /**
     * 设置是否选中
     * @param selected
     */
    public void setTabSelected(boolean selected){
        if(null==getContext()) return;
        if(null!=mTextView) mTextView.setSelected(selected);
        if(null!=icon) {
            if (selected && !TextUtils.isEmpty(imgUrlSelelcted)) {
                Glide.with(getContext())
                        .load(imgUrlSelelcted)
                        .centerCrop()
                        .skipMemoryCache(false)//跳过内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(icon);
            } else if (!TextUtils.isEmpty(imgUrl)){
                Glide.with(getContext())
                        .load(imgUrl)
                        .centerCrop()
                        .skipMemoryCache(false)//跳过内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(icon);
            }
        }
    }

    /**
     * 设置红点数量
     * @param num
     */
    public void setTabRedPoint (int num) {
        int serverCount = SharedPreferencesUtil.getInstance().getInt(Constant.KET_SERVER_MSG_COUNT, 0);
        num+=serverCount;
        Logger.d(TAG,"num:"+num+",serverCount:"+serverCount);
        if (num > 0 ) {
            numPoint.setVisibility(View.VISIBLE);
            if (num > 99) {
                numPoint.setText(getContext().getResources().getString(R.string.time_more));
            } else {
                numPoint.setText(String.valueOf(num));
            }
        } else {
            numPoint.setVisibility(View.INVISIBLE);
        }
    }
}