package com.yc.liaolive.user.IView;

import java.util.List;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.base.IHide;
import com.yc.liaolive.base.ILoading;
import com.yc.liaolive.base.INoData;
import com.yc.liaolive.base.INoNet;
import com.yc.liaolive.bean.HelpInfo;

/**
 * Created by wanglin  on 2018/7/8 15:33.
 */
public interface HelpContract {

    interface View extends BaseContract.BaseView,ILoading,IHide,INoData,INoNet {
        void showHelpList(List<HelpInfo> data);

        void shoFailView();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getHelpList();
    }
}
