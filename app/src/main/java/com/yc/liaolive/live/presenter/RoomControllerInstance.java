package com.yc.liaolive.live.presenter;

import com.yc.liaolive.live.ui.pager.VerticalRoomPager;

/**
 * TinyHung@Outlook.com
 * 2019/1/24
 * 房间交互
 */

public interface RoomControllerInstance {
    /**
     * 新的房间实例
     * @param verticalRoomPager
     */
    void newRoomInstance(VerticalRoomPager verticalRoomPager);

    /**
     * 聊天
     */
    void onInputChatText();

    /**
     * 显示加载弹窗
     */
    void showLoadingDialog(String message);

    /**
     * 隐藏加载弹窗
     */
    void hideLoadingDialog();

    /**
     * 下一个房间
     */
    void onNextRoom();

    /**
     * 界面结束
     */
    void onFinish();

    /**
     * 返回按钮文字
     * @return
     */
    String getButtonText();
}
