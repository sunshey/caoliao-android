
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;

/**
 * @time 2017/5/23 10:50
 * @des 上传图片
 */
public interface UploadImageContract {

    interface View extends BaseContract.BaseView {
        void showPostImageResult(String data);
        void showPostImageError(int errorCode,String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void onPostImagePhoto(String url,String userID, String filePathm,String pramarKey);
    }
}
