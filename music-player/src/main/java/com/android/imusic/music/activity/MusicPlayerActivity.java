package com.android.imusic.music.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.imusic.R;
import com.android.imusic.music.bean.MediaInfo;
import com.music.player.lib.bean.BaseMediaInfo;
import com.music.player.lib.bean.MusicStatus;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.listener.MusicAnimatorListener;
import com.music.player.lib.listener.MusicJukeBoxStatusListener;
import com.music.player.lib.listener.MusicOnItemClickListener;
import com.music.player.lib.listener.MusicPlayerEventListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.manager.MusicWindowManager;
import com.music.player.lib.model.MusicAlarmModel;
import com.music.player.lib.model.MusicPlayModel;
import com.music.player.lib.model.MusicPlayerState;
import com.music.player.lib.model.MusicPlayerStatus;
import com.music.player.lib.util.Logger;
import com.music.player.lib.util.MusicClickControler;
import com.music.player.lib.util.MusicUtils;
import com.music.player.lib.view.MusicJukeBoxBackgroundLayout;
import com.music.player.lib.view.MusicJukeBoxView;
import com.music.player.lib.view.dialog.MusicAlarmSettingDialog;
import com.music.player.lib.view.dialog.MusicPlayerListDialog;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/3/6
 * Audio Player
 * 此音频播放器工作机制说明：
 * MusicPlayerService：内部播放器服务组件，负责音频的播放、暂停、停止、上一首、下一首、闹钟定时关闭等工作
 * MusicPlayerActivity：播放器容器，监听内部播放器状态，负责处理当前正在播放的任务、刷新进度、处理MusicPlayerService抛出交互事件
 * MusicPlayerManager：内部播放器代理人，所有组件与播放器交互或指派任务给播放器，需经此代理人进行
 * MusicJukeBoxView：默认唱片机
 * MusicJukeBoxBackgroundLayout：默认播放器UI背景协调工作者
 * MusicJukeBoxCoverPager：默认唱片机封面
 * MusicAlarmSettingDialog：默认定制闹钟设置
 * MusicPlayerListDialog：默认当前正在播放的列表
 */

public class MusicPlayerActivity extends AppCompatActivity implements MusicJukeBoxStatusListener, MusicPlayerEventListener {

