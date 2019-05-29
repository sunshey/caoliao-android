package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/9/17
 * 上传文件鉴权及 要上传的对象
 */

public class UploadAuthenticationInfo {
    //配置信息
    private String callback;//业务回调域名
    private String endpoint;//OSS访问域名
    private String bucket;//分区
    private String callbackHost;//回调HOST
    //文件状态
    private int status;
    //鉴权信息
    private String AccessKeyId;//OOS APPID
    private String AccessKeySecret;//OOS Secret
    private String SecurityToken;//OOS Token
    private String Expiration; //上传过期时间戳
    //要上传的对象
    private UploadObjectInfo uploadInfo;

    public String getCallbackHost() {
        return callbackHost;
    }

    public void setCallbackHost(String callbackHost) {
        this.callbackHost = callbackHost;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAccessKeyId() {
        return AccessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        AccessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        AccessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return SecurityToken;
    }

    public void setSecurityToken(String securityToken) {
        SecurityToken = securityToken;
    }

    public String getExpiration() {
        return Expiration;
    }

    public void setExpiration(String expiration) {
        Expiration = expiration;
    }

    public UploadObjectInfo getUploadInfo() {
        return uploadInfo;
    }

    public void setUploadInfo(UploadObjectInfo uploadInfo) {
        this.uploadInfo = uploadInfo;
    }

    @Override
    public String toString() {
        return "UploadAuthenticationInfo{" +
                "status=" + status +
                ", AccessKeyId='" + AccessKeyId + '\'' +
                ", AccessKeySecret='" + AccessKeySecret + '\'' +
                ", SecurityToken='" + SecurityToken + '\'' +
                ", Expiration='" + Expiration + '\'' +
                ", uploadInfo=" + uploadInfo +
                '}';
    }
}
