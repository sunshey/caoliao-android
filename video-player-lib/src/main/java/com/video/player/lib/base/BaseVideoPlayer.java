package com.video.player.lib.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.video.player.lib.R;
import com.video.player.lib.constants.VideoConstants;
import com.video.player.lib.controller.DefaultCoverController;
import com.video.player.lib.controller.DefaultVideoController;
import com.video.player.lib.listener.VideoOrientationListener;
import com.video.player.lib.listener.VideoPlayerEventListener;
import com.video.player.lib.manager.VideoPlayerManager;
import com.video.player.lib.model.VideoPlayerScene;
import com.video.player.lib.model.VideoPlayerState;
import com.video.player.lib.utils.Logger;
import com.video.player.lib.utils.VideoUtils;
import com.video.player.lib.view.VideoTextureView;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * TinyHung@Outlook.com
 * 2019/4/8
 * Base Video Player
 * C：视频控制器 F：封面控制器
 * 继承此类，必须在.xml文件中引用 values下的ids.xml中定义的
 * surface_view、controller_view、cover_view 三个ID
 */

public abstract class BaseVideoPlayer<C extends BaseVideoController,F extends BaseCoverController>
        extends FrameLayout implements VideoPlayerEventListener, View.OnTouchListener {

    private static final String TAG = "BaseVideoPlayer";
    //VideoController
    protected C mVideoController;
    //CoverController
    protected F mCoverController;
    private String mDataSource,mTitle;
    //视频帧渲染父容器
    public FrameLayout mSurfaceView;
    //缩放类型
    public static int VIDEO_DISPLAY_TYPE = VideoConstants.VIDEO_DISPLAY_TYPE_ORIGINAL;
    //单击事件的有效像素
    public static int SCROLL_PIXEL=10;
    //手指在屏幕上的实时X、Y坐标
    private static float xInScreen,yInScreen;
    //手指按下X、Y坐标
    private static float xDownInScreen,yDownInScreen;
    //此播放器是否正在工作,配合列表滚动时，检测工作状态
    private boolean isWorking=false;
    private int SCRREN_ORIENTATION = VideoConstants.SCREEN_ORIENTATION_PORTRAIT;
    //用户是否打开重力感应开关
    private static boolean mOrientationEnable = false;
    //屏幕的方向,竖屏、横屏、窗口
    private SensorManager mSensorManager;
    //屏幕方向监听器
    private VideoOrientationListener mOrientationListener;

    protected abstract int getLayoutID();

    public BaseVideoPlayer(@NonNull Context context) {
        this(context,null);
    }

    public BaseVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        boolean autoSetVideoController=false;
        boolean autoSetCoverController=false;
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseVideoPlayer);
            autoSetVideoController = typedArray.getBoolean(R.styleable.BaseVideoPlayer_video_autoSetVideoController, false);
            autoSetCoverController = typedArray.getBoolean(R.styleable.BaseVideoPlayer_video_autoSetCoverController, false);
            boolean loop = typedArray.getBoolean(R.styleable.BaseVideoPlayer_video_loop, false);
            boolean workEnable = typedArray.getBoolean(R.styleable.BaseVideoPlayer_video_mobileWorkEnable, false);
            this.mOrientationEnable= typedArray.getBoolean(R.styleable.BaseVideoPlayer_video_orientantionEnable, false);
            VideoPlayerManager.getInstance().setLoop(loop);
            VideoPlayerManager.getInstance().setMobileWorkEnable(workEnable);
            typedArray.recycle();
        }
        View.inflate(context,getLayoutID(),this);
        //默认的初始化
        setVideoController(null,autoSetVideoController);
        setCoverController(null,autoSetCoverController);
        //画面渲染
        mSurfaceView = (FrameLayout) findViewById(R.id.surface_view);
        if(null!=mSurfaceView){
            mSurfaceView.setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() ;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (Math.abs(xInScreen - xDownInScreen) < SCROLL_PIXEL && Math.abs(yInScreen - yDownInScreen) < SCROLL_PIXEL) {
                    Logger.d(TAG,"触摸定性为单击事件,SCRREN_ORIENTATION:"+SCRREN_ORIENTATION);
                    if(VideoPlayerManager.getInstance().isPlaying()){
                        //单击
                        if(null!=mVideoController){
                            mVideoController.changeControllerState(SCRREN_ORIENTATION,true);
                        }
                    }
                    return true;
                }
                break;
            default:
                break;
        }
        return true;
    }

    //======================================播放器对外开放功能========================================

    /**
     * 设置播放资源
     * @param path 暂支持file、http、https等协议
     * @param title 视频描述
     * @param playerScene 应用场景
     */
    public void setDataSource(String path, String title, VideoPlayerScene playerScene) {
        if(null!= mVideoController){
            mVideoController.setTitle(title);
        }
        this.mDataSource=path;
        this.mTitle=title;
    }

    public void setLoop(boolean loop) {
        VideoPlayerManager.getInstance().setLoop(loop);
    }

    /**
     * 设置缩放类型
     * @param displayType 相见VideoConstants常量定义
     */
    public void setVideoDisplayType(int displayType) {
        this.VIDEO_DISPLAY_TYPE= displayType;
    }

    /**
     * 更新视频控制器
     * @param controller 自定义VideoPlayer控制器
     * @param autoCreateDefault 当 controller 为空，是否自动创建默认的控制器
     */
    public void setVideoController(C controller,boolean autoCreateDefault) {
        FrameLayout conntrollerView = (FrameLayout) findViewById(R.id.controller_view);
        if(null!=conntrollerView){
            if(conntrollerView.getChildCount()>0){
                conntrollerView.removeAllViews();
            }
            if(null!= mVideoController){
                mVideoController.onDestroy();
                mVideoController =null;
            }
            //使用自定义的
            if(null!=controller){
                mVideoController = controller;
            }else{
                //是否使用默认的
                if(autoCreateDefault){
                    mVideoController = (C) new DefaultVideoController(getContext());
                }
            }
            //添加控制器到播放器
            if(null!=mVideoController){
                mVideoController.setOnFuctionListener(new BaseVideoController.OnFuctionListener() {
                    @Override
                    public void onStartFullPlay() {
                        if(SCRREN_ORIENTATION==VideoConstants.SCREEN_ORIENTATION_PORTRAIT){
                            startFullPlay();
                        }else{
                            backFullWindow();
                        }
                    }

                    @Override
                    public void onTinyBack() {
                        if(SCRREN_ORIENTATION==VideoConstants.SCREEN_ORIENTATION_TINY){
                            backTinyWindow();
                        }
                    }

                    @Override
                    public void onBackPressed() {
                        backPressed();
                    }
                });
                conntrollerView.addView(mVideoController,new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
            }
        }
    }

    /**
     * 更新封面控制器
     * @param controller 自定义VideoPlayerCover控制器
     * @param autoCreateDefault 当 controller 为空，是否自动创建默认的控制器
     */
    public void setCoverController(F controller,boolean autoCreateDefault) {
        FrameLayout conntrollerView = (FrameLayout) findViewById(R.id.cover_view);
        if(null!=conntrollerView){
            if(conntrollerView.getChildCount()>0){
                conntrollerView.removeAllViews();
            }
            if(null!= mCoverController){
                mCoverController.onDestroy();
                mCoverController =null;
            }
            //使用自定义的
            if(null!=controller){
                mCoverController = controller;
            }else{
                //是否使用默认的
                if(autoCreateDefault){
                    mCoverController = (F) new DefaultCoverController(getContext());
                }
            }
            //添加控制器到播放器
            if(null!=mCoverController){
                mCoverController.setOnStartListener(new BaseCoverController.OnStartListener() {
                    @Override
                    public void onStartPlay() {
                        starPlaytVideo();
                    }
                });
                conntrollerView.addView(mCoverController,new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
            }
        }
    }

    /**
     * 移动网络工作开关
     * @param mobileWorkEnable
     */
    public void setMobileWorkEnable(boolean mobileWorkEnable){
        VideoPlayerManager.getInstance().setMobileWorkEnable(mobileWorkEnable);
    }

    /**
     * 返回封面控制器
     * @return
     */
    public F getCoverController() {
        return mCoverController;
    }

    /**
     * 更新播放器方向
     * @param scrrenOrientation
     */
    private void setScrrenOrientation(int scrrenOrientation) {
        this.SCRREN_ORIENTATION=scrrenOrientation;
        if(null!=mVideoController){
            mVideoController.setScrrenOrientation(scrrenOrientation);
        }
    }

    /**
     * 方向重力感应开关
     * @param enable
     */
    public void setOrientantionEnable(boolean enable){
        this.mOrientationEnable=enable;
        if(enable){
            AppCompatActivity appCompActivity = VideoUtils.getInstance().getAppCompActivity(getContext());
            if(null!=appCompActivity){
                mSensorManager = (SensorManager)appCompActivity.getSystemService(Context.SENSOR_SERVICE);
                mOrientationListener = new VideoOrientationListener(new VideoOrientationListener.OnOrientationChangeListener() {
                    @Override
                    public void orientationChanged(int orientation) {
                        if(SCRREN_ORIENTATION==VideoConstants.SCREEN_ORIENTATION_FULL){
                            Logger.d(TAG,"orientationChanged-->newOrientation:"+orientation);
                        }
                    }
                });
                mSensorManager.registerListener(mOrientationListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            }
        }else{
            if(null!=mSensorManager&&null!=mOrientationListener){
                mSensorManager.unregisterListener(mOrientationListener);
                mSensorManager=null;mOrientationListener=null;
            }
        }
    }

    /**
     * 开始播放的入口开始播放、准备入口
     */
    public void starPlaytVideo(){
        if(TextUtils.isEmpty(mDataSource)){
            Toast.makeText(getContext(),"播放地址为空",Toast.LENGTH_SHORT).show();
             return;
        }
        Logger.d(TAG,"startVideo-->");
        //还原可能正在进行的播放任务
        VideoPlayerManager.getInstance().onReset();
        VideoPlayerManager.getInstance().addOnPlayerEventListener(this);
        isWorking=true;
        //准备画面渲染图层
        if(null!=mSurfaceView){
            addTextrueViewToView(BaseVideoPlayer.this);
            //开始准备播放
            VideoPlayerManager.getInstance().startVideoPlayer(mDataSource,getContext());
        }
    }

    /**
     * 添加一个视频渲染组件至View
     * @param videoPlayer
     */
    private void addTextrueViewToView(BaseVideoPlayer videoPlayer) {
        //先移除存在的TextrueView
        if(null!=VideoPlayerManager.getInstance().getTextureView()){
            VideoTextureView textureView = VideoPlayerManager.getInstance().getTextureView();
            if(null!=textureView.getParent()){
                ((ViewGroup) textureView.getParent()).removeView(textureView);
            }
        }
        if(null!=VideoPlayerManager.getInstance().getTextureView()){
            videoPlayer.mSurfaceView.addView(VideoPlayerManager.getInstance().getTextureView(),new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, Gravity.CENTER));
        }else{
            VideoTextureView textureView=new VideoTextureView(getContext());
            VideoPlayerManager.getInstance().initTextureView(textureView);
            videoPlayer.mSurfaceView.addView(textureView,new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, Gravity.CENTER));
        }
    }

    /**
     * 开启全屏播放模式,参考系统API ActivityInfo 定义
     * 开启全屏播放的原理：
     * 1：改变屏幕方向，Activity 属性必须设置为android:configChanges="orientation|screenSize"，避免Activity销毁重建
     * 2：移除常规播放器已有的TextureView组件
     * 3：向Windown ViewGroup 添加一个新的VideoPlayer组件,赋值已有的TextrueView，设置新的播放器监听，结合TextrueView onSurfaceTextureAvailable 回调事件处理
     * 4：根据自身业务，向新的播放器添加控制器
     * 5：记录全屏窗口播放器
     */
    private void startFullPlay() {
        AppCompatActivity appCompActivity = VideoUtils.getInstance().getAppCompActivity(getContext());
        if(null!=appCompActivity){
            SCRREN_ORIENTATION=VideoConstants.SCREEN_ORIENTATION_FULL;
            setScrrenOrientation(SCRREN_ORIENTATION);
            //改变屏幕方向
            appCompActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            appCompActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ViewGroup viewGroup = (ViewGroup) appCompActivity.getWindow().getDecorView();
            if(null!=viewGroup&&null!=VideoPlayerManager.getInstance().getTextureView()){
                View oldFullVideo = viewGroup.findViewById(R.id.video_full_view);
                //移除Window可能存在的播放器组件
                if(null!=oldFullVideo){
                    viewGroup.removeView(oldFullVideo);
                }
                //保存当前实例
                VideoPlayerManager.getInstance().setNoimalPlayer(BaseVideoPlayer.this);
                try {
                    Constructor<? extends BaseVideoPlayer> constructor = BaseVideoPlayer.this.getClass().getConstructor(Context.class);
                    //新实例化自己
                    BaseVideoPlayer videoPlayer = constructor.newInstance(getContext());
                    //绑定组件ID
                    videoPlayer.setId(R.id.video_full_view);
                    //保存全屏窗口实例
                    VideoPlayerManager.getInstance().setFullScrrenPlayer(videoPlayer);
                    //将新的实例化添加至Window
                    viewGroup.addView(videoPlayer,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                    //这里的全屏播放器强制设置控制器
                    videoPlayer.setVideoController(null,true);
                    //更新屏幕方向
                    videoPlayer.setScrrenOrientation(SCRREN_ORIENTATION);
                    //转换为横屏方向
                    videoPlayer.mVideoController.startHorizontal();
                    videoPlayer.setWorking(true);
                    //设置基础的配置
                    videoPlayer.setDataSource(mDataSource,mTitle,VideoPlayerScene.NOIMAL);
                    //更新重力感应开关
                    videoPlayer.setOrientantionEnable(mOrientationEnable);
                    //添加TextrueView至播放控件
                    addTextrueViewToView(videoPlayer);
                    VideoPlayerManager.getInstance().addOnPlayerEventListener(videoPlayer);
                    //手动检查播放器内部状态，同步常规播放器状态至新的播放器
                    VideoPlayerManager.getInstance().checkedVidepPlayerState();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 退出全屏播放
     * 退出全屏播放的原理：和开启全屏反过来
     */
    private void backFullWindow(){
        AppCompatActivity appCompActivity = VideoUtils.getInstance().getAppCompActivity(getContext());
        if(null!=appCompActivity){
            SCRREN_ORIENTATION=VideoConstants.SCREEN_ORIENTATION_PORTRAIT;
            //改变屏幕方向
            appCompActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            appCompActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            BaseVideoPlayer fullScrrenPlayer = VideoPlayerManager.getInstance().getFullScrrenPlayer();
            //移除全屏播放器的SurfaceView及屏幕窗口的VideoPlayer
            if(null!=fullScrrenPlayer){
                if(null!=VideoPlayerManager.getInstance().getTextureView()){
                    fullScrrenPlayer.mSurfaceView.removeView(VideoPlayerManager.getInstance().getTextureView());
                }
                fullScrrenPlayer.destroy();
                //从窗口移除ViewPlayer
                ViewGroup viewGroup = (ViewGroup) appCompActivity.getWindow().getDecorView();
                View oldFullVideo = viewGroup.findViewById(R.id.video_full_view);
                if(null!=oldFullVideo){
                    viewGroup.removeView(oldFullVideo);
                }else{
                    viewGroup.removeView(fullScrrenPlayer);
                }
                VideoPlayerManager.getInstance().setFullScrrenPlayer(null);
            }
            BaseVideoPlayer noimalPlayer = VideoPlayerManager.getInstance().getNoimalPlayer();
            if(null!=noimalPlayer){
                noimalPlayer.setScrrenOrientation(SCRREN_ORIENTATION);
                addTextrueViewToView(noimalPlayer);
                VideoPlayerManager.getInstance().addOnPlayerEventListener(noimalPlayer);
                //手动检查播放器内部状态，同步全屏播放器至常规播放器状态
                VideoPlayerManager.getInstance().checkedVidepPlayerState();
            }
        }
    }

    /**
     * 开启小窗口播放
     * @param startX 位于屏幕的X轴像素
     * @param startY 位于屏幕的Y轴像素
     * @param tinyWidth 小窗口的宽 未指定使用默认 屏幕宽/2
     * @param tinyHeight 小窗口的高 未指定使用默认 屏幕宽/2*9/16
     */
    public void startTinyWindowPlay(int startX,int startY,int tinyWidth,int tinyHeight){
        if(SCRREN_ORIENTATION==VideoConstants.SCREEN_ORIENTATION_TINY){
            Toast.makeText(getContext(),"已切换至小窗口",Toast.LENGTH_SHORT).show();
            return;
        }
        AppCompatActivity appCompActivity = VideoUtils.getInstance().getAppCompActivity(getContext());
        if(null!=appCompActivity){
            SCRREN_ORIENTATION=VideoConstants.SCREEN_ORIENTATION_TINY;
            ViewGroup viewGroup = (ViewGroup) appCompActivity.getWindow().getDecorView();
            if(null!=viewGroup&&null!=VideoPlayerManager.getInstance().getTextureView()){
                View oldTinyVideo = viewGroup.findViewById(R.id.video_tiny_view);
                //移除Window可能存在的播放器组件
                if(null!=oldTinyVideo){
                    viewGroup.removeView(oldTinyVideo);
                }
                //保存当前常规实例
                VideoPlayerManager.getInstance().setNoimalPlayer(BaseVideoPlayer.this);
                VideoPlayerManager.getInstance().getNoimalPlayer().reset();
                try {
                    Constructor<? extends BaseVideoPlayer> constructor = BaseVideoPlayer.this.getClass().getConstructor(Context.class);
                    //新实例化窗口
                    BaseVideoPlayer videoPlayer = constructor.newInstance(getContext());
                    //绑定组件ID
                    videoPlayer.setId(R.id.video_tiny_view);
                    //保存小窗口实例
                    VideoPlayerManager.getInstance().setTinyPlayer(videoPlayer);
                    //将新的实例化添加至Window
                    int screenWidth = VideoUtils.getInstance().getScreenWidth(appCompActivity);
                    int width=screenWidth/2;
                    int height= width * 9 / 16;
                    if(tinyWidth>0){
                        width=tinyWidth;
                    }
                    if(tinyHeight>0){
                        height=tinyHeight;
                    }
                    Logger.d(TAG,"startTinyWindowPlay-->startX:"+startX+",startY:"+startY+",tinyWidth:"+tinyWidth+",tinyHeight:"+tinyHeight);
                    LayoutParams layoutParams = new LayoutParams(width, height);
                    layoutParams.setMargins(startX,startY,0,0);
                    viewGroup.addView(videoPlayer,layoutParams);
                    //设置一个默认的控制器
                    videoPlayer.setVideoController(null,true);
                    //更新屏幕方向,这里只更新为窗口模式即可
                    videoPlayer.setScrrenOrientation(SCRREN_ORIENTATION);
                    //转换为小窗口模式
                    videoPlayer.mVideoController.startTiny();
                    videoPlayer.setWorking(true);
                    //设置基础的配置
                    videoPlayer.setDataSource(mDataSource,mTitle,VideoPlayerScene.NOIMAL);
                    //添加TextrueView至播放控件
                    addTextrueViewToView(videoPlayer);
                    VideoPlayerManager.getInstance().addOnPlayerEventListener(videoPlayer);
                    //手动检查播放器内部状态，同步常规播放器状态至新的播放器
                    VideoPlayerManager.getInstance().checkedVidepPlayerState();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 退出小窗口播放
     */
    public void backTinyWindow(){
        AppCompatActivity appCompActivity = VideoUtils.getInstance().getAppCompActivity(getContext());
        if(null!=appCompActivity){
            SCRREN_ORIENTATION=VideoConstants.SCREEN_ORIENTATION_PORTRAIT;
            BaseVideoPlayer tinyPlayer = VideoPlayerManager.getInstance().getTinyPlayer();
            //移除全屏播放器的SurfaceView及屏幕窗口的VideoPlayer
            if(null!=tinyPlayer){
                if(null!=VideoPlayerManager.getInstance().getTextureView()){
                    tinyPlayer.mSurfaceView.removeView(VideoPlayerManager.getInstance().getTextureView());
                }
                tinyPlayer.destroy();
                //从窗口移除ViewPlayer
                ViewGroup viewGroup = (ViewGroup) appCompActivity.getWindow().getDecorView();
                View oldTinyVideo = viewGroup.findViewById(R.id.video_tiny_view);
                if(null!=oldTinyVideo){
                    viewGroup.removeView(oldTinyVideo);
                }else{
                    viewGroup.removeView(oldTinyVideo);
                }
                VideoPlayerManager.getInstance().setTinyPlayer(null);
            }
            BaseVideoPlayer noimalPlayer = VideoPlayerManager.getInstance().getNoimalPlayer();
            if(null!=noimalPlayer){
                noimalPlayer.setScrrenOrientation(SCRREN_ORIENTATION);
                addTextrueViewToView(noimalPlayer);
                VideoPlayerManager.getInstance().addOnPlayerEventListener(noimalPlayer);
                //手动检查播放器内部状态，同步全屏播放器至常规播放器状态
                VideoPlayerManager.getInstance().checkedVidepPlayerState();
            }
        }
    }

    /**
     * 弹射返回
     */
    public boolean backPressed() {
        if(SCRREN_ORIENTATION==VideoConstants.SCREEN_ORIENTATION_PORTRAIT){
            return true;
        }
        //退出全屏
        if(SCRREN_ORIENTATION==VideoConstants.SCREEN_ORIENTATION_FULL){
            backFullWindow();
            return false;
        }
        //退出小窗
        if(SCRREN_ORIENTATION==VideoConstants.SCREEN_ORIENTATION_TINY){
            backTinyWindow();
            return false;
        }
        return true;
    }

    /**
     * 此处返回此组件绑定的工作状态
     * @return
     */
    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    //======================================播放器内部状态回调========================================

    /**
     * 播放器内部状态变化
     * @param playerState 播放器内部状态
     * @param message
     */
    @Override
    public void onVideoPlayerState(final VideoPlayerState playerState, final String message) {
        Logger.d(TAG,"onVideoPlayerState-->"+playerState);
        if(playerState.equals(VideoPlayerState.MUSIC_PLAYER_ERROR)&&!TextUtils.isEmpty(message)){
            Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
        }
        BaseVideoPlayer.this.post(new Runnable() {
            @Override
            public void run() {
                switch (playerState) {
                    //播放器准备中
                    case MUSIC_PLAYER_PREPARE:
                        if(null!=mCoverController&&mCoverController.getVisibility()!=VISIBLE){
                            mCoverController.setVisibility(VISIBLE);
                        }
                        if(null!=mVideoController){
                            mVideoController.readyPlaying();
                        }
                        break;
                    //播放过程缓冲中
                    case MUSIC_PLAYER_BUFFER:
                        if(null!=mCoverController&&mCoverController.getVisibility()!=GONE){
                            mCoverController.setVisibility(GONE);
                        }
                        if(null!=mVideoController){
                            mVideoController.startBuffer();
                        }
                        break;
                    //缓冲结束、准备结束 后的开始播放
                    case MUSIC_PLAYER_START:
                        if(null!=mCoverController&&mCoverController.getVisibility()!=GONE){
                            mCoverController.setVisibility(GONE);
                        }
                        if(null!=mVideoController){
                            mVideoController.play();
                        }
                        break;
                    //恢复播放
                    case MUSIC_PLAYER_PLAY:
                        if(null!=mVideoController){
                            mVideoController.repeatPlay();
                        }
                        break;
                    //移动网络环境下播放
                    case MUSIC_PLAYER_MOBILE:
                        if(null!=mCoverController&&mCoverController.getVisibility()!=VISIBLE){
                            mCoverController.setVisibility(VISIBLE);
                        }
                        if(null!=mVideoController){
                            mVideoController.mobileWorkTips();
                        }
                        break;
                    //暂停
                    case MUSIC_PLAYER_PAUSE:
                        if(null!=mCoverController&&mCoverController.getVisibility()!=GONE){
                            mCoverController.setVisibility(GONE);
                        }
                        if(null!=mVideoController){
                            mVideoController.pause();
                        }
                        //如果是小窗口模式下播放时被被暂停了，直接停止播放,并退出小窗口
                        if(SCRREN_ORIENTATION==VideoConstants.SCREEN_ORIENTATION_TINY){
                            VideoPlayerManager.getInstance().onStop();
                        }
                        break;
                    //停止
                    case MUSIC_PLAYER_STOP:
                        isWorking=false;
                        if(null!=mCoverController&&mCoverController.getVisibility()!=VISIBLE){
                            mCoverController.setVisibility(VISIBLE);
                        }
                        if(null!=mVideoController){
                            mVideoController.reset();
                        }
                        //停止、结束 播放时，检测当前播放器如果处于非常规状态下，退出全屏、或小窗
                        if(SCRREN_ORIENTATION!=VideoConstants.SCREEN_ORIENTATION_PORTRAIT){
                            backPressed();
                        }
                        break;
                    //失败
                    case MUSIC_PLAYER_ERROR:
                        isWorking=false;
                        if(null!=mVideoController){
                            mVideoController.error(0,message);
                        }
                        if(null!=mCoverController&&mCoverController.getVisibility()!=VISIBLE){
                            mCoverController.setVisibility(VISIBLE);
                        }
                        //播放失败，检测当前播放器如果处于非常规状态下，退出全屏、或小窗
                        if(SCRREN_ORIENTATION!=VideoConstants.SCREEN_ORIENTATION_PORTRAIT){
                            backPressed();
                        }
                        break;
                }
            }
        });
    }

    /**
     * 播放器准备完成
     * @param totalDurtion 总时长
     */
    @Override
    public void onPrepared(long totalDurtion) {}

    /**
     * 播放器缓冲进度
     * @param percent 百分比
     */
    @Override
    public void onBufferingUpdate(final int percent) {
        if(null!=mVideoController){
            mVideoController.post(new Runnable() {
                @Override
                public void run() {
                    if(null!=mVideoController){
                        mVideoController.onBufferingUpdate(percent);
                    }
                }
            });
        }
    }

    @Override
    public void onInfo(int event, int extra) {}

    /**
     * 播放器地址无效
     */
    @Override
    public void onVideoPathInvalid() {
        if(null!=mVideoController){
            mVideoController.pathInvalid();
        }
    }

    /**
     * 播放器实时进度
     * @param totalDurtion 音频总时间
     * @param currentDurtion 当前播放的位置
     */
    @Override
    public void onTaskRuntime(final long totalDurtion, final long currentDurtion) {
        if(null!=mVideoController){
            mVideoController.post(new Runnable() {
                @Override
                public void run() {
                    if(null!=mVideoController){
                        mVideoController.onTaskRuntime(totalDurtion,currentDurtion);
                    }
                }
            });
        }
    }

    /**
     * 仅释放播放器窗口UI
     */
    public void reset() {
        //先移除存在的TextrueView
        if(null!=VideoPlayerManager.getInstance().getTextureView()){
            VideoTextureView textureView = VideoPlayerManager.getInstance().getTextureView();
            if(null!=textureView.getParent()){
                ((ViewGroup) textureView.getParent()).removeView(textureView);
            }
        }
        if(null!=mVideoController){
            Logger.d(TAG,"reset--1");
            mVideoController.reset();
        }
        if(null!=mCoverController){
            Logger.d(TAG,"reset--2");
            mCoverController.setVisibility(VISIBLE);
        }
    }

    /**
     * 销毁
     */
    public void onReset() {
        VideoPlayerManager.getInstance().onReset();
    }

    /**
     * 仅仅内部销毁，外部组件调用VideoPlayerManager 的 onDestroy()方法
     */
    @Override
    public void destroy() {
        if(null!= mVideoController){
            mVideoController.onDestroy();
            mVideoController =null;
        }
        if(null!=mCoverController){
            mCoverController.onDestroy();
            mCoverController=null;
        }
        if(null!=mSensorManager&&null!=mOrientationListener){
            mSensorManager.unregisterListener(mOrientationListener);
            mSensorManager=null;mOrientationListener=null;
        }
        mDataSource=null;mTitle=null;mSurfaceView=null;
    }
}