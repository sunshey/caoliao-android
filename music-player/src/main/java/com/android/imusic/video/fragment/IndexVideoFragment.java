package com.android.imusic.video.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.android.imusic.MainActivity;
import com.android.imusic.R;
import com.android.imusic.music.base.MusicBaseFragment;
import com.android.imusic.music.engin.IndexPersenter;
import com.android.imusic.music.net.MusicNetUtils;
import com.android.imusic.music.utils.MediaUtils;
import com.android.imusic.video.activity.VideoPlayerActviity;
import com.android.imusic.video.adapter.VideoIndexVideoAdapter;
import com.android.imusic.video.bean.OpenEyesIndexInfo;
import com.android.imusic.video.bean.OpenEyesIndexItemBean;
import com.android.imusic.video.bean.VideoParams;
import com.google.gson.reflect.TypeToken;
import com.music.player.lib.adapter.base.OnItemClickListener;
import com.music.player.lib.adapter.base.OnLoadMoreListener;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.util.Logger;
import com.music.player.lib.util.MusicUtils;
import com.video.player.lib.manager.VideoPlayerManager;
import com.video.player.lib.view.VideoPlayerTrackView;

/**
 * TinyHung@Outlook.com
 * 2019/4/8
 * Index Music
 */

public class IndexVideoFragment extends MusicBaseFragment<IndexPersenter> {

    private static final String TAG = "IndexVideoFragment";
    private VideoIndexVideoAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean refreshFinish=false;
    private int mPage=0;
    private PopupWindow mPopupWindow;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private View mAnchorView;

    @Override
    protected int getLayoutID() {
        return R.layout.video_fragment_index_video;
    }

