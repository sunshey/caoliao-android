package com.yc.liaolive.base;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.kaikai.securityhttp.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.ShareInfo;
import com.yc.liaolive.bean.ShareMenuItemInfo;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.interfaces.OnShareFinlishListener;
import com.yc.liaolive.interfaces.ShareFinlishListener;
import com.yc.liaolive.interfaces.SnackBarListener;
import com.yc.liaolive.manager.ActivityCollectorManager;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.ui.dialog.LoadingProgressView;
import com.yc.liaolive.ui.dialog.LoginAwardDialog;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.ui.dialog.RedAnimationDialog;
import com.yc.liaolive.ui.dialog.ShareDialog;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ShareUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.ui.dialog.QuireCallFinlishDialog;
import com.yc.liaolive.videocall.ui.dialog.QuireVideoDialog;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-06-27 22:29
 * 顶部的通用BaseActivity,主要统一分享操作
 */

public class TopBaseActivity extends AppCompatActivity  {

    private static final String TAG = "TopBaseActivity";
    private ShareInfo mShareInfo;
    protected LoadingProgressView mLoadingProgressedView;
    protected boolean isFront = false;//自己是否在前台运行
    private boolean isFullScreen=false;
    private ShareFinlishListener shareFinlishListener;//分享的监听，用于通知创建分享的对象
    private QuireVideoDialog mCallQuireTips;//结束提示
    private QuireCallFinlishDialog mCallFinlishDialog;//结算

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        if (isFullScreen&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        ActivityCollectorManager.addActivity(this);
    }

