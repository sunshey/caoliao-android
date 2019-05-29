package com.yc.liaolive.media.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.yc.liaolive.R;
import com.yc.liaolive.media.adapter.VerticalPagerAdapter;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityVerticalImagePreviewBinding;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.videocall.manager.MakeCallManager;
import com.yc.liaolive.ui.contract.VideoPlayerContract;
import com.yc.liaolive.media.ui.pager.VerticalImagePlayerPager;
import com.yc.liaolive.ui.presenter.VideoPlayerPresenter;
import com.yc.liaolive.util.CommonUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.VideoDataUtils;
import com.yc.liaolive.media.view.VerticalViewPager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/9/27
 * 此Activity只维护用户列表加载更多
 * 垂直方向切换用户、水平方向切换图片预览框架
 */

public class VerticalImagePreviewActivity extends BaseActivity<ActivityVerticalImagePreviewBinding> implements VideoPlayerContract.View, Observer {

    private static final String TAG = "VerticalImagePreviewActivity";
    private static final int CHANGE_ODE_START = 1;//开始播放
    private static final int CHANGE_ODE_STOP = 4;//不可见
    private Map<Integer,VerticalImagePlayerPager> mFragments =new HashMap<>();//存放片段的集合
    private int mScrollOffsetY;//当前滚动的Y轴偏移量
    private int mPosition;//要进入的房间位置、正在观看直播的房间位置
    private int mVideoListSize=0;//视频的总大小
    private VideoPlayerPresenter mPresenter;
    private String mUsreid;//宿主ID
    private int mIndex;//主页属于第几页
    private int mMediatype;
    private String mSource;//主页绑定的类型
    private String mHostUrl;//api
    private int mPage;//加载到了第几页
    private VerticalFragmentPagerAdapter mVerticalPagerAdapter;
    private Handler mHandler;

