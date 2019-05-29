package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.base.IHide;
import com.yc.liaolive.base.ILoading;
import com.yc.liaolive.base.INoData;
import com.yc.liaolive.base.INoNet;
import com.yc.liaolive.bean.HelpInfo;

/**
 * Created by wanglin  on 2018/7/8 16:35.
 */
public interface HelpDetailContract {
    interface View extends BaseContract.BaseView, ILoading, IHide, INoNet, INoData {
        void showHelpDetail(HelpInfo data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getHelpInfoDetail(String helpid);
    }
}
