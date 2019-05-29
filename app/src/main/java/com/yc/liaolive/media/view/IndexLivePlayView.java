package com.yc.liaolive.media.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.interfaces.PerfectClickListener;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.media.listener.VideoPlayerFunctionListener;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.media.manager.LiveVideoPlayerManager;

/**
 * TinyHung@Outlook.com
 * 2018/8/19
 * 抢聊组件
 */

public class IndexLivePlayView  extends RelativeLayout {

    private static final String TAG = "IndexLivePlayView";
    private LiveVideoPlayerManager mPlayerManager;
    private View mBtnRefresh;
    private TextView mViewUserName;
    private TextView mViewUserPrice;
    private ImageView mVideoCover;
    private TextView mViewUserLocation;
    private VideoPlayerFunctionListener mFunctionListener;//宿主
    private ImageView mLocationIcon;

    public IndexLivePlayView(Context context) {
        super(context);
        init(context,null);
    }

    public IndexLivePlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_index_live_play_layout,this);
        //播放组件
        mBtnRefresh = findViewById(R.id.view_btn_refresh);
        mVideoCover = (ImageView)findViewById(R.id.view_video_cover);
        mLocationIcon = (ImageView)findViewById(R.id.view_user_location_icon);
        //主播信息
        mViewUserName = (TextView)findViewById(R.id.view_user_name);
        mViewUserPrice = (TextView)findViewById(R.id.view_user_price);
        mViewUserLocation = (TextView)findViewById(R.id.view_user_location);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndexLivePlayView);
            Drawable closeDrawable = typedArray.getDrawable(R.styleable.IndexLivePlayView_indexLiveCloseSrc);
            Drawable loadingDrawable = typedArray.getDrawable(R.styleable.IndexLivePlayView_indexLiveLoadingSrc);
            if(null!=closeDrawable) mBtnRefresh.setBackground(closeDrawable);
            typedArray.recycle();
        }
        mBtnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IndexLivePlayView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        //切换视频源
                        stopPlay();
                        startPlay(null,false);
                    }
                });
            }
        });
        this.setOnClickListener(new PerfectClickListener(1000) {
            @Override
            protected void onNoDoubleClick(View v) {
                if(null==IndexLivePlayView.this.getTag()) return;
                if(null!=mOnSelectedListener) mOnSelectedListener.onMackCallClick((RoomList) IndexLivePlayView.this.getTag());
            }
        });
        mPlayerManager = (LiveVideoPlayerManager) findViewById(R.id.video_view);
        mPlayerManager.setLooping(true);
    }

    /**
     * 设置封面
     * @param roomList
     */
    private void initCover(RoomList roomList) {
        if(null==roomList) return;
        initCover(roomList.getFrontcover());
        if(null!=mViewUserName) mViewUserName.setText(roomList.getNickname());
        if(null!=mViewUserPrice) mViewUserPrice.setText(roomList.getChat_deplete()+"钻/"+roomList.getChat_minite()+"分钟");
        if(null!=mViewUserLocation) mViewUserLocation.setText(roomList.getCity());
        if(null!=mLocationIcon) mLocationIcon.setImageResource(R.drawable.ic_index_location);
    }

    /**
     * 设置封面
     * @param frontCover
     */
    private void initCover(String frontCover) {
        if(null==frontCover||null==mVideoCover) return;
        Glide.with(getContext())
                .load(frontCover)
                .error(R.drawable.ic_default_item_cover)
                .placeholder(R.drawable.ic_default_item_cover)
                .crossFade()//渐变
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .into(mVideoCover);
    }

    /**
     * 初始化状态中
     */
    public void initLoading() {
        showLoadingView();
    }
    /**
     * 加载中
     */
    private void showLoadingView(){
        if(null!= mBtnRefresh) mBtnRefresh.setVisibility(GONE);
        if(null!=mPlayerManager) mPlayerManager.startLoadingView();
    }

    /**
     * 隐藏加载中
     */
    private void stopLoadingView(){
        if(null!=mPlayerManager) mPlayerManager.stopLoadingView();
        if(null!= mBtnRefresh) mBtnRefresh.setVisibility(VISIBLE);
    }

    /**
     * 外界调用的开始
     * 必须在setAttach()之后调用
     */
    public void start(boolean isRelease){
        startPlay(null,isRelease);
    }
    /**
     * 开始播放
     * @param roomList
     * @param isRelease 是否复位
     */
    private void startPlay(RoomList roomList,boolean isRelease) {
        if(isRelease) stopPlay();
        if(null==roomList){
            //向管理者索取视频资源
            if(null!=mFunctionListener){
                roomList = mFunctionListener.getPlayInfo();
            }
        }
        if(null==roomList||null==mPlayerManager) return;
        initCover(roomList);
        IndexLivePlayView.this.setTag(roomList);
        if(null!=mPlayerManager&&!TextUtils.isEmpty(roomList.getPlayUrl())){
            mPlayerManager.startPlay(roomList.getPlayUrl());
        }
    }

    /**
     * 结束播放
     */
    protected void stopPlay() {
        if(null!=mPlayerManager) mPlayerManager.onStop();
        IndexLivePlayView.this.setTag(null);
        stopLoadingView();
        if(null!=mViewUserName)  mViewUserName.setText("");
        if(null!=mViewUserPrice) mViewUserPrice.setText("");
        if(null!=mViewUserLocation) mViewUserLocation.setText("");
        if(null!=mLocationIcon) mLocationIcon.setImageResource(0);
        if(null!=mVideoCover) mVideoCover.setImageResource(0);//还原封面
    }


    /**
     * 给定宿主
     * @param functionListener
     */
    public void setAttach(VideoPlayerFunctionListener functionListener) {
        this.mFunctionListener=functionListener;
    }

    /**
     * 伪 onResume
     */
    public void onResume(){
        if(null!=mPlayerManager)  mPlayerManager.onResume();
    }

    /**
     * 伪 onPause
     */
    public void onPause(){
        if(null!=mPlayerManager)  mPlayerManager.onPause();
    }

    /**
     * 伪 onStop
     */
    public void onStop(){
        stopPlay();
    }

    /**
     * 伪 onDestroy
     */
    public void onDestroy(){
        stopPlay();
        if(null!=mPlayerManager)  mPlayerManager.onDestroy();
        IndexLivePlayView.this.setTag(null);
        mFunctionListener=null;
    }

    public interface OnSelectedListener{
        void onMackCallClick(RoomList data);
    }

    private OnSelectedListener mOnSelectedListener;

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        mOnSelectedListener = onSelectedListener;
    }
}
