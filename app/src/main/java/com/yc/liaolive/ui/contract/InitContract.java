
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.AllGiftInfo;
import com.yc.liaolive.live.bean.GiftTypeInfo;
import com.yc.liaolive.index.ui.MainActivity;

import java.util.List;


/**
 * @time 2017/5/24 09:13
 * @des 初始化
 */
public interface InitContract {


    /**
     * 获取房间信息回调
     */
    interface OnCallBackListener{
        void onSuccess(AllGiftInfo data);
        void onFailure(int code,String errorMsg);
    }

    /**
     * 礼物类别
     */
    interface OnGiftTypeCallBackListener{
        void onSuccess(List<GiftTypeInfo> data);
        void onFailure(int code,String errorMsg);
    }


    interface View extends BaseContract.BaseView {

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void init(MainActivity context);
        void getAllGift(OnCallBackListener callBackListener);
    }
}
