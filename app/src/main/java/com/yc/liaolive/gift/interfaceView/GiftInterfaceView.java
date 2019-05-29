package com.yc.liaolive.gift.interfaceView;

import com.yc.liaolive.live.bean.GiftInfo;

/**
 * TinyHung@Outlook.com
 * 2018/9/11
 * 礼物面板辅助
 */

public interface GiftInterfaceView {
    //停止倒计时计时器
    void stopCountdown();
    //选中了礼物
    void selectedGiftChanged(GiftInfo giftInfo,int count);
}
