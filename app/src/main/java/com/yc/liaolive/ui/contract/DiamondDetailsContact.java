package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.DiamondInfo;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2019/1/25
 * 钻石详情
 */

public interface DiamondDetailsContact {

    interface View extends BaseContract.BaseView {
        void showDiamondInfo(DiamondInfo diamondInfoWrapper);
        void showDiamondDetails(List<DiamondInfo> data);
        void showDiamondError(int code, String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getDaimondDetails(String toUserID,String typeID,int assetsType,int page);
    }
}
