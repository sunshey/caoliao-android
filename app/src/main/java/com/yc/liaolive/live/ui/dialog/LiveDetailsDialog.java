package com.yc.liaolive.live.ui.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.databinding.DialogLiveDetailsBinding;
import com.yc.liaolive.live.bean.RoomOutBean;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.live.ui.contract.LiveUserContract;
import com.yc.liaolive.live.ui.presenter.LiveUserPresenter;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.Utils;
import java.util.Locale;

/**
 *  TinyHung@outlook.com
 *  2016/5/16
 *  主播端、观众端 结束后的详情
 */
public class LiveDetailsDialog extends BaseDialog<DialogLiveDetailsBinding> implements BaseContract.BaseView, LiveUserContract.View {

    //场景模式
    public static final int SCENE_MODE_PLAY=0;
    public static final int SCENE_MODE_PLAYER=1;
    private int mSceneMode;
    private RoomOutBean bean;
    private String mHomeUserID;
    private int mIsFollow;//默认是未关注此用户的
    private String mMsgTitle;
    private final LiveUserPresenter mPresenter;

    /**
     * 入口
     * @param homeUserID 宿主用户ID
     * @param sceneMode 场景模式 0：观看端 1：主播端
     * @param bean 直播数据
     * @return
     */
    public static LiveDetailsDialog newInstance(Activity activity, String homeUserID, int sceneMode, RoomOutBean bean){
        return new LiveDetailsDialog(activity, homeUserID ,sceneMode, bean,null);
    }


    /**
     * 入口
     * @param homeUserID 宿主用户ID
     * @param sceneMode 场景模式 0：观看端 1：主播端
     * @param bean 直播数据
     * @param msgTitle 错误消息
     * @return
     */
    public static LiveDetailsDialog newInstance(Activity activity, String homeUserID, int sceneMode, RoomOutBean bean, String msgTitle){
        return new LiveDetailsDialog(activity, homeUserID, sceneMode, bean, msgTitle);
    }


    private LiveDetailsDialog(Activity activity, String homeUserID, int sceneMode, RoomOutBean bean, String msgTitle) {
        super(activity);
        mHomeUserID = homeUserID;
        mSceneMode =sceneMode;
        this.bean = bean;
        mMsgTitle = msgTitle;
        mPresenter = new LiveUserPresenter();
        mPresenter.attachView(this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_live_details);
        setCanceledOnTouchOutside(false);
        //替换系统默认的背景颜色
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //全屏
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void initViews() {
        if(!TextUtils.isEmpty(mMsgTitle)) bindingView.tvTopTitle.setText(mMsgTitle);
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_close:
                        LiveDetailsDialog.this.dismiss();
                        break;
                    case R.id.btn_follow:
                        if(null== mHomeUserID) return;
                        if(!Utils.isCheckNetwork()) return;
                        if(null!=mPresenter&&!mPresenter.isLoading()){
                            mIsFollow=(mIsFollow==0?1:0);
                            mPresenter.followUser(UserManager.getInstance().getUserId(), mHomeUserID,mIsFollow);
                        }
                        break;
                    //主页
                    case R.id.re_user_icon:
                        if(null== mHomeUserID) return;
                        //内部处理
                        PersonCenterActivity.start(getActivity(),mHomeUserID);
                        LiveDetailsDialog.this.dismiss();
                        break;
                }
            }
        };

        if (mSceneMode == SCENE_MODE_PLAYER) { //主播端
            bindingView.llPlayerView.setVisibility(View.VISIBLE);
            bindingView.llPlayView.setVisibility(View.GONE);
            if (bean == null){
                bean = new RoomOutBean("0", "0", "0", "00:00:00");
            }
            bindingView.titleAction1.setText(String.format(Locale.CHINA,"收获积分：%s", bean.getPoints()));
            bindingView.titleAction2.setText(String.format(Locale.CHINA,"收获钻石：%s", bean.getDiamond()));
            bindingView.titleAction3.setText(String.format(Locale.CHINA,"获得亲密度：%s", bean.getIntimacy()));
            bindingView.titleAction4.setText(String.format(Locale.CHINA,"直播时长：%s",bean.getDuration()));
        } else { //if (mSceneMode == SCENE_MODE_PLAY) {
            bindingView.llPlayView.setVisibility(View.VISIBLE);
            bindingView.llPlayerView.setVisibility(View.GONE);
            bindingView.btnFollow.setOnClickListener(onClickListener);
        }
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.icUserIcon.setOnClickListener(onClickListener);

        //检查关注状态
        if(!TextUtils.isEmpty(mHomeUserID)) {
            mPresenter.followUser(UserManager.getInstance().getUserId(), mHomeUserID,2);//检查是否已关注此用户
            mPresenter.getUserDetsils(mHomeUserID);
        }
    }

    /**
     * 设置用户信息
     * @param userInfo
     */
    private void setUserData(FansInfo userInfo) {
        if(null==userInfo) return;
        bindingView.oneselfNickname.setText(userInfo.getNickname());
        //设置用户等级
        LiveUtils.setUserGradle(bindingView.oneselfUserGradle, userInfo.getLevel_integral());
        LiveUtils.setUserBlockVipGradle(bindingView.oneselfVipGradle,userInfo.getVip());//设置用户vip等级
//        LiveUtils.setUserVipGradle(bindingView.oneselfVipGradle,userInfo.getVip());
        LiveUtils.setUserSex(bindingView.oneselfUserSex,userInfo.getSex());
        Glide.with(getActivity())
                .load(userInfo.getAvatar())
                .error(R.drawable.ic_default_user_head)
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(getActivity()))
                .into(bindingView.icUserIcon);
    }

    @Override
    public void showFollowUserResult(String data) {
        if(null!=bindingView){
            bindingView.btnFollow.setText(mIsFollow==1?"已关注":"关注");
            bindingView.btnFollow.setBackgroundResource(mIsFollow==1?R.drawable.full_room_gray_bg_pre_8:R.drawable.full_room_follow_selector);
            bindingView.tvPlayState.setVisibility(mIsFollow==1?View.INVISIBLE:View.VISIBLE);//关注提示
        }
    }

    @Override
    public void showFollowUserError(int code, String data) {

    }

    @Override
    public void showIsFollow(int status) {
        this.mIsFollow=status;
        if(null!=bindingView){
            bindingView.btnFollow.setText(status==1?"已关注":"关注");
            bindingView.btnFollow.setBackgroundResource(mIsFollow==1?R.drawable.full_room_gray_bg_pre_8:R.drawable.full_room_follow_selector);
            bindingView.tvPlayState.setVisibility(status==1?View.INVISIBLE:View.VISIBLE);//关注提示
        }
    }

    @Override
    public void showIsFollowError(int code, String data) {

    }

    @Override
    public void showUserDetsilsResult(FansInfo userInfo) {
        setUserData(userInfo);
    }

    @Override
    public void showUserDetsilsError(int code, String msg) {
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }
}
