package com.yc.liaolive.media.ui.pager;

import android.app.Activity;
import android.text.TextUtils;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BasePager;
import com.yc.liaolive.bean.MediaFileInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.PagerHorizontaImagePreviewBinding;
import com.yc.liaolive.interfaces.ImagePreviewHelp;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.media.view.PrivateTouchImageView;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.ui.dialog.QuireVideoDialog;

/**
 * TinyHung@Outlook.com
 * 2018/9/27
 * 水平图片购买、预览界面
 */

public class HorizontaImagePreviewPager extends BasePager<PagerHorizontaImagePreviewBinding>{

    private final int mGroupPosition;
    private final PrivateMedia mVideoInfo;
    private final ImagePreviewHelp mPreviewHelp;

    public HorizontaImagePreviewPager(Activity context, VerticalImagePlayerPager previewHelp, PrivateMedia indexVideoInfo, int groupPosition, int currentPosition) {
        super(context);
        this.mVideoInfo=indexVideoInfo;
        this.mGroupPosition=groupPosition;
        this.mPreviewHelp = previewHelp;
        setContentView(R.layout.pager_horizonta_image_preview);
    }

    /**
     * UI组件初始化
     */
    @Override
    public void initViews() {
        bindingView.touchImage.setOnFunctionListener(new PrivateTouchImageView.OnFunctionListener() {
            @Override
            public void onClick() {
                if(null!=mOnFunctionListener) mOnFunctionListener.onClick();
            }

            @Override
            public void onDoubleClick() {
                if(null!=mPreviewHelp) mPreviewHelp.onDoubleClick(mGroupPosition);
            }

            @Override
            public void buyMediaFile() {
                startPlayer();
            }
        });
        if(null!=mVideoInfo) bindingView.touchImage.setImageData(mVideoInfo);
    }

    @Override
    public void initData() {

    }

    /**
     * 控制器的透明度
     * @param alpha
     */
    public void setConntrollerAlpha(float alpha) {
        bindingView.touchImage.setAlpha(alpha);
    }

    /**
     * 触发预览
     * 有关付费验证在这里进行
     */
    public void startPlayer(){
        if(null!=mVideoInfo&&isVisible()){
            //新版本 图片不需要付费，但需要上报预览状态
            if(TextUtils.isEmpty(mVideoInfo.getIsPost())){
                getMediaPath(mVideoInfo);
                return;
            }else{
                mPreviewHelp.newMediaInfo(mVideoInfo,mGroupPosition);
            }
            bindingView.touchImage.initPreview();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startPlayer();
    }

    /**
     * 当前界面不可见了
     */
    @Override
    public void onStop() {
        super.onStop();
        if(null!=bindingView) bindingView.touchImage.reset();
    }

    /**
     * 获取绝对路径再查看
     * @param videoInfo
     */
    private void getMediaPath(final PrivateMedia videoInfo) {
        //付费点播
        UserManager.getInstance().browseMediaFile(videoInfo.getId(), videoInfo.getUserid(), Constant.MEDIA_VIDEO_LIST, 1, new UserServerContract.OnCallBackListener() {
            @Override
            public void onSuccess(int code,Object object,String msg) {
                if(null!=object&& object instanceof MediaFileInfo){
                    MediaFileInfo data= (MediaFileInfo) object;
                    if(null!=videoInfo&&isVisible()){
                        //先更新界面
                        videoInfo.setAttent(data.getAttent());
                        videoInfo.setLove_number(data.getLove_number());
                        videoInfo.setShare_number(data.getShare_number());
                        videoInfo.setBrowse_number(data.getBrowse_number());
                        videoInfo.setSignature(data.getSignature());
                        videoInfo.setIs_love(data.getIs_love());
                        videoInfo.setIs_online(data.getIs_online());
                        videoInfo.setFile_path(data.getFile_path());
                        videoInfo.setIdentity_audit(data.getIdentity_audit());
                        videoInfo.setNickname(data.getNickname());
                        videoInfo.setUser_state(data.getUser_state());
                        videoInfo.setAvatar(data.getAvatar());
                        videoInfo.setIsPost("success");//标识为已上报状态
                        if(null!=data.getRoom_info()) videoInfo.setRoomInfo(data.getRoom_info());//房间信息
                        //绑定当前文件ID到控制器
                        if(null!= mPreviewHelp) mPreviewHelp.newMediaInfo(videoInfo,mGroupPosition);
                        if(!TextUtils.isEmpty(videoInfo.getFile_path())){
                            if(isVisible()) startPlayer();
                            return;
                        }
                    }
                    if(NetContants.API_RESULT_ARREARAGE_CODE==code&&isVisible()){
                        onRechgre(videoInfo);
                        return;
                    }
                    //向用户抛出错误信息
                    if(isVisible()) ToastUtils.showCenterToast(msg);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if(isVisible()){
                    //文件未找到
                    if(NetContants.API_RESULT_CANT_FIND==code&&isVisible()){
                        onCantFind(errorMsg);
                        return;
                    }
                    ToastUtils.showCenterToast(errorMsg);
                }
            }
        });
    }

    /**
     * 未找到文件
     * @param msg
     */
    private void onCantFind(String msg) {
        if(null==getContext()) return;
        QuireVideoDialog.getInstance(getContext())
                .setTipsData(msg,null,"确定")
                .show();
    }

    /**
     * 购买钻石
     * @param videoInfo
     */
    private void onRechgre(PrivateMedia videoInfo) {
        if(null==getContext()) return;
        QuireDialog.getInstance(((Activity) getContext()))
                .setTitleText("钻石不足")
                .setContentText("查看私照需要"+videoInfo.getPrice()+"钻石")
                .setSubContentText("开通会员海量私照<font color='#FF7575'>免费看</font>")
                .setSubmitTitleText("开通会员")
                .setCancelTitleText("充值钻石")
                .setContentTextColor(getContext().getResources().getColor(R.color.app_style_1))
                .setDialogCancelable(true)
                .showCloseBtn(true)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        VipActivity.startForResult(((Activity) getContext()),1);
                    }

                    @Override
                    public void onRefuse() {
                        VipActivity.startForResult(((Activity) getContext()),0);
                    }
                }).show();
    }

    /**
     * 触发了返回事件
     * 还原缩放状态
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 完全销毁阶段
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnFunctionListener{
        void onClick();
    }
    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }
}
