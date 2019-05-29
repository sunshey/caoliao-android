package com.yc.liaolive.gift.manager;

import android.view.View;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.util.ScreenUtils;

/**
 * TinyHung@Outlook.com
 * 2018/9/5
 * 礼物功能辅助管理者
 */

public class GiftHelpManager {

    private static GiftHelpManager mInstance;
    private GiftInfo mOldGiftInfo;//旧的选中的项
    private int mOldCountIndex;//旧的选中个数档位
    private int mFragmentIndex;//所在的分类片段位置
    private int mItemPagerIndex;//片段中中所在的分页位置
    private View mOldItemView;//旧的选中的Item
    private int[] mAwardEndLocation;

    public static synchronized GiftHelpManager getInstance(){
        synchronized (GiftHelpManager.class){
            if(null==mInstance){
                mInstance=new GiftHelpManager();
            }
        }
        return mInstance;
    }

    /**
     * 是否存在缓存的实例
     * @return
     */
    public boolean isExitRecoveryState(){
        return null!=mOldGiftInfo;
    }

    public GiftInfo getOldGiftInfo() {
        return mOldGiftInfo;
    }

    public void setOldGiftInfo(GiftInfo oldGiftInfo) {
        mOldGiftInfo = oldGiftInfo;
    }

    public int getOldCountIndex() {
        return mOldCountIndex;
    }

    public void setOldCountIndex(int oldCountIndex) {
        mOldCountIndex = oldCountIndex;
    }

    public int getFragmentIndex() {
        return mFragmentIndex;
    }

    public void setFragmentIndex(int fragmentIndex) {
        mFragmentIndex = fragmentIndex;
    }

    public int getItemPagerIndex() {
        return mItemPagerIndex;
    }

    public void setItemPagerIndex(int itemPagerIndex) {
        mItemPagerIndex = itemPagerIndex;
    }

    public void setOldItemView(View itemView) {
        this.mOldItemView=itemView;
    }

    public View getOldItemView() {
        return mOldItemView;
    }

    /**
     * 保存可供恢复的数据
     * @param oldGiftInfo
     * @param oldCountIndex 旧的选中个数档位
     * @param fragmentIndex 所在的分类片段位置
     * @param itemPagerIndex 片段中中所在的分页位置
     */
    public void saveRecoveryState(GiftInfo oldGiftInfo,int oldCountIndex,int fragmentIndex,int itemPagerIndex){
        this.mOldGiftInfo=oldGiftInfo;
        this.mOldCountIndex=oldCountIndex;
        this.mFragmentIndex=fragmentIndex;
        this.mItemPagerIndex=itemPagerIndex;
    }

    /**
     * 在清空缓存时候调用
     */
    public void onDestroy(){
        mOldGiftInfo=null;mOldCountIndex=0;mFragmentIndex=0;mItemPagerIndex=0; mOldItemView=null;
    }

    /**
     * 设定金币掉落位于屏幕的具体位置
     * @param awardEndLocation
     */
    public void setAwardEndLocation(int[] awardEndLocation) {
        mAwardEndLocation = awardEndLocation;
    }
    /**
     * 获取金币掉落位于屏幕的具体位置
     */
    public int[] getAwardEndLocation() {
        if(null==mAwardEndLocation||mAwardEndLocation.length<=0){
            mAwardEndLocation=new int[2];
            mAwardEndLocation[0]=(ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(45f));
            mAwardEndLocation[1]= (ScreenUtils.getScreenHeight()-ScreenUtils.dpToPxInt(145f)) ;
        }
        return mAwardEndLocation;
    }
}