    /**
     * 设置Dialog显示在屏幕中央
     */
    protected int initLayoutParams() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager)getSystemService(android.content.Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        int hight= LinearLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height= hight;
        //兼容低分辨率机型
        int screenDensity = ScreenUtils.getScreenDensity();
        attributes.width= (systemService.getDefaultDisplay().getWidth()-(screenDensity>300?190:90));
        attributes.gravity= Gravity.CENTER;
        return attributes.width;
    }

    /**
     * 设置Dialog显示在屏幕底部
     */
    protected void setActivityLayoutParams() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager)getSystemService(android.content.Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        attributes.width=LinearLayout.LayoutParams.MATCH_PARENT;
        attributes.height= LinearLayout.LayoutParams.WRAP_CONTENT;
        attributes.gravity= Gravity.BOTTOM;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFront=true;
        if(!Utils.isCheckNetwork()){
            showNetWorkTips();
        }
        MobclickAgent.onResume(this);
//        if(VideoApplication.getInstance().getGiveNum()>0){
//            LoginAwardDialog.getInstance(TopBaseActivity.this,"赠送的"+VideoApplication.getInstance().getGiveNum()+"钻石已到账！", (int) VideoApplication.getInstance().getGiveNum()).setBg(R.drawable.ic_login_vip_dialog_bg).show();
//            VideoApplication.getInstance().setGiveNum(0);
//        }
        //绑定手机的任务已经完成了
        if(VideoApplication.getInstance().isGetPhoneTask()&&VideoApplication.getInstance().getTaskCoin()>0){
            RedAnimationDialog.getInstance(TopBaseActivity.this,VideoApplication.getInstance().getTaskCoin()).show();
            VideoApplication.getInstance().setGetPhoneTask(false);
        }
        if(null!=mCallQuireTips) {
            mCallQuireTips.dismiss();
            mCallQuireTips=null;
        }
        try {
            //视频通话结算信息
            if(null!=VideoApplication.getInstance().getCallCloseExtra()&&!TextUtils.isEmpty(VideoApplication.getInstance().getCallCloseExtra().getRoomID())){
                if(null== mCallQuireTips&&!TopBaseActivity.this.isFinishing()){
                    String message=TextUtils.isEmpty(VideoApplication.getInstance().getCallCloseExtra().getCloseMsg())?"视频通话已结束":VideoApplication.getInstance().getCallCloseExtra().getCloseMsg();
                    mCallQuireTips = QuireVideoDialog.getInstance(TopBaseActivity.this);
                    mCallQuireTips.showCloseBtn(true)
                            .setTipsData("视频通话结束提示",message, "确定")
                            .setDialogCancelable(false)
                            .setDialogCanceledOnTouchOutside(false)
                            .setOnSubmitClickListener(new QuireVideoDialog.OnSubmitClickListener() {
                                @Override
                                public void onSubmit() {
                                    showDetails();
                                }

                                @Override
                                public void onClose() {
                                    showDetails();
                                }
                            });
                    mCallQuireTips.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mCallQuireTips=null;
                        }
                    });
                    mCallQuireTips.show();
                }
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 结算提示框
     */
    private void showDetails() {
        if(null!=mCallQuireTips) {
            mCallQuireTips.dismiss();
            mCallQuireTips=null;
        }
        if(null!=mCallFinlishDialog) {
            mCallFinlishDialog.dismiss();
            mCallFinlishDialog=null;
        }
        try {
            if(null==mCallFinlishDialog&&!TopBaseActivity.this.isFinishing()&&null!=VideoApplication.getInstance().getCallCloseExtra()){
                mCallFinlishDialog = QuireCallFinlishDialog.getInstance(TopBaseActivity.this)
                        .setUserData(VideoApplication.getInstance().getCallCloseExtra())
                        .getCallData(VideoApplication.getInstance().getCallCloseExtra().getIdType(),VideoApplication.getInstance().getCallCloseExtra().getRoomID())
                        .showCloseBtn(true);
                mCallFinlishDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        VideoApplication.getInstance().setCallCloseExtra(null);
                        mCallFinlishDialog=null;
                    }
                });
                mCallFinlishDialog.show();
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 释放通话状态
     */
    public void resetCallState(){
        VideoApplication.getInstance().setCallCloseExtra(null);
        if(null!=mCallQuireTips){
            mCallQuireTips.dismiss();
            mCallQuireTips=null;
        }
        if(null!=mCallFinlishDialog){
            mCallFinlishDialog.dismiss();
            mCallFinlishDialog=null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront=false;
        MobclickAgent.onPause(this);
    }

    /**
     * 分享直播间
     * @param shareInfo
     */
    public void share(final ShareInfo shareInfo) {
        if(null==shareInfo){
            showErrorToast(null,null,"缺少分享参数");
            return;
        }
        this.mShareInfo = shareInfo;
        shareIntent();
    }

    /**
     * 分享直播间
     * @param shareInfo
     */
    public void share(final ShareInfo shareInfo,ShareFinlishListener shareFinlishListener) {
        if(null==shareInfo){
            showErrorToast(null,null,"缺少分享参数");
            return;
        }
        this.shareFinlishListener=shareFinlishListener;
        this.mShareInfo = shareInfo;
        shareIntent();
    }

    /**
     * 分享
     */
    private void shareIntent() {
        if(null== mShareInfo){
            return;
        }
        ShareDialog.getInstance(this).setRoomID(mShareInfo.getRoomid()).setOnItemClickListener(new ShareDialog.OnShareItemClickListener() {
            @Override
            public void onItemClick(ShareMenuItemInfo shareMenuItemInfo) {
                ShareUtils.baseShare(TopBaseActivity.this, mShareInfo, shareMenuItemInfo.getPlatform(), new OnShareFinlishListener() {
                    @Override
                    public void onShareStart(SHARE_MEDIA media) {
                    }

                    @Override
                    public void onShareResult(SHARE_MEDIA media) {
                        if(!TopBaseActivity.this.isFinishing()&&null!=mShareInfo){
                            showFinlishToast(null,null,"分享成功");
                            if(!mShareInfo.isReport()) return;//如果不需要上报，无需处理
                            int platform=1;
                            switch (media) {
                                case QQ:
                                    platform=1;
                                    break;
                                case WEIXIN:
                                    platform=2;
                                    break;
                                case SINA:
                                    platform=3;
                                    break;
                                case WEIXIN_CIRCLE:
                                    platform=4;
                                    break;
                                case QZONE:
                                    platform=5;
                                    break;
                            }
                            //视频播放模块分享成功之后交由播放器处理
                            if(null!=shareFinlishListener){
                                shareFinlishListener.shareSuccess(mShareInfo.getVideoID(),platform);
                            }else{
                                //暂时直播间分享成功之后直接上报结果
                                UserManager.getInstance().shareStatistics(mShareInfo.getUserID(), platform, new UserServerContract.OnNetCallBackListener() {
                                    @Override
                                    public void onSuccess(Object object) {
                                    }

                                    @Override
                                    public void onFailure(int code, String errorMsg) {
                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onShareCancel(SHARE_MEDIA media) {
                    }

                    @Override
                    public void onShareError(SHARE_MEDIA media, Throwable throwable) {
                    }
                });
            }
        }).show();
    }

    /**
     * 分享到其他
     * @param shareInfo
     */
    public void shareOther(ShareInfo shareInfo) {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,shareInfo.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, shareInfo.getUrl());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.shared_to)));
    }

    /**
     * 显示进度框
     * @param message
     * @param isProgress
     */
    public void showProgressDialog(String message,boolean isProgress){
        if(!TopBaseActivity.this.isFinishing()){
            if(null==mLoadingProgressedView){
                mLoadingProgressedView = new LoadingProgressView(this);
            }
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.show();
        }
    }

    /**
     * 关闭进度框
     */
    public void closeProgressDialog(){
        try {
            if(!TopBaseActivity.this.isFinishing()){
                if(null!=mLoadingProgressedView&&mLoadingProgressedView.isShowing()){
                    mLoadingProgressedView.dismiss();
                }
                mLoadingProgressedView=null;
            }
        }catch (Exception e){

        }
    }

    public void showErrorToast(String action, SnackBarListener snackBarListener, String message){
        ToastUtils.showSnackebarStateToast(getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_error_white, Constant.SNACKBAR_ERROR,message);
    }
    public void showNetWorkTips(){
        showErrorToast("网络设置", new SnackBarListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);//直接进入网络设置
                startActivity(intent);
            }
        }, "没有可用的网络链接");
    }
    public void showFinlishToast(String action, SnackBarListener snackBarListener, String message){
        ToastUtils.showSnackebarStateToast(getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_done_white, Constant.SNACKBAR_DONE,message);
    }

    @Override
    protected void onDestroy() {
        mShareInfo=null;
        if(null!=mLoadingProgressedView){
            mLoadingProgressedView.dismiss();
            mLoadingProgressedView=null;
        }
        super.onDestroy();
        models=null;
        ActivityCollectorManager.removeActivity(this);
        UMShareAPI.get(this).release();
    }

    //===========================================权限处理============================================

    //权限处理
    protected final static int CAMERA = 103;//拍照
    protected final static int RECORD_AUDIO = 104;//录音
    protected final static int SETTING_REQUST = 123;
    protected static final int PREMISSION_CANCEL=0;//权限被取消申请
    protected static final int PREMISSION_SUCCESS=1;//权限申请成功

    /**
     * 向用户申请的权限列表
     */
    protected static PermissionModel[] models;

    /**
     * 运行时权限
     */
    protected void requstPermissions() {
        if(Build.VERSION.SDK_INT < 23){
            onRequstPermissionResult(PREMISSION_SUCCESS);
            return;
        }
        if(null==models) models=new PermissionModel[]{
                new PermissionModel(Manifest.permission.CAMERA, "使用视频通话功能必须授予拍照权限", CAMERA),
                new PermissionModel(Manifest.permission.RECORD_AUDIO, "使用视频通话功能必须授予录音权限", RECORD_AUDIO),
        };
        try {
            for (PermissionModel model : models) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, model.permission)) {
                    ActivityCompat.requestPermissions(this, new String[]{model.permission}, model.requestCode);
                    return;
                }
            }
            // 到这里就表示所有需要的权限已经通过申
            onRequstPermissionResult(PREMISSION_SUCCESS);
        } catch (Throwable e) {
        }
    }

    /**
     * 请求权限成功
     * @param resultCode 1：已授予 0：未授予
     */
    protected void onRequstPermissionResult(int resultCode) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SETTING_REQUST == requestCode) {
            if (isAllRequestedPermissionGranted()) {
                onRequstPermissionResult(PREMISSION_SUCCESS);
            } else {
                requstPermissions();
            }
        } else if(resultCode==Constant.RECHARGE_RESULT_CODE&&null!=data&&TextUtils.equals(Constant.VIP_SUCCESS,data.getStringExtra("vip"))){
            //充值界面返回
            buyVipSuccess();
            try{
                if(UserManager.getInstance().isVip()){
                    UserManager.getInstance().getTasks("2", new UserServerContract.OnNetCallBackListener() {
                        @Override
                        public void onSuccess(Object object) {
                            if(null!=object && object instanceof List){
                                List<TaskInfo> taskInfos= (List<TaskInfo>) object;
                                for (TaskInfo task : taskInfos) {
                                    if(task.getApp_id()==Constant.APP_TASK_VIP){
                                        UserManager.getInstance().drawTaskAward(task, new UserServerContract.OnNetCallBackListener() {
                                            @Override
                                            public void onSuccess(Object object) {
                                                if(null!=object&& object instanceof TaskInfo){
                                                    TaskInfo taskInfo= (TaskInfo) object;
                                                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED_NET);
                                                    if(!TopBaseActivity.this.isFinishing()){
                                                        LoginAwardDialog.getInstance(TopBaseActivity.this,"今日的"+taskInfo.getCoin()+"钻石已送达！",taskInfo.getCoin()).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(int code, String errorMsg) {
                                            }
                                        });
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                        }
                    });
                }
            }catch (RuntimeException e){

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }

    /**
     * 申请结果回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA:
            case RECORD_AUDIO:
                if(null!=grantResults&&grantResults.length>0){
                    if (PackageManager.PERMISSION_GRANTED != grantResults[0]) {
                        //用户拒绝过其中一个权限
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            QuireDialog.getInstance(TopBaseActivity.this)
                                    .setTitleText("部分权限申请失败")
                                    .setContentText(findPermissionExplain(permissions[0]))
                                    .setSubmitTitleTextColor(getResources().getColor(R.color.app_red_style))
                                    .setSubmitTitleText("确定")
                                    .setCancelTitleText("取消")
                                    .setDialogCancelable(false)
                                    .setDialogCanceledOnTouchOutside(false)
                                    .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                                        @Override
                                        public void onConsent() {
                                            requstPermissions();
                                        }

                                        @Override
                                        public void onRefuse() {
                                            onRequstPermissionResult(PREMISSION_CANCEL);
                                        }
                                    }).show();
                        } else {
                            //用户勾选了不再询问，手动开启
                            QuireDialog.getInstance(TopBaseActivity.this)
                                    .setTitleText("部分权限申请失败")
                                    .setContentText("拍照或录音权限被禁止，请点击‘去设置’手动开启拍照和录音权限")
                                    .setSubmitTitleTextColor(getResources().getColor(R.color.app_red_style))
                                    .setSubmitTitleText("去设置")
                                    .setCancelTitleText("取消")
                                    .setDialogCancelable(false)
                                    .setDialogCanceledOnTouchOutside(false)
                                    .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                                        @Override
                                        public void onConsent() {
                                            //SystemUtils.getInstance().startAppDetailsInfoActivity(TopBaseActivity.this, SETTING_REQUST);
                                            startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", getPackageName(), null)), SETTING_REQUST);
                                        }

                                        @Override
                                        public void onRefuse() {
                                            onRequstPermissionResult(PREMISSION_CANCEL);
                                        }
                                    }).show();
                        }
                        return;
                    }
                    // 到这里就表示用户允许了本次请求，继续检查是否还有待申请的权限没有申请
                    if (isAllRequestedPermissionGranted()) {
                        onRequstPermissionResult(PREMISSION_SUCCESS);
                    } else {
                        requstPermissions();
                    }
                }
                break;
        }
    }

    protected String findPermissionExplain(String permission) {
        if (null!=models) {
            for (PermissionModel model : models) {
                if (model != null && model.permission != null && model.permission.equals(permission)) {
                    return model.explain;
                }
            }
        }
        return null;
    }

    protected boolean isAllRequestedPermissionGranted() {
        if(null!=models){
            for (PermissionModel model : models) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, model.permission)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static class PermissionModel {
        public String permission;
        public String explain;
        public int requestCode;

        public PermissionModel(String permission, String explain, int requestCode) {
            this.permission = permission;
            this.explain = explain;
            this.requestCode = requestCode;
        }
    }

    protected void buyVipSuccess() {}
}
