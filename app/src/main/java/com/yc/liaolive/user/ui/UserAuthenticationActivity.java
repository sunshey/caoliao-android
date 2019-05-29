package com.yc.liaolive.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.UploadObjectInfo;
import com.yc.liaolive.bean.VideoDetailsMenu;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.common.ControllerConstant;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.ActivityUsreAuthenticationBinding;
import com.yc.liaolive.index.adapter.IndexVideoListAdapter;
import com.yc.liaolive.interfaces.OnUploadObjectListener;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.UploadFileToOSSManager;
import com.yc.liaolive.media.adapter.UserHeadAdapter;
import com.yc.liaolive.media.ui.activity.MediaLocationVideoListActivity;
import com.yc.liaolive.media.ui.activity.VerticalImagePreviewActivity;
import com.yc.liaolive.model.RecyclerViewSpacesItem;
import com.yc.liaolive.ui.activity.ContentFragmentActivity;
import com.yc.liaolive.ui.contract.IdentityAuthenticationContract;
import com.yc.liaolive.ui.dialog.AuthenticationDialog;
import com.yc.liaolive.ui.dialog.CommonMenuDialog;
import com.yc.liaolive.ui.dialog.DataPickerDialog;
import com.yc.liaolive.ui.presenter.IdentityAuthenticationPresenter;
import com.yc.liaolive.user.IView.ModifyInfoContract;
import com.yc.liaolive.user.manager.ModifyInfoPresenter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.view.SelectSexDialog;
import com.yc.liaolive.util.DataFactory;
import com.yc.liaolive.util.PhotoSelectedUtil;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.util.VideoDataUtils;
import com.yc.liaolive.util.VideoSelectedUtil;
import com.yc.liaolive.view.layout.DataLoadView;
import com.yc.liaolive.view.widget.AuthenticationImageLayout;
import com.yc.liaolive.view.widget.CommentTitleView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * TinyHung@Outlook.com
 * 2018/10/17
 * 主播实名认证
 */

public class UserAuthenticationActivity extends BaseActivity<ActivityUsreAuthenticationBinding> implements IdentityAuthenticationContract.View, ModifyInfoContract.View {

