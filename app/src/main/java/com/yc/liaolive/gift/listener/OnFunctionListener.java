package com.yc.liaolive.gift.listener;

import com.yc.liaolive.bean.FansInfo;

/**
 * Created by hty_Yuye@Outlook.com
 * 2018/12/16
 * 直播间模块间交互回调
 */

public interface OnFunctionListener {
    void onSendGift(FansInfo userInfo);
}
