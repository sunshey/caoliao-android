package com.yc.liaolive.videocall.manager;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.tencent.TIMManager;
import com.yc.liaolive.R;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.live.ui.activity.VipTipsDialogActivity;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.ui.dialog.LoadingProgressView;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.user.ui.ZhimaAuthentiActivity;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.bean.CallResultInfo;
import com.yc.liaolive.videocall.listsner.OnVideoCallBackListener;
import com.yc.liaolive.videocall.ui.activity.LiveCallActivity;
import com.yc.liaolive.videocall.ui.dialog.QuireAnchorDialog;
import com.yc.liaolive.videocall.ui.dialog.QuireVideoDialog;
import com.yc.liaolive.videocall.ui.presenter.VideoCallPresenter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TinyHung@Outlook.com
 * 2018/10/16
 * 呼叫管理者，呼叫、预约等逻辑处理
 */

public class MakeCallManager {

    private static final String TAG = "MakeCallManager";
    protected LoadingProgressView mLoadingProgressedView;
    private static MakeCallManager mInstance;
    private Activity mActivity;
    private CallExtraInfo mCallExtra;
    private boolean interceptAction;//是否拦截事件处理？
    private final VideoCallPresenter mPresenter;

    public static synchronized MakeCallManager getInstance(){
        synchronized (MakeCallManager.class){
            if(null==mInstance){
                mInstance=new MakeCallManager();
            }
        }
        return mInstance;
    }

    public MakeCallManager(){
        mPresenter = new VideoCallPresenter();
    }

    /**
     * 依附Avtivity
     * @param activity
     */
    public MakeCallManager attachActivity(Activity activity){
        mActivity=null;
        this.mActivity=activity;
        return mInstance;
    }

    /**
     * 是否拦截业务事件，当业务界面调用此方法且不为空，业务逻辑交由界面自行处理
     * @param flag 为true && mOnActionListener 不为空生效
     * @return
     */
    public MakeCallManager interceptAction(boolean flag) {
        this.interceptAction=flag;
        return mInstance;
    }

    /**
     * 调用视频通话组件关心业务事件需注册
     * @param listener
     * @return
     */
    public MakeCallManager registerListener(OnActionListener listener) {
        mOnActionListener=listener;
        return mInstance;
    }