    private static final String TAG = "UserAuthenticationActivity";
    private TimePickerView pvTime;
    private IdentityAuthenticationPresenter mPresenter;
    private String identityDate;
    private Animation mAnimation;
    private UserHeadAdapter mAdapter;
    private UserHeadAdapter mVideoAdapter;
    private DataLoadView mDataLoadView;
    private ModifyInfoPresenter mModifyPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usre_authentication);
        mPresenter = new IdentityAuthenticationPresenter(this);
        mPresenter.attachView(this);
        mModifyPresenter = new ModifyInfoPresenter();
        mModifyPresenter.attachView(this);
        mModifyPresenter.getUserHeads();
        mModifyPresenter.getUserVideos();
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
    }

    @Override
    public void initViews() {
        bindingView.commentTitleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                finish();
            }
        });

        //用户头像处理
        bindingView.tvImageTips.setText(Html.fromHtml("<font color='#E6646E'>*</font>至少上传2张照片，建议上传5-8张照片"));
        bindingView.tvVideoTips.setText(Html.fromHtml("<font color='#E6646E'>*</font>至少上传1个视频，建议上传1个以上"));
        String authenTips = getResources().getString(R.string.authen_tips);
        bindingView.authenTips.setText(Html.fromHtml("<font color='#FF0000'>*</font>"+authenTips));
        mAdapter = new UserHeadAdapter(null);
        mDataLoadView = new DataLoadView(UserAuthenticationActivity.this);
        mDataLoadView.setLoadHeight(mAdapter.getItemHeight()+ScreenUtils.dpToPxInt(4f));
        mDataLoadView.setOnRefreshListener(new DataLoadView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mModifyPresenter&&!mModifyPresenter.isLoading()){
                    mDataLoadView.showLoadingView();
                    mModifyPresenter.getUserHeads();
                }
            }
        });
        mDataLoadView.showLoadingView();
        mAdapter.setEmptyView(mDataLoadView);
        //条目点击事件
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, final View view, int position) {
                if(null!=view.getTag()){
                    PrivateMedia privateMedia = (PrivateMedia) view.getTag();
                    //拦截点击头部事件
                    if(privateMedia.getItemType()== IndexVideoListAdapter.ITEM_TYPE_ADD){
                        selectedMediaImage();
                        return;
                    }
                    //条目点击事件..
                    //预览多张图片
                    List<PrivateMedia> newImages=new ArrayList<>();
                    newImages.add(privateMedia);
                    VideoDataUtils.getInstance().setHostUrl(NetContants.getInstance().URL_FILE_LIST());
                    VideoDataUtils.getInstance().setFileType(0);
                    VideoDataUtils.getInstance().setIndex(-1);
                    VideoDataUtils.getInstance().setVideoData(newImages,position);
                    //先关闭可能打开的照片预览界面
                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_FINLISH_MEDIA_PLAYER);
                    new android.os.Handler(Looper.getMainLooper()).postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            VerticalImagePreviewActivity.start(UserAuthenticationActivity.this,UserManager.getInstance().getUserId(),view);
                        }
                    }, SystemClock.uptimeMillis()+100);
                }
            }
        });
        //条目长按事件
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if(0!=position&&null!=view.getTag()){
                    PrivateMedia privateMedia = (PrivateMedia) view.getTag();
                    showEditMenu(privateMedia,position);
                }
                return false;
            }
        });

        bindingView.recyclerView.setLayoutManager(new GridLayoutManager(UserAuthenticationActivity.this,4,GridLayoutManager.VERTICAL,false));
        bindingView.recyclerView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(3f)));
        bindingView.recyclerView.setHasFixedSize(true);
        bindingView.recyclerView.setNestedScrollingEnabled(false);//不允许单独行动
        bindingView.recyclerView.setAdapter(mAdapter);
        //小视频
        mVideoAdapter = new UserHeadAdapter(null);
        //条目点击事件
        mVideoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, final View view, int position) {
                if(null!=view.getTag()){
                    PrivateMedia privateMedia = (PrivateMedia) view.getTag();
                    //拦截点击头部事件
                    if(privateMedia.getItemType()== IndexVideoListAdapter.ITEM_TYPE_ADD){
                        selectedMediaVideo();
                        return;
                    }
                }
            }
        });
        bindingView.recyclerVideoView.setLayoutManager(new GridLayoutManager(UserAuthenticationActivity.this,4,GridLayoutManager.VERTICAL,false));
        bindingView.recyclerVideoView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(3f)));
        bindingView.recyclerVideoView.setHasFixedSize(true);
        bindingView.recyclerVideoView.setNestedScrollingEnabled(false);//不允许单独行动
        bindingView.recyclerVideoView.setAdapter(mVideoAdapter);

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //选择身份证有效期
                    case R.id.btn_select_date:
                        AuthenticationDialog dialog = new AuthenticationDialog(UserAuthenticationActivity.this);
                        dialog.show();
                        showDatePick();
                        dialog.setOnSelectDateListener(new AuthenticationDialog.onSelectDateListener() {
                            @Override
                            public void onSelectDate() {
                                if (pvTime != null && !pvTime.isShowing()) {
                                    showDatePick();
                                }
                            }

                            @Override
                            public void onSelectForever() {
                                if (pvTime != null) {
                                    pvTime.dismiss();
                                }
                                bindingView.tvExpire.setText("长期有效");
                            }
                        });
                        break;
                    //提交认证请求
                    case R.id.btn_submit:
                        submitUserData();
                        break;
                    //昵称
                    case R.id.item_nickname:
                        ModifyDataInfoActivity.start(UserAuthenticationActivity.this,bindingView.itemNickname.getTitleTextContent(),bindingView.itemNickname.getMoreTextContent(),12,null,Constant.MODITUTY_KEY_NICKNAME);
                        break;
                    //手机号码绑定
                    case R.id.item_bind_phone:
                        CaoliaoController.startActivity(ControllerConstant.BindPhoneTaskActivity);
                        break;
                    //性别
                    case R.id.item_sex:
                        SelectSexDialog selectSexDialog = new SelectSexDialog(UserAuthenticationActivity.this);
                        selectSexDialog.show();
                        selectSexDialog.setOnSelectSexListener(new SelectSexDialog.onSelectSexListener() {
                            @Override
                            public void onSelectSext(int sexId) {
                                updataUserInfo(Constant.MODITUTY_KEY_SEX,String.valueOf(sexId));
                            }
                        });
                        break;
                    //身高
                    case R.id.item_user_height:
                        DataPickerDialog.getInstance(UserAuthenticationActivity.this,"cm",UserManager.getInstance().getHeight()).setOnSelectedListener(new DataPickerDialog.OnSelectedListener() {
                            @Override
                            public void onItemSelected(int id, String content) {
                                if(TextUtils.equals(content,UserManager.getInstance().getHeight())){
                                    ToastUtils.showCenterToast("请选择与现有身高不一致的项");
                                    return;
                                }
                                bindingView.itemUserHeight.setItemMoreTitle(content+"cm");
                                updataUserInfo(Constant.MODITUTY_KEY_HEIGHT,content);
                            }
                        }).show();
                        break;
                    //体重
                    case R.id.item_user_weight:
                        DataPickerDialog.getInstance(UserAuthenticationActivity.this,"kg",UserManager.getInstance().getWeight()).setOnSelectedListener(new DataPickerDialog.OnSelectedListener() {
                            @Override
                            public void onItemSelected(int id, String content) {
                                if(TextUtils.equals(content,UserManager.getInstance().getWeight())){
                                    ToastUtils.showCenterToast("请选择与现有体重不一致的项");
                                    return;
                                }
                                bindingView.itemUserWeight.setItemMoreTitle(content+"kg");
                                updataUserInfo(Constant.MODITUTY_KEY_WEIGHT,content);
                            }
                        }).show();
                        break;
                    //星座
                    case R.id.item_user_start:
                        DataPickerDialog.getInstance(UserAuthenticationActivity.this,"start",UserManager.getInstance().getStar()).setOnSelectedListener(new DataPickerDialog.OnSelectedListener() {
                            @Override
                            public void onItemSelected(int id, String content) {
                                if(TextUtils.equals(content,UserManager.getInstance().getStar())){
                                    ToastUtils.showCenterToast("请选择与星座不一致的项");
                                    return;
                                }
                                bindingView.itemUserStart.setItemMoreTitle(content);
                                updataUserInfo(Constant.MODITUTY_KEY_STAR,content);
                            }
                        }).show();
                        break;
                    //所在城市
                    case R.id.item_city:
