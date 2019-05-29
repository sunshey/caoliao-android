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

import com.music.player.lib.manager.MusicWindowManager;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityVerticalVideoPlayerBinding;
import com.yc.liaolive.index.model.bean.OneListBean;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.ConfigSet;
import com.yc.liaolive.media.adapter.VerticalPagerAdapter;
import com.yc.liaolive.media.manager.VideoAudioManager;
import com.yc.liaolive.media.ui.pager.VerticalAnchorVideoPlayerPager;
import com.yc.liaolive.media.view.VerticalViewPager;
import com.yc.liaolive.ui.contract.IndexListContract;
import com.yc.liaolive.ui.presenter.IndexListPresenter;
import com.yc.liaolive.util.CommonUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.manager.MakeCallManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2019/1/26
 * 在线主播列表
 */

public class VerticalAnchorPlayerAvtivity extends BaseActivity<ActivityVerticalVideoPlayerBinding> implements Observer, IndexListContract.View {

    private static final String TAG = "VerticalAnchorPlayerAvtivity";
    //生命周期
    private static final int CHANGE_ODE_CREATE = 0;//初始化
    private static final int CHANGE_ODE_START = 1;//开始
    private static final int CHANGE_ODE_RESUME = 2;//可见
    private static final int CHANGE_ODE_PAUSE = 3;//不可见
    private static final int CHANGE_ODE_STOP = 4;//停止
    private static final int CHANGE_ODE_BACK = 5;//返回
    private static final int CHANGE_ODE_DESTROY= 6;//销毁
    private VerticalFragmentPagerAdapter mVerticalPagerAdapter;
    private Handler mHandler;
    private Map<Integer,VerticalAnchorVideoPlayerPager> mFragments =new HashMap<>();//存放片段的集合
    private int mScrollOffsetY;//当前滚动的Y轴偏移量
    private int mVideoListSize=0;//视频的总大小
    private int mPosition;//要进入的房间位置、正在观看直播的房间位置
    private String mType;
    private int mPage;//加载到了第几页
    private String mLastUserID;
    private IndexListPresenter mPresenter;

