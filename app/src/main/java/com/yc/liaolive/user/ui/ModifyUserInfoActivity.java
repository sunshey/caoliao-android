package com.yc.liaolive.user.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.PlatfromAccountInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.UploadObjectInfo;
import com.yc.liaolive.bean.VideoDetailsMenu;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.common.ControllerConstant;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.ActivityModifyUserInfoBinding;
import com.yc.liaolive.index.adapter.IndexVideoListAdapter;
import com.yc.liaolive.interfaces.OnUploadObjectListener;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.PlatformAccountBindHelp;
import com.yc.liaolive.manager.UploadFileToOSSManager;
import com.yc.liaolive.media.adapter.UserHeadAdapter;
import com.yc.liaolive.media.ui.activity.VerticalImagePreviewActivity;
import com.yc.liaolive.model.RecyclerViewSpacesItem;
import com.yc.liaolive.ui.activity.ContentFragmentActivity;
import com.yc.liaolive.ui.dialog.CommonMenuDialog;
import com.yc.liaolive.ui.dialog.DataPickerDialog;
import com.yc.liaolive.user.IView.ModifyInfoContract;
import com.yc.liaolive.user.IView.UserServerContract;
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
import com.yc.liaolive.view.layout.DataLoadView;
import com.yc.liaolive.view.widget.CommentTitleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/10/12
 * 用户资料修改 主播增加 身高、体重、星座、形象标签 等设置
 */
public class ModifyUserInfoActivity extends BaseActivity<ActivityModifyUserInfoBinding> implements Observer, ModifyInfoContract.View {

    private static final String TAG = "ModifyUserInfoActivity";
    private ModifyInfoPresenter mPresenter;
    private UserHeadAdapter mAdapter;
    private DataLoadView mDataLoadView;
    private boolean isGoBindWX = false; //处理微信应用双开

