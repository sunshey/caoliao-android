package com.yc.liaolive.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tencent.TIMConversationType;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.base.BaseDialogFragment;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.databinding.FragmentUserDetailsBinding;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.ui.contract.LiveUserContract;
import com.yc.liaolive.live.ui.presenter.LiveUserPresenter;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.ui.contract.RoomContract;
import com.yc.liaolive.ui.dialog.CommenNoticeDialog;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/5/19
 * 直播-用户详情
 */

public class LiveUserDetailsFragment extends BaseDialogFragment<FragmentUserDetailsBinding,LiveUserPresenter> implements BaseContract.BaseView, LiveUserContract.View {

    private static final String TAG = "LiveUserDetailsFragment";
    private String mHomeUserID;//宿主用户ID
    public static final int IDENTITY_PULL=0;//观众
    public static final int IDENTITY_PUSHER=1;//主播
    private int mIdentity=IDENTITY_PULL;//0:
    private int mIsFollow;//默认是未关注此用户的
    public FansInfo mUserInfo;
    private String mRoomID;
    private int speechState=0;//未禁言状态
    private String mAnchorID="";

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user_details;
    }

    /**
     * @param userID
     * @return
     */
    public static LiveUserDetailsFragment newInstance(String userID) {
        LiveUserDetailsFragment fragment=new LiveUserDetailsFragment();
        Bundle bundle=new Bundle();
        bundle.putString("userID",userID);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * @param userID
     * @param identity 打开此面板的用户身份 0：观众  1：主播
     * @param roomID 房间ID 禁言需要
     * @return
     */
    public static LiveUserDetailsFragment newInstance(String userID,int identity,String roomID) {
        LiveUserDetailsFragment fragment=new LiveUserDetailsFragment();
        Bundle bundle=new Bundle();
        bundle.putString("userID",userID);
        bundle.putInt("identity",identity);
        bundle.putString("roomID",roomID);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 携带全量信息进入入口
     * @param userInfo
     * @return
     */
    public static LiveUserDetailsFragment newInstance(FansInfo userInfo,int identity,String roomID) {
        LiveUserDetailsFragment fragment=new LiveUserDetailsFragment();
        Bundle bundle=new Bundle();
        bundle.putParcelable("userInfo",userInfo);
        bundle.putString("userID",userInfo.getUserid());
        bundle.putInt("identity",identity);
        bundle.putString("roomID",roomID);
        fragment.setArguments(bundle);
        return fragment;
    }


    /**
     * 携带全量信息进入入口
     * @param userInfo
     * @param identity
     * @param roomID
     * @param anchorID
     * @return
     */
    public static LiveUserDetailsFragment newInstance(FansInfo userInfo,int identity,String roomID,String anchorID) {
        LiveUserDetailsFragment fragment=new LiveUserDetailsFragment();
        Bundle bundle=new Bundle();
        bundle.putParcelable("userInfo",userInfo);
        bundle.putString("userID",userInfo.getUserid());
        bundle.putInt("identity",identity);
        bundle.putString("roomID",roomID);
        bundle.putString("anchorID",anchorID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setAutoHeight(true);//自适应宽高
        setGravity(Gravity.CENTER);//中间显示
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mHomeUserID = arguments.getString("userID");
            mIdentity = arguments.getInt("identity",0);//默认是观众对观众
            mRoomID = arguments.getString("roomID");
            mAnchorID = arguments.getString("anchorID","");
            mUserInfo=arguments.getParcelable("userInfo");
        }
    }

    @Override
    protected void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //举报
                    case R.id.btn_report:
                        //内部处理
                        if(null== mHomeUserID) return;
                        UserManager.getInstance().reportUser(mHomeUserID, new UserServerContract.OnNetCallBackListener() {
                            @Override
                            public void onSuccess(Object object) {
                                try {
                                    if (null!=getActivity())
                                        CommenNoticeDialog.getInstance(getActivity()).setTipsData("举报成功", getResources().getString(R.string.report_user_success), "确定").setOnSubmitClickListener(new CommenNoticeDialog.OnSubmitClickListener() {
                                            @Override
                                            public void onSubmit() {
                                                //处理确认点击事件
                                            }
                                        }).show();
                                } catch (Exception e) {

                                }
                            }

                            @Override
                            public void onFailure(int code, String errorMsg) {
                                ToastUtils.showCenterToast(errorMsg);
                            }
                        });
                        break;
                    //关闭
                    case R.id.btn_close:
                        LiveUserDetailsFragment.this.dismiss();
                        break;
                    //主页
                    case R.id.re_user_icon:
                        if(null== mHomeUserID) return;
                        //内部处理
                        LiveUserDetailsFragment.this.dismiss();
                        if(mAnchorID.equals(mHomeUserID)){
                            PersonCenterActivity.start(getActivity(),mHomeUserID,1);
                        }else{
                            PersonCenterActivity.start(getActivity(),mHomeUserID);
                        }
                        break;
                    //关注
                    case R.id.btn_add_follow:
                        String userID = (String) bindingView.btnAddFollow.getTag();
                        if(!Utils.isCheckNetwork()) return;
                        if(null!=userID){
                            if(null!=mPresenter&&!mPresenter.isLoading()){
                                mIsFollow=(mIsFollow==0?1:0);
                                mPresenter.followUser(UserManager.getInstance().getUserId(), userID,mIsFollow);
                            }
                        }
                        break;
                    //私聊
                    case R.id.btn_private_cacht:
                        String userid = (String) bindingView.btnPrivateCacht.getTag();
                        if(null!=userid){
                            //内部处理
                            LiveUserDetailsFragment.this.dismiss();
                            try {
                                ChatActivity.navToChat(getActivity(), userid, null!=mUserInfo?mUserInfo.getNickname():"",TIMConversationType.C2C);//单独会话
                            }catch (RuntimeException e){

                            }catch (Exception e){

                            }
                        }
                        break;
                    //送礼
                    case R.id.btn_gift:
                        //回调
                        if(null== mHomeUserID) return;
                        LiveUserDetailsFragment.this.dismiss();
                        if(null==mUserInfo){
                            mUserInfo=new FansInfo();
                            mUserInfo.setUserid(mHomeUserID);
                        }
                        if(null!=mOnFunctionClickListener) mOnFunctionClickListener.onSendGift(mUserInfo);
                        break;
                    //禁言
                    case R.id.btn_speech:
                        if(null== mHomeUserID||null==mRoomID) return;
                        LiveRoomManager.getInstance().getLiveRoom().speechToUser(mHomeUserID, 0==speechState?1:0, new RoomContract.OnRoomCallBackListener() {
                            @Override
                            public void onSuccess(Object object) {
                                speechState=(0==speechState?1:0);
                                if(1==speechState){
                                    LiveRoomManager.getInstance().getLiveRoom().addUserToSpeechs(mHomeUserID);
                                }else{
                                    LiveRoomManager.getInstance().getLiveRoom().removeUserForSpeechs(mHomeUserID);
                                }
                                changedSpeechBtn();
                            }

                            @Override
                            public void onFailure(int code, String errorMsg) {
                            }
                        });
                        break;
                }
            }
        };
        bindingView.btnAddFollow.setOnClickListener(onClickListener);
        bindingView.btnPrivateCacht.setOnClickListener(onClickListener);
        bindingView.btnGift.setOnClickListener(onClickListener);
        bindingView.btnSpeech.setOnClickListener(onClickListener);
        bindingView.btnReport.setOnClickListener(onClickListener);
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.reUserIcon.setOnClickListener(onClickListener);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter=new LiveUserPresenter();
        mPresenter.attachView(this);
        bindingView.llSpeech.setVisibility(0== mIdentity ?View.GONE:View.VISIBLE);
        bindingView.btnAddFollow.setEnabled(false);
        if(null!= mHomeUserID) mPresenter.followUser(UserManager.getInstance().getUserId(), mHomeUserID,2);//检查是否已关注此用户
        if(null==mUserInfo){
            mPresenter.getUserDetsils(mHomeUserID);
        }else{
            setUserData(mUserInfo);
        }
        //如果是自己,屏蔽关注和私信能力
        if(null!=mHomeUserID&& TextUtils.equals(mHomeUserID,UserManager.getInstance().getUserId())){
            //关注
            bindingView.ivAddFollow.setColorFilter(Color.parseColor("#999999"));
            bindingView.tvAddFollow.setTextColor(Color.parseColor("#999999"));
            //私信
            bindingView.ivPrivateMsg.setColorFilter(Color.parseColor("#999999"));
            bindingView.tvPrivateMsg.setTextColor(Color.parseColor("#999999"));
        }else if(null!=mHomeUserID){
            bindingView.btnAddFollow.setTag(mHomeUserID);
            bindingView.btnPrivateCacht.setTag(mHomeUserID);
        }
        //处理人物关系,观众端 的禁言不可见
        if(mIdentity==IDENTITY_PULL){
            bindingView.llSpeech.setVisibility(View.GONE);
        }else if(mIdentity==IDENTITY_PUSHER){
            if(null!=mHomeUserID&& TextUtils.equals(mHomeUserID,UserManager.getInstance().getUserId())){
                bindingView.llSpeech.setVisibility(View.GONE);
            }else{
                bindingView.llSpeech.setVisibility(View.VISIBLE);
            }
        }
        //拉取本群已被禁言的用户列表
        if(1==mIdentity&&!TextUtils.isEmpty(mRoomID)){
            List<FansInfo> speechList = LiveRoomManager.getInstance().getLiveRoom().getSpeechList();
            //本群的本地不存在禁言用户列表
            if(null==speechList){
                //直播打开此界面
                LiveRoomManager.getInstance().getLiveRoom().getSpeechList(mRoomID, new RoomContract.OnRoomRequstBackListener() {
                    @Override
                    public void onSuccess(List<FansInfo> data) {
                        //更新本地禁言列表库
                        LiveRoomManager.getInstance().getLiveRoom().setSpeechList(data);
                        changedSpeechBtn();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {

                    }
                });
            }else{
                changedSpeechBtn();
            }
        }
    }

    /**
     * 改变禁言按钮状态
     */
    private void changedSpeechBtn() {
        if(null==bindingView) return;
        boolean existSpeechToUser =LiveRoomManager.getInstance().getLiveRoom().isExistSpeechToUser(mHomeUserID);
        bindingView.btnTextSpeech.setText(existSpeechToUser?"解除禁言":"禁言");
        speechState=existSpeechToUser?1:0;
    }


    private void setUserData(final FansInfo userInfo) {
        if(null== userInfo) return;
        bindingView.oneselfNickname.setText(userInfo.getNickname());
        //设置用户等级
        LiveUtils.setUserGradle(bindingView.oneselfUserGradle, userInfo.getLevel_integral());
        LiveUtils.setUserBlockVipGradle(bindingView.oneselfVipGradle,userInfo.getVip());
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
    public void onDestroy() {
        if(null!=mPresenter) mPresenter.detachView();
        super.onDestroy();
        mOnFunctionClickListener=null;
        mHomeUserID =null; mUserInfo=null;

    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showFollowUserResult(String data) {
        VideoApplication.getInstance().setMineRefresh(true);
        if(null!=bindingView){
            bindingView.ivAddFollow.setVisibility(mIsFollow==1?View.GONE:View.VISIBLE);
            bindingView.tvAddFollow.setText(mIsFollow==1?"取消关注":"关注");
            if(null!=mOnFunctionClickListener) mOnFunctionClickListener.onFollowChanged(mIsFollow);
        }
    }

    @Override
    public void showFollowUserError(int code, String data) {
        if(null!=mOnFunctionClickListener) mOnFunctionClickListener.onFollowChanged(mIsFollow);
    }

    @Override
    public void showIsFollow(int status) {
        this.mIsFollow=status;
        if(null!=bindingView){
            bindingView.btnAddFollow.setEnabled(true);
            bindingView.ivAddFollow.setVisibility(status==1?View.GONE:View.VISIBLE);
            bindingView.tvAddFollow.setText(status==1?"取消关注":"关注");
        }
    }

    @Override
    public void showIsFollowError(int code, String data) {

    }

    @Override
    public void showUserDetsilsResult(FansInfo userInfo) {
        this.mUserInfo=userInfo;
        setUserData(userInfo);
    }

    @Override
    public void showUserDetsilsError(int code, String msg) {

    }



    public static abstract class OnFunctionClickListener{
       public void onSendGift(FansInfo userInfo){}
       public void onFollowChanged(int status){}
    }

    private OnFunctionClickListener mOnFunctionClickListener;

    public LiveUserDetailsFragment setOnFunctionClickListener(OnFunctionClickListener onFunctionClickListener) {
        mOnFunctionClickListener = onFunctionClickListener;
        return this;
    }
}
