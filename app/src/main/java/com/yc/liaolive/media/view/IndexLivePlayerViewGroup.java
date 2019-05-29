package com.yc.liaolive.media.view;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.ui.contract.IndexVideoLiveContract;
import com.yc.liaolive.ui.presenter.IndexVideoLivePresenter;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.media.listener.VideoPlayerFunctionListener;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/8/19
 * 首页的视频聊组件
 */

public class IndexLivePlayerViewGroup extends RelativeLayout implements VideoPlayerFunctionListener, IndexVideoLiveContract.View {

    private static final String TAG = "IndexLivePlayerViewGroup";
    private IndexLivePlayView mPlayerView1;
    private IndexLivePlayView mPlayerView2;
    private IndexLivePlayView mPlayerView3;
    private IndexVideoLivePresenter mPresenter;
    private List<RoomList> mRoomLists;
    private boolean isPause=false;//是否不可见
    private int currentIndex=0;//当前播放的位置
    private Handler mHandler;

    public IndexLivePlayerViewGroup(Context context) {
        super(context);
        init(context, null);
    }

    public IndexLivePlayerViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_index_live_play_group_layout,this);
        mPlayerView1 = (IndexLivePlayView) findViewById(R.id.index_player_view1);
        mPlayerView2 = (IndexLivePlayView) findViewById(R.id.index_player_view2);
        mPlayerView3 = (IndexLivePlayView) findViewById(R.id.index_player_view3);
        mPlayerView1.setOnSelectedListener(new IndexLivePlayView.OnSelectedListener() {
            @Override
            public void onMackCallClick(RoomList data) {
                if(null!=mOnSelectedListener) mOnSelectedListener.onMackCallClick(data);
            }
        });
        mPlayerView2.setOnSelectedListener(new IndexLivePlayView.OnSelectedListener() {
            @Override
            public void onMackCallClick(RoomList data) {
                if(null!=mOnSelectedListener) mOnSelectedListener.onMackCallClick(data);
            }
        });
        mPlayerView3.setOnSelectedListener(new IndexLivePlayView.OnSelectedListener() {
            @Override
            public void onMackCallClick(RoomList data) {
                if(null!=mOnSelectedListener) mOnSelectedListener.onMackCallClick(data);
            }
        });
        mPlayerView1.setAttach(this);
        mPlayerView2.setAttach(this);
        mPlayerView3.setAttach(this);
        mPresenter = new IndexVideoLivePresenter();
        mPresenter.attachView(this);
        mHandler = new Handler();
    }

    /**
     * 刷新
     */
    public void onRefresh(){
        if(null!=mPresenter&&!mPresenter.isLoading()){
            mPresenter.getPrivateVideos(true);
        }
    }

    /**
     * 伪 onResume
     */
    public void onResume(){
        isPause=false;
        if(null!=mHandler) mHandler.removeMessages(0);
        if(null!=mPlayerView1) mPlayerView1.onResume();
        if(null!=mPlayerView2) mPlayerView2.onResume();
        if(null!=mPlayerView3) mPlayerView3.onResume();
    }

    /**
     * 伪 onPause
     */
    public void onPause(){
        isPause=true;
        if(null!=mHandler) mHandler.removeMessages(0);
        if(null!=mPlayerView1) mPlayerView1.onPause();
        if(null!=mPlayerView2) mPlayerView2.onPause();
        if(null!=mPlayerView3) mPlayerView3.onPause();
    }

    /**
     * 伪 onStart
     */
    public void onStart(){
        isPause=false;
        if(null!=mPlayerView1) mPlayerView1.initLoading();
        if(null!=mPlayerView2) mPlayerView2.initLoading();
        if(null!=mPlayerView3) mPlayerView3.initLoading();
        if(null!=mRoomLists&&mRoomLists.size()>0){
            initPlayer(false);
        }else{
            mPresenter.getPrivateVideos(false);
        }
    }

    /**
     * 伪 onStop
     */
    public void onStop(){
        isPause=true;
        if(null!=mHandler) mHandler.removeMessages(0);
        currentIndex=0;
        if(null!=mPlayerView1) mPlayerView1.onStop();
        if(null!=mPlayerView2) mPlayerView2.onStop();
        if(null!=mPlayerView3) mPlayerView3.onStop();
    }


    /**
     * 伪 onDestroy
     */
    public void onDestroy(){
        if(null!=mHandler) mHandler.removeMessages(0); mHandler=null;
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mPlayerView1) mPlayerView1.onDestroy();
        if(null!=mPlayerView2) mPlayerView2.onDestroy();
        if(null!=mPlayerView3) mPlayerView3.onDestroy();
        isPause=false;
    }

    /**
     * 初始化播放器
     * @param isRelease 是否复位
     */
    private void initPlayer(final boolean isRelease) {
        currentIndex=0;
        if(null!=mHandler) mHandler.removeMessages(0);
        if(null!=mHandler){
            mHandler.postAtTime(new Runnable() {
                @Override
                public void run() {
                    if(isPause) return;
                    if(null!=mPlayerView1) mPlayerView1.start(isRelease);
                    if(null!=mPlayerView2) mPlayerView2.start(isRelease);
                    if(null!=mPlayerView3) mPlayerView3.start(isRelease);
                }
            }, SystemClock.uptimeMillis()+1000);
        }
    }

    @Override
    public RoomList getPlayInfo() {
        if(null==mRoomLists) return null;
        if(currentIndex>=(mRoomLists.size()-1)) currentIndex=0;
        RoomList roomList = mRoomLists.get(currentIndex);
        currentIndex++;
        return roomList;
    }

    //==========================================数据回调=============================================

    @Override
    public void showErrorView() {
    }

    @Override
    public void complete() {

    }

    @Override
    public void showLiveRooms(List<RoomList> data,boolean isRelease) {
        this.mRoomLists=data;
        if(isPause) return;
        initPlayer(isRelease);
    }

    @Override
    public void showLiveRoomEmpty() {
    }

    @Override
    public void showLiveRoomError(int code, String errorMsg) {
    }

    public interface OnSelectedListener{
        void onMackCallClick(RoomList data);
    }

    private OnSelectedListener mOnSelectedListener;

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        mOnSelectedListener = onSelectedListener;
    }
}
