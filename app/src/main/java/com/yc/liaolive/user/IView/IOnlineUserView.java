package com.yc.liaolive.user.IView;

import android.app.Activity;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.user.model.bean.OnlineUserBean;

/**
 * 在线用户列表 接口
 * Created by yangxueqin on 18/12/15.
 */
public interface IOnlineUserView extends BaseContract.BaseView{

    Activity getDependType();

    void setDataView(OnlineUserBean userBean);

    void showListEmpty();

    void setListEnd(boolean isEnd);

    void showError(String msg);

    /**
     * 倒计时更新秒
     * @param time
     */
    void setLeftTimeView (String time);

    /**
     * 启动、显示倒计时
     * @param time 单位秒
     */
    void setCallLeftTimeData(long time);

    void setCallLeftCountView(int leftCount);

    void setLeftTimeComplete ();
}
