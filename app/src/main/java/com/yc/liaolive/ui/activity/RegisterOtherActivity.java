package com.yc.liaolive.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import com.bumptech.glide.Glide;
import com.kaikai.securityhttp.domain.GoagalInfo;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yc.liaolive.BuildConfig;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.bean.UserDataInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityOtherRegisterBinding;
import com.yc.liaolive.live.listener.OnSpannableUserClickListener;
import com.yc.liaolive.live.util.LiveChatUserGradleSpan;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.HostManager;
import com.yc.liaolive.model.GlideRoundTransform;
import com.yc.liaolive.observer.SubjectObservable;
import com.yc.liaolive.ui.business.LoginBusiness;
import com.yc.liaolive.ui.dialog.LoadingProgressView;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ChannelUtls;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.manager.AudioPlayerManager;
import com.yc.liaolive.webview.ui.WebViewActivity;
import java.io.IOException;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/5/24
 * 第三方账号注册
 */

public class RegisterOtherActivity extends TopBaseActivity implements Observer {

    private static final String TAG = "RegisterOtherActivity";
    ActivityOtherRegisterBinding bindingView;
    protected LoadingProgressView mLoadingProgressedView;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;
    private boolean isWXLoginCancel = false; //用于微信双开取消时处理

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFullScreen(true);
        super.onCreate(savedInstanceState);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_other_register);
        AudioPlayerManager.getInstance().stopPlayer();
        ApplicationManager.getInstance().addObserver(this);
        initViews();
    }

    private void initViews() {
        //根据包的类型播放图片还是视频
        String loginType = getResources().getString(R.string.login_type);
        if(ChannelUtls.getInstance().getChannel().equals("app_group")||"login_image".equals(loginType)){
            //只有图片的模式
            bindingView.icLoginBg.setImageResource(R.drawable.login_bg);
            bindingView.llOtherLogin.setVisibility(View.INVISIBLE);
            bindingView.viewBg.getBackground().setAlpha(0);
            bindingView.tvLoginProtocol.setVisibility(View.GONE);
        } else if (BuildConfig.FLAVOR.contains("caoliao")) {
            bindingView.llOtherLogin.setVisibility(View.VISIBLE);
            bindingView.icLoginBg.setImageResource(0);
            bindingView.viewBg.getBackground().setAlpha(125);
            bindingView.tvLoginProtocol.setVisibility(View.VISIBLE);
        } else {
            bindingView.llOtherLogin.setVisibility(View.INVISIBLE);
            bindingView.icLoginBg.setImageResource(0);
            bindingView.viewBg.getBackground().setAlpha(125);
            bindingView.tvLoginProtocol.setVisibility(View.GONE);
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //微信登录
                    case R.id.btn_weixin_login:
                        if(LoginBusiness.getInstance().isPackageInstalled(RegisterOtherActivity.this, Constant.PACKAGE_WEIXIN)){
                            login(SHARE_MEDIA.WEIXIN);
                        }
                        break;
                    //QQ登录
                    case R.id.ll_qq:
                        if(LoginBusiness.getInstance().isPackageInstalled(RegisterOtherActivity.this,Constant.PACKAGE_QQ)){
                            login(SHARE_MEDIA.QQ);
                        }
                        break;
                    //手机号码登录
                    case R.id.ll_phone:
                        startActivityForResult(new Intent(RegisterOtherActivity.this, RegisterPhoneActivity.class), Constant.REGISTER_PHONE_REQUST_CODE);
                        break;
                    //游客登录
                    case R.id.ll_visitor:
                        visitorRegister();
                        break;
                }
            }
        };

        bindingView.btnWeixinLogin.setOnClickListener(onClickListener);
        bindingView.llQq.setOnClickListener(onClickListener);
        bindingView.llPhone.setOnClickListener(onClickListener);
        bindingView.llVisitor.setOnClickListener(onClickListener);
        bindingView.tvLoginProtocol.setText(getResources().getString(R.string.text_other_login));
        // 服务条款部分文字点击事件处理
        String content="登录即表示您已阅读并同意<font color='#FFF8583C'>《服务条款》</font>";
        SpannableString spannableString = LiveChatUserGradleSpan.stringFormat(Html.fromHtml(content), "《服务条款》",bindingView.tvLoginProtocol,getResources().getColor(R.color.login_hint),new OnSpannableUserClickListener() {
            @Override
            public void onClick(String userID) {
                WebViewActivity.loadUrl(RegisterOtherActivity.this, HostManager.getInstance().getLoginServer(),"服务条款");
            }
        });
        bindingView.tvLoginProtocol.setText(spannableString);
        if(VideoApplication.getInstance().isLoginIcon()){
            bindingView.ivLoginIcon.setVisibility(View.VISIBLE);
        }
        Glide.with(RegisterOtherActivity.this).load(R.drawable.ic_launcher).error(R.drawable.ic_launcher).transform(new GlideRoundTransform(RegisterOtherActivity.this,10)).into(bindingView.ivLoginIcon);
    }

    /**
     * 初始化播放器
     */
    private void initStartPlayer() {
        onDestoryPlayer();
        mSurfaceView = new SurfaceView(this);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(null!=mMediaPlayer){
                    mMediaPlayer.setDisplay(holder);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(null!=mMediaPlayer){
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer=null;
                }
            }
        });
        bindingView.surfaceView.addView(mSurfaceView);

        mMediaPlayer = new MediaPlayer();
        //播放完成监听
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();//加载完成
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //视频播放失败，显示默认的图片
                bindingView.viewBg.getBackground().setAlpha(0);
                return false;
            }
        });
        //从SD卡
