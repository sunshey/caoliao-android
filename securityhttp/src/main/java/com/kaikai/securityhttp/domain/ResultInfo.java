package com.kaikai.securityhttp.domain;

import okhttp3.Response;

/**
 * Created by zhangkai on 16/9/19.
 */

public class ResultInfo<T> {
    private int code;

    private String message;
    private T data;

    private int httpCode;
    private String httpMessage;
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getHttpMessage() {
        return httpMessage;
    }

    public void setHttpMessage(String httpMessage) {
        this.httpMessage = httpMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultInfo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", httpCode=" + httpCode +
                ", httpMessage='" + httpMessage + '\'' +
                ", response=" + response +
                '}';
    }
}
