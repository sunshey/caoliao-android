package com.yc.liaolive.user.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.user.bean.ZhimaParams;
import com.yc.liaolive.user.bean.ZhimaResult;

/**
 * TinyHung@Outlook.com
 * 2019/1/25
 * 芝麻认证
 */

public interface ZhimaContract {

    interface View extends BaseContract.BaseView {
        void showZhimaParams(ZhimaParams data);
        void showZhimaParamsError(int code,String data);
        void showCheckedZhimaResult(ZhimaResult data);
        void showCheckedZhimaError(int code,String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getZhimaParams(String nickName,String number,String jumpUrl);
        void checkedZhimaResult(String paramsContent);
    }
}
