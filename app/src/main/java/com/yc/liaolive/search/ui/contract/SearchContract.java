
package com.yc.liaolive.search.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.UserInfo;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 搜索
 */

public interface SearchContract {

    interface View extends BaseContract.BaseView {
        void showSearchResul(List<UserInfo> data);
        void showSearchEmpty(String key);
        void showSearchError(int code, String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void search(String key,int page);
    }
}
