package com.yc.liaolive.media.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityMediaVideoListBinding;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.ui.fragment.ImportVideoFolderFragment;
import com.yc.liaolive.ui.fragment.ImportVideoSelectorFragment;
import com.yc.liaolive.ui.fragment.MediaVideoLocationFragment;
import com.yc.liaolive.util.ImageCache;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.util.VideoSelectedUtil;
import com.yc.liaolive.view.widget.CommentTitleView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2017/8/19
 * 本地视频列表，文件夹目录和文件夹二级目录
 */

public class MediaLocationVideoListActivity extends BaseActivity<ActivityMediaVideoListBinding> implements Observer {

    private static final String TAG = "MediaLocationVideoListActivity";
    private MediaVideoLocationFragment mMediaVideoLocationFragment;

    public static void start(Context context) {
        context.startActivity(new Intent(context,MediaLocationVideoListActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_video_list);
        mMediaVideoLocationFragment = new MediaVideoLocationFragment();
        addReplaceFragment(mMediaVideoLocationFragment,"视频相册");
        //第一次使用弹出使用提示
        if(1!= SharedPreferencesUtil.getInstance().getInt(Constant.TIPS_SCANVIDEO_CODE)){
            bindingView.tvTipsMessage.setVisibility(View.VISIBLE);
            bindingView.tvTipsMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bindingView.tvTipsMessage.setVisibility(View.GONE);
                }
            });
            SharedPreferencesUtil.getInstance().putInt(Constant.TIPS_SCANVIDEO_CODE,1);
        }
        ApplicationManager.getInstance().addObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查SD读写权限
        RxPermissions.getInstance(MediaLocationVideoListActivity.this).request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(null!=aBoolean&&aBoolean){

                }else{
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MediaLocationVideoListActivity.this)
                            .setTitle("SD读取权限申请失败")
                            .setMessage("部分权限被拒绝，将无法上传视频,是否现在去设置？");
                    builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SystemUtils.getInstance().startAppDetailsInfoActivity(MediaLocationVideoListActivity.this,141);
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    @Override
    public void initViews() {
        bindingView.titltView.setTitle("文件夹");
        bindingView.titltView.showMoreTitle(true);
        bindingView.titltView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                //逐个关闭Fragment
                onBackPressed();
            }

            @Override
            public void onMoreTitleClick(View v) {
                super.onMoreTitleClick(v);
                bindingView.titltView.showMoreTitle(false);
                if (null != bindingView.tvTipsMessage && bindingView.tvTipsMessage.getVisibility() == View.VISIBLE) {
                    bindingView.tvTipsMessage.setVisibility(View.GONE);
                }
                addReplaceFragment(new ImportVideoFolderFragment(), "文件夹");
            }
        });
    }

    /**
     * 调用系统相机录制视频
     */
    public void startRecordVideo() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //选择视频文件并上传
        VideoSelectedUtil.getInstance()
                .attachActivity(MediaLocationVideoListActivity.this)
                .setOutPutFileName(timeStamp+".mp4")
                .setOnSelectedPhotoOutListener(new VideoSelectedUtil.OnSelectedPhotoOutListener() {
                    @Override
                    public void onOutFile(String filePath) {
                        Logger.d(TAG,"startRecordVideo-->onOutFile:"+filePath);
                        //去编辑封面和描述信息
                        MediaLocationVideoPriviewActivity.start(MediaLocationVideoListActivity.this,filePath,null,getIntent().getStringExtra(Constant.KEY_SELECTED_KEY));
                    }

                    @Override
                    public void onError(int code, String errorMsg) {
                    }
                }).startRecord();
    }

    /**
     * 添加界面
     * @param fragment
     */
    public void addReplaceFragment(Fragment fragment,String title) {
        bindingView.titltView.setTitle(title);
        android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, fragment, title);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 打开公用的相册缩略图列表界面
     * @param path
     * @param name
     */
    public void addFolderFragment(String path, String name) {
        bindingView.titltView.setTitle(name);
        android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, ImportVideoSelectorFragment.newInstance(path,name), name);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }



    @Override
    public void onBackPressed() {
        //只剩下到首页一个界面了
        if(getSupportFragmentManager().getBackStackEntryCount()==1&&!MediaLocationVideoListActivity.this.isFinishing()){
            finish();
            return;
        }
        //文件夹列表
        if(getSupportFragmentManager().getBackStackEntryCount()==2&&!MediaLocationVideoListActivity.this.isFinishing()){
            bindingView.titltView.setTitle("视频相册");
            bindingView.titltView.showMoreTitle(true);
        }
        //文件夹二级视频缩略图列表
        if(getSupportFragmentManager().getBackStackEntryCount()==3&&!MediaLocationVideoListActivity.this.isFinishing()){
            bindingView.titltView.setTitle("文件夹");
            bindingView.titltView.showMoreTitle(false);
        }
        super.onBackPressed();
    }

    /**
     * 完成选择
     * @param videoPath
     * @param view
     */
    public void onResultFinlish(String videoPath, View view) {
        MediaLocationVideoPriviewActivity.start(MediaLocationVideoListActivity.this,videoPath,null,getIntent().getStringExtra(Constant.KEY_SELECTED_KEY));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VideoSelectedUtil.getInstance().onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        VideoSelectedUtil.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void onDestroy() {
        VideoSelectedUtil.getInstance().onDestroy();
        ImageCache.getInstance().recyler();
        ApplicationManager.getInstance().removeObserver(this);
        super.onDestroy();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg&&arg instanceof String && TextUtils.equals(Constant.OBSERVER_CLOSE_LOCATION_VIDEO_ACTIVITY, (String) arg)){
            finish();
        }
    }
}
