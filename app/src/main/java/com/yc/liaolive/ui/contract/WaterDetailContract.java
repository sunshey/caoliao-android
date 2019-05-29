
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.RechargeFillInfo;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 用户信息
 */

public interface WaterDetailContract {

    interface View extends BaseContract.BaseView {
        //明细
        void showUserDetsilsResult(RechargeFillInfo userInfo);
        void showUserDetsilsError(int code, String msg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //获取明细
        void getUserWaterDetsils(String userID);
    }
}
