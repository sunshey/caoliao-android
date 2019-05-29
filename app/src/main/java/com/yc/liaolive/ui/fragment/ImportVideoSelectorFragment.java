package com.yc.liaolive.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.ui.adapter.MoivesListAdapter;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.listener.OnItemLongClickListener;
import com.yc.liaolive.bean.WeiXinVideo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentImportVideoSelectorBinding;
import com.yc.liaolive.bean.ScanMessageEvent;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.ThreadManager;
import com.yc.liaolive.model.GridSpaceItemDecorationComent;
import com.yc.liaolive.media.ui.activity.MediaLocationVideoListActivity;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.MediaStoreUtil;
import com.yc.liaolive.util.ScanWeixin;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@outlook.com
 * 2017-06-30 29:41
 * 本地视频列表选择
 */

public class ImportVideoSelectorFragment extends BaseFragment<FragmentImportVideoSelectorBinding,RxBasePresenter> implements Observer {

    private String mVideoFolderPath;//读取置顶文件夹下的视频封面
    private MoivesListAdapter mVideoListAdapter;
    private ScanWeixin mScanWeiXin;
    private List<WeiXinVideo> mWeiXinVideos;
    private MediaLocationVideoListActivity mActivity;
    private DataChangeView mDataChangeView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MediaLocationVideoListActivity) context;
    }

    @Override
    protected void initViews() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_import_video_selector;
    }

    /**
     * 传参
     * @param path
     * @param name
     * @return
     */
    public static ImportVideoSelectorFragment newInstance(String path, String name) {
        ImportVideoSelectorFragment importVideoSelectorFragment=new ImportVideoSelectorFragment();
        Bundle bundle=new Bundle();
        bundle.putString("video_folder_path",path);
        importVideoSelectorFragment.setArguments(bundle);
        return importVideoSelectorFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 取参
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mVideoFolderPath = arguments.getString("video_folder_path");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApplicationManager.getInstance().addObserver(this);
        initAdapter();
        if(TextUtils.isEmpty(mVideoFolderPath)){
            showErrorToast(null,null,"目录不正确，请返回重试");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(mVideoFolderPath)&&new File(mVideoFolderPath).exists()&&null==mWeiXinVideos){
            if(null!= mVideoListAdapter){
                mVideoListAdapter.setNewData(null);
                if(null!=mDataChangeView) mDataChangeView.showLoadingView();
                loadVideo();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!=mScanWeiXin){
            mScanWeiXin.setScanEvent(false);
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        bindingView.recyerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false));
        bindingView.recyerView.addItemDecoration(new GridSpaceItemDecorationComent(ScreenUtils.dpToPxInt(1.5f)));
        mVideoListAdapter = new MoivesListAdapter(null);
        //设置空视图
        mDataChangeView = new DataChangeView(getActivity());
        mDataChangeView.showLoadingView();
        mVideoListAdapter.setEmptyView(mDataChangeView);
        mVideoListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

            }
        }, bindingView.recyerView);
        bindingView.recyerView.setAdapter(mVideoListAdapter);
        //长按
        bindingView.recyerView.addOnItemTouchListener(new OnItemLongClickListener() {
            @Override
            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showActionMenu(view,position);
            }
        });
        //点击
        mVideoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<WeiXinVideo> data = mVideoListAdapter.getData();
                if(null!=data&&data.size()>position){
                    WeiXinVideo item = data.get(position);
                    if(1==item.getItemType()) return;
                    if(null!=item&&null!=item.getVideoPath()&&new File(item.getVideoPath()).isFile()){
                        if(MediaStoreUtil.getInstance().isSupport(item.getVideoPath(),"mp4","mov","3gp")){
                            if(item.getVideoDortion()<Constant.MEDIA_VIDEO_EDIT_MIN_DURTION){
                                showErrorToast(null,null,"视频长度小于5秒！");
                                return;
                            }
                            //完成选择
                            if(null!=mActivity){
                                mActivity.onResultFinlish(item.getVideoPath(),view);
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
                                    ToastUtils.showCenterToast("删除失败!"+e.getMessage());
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
        if(null!= mVideoFolderPath){
            ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                @Override
                public void run() {
                    mScanWeiXin = new ScanWeixin();
                    mScanWeiXin.setExts("mp4", "3gp", "mov");
                    mScanWeiXin.setEvent(true);
                    mWeiXinVideos = mScanWeiXin.scanFiles(mVideoFolderPath);
                    if(null!=mHandler){
                        mHandler.sendEmptyMessage(10011);
                    }
                }
            });
        }
    }



    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //分段刷新
            if(10010==msg.what){
                if(null!=mDataChangeView) mDataChangeView.showEmptyView("该文件夹下未找到视频文件~",R.drawable.ic_list_empty_icon);
                List<WeiXinVideo> weiXinVideos= (List<WeiXinVideo>) msg.obj;
                if(null!= mVideoListAdapter){
                    mVideoListAdapter.addData(weiXinVideos);
                }
            //加载完毕了
            }else if(10011==msg.what){
                if(null!=mDataChangeView) mDataChangeView.showEmptyView("该文件夹下未找到视频文件~",R.drawable.ic_list_empty_icon);
                if(null!= mVideoListAdapter){
                    bindingView.recyerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mVideoListAdapter.loadMoreEnd();//没有更多的数据了
                        }
                    });
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onDestroy() {
        ApplicationManager.getInstance().removeObserver(this);
        closeProgressDialog();
        if(null!=mScanWeiXin){
            mScanWeiXin.setScanEvent(false);
        }
        mScanWeiXin=null;
        if(null!=mWeiXinVideos){
            mWeiXinVideos.clear();
            mWeiXinVideos=null;
        }
        mDataChangeView=null;
        mActivity=null;
        super.onDestroy();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg&& arg instanceof ScanMessageEvent){
            ScanMessageEvent event= (ScanMessageEvent) arg;
            if("updata_video_list".equals(event.getMessage())){
                List<WeiXinVideo> weiXinVideos = event.getWeiXinVideos();
                if(null!=weiXinVideos&&weiXinVideos.size()>0){
                    if(null!=mHandler){
                        Message message=Message.obtain();
                        message.what=10010;
                        message.obj=weiXinVideos;
                        mHandler.sendMessage(message);
                    }
                }
            }
        }
    }
}
