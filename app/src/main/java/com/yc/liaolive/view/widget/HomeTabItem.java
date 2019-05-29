package com.yc.liaolive.view.widget;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;

/**
 * TinyHung@Outlook.com
 * 2018/3/13.
 * 首页的底部TAB
 * 调用者控制是否启用重复点击刷新功能
 */

public class HomeTabItem extends FrameLayout {

    private static final String TAG = "HomeTabItem";
    private boolean isRefresh;//是否支持重复点击刷新
    private int mCureenViewIndex=0;//当前显示的Index
    private IndexTabView[] mIndexTabViews;
    private TextView mTvMsgCount;

    public HomeTabItem(@NonNull Context context) {
        super(context);
    }

    public HomeTabItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_home_table,this);
        initViews();
    }

    private void initViews() {
        OnClickListener onTabClickListener=new OnClickListener() {

            private Vibrator mVibrator;

            @Override
            public void onClick(View view) {
                int childViewIndex=0;
                switch (view.getId()) {
                    case R.id.view_tab_index:
                        childViewIndex=0;
                        break;
                    case R.id.view_tab_follow:
                        childViewIndex=1;
                        break;
                    case R.id.view_tab_msg:
                        childViewIndex=2;
                        break;
                    case R.id.view_tab_mine:
                        childViewIndex=3;
                        break;
                }
                //将再次点击事件拦截，用于处理刷新
                if(isRefresh&&mCureenViewIndex==childViewIndex&&null!=mOnTabChangeListene){
                    try {
                        //触摸反馈
                        if(null==mVibrator) mVibrator = (Vibrator)getContext().getSystemService(getContext().VIBRATOR_SERVICE);
                        mVibrator.vibrate(Constant.VIBRATOR_MILLIS);
                    }catch (RuntimeException e){

                    }
                    mOnTabChangeListene.onRefresh(childViewIndex);
                    return;
                }
                if(null!= mIndexTabViews){
                    mIndexTabViews[mCureenViewIndex].setTabSelected(false);
                    mIndexTabViews[childViewIndex].setTabSelected(true);
                }
                mCureenViewIndex=childViewIndex;
                if(null!=mOnTabChangeListene){
                    mOnTabChangeListene.onChangeed(childViewIndex);
                }
            }
        };
        IndexTabView tabIndex = (IndexTabView) findViewById(R.id.view_tab_index);
        IndexTabView tabFollow = (IndexTabView) findViewById(R.id.view_tab_follow);
        IndexTabView tabMsg = (IndexTabView) findViewById(R.id.view_tab_msg);
        IndexTabView tabMine = (IndexTabView) findViewById(R.id.view_tab_mine);
        tabIndex.setOnClickListener(onTabClickListener);
        tabFollow.setOnClickListener(onTabClickListener);
        tabMsg.setOnClickListener(onTabClickListener);
        tabMine.setOnClickListener(onTabClickListener);
        mIndexTabViews =new IndexTabView[5];
        mIndexTabViews[0]=tabIndex;
        mIndexTabViews[1]=tabFollow;
        mIndexTabViews[2]=tabMsg;
        mIndexTabViews[3]=tabMine;
        mIndexTabViews[0].setTabSelected(true);
        //消息数量处理
        mTvMsgCount = (TextView) findViewById(R.id.view_tab_msg_count);
        //中间撩撩按钮处理
        View rootView = findViewById(R.id.root_view_layout);
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        rootView.measure(width,width);
        //确定撩撩的宽高
        View btnVideo =findViewById(R.id.btn_video);
        ViewGroup.LayoutParams layoutParams = btnVideo.getLayoutParams();
        layoutParams.width=(rootView.getMeasuredHeight()-10);
        layoutParams.height=(rootView.getMeasuredHeight()-10);
        btnVideo.setLayoutParams(layoutParams);
        btnVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnTabChangeListene) mOnTabChangeListene.onTabVideo();
            }
        });
    }

    /**
     * 设置是否支持重复点击刷新功能
     * @param flag
     */
    public void setDoubleRefresh(boolean flag){
        this.isRefresh=flag;
    }

    /**
     * 设置选中的TAB
     * @param index
     */
    public void setCurrentIndex(int index){
        if(mCureenViewIndex==index) return;
        if(null!= mIndexTabViews && mIndexTabViews.length>0){
            mIndexTabViews[mCureenViewIndex].setTabSelected(false);
            mIndexTabViews[index].setTabSelected(true);
        }
        mCureenViewIndex=index;
        if(null!=mOnTabChangeListene){
            mOnTabChangeListene.onChangeed(index);
        }
    }

    /**
     * 设置消息数量
  * @param count
     */
    public void setMessageContent(long count) {
        if(null==mTvMsgCount) return;
        if(count<=0){
            mTvMsgCount.setText("");
            mTvMsgCount.setBackgroundResource(0);
        }else{
            mTvMsgCount.setBackground(getContext().getResources().getDrawable(R.drawable.arl_round_red_dot));
            mTvMsgCount.setText(String.valueOf(count));
        }
    }

    /**
     * 对应方法中调用
     */
    public void onDestroy() {
        if(null!= mIndexTabViews){
            mIndexTabViews[mCureenViewIndex].setTabSelected(false);
            mCureenViewIndex=0;
            mIndexTabViews[mCureenViewIndex].setTabSelected(true);
        }
    }

    public interface OnTabChangeListene{
        void onChangeed(int index);
        void onRefresh(int index);
        void onTabVideo();
    }

    private OnTabChangeListene mOnTabChangeListene;

    public void setOnTabChangeListene(OnTabChangeListene onTabChangeListene) {
        mOnTabChangeListene = onTabChangeListene;
    }
}
