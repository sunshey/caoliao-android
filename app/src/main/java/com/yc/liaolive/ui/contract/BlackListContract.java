package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.base.IHide;
import com.yc.liaolive.base.ILoading;
import com.yc.liaolive.base.INoData;
import com.yc.liaolive.base.INoNet;
import com.yc.liaolive.bean.FansInfo;

import java.util.List;

/**
 * Created by wanglin  on 2018/7/10 21:37.
 */
public interface BlackListContract {

    interface View extends BaseContract.BaseView, INoData, INoNet, IHide, ILoading {
        void showBlackList(List<FansInfo> list);

        void showRemoveResult(FansInfo fansInfo);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getBlackList(int page, int page_size);

        void removeBlackList(FansInfo fansInfo);
    }
}
