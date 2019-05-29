package com.yc.liaolive.gift.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.yc.liaolive.gift.manager.GiftResourceManager;
import com.yc.liaolive.gift.manager.RoomDanmuManager;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.util.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * TinyHung@Outlook.com
 * 2018/11/27
 * SVGA礼物动画播放管理器
 */

public class AnimatorSvgaPlayerManager extends FrameLayout {

    private static final String TAG = "AnimatorSvgaPlayerManager";
    private Queue<CustomMsgInfo> mGroupGiftQueue;//礼物队列
    private SVGAImageView mSvgaImageView;
    private boolean isPlaying;//礼物动画是否正在播放
    private InputStream mInputStream;
    private RoomDanmuManager mDanmuManager;

    public AnimatorSvgaPlayerManager(Context context) {
        super(context);
    }

    public AnimatorSvgaPlayerManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initSvgaView() {
        mSvgaImageView = new SVGAImageView(getContext());
        mSvgaImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(mSvgaImageView);
        mSvgaImageView.setLoops(1);//重复播放次数
        mSvgaImageView.setCallback(new SVGACallback() {
            //暂停
            @Override
            public void onPause() {
            }
            //播放完成
            @Override
            public void onFinished() {
                isPlaying=false;
                giftEnd();
            }
            //重复
            @Override
            public void onRepeat() {
            }
            //停止
            @Override
            public void onStep(int i, double v) {
            }
        });
    }

    /**
     * 添加单个动画任务
     * @param mediaGiftInfo
     */
    public void addAnimationToTask(CustomMsgInfo mediaGiftInfo){
        if(null==mediaGiftInfo||null==mediaGiftInfo.getGift()) return;
        if(TextUtils.isEmpty(mediaGiftInfo.getGift().getBigSvga())) return;
        if(null==mGroupGiftQueue) mGroupGiftQueue=new ArrayDeque<>();
        //过滤自己快速赠送礼物
//        GiftInfo newGiftInfo = mediaGiftInfo.getGift();
//        String tagid=mediaGiftInfo.getSendUserID()+newGiftInfo.getId()+mediaGiftInfo.getAccapUserID();
//        Logger.d(TAG,"TAG_ID："+tagid);
//        if(null!=getTag()){
//            String tag = (String) getTag();
//            if(TextUtils.equals(tagid,tag)) {
//                Logger.d(TAG,"当前礼物正在播放");
//                return;
//            }
//        }
        //检测队列中是否存在相同的礼物
//        boolean isAdd=true;
//        for (CustomMsgInfo giftInfo : mGroupGiftQueue) {
//            GiftInfo oldGift = giftInfo.getGift();
//            if(tagid.equals(giftInfo.getSendUserID()+oldGift.getId()+giftInfo.getAccapUserID())){
//                int count = oldGift.getCount() + newGiftInfo.getCount();
//                oldGift.setCount(count);
//                isAdd=false;
//                Logger.d(TAG,"队列中存在相同的任务");
//                break;
//            }
//        }
//        if(isAdd) mGroupGiftQueue.add(mediaGiftInfo);
        mGroupGiftQueue.add(mediaGiftInfo);
        startAutoPlayer();
    }


    public void bindDanmakuView(RoomDanmuManager danmuManager){
        this.mDanmuManager =danmuManager;
    }


    /**
     * 开始礼物动画播放
     */
    private void startAutoPlayer() {
        if(isPlaying) return;
        if(null!=mGroupGiftQueue&&mGroupGiftQueue.size()>0&&null==getTag()){
            CustomMsgInfo poll = mGroupGiftQueue.poll();
            if(null!=poll) startPlayerGiftAnimation(poll);
        }
    }

    /**
     * 播放礼物动画
     * @param giftInfo
     */
    private void startPlayerGiftAnimation(final CustomMsgInfo giftInfo) {
        if(null==getContext()||null==giftInfo||null==giftInfo.getGift()){
            isPlaying =false;
            return;
        }
        removeAllViews();
        if(null == mSvgaImageView){
            initSvgaView();
        }
        isPlaying =true;
        final File giftSvgaFile = GiftResourceManager.getInstance().getGiftSvga(giftInfo.getGift().getBigSvga());
        try {
            if(null!=giftSvgaFile&&giftSvgaFile.exists()){
                try {
                    mInputStream = new FileInputStream(giftSvgaFile);
                    SVGAParser parser = new SVGAParser(getContext());
                    parser.parse(mInputStream, giftSvgaFile.getName(),new SVGAParser.ParseCompletion() {
                        @Override
                        public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                            SVGADrawable drawable = new SVGADrawable(videoItem);
                            if(null!=mSvgaImageView){
                                mSvgaImageView.setImageDrawable(drawable);
                                mSvgaImageView.startAnimation();
                            }else{
                                isPlaying =false;
                            }
                        }
                        @Override
                        public void onError() {
                            isPlaying =false;
                            //播放错误，删除本地动画缓存，下次从网络获取
                            if(giftSvgaFile.exists()){
                                giftSvgaFile.delete();
                            }
                            giftEnd();
                        }
                    }, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    isPlaying =false;
                    startAutoPlayer();
                }
            }else{
                SVGAParser parser = new SVGAParser(getContext());
                parser.parse(new URL(giftInfo.getGift().getBigSvga()), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                        SVGADrawable drawable = new SVGADrawable(videoItem);
                        if(null!=mSvgaImageView){
                            mSvgaImageView.setImageDrawable(drawable);
                            mSvgaImageView.startAnimation();
                        }else{
                            isPlaying =false;
                        }
                    }
                    @Override
                    public void onError() {
                        isPlaying =false;
                        giftEnd();
                    }
                });
            }
        } catch (Exception e){
            isPlaying = false;
            giftEnd();
        } finally {
            //LiveChatUserGradleSpan.getSpannableDrawableFotGift(giftInfo)
            if(null!=mDanmuManager && giftInfo.getGift().isTanmu()) mDanmuManager.addRoomDanmu(giftInfo, RoomDanmuManager.DanmuType.GIFT);
        }
    }

    /**
     * 动画完成，清除动画并移除动画控件，开始下一个动画
     */
    private void giftEnd () {
        if(null!=mSvgaImageView){
            mSvgaImageView.stopAnimation(true);
            mSvgaImageView.setImageResource(0);
            mSvgaImageView.clearAnimation();
        }
        mSvgaImageView=null;
        removeAllViews();
        try {
            if(null!=mInputStream){
                mInputStream.close();
                mInputStream=null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            startAutoPlayer();
        }
    }

    public void onReset(){
        if(null!=mGroupGiftQueue) mGroupGiftQueue.clear();
        if(null!=mSvgaImageView) mSvgaImageView.stopAnimation();
    }

    public void onPause(){
        if(null!=mSvgaImageView) mSvgaImageView.pauseAnimation();
        if(null!=mDanmuManager) mDanmuManager.onPause();
    }

    public void onResume(){
        if(null!=mSvgaImageView) mSvgaImageView.startAnimation();
        if(null!=mDanmuManager) mDanmuManager.onResume();
    }

    /**
     * 对应生命周期调用
     */
    public void onDestroy(){
        if(null!=mGroupGiftQueue) mGroupGiftQueue.clear();
        if(null!=mSvgaImageView) mSvgaImageView.stopAnimation(true);
        this.removeAllViews();
        if(null!=mDanmuManager) mDanmuManager.onDestroy();
        mGroupGiftQueue=null;mSvgaImageView=null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
