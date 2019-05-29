package com.yc.liaolive.upload.bean;

import com.yc.liaolive.contants.Constant;

/**
 * TinyHung@Outlook.com
 * 2017/8/17.
 * OSS文件上传配置
 */

public class UploadParamsConfig {

    private  String callbackAddress=Constant.STS_CALLBACKADDRESS;
    private  String bucket=Constant.STS_BUCKET;
    private  String endpoint=Constant.STS_ENDPOINT;
    private  String callBackHost=Constant.STS_HOST;
    private  String callBackType=Constant.STS_CALLBACL_CONTENT_TYPE;
    private  boolean isEncryptResponse=true;


    public UploadParamsConfig(){
        super();
    }

    public UploadParamsConfig(String callbackAddress, String bucket, String endpoint, String callBackHost, String callBackType, boolean isEncryptResponse) {
        this.callbackAddress = callbackAddress;
        this.bucket = bucket;
        this.endpoint = endpoint;
        this.callBackHost = callBackHost;
        this.callBackType = callBackType;
        this.isEncryptResponse = isEncryptResponse;
    }

    public String getCallbackAddress() {
        return callbackAddress;
    }

    public void setCallbackAddress(String callbackAddress) {
        this.callbackAddress = callbackAddress;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getCallBackHost() {
        return callBackHost;
    }

    public void setCallBackHost(String callBackHost) {
        this.callBackHost = callBackHost;
    }

    public String getCallBackType() {
        return callBackType;
    }

    public void setCallBackType(String callBackType) {
        this.callBackType = callBackType;
    }

    public boolean isEncryptResponse() {
        return isEncryptResponse;
    }

    public void setEncryptResponse(boolean encryptResponse) {
        isEncryptResponse = encryptResponse;
    }

    @Override
    public String toString() {
        return "UploadParamsConfig{" +
                ", callbackAddress='" + callbackAddress + '\'' +
                ", bucket='" + bucket + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", callBackHost='" + callBackHost + '\'' +
                ", callBackType='" + callBackType + '\'' +
                ", isEncryptResponse=" + isEncryptResponse +
                '}';
    }
}
