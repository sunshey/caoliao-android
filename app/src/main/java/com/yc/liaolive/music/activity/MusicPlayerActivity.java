package com.yc.liaolive.music.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.music.player.lib.bean.BaseMediaInfo;
import com.music.player.lib.bean.MusicStatus;
import com.music.player.lib.constants.MusicConstants;
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
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.bean.MediaFileInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.live.ui.dialog.EarphoneTipsDialog;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.ConfigSet;
import com.yc.liaolive.music.bean.MediaInfo;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import com.yc.liaolive.videocall.ui.activity.CallRechargeActivity;
import com.yc.liaolive.videocall.ui.dialog.QuireVideoDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2019/3/6
 * Audio Player
 */

public class MusicPlayerActivity extends TopBaseActivity implements MusicJukeBoxStatusListener, MusicPlayerEventListener, Observer {

    private static final String TAG = "MusicPlayerActivity";
    private MusicJukeBoxView mMusicJukeBoxView;
    private SeekBar mSeekBar;
    private MusicJukeBoxBackgroundLayout mRootLayout;
    private ImageView mMusicBtnPlayPause;
    private int mPisition;
    private TextView mViewTitle;
    private Handler mHandler;
    private MusicClickControler mClickControler;
    private TextView mTotalTime;
    private TextView mCurrentTime;
    private ImageView mMusicPlayerModel;
    private TextView mMusicAlarm;
    private boolean isVisibility=false;
    private boolean isShowHeadphone=false;//耳机提示
    private boolean isTouchSeekBar=false;//手指是否正在控制seekBar

    private QuireDialog rechageDialog;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFullScreen(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity_player);
        ApplicationManager.getInstance().addObserver(this);
        VideoCallManager.getInstance().setBebusying(true);
        initViews();
        //注册播放器状态监听器
        MusicPlayerManager.getInstance().addOnPlayerEventListener(this);
        mHandler=new Handler(Looper.getMainLooper());
        mClickControler = new MusicClickControler();
        mClickControler.init(1,600);
        getIntentParams(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntentParams(intent);
    }

