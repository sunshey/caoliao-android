package com.yc.liaolive.start.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.ChatExtra;
import com.yc.liaolive.bean.CustomMsgCall;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivitySplashBinding;
import com.yc.liaolive.index.ui.MainActivity;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.start.manager.AppManager;
import com.yc.liaolive.start.manager.StartManager;
import com.yc.liaolive.ui.activity.RegisterOtherActivity;
import com.yc.liaolive.ui.contract.SplashContract;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TinyHung@outlook.com
 * 2017/5/20 12:20
 * 开屏页-检查登录状态自动登录，未登录或自动登录跳转至注册界面
 */

public class SplashActivity extends AppCompatActivity implements SplashContract.View {

    private static final String TAG = "SplashActivity";
    private static int TOTA_TIME = 0;
    private ActivitySplashBinding bindingView = null;
    private Timer timeTask;
    private SplashPresenter mPresenter;
    //向用户申请的权限列表
    private PermissionModel[] models=null;
    /**
     * 避免BUG，1000以内的数值
     */
    private final static int READ_PHONE_STATE_CODE = 101;//读取手机唯一识别身份
    private final static int WRITE_EXTERNAL_STORAGE_CODE = 102;//SD卡

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        models=new PermissionModel[]{
                new PermissionModel(Manifest.permission.READ_PHONE_STATE, "我们需要读取手机信息的权限设备号来标识您的身份", READ_PHONE_STATE_CODE),
                new PermissionModel(Manifest.permission.WRITE_EXTERNAL_STORAGE, "为方便我们存储临时数据和保障部分功能的正常使用，我们需要您允许我们读写你的存储卡", WRITE_EXTERNAL_STORAGE_CODE),
        };
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        //项目初始化配置，需要每次进入都设置
                        StartManager.getInstance().startConfig();
                        onInit();
                    }
                });
            }
        });
    }

    /**
     * 初始化
     */
    private void onInit() {
        //激活统计
        if(0==SharedPreferencesUtil.getInstance().getInt(Constant.SP_FIRST_ACTIVATION,0)){
            UserManager.getInstance().activation(AppEngine.getApplication());
        }
        if (Build.VERSION.SDK_INT < 23) {
            startNextActivity();
            return;
        }
        checkPermissions();//安卓6.0申请权限
    }

    /**
     * 处理账号登录逻辑
     */
    private void startNextActivity() {
        AppManager.getInstance().setHttpDefaultParams();
        mPresenter = new SplashPresenter();
        mPresenter.attachView(this);
        mPresenter.onCreate();
    }

    /**
     * 登录成功 进入主页
     */
    @Override
    public void navToHome() {
        Intent intent = new Intent(AppEngine.getApplication(), MainActivity.class);
        //启动的APP包含参数传入
        try {
            if(null!=getIntent()){
                //直播间入参
                if(null!=getIntent().getParcelableExtra(Constant.APP_START_EXTRA_ROOM)){
                    RoomExtra roomExtra = getIntent().getParcelableExtra(Constant.APP_START_EXTRA_ROOM);
                    intent.putExtra(Constant.APP_START_EXTRA_ROOM,roomExtra);
                //视频通话入参
                }else if(null!=getIntent().getParcelableExtra(Constant.APP_START_EXTRA_CALL)){
                    CallExtraInfo callExtraInfo = getIntent().getParcelableExtra(Constant.APP_START_EXTRA_CALL);
                    intent.putExtra(Constant.APP_START_EXTRA_CALL, callExtraInfo);
                //虚拟视频通话推送入参
                }else if(null!=getIntent().getParcelableExtra(Constant.APP_START_EXTRA_CALL_FALSE)){
                    CustomMsgCall customMsgCall = getIntent().getParcelableExtra(Constant.APP_START_EXTRA_CALL_FALSE);
                    intent.putExtra(Constant.APP_START_EXTRA_CALL_FALSE, customMsgCall);
                //私信入参
                }else if(null!=getIntent().getSerializableExtra(Constant.APP_START_EXTRA_CHAT)){
                    ChatExtra chatExtra = (ChatExtra) getIntent().getSerializableExtra(Constant.APP_START_EXTRA_CHAT);
                    intent.putExtra(Constant.APP_START_EXTRA_CHAT, chatExtra);
                //视频通话，悬浮窗权限未成功获取状态下
                }else if(null!=getIntent().getParcelableExtra(Constant.APP_START_EXTRA_DOALOG_CHAT)){
                    CallExtraInfo callExtraInfo = getIntent().getParcelableExtra(Constant.APP_START_EXTRA_DOALOG_CHAT);
                    intent.putExtra(Constant.APP_START_EXTRA_DOALOG_CHAT,callExtraInfo);
                }
            }
        }catch (RuntimeException e){

        }finally {
            startActivity(intent);
            finish();
        }
    }

    /**
     * 注册、登录失败 或者 从设置中退出账号的用户 去登录界面
     */
    @Override
    public void navToLogin() {
        //去登录
        Intent intent = new Intent(SplashActivity.this, RegisterOtherActivity.class);
        startActivityForResult(intent, Constant.REGISTER_REQUST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Constant.REGISTER_REQUST_CODE == requestCode && resultCode == Constant.REGISTER_RESULT_CODE && null != data) {
            //注册成功
            if (null != data.getStringExtra("login")) {
                if(null!=mPresenter){
                    mPresenter.onCreate();
                }else{
                    UserManager.getInstance().autoLogin(new UserServerContract.OnNetCallBackListener() {
                        @Override
                        public void onSuccess(Object object) {
                            navToHome();
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            navToLogin();
                        }
                    });
                }
            //登录取消
            }else if(null != data.getStringExtra("login_canel")){
                finish();
            }
        } else if (123 == requestCode) {
            if (isAllRequestedPermissionGranted()) {
                startNextActivity();
            } else {
                checkPermissions();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 运行时权限
     */
    private void checkPermissions() {
        try {
            for (PermissionModel model : models) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, model.permission)) {
                    ActivityCompat.requestPermissions(this, new String[]{model.permission}, model.requestCode);
                    return;
                }
            }
            // 到这里就表示所有需要的权限已经通过申
            startNextActivity();
        } catch (Throwable e) {
            startNextActivity();
        }
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
            case READ_PHONE_STATE_CODE:
            case WRITE_EXTERNAL_STORAGE_CODE:
//            case REQUEST_PERMISSION_CODE:
                // 如果用户不允许，我们视情况发起二次请求或者引导用户到应用页面手动打开
                if(null!=grantResults&&grantResults.length>0){
                    if (PackageManager.PERMISSION_GRANTED != grantResults[0]) {
                        // 二次请求，表现为：以前请求过这个权限，但是用户拒接了
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            new AlertDialog.Builder(this).setTitle("权限申请失败").setMessage(findPermissionExplain(permissions[0]))
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkPermissions();
                                        }
                                    }).show();
                        }
                        // 到这里就表示已经是第3+次请求，让用户自己手动打开
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this)
                                    .setTitle("权限申请失败")
                                    .setMessage("部分权限被拒绝获取，没有授予权限将无法使用后续功能，是否立即前往设置中心授予本软件存储权限?");
                            builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SystemUtils.getInstance().startAppDetailsInfoActivity(SplashActivity.this, 123);
                                }
                            });
                            builder.show();
                        }
                        return;
                    }
                    // 到这里就表示用户允许了本次请求，继续检查是否还有待申请的权限没有申请
                    if (isAllRequestedPermissionGranted()) {
                        startNextActivity();
                    } else {
                        checkPermissions();
                    }
                }
                break;
        }
    }

    private String findPermissionExplain(String permission) {
        if (models != null) {
            for (PermissionModel model : models) {
                if (model != null && model.permission != null && model.permission.equals(permission)) {
                    return model.explain;
                }
            }
        }
        return null;
    }

    private boolean isAllRequestedPermissionGranted() {
        if(null!=models){
            for (final PermissionModel model : models) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, model.permission)) {
                    return false;
                }
            }
        }
        return true;
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * 开始倒计时
     */
    private void startTimeTask() {
        bindingView.btnTips.setVisibility(View.VISIBLE);
        if (timeTask == null) timeTask = new Timer();
        //延缓执行
        int task = 0;
        timeTask.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != bindingView)
                            bindingView.btnTips.setText("跳过 " + TOTA_TIME + "");
                        TOTA_TIME--;
                        if (TOTA_TIME < 0) {
                            jumpNetx();
                        }
                    }
                });
            }
        }, task, 1000);
    }

    /**
     * 手动跳过\或者倒计时完成\点击广告 都走这里
     */
    private void jumpNetx() {
        bindingView.btnTips.setVisibility(View.GONE);
        if (null != timeTask) {
            timeTask.cancel();
        }
        startNextActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
    }

    @Override
    public void onDestroy() {
        if (null != mPresenter) mPresenter.detachView(); mPresenter=null;
        if(null!=models) models=null;
        bindingView = null;
        getWindow().setBackgroundDrawable(null);//释放背景图片
        super.onDestroy();
    }
}