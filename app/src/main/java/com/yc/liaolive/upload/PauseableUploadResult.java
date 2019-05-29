package com.yc.liaolive.upload;

import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadResult;

/**
 * TinyHung@Outlook.com
 * 2017/9/4.
 * OSS文件分片上传回调实列
 */

public class PauseableUploadResult extends CompleteMultipartUploadResult {
    public PauseableUploadResult(CompleteMultipartUploadResult completeResult) {
        this.setBucketName(completeResult.getBucketName());
        this.setObjectKey(completeResult.getObjectKey());
        this.setETag(completeResult.getETag());
        this.setLocation(completeResult.getLocation());
        this.setRequestId(completeResult.getRequestId());
        this.setResponseHeader(completeResult.getResponseHeader());
        this.setStatusCode(completeResult.getStatusCode());
        this.setServerCallbackReturnBody(completeResult.getServerCallbackReturnBody());
    }
}
