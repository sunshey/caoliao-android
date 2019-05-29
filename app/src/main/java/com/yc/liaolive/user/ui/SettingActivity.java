package com.yc.liaolive.user.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.manager.MusicWindowManager;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivitySettingBinding;
import com.yc.liaolive.live.manager.SettingsManager;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.ConfigSet;
import com.yc.liaolive.start.manager.VersionCheckManager;
import com.yc.liaolive.start.model.VersionCheckData;
import com.yc.liaolive.start.model.bean.UpdataApkInfo;
import com.yc.liaolive.ui.activity.OpinionFeedActivity;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.IView.SettingContract;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.SettingPresenter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.model.bean.SettingActivityMenuBean;
import com.yc.liaolive.util.FileSizeUtil;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import com.yc.liaolive.view.widget.CommentTitleView;
import com.yc.liaolive.view.widget.SettingItemLayout;

import java.io.File;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2018/6/1
 * 设置中心
 */

public class SettingActivity extends BaseActivity<ActivitySettingBinding> implements SettingContract.View{

    private static final int REQUEST_CODE = 1110;
    private static final String TAG = "SettingActivity";
    private double mFileOrFilesSize;//缓存大小
    private SettingPresenter settingPresenter;
    public static final int LOGOUT_SUCCESS=1;
    private int action=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settingPresenter = new SettingPresenter(this);
        settingPresenter.attachView(this);
        settingPresenter.getActivityMenu();
        checkedCacheSize();
        setActivityMenu(null);
    }

    @Override
    public void initViews() {
        bindingView.titleView.setTitle(getResources().getString(R.string.setting_title));
        bindingView.setBtnSound.setChecked(SharedPreferencesUtil.getInstance().getBoolean(Constant.SOUND_SWITCH, false));
        bindingView.setBtnVibrate.setChecked(SharedPreferencesUtil.getInstance().getBoolean(Constant.VIBRATE_SWITCH, false));
        bindingView.setBtnHwcode.setChecked(ConfigSet.getInstance().isHWCodecEnabled());
        bindingView.setSwitchWindown.setChecked(ConfigSet.getInstance().isAudioOpenWindown());
        bindingView.setBtnSound.setItemClickable(true);
        bindingView.setBtnVibrate.setItemClickable(true);
        bindingView.setBtnHwcode.setItemClickable(true);
        bindingView.btnLoginout.setOnClickListener(onClickListener);
        bindingView.setBtnBlacklist.setOnClickListener(onClickListener);
        bindingView.setBtnCleanCache.setOnClickListener(onClickListener);
        bindingView.setBtnFeedback.setOnClickListener(onClickListener);
        bindingView.setBtnWindowPermission.setOnClickListener(onClickListener);
        bindingView.setBtnHelp.setOnClickListener(onClickListener);
        bindingView.setBtnVersion.setOnClickListener(onClickListener);
        bindingView.setBtnVersion.setItemMoreTitle(Utils.getVersion());
        bindingView.setBtnOnline.setItemClickable(false);
        bindingView.setBtnOnline.setSwitchEnabled(false);
        bindingView.setBtnOnline.setOnClickListener(onClickListener);

        if(UserManager.getInstance().isAuthenState()){
            bindingView.viewAnchorLayout.setVisibility(View.VISIBLE);
        }
        //硬件编码开关
        bindingView.setBtnHwcode.setOnSettingSwitchListener(new SettingItemLayout.OnSettingSwitchListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigSet.getInstance().setHWCodecEnabled(isChecked);
            }
        });
        //声音
        bindingView.setBtnSound.setOnSettingSwitchListener(new SettingItemLayout.OnSettingSwitchListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.getInstance().putBoolean(Constant.SOUND_SWITCH, isChecked);
            }
        });
        //震动
        bindingView.setBtnVibrate.setOnSettingSwitchListener(new SettingItemLayout.OnSettingSwitchListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.getInstance().putBoolean(Constant.VIBRATE_SWITCH, isChecked);
            }
        });
        //悬浮窗
        bindingView.setSwitchWindown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigSet.getInstance().setAudioOpenWindown(isChecked);
                //隐藏悬浮CD 暂停播放音频
                MusicPlayerManager.getInstance().pause();
                MusicWindowManager.getInstance().onInvisible();
            }
        });

        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                onBackPressed();
            }
        });
        //跳转至悬浮窗权限设置
        int is_open_windown = getIntent().getIntExtra("is_open_windown", 0);
        if(is_open_windown>0){
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,REQUEST_CODE);
            }catch (RuntimeException e){
                ToastUtils.showCenterToast("您的手机暂不支持");
            }
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_loginout:
                    if (!Utils.isCheckNetwork()) {
                        ToastUtils.showCenterToast("未检测到可用网络");
                        return;
                    }
                    logout();
                    break;
                case R.id.set_btn_blacklist:
                    startActivity(new Intent(SettingActivity.this, BlacklistActivity.class));
                    break;
                case R.id.set_btn_clean_cache:
                    emptyCache();
                    break;
                case R.id.set_btn_feedback:
                    startActivity(new Intent(SettingActivity.this, OpinionFeedActivity.class));
                    break;
                    //悬浮窗权限设置
                case R.id.set_btn_window_permission:
                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent,REQUEST_CODE);
                    }catch (RuntimeException e){
                        ToastUtils.showCenterToast("您的手机暂不支持");
                    }
                    break;
                case R.id.set_btn_help:
                    startActivity(new Intent(SettingActivity.this, HelpActivity.class));
                    break;
                case R.id.set_btn_version:
                    checkedUpRefreshAPK();
                    break;
                //在线设置状态
                case R.id.set_btn_online:
                    if(null==bindingView.setBtnOnline.getTag()){
                        ToastUtils.showCenterToast("未查询到设置状态，请等待 或 重新进入");
                        return;
                    }
                    changedExcumeMode();
                    break;
            }
        }
    };

    private void logout() {
        QuireDialog.getInstance(SettingActivity.this)
                .setTitleText("账号登出提示")
                .setContentText("确定要退出当前账号吗？")
                .setSubmitTitleText("确定")
                .setCancelTitleText("取消")
                .setDialogCanceledOnTouchOutside(true)
                .setDialogCancelable(true)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        //解绑个推别名设置
                        PushManager.getInstance().unBindAlias(SettingActivity.this,UserManager.getInstance().getUserId(),false);//是否只对当前设备有效，如果为false 则解绑所有绑定此别名的设备
                        startSplash();
                    }

                    @Override
                    public void onRefuse() {

                    }
                }).show();
    }

    @Override
    public void initData() {}

    /**
     * 勿扰状态更新
     */
    private void changedExcumeMode() {
        if(!UserManager.getInstance().isSetExcumeMode()){
            showProgressDialog("设置中,请稍后...",true);
            int excuse=(1==UserManager.getInstance().getQuite()?0:1);
            UserManager.getInstance().setExcuseMode(UserManager.getInstance().getUserId(),excuse, new UserServerContract.OnNetCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    closeProgressDialog();
                    String msg=(1==UserManager.getInstance().getQuite()?"已设置为离线状态":"已设置为在线状态");
                    ToastUtils.showCenterToast(msg);
                    VideoCallManager.getInstance().setCallStatus(0==UserManager.getInstance().getQuite()? VideoCallManager.CallStatus.CALL_FREE:VideoCallManager.CallStatus.CALL_OFFLINE);
                    if(null!=bindingView) bindingView.setBtnOnline.setChecked(0==UserManager.getInstance().getQuite());
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    closeProgressDialog();
                    ToastUtils.showCenterToast(errorMsg);
                }
            });
        }
    }

    /**
     * 检查更新
     */
    private void checkedUpRefreshAPK() {
        showProgressDialog("检查更新中，请稍后...",true);
        VersionCheckData.checkedVerstion(1, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                if (null != object && object instanceof UpdataApkInfo) {
                    UpdataApkInfo updataApkInfo = (UpdataApkInfo) object;
                    VersionCheckManager.getInstance().checkVersion(updataApkInfo, true);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                ToastUtils.showCenterToast(errorMsg);
            }
        });
    }

    /**
     * 清空用户所有数据
     */
    private void startSplash() {
        //清除登录状态
        SharedPreferencesUtil.getInstance().putInt(Constant.SP_START_FIRST,0);
        SharedPreferencesUtil.getInstance().putInt(Constant.SP_SETTING_EXIT,1);
        action=LOGOUT_SUCCESS;
        finish();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (101 == msg.what) {
                if (null != bindingView)
                    bindingView.setBtnCleanCache.setItemMoreTitle(mFileOrFilesSize + "M");
                //清理缓存完成
            } else if (102 == msg.what) {
                showFinlishToast(null, null, getResources().getString(R.string.setting_cache_clean_finlish));
                checkedCacheSize();
            }
        }
    };

    /**
     * 检查缓存大小
     */
    private void checkedCacheSize() {
        //检查SD读写权限
        RxPermissions.getInstance(SettingActivity.this).request(Manifest.permission.READ_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (null != aBoolean && aBoolean) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mFileOrFilesSize = FileSizeUtil.getFileOrFilesSize(ApplicationManager.getInstance().getObjectCacheDir(), 3);
                                mHandler.sendEmptyMessage(101);
                            }catch (RuntimeException e){

                            }
                        }
                    }).start();
                } else {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SettingActivity.this)
                            .setTitle(getResources().getString(R.string.setting_permission_error_title))
                            .setMessage(getResources().getString(R.string.setting_permission_error_tips));
                    builder.setNegativeButton(getResources().getString(R.string.setting_permission_setting), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SystemUtils.getInstance().startAppDetailsInfoActivity(SettingActivity.this, 123);
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    /**
     * 清空缓存
     */
    private void emptyCache() {
        //检查SD读写权限
        RxPermissions.getInstance(SettingActivity.this).request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (null != aBoolean && aBoolean) {
                    if (mFileOrFilesSize > 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.deleteAllFiles(new File(ApplicationManager.getInstance().getObjectCacheDir()));
                                mHandler.sendEmptyMessage(102);
                            }
                        }).start();
                    } else {
                        showErrorToast(null, null, getResources().getString(R.string.setting_no_cache_tips));
                    }
                } else {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SettingActivity.this)
                            .setTitle(getResources().getString(R.string.setting_permission_error_title))
                            .setMessage(getResources().getString(R.string.setting_permission_error_tips));
                    builder.setNegativeButton(getResources().getString(R.string.setting_permission_setting), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SystemUtils.getInstance().startAppDetailsInfoActivity(SettingActivity.this, 123);
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mHandler) mHandler.removeMessages(0);
        mHandler = null;
        if(null!=settingPresenter) settingPresenter.detachView();
        SettingsManager.getInstance().getSettingSubject().onNext(action);
        SettingsManager.getInstance().getSettingSubject().onCompleted();
    }

    @Override
    public void showResult(String data) {
        UserManager.getInstance().setChat_deplete(Integer.parseInt(data));
        ToastUtils.showCenterToast(Html.fromHtml("<font align='center'>已成功设置为每分钟<br/>" + data + "<br/>钻石</font>"));
    }

    @Override
    public void setActivityMenu(SettingActivityMenuBean data) {
        if (data != null && data.getList() != null && data.getList().size() > 0) {
            int margin = Utils.dip2px(16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, Utils.dip2px(48));
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            int[] attribute = new int[]{android.R.attr.selectableItemBackground};
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(typedValue.resourceId, attribute);
            for (int i = 0; i < data.getList().size(); i ++) {
                final SettingActivityMenuBean.ListBean itemBean = data.getList().get(i);
                TextView textView = new TextView(this);
                textView.setBackground(typedArray.getDrawable(0));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                textView.setTextColor(Color.parseColor("#313131"));
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(R.drawable.ic_mine_more), null);
                textView.setText(itemBean.getContent());
                textView.setPadding(margin, 0, margin, 0);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        if (!TextUtils.isEmpty(itemBean.getUrl())) {
                            CaoliaoController.start(itemBean.getUrl(),true,null);
                        }
                    }
                });
                bindingView.activityMenu.addView(textView, params);
//                if (i < data.getList().size() - 1) {
//                    View line = new View(this);
//                    line.setMinimumHeight(Utils.dip2px(0.5f));
//                    line.setBackgroundColor(getResources().getColor(R.color.background_dark));
//                    bindingView.activityMenu.addView(line);
//                }
            }
            View space = new View(this);
            space.setBackgroundColor(getResources().getColor(R.color.background_dark));
            bindingView.activityMenu.addView(space, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, Utils.dip2px(10)));
        }
    }

    @Override
    public void showErrorView() {}
    @Override
    public void complete() {}
}