    public Handler getHandler() {
        if(null==mHandler){
            mHandler=new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    private void getIntentParams(Intent intent) {
        MusicPlayerManager.getInstance().onCheckedPlayerConfig();//检查播放器配置
        //Music对象
        long musicID = intent.getLongExtra(MusicConstants.KEY_MUSIC_ID,0);
        if(null!=intent.getSerializableExtra(MusicConstants.KEY_MUSIC_LIST)){
            List<MediaInfo> mediaInfos = (List<MediaInfo>) intent.getSerializableExtra(MusicConstants.KEY_MUSIC_LIST);
            final List<MediaInfo> thisMusicLists=new ArrayList<>();
            thisMusicLists.addAll(mediaInfos);
            //正在播放的对象
            BaseMediaInfo currentPlayerMusic = MusicPlayerManager.getInstance().getCurrentPlayerMusic();
            final int index=MusicUtils.getInstance().getCurrentPlayIndex(thisMusicLists,musicID);
            if(null!=currentPlayerMusic&&currentPlayerMusic.getId()==musicID&&MusicPlayerManager.getInstance().getPlayerState()==MusicPlayerState.MUSIC_PLAYER_PLAYING){
                Logger.d(TAG,"任务对象相同，只替换播放器内部数据和回显,musicID:"+musicID);
                //更新播放器内部数据
                MusicPlayerManager.getInstance().updateMusicPlayerData(thisMusicLists,index);
                onStatusResume(musicID);
            }else{
                Logger.d(TAG,"新播放,musicID:"+musicID);
                mMusicJukeBoxView.setNewData(thisMusicLists,index);
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //开始播放
                        MusicPlayerManager.getInstance().startPlayMusic(thisMusicLists,index);
                    }
                },500);
            }
        }else{
            BaseMediaInfo currentPlayerMusic = MusicPlayerManager.getInstance().getCurrentPlayerMusic();
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
     */
    private void onStatusResume(long musicID) {
        Logger.d(TAG,"onStatusResume界面回显事件--> musicID = "+musicID);
        List<BaseMediaInfo> currentPlayList = (List<BaseMediaInfo>) MusicPlayerManager.getInstance().getCurrentPlayList();
        int currentPlayIndex = MusicUtils.getInstance().getCurrentPlayIndex(currentPlayList, musicID);
        Logger.d(TAG,"onStatusResume界面回显事件-->");
        mMusicJukeBoxView.setNewData(currentPlayList,currentPlayIndex);
        isVisibility=true;
        //主动获取正在播放状态
        MusicPlayerManager.getInstance().onCheckedCurrentPlayTask();
        if (MusicPlayerManager.getInstance().isReBrowse()) {
            reCheckBuyMedia();
        }
    }

    /**
     * 界面初始化
     */
    private void initViews() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //播放模式
                    case R.id.music_btn_model:
                        MusicPlayerManager.getInstance().changedPlayerPlayModel();
                        break;
                    //上一首
                    case R.id.music_btn_last:
                        if(mClickControler.canTrigger()){
                            int lastPosition = MusicPlayerManager.getInstance().playLastIndex();
                            Logger.d(TAG,"initViews--lastPosition:"+lastPosition);
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
                            Logger.d(TAG,"initViews--nextPosition:"+nextPosition);
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
                                Logger.d(TAG, "alarmModel:" + alarmModel);
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
        //唱片
        mMusicJukeBoxView = (MusicJukeBoxView) findViewById(R.id.music_discview);
        mSeekBar = (SeekBar) findViewById(R.id.music_seek_bar);
        mRootLayout = (MusicJukeBoxBackgroundLayout) findViewById(R.id.root_layout);
        mMusicJukeBoxView.setPlayerInfoListener(this);
        mViewTitle = (TextView) findViewById(R.id.music_title);
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
        if(MusicPlayerManager.getInstance().getPlayerState()==MusicPlayerState.MUSIC_PLAYER_PLAYING){
            if(null!=mMusicBtnPlayPause) mMusicBtnPlayPause.setImageResource(R.drawable.music_player_pause_selector);
            if(null!= mMusicJukeBoxView){
                Logger.d(TAG,"唱片机，onStart");
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
        if(null!=mediaInfo&&null!=mViewTitle){
            mViewTitle.setText(mediaInfo.getVideo_desp());
        }
    }

    /**
     * 切换了音频对象
     * @param position 索引
     * @param mediaInfo 音频对象
     * @param isEchoDisplay 是否回显
     */
    @Override
    public void onJukeBoxObjectChanged(final int position, BaseMediaInfo mediaInfo, boolean isEchoDisplay) {
        Logger.d(TAG,"DISC_NEW_MUSIC-->POSITION:"+position+",MUSIC_INFO:"+mediaInfo.getId()+",isEchoDisplay:"+isEchoDisplay);
        mIsBuy=0;
        if(null!=mediaInfo){
            mViewTitle.setText(mediaInfo.getVideo_desp());
            mTotalTime.setText(MusicUtils.getInstance().stringForAudioTime(mediaInfo.getVideo_durtion()));
            mRootLayout.setBackgroundCover(MusicUtils.getInstance().getMusicFrontPath(mediaInfo),0);
            if(!isEchoDisplay){
                mCurrentTime.setText("00:00");
                mSeekBar.setSecondaryProgress(0);
                mSeekBar.setProgress(0);
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
        Logger.d(TAG,"DISC_STATUS:"+playerState);
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
                        if (null != mMusicBtnPlayPause)
                            mMusicBtnPlayPause.setImageResource(R.drawable.music_player_pause_selector);
                        if (null != mMusicJukeBoxView) mMusicJukeBoxView.onStart();
                        Logger.d(TAG,"播放器，onStart1");
                        break;
                    case MUSIC_PLAYER_BUFFER:

                        break;
                    case MUSIC_PLAYER_PLAYING:
                        if (null != mMusicBtnPlayPause)
                            mMusicBtnPlayPause.setImageResource(R.drawable.music_player_pause_selector);
                        if (null != mMusicJukeBoxView) mMusicJukeBoxView.onStart();
                        Logger.d(TAG,"播放器，onStart2");
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
    public void onPrepared(long totalDurtion) {
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
    public void onBufferingUpdate(int percent) {
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
        Logger.d(TAG,"onMusicPlayMusicInfo-->MUSIC_INFO:"+musicInfo.getId()+",POSITION:"+position);
        mIsBuy=0;
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
    public void onEchoPlayCurrentIndex(BaseMediaInfo musicInfo,int position) {
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
        mIsBuy=0;
        if(null!=mMusicJukeBoxView){
            mMusicJukeBoxView.onPause();
        }
        if(null==musicInfo){
            return;
        }
        buyMediaSource(musicInfo);
    }

    /**
     * 闹钟剩余时长、音频时长、播放进度回调
     * @param totalDurtion 音频总时间
     * @param currentDurtion 当前播放的位置
     * @param alarmResidueDurtion 闹钟剩余时长
     * @param bufferProgress 当前缓冲进度
     */
    @Override
    public void onTaskRuntime(long totalDurtion, long currentDurtion, long alarmResidueDurtion,int bufferProgress) {
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
     * @param isToast 是否吐司提示
     */
    private synchronized void setPlayerConfig(MusicPlayModel playModel, MusicAlarmModel alarmModel,boolean isToast) {
        if(null!=playModel&&null!=mMusicPlayerModel){
            mMusicPlayerModel.setImageResource(getResToPlayModel(playModel,isToast));
        }
        if(null!=mMusicPlayerModel&&null!=playModel){
            if(playModel.equals(MusicPlayModel.MUSIC_MODEL_SINGLE)){
                mMusicPlayerModel.setImageResource(R.drawable.music_player_model_single_selector);
            }else if(playModel.equals(MusicPlayModel.MUSIC_MODEL_LOOP)){
                mMusicPlayerModel.setImageResource(R.drawable.music_player_model_loop_selector);
            }else if(playModel.equals(MusicPlayModel.MUSIC_MODEL_RANDOM)){
                mMusicPlayerModel.setImageResource(R.drawable.ic_music_lock_model_random);
                mMusicPlayerModel.setColorFilter(Color.parseColor("#FFFFFF"));
            }
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
    private synchronized void updataPlayerParams(final long totalDurtion, final long currentDurtion, final long alarmResidueDurtion, int bufferProgress) {
        //Logger.d(TAG,"updataPlayerParams :totalDurtion:"+totalDurtion+",currentDurtion:"+currentDurtion+",alarmResidueDurtion:"+alarmResidueDurtion);
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

    //===========================================业务交互============================================

    /**
     * 购买资源
     * @param musicInfo
     */
    private void buyMediaSource(BaseMediaInfo musicInfo) {
        showProgressDialog("获取音频信息中...",false);
        if(null==musicInfo){
            musicInfo=MusicPlayerManager.getInstance().getCurrentPlayerMusic();
        }
        //付费点播
        UserManager.getInstance().browseMediaFile(musicInfo.getId(), musicInfo.getUserid(), Constant.MEDIA_AUDIO_LIST,mIsBuy,new UserServerContract.OnCallBackListener() {
            @Override
            public void onSuccess(int code,Object object,String msg) {
                if(!MusicPlayerActivity.this.isFinishing()){
                    closeProgressDialog();
                    if(null!=object&& object instanceof MediaFileInfo){
                        MediaFileInfo data= (MediaFileInfo) object;
                        if(!TextUtils.isEmpty(data.getFile_path())){
                            Logger.d(TAG,"播放地址已购买");
                            if(!isShowHeadphone&&!TextUtils.isEmpty(data.getHeadset_img())){
                                EarphoneTipsDialog dialog = EarphoneTipsDialog.newInstance(
                                        MusicPlayerActivity.this, data.getHeadset_img());
                                isShowHeadphone=true;
                                dialog.show();
                            }
                            //显示耳机提示的同时开始播放
                            MusicPlayerManager.getInstance().continuePlay(data.getFile_path());
                            return;
                        }
                        //余额不足
                        if(NetContants.API_RESULT_ARREARAGE_CODE==code){
                            onRechgre(msg);
                            return;
                        }
                        //需要购买
                        if(NetContants.API_RESULT_BUY==code){
                            onBuyTips(msg);
                            return;
                        }
                        //其他错误信息
                        ToastUtils.showCenterToast(msg);
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if(!MusicPlayerActivity.this.isFinishing()){
                    closeProgressDialog();
                    //文件未找到
                    if(NetContants.API_RESULT_CANT_FIND==code){
                        onCantFind(errorMsg);
                        return;
                    }
                    ToastUtils.showCenterToast(errorMsg);
                }
            }
        });
    }

    /**
     * 未找到文件
     * @param msg
     */
    private void onCantFind(String msg) {
        if(this.isFinishing()) return;
        QuireVideoDialog.getInstance(MusicPlayerActivity.this)
                .setTipsData(msg,null,"确定")
                .show();
    }


    /**
     * 购买钻石
     * @param message
     */
    private void onRechgre(String message) {
        if(this.isFinishing()) return;
        rechageDialog = QuireDialog.getInstance(MusicPlayerActivity.this)
                .setTitleText("钻石不足")
                .setContentTextColor(getResources().getColor(R.color.tab_text_unselector_color))
                .showCloseBtn(true)
                .setContentText(message)
//                .setContentText("<font color='#FF6666'>观看需打赏15000钻石<br/></font><font color='#666666'>开通VIP海量ASMR资源</font><font color='#FF0000'>免费看</font>")
                .setSubmitTitleText("开通会员")
                .setSubmitTitleTextColor(getResources().getColor(R.color.app_style))
                .setCancelTitleText("充值钻石")
                .setCancelTitleTextColor(getResources().getColor(R.color.common_h33))
                .setBtnClickDismiss(false)
                .setDialogCancelable(false)
                .setDialogCanceledOnTouchOutside(false)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        VipActivity.startForResult(MusicPlayerActivity.this,1, "amsr_music");
                        MobclickAgent.onEvent(MusicPlayerActivity.this, "amsr_music_recharge_tips_vip_click");
                    }

                    @Override
                    public void onRefuse() {
                        CallRechargeActivity.start(MusicPlayerActivity.this, 20, null);
                        MobclickAgent.onEvent(MusicPlayerActivity.this, "amsr_music_recharge_tips_diamonds_click");
                    }

                    @Override
                    public void onCloseDialog() {
                        MobclickAgent.onEvent(MusicPlayerActivity.this, "amsr_music_recharge_tips_close_click");
                        finish();
                    }
                });
        rechageDialog.show();
    }

    private int mIsBuy;//是否询问过

    /**
     * 购买询问
     * @param msg  "<font color='#2A2A2A'>观看此视频需要打赏"+1500+"钻石</font>"
     */
    private void onBuyTips(String msg) {
        if(this.isFinishing()) return;
        QuireDialog.getInstance(MusicPlayerActivity.this)
                .showTitle(false)
                .setContentTextColor(getResources().getColor(R.color.tab_text_unselector_color))
                .setContentText(msg)
                .setSubmitTitleText("确定")
                .setSubmitTitleTextColor(getResources().getColor(R.color.app_style))
                .setCancelTitleText("取消")
                .setCancelTitleTextColor(getResources().getColor(R.color.common_h33))
                .setDialogCancelable(false)
                .setDialogCanceledOnTouchOutside(false)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        mIsBuy ++;
                        buyMediaSource(null);
                        MobclickAgent.onEvent(MusicPlayerActivity.this, "amsr_music_pay_tips_ok_click");
                    }

                    @Override
                    public void onRefuse() {
                        MobclickAgent.onEvent(MusicPlayerActivity.this, "amsr_music_pay_tips_cancel_click");
                        finish();
                    }
                }).show();
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
        if(ConfigSet.getInstance().isAudioOpenWindown()){
            if(!MusicWindowManager.getInstance().checkAlertWindowsPermission(MusicPlayerActivity.this)){
                QuireDialog.getInstance(MusicPlayerActivity.this)
                        .setTitleText("开启悬浮播放功能")
                        .setContentText("<font>新增悬浮播放功能，需要您在[系统设置]<br/>中手动开通[系统权限],取消后<br/>关闭悬浮播放功能</font>")
                        .setSubmitTitleText("立即开启")
                        .setCancelTitleText("暂不开启")
                        .setDialogCanceledOnTouchOutside(false)
                        .setDialogCancelable(false)
                        .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                            @Override
                            public void onConsent() {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setData(Uri.parse( "package:"+MusicUtils.getInstance().getPackageName(MusicPlayerActivity.this)));
                                    MusicPlayerActivity.this.startActivityForResult(intent,MusicConstants.REQUST_WINDOWN_PERMISSION);
                                } else {
                                    ToastUtils.showCenterToast("请在设置中手动开启");
                                    ConfigSet.getInstance().setAudioOpenWindown(true);
                                    createMiniJukeBoxToWindown();
                                }
                            }

                            @Override
                            public void onRefuse() {
                                //禁用悬浮功能
                                ConfigSet.getInstance().setAudioOpenWindown(false);
                                MusicPlayerManager.getInstance().onStop();
                                finish();
                            }
                        }).show();
                return;
            }
            createMiniJukeBoxToWindown();
            return;
        }
        MusicPlayerManager.getInstance().onStop();
        finish();
    }

    /**
     * 创建一个全局的迷你唱片至窗口
     */
    private void createMiniJukeBoxToWindown() {
        if(null!=MusicPlayerManager.getInstance().getCurrentPlayerMusic()){
            if(!MusicWindowManager.getInstance().isWindowShowing()){
                BaseMediaInfo musicInfo = MusicPlayerManager.getInstance().getCurrentPlayerMusic();
                MusicWindowManager.getInstance().createMiniJukeBoxToWindown(MusicPlayerActivity.this.getApplicationContext(),MusicUtils.getInstance().dpToPxInt(MusicPlayerActivity.this,80f),MusicUtils.getInstance().dpToPxInt(MusicPlayerActivity.this,152f));
                MusicStatus musicStatus=new MusicStatus();
                musicStatus.setId(musicInfo.getId());
                String frontPath=MusicUtils.getInstance().getMusicFrontPath(musicInfo);
                musicStatus.setCover(frontPath);
                musicStatus.setTitle(musicInfo.getVideo_desp());
                MusicPlayerState playerState = MusicPlayerManager.getInstance().getPlayerState();
                boolean playing = playerState.equals(MusicPlayerState.MUSIC_PLAYER_PLAYING) || playerState.equals(MusicPlayerState.MUSIC_PLAYER_PREPARE) || playerState.equals(MusicPlayerState.MUSIC_PLAYER_BUFFER);
                Logger.d(TAG,"createWindownJukeBox:"+playing);
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
        Logger.d(TAG,"onActivityResult--requestCode:"+requestCode+",resultCode:"+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (ConfigSet.getInstance().isAudioOpenWindown()&&MusicConstants.REQUST_WINDOWN_PERMISSION == requestCode) {
            if(MusicWindowManager.getInstance().checkAlertWindowsPermission(MusicPlayerActivity.this)){
                createMiniJukeBoxToWindown();
            }
        } else if (Constant.RECHARGE_REQUST_CODE == requestCode
                && Constant.RECHARGE_RESULT_CODE == resultCode) {
            //VIP充值完成
            if (rechageDialog != null) rechageDialog.dismiss();
            reCheckBuyMedia();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
        isVisibility=false;isShowHeadphone=false;
        if(null!=mHandler){
            mHandler.removeMessages(0);
            mHandler=null;
        }
        MusicPlayerManager.getInstance().removePlayerListener(this);
//        VideoCallManager.getInstance().setBebusying(false);
        if (rechageDialog != null) {
            rechageDialog.dismiss();
            rechageDialog = null;
        }
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

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg&&arg instanceof String) {
            String cmd = (String) arg;
            if(TextUtils.equals(Constant.OBSERVER_CMD_PRIVATE_RECHARGE_SUCCESS, cmd)){
                //充值完成
                if (rechageDialog != null) rechageDialog.dismiss();
                reCheckBuyMedia();
            }
        }
    }

    private void reCheckBuyMedia () {
        MusicPlayerState playerState = MusicPlayerManager.getInstance().getPlayerState();
        boolean playing = playerState.equals(MusicPlayerState.MUSIC_PLAYER_PLAYING) ||
                playerState.equals(MusicPlayerState.MUSIC_PLAYER_PREPARE) ||
                playerState.equals(MusicPlayerState.MUSIC_PLAYER_BUFFER);
        if (!playing) {
            BaseMediaInfo musicInfo = MusicPlayerManager.getInstance().getCurrentPlayerMusic();
            if(null==musicInfo){
                return;
            }
            mIsBuy=0;
            if(null!=mMusicJukeBoxView){
                mMusicJukeBoxView.onPause();
            }
            buyMediaSource(musicInfo);
        }
    }
}