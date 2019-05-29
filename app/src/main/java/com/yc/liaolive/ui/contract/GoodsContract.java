
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.recharge.model.bean.RechargeBean;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 充值
 */

public interface GoodsContract {

    interface View extends BaseContract.BaseView {
        void showGoldInfo(RechargeBean data);
        void showGoldEmpty();
        void showGoldError(int code,String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getGoldGoods(int type);
        void getGoldGoods(int type,boolean forbidSelected);
        void getVipGoogsList();
    }
}
