
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.RoomTaskDataInfo;
import com.yc.liaolive.bean.TaskInfo;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 任务中心
 */

public interface TaskRoomContract {

    interface OnCallBackListener{
        void onSuccess(Object object);
        void onFailure(int code, String errorMsg);
    }

    interface View extends BaseContract.BaseView {

        void showTasks(List<RoomTaskDataInfo> data);
        void showTaskEmpty();
        void showTaskError(int code, String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        //获取任务奖励
        void getTasks();
        //兑换奖励
        void getTaskDraw(TaskInfo taskInfo, OnCallBackListener callBackListener);
    }
}
