package com.yc.liaolive.gift.listener;

import com.yc.liaolive.live.bean.AwardInfo;

/**
 * Created by hty_Yuye@Outlook.com
 * 2018/12/16
 * 动画执行监听
 */

public interface AnimatorPlayListener {
    /**
     * 此动画属性是否和自己相关
     * @param awardInfo 礼物信息
     */
    void onStart(AwardInfo awardInfo);

    /**
     * 动画播放结束
     */
    void onEnd();
}
