package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.DiamondInfo;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/10/20
 * 积分钻石记录
 */

public interface AssetContract {

    interface View extends BaseContract.BaseView {
        void showListResult(List<DiamondInfo> data);
        void showListResultEmpty();
        void showListResultError(int code, String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getAssetsList(String typeID, int itemType, int page);
    }
}
