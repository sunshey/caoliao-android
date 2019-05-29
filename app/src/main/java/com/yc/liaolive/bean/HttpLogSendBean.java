package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * 网络日志上报对象
 * Created by yangxueqin on 2018/11/19.
 */

public class HttpLogSendBean implements Serializable {
    private int httpCode;

    private String message;

    private String requestUrl;

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
}
