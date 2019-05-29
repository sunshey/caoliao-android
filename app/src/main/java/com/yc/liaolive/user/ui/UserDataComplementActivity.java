package com.yc.liaolive.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.ActivityUserDataComplementBinding;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.ui.contract.UploadImageContract;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.ui.presenter.UploadImagePresenter;
import com.yc.liaolive.util.PhotoSelectedUtil;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.CommentTitleView;
import com.yc.liaolive.model.GlideCircleTransform;
import java.io.File;

/**
 * TinyHung@Outlook.com
 * 2018/6/20
 * 用户资料强制补全,头像更改等
 */

public class UserDataComplementActivity extends BaseActivity<ActivityUserDataComplementBinding> implements UploadImageContract.View {

    private static final String TAG = "UserDataComplementActivity";
    private UploadImagePresenter mPresenter;
    private Animation mInputAnimation;

    @Override
    public void initViews() {
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                onBackPressed();
            }

            @Override
            public void onMoreTitleClick(View v) {
                super.onMoreTitleClick(v);
                next();
            }
        });

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //更改用户头像
                    case R.id.iv_user_icon:
                        showPictureSelectorPop();
                        break;
                    //男生
                    case R.id.tv_man:
                        bindingView.tvWumen.setSelected(false);
                        bindingView.tvMan.setSelected(true);
                        break;
                    //女生
                    case R.id.tv_wumen:
                        bindingView.tvMan.setSelected(false);
                        bindingView.tvWumen.setSelected(true);
                        break;
                    //下一步
                    case R.id.btn_next:
                        next();
                        break;
                }
            }
        };
        bindingView.tvMan.setOnClickListener(onClickListener);
        bindingView.tvWumen.setOnClickListener(onClickListener);
        bindingView.ivUserIcon.setOnClickListener(onClickListener);
        bindingView.btnNext.setOnClickListener(onClickListener);
        bindingView.tvMan.setSelected(true);

        bindingView.btnNext.getLayoutParams().width= ScreenUtils.getScreenWidth()/2;
    }

    private void next() {
        if(null==bindingView) return;
        String title="您当前选择的是"+(bindingView.tvMan.isSelected()?"男":bindingView.tvWumen.isSelected()?"女":"火星人");
        QuireDialog.getInstance(UserDataComplementActivity.this)
                .setTitleText(title)
                .setContentText(getResources().getString(R.string.user_complment_edit_tips))
                .setContentTextColor(getResources().getColor(R.color.commont_title_7c))
                .setSubmitTitleText("我确定")
                .setSubmitTitleTextColor(getResources().getColor(R.color.black))
                .setCancelTitleText("去修改")
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        //提交资料
                        submitUserData();
                    }

                    @Override
                    public void onRefuse() {

                    }
                }).show();
    }

    /**
     * 提交用户信息
     */
    private void submitUserData() {
        String nickName = bindingView.etInput.getText().toString().trim();
        if(TextUtils.isEmpty(nickName)){
            ToastUtils.showCenterToast("请输入昵称");
            bindingView.etInput.startAnimation(mInputAnimation);
            return;
        }
        showProgressDialog("更新中，请稍后..",true);
        int userSex=bindingView.tvMan.isSelected()?0:1;
        //更新用户信息
        UserManager.getInstance().uploadUserInfo(nickName, null, null, userSex, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                ToastUtils.showCenterToast("修改成功");
                Intent intent=new Intent();
                intent.putExtra("complment","1");
                setResult(Constant.REGISTER_COMPLEMENT_RESULT_CODE,intent);
                finish();
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                ToastUtils.showCenterToast(errorMsg);
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data_complement);
        mPresenter = new UploadImagePresenter();
        mPresenter.attachView(this);
        mInputAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
    }

    /**
     * 设置用户头像
     * @param url
     */
    private void setUserHeadCover(String url) {
        if(this.isFinishing()) return;
        Glide.with(this)
                .load(url)
                .error(R.drawable.ic_list_empty_icon_2)
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(UserDataComplementActivity.this))
                .into(bindingView.ivUserIcon);
    }


    /**
     * 照片选择弹窗
     */
    private void showPictureSelectorPop() {
        PhotoSelectedUtil.getInstance()
                .attachActivity(this)//依附Activity
                .setCatScaleHeight(300)//裁剪输出的比例 宽
                .setCatScaleWidth(300)//裁剪输出的比例 高
                .setMaxWidth(600)//输出的最大像素宽度
                .setClipCircle(true)//是否是圆形裁剪输出
                .setOnSelectedPhotoOutListener(new PhotoSelectedUtil.OnSelectedPhotoOutListener() {
            @Override
            public void onOutFile(File file) {
                if(file.exists()&&file.isFile()){
                    if(null!=mPresenter&&!mPresenter.isLoading()){
                        mPresenter.onPostImagePhoto(NetContants.getInstance().URL_UPLOAD_USER_AVTAR(), UserManager.getInstance().getUserId(),file.getPath(),"avatar");
                    }
                }
            }

            @Override
            public void onError(int code, String errorMsg) {
                ToastUtils.showCenterToast(errorMsg);
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoSelectedUtil.getInstance().onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PhotoSelectedUtil.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void showPostImageResult(String data) {
        ToastUtils.showCenterToast("头像上传成功!");
        if(null==data) return;
        setUserHeadCover(data);
    }

    @Override
    public void showPostImageError(int errorCode, String data) {
        ToastUtils.showCenterToast(data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mInputAnimation) mInputAnimation.cancel(); mInputAnimation=null;
        PhotoSelectedUtil.getInstance().onDestroy();
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }
}