    public Handler getHandler(){
        if(null==mHandler){
            mHandler=new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /**
     * 批量预览入口 适合从个人中心或者私信跳转而来,无需回显数据
     * @param context
     * @param usreid 宿主身份ID，用户中心、私信等(用户宿主)界面跳转必须调用此方法
     * @param view 新特性转场动画sharedElement
     */
    public static void start(Activity context, String usreid, View view) {
        Intent intent=new Intent(context,VerticalImagePreviewActivity.class);
        intent.putExtra("usreid",usreid);
        if(null!=view){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, CommonUtils.getString(R.string.transition_movie_img));//与xml文件对应
            ActivityCompat.startActivity(context,intent, options.toBundle());
        }else{
            context.startActivity(intent);
        }
    }

    /**
     * 批量预览入口,适合从主页跳转进来，需要回显用户数据
     * @param context
     * @param hostUrl API
     * @param index 源界面所属的角标位置
     * @param position 当前预览的位置 界面回显用到
     * @param mediaType 文件类型 0：照片 1：视频
     * @param source 0：时间排序 1：浏览排序 2：喜欢排序 3：加密多媒体文件 4：推荐的多媒体文件
     * @param page 加载到了第几页
     * @param view 新特性转场动画sharedElement
     */
    public static void start(Activity context, String hostUrl,int index,int position,int mediaType,String source,int page,View view) {
        Intent intent=new Intent(context,VerticalImagePreviewActivity.class);
        intent.putExtra("hostUrl",hostUrl);
        intent.putExtra("index",index);
        intent.putExtra("mediaType",mediaType);
        intent.putExtra("source",source);
        intent.putExtra("page",page);
        intent.putExtra("position",position);
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
        if(null== VideoDataUtils.getInstance().getVideoData() || VideoDataUtils.getInstance().getVideoData().size()<=0){
            ToastUtils.showCenterToast("播放失败");
            finish();
            return;
        }
        setContentView(R.layout.activity_vertical_image_preview);
        ApplicationManager.getInstance().addObserver(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);//禁止用户截屏
        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", 0);
        mHostUrl = intent.getStringExtra("hostUrl");
        mIndex = intent.getIntExtra("index", 0);
        mUsreid = intent.getStringExtra("usreid");
        //默认视频类型文件
        mMediatype = intent.getIntExtra("mediaType", 0);
        //加载到了第几页
        mPage = intent.getIntExtra("page", 1);
        //默认推荐
        mSource = intent.getStringExtra("source");
        mVideoListSize=VideoDataUtils.getInstance().getVideoData().size();
        mPresenter = new VideoPlayerPresenter();
        mPresenter.attachView(this);

        //用户适配器 初始化
        mVerticalPagerAdapter = new VerticalFragmentPagerAdapter();
        bindingView.verticalViewPager.setOnPageChangeListener(onPageChangeListener);
        bindingView.verticalViewPager.setOffscreenPageLimit(1);
        bindingView.verticalViewPager.setAdapter(mVerticalPagerAdapter);
        bindingView.verticalViewPager.setCurrentItem(mPosition);
        //非用户中心模式下下载更多用户列表
        if(!TextUtils.isEmpty(mHostUrl)&&mVideoListSize==1&&TextUtils.isEmpty(mUsreid)){
            //立即加载分页数据
            if(null!=mPresenter&&!mPresenter.isLoading()){
                mPage++;
                mPresenter.getMedias(mHostUrl,mUsreid, mMediatype,mPage,mSource,0);
            }
        }
        waitPlayVideo(350,mPosition);
    }

    @Override
    public void initViews() {
    }

    @Override
    public void initData() {

    }

    /**
     * 垂直列表适配器
     */
    private class VerticalFragmentPagerAdapter extends VerticalPagerAdapter {

        @Override
        public int getCount() {
            return null==VideoDataUtils.getInstance().getVideoData()?0:VideoDataUtils.getInstance().getVideoData().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PrivateMedia indexVideoInfo = VideoDataUtils.getInstance().getVideoData().get(position);
            if(null!=indexVideoInfo){
                VerticalImagePlayerPager videoPlayerPager = new VerticalImagePlayerPager(VerticalImagePreviewActivity.this,indexVideoInfo,position);
                View view = videoPlayerPager.getView();
                view.setId(position);
                if(null!= mFragments) mFragments.put(position, videoPlayerPager);
                container.addView(view);
                return view;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(null!=container){
                container.removeView(container.findViewById(position));
                if(null!= mFragments) mFragments.remove(position);
            }
        }
    }


    /**
     * 监听预览的位置
     */
    private VerticalViewPager.OnPageChangeListener onPageChangeListener=new VerticalViewPager.OnPageChangeListener() {
        /**
         *
         * @param position Position index of the first page currently being displayed. Page 起始位置索引
         *            position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset Value from [0, 1) indicating the offset from the page at position. 正在移动的偏移量 [0, 1]
         * @param positionOffsetPixels Value in pixels indicating the offset from position. 起始位置像素偏移量，这里是Y轴  ++ 上滑  --下滑
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(positionOffsetPixels>mScrollOffsetY){
                setConntrollerAlpha(position,(1.0f-positionOffset));
                setConntrollerAlpha(position+1,positionOffset);
            }else if(positionOffsetPixels<mScrollOffsetY){
                setConntrollerAlpha(position+1,positionOffset);
                setConntrollerAlpha(position,(1.0f-positionOffset));
            }
            mScrollOffsetY=positionOffsetPixels;
        }

        @Override
        public void onPageSelected(int position) {
            onLifeChange(mPosition,CHANGE_ODE_STOP);
            //准备初始化新的用户作品
            if(mPosition!=position){
                onLifeChange(position, CHANGE_ODE_START);
            }
            mPosition=position;
            //到达最后一个了且用户ID为空
            if(!TextUtils.isEmpty(mHostUrl)&&TextUtils.isEmpty(mUsreid)&&position>=(mVideoListSize-1)){
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mPage++;
                    mPresenter.getMedias(mHostUrl,mUsreid, mMediatype,mPage,mSource,0);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 接收自杀事件
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg && arg instanceof String && TextUtils.equals(Constant.OBSERVER_FINLISH_MEDIA_PLAYER, (String) arg)){
            finish();
        }
    }

    /**
     * 设置延缓任务
     * @param misTime 需要延缓多久
     * @param waitPoistion 延缓播放视频的目标Poistion
     */
    private void waitPlayVideo(long misTime,int waitPoistion) {
        getHandler().removeMessages(0);
        getHandler().postAtTime(new PlayVideoRunnable(waitPoistion), SystemClock.uptimeMillis()+misTime);//设置延缓任务
    }


    /**
     * 这个Runnable用来执行延缓任务，waitPlayPoistin是记录要执行的延缓任务，只有当前显示的viewPager cureenItem与当时提交的cureenItem相等才允许播放
     * 防止用户手速过快
     */
    private class PlayVideoRunnable implements Runnable{
        private final int waitPlayPoistin;

        public PlayVideoRunnable(int waitPoistion){
            this.waitPlayPoistin=waitPoistion;
        }

        @Override
        public void run() {
            if(this.waitPlayPoistin!=mPosition){
                return;
            }
            onLifeChange(mPosition, CHANGE_ODE_START);
        }
    }



    /**
     * 生命周期调度
     * @param position
     * @param CHANGE_MODE
     */
    private void onLifeChange(int position,int CHANGE_MODE){
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, VerticalImagePlayerPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalImagePlayerPager> next = iterator.next();
                if(position==next.getKey()){
                    VerticalImagePlayerPager viewPager = next.getValue();
                    if(null!=viewPager){
                        if(CHANGE_ODE_START ==CHANGE_MODE){
                            viewPager.onStart();
                            return;
                        }else if(CHANGE_ODE_STOP==CHANGE_MODE){
                            viewPager.onStop();
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean getLifeOnBack(int position){
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, VerticalImagePlayerPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalImagePlayerPager> next = iterator.next();
                if(position==next.getKey()){
                    VerticalImagePlayerPager viewPager = next.getValue();
                    if(null!=viewPager){
                        return viewPager.getLifeOnBack();
                    }
                }
            }
        }
        return false;
    }


    /**
     * 控制器的透明度渐变
     * @param position 要渐变直播间索引
     * @param alpha 渐变值
     */
    private void setConntrollerAlpha(int position, float alpha) {
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, VerticalImagePlayerPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalImagePlayerPager> next = iterator.next();
                if(position==next.getKey()){
                    VerticalImagePlayerPager viewPager = next.getValue();
                    if(null!=viewPager){
                        viewPager.setConntrollerAlpha(alpha);
                    }
                }
            }
        }
    }

    //==========================================数据交互=============================================

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showMedias(List<PrivateMedia> data) {
        if(null!=mVerticalPagerAdapter){
            VideoDataUtils.getInstance().addData(data);
            mVideoListSize=VideoDataUtils.getInstance().getVideoData().size();
            mVerticalPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showMediaEmpty() {}

    @Override
    public void showMediaError(int code, String errorMsg) {
        if(mPage>0) mPage--;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //回显状态
        VideoDataUtils.getInstance().setPosition(mPosition);
        VideoDataUtils.getInstance().setIndex(mIndex);
        VideoDataUtils.getInstance().setSource(mSource);
        VideoDataUtils.getInstance().setPage(mPage);
        VideoDataUtils.getInstance().setHostUrl(mHostUrl);
        VideoDataUtils.getInstance().setFileType(mMediatype);
    }

    @Override
    public void onBackPressed() {
        if(getLifeOnBack(mPosition)) return;
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if(null!=mHandler) mHandler.removeMessages(0); mHandler=null;
        ApplicationManager.getInstance().removeObserver(this);
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=bindingView) bindingView.verticalViewPager.removeAllViews();
        super.onDestroy();
        MakeCallManager.getInstance().onDestroy();
    }
}
