package com.yc.liaolive.media.ui.pager;

import android.app.Activity;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BasePager;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.ShareInfo;
import com.yc.liaolive.bean.UnReadMsg;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.PagerVerticalImagePlayerBinding;
import com.yc.liaolive.index.adapter.IndexVideoListAdapter;
import com.yc.liaolive.interfaces.ImagePreviewHelp;
import com.yc.liaolive.interfaces.ShareFinlishListener;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.ui.activity.VerticalImagePreviewActivity;
import com.yc.liaolive.media.ui.activity.VerticalVideoPlayerAvtivity;
import com.yc.liaolive.media.view.PinchImageViewPager;
import com.yc.liaolive.media.view.PlayerAdLayout;
import com.yc.liaolive.media.view.VideoPlayerControllerLayout;
import com.yc.liaolive.ui.contract.VideoActionContract;
import com.yc.liaolive.ui.presenter.VideoActionPresenter;
import com.yc.liaolive.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/10/9
 * 照片预览 垂直列表ITEM ,此片段维护一个横向滑动切换照片、加载更多数据的 的Pager列表
 */

public class VerticalImagePlayerPager extends BasePager<PagerVerticalImagePlayerBinding> implements VideoActionContract.View, Observer, ImagePreviewHelp {

    private static final int CHANGE_ODE_STATR = 1;//开始播放
    private static final int CHANGE_ODE_STOP = 4;//不可见
    private static final int CHANGE_ODE_BACK = 5;//返回
    private Map<Integer,HorizontaImagePreviewPager> mFragments =new HashMap<>();//存放片段的集合
    private final PrivateMedia mVideoInfo;//私密多媒体文件
    private VideoPlayerControllerLayout mControllerLayout;//交互控制器
    private final VideoActionPresenter mPresenter;
    private final List<PrivateMedia> mData;//源数据,只属于用户下面的
    private ImagePagerAdapter mImagePagerAdapter;
    private int mVideoListSize;//当前列表总大小
    private int mPage;//当前浏览的位置
    private int mChildPosition=0;//当前预览的照片所在的位置
    private int mScrollOffsetY;//当前横向滚动的X轴偏移量

    /**
     *  @param context
     * @param videoInfo 这里通常表示用户的属性，默认是取此数据封装到横向的集合中
     * @param position 当前片段在父容器中所在的位置
     */
    public VerticalImagePlayerPager(Activity context, PrivateMedia videoInfo, int position) {
        super(context);
        this.mVideoInfo = videoInfo;
        this.mCurrentPosition=position;
//        this.mChildPosition=childPosition; 暂时不需要定位至用户点击的Poistion，因为点击的那张图片自动移动至第一张显示
        mData = new ArrayList<>();
        mData.add(videoInfo);
        mPresenter = new VideoActionPresenter();
        mPresenter.attachView(this);
        setContentView(R.layout.pager_vertical_image_player);
        ApplicationManager.getInstance().addObserver(this);
    }

