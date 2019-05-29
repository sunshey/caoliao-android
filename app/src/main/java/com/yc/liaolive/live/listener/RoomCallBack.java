package com.yc.liaolive.live.listener;

/**
 * TinyHung@Outlook.com
 * 2018/11/1
 */

public interface RoomCallBack<T> {

    void onError(int errCode, String errInfo);

    void onSuccess(T data);
}
