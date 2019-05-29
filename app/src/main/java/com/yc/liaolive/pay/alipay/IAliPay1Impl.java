package com.yc.liaolive.pay.alipay;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.ToastUtils;

import java.util.Map;

/**
 * Created by zhangkai on 2017/3/18.
 */

public class IAliPay1Impl extends IPayImpl {


//    private static String APPID = PayConfig.APPID;
//    private static String EMAIL = PayConfig.EMAIL;
//    private static String PARTNERID = PayConfig.PARTNERID;
//    private static String PRIVATE_KEY = PayConfig.PRIVATE_KEY;
//    private static String NOTIFY_URL = PayConfig.NOTIFY_URL;

    public IAliPay1Impl(Activity context) {
        super(context);
    }

    @Override
    public void pay(OrderInfo orderInfo, IPayCallback iPayCallback) {
//        if (orderInfo.getParams() != null) {
//            APPID = get(orderInfo.getParams().getAppid(), APPID);
//            PARTNERID = get(orderInfo.getParams().getPartnerid(), PARTNERID);
//            EMAIL = get(orderInfo.getParams().getEmail(), EMAIL);
//            PRIVATE_KEY = get(orderInfo.getParams().getPrivatekey(), PRIVATE_KEY);
//            NOTIFY_URL = get(orderInfo.getParams().getNotify_url(), NOTIFY_URL);
//        }
        alipay(orderInfo, iPayCallback);
    }

    /**
     * 支付宝支付
     */
    private void alipay(OrderInfo orderInfo, IPayCallback iPayCallback) {
        if (TextUtils.isEmpty(orderInfo.getPayurl())) {
            ToastUtils.showCenterToast("支付地址为空，请稍后重试");
            orderInfo.setMessage("支付失败");
            iPayCallback.onCancel(orderInfo);
            return;
        }
//        String privatekey = GoagalInfo.get().getPublicKey(PRIVATE_KEY);
//        Map<String, String> params = buildOrderParamMap(money, theOrderName, theOrderDetail, ordeID);
//        String orderParam = buildOrderParam(params);//对订单地址排序
//        String sign = getSign(params, privatekey);
        try {
//            if (!TextUtils.isEmpty(sign)) {
                // 完整的符合支付宝参数规范的订单信息
//                final String payInfo = orderParam + "&" + sign;
                //调用新线程支付
                new Thread(new AlipayRunnable(orderInfo, iPayCallback)).start();
//            } else {
//                new IllegalThreadStateException("签名错误");
//            }
        } catch (Exception e) {
        }
    }


    /**
     * 支付宝支付
     */

    private class AlipayRunnable implements Runnable {
        private OrderInfo orderInfo;
        private IPayCallback iPayCallback;

        private String payString;

        public AlipayRunnable(OrderInfo orderInfo,  IPayCallback iPayCallback) {
            this.orderInfo = orderInfo;
            this.payString = orderInfo.getPayurl();
            this.iPayCallback = iPayCallback;
        }

        @Override
        public void run() {
            // 构造PayTask 对象
            PayTask alipay = new PayTask(mContext);
            // 调用支付接口，获取支付结果
            Map<String, String> result = alipay.payV2(payString, false);
            PayResult payResult = new PayResult(result);
            String resultStatus = payResult.getResultStatus();
            orderInfo.setState(resultStatus);
            if (TextUtils.equals(resultStatus, "9000")) {
                orderInfo.setMessage("支付成功");
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iPayCallback.onSuccess(orderInfo);
//                        ToastUtils.showCenterToast("支付成功");
                    }
                });

            } else if (TextUtils.equals(resultStatus, "6001")) {
                orderInfo.setMessage("支付取消");
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        ToastUtils.showCenterToast("支付取消");
                        iPayCallback.onCancel(orderInfo);
                    }
                });

            } else {
                //payResult
                String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
                String content="原生支付宝调起支付失败。errorCode:"+resultStatus+",errorMsg:"+payResult.getResult()+",appSign:"+appSign;
                LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_PAY,content,appSign);
                orderInfo.setMessage("支付失败");
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        ToastUtils.showCenterToast("支付失败");
                        iPayCallback.onFailure(orderInfo);
                    }
                });


            }
        }
    }