    /**
     * 准备开始呼叫对方
     * @param callExtraInfo 通话基本参数
     * @param scene 场景：1 主动给主播打电话 2推送视频回拨 （默认1）
     */
    public void mackCall(CallExtraInfo callExtraInfo, int scene){
        if(null==mActivity){
            throw new IllegalArgumentException("MakeCallManager--You must make a small call to the attachActivity() method!");
        }
        if(TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())){
            ToastUtils.showCenterToast("您的账户正在登入中，请稍后再试");
            return;
        }
        this.mCallExtra =callExtraInfo;
        if(null== mCallExtra ||null==mActivity||mActivity.isFinishing()) return;
        VideoCallManager.getInstance().setBebusying(true);
        showProgressDialog("准备呼叫中...");
        changedCallIdentify();
        Logger.d(TAG,"付费人："+ mCallExtra.getCallUserID()+",主播："+ mCallExtra.getCallAnchorID());
        //1.检查拨号权限
        checkedMakeCallPermission(mCallExtra.getCallUserID(), mCallExtra.getCallAnchorID(),
                mCallExtra.getRecevierID(), scene, new OnVideoCallBackListener() {

                    @Override
            public void onSuccess(Object object) {
                //2.开始视频通话流程,设置为等待接听状态
                        startCall(NetContants.getInstance().URL_CALL_BUSY(), mCallExtra.getCallUserID(), mCallExtra.getCallAnchorID(),
                                mCallExtra.getRecevierID(), String.valueOf(getIdType(mCallExtra.getCallUserID())), new OnVideoCallBackListener() {
                                    @Override
                    public void onSuccess(Object object) {
                        closeProgressDialog();
                        //3.确定接听人剩余通话时长>0则继续
                        if(null!=object && object instanceof CallResultInfo){
                            CallResultInfo resultInfo= (CallResultInfo) object;
                            if(resultInfo.getLimit_time()>0){
                                //主播的价格
                                if(interceptAction&&null!=mOnActionListener){
                                    mOnActionListener.onCallAction(Constant.CHEKCED_REQUST_CALL_SECCESS,"检查通话权限通过");
                                }
                                if(null!=resultInfo.getChat_token()&&null!=resultInfo.getChat_token().getTokens()){
                                    //4.开始呼叫
                                    String toJson = new Gson().toJson(resultInfo.getChat_token().getTokens());
                                    try {
                                        JSONObject jsonObject=new JSONObject(toJson);
                                        String sendRoomToken = jsonObject.getString(UserManager.getInstance().getUserId());
                                        String receiverRoomToken = jsonObject.getString(mCallExtra.getToUserID());
                                        CallExtraInfo callExtraInfo=new CallExtraInfo();
                                        callExtraInfo.setRoomID(resultInfo.getChat_token().getRoom_name());
                                        callExtraInfo.setPrice(String.valueOf(resultInfo.getChat_deplete()));
                                        callExtraInfo.setCallAnchorID(mCallExtra.getCallAnchorID());
                                        callExtraInfo.setRecevierID(mCallExtra.getRecevierID());
                                        callExtraInfo.setCallUserID(mCallExtra.getCallUserID());
                                        callExtraInfo.setSenderRoomToken(sendRoomToken);
                                        callExtraInfo.setReceiverRoomToken(receiverRoomToken);
                                        callExtraInfo.setContent("请求视频通话");
                                        //相对于呼叫方的对方用户信息
                                        callExtraInfo.setToAvatar(mCallExtra.getToAvatar());
                                        callExtraInfo.setToUserID(mCallExtra.getToUserID());
                                        callExtraInfo.setToNickName(mCallExtra.getToNickName());
                                        //其他
                                        callExtraInfo.setVideoPath(mCallExtra.getVideoPath());
                                        callExtraInfo.setAnchorFront(mCallExtra.getAnchorFront());
                                        if(null!=mActivity){
                                            LiveCallActivity.makeCall(mActivity,callExtraInfo);
                                        }
                                    } catch (JSONException e) {
                                        VideoCallManager.getInstance().setBebusying(false);
                                        e.printStackTrace();
                                    }
                                }else{
                                    VideoCallManager.getInstance().setBebusying(false);
                                    ToastUtils.showCenterToast("视频通话服务暂不可用");
                                }
                            }else{
                                VideoCallManager.getInstance().setBebusying(false);
                                ToastUtils.showCenterToast("剩余可通话时长不足");
                            }
                        }else{
                            VideoCallManager.getInstance().setBebusying(false);
                        }
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        VideoCallManager.getInstance().setBebusying(false);
                        closeProgressDialog();
                        ToastUtils.showCenterToast(errorMsg);
                        if(interceptAction&&null!=mOnActionListener){
                            mOnActionListener.onCallAction(code,errorMsg);
                        }
                    }
                });
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                //拦截事件处理
                if(interceptAction&&null!=mOnActionListener){
                    mOnActionListener.onCallAction(code,errorMsg);
                    return;
                }
                VideoCallManager.getInstance().setBebusying(false);
                //金币不足
                if(NetContants.API_RESULT_ARREARAGE_CODE==code){
                    showRechgre();
                    return;
                }
                //可预约主播
                if(NetContants.API_RESULT_USER_OFLINE==code){
                    makeOnUser();
                    return;
                }
                //用户需要芝麻认证
                if(NetContants.API_RESULT_NO_BIND_ZHIMA==code){
                    showVerificationZhima(errorMsg);
                    return;
                }
                //非VIP弹窗提示
                if(NetContants.API_RESULT_NO_VIP_TIPS==code){
                    VipTipsDialogActivity.startVipTipsDialog(errorMsg, mCallExtra.getToAvatar());
                    return;
                }
                ToastUtils.showCenterToast(errorMsg);
            }
        });
    }

    /**
     * 预约主播
     * @param userID 用户ID
     * @param anchorID 主播ID
     * @param callBackListener
     */
    public void subscribeAnchor(String userID, String anchorID,OnVideoCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.subscribeAnchor(userID,anchorID,callBackListener);
    }

    /**
     * 检查拨号状态及权限
     * @param userId 呼叫方ID
     * @param anchorid 接听方ID
     * @param reserve_id 预约ID
     * @param scene 场景：1 主动给主播打电话 2推送视频回拨 （默认1）
     * @param callBackListener
     */
    public void checkedMakeCallPermission(String userId, String anchorid, String reserve_id, int scene, OnVideoCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.checkedMakeCallPermission(userId,anchorid,reserve_id, scene, callBackListener);
    }

    /**
     * 改变视频通话状态
     * @param api api
     * @param userId 呼叫方
     * @param anchorid 接听方
     * @param reserve_id 预约号
     * @param idType  谁触发的视频通话
     * @param callBackListener
     */
    public void changedCallState(String api, String userId, String anchorid, String reserve_id,String idType, OnVideoCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.changedCallState(api,userId,anchorid,reserve_id,idType,callBackListener);
    }

    /**
     * 开始视频通话
     * @param api api
     * @param userId 呼叫方
     * @param anchorid 接听方
     * @param reserve_id 预约号
     * @param idType  谁触发的视频通话
     * @param callBackListener
     */
    public void startCall(String api, String userId, String anchorid, String reserve_id,String idType, OnVideoCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.startCall(api,userId,anchorid,reserve_id,idType,callBackListener);
    }

    /**
     * 购买通话时长
     * @param userId 呼叫方
     * @param anchorid 接听方
     * @param reserve_id 预约号
     * @param is_select 是否只是查询，1：只查询  其他：扣费
     * @param callBackListener
     */
    public void buyCallDuration(String userId, String anchorid, String reserve_id, int is_select,OnVideoCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.buyCallDuration(userId,anchorid,reserve_id,is_select,callBackListener);
    }

    /**
     * 结束视频通话状态
     * @param userID 用户ID
     * @param anchorid 主播ID
     * @param reserveId 预约ID
     * @param idType 是谁先主动挂断 1：前者 2：后者
     * @param callBackListener
     */
    public void endCall(String userID, String anchorid, String reserveId, int idType, OnVideoCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.endCall(userID,anchorid,reserveId,idType,callBackListener);
    }

    /**
     * 上报视频通话心跳事件
     * @param userId
     * @param anchorUserID
     * @param idType
     * @param callBackListener
     */
    public void postCallHeartState(String userId, String anchorUserID, int idType, OnVideoCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.postCallHeartState(userId,anchorUserID,idType,callBackListener);
    }

    /**
     * 查询视频通话结算信息
     * @param roomID
     * @param callBackListener
     */
    public void queryCallData(String roomID, OnVideoCallBackListener callBackListener) {
        if (null != mPresenter) mPresenter.queryCallData(roomID,callBackListener);
    }

    /**
     * 根据发起人身份确定 idType
     * @param callUserID
     * @return idType:向服务器标识是谁主动发起  1：前面参数（发起人） 2：主播
     */
    public int getIdType(String callUserID) {
        return TextUtils.equals(callUserID,UserManager.getInstance().getUserId())?1:2;
    }

    /**
     * 确定发起人(付费人) 和接收人(主播)
     */
    public void changedCallIdentify() {
        if(null==mCallExtra) return;
        mCallExtra.setCallUserID(TextUtils.isEmpty(mCallExtra.getRecevierID())?UserManager.getInstance().getUserId():mCallExtra.getToUserID());
        mCallExtra.setCallAnchorID(TextUtils.isEmpty(mCallExtra.getRecevierID())?mCallExtra.getToUserID():UserManager.getInstance().getUserId());
    }

    /**
     * 芝麻认证对话框
     * @param errorMsg
     */
    private void showVerificationZhima(String errorMsg) {
        if(null!=mActivity){
            QuireDialog.getInstance(mActivity)
                    .setTitleText("温馨提示")
                    .setContentText(errorMsg)
                    .setSubmitTitleText("去认证")
                    .setCancelTitleText("取消")
                    .setDialogCanceledOnTouchOutside(true)
                    .setDialogCancelable(true)
                    .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                        @Override
                        public void onConsent() {
                            CaoliaoController.startActivity(ZhimaAuthentiActivity.class.getName());
                        }

                        @Override
                        public void onRefuse() {

                        }
                    }).show();
        }
    }

    /**
     * 购买钻石
     */
    private void showRechgre() {
        if(null==mActivity||mActivity.isFinishing()) return;
        QuireDialog.getInstance(mActivity)
                .setTitleText("钻石不足")
                .setContentText("是否充值钻石?")
                .setSubmitTitleText("充值")
                .setCancelTitleText("取消")
                .setDialogCancelable(true)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        if(null!=mActivity){
                            VipActivity.startForResult(mActivity,0);
                        }
                    }

                    @Override
                    public void onRefuse() {

                    }
                }).show();
    }

    /**
     * 预约主播
     */
    private void makeOnUser(){
        if(null==mActivity||null== mCallExtra ||mActivity.isFinishing()) return;
        //去邀请上线
        QuireAnchorDialog.getInstance(mActivity)
                .setTipsData("预约TA",mActivity.getResources().getString(R.string.make_call_tips))
                .setTitle(Html.fromHtml("主播暂时无法接通<br>可以<font color='#FF7575'><big>立即预约TA</big></font>或者稍后访问"))
                .setAuthorAvatar(mCallExtra.getToAvatar())
                .setOnSubmitClickListener(new QuireAnchorDialog.OnSubmitClickListener() {
                    /**
                     * 用户确定预约
                     */
                    @Override
                    public void onSubmit() {
                        super.onSubmit();
                        showProgressDialog("预约中，请稍后..");
                        subscribeAnchor(UserManager.getInstance().getUserId(), mCallExtra.getToUserID(), new OnVideoCallBackListener() {
                            @Override
                            public void onSuccess(Object object) {
                                ToastUtils.showCenterToast("已预约");
                                closeProgressDialog();
//                                showMakeSuccess();
                            }

                            @Override
                            public void onFailure(int code, String errorMsg) {
                                closeProgressDialog();
                                ToastUtils.showCenterToast(errorMsg);
                            }
                        });
                    }

                    /**
                     * 前往用户中心
                     */
                    @Override
                    public void onStartUserCenter() {
                        super.onStartUserCenter();
                        if(null!=mActivity&&null!= mCallExtra) PersonCenterActivity.start(mActivity, mCallExtra.getToUserID());

                    }
                })
                .show();
    }

    /**
     * 非会员预约成功弹窗
     */
    private void showMakeSuccess() {
        if(null!=mActivity&&!mActivity.isFinishing()){
            QuireVideoDialog.getInstance(mActivity)
                    .showCloseBtn(true)
                    .setTipsData(Html.fromHtml("<font color='#FF333333'>预约成功!</font>小姐姐邀你享<font color='#FB7078'>5折优惠</font>哟!"), "立即享受")
                    .setDialogCancelable(false)
                    .setDialogCanceledOnTouchOutside(false)
                    .setOnSubmitClickListener(new QuireVideoDialog.OnSubmitClickListener() {
                        @Override
                        public void onSubmit() {
                            super.onSubmit();
                            if(null!=mActivity&&!mActivity.isFinishing()) VipActivity.start(mActivity,1);
                        }
                    }).show();
        }
    }

    /**
     * 显示进度框
     * @param message
     */
    public void showProgressDialog(String message ){
        if(null!=mActivity&&!mActivity.isFinishing()){
            if(null!=mLoadingProgressedView){
                mLoadingProgressedView.dismiss();
                mLoadingProgressedView=null;
            }
            mLoadingProgressedView = new LoadingProgressView(mActivity);
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.show();
        }
    }

    /**
     * 关闭进度框
     */
    public void closeProgressDialog(){
        if(null!=mActivity&&!mActivity.isFinishing()){
            if(null!=mLoadingProgressedView){
                mLoadingProgressedView.dismiss();
                mLoadingProgressedView=null;
            }
        }
    }

    public interface OnActionListener{
        void onCallAction(int code,String msg);
    }
    private OnActionListener mOnActionListener;

    /**
     * 对应方法中销毁
     */
    public void onDestroy(){
        if(null!=mLoadingProgressedView) mLoadingProgressedView.dismiss();
        mActivity=null;
        mCallExtra =null;mLoadingProgressedView=null;interceptAction=false;mOnActionListener=null;
    }
}