package com.android.imusic.music.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.imusic.R;
import com.android.imusic.music.adapter.MusicSearchAdapter;
import com.android.imusic.music.base.MusicBaseActivity;
import com.android.imusic.music.bean.MediaInfo;
import com.android.imusic.music.bean.MusicDetails;
import com.android.imusic.music.bean.ResultData;
import com.android.imusic.music.bean.SearchHistroy;
import com.android.imusic.music.bean.SearchMusicAnchor;
import com.android.imusic.music.bean.SearchMusicData;
import com.android.imusic.music.bean.SearchResult;
import com.android.imusic.music.bean.SearchResultInfo;
import com.android.imusic.music.dialog.MusicMusicDetailsDialog;
import com.android.imusic.music.engin.SearchPersenter;
import com.android.imusic.music.net.MusicNetUtils;
import com.android.imusic.music.utils.MediaUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.reflect.TypeToken;
import com.music.player.lib.adapter.base.OnLoadMoreListener;
import com.music.player.lib.bean.MusicStatus;
import com.music.player.lib.listener.MusicOnItemClickListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.manager.MusicSubjectObservable;
import com.music.player.lib.model.MusicPlayingChannel;
import com.music.player.lib.util.Logger;
import com.music.player.lib.util.MusicUtils;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2019/3/24
 * Music Search
 */

public class MusicSearchActivity extends MusicBaseActivity<SearchPersenter> implements MusicOnItemClickListener, Observer {

