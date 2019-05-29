package com.yc.liaolive.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.kaikai.securityhttp.utils.LogUtil;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.databinding.ActivityFeedOpinionBinding;
import com.yc.liaolive.ui.contract.FeedBackContract;
import com.yc.liaolive.ui.presenter.FeedBackPresenter;
import com.yc.liaolive.util.GlideUtils;
import com.yc.liaolive.util.PhotoSelectedUtil;
import com.yc.liaolive.view.widget.CommentTitleView;

import java.io.File;

/**
 * Created by wanglin  on 2018/7/5 14:06.
 */
public class OpinionFeedActivity extends BaseActivity<ActivityFeedOpinionBinding> implements FeedBackContract.View {

    private File mFile;
    private FeedBackPresenter mPresenter;
    private Animation animation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_opinion);
        mPresenter = new FeedBackPresenter(this);
        mPresenter.attachView(this);
        animation = AnimationUtils.loadAnimation(this, R.anim.shake);
    }

    @Override
    public void initViews() {
        bindingView.commentTitleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                finish();
            }
        });
        bindingView.llAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoSelectedUtil.getInstance().attachActivity(OpinionFeedActivity.this).setOnSelectedPhotoOutListener(new PhotoSelectedUtil.OnSelectedPhotoOutListener() {
                    @Override
                    public void onOutFile(File file) {
                        if (file != null) {
                            LogUtil.msg("file: " + file.getName() + "---" + file.getPath());
                            mFile = file;
                            GlideUtils.loadImageView(OpinionFeedActivity.this, file.getPath(), bindingView.ivShow);
                        }
                    }

                    @Override
                    public void onError(int code, String errorMsg) {

                    }
                }).start();
            }
        });
        bindingView.tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = bindingView.etContent.getText().toString().trim();
                String concact = bindingView.etPhoneNumber.getText().toString().trim();
                mPresenter.feedBack(content, mFile, concact);

            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void showEmptyNum() {
        bindingView.etPhoneNumber.startAnimation(animation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoSelectedUtil.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PhotoSelectedUtil.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }
}
