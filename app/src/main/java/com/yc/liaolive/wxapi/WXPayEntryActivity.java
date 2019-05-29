package com.yc.liaolive.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.pay.alipay.IPayImpl;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, IPayImpl.appid); //appid需换成商户自己开放平台appid
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX && IPayImpl.uOrderInfo != null && IPayImpl.uiPayCallback != null) {
            // resp.errCode == -1 原因：支付错误,可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
            // resp.errCode == -2 原因 用户取消,无需处理。发生场景：用户不支付了，点击取消，返回APP
            Logger.d(TAG,"onResp-->CODE:"+resp.errCode);
            IPayImpl.uOrderInfo.setState(String.valueOf(resp.errCode));
            if (resp.errCode == 0) //支付成功
            {
                IPayImpl.uOrderInfo.setMessage("支付成功");
                IPayImpl.uiPayCallback.onSuccess(IPayImpl.uOrderInfo);
                // 支付错误
            } else if (resp.errCode == -1) {
                String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
                String content="原生微信调起支付错误，可能原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配。errorCode:"+resp.errCode+",errorMsg:"+resp.errStr+",appSign:"+appSign;
                LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_PAY,content,appSign);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showCenterToast("支付错误");
                    }
                });
                IPayImpl.uOrderInfo.setMessage("支付错误");
                IPayImpl.uiPayCallback.onFailure(IPayImpl.uOrderInfo);

            } else if (resp.errCode == -2) // 支付取消
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showCenterToast("支付取消");
                    }
                });
                IPayImpl.uOrderInfo.setMessage("支付取消");
                IPayImpl.uiPayCallback.onCancel(IPayImpl.uOrderInfo);

            } else {
                String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
                String content="原生微信调起支付失败，其他未知错误。errorCode:"+resp.errCode+",errorMsg:"+resp.errStr+",appSign:"+appSign;
                LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_PAY,content,appSign);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showCenterToast("支付失败");
                    }
                });
                IPayImpl.uOrderInfo.setMessage("支付失败");
                IPayImpl.uiPayCallback.onFailure(IPayImpl.uOrderInfo);
            }
            IPayImpl.uOrderInfo = null;
            IPayImpl.uiPayCallback = null;
            finish();
        }
    }
}