package com.yc.liaolive.pay.alipay;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;

import java.util.List;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * Created by zhangkai on 2017/4/19.
 */

public class IWXPay1Impl extends IPayImpl {

    private static final String TAG = "IWXPay1Impl";
    private IWXAPI msgApi;

    public IWXPay1Impl(Activity context) {
        super(context);
        msgApi = WXAPIFactory.createWXAPI(context, null);
    }

    @Override
    public void pay(final OrderInfo orderInfo, IPayCallback iPayCallback) {
        if (orderInfo == null || orderInfo.getParams() == null) {
            if(null!=iPayCallback) iPayCallback.onFailure(orderInfo);
            ToastUtils.showCenterToast("支付数据解析失败");
            return;
        }
        PayInfo payInfo = orderInfo.getParams();
        if ("SUCCESS".equals(payInfo.getResult_code())) {
            uOrderInfo = orderInfo;
            uiPayCallback = iPayCallback;
            wxpay(payInfo);
        } else {
            orderInfo.setState("10087");//预下订单请求失败
            orderInfo.setMessage("预下订单请求失败");
            if(null!=iPayCallback) iPayCallback.onFailure(orderInfo);
        }
    }

    private void wxpay(PayInfo payInfo) {
        if (isPackageInstalled()) {
            PayReq request = new PayReq();
            request.appId = payInfo.getAppid();
            request.partnerId = payInfo.getMch_id();
            request.prepayId = payInfo.getPrepay_id();
            request.packageValue = "Sign=WXPay";
            request.nonceStr = payInfo.getNonce_str();
            request.timeStamp = payInfo.getTimestamp();
            request.sign = payInfo.getSign();
            IPayImpl.appid = payInfo.getAppid();
            msgApi.registerApp(payInfo.getAppid());
            boolean send = msgApi.sendReq(request);
        } else {
            uOrderInfo.setMessage("你没有安装微信,请先安装...");
            uOrderInfo.setState("10086");//微信应用未安装
            if(null!=uiPayCallback) uiPayCallback.onFailure(uOrderInfo);
        }

    }

    private boolean isPackageInstalled() {

        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(GET_META_DATA);
        if (packageInfos != null) {
            for (PackageInfo packageInfo : packageInfos) {
                if ("com.tencent.mm".equals(packageInfo.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

}
