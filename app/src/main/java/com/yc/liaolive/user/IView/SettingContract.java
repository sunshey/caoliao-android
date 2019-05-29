package com.yc.liaolive.user.IView;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.user.model.bean.SettingActivityMenuBean;

/**
 * Created by wanglin  on 2018/7/8 17:13.
 */
public interface SettingContract {
    interface View extends BaseContract.BaseView {
        void showResult(String data);

        void setActivityMenu(SettingActivityMenuBean bean);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void setVideoExpire(String chat_deplete, String time);

        void getActivityMenu();
    }
}
