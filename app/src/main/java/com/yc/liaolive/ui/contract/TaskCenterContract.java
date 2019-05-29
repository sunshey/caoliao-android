
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.TaskInfo;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 任务中心
 */

public interface TaskCenterContract {

    interface View extends BaseContract.BaseView {
        void showTasks(List<TaskInfo> data);
        void showTaskEmpty();
        void showTaskError(int code, String errorMsg);
        //领取任务
        void showGetTaskResult(String data);
        void showGetTaskError(int code,String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        //获取任务奖励
        void getTasks(String type);

        //获取充值奖励
        void getRechargeTasks();

        //兑换奖励
        void getTaskDraw(String task_id,RxBasePresenter.OnResqustCallBackListener callBackListener);
    }
}
