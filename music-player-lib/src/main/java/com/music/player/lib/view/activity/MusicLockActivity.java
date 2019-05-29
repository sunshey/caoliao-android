package com.music.player.lib.view.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.music.player.lib.R;
import com.music.player.lib.bean.BaseMediaInfo;
import com.music.player.lib.bean.MusicStatus;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.listener.MusicPlayerEventListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.manager.MusicWindowManager;
import com.music.player.lib.model.MusicAlarmModel;
import com.music.player.lib.model.MusicPlayModel;
import com.music.player.lib.model.MusicPlayerState;
import com.music.player.lib.util.Logger;
import com.music.player.lib.util.MusicClickControler;
import com.music.player.lib.util.MusicUtils;
import com.music.player.lib.view.MusicCustomTextView;
import com.music.player.lib.view.MusicSildingLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * TinyHung@Outlook.com
 * 2019/3/19
 * Music lock
 */

public class MusicLockActivity extends AppCompatActivity implements MusicPlayerEventListener {

    private static final String TAG = "MusicLockActivity";
    private TextView mMusicTime,mMusicDate,mMusicTitle,mMusicAnchor;
    private Handler mHandler;
    private ImageView mMusicCover,mMusicPause,mMusicCollect,mMusicModel;
    private ObjectAnimator mDiscObjectAnimator;
    private boolean discIsPlaying=false;
    private MusicClickControler mClickControler;
    private MusicCustomTextView mCustomTextView;
    private int mScreenWidth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG,"onCreate");
        Window window = getWindow();
        //去除锁和在锁屏界面显示此Activity
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    |WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    |WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }else{
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    |WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    |WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_music_lock);
        MusicSildingLayout sildingLayout = (MusicSildingLayout) findViewById(R.id.music_silding_root);
        sildingLayout.setOnSildingFinishListener(new MusicSildingLayout.OnSildingFinishListener() {

            @Override
            public void onSildingFinish() {
                finish();
            }
        });
        sildingLayout.setTouchView(getWindow().getDecorView());
        mMusicTime = (TextView) findViewById(R.id.music_lock_time);
        mMusicDate = (TextView) findViewById(R.id.music_lock_date);
        mMusicTitle = (TextView) findViewById(R.id.music_lock_name);
        mMusicAnchor = (TextView) findViewById(R.id.music_lock_anchor);
        mMusicCover = (ImageView) findViewById(R.id.music_lock_cover);
        mMusicPause = (ImageView) findViewById(R.id.music_lock_pause);
        mMusicCollect = (ImageView) findViewById(R.id.music_lock_collect);
        mMusicModel = (ImageView) findViewById(R.id.music_lock_model);
        mCustomTextView = (MusicCustomTextView) findViewById(R.id.lock_tip);
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClickControler.canTrigger()){
                    int id = v.getId();
                    if(id==R.id.music_lock_pause){
                        MusicPlayerManager.getInstance().playOrPause();
                    }else if(id==R.id.music_lock_last){
                        MusicPlayerManager.getInstance().playLastMusic();
                    }else if(id==R.id.music_lock_next){
                        MusicPlayerManager.getInstance().playNextMusic();
                    }else if(id==R.id.music_lock_collect){
                        if(null!=mMusicCollect.getTag()){
                            BaseMediaInfo mediaInfo = (BaseMediaInfo) mMusicCollect.getTag();
                            if(mMusicCollect.isSelected()){
                                boolean isSuccess = MusicUtils.getInstance().removeMusicCollectById(mediaInfo.getId());
                                Logger.d(TAG,"removeState:"+isSuccess);
                                if(isSuccess){
                                    mMusicCollect.setSelected(false);
                                }
                            }else{
                                boolean isSuccess = MusicUtils.getInstance().putMusicToCollect(mediaInfo);
                                Logger.d(TAG,"addState:"+isSuccess);
                                if(isSuccess){
                                    mMusicCollect.setSelected(true);
                                    MusicPlayerManager.getInstance().observerUpdata(new MusicStatus());
                                }
                            }
                        }
                    }else if(id==R.id.music_lock_model){
                        MusicPlayerManager.getInstance().changedPlayerPlayFullModel();
                    }
                }
            }
        };
        findViewById(R.id.music_lock_last).setOnClickListener(onClickListener);
        findViewById(R.id.music_lock_next).setOnClickListener(onClickListener);
        mMusicPause.setOnClickListener(onClickListener);
        mMusicCollect.setOnClickListener(onClickListener);
        mMusicModel.setOnClickListener(onClickListener);
        mHandler=new Handler();
        //闹钟模式
        MusicPlayModel playerModel = MusicPlayerManager.getInstance().getPlayerModel();
        mMusicModel.setImageResource(getResToPlayModel(playerModel,false));
        //播放对象、状态
        BaseMediaInfo mediaInfo = MusicPlayerManager.getInstance().getCurrentPlayerMusic();
        mMusicPause.setImageResource(getPauseIcon(MusicPlayerManager.getInstance().getPlayerState()));
        mScreenWidth = MusicUtils.getInstance().getScreenWidth(this);
        int discSize = (int) (mScreenWidth * MusicConstants.SCALE_DISC_LOCK_SIZE);
        Logger.d(TAG,"discSize:"+discSize);
        mMusicCover.getLayoutParams().height=discSize;
        mMusicCover.getLayoutParams().width=discSize;
        updateMusicData(mediaInfo);
        MusicPlayerManager.getInstance().addOnPlayerEventListener(this);
        mDiscObjectAnimator = getDiscObjectAnimator(mMusicCover);
        mClickControler=new MusicClickControler();
        mClickControler.init(1,300);
    }

    /**
     * 更新当前处理的对象
     * @param mediaInfo
     */
    private void updateMusicData(BaseMediaInfo mediaInfo) {
        if(null!=mediaInfo){
            //是否已收藏
            boolean isExist = MusicUtils.getInstance().isExistCollectHistroy(mediaInfo.getId());
            mMusicCollect.setSelected(isExist);
            mMusicCollect.setTag(mediaInfo);
            mMusicTitle.setText(mediaInfo.getVideo_desp());
            mMusicAnchor.setText(mediaInfo.getNickname());
            MusicUtils.getInstance().setMusicComposeFront(MusicLockActivity.this,mMusicCover,MusicUtils.getInstance().getMusicFrontPath(mediaInfo),MusicConstants.SCALE_DISC_LOCK_SIZE
                    ,MusicConstants.SCALE_MUSIC_PIC_LOCK_SIZE,R.drawable.ic_music_lock_cover_plate,R.drawable.ic_music_juke_default_cover);
        }
    }

    /**
     * 实例化旋转动画对象
     * @param view
     * @return
     */
    private ObjectAnimator getDiscObjectAnimator(View view) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.ROTATION, 0, 360);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(20 * 1000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        return objectAnimator;
    }

    /**
     * 播放按钮状态
     * @return
     * @param playerState
     */
    private int getPauseIcon(MusicPlayerState playerState) {
        switch (playerState) {
            case MUSIC_PLAYER_PREPARE:
            case MUSIC_PLAYER_PLAYING:
            case MUSIC_PLAYER_BUFFER:
                return R.drawable.music_player_pause_selector;
            case MUSIC_PLAYER_STOP:
            case MUSIC_PLAYER_ERROR:
                return R.drawable.music_player_play_selector;
        }
        return R.drawable.music_player_play_selector;
    }

    /**
     * 获取对应播放模式ICON
     * @param playerModel
     * @param isToast 是否吐司提示
     * @return
     */
    private int getResToPlayModel(MusicPlayModel playerModel,boolean isToast) {
        if(playerModel.equals(MusicPlayModel.MUSIC_MODEL_LOOP)){
            if(isToast){
                Toast.makeText(MusicLockActivity.this,"列表循环",Toast.LENGTH_SHORT).show();
            }
            return R.drawable.ic_music_lock_model_loop;
        }
        if(playerModel.equals(MusicPlayModel.MUSIC_MODEL_SINGLE)){
            if(isToast){
                Toast.makeText(MusicLockActivity.this,"单曲循环",Toast.LENGTH_SHORT).show();
            }
            return R.drawable.ic_music_lock_model_signle;
        }
        if(playerModel.equals(MusicPlayModel.MUSIC_MODEL_RANDOM)){
            if(isToast){
                Toast.makeText(MusicLockActivity.this,"随机播放",Toast.LENGTH_SHORT).show();
            }
            return R.drawable.ic_music_lock_model_random;
        }
        return R.drawable.ic_music_lock_model_signle;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d(TAG,"onResume");
        //View.SYSTEM_UI_FLAG_FULLSCREEN 状态栏不可见
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        MusicWindowManager.getInstance().onInvisible();
        jukeBoxResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(TAG,"onPause");
        MusicWindowManager.getInstance().onVisible();
        jukeBoxPause();
    }

    /**
     * 封面动画开始
     */
    private synchronized void jukeBoxResume() {
        Logger.d(TAG,"jukeBoxResume-->"+discIsPlaying);
        if(null!=mDiscObjectAnimator&&MusicPlayerManager.getInstance().isPlaying()){
            if(discIsPlaying){
                return;
            }
            discIsPlaying=true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if(mDiscObjectAnimator.isPaused()){
                    mDiscObjectAnimator.resume();
                }else{
                    mDiscObjectAnimator.start();
                }
            }else{
                mDiscObjectAnimator.start();
            }
        }
    }

    /**
     * 封面动画暂停
     */
    private synchronized void jukeBoxPause() {
        Logger.d(TAG,"jukeBoxPause-->"+discIsPlaying);
        if(null!=mDiscObjectAnimator){
            discIsPlaying=false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mDiscObjectAnimator.pause();
            }else{
                mDiscObjectAnimator.cancel();
                mMusicCover.setRotation(0);
            }
        }
    }

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d(TAG,"onDestroy");
        if(null!=mCustomTextView){
            mCustomTextView.onDestroy();
        }
        if(null!=mHandler){
            mHandler.removeMessages(0);
            mHandler=null;
        }
        mClickControler=null;
        if(null!=mDiscObjectAnimator){
            discIsPlaying=false;
            mDiscObjectAnimator.cancel();
            mDiscObjectAnimator=null;
            if(null!=mMusicCover) mMusicCover.setRotation(0);
        }
        MusicPlayerManager.getInstance().removePlayerListener(this);
    }

    //========================================播放器内部状态==========================================

    /**
     * 播放器内部状态发生了变化
     * @param playerState 播放器内部状态
     * @param message
     */
    @Override
    public void onMusicPlayerState(final MusicPlayerState playerState, final String message) {
        Logger.d(TAG,"onMusicPlayerState-->"+playerState);
        if(null!=mHandler){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (playerState.equals(MusicPlayerState.MUSIC_PLAYER_ERROR)&&!TextUtils.isEmpty(message)) {
                        Toast.makeText(MusicLockActivity.this,message,Toast.LENGTH_SHORT).show();
                    }
                    if(null!=mMusicPause){
                        mMusicPause.setImageResource(getPauseIcon(playerState));
                    }
                    if(playerState.equals(MusicPlayerState.MUSIC_PLAYER_PREPARE)||playerState.equals(MusicPlayerState.MUSIC_PLAYER_PLAYING)){
                        jukeBoxResume();
                    }else if(playerState.equals(MusicPlayerState.MUSIC_PLAYER_PAUSE)
                            ||playerState.equals(MusicPlayerState.MUSIC_PLAYER_ERROR)
                            ||playerState.equals(MusicPlayerState.MUSIC_PLAYER_STOP)){
                        jukeBoxPause();
                    }
                }
            });
        }
    }

    @Override
    public void onPrepared(long totalDurtion) {}
    @Override
    public void onBufferingUpdate(int percent) {}
    @Override
    public void onInfo(int event, int extra) {}

    /**
     * 播放器正在处理的对象发生了变化
     * @param musicInfo 正在播放的对象
     * @param position 当前正在播放的位置
     */
    @Override
    public void onPlayMusiconInfo(final BaseMediaInfo musicInfo, int position) {
        if(null!=musicInfo&&null!=mHandler){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateMusicData(musicInfo);
                }
            });
        }
    }

    @Override
    public void onEchoPlayCurrentIndex(final BaseMediaInfo musicInfo, int position) {
        if(null!=musicInfo&&null!=mHandler){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateMusicData(musicInfo);
                }
            });
        }
    }

    @Override
    public void onMusicPathInvalid(BaseMediaInfo musicInfo, int position) {}

    @Override
    public void onTaskRuntime(long totalDurtion, long currentDurtion, long alarmResidueDurtion,int bufferProgress) {
        if(null!=mHandler&&null!=mMusicTime){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm-MM月dd日 E", Locale.CHINESE);
                    String date[] = simpleDateFormat.format(new Date()).split("-");
                    if(null!=mMusicTime){
                        mMusicTime.setText(date[0]);
                    }
                    if(null!=mMusicDate){
                        mMusicDate.setText(date[1]);
                    }
                }
            });
        }
    }
    @Override
    public void onPlayerConfig(MusicPlayModel playModel, MusicAlarmModel alarmModel,boolean isToast) {
        if(null!=mMusicModel){
            mMusicModel.setImageResource(getResToPlayModel(playModel,isToast));
        }
    }
}