    private View mBtnClean;
    private EditText mEtInput;
    private MusicSearchAdapter mAdapter;
    private int mPage,mSearchHistroyCount;
    private RecyclerView mRecyclerView;
    private View mTagsRoot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowEnable(true);
        setContentView(R.layout.music_activity_search);
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.music_btn_back:
                        onBackPressed();
                        break;
                    case R.id.music_btn_search:
                        search(false);
                        break;
                    case R.id.music_btn_clean:
                        mEtInput.setText("");
                        break;
                    case R.id.music_btn_remove:
                        if(mSearchHistroyCount>0){
                            MusicUtils.getInstance().closeKeybord(MusicSearchActivity.this,mEtInput);
                            new android.support.v7.app.AlertDialog.Builder(MusicSearchActivity.this)
                                    .setTitle("删除提示")
                                    .setMessage("清空搜索记录后无法恢复，是否继续？")
                                    .setNegativeButton("取消",null)
                                    .setPositiveButton("继续清空", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MediaUtils.getInstance().removeAllHistroySearchCache();
                                            createSearchCache();
                                        }
                                    }).setCancelable(false).show();
                        }else{
                            Toast.makeText(MusicSearchActivity.this,"暂无搜索记录可清除",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
        findViewById(R.id.music_btn_back).setOnClickListener(onClickListener);
        findViewById(R.id.music_btn_search).setOnClickListener(onClickListener);
        findViewById(R.id.music_btn_remove).setOnClickListener(onClickListener);
        mBtnClean = findViewById(R.id.music_btn_clean);
        mBtnClean.setOnClickListener(onClickListener);
        mEtInput = (EditText) findViewById(R.id.music_et_input);
        mEtInput.setHint("搜索歌手、歌曲、专辑");
        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(null!=s&&s.length()>0){
                    mBtnClean.setVisibility(View.VISIBLE);
                }else{
                    mBtnClean.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    search(false);
                }
                return false;
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        mTagsRoot = findViewById(R.id.music_tags_root);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new MusicSearchAdapter(MusicSearchActivity.this,null,this);
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                search(true);
            }
        },mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter=new SearchPersenter();
        MusicPlayerManager.getInstance().addObservable(this);
        //搜索记录回显
        createSearchCache();
    }

    /**
     * 更新缓存
     */
    private void createSearchCache() {
        List<SearchHistroy> searchByCache = MediaUtils.getInstance().getSearchByHistroy();
        FlexboxLayout flexboxLayout = (FlexboxLayout) findViewById(R.id.music_search_flags);
        if(null!=searchByCache&&searchByCache.size()>0){
            mSearchHistroyCount++;
            flexboxLayout.removeAllViews();
            for (int i = 0; i < searchByCache.size(); i++) {
                SearchHistroy searchHistroy = searchByCache.get(i);
                TextView tagTextView = new TextView(this);
                tagTextView.setTextSize(13);
                tagTextView.setTextColor(Color.parseColor("#313131"));
                tagTextView.setText(searchHistroy.getKey());
                tagTextView.setGravity(Gravity.CENTER);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    tagTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.music_search_tag_bg));
                }
                tagTextView.setTag(searchHistroy.getKey());
                tagTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key=(String) v.getTag();
                        mEtInput.setText(key);
                        mEtInput.setSelection(key.length());
                        search(key,false);
                    }
                });
                flexboxLayout.addView(tagTextView);
            }
        }else{
            mSearchHistroyCount=0;
            flexboxLayout.removeAllViews();
        }
    }

    /**
     * 开始搜索,是否自动搜索
     * @param isAuto
     */
    private void search(boolean isAuto) {
        if(null==mPresenter){
            mPresenter=new SearchPersenter();
        }
        String key = mEtInput.getText().toString().trim();
        if(!TextUtils.isEmpty(key)){
            search(key,isAuto);
        }
    }

    /**
     * 开始搜索
     * @param key
     * @param isAuto 是否自动搜索
     */
    private void search(String key,boolean isAuto) {
        if(!isAuto){
            MusicUtils.getInstance().closeKeybord(MusicSearchActivity.this,mEtInput);
            mPage=1;
            showProgressDialog("搜索中,请稍后...");
            mAdapter.setCurrentKey(key);
            //写入搜索记录
            MediaUtils.getInstance().putSearchKeyToHistroy(key, new MediaUtils.OnTaskCallBack() {
                @Override
                public void onFinlish() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createSearchCache();
                        }
                    });
                }
            });
        }
        mPresenter.queryMusicToKey(key,mPage,new TypeToken<ResultData<SearchResult>>() {}.getType(), new MusicNetUtils.OnRequstCallBack<SearchResult>() {
            @Override
            public void onResponse(ResultData<SearchResult> data) {
                if(!MusicSearchActivity.this.isFinishing()){
                    closeProgressDialog();
                    if(null!=mRecyclerView&&mRecyclerView.getVisibility()!=View.VISIBLE){
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                    if(null!=mTagsRoot&&mTagsRoot.getVisibility()!=View.GONE){
                        mTagsRoot.setVisibility(View.GONE);
                    }
                    if(null!=mAdapter){
                        if(null!=data.getData()&&null!=data.getData().getInfo()){
                            mAdapter.onLoadComplete();
                            if(mPage==1){
                                mAdapter.setNewData(data.getData().getInfo());
                            }else{
                                mAdapter.addData(data.getData().getInfo());
                            }
                        }else{
                            mAdapter.onLoadEnd();
                            Toast.makeText(MusicSearchActivity.this,data.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onError(int code, String errorMsg) {
                Logger.d(TAG,"onError-->code:"+code+",errorMsg:"+errorMsg);
                if(!MusicSearchActivity.this.isFinishing()){
                    closeProgressDialog();
                    if(mPage>0){
                        mPage--;
                    }
                    if(null!=mAdapter){
                        mAdapter.onLoadError();
                    }
                    Toast.makeText(MusicSearchActivity.this,errorMsg,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 条目点击事件
     * @param view
     * @param posotion
     * @param musicID
     */
    @Override
    public void onItemClick(View view, final int posotion, long musicID) {
        if(null!=view.getTag()){
            final SearchResultInfo searchResultInfo = (SearchResultInfo) view.getTag();
            if(musicID>0){
                String hashKey = MusicPlayerManager.getInstance().getCurrentPlayerHashKey();
                //检测是否正在播放当前歌曲
                if(!TextUtils.isEmpty(hashKey)&&hashKey.equals(searchResultInfo.getHash())){
                    //重复点击，打开播放器
                    startToMusicPlayer(searchResultInfo.getAudio_id());
                    return;
                }
                if(null!=mPresenter&&!mPresenter.isRequsting()){
                    mPresenter.getPlayUrl(searchResultInfo.getHash(), new TypeToken<ResultData<SearchMusicData>>() {}.getType(), new MusicNetUtils.OnRequstCallBack<SearchMusicData>() {
                        @Override
                        public void onResponse(ResultData<SearchMusicData> data) {
                            if(null!=mAdapter&&null!=data.getData()){
                                //准备播放，并将当前列表Item对象更新，方便收藏
                                if(!TextUtils.isEmpty(data.getData().getPlay_url())){
                                    SearchMusicData musicData = data.getData();
                                    searchResultInfo.setAlbum_img(musicData.getImg());
                                    searchResultInfo.setSource(musicData.getPlay_url());
                                    mAdapter.notifyDataSetChanged(posotion);
                                    MediaInfo mediaInfo=getMediaInfo(musicData,searchResultInfo.getAudio_id());
                                    MusicPlayerManager.getInstance().setPlayingChannel(MusicPlayingChannel.CHANNEL_SEARCH);
                                    MusicPlayerManager.getInstance().addPlayMusicToTop(mediaInfo);
                                    //如果悬浮窗权限未给定
                                    createMiniJukeboxWindow();
                                }else{
                                    Toast.makeText(MusicSearchActivity.this,"此歌曲已被下架",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(MusicSearchActivity.this,data.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(int code, String errorMsg) {
                            Logger.d(TAG,"onError-->code:"+code+",errorMsg:"+errorMsg);
                            Toast.makeText(MusicSearchActivity.this,errorMsg,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }else{
                //Menu
                final MediaInfo mediaInfo = getMediaInfo(searchResultInfo);
                MusicMusicDetailsDialog.getInstance(MusicSearchActivity.this,mediaInfo,MusicMusicDetailsDialog.DialogScene.SCENE_SEARCH)
                        .setMusicOnItemClickListener(new MusicOnItemClickListener() {
                            /**
                             * @param view
                             * @param itemId 参考 MusicDetails 定义
                             * @param musicID
                             */
                            @Override
                            public void onItemClick(View view, int itemId, long musicID) {
                                //只有用户试听成功后才可收藏
                                if(!TextUtils.isEmpty(searchResultInfo.getSource())){
                                    onMusicMenuClick(posotion,itemId,searchResultInfo);
                                }else{
                                    Toast.makeText(MusicSearchActivity.this,"请先试听后收藏",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
            }
        }
    }

    /**
     * 音乐列表菜单处理
     * @param itemId
     * @param mediaInfo
     */
    protected void onMusicMenuClick(int position,int itemId, SearchResultInfo mediaInfo) {
        if(itemId== MusicDetails.ITEM_ID_NEXT_PLAY){
            MusicPlayerManager.getInstance().playNextMusic();
        }else if(itemId== MusicDetails.ITEM_ID_SHARE){
            try {
                if(!TextUtils.isEmpty(mediaInfo.getSource())){
                    if(mediaInfo.getSource().startsWith("http:")||mediaInfo.getSource().startsWith("https:")){
                        Intent sendIntent = new Intent();
                        //sendIntent.setPackage("com.tencent.mm")
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "iMusic分享");
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "我正在使用"+getResources().getString(R.string.app_name)+
                                "听:《"+mediaInfo.getSongname()+"》，快来听吧~猛戳-->"+mediaInfo.getSource());
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "iMusic分享"));
                    }else{
                        Intent sendIntent = new Intent();
                        //sendIntent.setPackage("com.tencent.mm")
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "来自iMusic的音乐分享:《"+mediaInfo.getSongname()+"》-"+mediaInfo.getSingername());
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mediaInfo.getSource()));
                        sendIntent.setType("audio/*");
                        startActivity(Intent.createChooser(sendIntent, "iMusic分享"));
                    }
                }else{
                    Toast.makeText(MusicSearchActivity.this,"此歌曲已被下架",Toast.LENGTH_SHORT).show();
                }
            }catch (RuntimeException e){
                e.printStackTrace();
                Toast.makeText(MusicSearchActivity.this,"分享失败："+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }else if(itemId==MusicDetails.ITEM_ID_COLLECT){
            MediaInfo cacheAudioInfo = getMediaInfo(mediaInfo);
            cacheAudioInfo.setId(mediaInfo.getAudio_id());
            cacheAudioInfo.setImg_path(mediaInfo.getAlbum_img());
            cacheAudioInfo.setFile_path(mediaInfo.getSource());
            if(!TextUtils.isEmpty(cacheAudioInfo.getFile_path())){
                boolean toCollect = MusicUtils.getInstance().putMusicToCollect(cacheAudioInfo);
                if(toCollect){
                    Toast.makeText(MusicSearchActivity.this,"已添加至收藏列表",Toast.LENGTH_SHORT).show();
                    MusicPlayerManager.getInstance().observerUpdata(new MusicStatus());
                }
            }else{
                Toast.makeText(MusicSearchActivity.this,"添加失败，此歌曲不支持收藏",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 转换音频对象
     * @param musicData
     * @return
     */
    private MediaInfo getMediaInfo(SearchResultInfo musicData) {
        if(null!=musicData){
            MediaInfo mediaInfo=new MediaInfo();
            mediaInfo.setId(musicData.getAudio_id());
            mediaInfo.setVideo_desp(musicData.getSongname());
            mediaInfo.setMediaAlbum(musicData.getAlbum_name());
            mediaInfo.setNickname(musicData.getSingername());
            mediaInfo.setVideo_durtion(musicData.getDuration());
            mediaInfo.setFile_path(musicData.getSource());
            mediaInfo.setFile_size(musicData.getFilesize());
            return mediaInfo;
        }
        return null;
    }

    /**
     * 转换音频对象
     * @param musicData
     * @param audioID 多媒体文件ID
     * @return
     */
    private MediaInfo getMediaInfo(SearchMusicData musicData,long audioID) {
        if(null!=musicData){
            MediaInfo mediaInfo=new MediaInfo();
            mediaInfo.setId(audioID);
            mediaInfo.setFile_path(musicData.getPlay_url());
            mediaInfo.setVideo_desp(musicData.getAudio_name());
            mediaInfo.setMediaAlbum(musicData.getAlbum_name());
            mediaInfo.setImg_path(musicData.getImg());
            mediaInfo.setNickname(musicData.getAuthor_name());
            mediaInfo.setVideo_durtion(musicData.getTimelength());
            mediaInfo.setFile_size(musicData.getFilesize());
            mediaInfo.setAvatar(musicData.getImg());
            mediaInfo.setHashKey(musicData.getHash());
            if(null!=musicData.getAuthors()&&musicData.getAuthors().size()>0){
                SearchMusicAnchor searchMusicAnchor = musicData.getAuthors().get(0);
                mediaInfo.setAvatar(searchMusicAnchor.getAvatar());
            }
            return mediaInfo;
        }
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=mAdapter&&o instanceof MusicSubjectObservable && null!=arg && arg instanceof MusicStatus){
            MusicStatus musicStatus= (MusicStatus) arg;
            if(MusicStatus.PLAYER_STATUS_DESTROY==musicStatus.getPlayerStatus()
                    ||MusicStatus.PLAYER_STATUS_STOP==musicStatus.getPlayerStatus()){
                if(null!=mAdapter.getData()&&mAdapter.getData().size()>mAdapter.getCurrentPosition()){
                    mAdapter.getData().get(mAdapter.getCurrentPosition()).setSelected(false);
                    mAdapter.notifyDataSetChanged();
                }
            }else{
                mAdapter.notifyDataSetChanged();
                int position = MediaUtils.getInstance().getNetCurrentPlayIndexInThis(mAdapter.getData(), MusicPlayerManager.getInstance().getCurrentPlayerID());
                mAdapter.setCurrentPosition(position);
            }
        }
    }

    /**
     * 拦截返回和菜单事件
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if(null!=mRecyclerView&&mRecyclerView.getVisibility()!=View.GONE){
            if(null!=mAdapter){
                mAdapter.setNewData(null);
            }
            mRecyclerView.setVisibility(View.GONE);
            mTagsRoot.setVisibility(View.VISIBLE);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(null!=mEtInput){
            MusicUtils.getInstance().closeKeybord(MusicSearchActivity.this,mEtInput);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mAdapter){
            mAdapter.onDestroy();
            mAdapter=null;
        }
        mEtInput=null;
        mSearchHistroyCount=0;
        MusicPlayerManager.getInstance().removeObserver(this);
    }
}