package com.yc.liaolive.view.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.PersonCenterInfo;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.ModifyUserInfoActivity;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.Utils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * TinyHung@Outlook.com
 * 2018/9/20
 * 个人中心用户头部
 */

public class IndexMineHeaderLayout extends FrameLayout {

    private static final String TAG = "IndexMineHeaderLayout";
    private MineHeadAssetsLayout mAssetsVip;
    private MineHeadAssetsLayout mAssetsDiamond;

    public IndexMineHeaderLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public IndexMineHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context,R.layout.view_index_mine_header_layout,this);
        findViewById(R.id.status_bar_19).setVisibility(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP ? VISIBLE : GONE);
        mAssetsVip = (MineHeadAssetsLayout) findViewById(R.id.view_vip);
        mAssetsDiamond = (MineHeadAssetsLayout) findViewById(R.id.view_diamond);
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //头部
                    case R.id.view_head_layout:
                        ModifyUserInfoActivity.start(getContext());
                        break;
                    //头像
                    case R.id.view_user_icon:
                        PersonCenterActivity.start(getContext(), UserManager.getInstance().getUserId());
                        break;
                    //钻石
                    case R.id.view_diamond:
                        if(null!=getContext()){
                            VipActivity.start((Activity) getContext(),0);
                        }
                        break;
                }
            }
        };
        findViewById(R.id.view_head_layout).setOnClickListener(onClickListener);
        findViewById(R.id.view_user_icon).setOnClickListener(onClickListener);
        mAssetsDiamond.setOnClickListener(onClickListener);
    }

    /**
     * 更新用户信息
     * @param data
     */
    public void setUserData(final PersonCenterInfo data){
        if(null==data) return;
        String nickName=data.getNickname();
        try {
            nickName= URLDecoder.decode(null==data.getNickname()?data.getUserid():data.getNickname().replaceAll("%", "%25"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (RuntimeException e){
            e.printStackTrace();
        }finally {
            TextView nameTV = findViewById(R.id.view_tv_nickname);
            nameTV.setText(nickName);
            if (UserManager.getInstance().isVip()) {
                nameTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_nearby_vip, 0);
            } else {
                nameTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            ((TextView) findViewById(R.id.view_tv_id)).setText("ID:" + data.getUserid());
            ((TextView) findViewById(R.id.view_tv_signature)).setText(TextUtils.isEmpty(data.getSignature()) ? "这家伙很懒，什么也没留下~" : data.getSignature());
            //资产情况
            if(null!=mAssetsDiamond) mAssetsDiamond.setItemSubTitle(Utils.formatWan(UserManager.getInstance().getDiamonds(),true));
            if(null!=mAssetsVip){
                mAssetsVip.setItemTitle(UserManager.getInstance().isVip()?"我的VIP":"开通VIP");
                mAssetsVip.setItemSubTitleColor(UserManager.getInstance().isVip()?getContext().getResources().getColor(R.color.colorContent):getContext().getResources().getColor(R.color.coment_color_66));
                mAssetsVip.setItemSubTitle(UserManager.getInstance().isVip()? "立即查看":"立即开通");
                mAssetsVip.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(data.getTelephone_rate_url())) {
                            VipActivity.start(((Activity) getContext()),1);
                        } else {
                            CaoliaoController.start(data.getTelephone_rate_url());
                        }
                    }
                });
            }
            RoundImageView userIcon = (RoundImageView) findViewById(R.id.view_user_icon);
            Glide.with(getContext())
                    .load(data.getAvatar())
                    .asBitmap()
                    .error(R.drawable.ic_default_user_head)
                    .placeholder(R.drawable.ic_default_user_head)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存源资源和转换后的资源
                    .into(new BitmapImageViewTarget(userIcon) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                        }
                    });
        }
    }

    /**
     * 更新用户信息
     * @param userData
     */
    public void setUserData(FansInfo userData){
        if(null==userData) return;
        PersonCenterInfo personCenterInfo=new PersonCenterInfo();
        personCenterInfo.setNickname(userData.getNickname());
        personCenterInfo.setAvatar(userData.getAvatar());
        personCenterInfo.setSignature(userData.getSignature());
        personCenterInfo.setUserid(userData.getUserid());
        personCenterInfo.setSex(userData.getSex());
        personCenterInfo.setVip(userData.getVip());
        setUserData(personCenterInfo);
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
}