
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.IntegralUser;

import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 积分排行榜
 */

public interface TopTableContract {

    interface View extends BaseContract.BaseView {
        void showTopTbaleList(List<FansInfo> data, IntegralUser userData);
        void showTopTbaleEmpty();
        void showTopTbaleError(int code, String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getTopTables(String homeUserid,String type);
    }
}
