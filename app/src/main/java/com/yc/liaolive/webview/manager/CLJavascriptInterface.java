package com.yc.liaolive.webview.manager;

/**
 * Created by jingbin on 2016/11/17.
 * js通信接口
 */

public class CLJavascriptInterface {

    private OnJsListener mJsListener;

    public void setOnJsListener(OnJsListener mJsListener) {
        this.mJsListener = mJsListener;
    }

    public CLJavascriptInterface() {
    }

    @android.webkit.JavascriptInterface
    public void setJsContent(String eventName, String eventData) {
        mJsListener.setJsContent(eventName, eventData);
    }

    public interface OnJsListener {
        void setJsContent(String eventName, String data);
    }
}
