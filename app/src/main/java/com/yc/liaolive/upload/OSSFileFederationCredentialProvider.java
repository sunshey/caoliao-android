package com.yc.liaolive.upload;

import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.yc.liaolive.bean.UploadAuthenticationInfo;

/**
 * TinyHung@Outlook.com
 * 2018/9/17
 * 上传Token构造
 */

public class OSSFileFederationCredentialProvider extends OSSFederationCredentialProvider {

    private final UploadAuthenticationInfo mUploadAuthenticationInfo;

    public OSSFileFederationCredentialProvider(UploadAuthenticationInfo uploadAuthenticationInfo) {
        this.mUploadAuthenticationInfo=uploadAuthenticationInfo;
    }

    @Override
    public OSSFederationToken getFederationToken() {
        if(null!=mUploadAuthenticationInfo){
            return new OSSFederationToken(mUploadAuthenticationInfo.getAccessKeyId(), mUploadAuthenticationInfo.getAccessKeySecret(), mUploadAuthenticationInfo.getSecurityToken(), mUploadAuthenticationInfo.getExpiration());
        }
        return null;
    }
}
