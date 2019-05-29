package com.yc.liaolive.gift.ui.pager;

import android.app.Activity;
import android.text.TextUtils;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BasePager;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.DialogGiftItemBinding;
import com.yc.liaolive.gift.interfaceView.GiftInterfaceView;
import com.yc.liaolive.gift.manager.GiftHelpManager;
import com.yc.liaolive.gift.view.GiftBoardView;
import com.yc.liaolive.live.bean.GiftTypeInfo;
import com.yc.liaolive.manager.ApplicationManager;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/11/2
 * 礼物面板片段
 */

public class GiftFaceplatePager extends BasePager<DialogGiftItemBinding> implements Observer {

    private static GiftInterfaceView mInterFaceView;
    private int mTypeID;
    private int mSourceApiType;//礼物素材API TYPE
    private int mIndex;
    private GiftBoardView mGiftBoardView;
    private boolean mIsRecovery;//是否需要恢复至最后一次关闭礼物面板的项

    public GiftFaceplatePager(Activity context,GiftTypeInfo giftTypeInfo, int index,boolean isRecovery, GiftInterfaceView interfaceView,int sourceApiType) {
        super(context);
        mTypeID=giftTypeInfo.getId();
        mIndex=index;
        mIsRecovery=isRecovery;
        mInterFaceView=interfaceView;
        this.mSourceApiType= sourceApiType;
        setContentView(R.layout.dialog_gift_item);
    }

    @Override
    public void initViews() {
        ApplicationManager.getInstance().addObserver(this);
        //初始化礼物面板
        mGiftBoardView = new GiftBoardView(getContext());
        mGiftBoardView.setGiftClassID(mTypeID);
        //UI配置
        mGiftBoardView.setInterFaceView(mInterFaceView);
        mGiftBoardView.setFragmentIndex(mIndex);
        bindingView.giftItemContent.addView(mGiftBoardView);
    }

    @Override
    public void initData() {
        if((0==mIndex&&null!=mGiftBoardView)||mIndex== GiftHelpManager.getInstance().getFragmentIndex()){
            if(null!=mGiftBoardView) mGiftBoardView.onVisible(mIsRecovery,mSourceApiType);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null!=mGiftBoardView){
            mGiftBoardView.onVisible(mIsRecovery,mSourceApiType);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        ApplicationManager.getInstance().removeObserver(this);
        if(null!=mGiftBoardView) {
            mGiftBoardView.onDestroy();
            mGiftBoardView=null;
        }
        super.onDestroy();
    }

    /**
     * 注册观察者
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg&&arg instanceof String){
            //还原本界面可能选中的项
            if(TextUtils.equals(Constant.OBSERVER_GIFT_CLEAN_SELECTED_REST, (String) arg)){
                if(null!=mGiftBoardView) mGiftBoardView.initializtionView();
            }
        }
    }
}
