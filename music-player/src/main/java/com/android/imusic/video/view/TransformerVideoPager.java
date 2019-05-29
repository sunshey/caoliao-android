package com.android.imusic.video.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.imusic.R;
import com.android.imusic.video.bean.OpenEyesIndexItemBean;
import com.music.player.lib.util.MusicUtils;
import com.music.player.lib.view.MusicJukeBoxBackgroundLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/4/8
 */

public class TransformerVideoPager extends RelativeLayout{

    private static final String TAG = "TransformerVideoPager";
    private List<OpenEyesIndexItemBean> mDataBeans;
    private MusicJukeBoxBackgroundLayout mBackgroundLayout;
    private ViewPager mViewPager;
    private TransformerViewpager mAdapter;
    private TextView mVideoIndexNum;


    public TransformerVideoPager(Context context) {
        this(context,null);
    }

    public TransformerVideoPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.video_pager_transforme,this);
        mViewPager = (ViewPager) findViewById(R.id.view_item_pager);
        //ViewPager的父容器高度确定
        RelativeLayout pagerLayout = (RelativeLayout) findViewById(R.id.re_item_pager_view);
        int screenWidth = MusicUtils.getInstance().getScreenWidth(context);
        int width = screenWidth * 8 / 10;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) pagerLayout.getLayoutParams();
        layoutParams.width=LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height=width*9/16;
        pagerLayout.setLayoutParams(layoutParams);
        //ViewPager宽度为父容器8/10，高度与父容器一致
        LayoutParams params=new LayoutParams(width, width*9/16);
        mViewPager.setLayoutParams(params);

        mAdapter = new TransformerViewpager();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                setPagerData(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        //设置ViewPager切换效果
        mViewPager.setPageTransformer(true, new TransformerPageAnimation());
        mViewPager.setOffscreenPageLimit(2);
        mBackgroundLayout = (MusicJukeBoxBackgroundLayout) findViewById(R.id.background_view);
        mVideoIndexNum = (TextView) findViewById(R.id.view_index_num);
    }

    /**
     * 更新页签数据
     * @param position
     */
    private void setPagerData(int position) {
        if(null!=mDataBeans&&mDataBeans.size()>position) {
            //取出Card元素
            OpenEyesIndexItemBean indexItemBean = mDataBeans.get(position).getData().getContent().getData();
            mVideoIndexNum.setText((position + 1) + "/" + mDataBeans.size());
            if(null!=mBackgroundLayout){
                mBackgroundLayout.setBackgroundCover(indexItemBean.getCover().getBlurred(),100,false);
            }
        }
    }

    public void setVideos(List<OpenEyesIndexItemBean> data) {
        if(null!= mAdapter &&null!= mAdapter){
            if(null!=mDataBeans){
                mDataBeans.clear();
            }
            if(null==mDataBeans) mDataBeans=new ArrayList<>();
            mDataBeans.addAll(data);
            mAdapter.notifyDataSetChanged();
            if(null!=mViewPager) mViewPager.setCurrentItem(0);
        }
        setPagerData(0);
    }

    private class TransformerViewpager extends PagerAdapter {
        @Override
        public int getCount() {
            return mDataBeans ==null?0:mDataBeans.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            OpenEyesIndexItemBean movieItem = mDataBeans.get(position);
            TransformerMoiveItem moiveItem=new TransformerMoiveItem(getContext());
            moiveItem.setData(movieItem);
            container.addView(moiveItem);
            return moiveItem;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(object instanceof TransformerMoiveItem){
                TransformerMoiveItem moiveItem= (TransformerMoiveItem) object;
                moiveItem.onDestroy();
            }
            container.removeView((View)object);
        }
    }

    public void onDestroy(){
        if(null!=mDataBeans) mDataBeans.clear();
        if(null!=mAdapter){
            mAdapter.notifyDataSetChanged();
        }
        if(null!=mBackgroundLayout){
            mBackgroundLayout.onDestroy();
            mBackgroundLayout=null;
        }
        if(null!=mViewPager){
            mViewPager.removeAllViews();
            mViewPager=null;
        }
    }
}