//    /**
//     * 对订单签名
//     *
//     * @param map
//     * @param rsaKey
//     * @return
//     */
//    public static String getSign(Map<String, String> map, String rsaKey) {
//        List<String> keys = new ArrayList<>(map.keySet());
//        // key排序
//        Collections.sort(keys);
//
//        StringBuilder authInfo = new StringBuilder();
//        for (int i = 0; i < keys.size() - 1; i++) {
//            String key = keys.get(i);
//            String value = map.get(key);
//            authInfo.append(buildKeyValue(key, value, false));
//            authInfo.append("&");
//        }
//
//        String tailKey = keys.get(keys.size() - 1);
//        String tailValue = map.get(tailKey);
//        authInfo.append(buildKeyValue(tailKey, tailValue, false));
//
//        String oriSign = SignUtils.sign(authInfo.toString(), rsaKey, true);
//        String encodedSign = "";
//
//        try {
//            encodedSign = URLEncoder.encode(oriSign, "UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "sign=" + encodedSign;
//    }

//    /**
//     * 生成订单信息
//     *
//     * @param money
//     * @param theOrderName
//     * @param theOrderDetail
//     * @param ordeID
//     * @return
//     */
//
//    private static Map<String, String> buildOrderParamMap(String money, String theOrderName, String theOrderDetail, String ordeID) {
//
//        Map<String, String> keyValues = new HashMap<>();
//        keyValues.put("app_id", APPID);
//        keyValues.put("partner", PARTNERID);
//        keyValues.put("seller_id", EMAIL);
//        keyValues.put("notify_url", NOTIFY_URL);
//        keyValues.put("biz_content", "{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"" + money + "\",\"subject\":\"" + theOrderName + "\",\"body\":\"" + theOrderDetail + "\",\"out_trade_no\":\"" + ordeID + "\"}");
//        keyValues.put("charset", "utf-8");
//        keyValues.put("method", "alipay.trade.app.pay");
//        keyValues.put("sign_type", "RSA2");
//        keyValues.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
//        keyValues.put("payment_type", "1");
//        keyValues.put("version", "1.0");
//
//        return keyValues;
//    }


//    private static String buildOrderParam(Map<String, String> map) {
//        List<String> keys = new ArrayList<>(map.keySet());
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < keys.size() - 1; i++) {
//            String key = keys.get(i);
//            String value = map.get(key);
//            sb.append(buildKeyValue(key, value, true));
//            sb.append("&");
//        }
//        String tailKey = keys.get(keys.size() - 1);
//        String tailValue = map.get(tailKey);
//        sb.append(buildKeyValue(tailKey, tailValue, true));
//        return sb.toString();
//    }

//    private static String buildKeyValue(String key, String value, boolean isEncode) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(key);
//        sb.append("=");
//        if (isEncode) {
//            try {
//                sb.append(URLEncoder.encode(value, "UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                sb.append(value);
//            }
//        } else {
//            sb.append(value);
//        }
//        return sb.toString();
//    }


    private class PayResult {
        private String resultStatus;
        private String result;
        private String memo;

        public PayResult(Map<String, String> rawResult) {
            if (rawResult == null) {
                return;
            }

            for (String key : rawResult.keySet()) {
                if (TextUtils.equals(key, "resultStatus")) {
                    resultStatus = rawResult.get(key);
                } else if (TextUtils.equals(key, "result")) {
                    result = rawResult.get(key);
                } else if (TextUtils.equals(key, "memo")) {
                    memo = rawResult.get(key);
                }
            }
        }

        @Override
        public String toString() {
            return "resultStatus={" + resultStatus + "};memo={" + memo
                    + "};result={" + result + "}";
        }

        /**
         * @return the resultStatus
         */
        public String getResultStatus() {
            return resultStatus;
        }

        /**
         * @return the memo
         */
        public String getMemo() {
            return memo;
        }

        /**
         * @return the result
         */
        public String getResult() {
            return result;
        }
    }
}
