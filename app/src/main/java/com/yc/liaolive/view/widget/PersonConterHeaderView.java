package com.yc.liaolive.view.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yc.liaolive.R;
import com.yc.liaolive.bean.PersonCenterInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.index.view.AnchorStatusView;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.model.BannerImageLoader;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/22
 * 用户中心用户头部
 */

public class PersonConterHeaderView extends FrameLayout {

    private UserCenterImageBannerLayout mImageBannerLayout;
    private AutoBannerLayout mBannerLayout;

    public PersonConterHeaderView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PersonConterHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_person_conter_header_layout,this);
        //初始化一个适用于用户中心的封装Banner组件
        mImageBannerLayout = new UserCenterImageBannerLayout(context);
        mImageBannerLayout.setOnBannerClickListenr(new UserCenterImageBannerLayout.OnBannerClickListenr() {
            @Override
            public void onItemClick(PrivateMedia privateMedia,View view,int count) {
                if(null!=mOnFunctionListener) mOnFunctionListener.onItemClick(privateMedia,view,count);
            }
        });
        RelativeLayout imageLayout = (RelativeLayout) findViewById(R.id.user_image_banner);
        imageLayout.addView(mImageBannerLayout);
        //广告
        mBannerLayout = (AutoBannerLayout) findViewById(R.id.view_active_banner);
        mBannerLayout.setImageLoader(new BannerImageLoader()).setOnItemClickListener(new AutoBannerLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                VipActivity.startForResult(((Activity) getContext()),1);
            }
        });
    }

    /**
     * 初始化广告位
     */
    public void initBanner(){
        if(null==mBannerLayout) return;
        final List<TaskInfo> newTasks=new ArrayList<>();
        //会员充值任务
        if(!UserManager.getInstance().isVip()){
            TaskInfo info1=new TaskInfo();
            info1.setItemID(Constant.TASK_ITEM_ID_VIP);
            info1.setId(Constant.ROOM_VIP_CHARGE);
            info1.setApp_id(Constant.ROOM_VIP_CHARGE);
            info1.setIcon(R.drawable.ic_vip_charge);
            newTasks.add(info1);
        }
        if(null!=newTasks&&newTasks.size()>0){
            List<Integer> list=new ArrayList<>();
            //图片的宽度根据 充满组件的宽度 计算
            TaskInfo taskInfo = newTasks.get(0);
            if(taskInfo.getWidth()==0){
                taskInfo.setWidth(283);
                taskInfo.setHeight(209);
            }
            mBannerLayout.setLayoutParams(ScreenUtils.dpToPxInt(95f),taskInfo.getWidth(),taskInfo.getHeight());
            for (int i = 0; i < newTasks.size(); i++) {
                list.add(newTasks.get(i).getIcon());
            }
            mBannerLayout.setData(list);
        }else{
            mBannerLayout.setData(null);
            mBannerLayout.setLayoutParams(0,0);
        }
    }

    /**
     * 更新用户信息
     * @param data
     */
    public void setUserData(PersonCenterInfo data){
        if(null==data) return;
        ImageView userSex = (ImageView) findViewById(R.id.view_iv_sex);
        ImageView userVip = (ImageView) findViewById(R.id.view_iv_vip);
        ImageView userLevel = (ImageView) findViewById(R.id.view_iv_level);
        TextView tvNickname = (TextView) findViewById(R.id.view_tv_nickname);
        TextView userSignature = (TextView) findViewById(R.id.view_tv_signature);
        userSex.setImageResource(data.getSex() == 0 ? R.drawable.ic_user_sex_man : R.drawable.ic_user_sex_wumen);
        LiveUtils.setUserBlockVipGradle(userVip,data.getVip());//设置用户vip等级
        LiveUtils.setUserGradle(userLevel, data.getLevel_integral());
        tvNickname.setText(data.getNickname());
        AnchorStatusView offlineState = findViewById(R.id.user_offline_state);
        offlineState.setVisibility(2==data.getIdentity_audit()?VISIBLE:GONE);
//        LiveUtils.setUserOffline(offlineState,data.getUser_state());//设置用户在线状态
        if (2 == data.getIdentity_audit()) {
            offlineState.setVisibility(VISIBLE);
            //主播、用户的在线状态
            offlineState.setData(data.getUser_state(), 0);
        } else {
            offlineState.setVisibility(GONE);
        }
        userSignature.setText(TextUtils.isEmpty(data.getSignature()) ? "这家伙很懒，什么也没留下" : data.getSignature());
    }

    /**
     * 返回头部View
     * @return
     */
    public View getHeadView() {
        return this;
    }

    /**
     * 更新用户头像
     * @param privateMedias
     */
    public void setUserHeads(List<PrivateMedia> privateMedias) {
        if(null!=mImageBannerLayout) mImageBannerLayout.setBanners(privateMedias);
    }

    public void onResume(){
        if(null!=mImageBannerLayout) mImageBannerLayout.onResume();
        if(null!=mBannerLayout) mBannerLayout.onResume();
    }

    public void onPause(){
        if(null!=mImageBannerLayout) mImageBannerLayout.onPause();
        if(null!=mBannerLayout) mBannerLayout.onPause();
    }

    public void onDestroy(){
        if(null!=mImageBannerLayout) mImageBannerLayout.onDestroy();
    }

    public interface onFunctionListener{
        void onItemClick(PrivateMedia privateMedia, View view,int count);
    }
    private onFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(onFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }
}
