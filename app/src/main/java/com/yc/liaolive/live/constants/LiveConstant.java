package com.yc.liaolive.live.constants;

/**
 * TinyHung@Outlook.com
 * 2018/5/10
 */

public interface LiveConstant {

    //生成临时的推流地址
    String TEMP_PUSH_URL="https://lvb.qcloud.com/weapp/utils/get_test_pushurl";

    //直播间敞场景模式
    String LIVE_SCENE_MODE_CHAT = "av_chat_room";
    String LIVE_SCENE_MODE_PRIVATE = "av_private_room";
    String LIVE_SCENE_MODE_VOICE = "av_voice_room";

    /**
     * CMD
     */
    //视频通话礼物消息
    String MSG_CUSTOM_ROOM_PRIVATE_GIFT = "CustomCmdCallGift";
    //视频通话信令
    String VIDEO_CALL_CMD = "CustomCmdCall";
    //请求视频通话
    String VIDEO_CALL_CMD_MACKCALL = "call_state_send";
    //取消、拒绝视频通话
    String VIDEO_CALL_CMD_REJECT = "call_state_cancel";
    //呼叫过程中握手
    String VIDEO_CALL_CMD_POST = "call_state_post";
    //超时
    String VIDEO_CALL_CMD_TIMOUT = "call_state_timeout";
    //占线
    String VIDEO_CALL_CMD_BEBUSY = "call_state_busy";
    //离线(用户、主播设置了离线状态)
    String VIDEO_CALL_CMD_OFFLINE = "call_state_offline";
    /**
     * 错误码
     */
    //其他未知
    int CALL_STATE_NOIMAL=1000;
    //占线
    int CALL_STATE_BEBUSY=1001;
    //离线
    int CALL_STATE_OFFLINE=1002;
    //超时
    int CALL_STATE_TIMEOUT =1003;
    //挂断
    int CALL_STATE_CANCEL =1004;
    //信令交互
    int CALL_STATE_POST =1005;
}
