package com.yc.liaolive.ui.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.ui.adapter.MoivesListAdapter;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.WeiXinVideo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentLocationVideoListBinding;
import com.yc.liaolive.manager.ThreadManager;
import com.yc.liaolive.model.RecyclerViewSpacesItem;
import com.yc.liaolive.media.ui.activity.MediaLocationVideoListActivity;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.MediaStoreUtil;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/7.
 * 本机视频列表
 */

public class MediaVideoLocationFragment extends BaseFragment<FragmentLocationVideoListBinding,RxBasePresenter> {

    private MoivesListAdapter mVideoListAdapter;
    private MediaLocationVideoListActivity mActivity;
    private DataChangeView mDataChangeView;

    @Override
    protected void initViews() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_location_video_list;
    }


    @Override
    public void onAttach(android.content.Context context) {
        super.onAttach(context);
        mActivity = (MediaLocationVideoListActivity) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        loadVideo();
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        if(null==bindingView) return;
        bindingView.recyerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false));
        bindingView.recyerView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(1.5f)));
        mVideoListAdapter = new MoivesListAdapter(null);
        //设置空视图
        mDataChangeView = new DataChangeView(getActivity());
        mDataChangeView.showLoadingView();
        mVideoListAdapter.setEmptyView(mDataChangeView);
        mVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mVideoListAdapter.setEnableLoadMore(true);
            }
        },bindingView.recyerView);

        bindingView.recyerView.setAdapter(mVideoListAdapter);
        //长按
        mVideoListAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showActionMenu(view,position);
                return false;
            }
        });
        mVideoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<WeiXinVideo> data = mVideoListAdapter.getData();
                if(null!=data&&data.size()>position){
                    WeiXinVideo item = data.get(position);
                    if(1==item.getItemType()){
                        //调用系统相机录制
                        if(null!=mActivity){
                            mActivity.startRecordVideo();
                        }
                        return;
                    }
                    if(null!=item.getVideoPath()&&new File(item.getVideoPath()).isFile()){
                        if(MediaStoreUtil.getInstance().isSupport(item.getVideoPath(),"mp4","mov","3gp")){
                            if(item.getVideoDortion()<Constant.MEDIA_VIDEO_EDIT_MIN_DURTION){
                                showErrorToast(null,null,"视频长度小于5秒！");
                                return;
                            }
                            //完成选择
                            if(null!=mActivity){
                                mActivity.onResultFinlish(item.getVideoPath(), view);
                            }
                            return;
                        }else{
                            showErrorToast(null,null,"该视频格式不受支持！");
                            return;
                        }
                    }else{
                        showErrorToast(null,null,"视频不存在，请重新扫描重试！");
                        return;
                    }
                }
            }
        });
    }

    /**
     * 显示删除菜单
     */
    private void showActionMenu(View view, final int position) {
        if(null!= mVideoListAdapter){
            SystemUtils.startVibrator(100);
            List<WeiXinVideo> data = mVideoListAdapter.getData();
            if(null!=data&&data.size()>0){
                final WeiXinVideo weiXinVideo = data.get(position);
                if(null!=weiXinVideo){
                    PopupMenu actionMenu = new PopupMenu(getActivity(), view, Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
                    actionMenu.inflate(R.menu.detele_video_action);
                    actionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId()==R.id.menu_detele){
                                try {
                                    boolean flag = FileUtils.deleteFile(weiXinVideo.getVideoPath());
                                    if(flag&&null!= mVideoListAdapter){
                                        mVideoListAdapter.remove(position);
                                    }else{
                                        ToastUtils.showCenterToast("删除失败!");
                                    }
                                }catch (Exception e){
                                    ToastUtils.showCenterToast("删除失败!");
                                }
                            }
                            return false;
                        }
                    });
                    actionMenu.show();
                }
            }
        }
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           if(10011==msg.what){
               if(null!=mDataChangeView) mDataChangeView.showEmptyView("在相册中未找到视频！点击右上角的文件查看更多~",R.drawable.ic_list_empty_icon);
                List<WeiXinVideo> videoInfos = (List<WeiXinVideo>) msg.obj;
                if(null!= mVideoListAdapter){
                    if(null==videoInfos) videoInfos=new ArrayList<>();
                    WeiXinVideo weiXinVideo=new WeiXinVideo();
                    weiXinVideo.setItemType(1);
                    videoInfos.add(0,weiXinVideo);
                    mVideoListAdapter.setNewData(videoInfos);
                    mVideoListAdapter.loadMoreEnd();
                }
            }
            super.handleMessage(msg);
        }
    };


    /**
     * 扫描本机相册的所有视频
     */
    private void loadVideo() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            closeProgressDialog();
            ToastUtils.showCenterToast("SD存储卡准备中");
            return;
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
            closeProgressDialog();
            ToastUtils.showCenterToast("您的设备没有链接到USB位挂载");
            return;
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            closeProgressDialog();
            ToastUtils.showCenterToast("无法读取SD卡，请检查SD卡授予本软件的使用权限！");
            return;
        }

        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                List<WeiXinVideo> videoInfos = MediaStoreUtil.getInstance().getVideoInfo(getActivity(),"mp4","mov","3gp");
                if(null!=mHandler){
                    Message message=Message.obtain();
                    message.what=10011;
                    message.obj=videoInfos;
                    mHandler.sendMessage(message);
                }

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataChangeView=null;mActivity=null;
    }
}
