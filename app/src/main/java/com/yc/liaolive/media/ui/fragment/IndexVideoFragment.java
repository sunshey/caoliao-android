package com.yc.liaolive.media.ui.fragment;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.databinding.FragmentVideoLiveBinding;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.media.view.IndexLivePlayerViewGroup;

/**
 * TinyHung@Outlook.com
 * 2018/5/25
 * 首页-抢聊
 * onVisible 一级界面切换延时处理提升速度
 * onResume 二级界面切换应立即销毁播放器
 */

public class IndexVideoFragment extends BaseFragment<FragmentVideoLiveBinding,RxBasePresenter> {

    private static final int CAMERA_OK = 100;
//    private TXLivePusher mTxLivePusher;
    private Handler mHandler;

    @Override
    protected void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_changed:
                        bindingView.indexLivePlayer.onRefresh();
                        break;
                    case R.id.index_video_tips_layout:
                        bindingView.indexVideoTipsLayout.setVisibility(View.GONE);
                        break;
                }
            }
        };
        bindingView.indexVideoTipsLayout.setOnClickListener(onClickListener);
        bindingView.btnChanged.setOnClickListener(onClickListener);
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("点击");
        stringBuilder.append("\"X\"");
        stringBuilder.append("可以换人哦~");
        bindingView.indexVideoTips.setText(stringBuilder.toString());
        //监听抢聊事件
        bindingView.indexLivePlayer.setOnSelectedListener(new IndexLivePlayerViewGroup.OnSelectedListener() {
            @Override
            public void onMackCallClick(final RoomList data) {
                if(null==data) return;
                ToastUtils.showCenterToast("开发中...");
//                if(null!=bindingView) bindingView.indexLivePlayer.onStop();
//                bindingView.indexLivePlayer.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        LiveCallActivity.makeCall(getContext(),data.getUserid(),data.getAvatar(),data.getNickname());
//                    }
//                },500);
            }
        });
//        mTxLivePusher = new TXLivePusher(getContext());
//        mTxLivePusher.setBeautyFilter(TXLiveConstants.BEAUTY_STYLE_SMOOTH, 9, 3, 7);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_live;
    }

    @Override
    public void fromMainUpdata() {
        super.fromMainUpdata();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(null!=bindingView) bindingView.indexLivePlayer.onStart();
        startPreview();
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        closePreview();
        if(null!=bindingView) bindingView.indexLivePlayer.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()){
            startPreview();
            if(null!=bindingView) bindingView.indexLivePlayer.onStart();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        closePreview();
        if(null!=bindingView) bindingView.indexLivePlayer.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler = new Handler();
//        List<VideoLiveTagInfo> liveTagInfos=new ArrayList<>();
//        for (int i = 1; i <= 12; i++) {
//            VideoLiveTagInfo liveTagInfo=new VideoLiveTagInfo();
//            liveTagInfo.setId(i+"");
//            if(i%2==0){
//                liveTagInfo.setTitle("学生"+i);
//            }else{
//                liveTagInfo.setTitle("小护士姐姐"+i);
//            }
//            liveTagInfos.add(liveTagInfo);
//        }
//        VideoLiveTagInfo liveTagInfoHeader=new VideoLiveTagInfo();
//        liveTagInfoHeader.setId(0+"");
//        liveTagInfoHeader.setTitle("全部");
//        liveTagInfos.add(0,liveTagInfoHeader);
//        bindingView.flowLayout.setOnTagClickListener(new FlowLayout.OnTagClickListener() {
//            @Override
//            public void TagClick(String tag) {
//                ToastUtils.showCenterToast("TAG："+tag);
//            }
//        });
//        bindingView.flowLayout.setListData(liveTagInfos);
    }

    /**
     * 用户摄像头预览
     */
    private void startPreview() {
        if (Build.VERSION.SDK_INT>22){
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                //检查摄像头权限
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA},CAMERA_OK);
            }else {
                //权限已获取
                openPerview();
            }
        }else {
            //6.0系统下，无需获取权限
            openPerview();
        }
    }

    /**
     * 开启相机预览
     * 加速运行，使用定时任务
     */
    private void openPerview(){
        if(null!=mHandler){
            mHandler.removeMessages(0);
            mHandler.postAtTime(new Runnable() {
                @Override
                public void run() {
//                    if(null!=mTxLivePusher)mTxLivePusher.startCameraPreview(bindingView.videoView);
                }
            }, SystemClock.uptimeMillis()+500);
        }
    }

    /**
     * 用户摄像头预览关闭
     * 加速运行，使用定时任务
     */
    private void closePreview() {
        if(null!=mHandler){
            mHandler.removeMessages(0);
            mHandler.postAtTime(new Runnable() {
                @Override
                public void run() {
//                    if (null!=mTxLivePusher) {
//                        mTxLivePusher.stopCameraPreview(true);
//                        mTxLivePusher.stopPusher();
//                    }
                }
            }, SystemClock.uptimeMillis()+300);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case CAMERA_OK:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                        if(getUserVisibleHint()){
                            openPerview();
                        }
                }else {
                    ToastUtils.showCenterToast("请手动打开相机权限");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mHandler) mHandler.removeMessages(0);mHandler=null;
    }
}
