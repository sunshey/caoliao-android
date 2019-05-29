package com.yc.liaolive.upload.listener;

import com.yc.liaolive.bean.UploadObjectInfo;

/**
 * TinyHung@Outlook.com
 * 2017/8/17.
 * OSS文件上传监听
 */

public interface UploadStsStateListener {
    void uploadStart(UploadObjectInfo data);
    void uploadSuccess(UploadObjectInfo data);
    void uploadProgress(UploadObjectInfo data, int progress);
    void uploadFail(UploadObjectInfo data, boolean isCanelTask, String msg);
    void uploadSynch(UploadObjectInfo videoInfo);
}