//                        startSelectedCity();
                        showCityList();
                        break;
                    //个人介绍
                    case R.id.item_user_desp:
                        ModifyDataInfoActivity.start(UserAuthenticationActivity.this,bindingView.itemUserDesp.getTitleTextContent(),bindingView.itemUserDesp.getMoreTextContent(),30,null,Constant.MODITUTY_KEY_SPECIALITY);
                        break;
                    //形象标签
                    case R.id.item_user_tag:
                        ContentFragmentActivity.start(UserAuthenticationActivity.this,Constant.FRAGMENT_TYPE_USER_TAG,"选择标签",null,null);
                        break;
                    //个性签名
                    case R.id.item_user_singtrue:
                        ModifyDataInfoActivity.start(UserAuthenticationActivity.this,bindingView.itemUserSingtrue.getTitleTextContent(),bindingView.itemUserSingtrue.getMoreTextContent(),30,null,Constant.MODITUTY_KEY_SIGNTURE);
                        break;
                }
            }
        };
        //身份证有效期选择
        bindingView.btnSelectDate.setOnClickListener(onClickListener);
        //提交认证
        bindingView.btnSubmit.setOnClickListener(onClickListener);
        //基本信息
        bindingView.itemNickname.setOnClickListener(onClickListener);
        bindingView.itemBindPhone.setOnClickListener(onClickListener);
        bindingView.itemSex.setOnClickListener(onClickListener);
        bindingView.itemUserHeight.setOnClickListener(onClickListener);
        bindingView.itemUserWeight.setOnClickListener(onClickListener);
        bindingView.itemUserStart.setOnClickListener(onClickListener);
        bindingView.itemCity.setOnClickListener(onClickListener);
        bindingView.itemUserDesp.setOnClickListener(onClickListener);
        bindingView.itemUserTag.setOnClickListener(onClickListener);
        bindingView.itemUserSingtrue.setOnClickListener(onClickListener);

        //照片选取监听
        AuthenticationImageLayout.OnItemClickListener onItemClickListener=new AuthenticationImageLayout.OnItemClickListener() {
            @Override
            public void onClickkAdd(View view) {
                //正面照
                if(R.id.authent_front==view.getId()){
                    checkedImage(0);
                //反面照
                }else if(R.id.authent_unfront==view.getId()){
                    checkedImage(1);
                }
            }
        };
        //确定身份证图片控件宽高为16:11
        int screenWidth = ScreenUtils.getScreenWidth();
        int itemWidth = (screenWidth - ScreenUtils.dpToPxInt(30f)) / 2;
        bindingView.authentFront.setImageLayoutParams(itemWidth,itemWidth/16*11);//按照9:16比例显示
        bindingView.authentUnfront.setImageLayoutParams(itemWidth,itemWidth/16*11);//按照9:16比例显示
        bindingView.authentFront.setOnItemClickListener(onItemClickListener);
        bindingView.authentUnfront.setOnItemClickListener(onItemClickListener);
        bindingView.inputNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)&&s.length()>0){
                    bindingView.btnSubmit.setBackgroundResource(R.drawable.btn_login_app_style_cicle);
                }else{
                    bindingView.btnSubmit.setBackgroundResource(R.drawable.btn_login_shen_gray_cicle);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //预览照片、视频回显
        if(-1==VideoDataUtils.getInstance().getIndex()){
            VideoDataUtils.getInstance().setPosition(0);
            VideoDataUtils.getInstance().setIndex(0);
            VideoDataUtils.getInstance().setFileType(0);
            VideoDataUtils.getInstance().setHostUrl(null);
        }
        if(null!=bindingView){
            //用户基本信息回显
            if (!TextUtils.isEmpty(UserManager.getInstance().getPhone())) {
                bindingView.itemBindPhone.setItemMoreTitle(Utils.submitPhone(UserManager.getInstance().getPhone(),3,7));
            }else{
                bindingView.itemBindPhone.setItemMoreTitle(getString(R.string.unbinded));
            }
            bindingView.itemNickname.setItemMoreTitle(UserManager.getInstance().getNickname());
            bindingView.itemSex.setItemMoreTitle(UserManager.getInstance().getSex() == 0 ? getString(R.string.sex_man) : getString(R.string.sex_wumen));
            bindingView.itemUserHeight.setItemMoreTitle(TextUtils.isEmpty(UserManager.getInstance().getHeight())?getString(R.string.make_input):UserManager.getInstance().getHeight()+"m");
            bindingView.itemUserWeight.setItemMoreTitle(TextUtils.isEmpty(UserManager.getInstance().getWeight())?getString(R.string.make_input):UserManager.getInstance().getWeight()+"kg");
            bindingView.itemUserStart.setItemMoreTitle(TextUtils.isEmpty(UserManager.getInstance().getStar())?getString(R.string.make_input) :UserManager.getInstance().getStar());
            bindingView.itemUserSingtrue.setItemMoreTitle(TextUtils.isEmpty(UserManager.getInstance().getSignature()) ? getString(R.string.make_input) : UserManager.getInstance().getSignature());
            bindingView.itemUserDesp.setItemMoreTitle(TextUtils.isEmpty(UserManager.getInstance().getSpeciality()) ? getString(R.string.make_input) : UserManager.getInstance().getSpeciality());
            String position = UserManager.getInstance().getPosition();
            bindingView.itemCity.setItemMoreTitle(TextUtils.isEmpty(position) ? getString(R.string.make_input) : position);
            String tag = DataFactory.framtTags(UserManager.getInstance().getLabel());
            bindingView.itemUserTag.setItemMoreTitle(TextUtils.isEmpty(tag)?getString(R.string.make_input):tag);
        }
        //视频编辑完成
        if(null!=VideoApplication.getInstance().getUploadObjectInfo()){
            UploadFileToOSSManager.get(UserAuthenticationActivity.this).addUploadListener(new OnUploadObjectListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(long progress) {
                }

                @Override
                public void onSuccess(UploadObjectInfo data, String msg) {
                    ToastUtils.showCenterToast(msg);
                    VideoApplication.getInstance().setMineRefresh(true);
                    if(null!=mModifyPresenter) mModifyPresenter.getUserHeads();
                }

                @Override
                public void onFail(int code, String errorMsg) {
                    ToastUtils.showCenterToast(errorMsg);
                }
            }).createAsyncUploadTask(VideoApplication.getInstance().getUploadObjectInfo());
            VideoApplication.getInstance().setUploadObjectInfo(null);
        }
    }

    @Override
    protected void updateUserSex() {
        super.updateUserSex();
        bindingView.itemSex.setItemMoreTitle(UserManager.getInstance().getSex() == 0 ? getString(R.string.sex_man) : getString(R.string.sex_wumen));
    }

    /**
     * 选择照片
     */
    private void selectedMediaImage() {
//        Intent intent=new Intent(UserAuthenticationActivity.this,MediaLocationImageListSingerActivity.class);
//        startActivityForResult(intent, Constant.SELECT_SINGER_IMAGE_REQUST);
        requstPermissions();
    }

    @Override
    protected void onRequstPermissionResult(int resultCode) {
        super.onRequstPermissionResult(resultCode);
        if(PREMISSION_SUCCESS==resultCode){
            PhotoSelectedUtil.getInstance()
                    .attachActivity(UserAuthenticationActivity.this)
                    .setCatScaleWidth(1)
                    .setCatScaleHeight(1)
                    .setCropMode(0)
                    .setOnSelectedPhotoOutListener(new PhotoSelectedUtil.OnSelectedPhotoOutListener() {
                        @Override
                        public void onOutFile(File file) {
                            UploadFileToOSSManager.get(UserAuthenticationActivity.this).addUploadListener(new OnUploadObjectListener() {
                                @Override
                                public void onStart() {
                                }

                                @Override
                                public void onProgress(long progress) {

                                }

                                @Override
                                public void onSuccess(UploadObjectInfo data, String msg) {
                                    ToastUtils.showCenterToast(msg);
                                    VideoApplication.getInstance().setMineRefresh(true);
                                    if(null!=mModifyPresenter) mModifyPresenter.getUserHeads();
                                }

                                @Override
                                public void onFail(int code, String errorMsg) {
                                    ToastUtils.showCenterToast(errorMsg);
                                }
                            }).createAsyncUploadTask(file);
                        }

                        @Override
                        public void onError(int code, String errorMsg) {

                        }
                    }).start();
        }else{
            ToastUtils.showCenterToast("拍照权限被拒绝");
        }
    }

    /**
     * 选择视频
     */
    private void selectedMediaVideo() {
        Intent intent=new Intent(UserAuthenticationActivity.this,MediaLocationVideoListActivity.class);
        startActivityForResult(intent,Constant.SELECT_VIDEO_REQUST);
    }

    /**
     * 长按触发了编辑
     * @param privateMedia
     * @param position
     */
    private void showEditMenu(final PrivateMedia privateMedia, final int position) {
        if(IndexVideoListAdapter.ITEM_TYPE_ADD==privateMedia.getItemType()) return;//拦截上传照片的长按事件
        SystemUtils.startVibrator(100);
        List<VideoDetailsMenu> list=new ArrayList<>();
        VideoDetailsMenu videoDetailsMenu1=new VideoDetailsMenu();
        videoDetailsMenu1.setItemID(1);
        videoDetailsMenu1.setTextColor("#FF555555");
        videoDetailsMenu1.setItemName("设为封面");
        list.add(videoDetailsMenu1);
        if(position>1){
            VideoDetailsMenu videoDetailsMenu2=new VideoDetailsMenu();
            videoDetailsMenu2.setItemID(2);
            videoDetailsMenu2.setTextColor("#FFFF7575");
            videoDetailsMenu2.setItemName("删除");
            list.add(videoDetailsMenu2);
        }
        CommonMenuDialog commonMenuDialog =new CommonMenuDialog(UserAuthenticationActivity.this);
        commonMenuDialog.setData(list);
        commonMenuDialog.setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int itemID, VideoDetailsMenu videoDetailsMenu) {
                switch (itemID) {
                    case 1:
                        if(0==privateMedia.getState()){
                            ToastUtils.showCenterToast("无法设置封面,该图片正在审核中..");
                            return;
                        }
                        if(null!=mModifyPresenter&&!mModifyPresenter.isSetFront()){
                            mModifyPresenter.setImageFront(privateMedia,position);
                        }
                        break;
                    case 2:
                        if(mAdapter.getData().size()<=2){
                            ToastUtils.showCenterToast("默认头像不能被删除！");
                            return;
                        }
                        showProgressDialog("删除中，请稍后...",false);
                        if(null!=mModifyPresenter&&!mModifyPresenter.isDelete()){
                            mModifyPresenter.deleteHeadImage(privateMedia,position);
                        }
                        break;
                }
            }
        });
        commonMenuDialog.show();
    }

    @Override
    protected void setPosition(String name) {
        super.setPosition(name);
        if(null!=bindingView) bindingView.itemCity.setItemMoreTitle(name);
    }

    /**
     * 提交认证请求
     */
    private void submitUserData() {
        if(null!=mPresenter&&null!=bindingView){
            if(null==mAdapter||null==mVideoAdapter) return;
            String num = bindingView.inputNumber.getText().toString().trim();
            String name = bindingView.inputName.getText().toString().trim();
            String expire_date = bindingView.tvExpire.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                if(null!= mAnimation) bindingView.inputName.startAnimation(mAnimation);
                ToastUtils.showCenterToast("身份证姓名不能为空");
                return;
            }
            if (TextUtils.isEmpty(num)) {
                ToastUtils.showCenterToast("身份证号码不能为空");
                if(null!= mAnimation) bindingView.inputNumber.startAnimation(mAnimation);
                return;
            }

            if (TextUtils.isEmpty(expire_date)) {
                ToastUtils.showCenterToast("有效期不能为空");
                if(null!= mAnimation) bindingView.llTimeLimit.startAnimation(mAnimation);
                return;
            }

            if (TextUtils.isEmpty(bindingView.authentFront.getImageUrl())) {
                ToastUtils.showCenterToast("请上传身份证正面照");
                return;
            }

            if (TextUtils.isEmpty(bindingView.authentUnfront.getImageUrl())) {
                ToastUtils.showCenterToast("请上传身份证反面照");
                return;
            }
            if (TextUtils.isEmpty(UserManager.getInstance().getPhone())) {
                ToastUtils.showCenterToast("主播认证必须绑定手机号码");
                return;
            }
            
            if (mAdapter.getData().size()<2) {
                ToastUtils.showCenterToast("请上传至少2张照片");
                return;
            }

            if (mVideoAdapter.getData().size()<1) {
                ToastUtils.showCenterToast("请上传至少1部小视频");
                return;
            }

            if(TextUtils.isEmpty(UserManager.getInstance().getHeight())
                    ||TextUtils.isEmpty(UserManager.getInstance().getWeight())
                    ||TextUtils.isEmpty(UserManager.getInstance().getStar())
                    ||TextUtils.isEmpty(UserManager.getInstance().getSpeciality())
                    ||TextUtils.isEmpty(UserManager.getInstance().getLabel())
                    ||TextUtils.isEmpty(UserManager.getInstance().getSignature())){
                ToastUtils.showCenterToast("请填写所有必填项");
                return;
            }
            mPresenter.identityAuthentication(name, num, expire_date, bindingView.authentFront.getImageUrl(), bindingView.authentUnfront.getImageUrl());
        }
    }

    /**
     * 选取照片
     * @param flag  0：正面照  1：反面照
     */
    private void checkedImage(final int flag) {
        PhotoSelectedUtil.getInstance()
                .attachActivity(UserAuthenticationActivity.this)
                .setCatScaleWidth(16)
                .setCatScaleHeight(11)
                .setCropMode(0)//配置裁剪模式，1为新模式
                .setOnSelectedPhotoOutListener(new PhotoSelectedUtil.OnSelectedPhotoOutListener() {
            @Override
            public void onOutFile(File file) {
                if(null!=mPresenter) mPresenter.upload(file, flag);
            }

            @Override
            public void onError(int code, String errorMsg) {

            }
        }).start();
    }


    @Override
    public void initData() {

    }

    private void showDatePick() {
        //时间选择器
        pvTime = new TimePickerBuilder(UserAuthenticationActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                identityDate = sdf.format(date);
                bindingView.tvExpire.setText(identityDate);
            }
        }).setCancelColor(ContextCompat.getColor(this, R.color.black))
                .setSubmitColor(ContextCompat.getColor(this, R.color.app_style))
                .build();
        pvTime.show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mAnimation) mAnimation.cancel();
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mModifyPresenter) mModifyPresenter.detachView();
        PhotoSelectedUtil.getInstance().onDestroy();
        VideoSelectedUtil.getInstance().onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //照片
        if(requestCode==Constant.SELECT_SINGER_IMAGE_REQUST&&resultCode==Constant.SELECT_SINGER_IMAGE_RESULT){
            if(null!=data.getStringExtra("selected_image")){
                UploadFileToOSSManager.get(UserAuthenticationActivity.this).addUploadListener(new OnUploadObjectListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onProgress(long progress) {

                    }

                    @Override
                    public void onSuccess(UploadObjectInfo data, String msg) {
                        ToastUtils.showCenterToast(msg);
                        VideoApplication.getInstance().setMineRefresh(true);
                        if(null!=mModifyPresenter) mModifyPresenter.getUserHeads();
                    }

                    @Override
                    public void onFail(int code, String errorMsg) {
                        ToastUtils.showCenterToast(errorMsg);
                    }
                }).createAsyncUploadTask(data.getStringExtra("selected_image"));
            }
            //视频
        }else if(requestCode==Constant.SELECT_VIDEO_REQUST&&resultCode==Constant.SELECT_VIDEO_RESULT){
            if(null!=data.getStringExtra("selected_video")){
                UploadFileToOSSManager.get(UserAuthenticationActivity.this).addUploadListener(new OnUploadObjectListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onProgress(long progress) {

                    }

                    @Override
                    public void onSuccess(UploadObjectInfo data, String msg) {
                        ToastUtils.showCenterToast(msg);
                        VideoApplication.getInstance().setMineRefresh(true);
                        if(null!=mModifyPresenter) mModifyPresenter.getUserVideos();
                    }

                    @Override
                    public void onFail(int code, String errorMsg) {
                        ToastUtils.showCenterToast(errorMsg);
                    }
                }).createAsyncUploadTask(data.getStringExtra("selected_video"));
            }
        }
        PhotoSelectedUtil.getInstance().onActivityResult(requestCode, resultCode, data);
        VideoSelectedUtil.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PhotoSelectedUtil.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
        VideoSelectedUtil.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 照片上传结果
     * @param data
     * @param flag
     */
    @Override
    public void showUploadResult(String data, int flag) {
        if(null!=bindingView){
            //正面照
            if (flag == 0) {
                bindingView.authentFront.setImageData(data);
            //反面照
            } else {
                bindingView.authentUnfront.setImageData(data);
            }
        }
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    /**
     * 用户头像拉取成功
     * @param list
     */
    @Override
    public void showHeadList(List<PrivateMedia> list) {
        if(null!=mDataLoadView) mDataLoadView.stopLoading();
        if(null!=bindingView) bindingView.tvImageTips.setVisibility(View.VISIBLE);
        if(null==list) return;
        if(null!=mAdapter){
            if(list.size()<UserManager.getInstance().getUploadImageCount()){
                PrivateMedia privateMedia=new PrivateMedia();
                privateMedia.setFile_type(0);
                privateMedia.setItemCategory(Constant.ITEM_ACTION_ADD);
                privateMedia.setIcon(R.drawable.ic_user_head_add);
                list.add(privateMedia);
            }
            mAdapter.setNewData(list);
        }
    }

    /**
     * 获取头像失败
     * @param code
     * @param msg
     */
    @Override
    public void showHeadError(int code, String msg) {
        if(0==code){
            if(null!=bindingView) bindingView.tvImageTips.setVisibility(View.VISIBLE);
            if(null!=mDataLoadView) mDataLoadView.stopLoading();
            List<PrivateMedia> privateMedias=new ArrayList<>();
            PrivateMedia privateMedia=new PrivateMedia();
            privateMedia.setFile_type(0);
            privateMedia.setItemCategory(Constant.ITEM_ACTION_ADD);
            privateMedia.setIcon(R.drawable.ic_user_head_add);
            privateMedias.add(privateMedia);
            mAdapter.setNewData(privateMedias);
        }else{
            if(null!=mDataLoadView) mDataLoadView.showErrorLayout(msg);
        }
    }

    @Override
    public void showVideoList(List<PrivateMedia> list) {
        if(null!=bindingView) bindingView.tvVideoTips.setVisibility(View.VISIBLE);
        if(null==list) return;
        if(null!=mVideoAdapter){
            if(list.size()<UserManager.getInstance().getUploadVideoCount()){
                PrivateMedia privateMedia=new PrivateMedia();
                privateMedia.setFile_type(0);
                privateMedia.setItemCategory(Constant.ITEM_ACTION_ADD);
                privateMedia.setIcon(R.drawable.ic_user_head_add);
                list.add(privateMedia);
            }
            mVideoAdapter.setNewData(list);
        }
    }

    @Override
    public void showVideoError(int code, String errorMsg) {
        if(0==code){
            if(null!=bindingView) bindingView.tvVideoTips.setVisibility(View.VISIBLE);
            if(null!=mVideoAdapter){
                List<PrivateMedia> privateMedias=new ArrayList<>();
                PrivateMedia privateMedia=new PrivateMedia();
                privateMedia.setFile_type(0);
                privateMedia.setItemCategory(Constant.ITEM_ACTION_ADD);
                privateMedia.setIcon(R.drawable.ic_user_head_add);
                privateMedias.add(privateMedia);
                mVideoAdapter.setNewData(privateMedias);
            }
        }
    }

    /**
     * 设置头像回执
     * @param code
     * @param msg
     */
    @Override
    public void showSetImageFrontResult(int code, String msg) {
        ToastUtils.showCenterToast(msg);
        if(NetContants.API_RESULT_CODE==code){
            VideoApplication.getInstance().setMineRefresh(true);
            if(null!=mModifyPresenter) mModifyPresenter.getUserHeads();
        }
    }

    @Override
    public void showSetUserHeadResult(int code, String msg) {

    }

    /**
     * 删除头像回执
     * @param media
     * @param position
     * @param code
     * @param msg
     */
    @Override
    public void showDeleteHeadImageResult(PrivateMedia media, int position, int code, String msg) {
        closeProgressDialog();
        ToastUtils.showCenterToast(msg);
        VideoApplication.getInstance().setMineRefresh(true);
        if(1==code&&null!=mAdapter){
            try {
                mAdapter.remove(position);
                if(null!=bindingView) bindingView.recyclerView.requestLayout();//重绘界面，避免留白
                List<PrivateMedia> data = mAdapter.getData();
                //对方小于8个且不存在上传按钮时，允许用户上传更多照片
                if(data.size()< UserManager.getInstance().getUploadImageCount()){
                    int count=0;
                    if(null!=data&&data.size()>0){
                        for (int i = 0; i < data.size(); i++) {
                            if(IndexVideoListAdapter.ITEM_TYPE_ADD==data.get(i).getItemType()){
                                count++;
                                break;
                            }
                        }
                    }
                    if(count<=0){
                        PrivateMedia privateMedia=new PrivateMedia();
                        privateMedia.setFile_type(0);
                        privateMedia.setItemCategory(Constant.ITEM_ACTION_ADD);
                        privateMedia.setIcon(R.drawable.ic_user_head_add);
                        mAdapter.addData(privateMedia);
                    }
                }
            }catch (RuntimeException e){

            }
        }
    }
}