    private static final String TAG = "MusicPlayerActivity";
    private MusicJukeBoxView mMusicJukeBoxView;
    private SeekBar mSeekBar;
    private MusicJukeBoxBackgroundLayout mRootLayout;
    private ImageView mMusicBtnPlayPause;
    private TextView mViewTitle;
    private Handler mHandler;
    private MusicClickControler mClickControler;
    private TextView mTotalTime;
    private TextView mCurrentTime;
    private ImageView mMusicPlayerModel;
    private TextView mMusicAlarm;
    private boolean isVisibility=false;
    private boolean isTouchSeekBar=false;//手指是否正在控制seekBar
    private TextView mSubTitle;
    private ImageView mBtnCollect;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.music_activity_player);
        initViews();
        //注册播放器状态监听器
        MusicPlayerManager.getInstance().addOnPlayerEventListener(this);
        mHandler=new Handler(Looper.getMainLooper());
        mClickControler=new MusicClickControler();
        mClickControler.init(1,600);
        getIntentParams(getIntent(),true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntentParams(intent,false);
    }

    public Handler getHandler() {
        if(null==mHandler){
            mHandler=new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /**
     * 解析意图
     * @param intent
     * @param isOnCreate 是否是初始化
     */
    private void getIntentParams(Intent intent,boolean isOnCreate) {
        //Music对象
        long musicID = intent.getLongExtra(MusicConstants.KEY_MUSIC_ID,0);
        if(musicID<=0){
            Toast.makeText(MusicPlayerActivity.this,"参数错误,缺少ID",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //正在播放的对象
        BaseMediaInfo currentPlayerMusic = MusicPlayerManager.getInstance().getCurrentPlayerMusic();
        //点击了通知栏回显
        if(!isOnCreate&&null!=currentPlayerMusic&&currentPlayerMusic.getId()==musicID){
            return;
        }
        MusicWindowManager.getInstance().onInvisible();
        MusicPlayerManager.getInstance().onCheckedPlayerConfig();//检查播放器配置
        if(null!=intent.getSerializableExtra(MusicConstants.KEY_MUSIC_LIST)){
            List<MediaInfo> mediaInfos = (List<MediaInfo>) intent.getSerializableExtra(MusicConstants.KEY_MUSIC_LIST);
            final List<MediaInfo> thisMusicLists=new ArrayList<>();
            thisMusicLists.addAll(mediaInfos);
            final int index=MusicUtils.getInstance().getCurrentPlayIndex(thisMusicLists,musicID);
            if(null!=currentPlayerMusic&&currentPlayerMusic.getId()==musicID&&MusicPlayerManager.getInstance().getPlayerState()==MusicPlayerState.MUSIC_PLAYER_PLAYING){
                Logger.d(TAG,"RESET PLAY,musicID:"+musicID);
                //更新播放器内部数据
                MusicPlayerManager.getInstance().updateMusicPlayerData(thisMusicLists,index);
                onStatusResume(musicID);
            }else{
                Logger.d(TAG,"NEW PLAY,musicID:"+musicID);
                MusicPlayerManager.getInstance().onReset();
                if(null!=mSeekBar){
                    mSeekBar.setSecondaryProgress(0);
                    mSeekBar.setProgress(0);
                }
                mMusicJukeBoxView.setNewData(thisMusicLists,index);
                mMusicJukeBoxView.onStart(new MusicAnimatorListener() {
                    @Override
                    public void onAnimationStart() {}
                    @Override
                    public void onAnimationEnd() {
                        mMusicJukeBoxView.resetAnimationListener();
                        //开始播放
                        MusicPlayerManager.getInstance().startPlayMusic(thisMusicLists,index);
                    }
                });
            }
        }else{
            if(null!=currentPlayerMusic){
                onStatusResume(musicID);
                return;
            }else{
                Toast.makeText(MusicPlayerActivity.this,"播放失败，请检查播放列表",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 只是回显
     * @param musicID
     */
    private void onStatusResume(long musicID) {
        Logger.d(TAG,"onStatusResume-->musicID:"+musicID);
        List<BaseMediaInfo> currentPlayList = (List<BaseMediaInfo>) MusicPlayerManager.getInstance().getCurrentPlayList();
        int currentPlayIndex = MusicUtils.getInstance().getCurrentPlayIndex(currentPlayList, musicID);
        mMusicJukeBoxView.setNewData(currentPlayList,currentPlayIndex);
        isVisibility=true;
        //主动获取正在播放状态
        MusicPlayerManager.getInstance().onCheckedCurrentPlayTask();
    }

    /**
     * 界面初始化
     */
    private void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //播放模式
                    case R.id.music_btn_model:
                        MusicPlayerManager.getInstance().changedPlayerPlayFullModel();
                        break;
                    //上一首
                    case R.id.music_btn_last:
                        if(mClickControler.canTrigger()){
                            int lastPosition = MusicPlayerManager.getInstance().playLastIndex();
                            if(-1!=lastPosition){
                                if(Math.abs(mMusicJukeBoxView.getCurrentItem()-lastPosition)>2){
                                    mMusicJukeBoxView.setCurrentMusicItem(lastPosition,false,true);
                                }else{
                                    mMusicJukeBoxView.setCurrentMusicItem(lastPosition,true,true);
                                }
                            }
                        }
                        break;
                    //开始、暂停
                    case R.id.music_btn_play_pause:
                        if(mClickControler.canTrigger()){
                            MusicPlayerManager.getInstance().playOrPause();
                        }
                        break;
                    //下一首
                    case R.id.music_btn_next:
                        if(mClickControler.canTrigger()){
                            int nextPosition = MusicPlayerManager.getInstance().playNextIndex();
                            if(-1!=nextPosition){
                                if(Math.abs(mMusicJukeBoxView.getCurrentItem()-nextPosition)>2){
                                    mMusicJukeBoxView.setCurrentMusicItem(nextPosition,false,true);
                                }else{
                                    mMusicJukeBoxView.setCurrentMusicItem(nextPosition,true,true);
                                }
                            }
                        }
                        break;
                    //菜单
                    case R.id.music_btn_menu:
                        MusicPlayerListDialog.getInstance(MusicPlayerActivity.this).setMusicOnItemClickListener(new MusicOnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int posotion,long musicID) {
                                if(Math.abs(mMusicJukeBoxView.getCurrentItem()-posotion)>2){
                                    mMusicJukeBoxView.setCurrentMusicItem(posotion,false,true);
                                }else{
                                    mMusicJukeBoxView.setCurrentMusicItem(posotion,true,true);
                                }
                            }
                        }).show();
                        break;
                    //闹钟定时
                    case R.id.music_btn_alarm:
                        MusicAlarmSettingDialog.getInstance(MusicPlayerActivity.this).setOnAlarmModelListener(new MusicAlarmSettingDialog.OnAlarmModelListener() {
                            @Override
                            public void onAlarmModel(MusicAlarmModel alarmModel) {
                                final MusicAlarmModel musicAlarmModel = MusicPlayerManager.getInstance().setPlayerAlarmModel(alarmModel);
                                getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setPlayerConfig(null,musicAlarmModel,true);
                                    }
                                });
                            }
                        }).show();
                        break;
                    //关闭
                    case R.id.music_back:
                        onBackOutPlayer();
                        break;
                    //收藏
                    case R.id.music_top_collect:
                        if(null!=mMusicJukeBoxView&&null!=mMusicJukeBoxView.getCurrentMedia()){
                            BaseMediaInfo currentMedia = mMusicJukeBoxView.getCurrentMedia();
                            if(mBtnCollect.isSelected()){
                                boolean isSuccess = MusicUtils.getInstance().removeMusicCollectById(currentMedia.getId());
                                if(isSuccess){
                                    mBtnCollect.setSelected(false);
                                }
                            }else{
                                boolean isSuccess = MusicUtils.getInstance().putMusicToCollect(currentMedia);
                                if(isSuccess){
                                    mBtnCollect.setSelected(true);
                                    MusicPlayerManager.getInstance().observerUpdata(new MusicStatus());
                                }
                            }
                        }
                        break;
                }
            }
        };

        findViewById(R.id.music_btn_model).setOnClickListener(onClickListener);
        findViewById(R.id.music_btn_last).setOnClickListener(onClickListener);
        findViewById(R.id.music_btn_play_pause).setOnClickListener(onClickListener);
        findViewById(R.id.music_btn_next).setOnClickListener(onClickListener);
        findViewById(R.id.music_btn_menu).setOnClickListener(onClickListener);

        mMusicPlayerModel = (ImageView) findViewById(R.id.music_btn_model);
        mMusicBtnPlayPause = (ImageView) findViewById(R.id.music_btn_play_pause);
        findViewById(R.id.music_back).setOnClickListener(onClickListener);
        mMusicAlarm = (TextView) findViewById(R.id.music_btn_alarm);
        mMusicAlarm.setOnClickListener(onClickListener);
        mCurrentTime = (TextView) findViewById(R.id.music_current_time);
        mTotalTime = (TextView) findViewById(R.id.music_total_time);
        mBtnCollect = (ImageView) findViewById(R.id.music_top_collect);
        mBtnCollect.setOnClickListener(onClickListener);
        //唱片
        mMusicJukeBoxView = (MusicJukeBoxView) findViewById(R.id.music_discview);
        mSeekBar = (SeekBar) findViewById(R.id.music_seek_bar);
        mRootLayout = (MusicJukeBoxBackgroundLayout) findViewById(R.id.root_layout);
        mMusicJukeBoxView.setPlayerInfoListener(this);
        mViewTitle = (TextView) findViewById(R.id.music_title);
        mSubTitle = (TextView) findViewById(R.id.music_sub_title);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    long durtion = MusicPlayerManager.getInstance().getDurtion();
                    if(durtion>0){
                        mCurrentTime.setText(MusicUtils.getInstance().stringForAudioTime(progress * durtion / 100));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouchSeekBar=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTouchSeekBar=false;
                long durtion = MusicPlayerManager.getInstance().getDurtion();
                if(durtion>0){
                    long currentTime = seekBar.getProgress() * durtion / 100;
                    MusicPlayerManager.getInstance().onSeekTo(currentTime);
                }
            }
        });
        mClickControler = new MusicClickControler();
        mClickControler.init(3,1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisibility=true;
        //收藏状态,针对可能在锁屏界面收藏的同步
        if(null!=mBtnCollect&&null!=mMusicJukeBoxView&&null!=mMusicJukeBoxView.getCurrentMedia()){
            boolean isExist = MusicUtils.getInstance().isExistCollectHistroy(mMusicJukeBoxView.getCurrentMedia().getId());
            mBtnCollect.setSelected(isExist);
        }
        if(MusicPlayerManager.getInstance().getPlayerState()==MusicPlayerState.MUSIC_PLAYER_PLAYING){
            if(null!=mMusicBtnPlayPause) mMusicBtnPlayPause.setImageResource(R.drawable.music_player_pause_selector);
            if(null!= mMusicJukeBoxView){
                mMusicJukeBoxView.onStart();
            }
        }
        MusicWindowManager.getInstance().onInvisible();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null!= mMusicJukeBoxView){
            mMusicJukeBoxView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisibility=false;
    }

    /**
     * 获取对应播放模式ICON
     * @param playerModel
     * @param isToast 是否吐司提示
     * @return
     */
    private int getResToPlayModel(MusicPlayModel playerModel,boolean isToast) {
        String content="列表循环";
        int resID=R.drawable.music_player_model_loop_selector;
        if(playerModel.equals(MusicPlayModel.MUSIC_MODEL_SINGLE)){
            content="单曲循环";
            resID=R.drawable.music_player_model_single_selector;
        }else if(playerModel.equals(MusicPlayModel.MUSIC_MODEL_LOOP)){
            content="列表循环";
            resID=R.drawable.music_player_model_loop_selector;
        }else if(playerModel.equals(MusicPlayModel.MUSIC_MODEL_RANDOM)){
            content="随机播放";
            resID=R.drawable.ic_music_lock_model_random;
            mMusicPlayerModel.setColorFilter(Color.parseColor("#FFFFFF"));
        }
        if(isToast){
            Toast.makeText(MusicPlayerActivity.this,content,Toast.LENGTH_SHORT).show();
        }
        return resID;
    }

    //========================================唱片机内部状态==========================================
    /**
     * 用户手指滑动触发
     * @param mediaInfo 音频对象
     */
    @Override
    public void onJukeBoxOffsetObject(BaseMediaInfo mediaInfo) {
        Logger.d(TAG,"onJukeBoxOffsetObject:MUSIC_ID："+mediaInfo.getId());
        mViewTitle.setText(mediaInfo.getVideo_desp());
        mSubTitle.setText(mediaInfo.getNickname());
    }

    /**
     * 唱片机切换了音频对象
     * @param position 索引
     * @param mediaInfo 音频对象
     * @param isEchoDisplay 是否回显
     */
    @Override
    public void onJukeBoxObjectChanged(final int position, BaseMediaInfo mediaInfo, boolean isEchoDisplay) {
        Logger.d(TAG,"onJukeBoxObjectChanged-->POSITION:"+position+",MUSIC_INFO:"+mediaInfo.getId()+",isEchoDisplay:"+isEchoDisplay);
        //清空唱片机播放器
        if(null!=mediaInfo){
            mViewTitle.setText(mediaInfo.getVideo_desp());
            mSubTitle.setText(mediaInfo.getNickname());
            mTotalTime.setText(MusicUtils.getInstance().stringForAudioTime(mediaInfo.getVideo_durtion()));
            //收藏状态
            boolean isExist = MusicUtils.getInstance().isExistCollectHistroy(mediaInfo.getId());
            mBtnCollect.setSelected(isExist);
            mRootLayout.setBackgroundCover(MusicUtils.getInstance().getMusicFrontPath(mediaInfo),1200);
            //非回显事件，释放原有播放器并开始播放
            if(!isEchoDisplay){
                mCurrentTime.setText("00:00");
                mSeekBar.setSecondaryProgress(0);
                mSeekBar.setProgress(0);
                //先还原内部播放器，避免界面绘制BUG
                MusicPlayerManager.getInstance().onReset();
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //切换音频
                        MusicPlayerManager.getInstance().startPlayMusic(position);
                    }
                },200);
            }
        }
    }

    /**
     * @param playerState 唱片机内部状态
     */
    @Override
    public void onJukeBoxState(MusicPlayerStatus playerState) {
//        Logger.d(TAG,"onJukeBoxState-->JUKEBOX_STATE:"+playerState);
//        if(null!=mMusicBtnPlayPause){
//            if(playerState==MusicPlayerStatus.PLAY){
//                mMusicBtnPlayPause.setImageResource(R.drawable.music_player_pause_selector);
//            }else if(playerState==MusicPlayerStatus.PAUSE){
//                mMusicBtnPlayPause.setImageResource(R.drawable.music_player_play_selector);
//            }
//        }
    }

    //========================================播放器内部状态==========================================

    /**
     * 播放器内部状态
     * @param playerState 播放器内部状态
     * @param message
     */
    @Override
    public void onMusicPlayerState(final MusicPlayerState playerState, final String message) {
        Logger.d(TAG,"onMusicPlayerState-->"+playerState);
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (playerState.equals(MusicPlayerState.MUSIC_PLAYER_ERROR)&&!TextUtils.isEmpty(message)) {
                    Toast.makeText(MusicPlayerActivity.this,message,Toast.LENGTH_SHORT).show();
                }
                switch (playerState) {
                    case MUSIC_PLAYER_PREPARE:
                        if (null != mMusicAlarm && !MusicPlayerManager.getInstance().getPlayerAlarmModel().equals(MusicAlarmModel.MUSIC_ALARM_MODEL_0)) {
                            Drawable drawable = getResources().getDrawable(R.drawable.ic_music_alarm_pre);
                            mMusicAlarm.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                            mMusicAlarm.setTextColor(Color.parseColor("#F8E71C"));
                        }
                        if (null != mMusicBtnPlayPause) mMusicBtnPlayPause.setImageResource(R.drawable.music_player_pause_selector);
                        if (null != mMusicJukeBoxView) mMusicJukeBoxView.onStart();
                        break;
                    case MUSIC_PLAYER_BUFFER:

                        break;
                    case MUSIC_PLAYER_PLAYING:
                        if (null != mMusicBtnPlayPause)
                            mMusicBtnPlayPause.setImageResource(R.drawable.music_player_pause_selector);
                        if (null != mMusicJukeBoxView) mMusicJukeBoxView.onStart();
                        break;
                    case MUSIC_PLAYER_PAUSE:
                        if (null != mMusicBtnPlayPause)
                            mMusicBtnPlayPause.setImageResource(R.drawable.music_player_play_selector);
                        if (null != mMusicJukeBoxView) mMusicJukeBoxView.onPause();
                        break;
                    case MUSIC_PLAYER_STOP:
                        if (null != mMusicBtnPlayPause) mMusicBtnPlayPause.setImageResource(R.drawable.music_player_play_selector);
                        if (null != mCurrentTime) mCurrentTime.setText("00:00");
                        if(null!=mSeekBar){
                            mSeekBar.setSecondaryProgress(0);
                            mSeekBar.setProgress(0);
                        }
                        if(null!=mMusicAlarm){
                            Drawable drawable = getResources().getDrawable(R.drawable.ic_music_alarm_noimal);
                            mMusicAlarm.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                            mMusicAlarm.setTextColor(Color.parseColor("#FFFFFF"));
                            mMusicAlarm.setText("定时关闭");
                        }
                        if (null != mMusicJukeBoxView) mMusicJukeBoxView.onStop();
                        break;
                    case MUSIC_PLAYER_ERROR:
                        if (null != mMusicBtnPlayPause){
                            mMusicBtnPlayPause.setImageResource(R.drawable.music_player_play_selector);
                        }
                        if (null != mSeekBar) {
                            mSeekBar.setSecondaryProgress(0);
                            mSeekBar.setProgress(0);
                        }
                        if (null != mCurrentTime){
                            mCurrentTime.setText("00:00");
                        }
                        if (null != mMusicJukeBoxView){
                            mMusicJukeBoxView.onPause();
                        }
                        break;
                }
            }
        });
    }

    /**
     * 播放器准备完毕
     * @param totalDurtion 总时长
     */
    @Override
    public void onPrepared(final long totalDurtion) {
        Logger.d(TAG,"onPrepared:totalDurtion:"+totalDurtion);
        if(null!=mTotalTime){
            mTotalTime.setText(MusicUtils.getInstance().stringForAudioTime(totalDurtion));
        }
        if(isVisibility&&null!=mMusicJukeBoxView){
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mMusicJukeBoxView.onStart();
                }
            });
        }
    }

    /**
     * 缓冲进度
     * @param percent 百分比
     */
    @Override
    @Deprecated
    public void onBufferingUpdate(final int percent) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if(null!=mSeekBar&&mSeekBar.getSecondaryProgress()<100){
                    mSeekBar.setSecondaryProgress(percent);
                }
            }
        });
    }

    @Override
    public void onInfo(int event, int extra) {
        Logger.d(TAG,"onInfo-->event:"+event+",extra:"+extra);
    }

    /**
     * 内部播放器正在处理的对象发生了变化，这里接收到回调只负责定位，数据更新应以唱片机回调状态为准
     * @param musicInfo 正在播放的对象
     * @param position 当前正在播放的位置
     */
    @Override
    public void onPlayMusiconInfo(BaseMediaInfo musicInfo,final int position) {
        Logger.d(TAG,"onPlayMusiconInfo-->MUSIC_INFO:"+musicInfo.getId()+",POSITION:"+position);
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if(null!= mMusicJukeBoxView){
                    mMusicJukeBoxView.setCurrentMusicItem(position,false,false);
                }
            }
        });
    }

    /**
     * 回显播放器内部正在处理的对象位置
     * @param musicInfo 音频对象
     * @param position 内部播放器正在处理的对象位置,相对于当前播放队列
     */
    @Override
    public void onEchoPlayCurrentIndex(BaseMediaInfo musicInfo, final int position) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if(null!= mMusicJukeBoxView){
                    mMusicJukeBoxView.setCurrentMusicItem(position,false,false,true);
                }
            }
        });
    }

    /**
     * 播放地址无效,播放器内部会停止工作，回调至此交由组件处理业务逻辑
     * 若购买成功，调用 MusicPlayerManager.getInstance().continuePlay(String sourcePath);继续
     * @param musicInfo 播放对象
     * @param position 索引
     */
    @Override
    public void onMusicPathInvalid(BaseMediaInfo musicInfo, int position) {
        Logger.d(TAG,"onMusicPathInvalid-->MUSIC_INFO:"+musicInfo.getId()+",POSITION:"+position);
        if(null!=mMusicJukeBoxView){
            mMusicJukeBoxView.onPause();
        }
    }

    /**
     * 闹钟剩余时长、音频时长、播放进度回调
     * @param totalDurtion
     * @param currentDurtion
     * @param alarmResidueDurtion
     */
    @Override
    public void onTaskRuntime(final long totalDurtion, final long currentDurtion, final long alarmResidueDurtion,int bufferProgress) {
        updataPlayerParams(totalDurtion,currentDurtion,alarmResidueDurtion,bufferProgress);
    }

    /**
     * 播放器配置
     * @param playModel 播放模式
     * @param alarmModel 闹钟模式
     * @param isToast 是否吐司提示
     */
    @Override
    public void onPlayerConfig(final MusicPlayModel playModel, final MusicAlarmModel alarmModel, final boolean isToast) {
        Logger.d(TAG,"onPlayerConfig--:playModel"+playModel+",alarmModel:"+alarmModel);
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                setPlayerConfig(playModel,alarmModel,isToast);
            }
        });
    }

