package com.yc.liaolive.media.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityLocationVideoEditBinding;
import com.yc.liaolive.media.ui.fragment.VideoAsmrEditFragment;
import com.yc.liaolive.media.ui.fragment.VideoEditFragment;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.CommentTitleView;

/**
 * TinyHung@Outlook.com
 * 2018/9/26
 * 视频上传描述
 */

public class MediaLocationVideoEditActivity extends BaseActivity<ActivityLocationVideoEditBinding> {

    private static final String TAG = "MediaLocationVideoEditActivity";

    /**
     * 视频预览编辑入口
     * @param context
     * @param videoPath
     * @param typeDescribe 场景描述，是普通视频还是ASMR Constant.KEY_SELECTED_SMAR_VIDEO 描述
     */
    public static void start(Context context,String videoPath,String typeDescribe) {
        Intent intent=new Intent(context,MediaLocationVideoEditActivity.class);
        intent.putExtra("videoPath",videoPath);
        intent.putExtra(Constant.KEY_SELECTED_KEY,typeDescribe);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String videoPath = getIntent().getStringExtra("videoPath");
        if(TextUtils.isEmpty(videoPath)){
            ToastUtils.showCenterToast("参数错误！");
            finish();
            return;
        }
        setContentView(R.layout.activity_location_video_edit);
        String stringExtra = getIntent().getStringExtra(Constant.KEY_SELECTED_KEY);
        Fragment fragment=null;
        if(!TextUtils.isEmpty(stringExtra)&&stringExtra.equals(Constant.KEY_SELECTED_SMAR_VIDEO)){
            bindingView.titltView.setTitle("ASMR视频描述");
            fragment= VideoAsmrEditFragment.getInstance(videoPath);
        }else{
            bindingView.titltView.setTitle("上传视频");
            fragment=VideoEditFragment.getInstance(videoPath);
        }
        android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, fragment, "FRAGMENT");
        fragmentTransaction.addToBackStack("EDIT");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void initViews() {
        bindingView.titltView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}