    public Handler getHandler(){
        if(null==mHandler){
            mHandler=new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /**
     * 批量预览入口,适合主页进来
     * @param context
     * @param type 数据分类所属Type
     * @param position 当前预览的位置 界面回显用到
     * @param page 加载到了第几页
     * @param view 新特性转场动画sharedElement
     */
    public static void start(Activity context, String type,int position,int page,View view) {
        Intent intent=new Intent(context,VerticalAnchorPlayerAvtivity.class);
        intent.putExtra("type",type);
        intent.putExtra("position",position);
        intent.putExtra("page",page);
        if(null!=view){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, CommonUtils.getString(R.string.transition_movie_img));//与xml文件对应
            ActivityCompat.startActivity(context,intent, options.toBundle());
        }else{
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFullScreen(true);//全屏
        super.onCreate(savedInstanceState);
        if(null== LiveRoomManager.getInstance().getData() || LiveRoomManager.getInstance().getData().size()<=0){
            ToastUtils.showCenterToast("播放失败");
            finish();
            return;
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);//禁止用户截屏
        setContentView(R.layout.activity_vertical_video_player);
        ApplicationManager.getInstance().addObserver(this);
        MusicWindowManager.getInstance().onInvisible();
        VideoAudioManager.getInstance().getAudioManager(getApplicationContext()).requestAudioFocus();
        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", 0);
        mType = intent.getStringExtra("type");
        mPage = intent.getIntExtra("page", 0);
        List<RoomList> data = LiveRoomManager.getInstance().getData();
        mLastUserID = data.get(data.size()-1).getUserid();

        mPresenter = new IndexListPresenter();
        mPresenter.attachView(this);
        mVideoListSize= LiveRoomManager.getInstance().getData().size();
        //适配器初始化
        mVerticalPagerAdapter = new VerticalFragmentPagerAdapter();
        bindingView.verticalViewPager.setOnPageChangeListener(onPageChangeListener);
        bindingView.verticalViewPager.setOffscreenPageLimit(1);
        bindingView.verticalViewPager.setAdapter(mVerticalPagerAdapter);
        bindingView.verticalViewPager.setCurrentItem(mPosition);
        if(mVideoListSize==1){
            //立即加载分页数据
            if(null!=mPresenter&&!mPresenter.isLoading()){
                mPage++;
                mPresenter.getLiveLists(mLastUserID,mType,mPage);
            }
        }
        waitPlayVideo(350,mPosition);//500毫秒后立即开始播放视频
    }

    @Override
    public void initViews() {}

    @Override
    public void initData() {}

    /**
     * 监听预览的位置，在松手后500毫秒之后开始预览
     */
    private VerticalViewPager.OnPageChangeListener onPageChangeListener=new VerticalViewPager.OnPageChangeListener() {

        /**
         * @param position Position index of the first page currently being displayed. Page 起始位置索引 position+1 will be visible if positionOffset is nonzero.
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
            //回收上一个片段持有的播放器
            onLifeChange(mPosition,CHANGE_ODE_STOP);
            //创建新的延时播放任务
            if(mPosition!=position){
                waitPlayVideo(300,position);
            }
            mPosition=position;
            //到达最后一个了
            if(position>=(mVideoListSize-1)){
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mPage++;
                    List<RoomList> data = LiveRoomManager.getInstance().getData();
                    mLastUserID = data.get(data.size()-1).getUserid();
                    mPresenter.getLiveLists(mLastUserID,mType,mPage);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    /**
     * 控制器的透明度渐变
     * @param position 要渐变直播间索引
     * @param alpha 渐变值
     */
    private void setConntrollerAlpha(int position, float alpha) {
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, VerticalAnchorVideoPlayerPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalAnchorVideoPlayerPager> next = iterator.next();
                if(position==next.getKey()){
                    VerticalAnchorVideoPlayerPager viewPager = next.getValue();
                    if(null!=viewPager){
                        viewPager.setConntrollerAlpha(alpha);
                    }
                }
            }
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
            Iterator<Map.Entry<Integer, VerticalAnchorVideoPlayerPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalAnchorVideoPlayerPager> next = iterator.next();
                if(position==next.getKey()){
                    VerticalAnchorVideoPlayerPager viewPager = next.getValue();
                    if(null!=viewPager){
                        if(CHANGE_ODE_CREATE==CHANGE_MODE){
                            viewPager.onCreate();
                            return;
                        }else if(CHANGE_ODE_START ==CHANGE_MODE){
                            viewPager.onStart();
                            return;
                        }else if(CHANGE_ODE_RESUME==CHANGE_MODE){
                            viewPager.onResume();
                            return;
                        }else if(CHANGE_ODE_PAUSE==CHANGE_MODE){
                            viewPager.onPause();
                            return;
                        }else if(CHANGE_ODE_STOP==CHANGE_MODE){
                            viewPager.onStop();
                            return;
                        }else if(CHANGE_ODE_BACK==CHANGE_MODE){
                            viewPager.onBackPressed();
                            return;
                        }else if(CHANGE_ODE_DESTROY==CHANGE_MODE){
                            viewPager.onDestroy();
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * 垂直列表适配器
     */
    private class VerticalFragmentPagerAdapter extends VerticalPagerAdapter {

        @Override
        public int getCount() {
            return null==LiveRoomManager.getInstance().getData()?0:LiveRoomManager.getInstance().getData().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RoomList roomList = LiveRoomManager.getInstance().getData().get(position);
            if(null!=roomList){
                VerticalAnchorVideoPlayerPager videoPlayerPager = new VerticalAnchorVideoPlayerPager(VerticalAnchorPlayerAvtivity.this,roomList,position);
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

    @Override
    protected void onResume() {
        super.onResume();
        onLifeChange(mPosition,CHANGE_ODE_RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //回显状态
        LiveRoomManager.getInstance().setPosition(mPosition).setType(mType).setPage(mPage).setLastUserID(mLastUserID);
        onLifeChange(mPosition,CHANGE_ODE_PAUSE);
    }

    @Override
    public void onBackPressed() {
        onLifeChange(mPosition,CHANGE_ODE_BACK);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        VideoAudioManager.getInstance().releaseAudioFocus();
        if (ConfigSet.getInstance().isAudioOpenWindown()) {
            MusicWindowManager.getInstance().onVisible();
        }
        ApplicationManager.getInstance().removeObserver(this);
        if(null!=mPresenter) mPresenter.detachView();
        onLifeChange(mPosition,CHANGE_ODE_DESTROY);
        if(null!= mFragments) mFragments.clear(); mFragments =null;
        if(null!=mHandler) mHandler.removeMessages(0); mHandler=null;
        if(null!=bindingView) bindingView.verticalViewPager.removeAllViews();
        super.onDestroy();
        MakeCallManager.getInstance().onDestroy();
    }

    //==========================================数据交互回调=========================================

    @Override
    public void showLiveRooms(OneListBean data) {
        if(null!=mVerticalPagerAdapter&&null!=data.getList()){
            LiveRoomManager.getInstance().addData(data.getList());
            mVideoListSize=LiveRoomManager.getInstance().getData().size();
            mLastUserID=data.getList().get(data.getList().size()-1).getUserid();
            mVerticalPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showLiveRoomEmpty() {}

    @Override
    public void showLiveRoomError(int code, String errorMsg) {
        if(mPage>0) mPage--;
    }

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}
}