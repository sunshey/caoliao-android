package com.yc.liaolive.common;

import java.util.HashMap;

/**
 * controller 常量类
 * Created by yangxueqin on 2018/11/9.
 */

public class ControllerConstant {

    /**
     * 首页
     */
    public static final String MainActivity = "com.yc.liaolive.ui.activity.MainActivity";

    /**
     * h5页面
     */
    public static final String WebViewActivity = "com.yc.liaolive.webview.ui.WebViewActivity";

    /**
     * 充值页面
     */
    public static final String VipActivity = "com.yc.liaolive.recharge.ui.VipActivity";

    /**
     * 绑定手机界面
     */
    public static final String BindPhoneTaskActivity = "com.yc.liaolive.user.ui.BindPhoneTaskActivity";

    /**
     * 芝麻认证结果
     */
    public static final String ZhimaAuthentiResultActivity = "com.yc.liaolive.user.ui.ZhimaAuthentiResultActivity";

    /**
     * 闪玩
     */
    public static final String GameWebActivity = "com.yc.liaolive.util.game.view.GameWebActivity";

    /**
     * 小额贷
     */
    public static final String LOAD_BOX = "com.yc.loanbox.view.LoanboxMainActivity";

    /**
     * 小程序
     */
    public static final String LOAD_MINI_PROGRAM = "LOAD_MINI_PROGRAM";

    // type映射activity的完整类名class
    private static HashMap<String, String> type2Class;

    static {
        type2Class = new HashMap<>();
        type2Class.put("1", MainActivity);
        type2Class.put("2", WebViewActivity);
        type2Class.put("3", VipActivity);
        type2Class.put("4", BindPhoneTaskActivity);
        type2Class.put("5", ZhimaAuthentiResultActivity);
        type2Class.put("6", GameWebActivity);
        type2Class.put("7", LOAD_BOX);
        type2Class.put("8", LOAD_MINI_PROGRAM);
    }

    /**
     * 初始化controller配置
     */
    public static void init() {
        CaoliaoController.init(type2Class);
    }

}
