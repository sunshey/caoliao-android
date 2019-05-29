package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.msg.model.bean.CallMessageInfo;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/10/16
 * 主页消息
 */

public interface IndexMsgContract {

    interface View extends BaseContract.BaseView {
        void showListResult(List<CallMessageInfo> data);
        void showListResultEmpty();
        void showListResultError(int code,String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //获取主页消息索引
        void getMessageIndexList();
        void getCallNotesList(String url,int itemType,long last_id,int state);
    }
}