    public static void start(Context context) {
        Intent intent=new Intent(context,ModifyUserInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGoBindWX) {
            this.closeProgressDialog();
        }
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
            bindingView.itemId.setItemMoreTitle(UserManager.getInstance().getUserId());
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
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_info);
        ApplicationManager.getInstance().addObserver(this);
        mPresenter = new ModifyInfoPresenter();
        mPresenter.attachView(this);
        mPresenter.getUserHeads();
        queryBindState();
    }

    @Override
    public void initViews() {
        //如果是主播，显示全功能版本
        if(UserManager.getInstance().isAuthenState()){
            bindingView.itemUserHeight.setVisibility(View.VISIBLE);
            bindingView.itemUserWeight.setVisibility(View.VISIBLE);
            bindingView.itemUserStart.setVisibility(View.VISIBLE);
            bindingView.itemUserDesp.setVisibility(View.VISIBLE);
            bindingView.itemUserTag.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(UserManager.getInstance().getPhone())) {
            bindingView.itemBindPhone.setTag(3);
            bindingView.itemBindPhone.setItemMoreTitle(Utils.submitPhone(UserManager.getInstance().getPhone(),3,7));
        }
        bindingView.tvImageTips.setText(Html.fromHtml("<font color='#E6646E'>*</font>至少上传两张照片，建议上传5-8张照片"));

        //标题栏事件
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                finish();
            }
        });

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.item_id:
                        Utils.copyString(ModifyUserInfoActivity.this,UserManager.getInstance().getUserId());
                        ToastUtils.showCenterToast("ID已复制到粘贴板");
                        break;
                    //昵称
                    case R.id.item_nickname:
                        if(2==UserManager.getInstance().getIdentity_audit()){
                            ToastUtils.showCenterToast("主播不允许修改昵称");
                            return;
                        }
                        if(1==UserManager.getInstance().getIdentity_audit()){
                            ToastUtils.showCenterToast("主播身份审核中，不支持修改昵称");
                            return;
                        }
                        ModifyDataInfoActivity.start(ModifyUserInfoActivity.this,bindingView.itemNickname.getTitleTextContent(),bindingView.itemNickname.getMoreTextContent(),12,null,Constant.MODITUTY_KEY_NICKNAME);
                        break;
                    //个性签名
                    case R.id.item_user_singtrue:
                        ModifyDataInfoActivity.start(ModifyUserInfoActivity.this,bindingView.itemUserSingtrue.getTitleTextContent(),bindingView.itemUserSingtrue.getMoreTextContent(),30,null,Constant.MODITUTY_KEY_SIGNTURE);
                        break;
                    //性别
                    case R.id.item_sex:
                        if(2==UserManager.getInstance().getIdentity_audit()){
                            ToastUtils.showCenterToast("主播不允许修改性别");
                            return;
                        }
                        if(1==UserManager.getInstance().getIdentity_audit()){
                            ToastUtils.showCenterToast("主播身份审核中，不支持修改性别");
                            return;
                        }
                        SelectSexDialog selectSexDialog = new SelectSexDialog(ModifyUserInfoActivity.this);
                        selectSexDialog.show();
                        selectSexDialog.setOnSelectSexListener(new SelectSexDialog.onSelectSexListener() {
                            @Override
                            public void onSelectSext(int sexId) {
                                updataUserInfo(Constant.MODITUTY_KEY_SEX,String.valueOf(sexId));
                            }
                        });
                        break;
                    //主播认证
                    case R.id.item_authentication:
                        if (UserManager.getInstance().getIdentity_audit() == 0) {
                            startActivity(new Intent(ModifyUserInfoActivity.this, UserAuthenticationActivity.class));
                            return;
                        }
                        if (UserManager.getInstance().getIdentity_audit() == 1) {
                            ToastUtils.showCenterToast("认证信息正在审核中");
                            return;
                        }
                        ToastUtils.showCenterToast("已通过主播认证");
                        break;
                    //所在城市
                    case R.id.item_city:
                        showCityList();
                        break;
                    //身高
                    case R.id.item_user_height:
                        DataPickerDialog.getInstance(ModifyUserInfoActivity.this,"cm",UserManager.getInstance().getHeight()).setOnSelectedListener(new DataPickerDialog.OnSelectedListener() {
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
                        DataPickerDialog.getInstance(ModifyUserInfoActivity.this,"kg",UserManager.getInstance().getWeight()).setOnSelectedListener(new DataPickerDialog.OnSelectedListener() {
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
                        DataPickerDialog.getInstance(ModifyUserInfoActivity.this,"start",UserManager.getInstance().getStar()).setOnSelectedListener(new DataPickerDialog.OnSelectedListener() {
                            @Override
                            public void onItemSelected(int id, String content) {
                                if(TextUtils.equals(content,UserManager.getInstance().getStar())){
                                    ToastUtils.showCenterToast("请选择与现有星座不一致的项");
                                    return;
                                }
                                bindingView.itemUserStart.setItemMoreTitle(content);
                                updataUserInfo(Constant.MODITUTY_KEY_STAR,content);
                            }
                        }).show();
                        break;
                    //个人介绍
                    case R.id.item_user_desp:
                        ModifyDataInfoActivity.start(ModifyUserInfoActivity.this,bindingView.itemUserDesp.getTitleTextContent(),bindingView.itemUserDesp.getMoreTextContent(),30,null,Constant.MODITUTY_KEY_SPECIALITY);
                        break;
                    //形象标签
                    case R.id.item_user_tag:
                        ContentFragmentActivity.start(ModifyUserInfoActivity.this,Constant.FRAGMENT_TYPE_USER_TAG,"选择标签",null,null);
                        break;
                    //绑定手机号
                    case R.id.item_bind_phone:
                        bindAccount(bindingView.itemBindPhone,Constant.LOGIN_TYPE_PHONE);
                        break;
                    //绑定微信
                    case R.id.item_bind_wx:
                        bindAccount(bindingView.itemBindWx,Constant.LOGIN_TYPE_WEXIN);
                        break;
                    //绑定QQ
                    case R.id.item_bind_qq:
                        bindAccount(bindingView.itemBindQq,Constant.LOGIN_TYPE_QQ);
                        break;
                }
            }
        };

        bindingView.itemBindPhone.setOnClickListener(onClickListener);
        bindingView.itemBindWx.setOnClickListener(onClickListener);
        bindingView.itemBindQq.setOnClickListener(onClickListener);
        bindingView.itemNickname.setOnClickListener(onClickListener);
        bindingView.itemSex.setOnClickListener(onClickListener);
        bindingView.itemUserSingtrue.setOnClickListener(onClickListener);
        bindingView.itemAuthentication.setOnClickListener(onClickListener);
        bindingView.itemCity.setOnClickListener(onClickListener);
        bindingView.itemId.setOnClickListener(onClickListener);

        bindingView.itemUserHeight.setOnClickListener(onClickListener);
        bindingView.itemUserWeight.setOnClickListener(onClickListener);
        bindingView.itemUserStart.setOnClickListener(onClickListener);
        bindingView.itemUserDesp.setOnClickListener(onClickListener);
        bindingView.itemUserTag.setOnClickListener(onClickListener);

        //用户相册中头像处理
        mAdapter = new UserHeadAdapter(null);
        mDataLoadView = new DataLoadView(ModifyUserInfoActivity.this);
        mDataLoadView.setLoadHeight(mAdapter.getItemHeight()+ScreenUtils.dpToPxInt(4f));
        mDataLoadView.setOnRefreshListener(new DataLoadView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mDataLoadView.showLoadingView();
                    mPresenter.getUserHeads();
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
                    //条目点击事件..这里前往预览单张图片
//                    MediaImageSingerPreviewActivity.start(ModifyUserInfoActivity.this,privateMedia.getImg_path(),"保存头像",view);
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
                            VerticalImagePreviewActivity.start(ModifyUserInfoActivity.this,UserManager.getInstance().getUserId(),view);
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

        bindingView.recyclerView.setLayoutManager(new GridLayoutManager(ModifyUserInfoActivity.this,4,GridLayoutManager.VERTICAL,false));
        bindingView.recyclerView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(3f)));
        bindingView.recyclerView.setHasFixedSize(true);
        bindingView.recyclerView.setNestedScrollingEnabled(false);//不允许单独行动
        bindingView.recyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void updateUserSex() {
        super.updateUserSex();
        bindingView.itemSex.setItemMoreTitle(UserManager.getInstance().getSex() == 0 ? getString(R.string.sex_man) : getString(R.string.sex_wumen));
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
        if(0!=privateMedia.getState()){
            if (UserManager.getInstance().getIdentity_audit() == 0) {
                VideoDetailsMenu videoDetailsMenu=new VideoDetailsMenu();
                videoDetailsMenu.setItemID(0);
                videoDetailsMenu.setTextColor("#FF555555");
                videoDetailsMenu.setItemName("设为头像");
                list.add(videoDetailsMenu);
            }

            VideoDetailsMenu videoDetailsMenu1=new VideoDetailsMenu();
            videoDetailsMenu1.setItemID(1);
            videoDetailsMenu1.setTextColor("#FF555555");
            videoDetailsMenu1.setItemName("设为封面");
            list.add(videoDetailsMenu1);
        }
        if(position>1){
            VideoDetailsMenu videoDetailsMenu2=new VideoDetailsMenu();
            videoDetailsMenu2.setItemID(2);
            videoDetailsMenu2.setTextColor("#FFFF7575");
            videoDetailsMenu2.setItemName("删除");
            list.add(videoDetailsMenu2);
        }
        CommonMenuDialog commonMenuDialog =new CommonMenuDialog(ModifyUserInfoActivity.this);
        commonMenuDialog.setData(list);
        commonMenuDialog.setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int itemID, VideoDetailsMenu videoDetailsMenu) {
                switch (itemID) {
                    //设为头像
                    case 0:
                        if(null!=mPresenter&&!mPresenter.isSetFront()){
                            mPresenter.setUserHead(privateMedia,position);
                        }
                        break;
                    //设为封面
                    case 1:
                        if(null!=mPresenter&&!mPresenter.isSetFront()){
                            mPresenter.setImageFront(privateMedia,position);
                        }
                        break;
                    //删除
                    case 2:
                        if(mAdapter.getData().size()<=2){
                            ToastUtils.showCenterToast("默认头像不能被删除！");
                            return;
                        }
                        showProgressDialog("删除中，请稍后...",false);
                        if(null!=mPresenter&&!mPresenter.isDelete()){
                            mPresenter.deleteHeadImage(privateMedia,position);
                        }
                        break;
                }
            }
        });
        commonMenuDialog.show();
    }

    @Override
    public void initData() {

    }

    /**
     * 选择照片
     */
    private void selectedMediaImage() {
        //自定义相册选取
//        Intent intent=new Intent(ModifyUserInfoActivity.this,MediaLocationImageListSingerActivity.class);
//        startActivityForResult(intent,Constant.SELECT_SINGER_IMAGE_REQUST);
        requstPermissions();
    }

    @Override
    protected void onRequstPermissionResult(int resultCode) {
        super.onRequstPermissionResult(resultCode);
        if(PREMISSION_SUCCESS==resultCode){
            PhotoSelectedUtil.getInstance()
                    .attachActivity(ModifyUserInfoActivity.this)
                    .setCatScaleWidth(1)
                    .setCatScaleHeight(1)
                    .setCropMode(0)
                    .setOnSelectedPhotoOutListener(new PhotoSelectedUtil.OnSelectedPhotoOutListener() {
                        @Override
                        public void onOutFile(File file) {
                            UploadFileToOSSManager.get(ModifyUserInfoActivity.this).addUploadListener(new OnUploadObjectListener() {
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
                                    if(null!=mPresenter) mPresenter.getUserHeads();
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

    @Override
    protected void setPosition(String name) {
        super.setPosition(name);
        if(null!=bindingView) bindingView.itemCity.setItemMoreTitle(name);
    }

    /**
     * 查询账号的绑定状态
     */
    private void queryBindState() {
        UserManager.getInstance().queryBindState(new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null!=object){
                    List<PlatfromAccountInfo> platfromAccountInfos= (List<PlatfromAccountInfo>) object;
                    if(!ModifyUserInfoActivity.this.isFinishing()){
                        for (PlatfromAccountInfo platfromAccountInfo : platfromAccountInfos) {
                            changeBindState(platfromAccountInfo.getPlatform(),platfromAccountInfo.getIsset());
                        }
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {

            }
        });
    }

    /**
     * 改变文字状态
     * @param platform
     * @param isset
     */
    private void changeBindState(int platform, int isset) {
        if(null!=bindingView){
            switch (platform) {
                case Constant.LOGIN_TYPE_QQ:
                    bindingView.itemBindQq.setItemMoreTitle(1==isset?getResources().getString(R.string.binded):getResources().getString(R.string.unbinded));
                    bindingView.itemBindQq.setTag(1==isset?3:0);//已绑定状态
                    break;
                case Constant.LOGIN_TYPE_WEXIN:
                    bindingView.itemBindWx.setItemMoreTitle(1==isset?getResources().getString(R.string.binded):getResources().getString(R.string.unbinded));
                    bindingView.itemBindWx.setTag(1==isset?3:0);//已绑定状态
                    break;
                case Constant.LOGIN_TYPE_PHONE:
                    bindingView.itemBindPhone.setItemMoreTitle(TextUtils.isEmpty(UserManager.getInstance().getPhone())?"未绑定":Utils.submitPhone(UserManager.getInstance().getPhone(),3,7));
                    bindingView.itemBindPhone.setTag(!TextUtils.isEmpty(UserManager.getInstance().getPhone())?3:0);//已绑定状态
                    break;
            }
        }
    }

    /**
     * 开始绑定账号
     * @param view
     * @param platfromID
     */
    private void bindAccount(View view, int platfromID) {
        if(null==view) return;
        if(null!=view.getTag()){
            int tag = (int) view.getTag();
            if(3==tag) {//已绑定
                String msg="手机号码已绑定";
                if(Constant.LOGIN_TYPE_QQ==platfromID){
                    msg="账号已绑定QQ";
                }else if(Constant.LOGIN_TYPE_WEXIN==platfromID){
                    msg="账号已绑定微信";
                }
                ToastUtils.showCenterToast(msg);
                return;
            }
            //绑定手机号
            if(Constant.LOGIN_TYPE_PHONE==platfromID){
                CaoliaoController.startActivity(ControllerConstant.BindPhoneTaskActivity);
                return;
            }
            //绑定其他平台账号
            bindPlatfromAccount(platfromID);
        }
    }


    /**
     * 绑定第三方账号
     * @param platfromID
     */
    private void bindPlatfromAccount(int platfromID) {
        isGoBindWX = Constant.LOGIN_TYPE_WEXIN == platfromID;
        PlatformAccountBindHelp.getInstance().attachActivity(ModifyUserInfoActivity.this).setOnBindChangedListener(new PlatformAccountBindHelp.OnBindChangedListener() {
            @Override
            public void onSuccess(int platfromID,String content) {
                changeBindState(platfromID,1);//先改变本地状态
                queryBindState();//查询绑定状态
            }

            @Override
            public void onFailure(int code, String errorMsg) {
            }
        }).onBindPlatformAccount(platfromID);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String success = (String) arg;
            if ("authentication".equals(success)) {
                if(null!=bindingView) bindingView.itemUserSingtrue.setItemMoreTitle(UserManager.getInstance().getAuthenticationState());
            }
        } else if (arg instanceof FansInfo) {
            FansInfo fansInfo = (FansInfo) arg;
            String nickName = fansInfo.getNickname();
            String signature = fansInfo.getSignature();
            String city = fansInfo.getPosition();

            if (TextUtils.isEmpty(nickName)) {
                nickName = UserManager.getInstance().getNickname();
            }
            if (TextUtils.isEmpty(signature)) {
                signature = UserManager.getInstance().getSignature();
            }
            if (TextUtils.isEmpty(city)) {
                city = UserManager.getInstance().getPosition();
            }
            if (TextUtils.isEmpty(signature)) signature = getString(R.string.no_input);

            if(null!=bindingView){
                bindingView.itemNickname.setItemMoreTitle(nickName);
                bindingView.itemUserSingtrue.setItemMoreTitle(signature);
                bindingView.itemSex.setItemMoreTitle(fansInfo.getSex() == 0 ? getString(R.string.sex_man) : getString(R.string.sex_wumen));
                bindingView.itemCity.setItemMoreTitle(city);
            }
            UserManager.getInstance().setSex(fansInfo.getSex());
            UserManager.getInstance().setNickName(nickName);
            UserManager.getInstance().setSignature(signature);
            UserManager.getInstance().setPosition(city);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoSelectedUtil.getInstance().onActivityResult(requestCode,resultCode,data);
        if(requestCode==Constant.SELECT_SINGER_IMAGE_REQUST&&resultCode==Constant.SELECT_SINGER_IMAGE_RESULT){
            if(null!=data.getStringExtra("selected_image")){
                UploadFileToOSSManager.get(ModifyUserInfoActivity.this).addUploadListener(new OnUploadObjectListener() {
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
                        if(null!=mPresenter) mPresenter.getUserHeads();
                    }

                    @Override
                    public void onFail(int code, String errorMsg) {
                        ToastUtils.showCenterToast(errorMsg);
                    }
                }).createAsyncUploadTask(data.getStringExtra("selected_image"));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PhotoSelectedUtil.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void onDestroy() {
        ApplicationManager.getInstance().removeObserver(this);
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mDataLoadView) mDataLoadView.onDestroy();
        super.onDestroy();
        if(null!=mAdapter) mAdapter.setNewData(null);
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
     * @param masg
     */
    @Override
    public void showHeadError(int code, String masg) {
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
            if(null!=mDataLoadView) mDataLoadView.showErrorLayout(masg);
        }
    }

    @Override
    public void showVideoList(List<PrivateMedia> list) {

    }

    @Override
    public void showVideoError(int code, String errorMsg) {

    }

    /**
     * 设置封面回执
     * @param code
     * @param msg
     */
    @Override
    public void showSetImageFrontResult(int code, String msg) {
        ToastUtils.showCenterToast(msg);
        if(NetContants.API_RESULT_CODE==code){
            VideoApplication.getInstance().setMineRefresh(true);
            if(null!=mPresenter) mPresenter.getUserHeads();
        }
    }

    /**
     * 设置头像回执
     * @param code
     * @param msg
     */
    @Override
    public void showSetUserHeadResult(int code, String msg) {
        ToastUtils.showCenterToast(msg);
        if(NetContants.API_RESULT_CODE==code){
            VideoApplication.getInstance().setMineRefresh(true);
            if(null!=mPresenter) mPresenter.getUserHeads();
        }
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
                if(data.size()<UserManager.getInstance().getUploadImageCount()){
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


    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }
}
