package com.yc.liaolive.index.contract;

import android.app.Activity;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.index.model.bean.NearbyUserBean;

/**
 * 附近用户 管理
 * Created by yangxueqin on 19/1/18.
 */
public interface INearbyUserView extends BaseContract.BaseView{

    Activity getDependType();

    void setDataView(NearbyUserBean userBean);

    void showListEmpty();

    void setListEnd(boolean isEnd);

    void showError(String message);
}