    /**
     * UI组件初始化
     */
    @Override
    public void initViews() {
        //控制器
        mControllerLayout = new VideoPlayerControllerLayout(getContext());
        mControllerLayout.setVisibility(View.GONE);
        mControllerLayout.setMediaType(Constant.MEDIA_TYPE_IMAGE);
        mControllerLayout.setControllerFunctionListener(new VideoPlayerControllerLayout.OnControllerFunctionListener() {

            //点赞
            @Override
            public void onLike(PrivateMedia privateMedia) {
                if(null!=bindingView) bindingView.heartLayout.startPriceAnimation();
                if(null!= privateMedia){
                    if(null!=mPresenter&&!mPresenter.isLoveing()) mPresenter.videoLoveShare(privateMedia,0);
                }
            }

            //分享
            @Override
            public void onShare(final PrivateMedia privateMedia) {
                if(null!= privateMedia &&null!=getContext()&&getContext() instanceof VerticalImagePreviewActivity){
                    if(1== privateMedia.getIs_private()){
                        ToastUtils.showCenterToast("私密视频无法分享");
                        return;
                    }
                    VerticalVideoPlayerAvtivity activity= (VerticalVideoPlayerAvtivity) getContext();
                    ShareInfo shareInfo=new ShareInfo();
                    shareInfo.setTitle(TextUtils.isEmpty(privateMedia.getVideo_desp())?privateMedia.getNickname():privateMedia.getVideo_desp());
                    shareInfo.setVideoID(String.valueOf(privateMedia.getId()));
                    shareInfo.setRoomid("0");
                    shareInfo.setDesp(privateMedia.getNickname()+"的视频");
                    shareInfo.setUserID(privateMedia.getUserid());
                    shareInfo.setImageLogo(privateMedia.getAvatar());
                    shareInfo.setReport(true);
                    shareInfo.setUrl("http://cl.dapai52.com/share/share.html");
                    shareInfo.setShareTitle("分享视频到");
                    activity.share(shareInfo, new ShareFinlishListener() {
                        //分享成功的VideoID,平台ID
                        @Override
                        public void shareSuccess(String id,int platformID) {
                            if(null!=privateMedia&&null!=mPresenter&&!mPresenter.isLoveing()) mPresenter.videoLoveShare(privateMedia,1);
                        }
                    });
                }
            }

            //关闭
            @Override
            public void onBack() {
                if(null!=getContext()){
                    getContext().onBackPressed();
                }
            }

            //去付费购买多媒体文件
            @Override
            public void buyMediaFile(PrivateMedia privateMedia) {
                onStart();
            }
        });
        bindingView.videoController.addView(mControllerLayout);

        mControllerLayout.setVideoData(mVideoInfo);//更新界面元素
        mControllerLayout.initState();//初始化
        checkedReadMsg();//检查未读消息
        mVideoListSize=mData.size();

        bindingView.heartLayout.setImageVisibility();//默认点赞是隐藏的
        //用户适配器 初始化
        mImagePagerAdapter = new ImagePagerAdapter();
        bindingView.viewImagePager.setOnPageChangeListener(onPageChangeListener);
        bindingView.viewImagePager.setOffscreenPageLimit(1);
        bindingView.viewImagePager.setAdapter(mImagePagerAdapter);
        bindingView.viewImagePager.setCurrentItem(mChildPosition);//定位至当前预览所在的位置
        if(null!=mControllerLayout) mControllerLayout.setNumText(1+"/"+mVideoListSize);
        new android.os.Handler(Looper.getMainLooper()).postAtTime(new Runnable() {
            @Override
            public void run() {
                if(mVideoListSize==1){
                    //立即加载分页数据
                    if(null!=mVideoInfo&&null!=mPresenter&&!mPresenter.isLoading()){
                        mPage++;
                        mPresenter.getMedias(NetContants.getInstance().URL_FILE_LIST(),mVideoInfo.getUserid(), 0,mPage,0,mVideoInfo.getId());
                    }
                }
            }
        }, SystemClock.uptimeMillis()+300);

    }

