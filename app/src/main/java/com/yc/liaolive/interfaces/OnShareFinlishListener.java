package com.yc.liaolive.interfaces;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * TinyHung@Outlook.com
 * 2017/10/27.
 */

public interface OnShareFinlishListener {
    void onShareStart(SHARE_MEDIA media);
    void onShareResult(SHARE_MEDIA media);
    void onShareCancel(SHARE_MEDIA media);
    void onShareError(SHARE_MEDIA media,Throwable throwable);
}
