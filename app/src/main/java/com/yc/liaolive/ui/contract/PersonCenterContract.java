package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.PersonCenterInfo;
import com.yc.liaolive.bean.TabMineUserInfo;

import java.util.List;

/**
 * Created by wanglin  on 2018/7/9 15:59.
 */
public interface PersonCenterContract {

    interface View extends BaseContract.BaseView {
        void showPersonInfo(PersonCenterInfo data);
        void showPersonInfoError(int code, String msg);

        void showPersonList(List<TabMineUserInfo> data);
        void showPersonListError(int code, String msg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getPersonCenterInfo(String to_userid);
        void getItemList();
    }
}
