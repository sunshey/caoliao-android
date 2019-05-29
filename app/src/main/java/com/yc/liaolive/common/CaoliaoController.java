package com.yc.liaolive.common;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.loanboxsdk.LoanboxSDK;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Activity控制器
 * <p/>
 * <p/>
 * 1.className启动方式<br>
 * Controller.startActivity(Controller.MainActivity);
 * <p/>
 * <p/>
 * 2.className启动方式，带自定义参数<br>
 * Controller.startActivity(Controller.MainActivity, "id", "123",
 * "title", "标题");
 * <p/>
 * <p/>
 * 3.className启动方式，带自定义参数，自定义Intent启动flag<br>
 * Intent intent = Controller.createIntent(Controller.MainActivity, "id",
 * "123", "title", "标题");<br>
 * intent.putExtra("boolKey", true);<br>
 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);<br>
 * intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);<br>
 * Controller.startActivity(intent);
 * <p/>
 * <p/>
 * 4.uri启动方式<br>
 * Controller.startActivityForUri("caoliao://jump?type=xx&content=xx");
 * <p/>
 * <p/>
 * 5.uri启动方式，自定义Intent启动flag<br>
 * Intent intent = Controller.createIntentForUri("caoliao://jump?type=xx&content=xx");<br>
 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);<br>
 * intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);<br>
 * Controller.startActivity(intent);
 * <p/>
 * <p/>
 * 6.自定义Intent启动<br>
 * Intent intent = Controller.createIntent(Controller.MainActivity);<br>
 * intent.putExtra("boolKey", true);<br>
 * activity.startActivityForResult(intent, 0);
 *
 * @author weige
 */

public class CaoliaoController {

    private static final String TAG = "CaoliaoController";

    public static final String SCHEME = "caoliao";
    public static final String SCHEME_HUAYAN = "huayan";

    public static final String SCHEME_PREFIX = SCHEME + "://";// schema前缀

    public static final String HOST = "jump";

    public static final String HOST_ACTION = "action";

    public static final String URI_TYPE = "type";

    public static final String URI_CONTENT = "content";

    public static final String URI_SOURCE = "source";

    public static final String URI_CL = "uri_cl";

    public static final String TARGET_INTENT = "target_intent";


    private static Context context = AppEngine.getApplication();

    // type映射activity的完整类名class
    private static HashMap<String, String> type2Class;

    static {
        type2Class = new HashMap<>();
    }


    /**
     * 初始化需要scheme启动的activity
     *
     * @param mType2Class
     */
    public static void init(HashMap<String, String> mType2Class) {
        type2Class.putAll(mType2Class);
    }


    /**
     * 根据定义的类型获取启动Activity class
     *
     * @param type 类型定义：
     * @return Activity
     */
    public static String getClass(String type) {
        return type2Class.get(type);
    }

    /**
     * 根据className启动Activity，参考startActivity(Intent targetIntent)
     *
     * @param className 定义见：AndroidManifest.xml
     */
    public static void startActivity(String className) {
        startActivity(createIntent(className));
    }

    /**
     * 根据className启动Activity，参考startActivity(Intent targetIntent)
     *
     * @param className 定义见：AndroidManifest.xml
     * @param args 附加到Intent的参数，必须是偶数个arg，见：createIntent(String, String...)
     */
    public static void startActivity(String className, String... args) {
        Intent intent = createIntent(className, args);
        startActivity(intent);
    }

    /**
     * 根据Uri启动Activity，<br>
     * 参考：createIntentForUri(String uriString)， startActivity(Intent targetIntent)
     *
     * @param uriString Uri
     */
    public static void startActivityForUri(String uriString) {
        start(uriString);
    }

    /**
     * Uri格式：
     * 1）跳转 caoliao://jump?type=xxx&content=xxx
     * 2）事件 caoliao://action?type=action_name&content=xxx
     * 根据Uri启动Activity或者发送EventBus事件
     *
     * @param uriString Uri
     */
    public static void start(String uriString) {
        start(uriString,false,"");
    }