    @Override
    protected void initViews() {
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {}

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if(null!=view.getTag()&& view.getTag() instanceof OpenEyesIndexItemBean){
                    VideoPlayerTrackView playerTrackView = (VideoPlayerTrackView) view.findViewById(R.id.video_track);
                    if(null!=playerTrackView&&playerTrackView.isWorking()){
                        playerTrackView.onReset();
                    }
                }
            }
        });
        mAdapter = new VideoIndexVideoAdapter(getContext(),null);
        //条目点击事件
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long itemId){
                if(null!=view.getTag() && view.getTag() instanceof OpenEyesIndexItemBean){
                    OpenEyesIndexItemBean indexItemBean = (OpenEyesIndexItemBean) view.getTag();
                    VideoPlayerTrackView trackView = (VideoPlayerTrackView) view.findViewById(R.id.video_track);
                    VideoParams videoParams= MediaUtils.getInstance().formatVideoParams(indexItemBean);
                    Intent intent=new Intent(getActivity(), VideoPlayerActviity.class);
                    intent.putExtra(MusicConstants.KEY_VIDEO_PARAMS,videoParams);
                    if(null!=trackView&&trackView.isWorking()){
                        ((MainActivity) getActivity()).setContinuePlay(true);
                        trackView.reset();
                        intent.putExtra(MusicConstants.KEY_VIDEO_PLAYING,true);
                    }else{
                        VideoPlayerManager.getInstance().onReset();
                    }
                    startActivity(intent);
                }
            }
        });
        //菜单事件
        mAdapter.setOnMenuClickListener(new VideoIndexVideoAdapter.OnMenuClickListener() {
            @Override
            public void onMenuClick(View itemView,View clickView) {
                if(null!=clickView.getTag() && clickView.getTag() instanceof OpenEyesIndexItemBean){
                    final OpenEyesIndexItemBean indexItemBean= (OpenEyesIndexItemBean) clickView.getTag();
                    VideoParams videoParams= MediaUtils.getInstance().formatVideoParams(indexItemBean);
                    videoParams.setHeadTitle("相关推荐");
                    showPropupMenu(itemView,clickView,videoParams);
                }
            }
        });
        //加载更多
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(null!=mPresenter&&!mPresenter.isRequsting()){
                    mPage++;
                    loadData();
                }
            }
        },recyclerView);
        recyclerView.setAdapter(mAdapter);

        mAnchorView = getView().findViewById(R.id.view_anchor);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipre_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setProgressViewOffset(false,0,200);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=0;
                loadData();
            }
        });
        mPresenter=new IndexPersenter();
    }

    /**
     * 在某个锚点显示弹窗
     * @param itemView ItemView
     * @param clickView 锚点View,这里使用mAnchorView做屏幕的锚点,弹窗位置出现在按钮的左侧
     * @param indexItemBean
     */
    private void showPropupMenu(final View itemView, final View clickView, final VideoParams indexItemBean) {
        View view = View.inflate(getActivity(), R.layout.video_popup_window_layout, null);
        view.findViewById(R.id.tv_item_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                new android.support.v7.app.AlertDialog.Builder(getActivity())
                        .setTitle("描述信息")
                        .setMessage(indexItemBean.getVideoDesp())
                        .setPositiveButton("关闭", null).show();
            }
        });
        view.findViewById(R.id.tv_item_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                if(null!=indexItemBean){
                    VideoPlayerTrackView trackView = (VideoPlayerTrackView) itemView.findViewById(R.id.video_track);
                    Intent intent=new Intent(getActivity(), VideoPlayerActviity.class);
                    intent.putExtra(MusicConstants.KEY_VIDEO_PARAMS,indexItemBean);
                    if(null!=trackView&&trackView.isWorking()){
                        ((MainActivity) getActivity()).setContinuePlay(true);
                        trackView.reset();
                        intent.putExtra(MusicConstants.KEY_VIDEO_PLAYING,true);
                    }else{
                        VideoPlayerManager.getInstance().onReset();
                    }
                    startActivity(intent);
                }
            }
        });
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setFocusable(true);//获得焦点，才能让View里的点击事件生效
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPopupWindow=null;
            }
        });
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width,width);
        mMeasuredWidth = view.getMeasuredWidth();
        mMeasuredHeight = view.getMeasuredHeight();
        int[] locations=new int[2];
        clickView.getLocationOnScreen(locations);
        //X:控件在屏幕的X轴-弹窗总宽度 Y:控件在屏幕的X轴-弹窗总高度
        int startX=locations[0]-mMeasuredWidth;
        int startY=locations[1]+clickView.getMeasuredHeight()-mMeasuredHeight;
        //如果现实之后的Y轴到达了屏幕的状态栏或者之上，反过来显示
        if(startY< MusicUtils.getInstance().getStatusBarHeight(getActivity())){
            startY=locations[1]+(clickView.getMeasuredHeight()/2);
        }
        Logger.d(TAG,"showPropupMenu-->viewX:"+locations[0]+",viewY:"+locations[1]+",startX:"+startX+",startY:"+startY+",viewW:"+mMeasuredWidth+",viewH:"+mMeasuredHeight);
        if(null==mAnchorView){
            mAnchorView=getView().findViewById(R.id.view_anchor);
        }
        mPopupWindow.showAsDropDown(mAnchorView,startX ,startY);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(!refreshFinish&&null!=mAdapter&&null!=mPresenter&&!mPresenter.isRequsting()){
            mPage=0;
            loadData();
        }
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        VideoPlayerManager.getInstance().onStop();
    }

    /**
     * 加载音频列表
     */
    private void loadData() {
        if(null!=mPresenter){
            if(0==mPage&&null!=mSwipeRefreshLayout&&!mSwipeRefreshLayout.isRefreshing()){
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
            }
            mPresenter.getIndexVideoList(mPage,new TypeToken<OpenEyesIndexInfo>(){}.getType(),new MusicNetUtils.OnOtherRequstCallBack<OpenEyesIndexInfo>() {

                @Override
                public void onResponse(OpenEyesIndexInfo data) {
                    refreshFinish=true;
                    if(null!=mSwipeRefreshLayout){
                        mSwipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                    if(null!=mAdapter){
                        if(null!=data.getItemList()&&data.getItemList().size()>0){
                            mAdapter.onLoadComplete();
                            if(mPage==0){
                                mAdapter.setNewData(data.getItemList());
                            }else{
                                mAdapter.addData(data.getItemList());
                            }
                        }else{
                            mAdapter.onLoadEnd();
                        }
                    }
                }

                @Override
                public void onError(int code, String errorMsg) {
                    Logger.d(TAG,"onError-->code:"+code+",errorMsg:"+errorMsg);
                    if(null!=mSwipeRefreshLayout){
                        mSwipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                    if(mPage>-1){
                        mPage--;
                    }
                    if(null!=mAdapter){
                        mAdapter.onLoadError();
                    }
                    Toast.makeText(getContext(),errorMsg,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mSwipeRefreshLayout){
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout=null;
        }
        if(null!=mAdapter){
            mAdapter.onDestroy();
            mAdapter=null;
        }
    }
}