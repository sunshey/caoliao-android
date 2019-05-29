package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.live.bean.RoomList;
import java.util.List;


/**
 * @time 2018/5/24
 * @des 首页-头部
 */
public interface IndexHeaderContract {

    interface View extends BaseContract.BaseView {
        //幻灯片
        void showBannerResult(List<BannerInfo> data);
        void showBannerResultEmpty();
        void showBannerResultError(int code,String errorMsg);
        //推荐位
        void showRecommendAnchors(List<RoomList> data);
        void showRecommendEmpty();
        void showRecommendError(int code,String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getBanners();
        void getRecommendAnchor(String type, int page);//推荐位主播
    }
}
