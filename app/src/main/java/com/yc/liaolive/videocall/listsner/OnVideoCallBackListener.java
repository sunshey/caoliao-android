package com.yc.liaolive.videocall.listsner;

/**
 * TinyHung@Outlook.com
 * 2018/12/28
 */

public interface OnVideoCallBackListener {
    void onSuccess(Object object);
    void onFailure(int code, String errorMsg);
}
