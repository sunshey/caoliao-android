
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.FansInfo;

import java.util.List;

/**
 * @time 2017/5/23 10:50
 * @des 获取关注、粉丝
 */
public interface AttachFirendContract {

    interface View extends BaseContract.BaseView {
        //粉丝
        void showList(List<FansInfo> data);
        void showListEmpty(String data);
        void showListError(int code,String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getAttachFirends(String url,String userID, String page);
    }
}
