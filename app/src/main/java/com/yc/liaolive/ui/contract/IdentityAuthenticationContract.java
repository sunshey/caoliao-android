package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import java.io.File;

/**
 * Created by wanglin  on 2018/7/8 11:09.
 */
public interface IdentityAuthenticationContract {

    interface View extends BaseContract.BaseView {
        void showUploadResult(String data, int flag);
        void finish();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void upload(File file, int flag);

        void identityAuthentication(String name, String id_number, String expiration_date
                , String card_front, String card_back);
    }
}
