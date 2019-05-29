package com.yc.liaolive.interfaces;

import com.yc.liaolive.bean.UploadObjectInfo;

/**
 * TinyHung@Outlook.com
 * 2018/9/14
 * 文件上传监听
 */

public interface OnUploadObjectListener {
    void onStart();
    void onProgress(long progress);
    void onSuccess(UploadObjectInfo data, String msg);
    void onFail(int code,String errorMsg);
}
