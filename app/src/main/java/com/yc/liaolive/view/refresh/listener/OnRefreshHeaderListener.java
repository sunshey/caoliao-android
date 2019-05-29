package com.yc.liaolive.view.refresh.listener;

/**
 * TinyHung@Outlook.com
 * 2017/9/27.
 * 提供给自定义头部处理逻辑的监听器
 */

public interface OnRefreshHeaderListener {

    /**下拉时第一次触发*/
    void onThuchPull();

    /**持续下拉调用*/
    void onPositionChange(float scrollTop, float dragDistance, float dragPercent);

    /**正在刷新的时候调用*/
    void onRefreshing();

    /**开始刷新*/
    void onRefreshStart();

    /**刷新成功调用*/
    void onRefreshComplete();

    /**结束刷新*/
    void onRefreshEnd();

    /**刷新成功显示刷新结果*/
    void onRefreshNewCount(int newCount);

    /**刷新失败*/
    void onRefreshError();

    /**还原*/
    void onReset();
}
