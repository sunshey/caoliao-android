package com.yc.liaolive.upload.listener;

import com.yc.liaolive.bean.UploadObjectInfo;

/**
 * TinyHung@Outlook.com
 * 2017/8/17.
 * OSS文件上传监听
 */

public interface FileUploadListener {
    void uploadStart(UploadObjectInfo data);
    void uploadSuccess(UploadObjectInfo data, String extras,boolean isFinlish);
    void uploadProgress(UploadObjectInfo data,int currentCount,int totalCount);
    void uploadFail(UploadObjectInfo data, int stateCode, int errorCode, String errorMsg,boolean isFinlish);
}
