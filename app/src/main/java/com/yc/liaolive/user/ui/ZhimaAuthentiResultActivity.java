package com.yc.liaolive.user.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.databinding.ActivityZhimaAuthentiResultBinding;
import com.yc.liaolive.user.bean.ZhimaParams;
import com.yc.liaolive.user.bean.ZhimaResult;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.contract.ZhimaContract;
import com.yc.liaolive.user.ui.presenter.ZhimaPresenter;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.CommentTitleView;

/**
 * TinyHung@Outlook.com
 * 2019/1/25
 * 芝麻认证结果校验
 * RESULT:huayan://huayanzhima:result?
 * biz_content=%7B%22biz_no%22%3A%22ZM201901263000000707000178225441%22%2C%22passed%22%3A%22true%22%7D&sign=D1rW45JjjByqdqJ5jSZ4FH2Q4EVSRw9PFBQXnVJfrfSAk7p3svwF56Mz84iPzPF0JbdZcd7qGDd7LiTlhnz1FB9FR7Xu%2BGH%2FJOo8WnBogS6R3l3Smws0UUQSBD8w5O2Ya9ORr3OILxvdeMYv%2FWWm0u4uB6EV9hTue%2FYZ9pWq35WYLUmxBRwGLzkj9QRFvBlISRSx40FIw4fUnUrVQtq4mfSLz0eY1RoCG%2BqHw%2FoPfUZsVD9PPMehoqvq%2FtIEQBbEx4clry8xNFGzNvyYL2BtwVBt5Qh58XucDD%2Fm0qTlAXbPBwV9Q7bzKvxev7uFFpQXXERf5MeayZZ8wHiZNpxqKg%3D%3D
 */

public class ZhimaAuthentiResultActivity extends BaseActivity<ActivityZhimaAuthentiResultBinding> implements ZhimaContract.View {

    private static final String TAG = "ZhimaAuthentiResultActivity";
    private ZhimaPresenter mPresenter;

    public static void start(Context context, String result) {
        Intent intent=new Intent(context,ZhimaAuthentiResultActivity.class);
        intent.putExtra("result",result);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhima_authenti_result);
        String result = getIntent().getStringExtra("result");
        if(TextUtils.isEmpty(result)){
            VideoApplication.getInstance().setZmAuthentResult(false);
            ToastUtils.showCenterToast("校验失败，无返回值");
            finish();
        }
        bindingView.btnNext.setText("查询中...");
        mPresenter = new ZhimaPresenter();
        mPresenter.attachView(this);
        mPresenter.checkedZhimaResult(result);
    }

    @Override
    public void initViews() {
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                finish();
            }
        });
        bindingView.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoApplication.getInstance().setZmAuthentResult(false);
                if(null!=v.getTag()){
                    VideoApplication.getInstance().setZmAuthentResult(true);
                }
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showZhimaParams(ZhimaParams data) {

    }

    @Override
    public void showZhimaParamsError(int code, String data) {
        closeProgressDialog();
        ToastUtils.showCenterToast(data);
    }

    @Override
    public void showCheckedZhimaResult(ZhimaResult data) {
        if(null!=data&&null!=data.getUserinfo()){
            VideoApplication.getInstance().setMineRefresh(true);
            UserManager.getInstance().setIsZhima(data.getUserinfo().getIs_zhima());
            if(1==data.getUserinfo().getIs_zhima()){
                if(null!=bindingView){
                    bindingView.btnNext.setText("确定");
                    bindingView.tvResult.setTextColor(Color.parseColor("#1296DB"));
                    bindingView.tvResult.setText("认证成功");
                    bindingView.btnNext.setTag("认证成功");
                    bindingView.tvSuccess.setVisibility(View.VISIBLE);
                }
            }else{
                if(null!=bindingView){
                    bindingView.btnNext.setText("确定");
                    bindingView.tvResult.setTextColor(Color.parseColor("#1296DB"));
                    bindingView.tvResult.setText("认证失败，请稍后重试");
                    bindingView.tvSuccess.setVisibility(View.INVISIBLE);
                    bindingView.btnNext.setTag("认证失败");
                }
            }
        }
    }

    @Override
    public void showCheckedZhimaError(int code, String data) {
        if(null!=bindingView){
            bindingView.btnNext.setText("确定");
            bindingView.tvResult.setTextColor(Color.parseColor("#FF6666"));
            bindingView.tvResult.setText("认证失败,请重试");
            bindingView.tvSuccess.setVisibility(View.INVISIBLE);
        }
        ToastUtils.showCenterToast(data);
    }
}