    @Override
    public void initData() {
        if(null==mVideoInfo||null==bindingView) return;
        if(mVideoInfo.getItemCategory().equals(Constant.INDEX_ITEM_TYPE_BANNERS)
                ||mVideoInfo.getItemCategory().equals(Constant.INDEX_ITEM_TYPE_BANNER)){
            bindingView.adViewLayout.setVisibility(View.VISIBLE);
            bindingView.adViewLayout.setOnAdClickListener(new PlayerAdLayout.OnAdClickListener() {
                @Override
                public void onBack(View view) {
                    if(null!=getContext()){
                        getContext().onBackPressed();
                    }
                }
            });
            bindingView.adViewLayout.init(mVideoInfo);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(null!=mVideoInfo&&mVideoInfo.getItemType()==IndexVideoListAdapter.ITEM_TYPE_IMAGE){
            if(null!=mControllerLayout) mControllerLayout.setVisibility(View.VISIBLE);
            onLifeChange(mChildPosition, CHANGE_ODE_STATR);
            if(null!=mControllerLayout) mControllerLayout.showTopNumTextView(true);
            //预览广告
        }else{
            showAdView();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //此片段属于不可见状态
        onLifeChange(mChildPosition,CHANGE_ODE_STOP);
        if(null!=mControllerLayout){
            mControllerLayout.showTopNumTextView(false);
            mControllerLayout.resetControllerTabBar();
            mControllerLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 临时性的广告View
     */
    public void showAdView() {
        if(bindingView.adViewLayout.getVisibility()!=View.VISIBLE){
            bindingView.adViewLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 生命周期调度
     * @param position
     * @param CHANGE_MODE
     */
    private void onLifeChange(int position,int CHANGE_MODE){
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, HorizontaImagePreviewPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, HorizontaImagePreviewPager> next = iterator.next();
                if(position==next.getKey()){
                    HorizontaImagePreviewPager viewPager = next.getValue();
                    if(null!=viewPager){
                        if(CHANGE_ODE_STATR ==CHANGE_MODE){
                            viewPager.onStart();
                            return;
                        }else if(CHANGE_ODE_STOP==CHANGE_MODE){
                            viewPager.onStop();
                            return;
                        }else if(CHANGE_ODE_BACK==CHANGE_MODE){
                            viewPager.onBackPressed();
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * 滑动监听
     */
    private PinchImageViewPager.OnPageChangeListener onPageChangeListener=new PinchImageViewPager.OnPageChangeListener() {
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
            if(null!=mControllerLayout) mControllerLayout.setNumText((position+1)+"/"+mVideoListSize);
            onLifeChange(mChildPosition,CHANGE_ODE_STOP);
            onLifeChange(position, CHANGE_ODE_STATR);
            mChildPosition =position;
            //到达最后一个了,加载用户名下其他照片
            if(position>=(mVideoListSize-1)){
                if(null!=mVideoInfo&&null!=mPresenter&&!mPresenter.isLoading()){
                    mPage++;
                    mPresenter.getMedias(NetContants.getInstance().URL_FILE_LIST(),mVideoInfo.getUserid(), 0,mPage,0,mVideoInfo.getId());
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    /**
     * 返回当前控制器是否可见状态
     * 响应 Activity的onBackPressed()事件
     * @return
     */
    public boolean getLifeOnBack() {
        if(null!=mControllerLayout&&mControllerLayout.isBack()){
            return true;
        }
        if(null!=mControllerLayout) mControllerLayout.setVisibility(View.GONE);
        return false;
    }

    /**
     * 浏览当前用户下的照片数据发生了变化
     * @param newMediaInfo
     * @param groupPosition 父容器所在的位置
     */
    @Override
    public void newMediaInfo(PrivateMedia newMediaInfo, int groupPosition) {
        if(null!=mControllerLayout&&mCurrentPosition==groupPosition) {
            mControllerLayout.setVideoData(newMediaInfo);//刷新基本信息
            mControllerLayout.updateRoomOffline();//刷新在线状态
        }
    }

    /**
     * 双击产生，禁用所有滑动事件，开启沉浸缩放看图模式
     * @param groupPosition
     */
    @Override
    public void onDoubleClick(int groupPosition) {
        if(null!=mControllerLayout) mControllerLayout.hideTabView();
    }


    /**
     * 显示、隐藏控制器
     */
    private void changedControllerVisibility() {
        if(null==mControllerLayout) return;
        mControllerLayout.changedTabBarVisibility();
    }

    /**
     * 图片横向滑动分页适配器
     */
    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return null== mData ? 0 : mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * View创建
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PrivateMedia indexVideoInfo = mData.get(position);
            if(null!=indexVideoInfo){
                HorizontaImagePreviewPager imagePreviewPager = new HorizontaImagePreviewPager(getContext(),VerticalImagePlayerPager.this,indexVideoInfo,mCurrentPosition,position);
                imagePreviewPager.setOnFunctionListener(new HorizontaImagePreviewPager.OnFunctionListener() {
                    @Override
                    public void onClick() {
                        changedControllerVisibility();
                    }
                });
                View view = imagePreviewPager.getView();
                view.setId(position);
                if(null!= mFragments) mFragments.put(position, imagePreviewPager);
                container.addView(view);
                return view;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(null!=container) container.removeView(container.findViewById(position));
            if(null!= mFragments) mFragments.remove(position);
        }
    }


    /**
     * 控制器的透明度渐变
     * @param position 要渐变直播间索引
     * @param alpha 渐变值
     */
    private void setConntrollerAlpha(int position, float alpha) {
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, HorizontaImagePreviewPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, HorizontaImagePreviewPager> next = iterator.next();
                if(position==next.getKey()){
                    HorizontaImagePreviewPager viewPager = next.getValue();
                    if(null!=viewPager){
                        viewPager.setConntrollerAlpha(alpha);
                    }
                }
            }
        }
    }

    /**
     * 控制器的透明度
     * @param alpha
     */
    public void setConntrollerAlpha(float alpha) {
        //广告
        if(null!=bindingView) bindingView.adViewLayout.setAlpha(alpha);
        //控制器
        if(null!=mControllerLayout) mControllerLayout.setTabAlpha(alpha);
        //封面
        if(null!=bindingView) bindingView.viewImagePager.setAlpha(alpha);
    }


    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg && arg instanceof String){
            if(TextUtils.equals(Constant.OBSERVER_LIVE_MESSAGE_CHANGED, (String) arg)){
                checkedReadMsg();
                //关注
            }else if(TextUtils.equals(Constant.OBSERVER_CMD_FOLLOW_TRUE, (String) arg)){
                if(null!=mVideoInfo&&null!=mControllerLayout){
                    mVideoInfo.setAttent(1);
                    mControllerLayout.updataFollowState();
                }
                //取关
            }else if(TextUtils.equals(Constant.OBSERVER_CMD_FOLLOW_FALSE, (String) arg)){
                if(null!=mVideoInfo&&null!=mControllerLayout){
                    mVideoInfo.setAttent(0);
                    mControllerLayout.updataFollowState();
                }
            }

        }
    }

    /**
     * 检查未读消息
     */
    private synchronized void checkedReadMsg() {
        if(null==mControllerLayout||null==mVideoInfo) return;
        if(VideoApplication.getInstance().getUnReadMsgMap().containsKey(mVideoInfo.getUserid())){
            Iterator<Map.Entry<String, UnReadMsg>> iterator = VideoApplication.getInstance().getUnReadMsgMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, UnReadMsg> next = iterator.next();
                if(next.getKey().equals(mVideoInfo.getUserid())){
                    if(next.getValue().count>0){
                        mControllerLayout.setMsgIcon(R.drawable.ic_video_private_chat_new);
                    }else{
                        mControllerLayout.setMsgIcon(R.drawable.ic_video_private_chat);
                    }
                    break;
                }
            }
        }else{
            mControllerLayout.setMsgIcon(R.drawable.ic_video_private_chat);
        }
    }

    /**
     * 触发了返回时间
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 完全销毁阶段
     */
    @Override
    public void onDestroy() {
        if(null!=mPresenter) mPresenter.detachView();
        ApplicationManager.getInstance().removeObserver(this);
        super.onDestroy();
    }

    //==========================================网络交互回调=========================================

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showActionResul(PrivateMedia privateMedia, int actionType) {
        if(1==actionType){
            ToastUtils.showCenterToast("已分享");
        }
        if(null!=mControllerLayout) mControllerLayout.setVideoData(privateMedia);
    }

    @Override
    public void showActionError(int code, String errorMsg) {

    }

    @Override
    public void showMedias(List<PrivateMedia> data) {
        if(null!=mImagePagerAdapter&&null!=mData){
            mData.addAll(data);
            mVideoListSize=mData.size();
            if(null!=mControllerLayout) mControllerLayout.setNumText((mChildPosition+1)+"/"+mVideoListSize);
            mImagePagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showMediaEmpty() {

    }

    @Override
    public void showMediaError(int code, String errorMsg) {
        if(mPage>0) mPage--;
    }
}
