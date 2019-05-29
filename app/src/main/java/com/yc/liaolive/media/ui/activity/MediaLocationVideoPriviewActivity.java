package com.yc.liaolive.media.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityLocationVideoPriviewBinding;
import com.yc.liaolive.media.manager.LiveVideoPlayerManager;
import com.yc.liaolive.util.CommonUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.CommentTitleView;

/**
 * TinyHung@Outlook.com
 * 2018/9/26
 * 本地视频预览
 */

public class MediaLocationVideoPriviewActivity extends BaseActivity<ActivityLocationVideoPriviewBinding>{

    private String mVideoPath;
    private LiveVideoPlayerManager mPlayerManager;
    private String mTypeDescribe;

    /**
     *
     * @param context
     * @param videoPath
     * @param view
     * @param typeDescribe 场景描述，是普通视频还是ASMR Constant.KEY_SELECTED_SMAR_VIDEO 描述
     */
    public static void start(Activity context, String videoPath, View view,String typeDescribe) {
        Intent intent=new Intent(context,MediaLocationVideoPriviewActivity.class);
        intent.putExtra("videoPath",videoPath);
        intent.putExtra(Constant.KEY_SELECTED_KEY,typeDescribe);
        if(null!=view){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, CommonUtils.getString(R.string.transition_movie_img));//与xml文件对应
            ActivityCompat.startActivity(context,intent, options.toBundle());
        }else{
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoPath = getIntent().getStringExtra("videoPath");
        mTypeDescribe = getIntent().getStringExtra(Constant.KEY_SELECTED_KEY);
        if(TextUtils.isEmpty(mVideoPath)){
            ToastUtils.showCenterToast("参数错误！");
            finish();
            return;
        }
        setContentView(R.layout.activity_location_video_priview);
        mPlayerManager = (LiveVideoPlayerManager) findViewById(R.id.video_view);
        mPlayerManager.setVideoScalingMode(LiveVideoPlayerManager.VIDEO_SCALING_MODE_NOSCALE_TO_FIT);//原始尺寸预览
        mPlayerManager.setLooping(true);
        mPlayerManager.startPlay(mVideoPath,false);
        mPlayerManager.setAutoRotateEnabled(true);
    }

    @Override
    public void initViews() {
        bindingView.titltView.showMoreTitle(true);
        bindingView.titltView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                finish();
            }

            @Override
            public void onMoreTitleClick(View v) {
                super.onMoreTitleClick(v);
                MediaLocationVideoEditActivity.start(MediaLocationVideoPriviewActivity.this,mVideoPath,mTypeDescribe);
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null!=mPlayerManager) mPlayerManager.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null!= mPlayerManager) mPlayerManager.onStop(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mPlayerManager){
            mPlayerManager.onDestroy();
            mPlayerManager=null;
        }
    }
}