//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(0, R.anim.music_bottom_menu_exit);
//    }

    /**
     * 播放器配置
     * @param playModel
     * @param alarmModel
     */
    private synchronized void setPlayerConfig(MusicPlayModel playModel, MusicAlarmModel alarmModel,boolean isToast) {
        if(null!=playModel&&null!=mMusicPlayerModel){
            mMusicPlayerModel.setImageResource(getResToPlayModel(playModel,isToast));
        }
        if(null!=mMusicAlarm&&null!=alarmModel){
            if(alarmModel.equals(MusicAlarmModel.MUSIC_ALARM_MODEL_0)){
                Drawable drawable = getResources().getDrawable(R.drawable.ic_music_alarm_noimal);
                mMusicAlarm.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                mMusicAlarm.setText("定时关闭");
                mMusicAlarm.setTextColor(Color.parseColor("#FFFFFF"));
            }else {
                Drawable drawable = getResources().getDrawable(R.drawable.ic_music_alarm_pre);
                mMusicAlarm.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                //这里不想去浪费资源运算了
                String durtion="00:00";
                if(alarmModel.equals(MusicAlarmModel.MUSIC_ALARM_MODEL_10)){
                    durtion="10:00";
                }else if(alarmModel.equals(MusicAlarmModel.MUSIC_ALARM_MODEL_15)){
                    durtion="15:00";
                }else if(alarmModel.equals(MusicAlarmModel.MUSIC_ALARM_MODEL_30)){
                    durtion="30:00";
                }else if(alarmModel.equals(MusicAlarmModel.MUSIC_ALARM_MODEL_60)){
                    durtion="01:00:00";
                }else if(alarmModel.equals(MusicAlarmModel.MUSIC_ALARM_MODEL_CURRENT)){
                    durtion="00:00";
                }
                mMusicAlarm.setText(durtion);
                mMusicAlarm.setTextColor(Color.parseColor("#F8E71C"));
            }
        }
    }

    /**
     * 更新播放器数据
     * @param totalDurtion
     * @param currentDurtion
     * @param alarmResidueDurtion
     * @param bufferProgress
     */
    private synchronized void updataPlayerParams(final long totalDurtion, final long currentDurtion, final long alarmResidueDurtion, final int bufferProgress) {
        if(isVisibility&&null!=mSeekBar){
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if(mSeekBar.getSecondaryProgress()<100){
                        mSeekBar.setSecondaryProgress(bufferProgress);
                    }
                    //缓冲、播放进度
                    if(totalDurtion>-1){
                        if(!isTouchSeekBar){
                            int progress = (int) (((float) currentDurtion / totalDurtion) * 100);// 得到当前进度
                            mSeekBar.setProgress(progress);
                        }
                        if(null!=mTotalTime){
                            mTotalTime.setText(MusicUtils.getInstance().stringForAudioTime(totalDurtion));
                            mCurrentTime.setText(MusicUtils.getInstance().stringForAudioTime(currentDurtion));
                        }
                    }
                    //定时闹钟状态
                    if(alarmResidueDurtion<=0){
                        if(null!=mMusicAlarm){
                            Drawable drawable = getResources().getDrawable(R.drawable.ic_music_alarm_noimal);
                            mMusicAlarm.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                            mMusicAlarm.setTextColor(Color.parseColor("#FFFFFF"));
                            mMusicAlarm.setText("定时关闭");
                        }
                        return;
                    }
                    if(alarmResidueDurtion>-1&&alarmResidueDurtion <= (60 * 60)){
                        String audioTime = MusicUtils.getInstance().stringForAudioTime(alarmResidueDurtion*1000);
                        if(null!=mMusicAlarm) mMusicAlarm.setText(audioTime);
                    }
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackOutPlayer();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 即将退出播放器
     */
    private void onBackOutPlayer() {
        if(!MusicWindowManager.getInstance().checkAlertWindowsPermission(MusicPlayerActivity.this)){
            new android.support.v7.app.AlertDialog.Builder(MusicPlayerActivity.this)
                    .setTitle("退出播放器提示")
                    .setMessage("后台播放需要开启悬浮窗播放功能，请前往系统设置开启悬浮窗权限")
                    .setNegativeButton("停止播放", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MusicPlayerManager.getInstance().onStop();
                            finish();
                        }
                    })
                    .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setData(Uri.parse( "package:"+MusicUtils.getInstance().getPackageName(MusicPlayerActivity.this)));
                                    MusicPlayerActivity.this.startActivityForResult(intent,MusicConstants.REQUST_WINDOWN_PERMISSION);
                                } else {
                                    Toast.makeText(MusicPlayerActivity.this,"请在设置中手动开启",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                                    startActivityForResult(intent,MusicConstants.REQUST_WINDOWN_PERMISSION);
                                }
                            }catch (RuntimeException e){
                                e.printStackTrace();
                            }
                        }
                    }).setCancelable(false).show();
            return;
        }
        createMiniJukeBoxToWindown();
    }

    /**
     * 创建一个全局的迷你唱片至窗口
     */
    private void createMiniJukeBoxToWindown() {
        if(!MusicWindowManager.getInstance().isWindowShowing()){
            if(null!=MusicPlayerManager.getInstance().getCurrentPlayerMusic()){
                BaseMediaInfo musicInfo = MusicPlayerManager.getInstance().getCurrentPlayerMusic();
                MusicWindowManager.getInstance().createMiniJukeBoxToWindown(MusicPlayerActivity.this.getApplicationContext(),MusicUtils.getInstance().dpToPxInt(MusicPlayerActivity.this,80f),MusicUtils.getInstance().dpToPxInt(MusicPlayerActivity.this,170f));
                MusicStatus musicStatus=new MusicStatus();
                musicStatus.setId(musicInfo.getId());
                String frontPath=MusicUtils.getInstance().getMusicFrontPath(musicInfo);
                musicStatus.setCover(frontPath);
                musicStatus.setTitle(musicInfo.getVideo_desp());
                MusicPlayerState playerState = MusicPlayerManager.getInstance().getPlayerState();
                boolean playing = playerState.equals(MusicPlayerState.MUSIC_PLAYER_PLAYING) || playerState.equals(MusicPlayerState.MUSIC_PLAYER_PREPARE) || playerState.equals(MusicPlayerState.MUSIC_PLAYER_BUFFER);
                musicStatus.setPlayerStatus(playing?MusicStatus.PLAYER_STATUS_START:MusicStatus.PLAYER_STATUS_PAUSE);
                MusicWindowManager.getInstance().updateWindowStatus(musicStatus);
            }
        }
        //此处手动显示一把，避免悬浮窗还未成功创建
        MusicWindowManager.getInstance().onVisible();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(MusicWindowManager.getInstance().checkAlertWindowsPermission(MusicPlayerActivity.this)){
            createMiniJukeBoxToWindown();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isVisibility=false;
        if(null!=mHandler){
            mHandler.removeMessages(0);
            mHandler=null;
        }
        MusicPlayerManager.getInstance().removePlayerListener(this);
        if(null!= mMusicJukeBoxView){
            mMusicJukeBoxView.onDestroy();
            mMusicJukeBoxView =null;
        }
        if(null!=mRootLayout){
            mRootLayout.onDestroy();
            mRootLayout=null;
        }
        isTouchSeekBar=false;
    }
}