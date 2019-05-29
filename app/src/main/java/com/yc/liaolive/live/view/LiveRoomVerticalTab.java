package com.yc.liaolive.live.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/6/11
 * 直播间的垂直列表
 */

public class LiveRoomVerticalTab extends LinearLayout implements View.OnClickListener {

    private RelativeLayout mTab1;
    private RelativeLayout mTab2;
    private RelativeLayout mTab3;

    public LiveRoomVerticalTab(Context context) {
        super(context);
    }

    public LiveRoomVerticalTab(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_live_vertical_tab_layouty,this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //所有消息
            case R.id.view_tab_1:
                mTab1.setSelected(true);
                mTab2.setSelected(false);
                mTab3.setSelected(false);
                if(null!=mOnTabClickListener) mOnTabClickListener.onTabClick(0);
                break;
            //系统消息
            case R.id.view_tab_2:
                mTab1.setSelected(false);
                mTab2.setSelected(true);
                mTab3.setSelected(false);
                if(null!=mOnTabClickListener) mOnTabClickListener.onTabClick(1);
                break;
            //聊天消息
            case R.id.view_tab_3:
                mTab1.setSelected(false);
                mTab2.setSelected(false);
                mTab3.setSelected(true);
                if(null!=mOnTabClickListener) mOnTabClickListener.onTabClick(2);
                break;
        }
    }

    /**
     * 设置选项卡数据
     * @param tabs
     */
    public void setTabs(String[] tabs){
        if(null==tabs||tabs.length<3) return;
        mTab1 = (RelativeLayout) findViewById(R.id.view_tab_1);
        mTab2 = (RelativeLayout) findViewById(R.id.view_tab_2);
        mTab3 = (RelativeLayout) findViewById(R.id.view_tab_3);
        ((TextView) findViewById(R.id.view_btn_tab_1)).setText(tabs[0]);
        ((TextView) findViewById(R.id.view_btn_tab_2)).setText(tabs[1]);
        ((TextView) findViewById(R.id.view_btn_tab_3)).setText(tabs[2]);
        mTab1.setOnClickListener(this);
        mTab2.setOnClickListener(this);
        mTab3.setOnClickListener(this);
    }

    /**
     * 设置默认选中项
     * @param index
     * @param flag
     */
    public void setSelected(int index,boolean flag){
        switch (index) {
            case 0:
                if(null!=mTab1) mTab1.setSelected(flag);
                break;
            case 1:
                if(null!=mTab2) mTab2.setSelected(flag);
                break;
            case 2:
                if(null!=mTab3) mTab3.setSelected(flag);
                break;

        }
    }

    public interface OnTabClickListener{
        void onTabClick(int index);
    }
    private OnTabClickListener mOnTabClickListener;

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        mOnTabClickListener = onTabClickListener;
    }

    public void onDestroy(){
        mOnTabClickListener=null;mTab1=null;mTab2=null;mTab3=null;
    }
}
