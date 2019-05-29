package com.yc.liaolive.live.ui.fragment;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialogFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.MediaMusicCategoryList;
import com.yc.liaolive.bean.MusicInfo;
import com.yc.liaolive.databinding.FragmentLocationMusicBinding;
import com.yc.liaolive.databinding.ReEmptyMarginLayoutBinding;
import com.yc.liaolive.interfaces.OnMediaMusicClickListener;
import com.yc.liaolive.msg.model.MusicComparator;
import com.yc.liaolive.util.AudioUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.layout.MineDataChangeMarginView;
import com.yc.liaolive.view.widget.SideBar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/11/9.
 * 本地音乐选择
 */

public class LiveMusicFragment extends BaseDialogFragment<FragmentLocationMusicBinding,RxBasePresenter> implements OnMediaMusicClickListener {

//    private MediaLocationMusicAdapter mMusicAdapter;
    private HashMap<String, Integer> positionMap = new HashMap<>();
    private ReEmptyMarginLayoutBinding mEmptyViewbindView;

    @Override
    protected void initViews() {
        bindingView.recyerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
//        mMusicAdapter = new MediaLocationMusicAdapter(null,this);
//        mMusicAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
//            @Override
//            public void onLoadMoreRequested() {
//                bindingView.recyerView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mMusicAdapter.loadMoreEnd();
//                    }
//                });
//            }
//        }, bindingView.recyerView);
        //加载中、数据为空、加载失败布局
        mEmptyViewbindView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.re_empty_margin_layout, (ViewGroup) bindingView.recyerView.getParent(),false);
        mEmptyViewbindView.emptyView.setOnRefreshListener(new MineDataChangeMarginView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEmptyViewbindView.emptyView.showLoadingView();
                queryLocationMusic();
            }

            @Override
            public void onClickView(View v) {

            }
        });
        mEmptyViewbindView.emptyView.showLoadingView();
//        mMusicAdapter.setEmptyView(mEmptyViewbindView.getRoot());
//
//        bindingView.recyerView.setAdapter(mMusicAdapter);

        bindingView.sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                bindingView.dialogText.setText(s);
                bindingView.sidebar.setView(bindingView.dialogText);
                if (positionMap.get(s) != null) {
                    int i = positionMap.get(s);
                    ((LinearLayoutManager) bindingView.recyerView.getLayoutManager()).scrollToPositionWithOffset(i + 1, 0);
                }
            }
        });
        bindingView.tvTitle.setText("本地音乐列表");
        bindingView.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveMusicFragment.this.dismiss();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_location_music;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                queryLocationMusic();
            }
        },300);
    }


    private void queryLocationMusic() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            ToastUtils.showCenterToast("SD存储卡准备中");
            return;
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
            ToastUtils.showCenterToast("您的设备没有链接到USB位挂载");
            return;
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showCenterToast("无法读取SD卡，请检查SD卡使用权限！");
            return;
        }

        new AsyncTask<Void, Void, List<MusicInfo>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<MusicInfo> doInBackground(final Void... unused) {
                ArrayList<MusicInfo> allSongs = AudioUtils.getAllSongs();
                Collections.sort(allSongs, new MusicComparator());
                for (int i = 0; i < allSongs.size(); i++) {
                    if (positionMap.get(allSongs.get(i).getPinyin()) == null)
                        positionMap.put(allSongs.get(i).getPinyin(), i);
                }
                return allSongs;
            }

            @Override
            protected void onPostExecute(List<MusicInfo> data) {
                if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.showEmptyView("未扫描到本机音乐",R.drawable.iv_folder_all,false);
//                if(null!= mMusicAdapter){
//                    mMusicAdapter.setNewData(data);
//                }
            }
        }.execute();
    }

    @Override
    public void onItemClick(int poistion) {

    }

    @Override
    public void onLikeClick(MediaMusicCategoryList.DataBean musicID) {

    }

    @Override
    public void onDetailsClick(String musicID) {

    }

    @Override
    public void onSubmitMusic(MediaMusicCategoryList.DataBean musicPath) {

    }

    @Override
    public void onSubmitLocationMusic(MusicInfo data) {
        if(null!=data&&!TextUtils.isEmpty(data.getFileUrl())){
            if(null!=mOnMusicSeletedListener){
                mOnMusicSeletedListener.onSelected(data.getFileUrl());
                LiveMusicFragment.this.dismiss();
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=positionMap){
            positionMap.clear();
        }
        if(null!=mEmptyViewbindView) mEmptyViewbindView.emptyView.stopLoading();
    }


    public interface OnMusicSeletedListener{
        void onSelected(String musicFilePath);
    }

    private OnMusicSeletedListener mOnMusicSeletedListener;

    public void setOnMusicSeletedListener(OnMusicSeletedListener onMusicSeletedListener) {
        mOnMusicSeletedListener = onMusicSeletedListener;
    }
}