//        try {
//            mMediaPlayer.setDataSource(url);
//            mMediaPlayer.setVolume(0, 0);//关闭声音
//            mMediaPlayer.setLooping(true);
//            mMediaPlayer.prepareAsync();//异步加载
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//        }catch (Exception e){
//
//        }

        //从资产目录
//        AssetManager assetManager = getAssets();
//        try {
//            AssetFileDescriptor fileDescriptor = assetManager.openFd("login_video.mp4");
//            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
//            mMediaPlayer.setVolume(0, 0);//关闭声音
//            mMediaPlayer.setLooping(true);
//            mMediaPlayer.prepareAsync();//异步加载
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//        }catch (Exception e){
//
//        }
        //从RAW
        Uri mUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.login_video);
        try {
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setDataSource(RegisterOtherActivity.this, mUri);
            mMediaPlayer.setVolume(0, 0);//关闭声音
            mMediaPlayer.prepareAsync();//异步加载
        } catch (RuntimeException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁播放器
     */
    private void onDestoryPlayer() {
        if(null!=mMediaPlayer){
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
        if(null!=bindingView){
            bindingView.surfaceView.removeAllViews();
        }
        mSurfaceView=null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isWXLoginCancel) {
            closeProgressDialog();
        }
        String loginType = getResources().getString(R.string.login_type);
        if(BuildConfig.FLAVOR.contains("caoliao")||"login_video".equals(loginType)){
            initStartPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onDestoryPlayer();
    }

    /**
     * 游客注册
     */
    private void visitorRegister() {
        showProgressDialog("操作中，请稍后...",false);
        UserManager.getInstance().visitorRegister(new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                Intent intent = new Intent();
                intent.putExtra("login", "1");
                setResult(Constant.REGISTER_RESULT_CODE, intent);
                finish();
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                ToastUtils.showCenterToast(errorMsg);
            }
        });
    }

    /**
     * QQ、微信、微博 登录-获取用户授权信息
     *
     * @param media
     */
    public void login(SHARE_MEDIA media) {
        boolean isauth = UMShareAPI.get(RegisterOtherActivity.this).isAuthorize(RegisterOtherActivity.this, media);//判断当前APP有没有授权登录
        if (isauth) {
            UMShareAPI.get(RegisterOtherActivity.this).getPlatformInfo(RegisterOtherActivity.this, media, LoginAuthListener);//获取用户信息
        } else {
            UMShareAPI.get(RegisterOtherActivity.this).doOauthVerify(RegisterOtherActivity.this, media, LoginAuthListener);//用户授权登录
        }
    }

    /**
     * QQ、微信注册
     *
     * @param userDataInfo
     * @param platform
     */
    private void register(UserDataInfo userDataInfo, SHARE_MEDIA platform) {
        if (null == userDataInfo) return;
        //立即删除授权记录
        UMShareAPI.get(RegisterOtherActivity.this).deleteOauth(RegisterOtherActivity.this, platform, null);
        UserManager.getInstance().registerByOther(userDataInfo.getOpenid(), userDataInfo.getAccessToken(), userDataInfo.getLoginType(), new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                Intent intent = new Intent();
                intent.putExtra("login", "1");
                setResult(Constant.REGISTER_RESULT_CODE, intent);
                finish();
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                ToastUtils.showCenterToast(errorMsg);
            }
        });
    }


    /**
     * QQ 微信 微博 登陆后回调
     */
    UMAuthListener LoginAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            isWXLoginCancel = share_media == SHARE_MEDIA.WEIXIN;
            showProgressDialog("登录中，请稍后...", true);
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            int loginType = Constant.LOGIN_TYPE_QQ;
            switch (platform) {
                case QQ:
                    loginType = Constant.LOGIN_TYPE_QQ;
                    break;
                case WEIXIN:
                    loginType = Constant.LOGIN_TYPE_WEXIN;
                    break;
                case SINA:
                    loginType = Constant.LOGIN_TYPE_WEIBO;
                    break;
            }
            try {
                if (null != data && data.size() > 0) {
                    UserDataInfo userDataInfo = new UserDataInfo();
                    userDataInfo.setIemil(GoagalInfo.get().uuid);
                    userDataInfo.setLoginType(String.valueOf(loginType));
                    //新浪微博
                    if (platform == SHARE_MEDIA.SINA) {
                        userDataInfo.setNickname(data.get("name"));
                        userDataInfo.setProvince(data.get("location"));
                        userDataInfo.setFigureurl_qq_2(data.get("iconurl"));
                        userDataInfo.setGender(data.get("gender"));
                        userDataInfo.setOpenid(data.get("id"));
                        userDataInfo.setImageBG(data.get("cover_image_phone"));
                        //微信、QQ
                    } else {
                        userDataInfo.setNickname(data.get("screen_name"));
                        userDataInfo.setCity(data.get("city"));
                        userDataInfo.setFigureurl_qq_2(data.get("iconurl"));
                        userDataInfo.setGender(data.get("gender"));
                        userDataInfo.setProvince(data.get("province"));
                        userDataInfo.setOpenid(data.get("openid"));
                        userDataInfo.setAccessToken(data.get("accessToken"));
                        userDataInfo.setUid(data.get("uid"));
                    }
                    //授权成功,再次请求用户token和用户基本信息
                    if (TextUtils.isEmpty(userDataInfo.getNickname()) && TextUtils.isEmpty(userDataInfo.getFigureurl_qq_2())) {
                        login(platform);
                    } else {
                        //登录App成功,防止微博
                        if (!TextUtils.isEmpty(userDataInfo.getOpenid())) {
                            register(userDataInfo,platform);
                        } else {
                            login(platform);
                        }
                    }
                } else {
                    closeProgressDialog();
                    ToastUtils.showCenterToast("登录失败，请重试!");
                }
            } catch (Exception e) {
                closeProgressDialog();
                ToastUtils.showCenterToast("登录失败，请重试!");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            closeProgressDialog();
            ToastUtils.showCenterToast("登录失败，"+t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            closeProgressDialog();
            ToastUtils.showCenterToast("登录取消");
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("login_canel", "1");
        setResult(Constant.REGISTER_RESULT_CODE, intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
        onDestoryPlayer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //注册成功、用户信息补全成功
        if (Constant.REGISTER_PHONE_REQUST_CODE == requestCode && resultCode == Constant.REGISTER_PHONE_RESULT_CODE && null != data) {
            //手机号码注册账号成功，返回开屏页自动登录
            Intent intent = new Intent();
            intent.putExtra("login", "1");
            setResult(Constant.REGISTER_RESULT_CODE, intent);
            finish();
        }
    }

    /**
     * 显示进度框
     *
     * @param message
     * @param isProgress
     */
    public void showProgressDialog(String message, boolean isProgress) {
        if (!RegisterOtherActivity.this.isFinishing()) {
            if (null == mLoadingProgressedView) {
                mLoadingProgressedView = new LoadingProgressView(this);
            }
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.show();
        }
    }

    /**
     * 关闭进度框
     */
    public void closeProgressDialog() {
        try {
            if (!RegisterOtherActivity.this.isFinishing()) {
                if (null != mLoadingProgressedView && mLoadingProgressedView.isShowing()) {
                    mLoadingProgressedView.dismiss();
                }
                mLoadingProgressedView = null;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof SubjectObservable&&null!=arg&&arg instanceof String){
            if(((String) arg).equals(Constant.OBSERVER_CMD_APP_AVAILABLE)){
                if(null!=bindingView){
                    bindingView.ivLoginIcon.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}