    /**
     * Uri格式：
     * 1）跳转 caoliao://jump?type=xxx&content=xxx
     * 2）事件 caoliao://action?type=action_name&content=xxx
     * 根据Uri启动Activity或者发送EventBus事件
     *
     * @param uriString Uri
     * @param uriString isAddPost 是否需要上报至友盟
     * @param uriString headContent 头部标识，用户定位是列表点击还是详情页点击
     */
    public static void start(String uriString,boolean isAddPost,String headContent) {
        if (TextUtils.isEmpty(uriString)) {
            return;
        }
        Uri uri = Uri.parse(uriString);
        if (SCHEME.equalsIgnoreCase(uri.getScheme())
                || SCHEME_HUAYAN.equalsIgnoreCase(uri.getScheme())) {
            if (HOST.equalsIgnoreCase(uri.getHost())) {
                HashMap<String, String> mapBean = parseUri(uriString);
                String action_name = mapBean.get(URI_TYPE);
                String className = getClass(action_name);
                if (!TextUtils.isEmpty(action_name)) {
                    if(className.equals(ControllerConstant.LOAD_BOX)){
                        String channel_id="122";
                        if(!TextUtils.isEmpty(mapBean.get("channle_id"))){
                            channel_id=mapBean.get("channle_id");
                        }
                        MobclickAgent.onEvent(AppEngine.getApplication().getApplicationContext(),headContent+"channel_id_"+channel_id);
                        LoanboxSDK loanboxSDK = LoanboxSDK.defaultLoanboxSDK();
                        loanboxSDK.setChannelId(channel_id);
                        loanboxSDK.init(AppEngine.getApplication());
                        if(!TextUtils.isEmpty(UserManager.getInstance().getPhone())){
                            loanboxSDK.setPhone(UserManager.getInstance().getPhone());
                        }
                        loanboxSDK.open();
                    }else if(className.equals(ControllerConstant.LOAD_MINI_PROGRAM)){
                        String gh_id = mapBean.get("gh_id");
                        String path = mapBean.get("path");
                        if(!TextUtils.isEmpty(gh_id)&&!TextUtils.isEmpty(path)){
                            MobclickAgent.onEvent(VideoApplication.getInstance().getApplicationContext(),headContent+"load_mini_program_"+gh_id);
                            IWXAPI api = WXAPIFactory.createWXAPI(VideoApplication.getInstance().getApplicationContext(), Constant.LOGIN_WX_KEY);
                            WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                            // 填小程序原始id
                            req.userName = gh_id;
                            //拉起小程序页面的可带参路径，不填默认拉起小程序首页
                            req.path = path;
                            //可选打开 开发版，体验版和正式版
                            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
                            api.sendReq(req);
                        }
                    }else{
                        Intent intent = createIntentForUri(uriString,isAddPost,headContent);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    /**
     * 启动Activity<br>
     * 会给传入的Intent附加FLAG_ACTIVITY_NEW_TASK
     *
     * @param targetIntent 目标页面
     */
    public static void startActivity(Intent targetIntent) {
        targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(targetIntent);
    }

    /**
     * 根据className创建Intent
     *
     * @param className 定义：AndroidManifest.xml
     * @return Intent
     */
    public static Intent createIntent(String className) {
        Intent intent = new Intent();
        intent.setClassName(context, className);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    /**
     * 根据className和args创建Intent
     *
     * @param className 定义：AndroidManifest.xml
     * @param args 附加到Intent的参数，必须是偶数个arg
     * @return Intent
     */
    public static Intent createIntent(String className, String... args) {
        Intent intent = createIntent(className);
        if (args != null && args.length % 2 == 0) {
            for (int i = 0; i < args.length; i += 2) {
                intent.putExtra(args[i], args[i + 1]);
            }
        }
        return intent;
    }

    /**
     * 根据Uri创建Intent，Uri格式：huiguo://jump?type=xx&content=xx<br>
     * type参数会解析成对应的Class，content参数会附加到Intent的参数中：<br>
     * intent.putExtra("content", content);<br>
     * 如果content中的内容是json，会解析json的key-value键值对，并将其附加到Intent中：<br>
     * intent.putExtra(key, json.optString(key));
     * @param uriString Uri
     * @param isAddPost 是否上报点击记录
     * @param headContent 事件头部，用于描述是列表点击还是内页点击
     * @return Intent
     */
    public static Intent createIntentForUri(String uriString,boolean isAddPost,String headContent) {
        Intent intent = new Intent();
        HashMap<String, String> mapBean = parseUri(uriString);
        String type = mapBean.get(URI_TYPE);
        String className = getClass(type);
        //上报至友盟统计事件
        if(isAddPost&&!TextUtils.isEmpty(className)){
            if(TextUtils.isEmpty(headContent)) headContent="";
            String id=headContent;
            if(ControllerConstant.GameWebActivity.equals(className)){
                id=headContent+"ad_click_game_shanwan";
            }else if(ControllerConstant.VipActivity.equals(className)){
                id=headContent+"ad_click_vip";
            }else if(ControllerConstant.WebViewActivity.equals(className)){
                id=headContent+"ad_click_web";
            }else if(ControllerConstant.BindPhoneTaskActivity.equals(className)){
                id=headContent+"ad_click_bind_phone";
            }
            MobclickAgent.onEvent(AppEngine.getApplication().getApplicationContext(),id);
        }
        if (!TextUtils.isEmpty(className)) {
            intent.setClassName(context, className);
        } else {
            intent.setClassName(context, ControllerConstant.MainActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        for (String key : mapBean.keySet()) {
            intent.putExtra(key, mapBean.get(key));
        }
        return intent;
    }

    /**
     * 根据host解析目标
     * @param hostUrl
     * @return
     */
    public static String getClassName(String hostUrl){
        if(TextUtils.isEmpty(hostUrl)) return "";
        HashMap<String, String> mapBean = parseUri(hostUrl);
        String type = mapBean.get(URI_TYPE);
        return getClass(type);
    }

    /**
     * 根据Uri数据来封装成MapBean
     * type 和 source，直接附加到map中
     * 如果content中的内容是String，直接附加到map中
     * 如果content中的内容是json，会解析json的key-value键值对后，附加到map中
     *
     * @param uriString Uri
     * @return
     */
    public static HashMap parseUri(String uriString) {
        HashMap<String, String> mapBean = new HashMap<>();

        mapBean.put(URI_CL, uriString);

        Uri uri = Uri.parse(uriString);

        String type = uri.getQueryParameter(URI_TYPE);
        mapBean.put(URI_TYPE, type);

//        String source = uri.getQueryParameter(URI_SOURCE);
//        if (!TextUtils.isEmpty(source)) {
//        }

        String content = null;
        try {
            // 使用截取的方式获取Uri中的content，服务器对content没有做URL Encode，
            // getQueryParameter获取到的可能不完整
            // content = uri.getQueryParameter(URI_CONTENT);
            int index = uriString.indexOf(URI_CONTENT);
            if (index > -1) {
                content = uriString.substring(index + URI_CONTENT.length() + 1);
                content = URLDecoder.decode(content, "UTF-8");
            }
            if (!TextUtils.isEmpty(content)) {
                JSONObject json = new JSONObject(content);
                for (Iterator<?> iterator = json.keys(); iterator.hasNext(); ) {
                    String key = (String) iterator.next();
                    mapBean.put(key, json.optString(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!TextUtils.isEmpty(content)) {
                mapBean.put(URI_CONTENT, content);
            }
        }
        return mapBean;
    }

}

