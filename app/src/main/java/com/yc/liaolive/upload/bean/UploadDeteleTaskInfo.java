package com.yc.liaolive.upload.bean;

/**
 * TinyHung@Outlook.com
 * 2017/11/22
 * 删除上传的任务状态
 */

public class UploadDeteleTaskInfo {

    private String message;
    private boolean isCancel;
    private int errorCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
