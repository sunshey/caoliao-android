package com.yc.liaolive.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.model.BannerViewInterface;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/11/13
 * 轮播组件
 */

public class AutoBannerLayout extends FrameLayout {

    private static final String TAG = "AutoBannerLayout";
    private ViewPager mViewPager;
    private LinearLayout mLinearDotLayout;//指示器
    private List<?> mList;
    private Drawable mSelectedDrawable;//选中的角标
    private Drawable mUnSelectedDrawable;//未选中的角标
    private int mIndicatorHeight;//角标高
    private int mIndicatorWidth;//角标宽
    private int mIndicatorMargin;//角标边距
    private int mScaleType=1;//Image缩放类型
    private long AUTO_ROLL_DURTION = 5000;//间隔自动轮播时间
    private BannerPagerAdapter mAdapter;
    private boolean isRuning=false;
    private int mLastPosition;//上一个
    private BannerViewInterface mLoaderInterface;
    private boolean mAutoIsRoll;//是否自动滚动
    private Handler mHandler;

    public AutoBannerLayout(Context context) {
        this(context, null,0);
    }

    public AutoBannerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoBannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_auto_banner_layout, this);
        mViewPager = (ViewPager) findViewById(R.id.view_banner_pager);
        mLinearDotLayout = (LinearLayout) findViewById(R.id.view_dot_view);
        mViewPager.setOffscreenPageLimit(1);
        mAdapter = new BannerPagerAdapter();
        mViewPager.addOnPageChangeListener(mPageListener);
        mViewPager.setAdapter(mAdapter);
        if(null!=attrs){
            TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.AutoBannerLayout);
            mSelectedDrawable = typedArray.getDrawable(R.styleable.AutoBannerLayout_bannerIndicatorSelected);
            mUnSelectedDrawable = typedArray.getDrawable(R.styleable.AutoBannerLayout_bannerIndicatorUnselected);
            mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.AutoBannerLayout_bannerIndicatorHeight, ScreenUtils.dpToPxInt(6f));
            mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.AutoBannerLayout_bannerIndicatorWidth, ScreenUtils.dpToPxInt(6f));
            mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.AutoBannerLayout_bannerIndicatorMargin, ScreenUtils.dpToPxInt(5f));
            mScaleType = typedArray.getInt(R.styleable.AutoBannerLayout_bannerScaleType, 1);
            AUTO_ROLL_DURTION=typedArray.getInteger(R.styleable.AutoBannerLayout_bannerRollDurtion,5000);
            boolean aBoolean = typedArray.getBoolean(R.styleable.AutoBannerLayout_bannerShowIndicator, false);
            mAutoIsRoll = typedArray.getBoolean(R.styleable.AutoBannerLayout_bannerAutoRoll, false);
            showIndicator(aBoolean);
            typedArray.recycle();
        }
        mHandler = new Handler();
    }


    /**
     * 设定组件宽高
     * @param width
     * @param height
     */
    @SuppressLint("WrongViewCast")
    public AutoBannerLayout setLayoutParams(int width, int height){
        View rootView = findViewById(R.id.view_banner_root);
        ViewGroup.LayoutParams params = rootView.getLayoutParams();
        params.width=width;
        params.height=height;
        rootView.setLayoutParams(params);
        return this;
    }

    /**
     * 设定Banner组件宽高属性
     * @param viewWidth View宽度
     * @param childViewWidth childView宽
     * @param childViewHeight childView高
     * @return
     */
    public AutoBannerLayout setLayoutParams(int viewWidth,int childViewWidth,int childViewHeight){
        View rootView = findViewById(R.id.view_banner_root);
        int height = (int) Math.ceil((float) viewWidth * (float) childViewHeight / (float)  childViewWidth);
        ViewGroup.LayoutParams params = rootView.getLayoutParams();
        params.width=viewWidth;
        params.height=height;
        rootView.setLayoutParams(params);
        return this;
    }

    /**
     * 返回运行状态
     * @return
     */
    public boolean isRuning() {
        return isRuning;
    }

    /**
     * 设置数据
     * @param items
     */
    public AutoBannerLayout setData(List<?> items){
        if(null==mLoaderInterface) throw new NullPointerException("Please Set BannerViewInterface");
        if(null!=mAdapter){
            if(null!=items&&items.size()>0){
                mList = items;
                mAdapter.notifyDataSetChanged();
                addDots();
                if(mAutoIsRoll) start();
            }else{
                stopAuto();
                mList=null;
                if(null!=mLinearDotLayout) mLinearDotLayout.removeAllViews();
                mAdapter.notifyDataSetChanged();
            }
        }
        return this;
    }

    /**
     * 设置渲染加载器
     * @param imageLoader
     * @return
     */
    public AutoBannerLayout setImageLoader(BannerViewInterface imageLoader) {
        this.mLoaderInterface = imageLoader;
        return this;
    }

    /**
     * 开始
     * @return
     */
    public AutoBannerLayout start(){
        startAuto();
        return this;
    }


    /**
     * 对应生命周期调用
     */
    public void onResume(){
        if(mAutoIsRoll) startAuto();
    }

    /**
     * 对应生命周期调用
     */
    public void onPause(){
        stopAuto();
    }

    /**
     * 是否显示指示器
     * @param flag
     * @return
     */
    public AutoBannerLayout showIndicator(boolean flag){
        if(null!=mLinearDotLayout) mLinearDotLayout.setVisibility(flag?VISIBLE:GONE);
        return this;
    }

    /**
     * 滚动间隔时长
     * @param autoDurtion
     */
    public void setAutoDurtion(long autoDurtion) {
        this.AUTO_ROLL_DURTION = autoDurtion;
    }

    /**
     * 是否自动滚动
     * @param autoIsRoll
     */
    public AutoBannerLayout setAutoRoll(boolean autoIsRoll) {
        mAutoIsRoll = autoIsRoll;
        return this;
    }

    /**
     * 重置
     */
    public void onReset() {
        stopAuto();
        if(null!=mLinearDotLayout)mLinearDotLayout.removeAllViews();
    }

    /**
     * 对应生命周期调用
     */
    public void onDestroy(){
        startAuto();
        if(null!=mList) mList.clear();
        if(null!=mAdapter) mAdapter.notifyDataSetChanged();
        if(null!=mLinearDotLayout)mLinearDotLayout.removeAllViews();
        if(null!=mViewPager) mViewPager.removeAllViews();
        mSelectedDrawable=null;mUnSelectedDrawable=null;mOnItemClickListener=null;mAutoIsRoll=false;
        mList=null;mAdapter=null;mViewPager=null;mLastPosition=0;mLoaderInterface=null;
    }

    /**
     * 结束自动滚动
     */
    private void stopAuto() {
        if(null!=mHandler) {
            mHandler.removeCallbacks(rollRunnable);
            mHandler.removeMessages(0);
        }
        isRuning=false;
    }

    /**
     * 开始自动滚动
     */

    private void startAuto() {
        if(isRuning) return;
        if(null!=mList&&mList.size()>1){
            isRuning=true;
            if(null!=mHandler){
                mHandler.removeCallbacks(rollRunnable);
                mHandler.postDelayed(rollRunnable,AUTO_ROLL_DURTION);
            }
        }
    }

    private Runnable rollRunnable=new Runnable() {
        @Override
        public void run() {
            nextPage();
            if(null!=mHandler) mHandler.postDelayed(rollRunnable,AUTO_ROLL_DURTION);
        }
    };

    /**
     * 滚动至下一个
     */
    private void nextPage() {
        if(null==mAdapter||null==mViewPager) return;
        if (mAdapter.getCount() <= 1) {
            return;
        }
        int count = mAdapter.getCount();
        int index = mViewPager.getCurrentItem();
        index = (index + 1) % count;
        mViewPager.setCurrentItem(index, true);
    }

    /**
     * 绘制指示器角标
     */
    private void addDots() {
        if (null==mList) {
            return;
        }
        if(null!=mLinearDotLayout) mLinearDotLayout.removeAllViews();
        int num=mList.size();
        if(num>1){
            for (int i=0;i<num;i++) {
                View dot = new View(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mIndicatorWidth, mIndicatorHeight);
                layoutParams.setMargins(0, 0, mIndicatorMargin, 0);
                dot.setLayoutParams(layoutParams);
                dot.setBackground(null==mUnSelectedDrawable?getResources().getDrawable(R.drawable.arice_gray_dot):mUnSelectedDrawable);
                dot.setTag(i);
                dot.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=mPageListener) mPageListener.onPageSelected((Integer) v.getTag());
                    }
                });
                mLinearDotLayout.addView(dot);
            }
            if(null!=mPageListener) mPageListener.onPageSelected(0);
            mLastPosition=0;
        }
    }


    /**
     * 片段适配器
     */
    private class BannerPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return  null==mList ? 0 : mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return -2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            if(null!=mLoaderInterface){
                View imageView = mLoaderInterface.createImageView(getContext());
                setScaleType(imageView);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=mOnItemClickListener) mOnItemClickListener.onItemClick(v,position);
                    }
                });
                imageView.setId(position);
                container.addView(imageView);
                mLoaderInterface.displayView(getContext(),mList.get(position),imageView);
                return imageView;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(container.findViewById(position));
        }
    }

    /**
     * 监听器
     */
    private ViewPager.OnPageChangeListener mPageListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            if(null==mLinearDotLayout||mLinearDotLayout.getChildCount()==0){
                return;
            }
            if(mLinearDotLayout.getChildCount()>mLastPosition) mLinearDotLayout.getChildAt(mLastPosition).setBackground(null==mUnSelectedDrawable?getResources().getDrawable(R.drawable.arice_gray_dot):mUnSelectedDrawable);
            if(mLinearDotLayout.getChildCount()>position) mLinearDotLayout.getChildAt(position).setBackground(null==mSelectedDrawable?getResources().getDrawable(R.drawable.arice_app_style_dot):mSelectedDrawable);
            mLastPosition=position;
        }
    };

    /**
     * 设置图片缩放类型
     * @param imageView
     */
    private void setScaleType(View imageView) {
        if (imageView instanceof ImageView) {
            ImageView view = ((ImageView) imageView);
            switch (mScaleType) {
                case 0:
                    view.setScaleType(ImageView.ScaleType.CENTER);
                    break;
                case 1:
                    view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case 2:
                    view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    break;
                case 3:
                    view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;
                case 4:
                    view.setScaleType(ImageView.ScaleType.FIT_END);
                    break;
                case 5:
                    view.setScaleType(ImageView.ScaleType.FIT_START);
                    break;
                case 6:
                    view.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 7:
                    view.setScaleType(ImageView.ScaleType.MATRIX);
                    break;
                    default:
                        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }

    /**
     * 触摸停止计时器，抬起启动计时器
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mAutoIsRoll) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
                startAuto();
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopAuto();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public AutoBannerLayout setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }
}
