package com.yc.liaolive.contants;

import android.os.Environment;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.manager.HostManager;

/**
 * TinyHung@outlook.com
 * 2017/3/18 9:59
 * 所有和网络相关的配置
 */
public class NetContants {

    private static NetContants mInstance;
    public static final int API_RESULT_CODE = 1;//正确的返回值
    public static final int API_RESULT_SEND_MSG_ERROR = 1001;//只有会员才能发送消息
    public static final int API_APP_CLOSURE = 1010;//APP封禁
    public static final int API_RESULT_NO_BIND_PHONE = 1114;//用户未绑定手机号码
    public static final int API_RESULT_NO_BIND_ZHIMA = 1115;//用户未芝麻认证
    public static final int API_RESULT_NO_VIP_TIPS = 1116;//非VIP弹窗提示
    public static final int API_RESULT_ARREARAGE_CODE = 1303;//金币不足
    public static final int API_RESULT_WHITE_ARREARAGE_CODE = 1305;//白名单用户今日钻石不足
    public static final int API_RESULT_CANT_PAY_CODE = 1409;//不可以支付 不在支付时间段内
    public static final int API_RESULT_CANT_FIND = 1505;//文件未找到
    public static final int API_RESULT_BUY = 1507;//需要购买
    public static final int API_RESULT_APP_SHOULD_UPDATE = 1702;//版本失效，需要请求版本更新
    public static final int API_RESULT_USER_EXIST = 1109;//用户已存在
    public static final int API_RESULT_USER_OFLINE = 2006;//用户未设置来电勿扰且处于离线状态
    public static final int PAGE_SIZE = 20;//分页大小
    public static final String NET_REQUST_ERROR = "请求失败,请检查网络连接状态";
    public static final String NET_REQUST_JSON_ERROR = "服务器返回数据格式不正确";

    public static synchronized NetContants getInstance() {
        synchronized (NetContants.class) {
            if (null == mInstance) {
                mInstance = new NetContants();
            }
        }
        return mInstance;
    }

    //草聊域名配置
    public static final String SERVER_URL = "http://z.tn990.com/clzhibo.php";
//    public static final String SERVER_URL = "http://z.197754.com/clzhibo.php";
    //ttvideo域名配置
    public static final String SERVER_URL_TTVIDEO = "http://z.197754.com/newzb.php";
    //开发版本
    public static final String API_DEVELOP = "http://zbtest.6071.com";
    //灰度版本
    public static final String API_TEST = "http://t.tn990.com";
    //预发布版本
    public static final String API_PRE = "http://b.clyfb.dandanq.cn";
    //线上版本
    public static final String API_RELEASE = "https://a.tn990.com";
    //ttvideo 应用请求域名
    public static final String API_RELEASE_TTVIDEO = "http://a.tnxxjs.com";

    //日志-支付
    public static final int POST_ACTION_TYPE_RECHGRE = 10001;
    //日志-视频通话
    public static final int POST_ACTION_TYPE_CALL = 10002;
    //日志-首页数据拉取
    public static final int POST_ACTION_TYPE_INDEX = 10003;
    //直播间大厅日志
    public static final int POST_ACTION_TYPE_ROOM = 10004;
    //群组消息
    public static final int POST_ACTION_TYPE_GROUP_MESSAGE = 10005;
    //赠送礼物失败状态上报
    public static final int POST_ACTION_TYPE_GIFT_SEND = 10006;
    //网络请求错误上报
    public static final int POST_ACTION_TYPE_REQUST_FAILD = 10007;
    //视频通话数据统计
    public static final int POST_ACTION_TYPE_CALL_POST = 10008;
    //路由访问记录
    public static final int POST_ACTION_TYPE_WEB_HOST = 11001;

    /**
     * 网络状态
     */
    public static final int NETWORK_STATE_ERROR = -1;             // 网络状态获取异常
    public static final int NETWORK_STATE_NO_CONNECTION = 0;      // 网络状态当前没有连接
    public static final int NETWORK_STATE_WIFI = 1;               // 当前使用的是wifi
    public static final int NETWORK_STATE_3G = 2;                 // 当前使用的是3g

    //缓存目录
    public static final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/caoliao/cache/";
    //微信视频文件目录
    public static final String WEICHAT_VIDEO_PATH = Environment.getExternalStorageDirectory() + "/tencent/MicroMsg";

    /**
     * 根据code返回对象的提示信息
     * @param data
     * @return
     */
    public static String getErrorMsg(ResultInfo data) {
        if (null == data || null == data.getMsg()) return "未知错误";
        return data.getMsg();
    }

    //日志上报
    private String URL_LOG_UPLOAD = "/logs.json";
    public String URL_LOG_UPLOAD() {
        return HostManager.getInstance().getRootHost() + URL_LOG_UPLOAD;
    }

    //视频通话、直播 日志
    private String URL_LOG_ACTION = "/chatlogs.json";
    public String URL_LOG_ACTION() {
        return HostManager.getInstance().getRootHost() + URL_LOG_ACTION;
    }

    private String URL_LOGIN_SERVER = "/user_services.html";
    //默认的登录服务协议
    public String URL_LOGIN_SERVER() {
        return HostManager.getInstance().getRootHost() + URL_LOGIN_SERVER;
    }


    /**
     * 房间模块
     */
    private String URL_ROOM = "room/";

    //派发房间号
    private String URL_GET_ROOMID = "get_room_id";

    public String URL_GET_ROOMID() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_GET_ROOMID;
    }

    //刷新房间信息
    private String URL_UPLOAD_ROOM = "upload_room";

    public String URL_UPLOAD_ROOM() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_UPLOAD_ROOM;
    }

    //直播间列表
    private String URL_ROOM_LIST = "room_list";

    public String URL_ROOM_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_ROOM_LIST;
    }

    //推荐热门直播间
    private String URL_ROOM_RECOMMEND = "room_recommend";

    public String URL_ROOM_RECOMMEND() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_ROOM_RECOMMEND;
    }

    //热门直播间列表
    private String URL_ROOM_HOT = "room_top";

    public String URL_ROOM_HOT() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_ROOM_HOT;
    }

    //广场
    private String URL_ROOM_3 = "room_top3";

    public String URL_ROOM_3() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_ROOM_3;
    }

    //1v1
    private String URL_ROOM_4 = "room_top4";

    public String URL_ROOM_4() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_ROOM_4;
    }

    //房间信息
    private String URL_ROOM_INFO = "info_user";

    public String URL_ROOM_INFO() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_ROOM_INFO;
    }

    //付费直播间购买
    private String URL_BUY_ROOM = "pay_room";

    public String URL_BUY_ROOM() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_BUY_ROOM;
    }


    //直播间观众列表
    private String URL_ROOM_AUDIENCE_LIST = "room_members";

    public String URL_ROOM_AUDIENCE_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_ROOM_AUDIENCE_LIST;
    }

    //直播间观众列表
    private String UEL_WEB_URL = "webkey";

    public String UEL_WEB_URL() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ UEL_WEB_URL;
    }

    //心跳包
    private String URL_HEARTBEAT = "heartbeat";

    public String URL_HEARTBEAT() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_HEARTBEAT;
    }

    //进入房间
    private String IN_ROOM = "in_room";

    public String IN_ROOM() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ IN_ROOM;
    }

    //退出房间
    private String OUT_ROOM = "out_room";

    public String OUT_ROOM() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ OUT_ROOM;
    }

    //初始化
    private String URL_INIT = "room_init";

    public String URL_INIT() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_INIT;
    }

    //直播间简化版初始化
    private String URL_ROOM_INIT = "room_init2";

    public String URL_ROOM_INIT() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_ROOM_INIT;
    }

    //视频聊天-主播设定的套餐
    private String URL_ROOM_CHAT_DEPLETE = "get_chat_deplete";

    public String URL_ROOM_CHAT_DEPLETE() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_ROOM_CHAT_DEPLETE;
    }

    //视频通话分钟套餐结算
    private String URL_CALL_MENUTE_SETTLE = "chat_deplete";

    public String URL_CALL_MENUTE_SETTLE() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_CALL_MENUTE_SETTLE;
    }

    //请求取消视频通话套餐
    private String URL_CALL_MENUTE_UNSETTLE = "revoke_deplete";

    public String URL_CALL_MENUTE_UNSETTLE() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_CALL_MENUTE_UNSETTLE;
    }

    //更新主播的状态
    private String URL_CALL_ANCHOR_STATE = "videocall";

    public String URL_CALL_ANCHOR_STATE() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_CALL_ANCHOR_STATE;
    }

    //一对一视视频通话可接通列表
    private String URL_CALL_VIDEOCALL_ONLINE ="videocall_online";

    public String URL_CALL_VIDEOCALL_ONLINE() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_CALL_VIDEOCALL_ONLINE;
    }

    //禁言、解禁某个用户
    private String URL_CALL_SPEECH_TOUSER = "op_gag";

    public String URL_CALL_SPEECH_TOUSER() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_CALL_SPEECH_TOUSER;
    }

    //禁言名单
    private String URL_CALL_SPEECH_LIST = "gag_list";

    public String URL_CALL_SPEECH_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_CALL_SPEECH_LIST;
    }

    //关注、推荐
    private String URL_FOLLOW_TOP = "room_top2";

    public String URL_FOLLOW_TOP() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_FOLLOW_TOP;
    }

    //订阅主播
    private String URL_SUBSCRIBE_ANCHOR = "subscribe_player";

    public String URL_SUBSCRIBE_ANCHOR() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_SUBSCRIBE_ANCHOR;
    }

    //预约主播
    private String URL_RESERVE_ANCHOR = "reserve_anchor";

    public String URL_RESERVE_ANCHOR() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_RESERVE_ANCHOR;
    }

    //关注列表
    private String URL_FOLLOW_LIST = "room_top5";

    public String URL_FOLLOW_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_FOLLOW_LIST;
    }

    //预约列表
    private String URL_RESEVER_LIST = "get_reserve_list";

    public String URL_RESEVER_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_RESEVER_LIST;
    }

    //视频通话记录
    private String URL_GET_CALL_LET_LIST = "get_video_chat_list";

    public String URL_GET_CALL_LET_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_GET_CALL_LET_LIST;
    }

    //任务
    private String URL_GET_ROOM_TASK = "popup_page";

    public String URL_GET_ROOM_TASK() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_GET_ROOM_TASK;
    }

    //用户未接挂断电话后主播方触发自动消息
    private String URL_UP_INVITE_VIDEO_CHAT_STATE = "up_invite_video_chat_state";

    public String URL_UP_INVITE_VIDEO_CHAT_STATE() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_UP_INVITE_VIDEO_CHAT_STATE;
    }

    /**
     * 视频通话
     */
    //检查拨号权限
    private String URL_CALL_CHECKED_PER = "check_chat_video";

    public String URL_CALL_CHECKED_PER() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_CALL_CHECKED_PER;
    }

    //建立视频通话
    private String URL_CALL_BUSY = "call_video_chat";

    public String URL_CALL_BUSY() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_CALL_BUSY;
    }

    //预付费
    private String URL_CALL_BUILD = "start_video_chat";

    public String URL_CALL_BUILD() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_CALL_BUILD;
    }

    //视频通话结束
    private String URL_CALL_END = "end_video_chat";

    public String URL_CALL_END() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_CALL_END;
    }

    //视频通话心跳状态上报
    private String URL_POST_HEART = "up_video_chat_heart";

    public String URL_POST_HEART() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_POST_HEART;
    }

    //获取通话结算信息
    private String URL_GET_VIDEO_CALL_FREE = "get_video_chat_fee_info";

    public String URL_GET_VIDEO_CALL_FREE() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_GET_VIDEO_CALL_FREE;
    }

    //直播间封面更新
    private String URL_UPLOAD_FRONT_COVER = "room_front_cover";

    public String URL_UPLOAD_FRONT_COVER() {
        return HostManager.getInstance().getHostUrl() + URL_ROOM+ URL_UPLOAD_FRONT_COVER;
    }

    /**
     * 用户模块
     */
    private String URL_USER = "user/";

    //粉丝
    private String URL_FANS = "fans";
    //粉丝模块
    public String URL_FANS() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_FANS;
    }
    //关注
    private String URL_FOLLOW = "attention";
    //关注模块
    public String URL_FOLLOW() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_FOLLOW;
    }

    //用户模块
    public String URL_USER() {
        return HostManager.getInstance().getHostUrl() + URL_USER;
    }


    //严重错误日志上传
    private String URL_REPORT_DEADLY = "report_deadly";

    public String URL_REPORT_DEADLY() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_REPORT_DEADLY;
    }

    //第三方用户注册
    private String URL_OTHER_REGISTER = "tencent_reg";

    public String URL_OTHER_REGISTER() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_OTHER_REGISTER;
    }
    //配置信息
    private String URL_USER_CONFIG = "config";
    public String URL_USER_CONFIG() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_CONFIG;
    }

    //手机号码用户注册
    private String URL_PHONE_REGISTER = "phone_reg";

    public String URL_PHONE_REGISTER() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_PHONE_REGISTER;
    }

    //用户登录
    private String URL_LOGIN = "login";

    public String URL_LOGIN() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_LOGIN;
    }

    //账号登出
    private String URL_LOGIN_OUT = "loginOut";

    public String URL_LOGIN_OUT() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_LOGIN_OUT;
    }

    //关注用户
    private String URL_FOLLOW_USER = "attent";

    public String URL_FOLLOW_USER() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_FOLLOW_USER;
    }

    //用户基本资料
    private String URL_USER_DETAILS = "getuserinfo";

    public String URL_USER_DETAILS() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_DETAILS;
    }

    //个人明细
    private String URL_USER_PERSONAL = "personal_details";

    public String URL_USER_PERSONAL() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_PERSONAL;
    }

    //主播下的积分排行榜
    private String URL_USER_TOP_TABLE = "gift_contribution";

    public String URL_USER_TOP_TABLE() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_TOP_TABLE;
    }

    //关注状态
    private String URL_FOLLOW_STATUS = "is_attention";

    public String URL_FOLLOW_STATUS() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_FOLLOW_STATUS;
    }

    //视频聊详情 获取视频聊接通前的详情页
    private String URL_VIDEOCHAT_INFO = "videochat_info";

    public String URL_VIDEOCHAT_INFO() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_VIDEOCHAT_INFO;
    }

    //更新用户头像
    private String URL_UPLOAD_USER_AVTAR = "upload_avatar";

    public String URL_UPLOAD_USER_AVTAR() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_UPLOAD_USER_AVTAR;
    }

    //举报用户
    private String URL_USER_REPORT = "user_report";

    public String URL_USER_REPORT() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_REPORT;
    }

    //添加至黑名单
    private String URL_USER_ADD_BLACK = "add_blacklist";

    public String URL_USER_ADD_BLACK() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_ADD_BLACK;
    }

    //检查用户是否在黑名单中
    private String URL_USER_IS_BLACK = "is_black";

    public String URL_USER_IS_BLACK() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_IS_BLACK;
    }

    //用户绑定手机号
    private String URL_BIND_MOBILE = "bind_mobile";

    public String URL_BIND_MOBILE() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_BIND_MOBILE;
    }

    //修改用户信息
    private String URL_UPLOAD_USER_INFO = "upload_user_info";

    public String URL_UPLOAD_USER_INFO() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_UPLOAD_USER_INFO;
    }

    //附近的人
    private String URL_USER_NEARBY_LIST = "sel_nearby";

    public String URL_USER_NEARBY_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_NEARBY_LIST;
    }

    //用户位置
    private String URL_USER_LOCATION = "report_position";

    public String URL_USER_LOCATION() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_LOCATION;
    }

    //用户等级
    private String URL_USER_RANK = "user_rank";

    public String URL_USER_RANK() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_RANK;
    }

    //身份认证
    private String URL_IDENTITY_AUTHENTICATION = "upload_identity";

    public String URL_IDENTITY_AUTHENTICATION() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_IDENTITY_AUTHENTICATION;
    }

    //上传身份证图片
    private String URL_UPLOAD = "upload";

    public String URL_UPLOAD() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_UPLOAD;
    }

    //意见反馈
    private String URL_SUGGEST = "suggest";

    public String URL_SUGGEST() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_SUGGEST;
    }

    //帮助
    private String URL_HELP = "helps";

    public String URL_HELP() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_HELP;
    }

    //幻灯位
    private String URL_HOME_BANNER = "advlist";

    public String URL_HOME_BANNER() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_HOME_BANNER;
    }

    //公告
    private String URL_HOME_NOTICE_LIST = "announce_list";

    public String URL_HOME_NOTICE_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_HOME_NOTICE_LIST;
    }

    //通告详情
    private String URL_HOME_NOTICE_DETAILS = "announce_info";

    public String URL_HOME_NOTICE_DETAILS() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_HOME_NOTICE_DETAILS;
    }

    //搜索用户
    private String URL_SEARCH = "user_search";

    public String URL_SEARCH() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_SEARCH;
    }

    //关注的主播在线列表
    private String URL_FOLLOW_ONLINES = "attention_online";

    public String URL_FOLLOW_ONLINES() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_FOLLOW_ONLINES;
    }

    //分享上报
    private String URL_SHARE_REPORT = "report_chare";

    public String URL_SHARE_REPORT() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_SHARE_REPORT;
    }

    //版本更新
    private String URL_VERSTION = "version_compare";

    public String URL_VERSTION() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_VERSTION;
    }

    //绑定CID
    private String URL_BIND_CID = "bind_clientid";

    public String URL_BIND_CID() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_BIND_CID;
    }

    //游客登录
    private String URL_GUEST_LOGIN = "guest_login";

    public String URL_GUEST_LOGIN() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_GUEST_LOGIN;
    }

    //第三方平台账号绑定
    private String URL_OTHER_PLATFORM_BIND = "bind_platform";

    public String URL_OTHER_PLATFORM_BIND() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_OTHER_PLATFORM_BIND;
    }

    //第三方平台账号解绑
    private String URL_OTHER_PLATFORM_UNBIND = "unbind_platform";

    public String URL_OTHER_PLATFORM_UNBIND() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_OTHER_PLATFORM_UNBIND;
    }

    //已绑定的账号查询
    private String URL_PLATFORM_ACCOUNT_QUERY = "list_platform";

    public String URL_PLATFORM_ACCOUNT_QUERY() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_PLATFORM_ACCOUNT_QUERY;
    }

    //发送私信
    private String URL_SEND_PRIVATE_MSG = "send_chat";

    public String URL_SEND_PRIVATE_MSG() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_SEND_PRIVATE_MSG;
    }

    //帮助详情
    private String URL_HELP_INFO = "help_info";

    public String URL_HELP_INFO() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_HELP_INFO;
    }

    //设置聊天时长
    private String URL_SET_CHAT_DEPLETE = "set_chat_deplete";

    public String URL_SET_CHAT_DEPLETE() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_SET_CHAT_DEPLETE;
    }

    //用户中心
    private String URL_PERSONAL_CENTER = "personal_center";

    public String URL_PERSONAL_CENTER() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_PERSONAL_CENTER;
    }

    private String URL_PERSONAL_MY_LIST = "personal_center_seting";

    public String URL_PERSONAL_MY_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_PERSONAL_MY_LIST;
    }

    //我的会员
    private String URL_MYVIP = "myvip";

    public String URL_MYVIP() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_MYVIP;
    }

    //在线用户
    private String URL_ONLINE_LIST = "online_list";

    public String URL_ONLINE_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_ONLINE_LIST;
    }

    //我的钻石
    private String URL_PERSONAL_DETAILS_LIST = "personal_details_list";

    public String URL_PERSONAL_DETAILS_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_PERSONAL_DETAILS_LIST;
    }

    //修改性别
    private String URL_EDIT_SEX = "edit_sex";

    public String URL_EDIT_SEX() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_EDIT_SEX;
    }

    //黑名单
    private String URL_BLACK_LIST = "blacklist";

    public String URL_BLACK_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_BLACK_LIST;
    }

    //移除黑名单
    private String URL_REMOVE_BLACKLIST = "remove_blacklist";

    public String URL_REMOVE_BLACKLIST() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_REMOVE_BLACKLIST;
    }

    //获取城市信息
    private String URL_COORDINATE_CITY = "coordinate_city";

    public String URL_COORDINATE_CITY() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_COORDINATE_CITY;
    }

    //激活设备
    private String URL_ACTIVATION = "activation";

    public String URL_ACTIVATION() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_ACTIVATION;
    }

    //用户勿扰模式设置
    private String URL_USER_QUITE = "be_quite";

    public String URL_USER_QUITE() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_QUITE;
    }

    //消息列表中的我的相关的功能
    private String URL_USER_MSG_MENU = "get_user_record_list";

    public String URL_USER_MSG_MENU() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_MSG_MENU;
    }

    //设置页获取活动菜单
    private String URL_USER_ACTIVITY_MENU = "get_activity_menu";

    public String URL_USER_ACTIVITY_MENU() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_ACTIVITY_MENU;
    }

    //获取芝麻认证参数
    private String URL_USER_ZHIMA_PARAMS = "get_zhima_verify_url";

    public String URL_USER_ZHIMA_PARAMS() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_ZHIMA_PARAMS;
    }

    //芝麻认证状态校验
    private String URL_USER_ZHIMA_RESULT = "verify_zhima_result";

    public String URL_USER_ZHIMA_RESULT() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_USER_ZHIMA_RESULT;
    }

    //下发验证码
    private String URL_VERIFICATION_CODE = "send_sms";

    public String URL_VERIFICATION_CODE() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_VERIFICATION_CODE;
    }

    /**
     * 获取活动
     */
    private String URL_ACTIVITY_LIST="show_activity";

    public String URL_ACTIVITY_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_USER + URL_ACTIVITY_LIST;
    }


    /**
     * 任务模块
     */
    private String URL_TASK = "task/";
    //任务列表
    private String URL_TASK_CENTER_LIST = "task_list";

    public String URL_TASK_CENTER_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_TASK + URL_TASK_CENTER_LIST;
    }

    //直播间任务
    private String URL_ROOM_TASK ="room_task";

    public String URL_ROOM_TASK() {
        return HostManager.getInstance().getHostUrl() + URL_TASK + URL_ROOM_TASK;
    }

    //任务领取 task_id
    private String URL_TASK_GET ="task_receive";

    public String URL_TASK_GET() {
        return HostManager.getInstance().getHostUrl() + URL_TASK + URL_TASK_GET;
    }

    /**
     * 礼物模块
     */
    private String URL_GIFT = "gift/";
    //礼物结算 大厅
    private String URL_GIFT_GIVI = "giving_gift";

    public String URL_GIFT_GIVI() {
        return HostManager.getInstance().getHostUrl() + URL_GIFT +  URL_GIFT_GIVI;
    }

    //视频通话一对一礼物结算
    private String URL_GIFT_PRIVATE_GIFT = "giving_private_gift";

    public String URL_GIFT_PRIVATE_GIFT() {
        return HostManager.getInstance().getHostUrl() + URL_GIFT + URL_GIFT_PRIVATE_GIFT;
    }

    //私聊礼物结算
    private String URL_GIFT_CHAT_GIFT = "giving_chat_gift";

    public String URL_GIFT_CHAT_GIFT() {
        return HostManager.getInstance().getHostUrl() + URL_GIFT + URL_GIFT_CHAT_GIFT;
    }

    //自己对自己礼物结算
    private String URL_GIFT_SELF_GIFT = "giving_self_gift";

    public String URL_GIFT_SELF_GIFT() {
        return HostManager.getInstance().getHostUrl() + URL_GIFT +  URL_GIFT_SELF_GIFT;
    }

    //多媒体预览礼物赠送
    private String URL_GIFT_MEDIA = "giving_file_gift";

    public String URL_GIFT_MEDIA() {
        return HostManager.getInstance().getHostUrl() + URL_GIFT + URL_GIFT_MEDIA;
    }

    //礼物列表
    private String URL_ROOM_GIFT =  "gift_list";

    public String URL_ROOM_GIFT() {
        return HostManager.getInstance().getHostUrl() + URL_GIFT +  URL_ROOM_GIFT;
    }

    //所有的礼物
    private String URL_ROOM_ALL_GIFT =  "gift_all";

    public String URL_ROOM_ALL_GIFT() {
        return HostManager.getInstance().getHostUrl() + URL_GIFT + URL_ROOM_ALL_GIFT;
    }

    //礼物的分类
    private String URL_ROOM_GIFT_TYPE ="gift_type";

    public String URL_ROOM_GIFT_TYPE() {
        return HostManager.getInstance().getHostUrl() + URL_GIFT + URL_ROOM_GIFT_TYPE;
    }


    /**
     * 充值模块
     */
    //商品列表
    private String URL_GOOD = "recharge/";
    //订单
    private String URL_ORDERS = "orders/";
    //订单回调
    private String URL_NOTIFY = "Notify/";
    //商品模块
    private String URL_GOODS = "goods/";

    //充值金币套餐
    private String URL_RECHARGE_LIST = "charge_list";

    public String URL_RECHARGE_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_GOODS + URL_RECHARGE_LIST;
    }

    //订单校验
    private String URL_RECHARGE_CHECKORDER = "check_order";

    public String URL_RECHARGE_CHECKORDER() {
        return HostManager.getInstance().getHostUrl() + URL_ORDERS + URL_RECHARGE_CHECKORDER;
    }

    //生成订单
    private String CREATE_ORDES ="init";

    public String CREATE_ORDES() {
        return HostManager.getInstance().getHostUrl() + URL_ORDERS + CREATE_ORDES;
    }

    //订单支付状态查询
    private String ORDES_QUERY = "query";

    public String ORDES_QUERY() {
        return HostManager.getInstance().getHostUrl() + URL_ORDERS + ORDES_QUERY;
    }

    //订单支付状态查询
    private String ORDES_PAYAGAIN = "payagain";

    public String ORDES_PAYAGAIN() {
        return HostManager.getInstance().getHostUrl() + URL_ORDERS + ORDES_PAYAGAIN;
    }

    //订单支付取消
    private String ORDES_CANCEL = "cancel";

    public String ORDES_CANCEL() {
        return HostManager.getInstance().getHostUrl() + URL_ORDERS + ORDES_CANCEL;
    }

    private String PUSH_SWITCH_CHANGED = "push_switch_changed";

    public String PUSH_SWITCH_CHANGED() {
        return HostManager.getInstance().getHostUrl() + PUSH_SWITCH_CHANGED;
    }


    //VIP套餐

    private String BUY_VIP2 = "buy_vip2";

    public String BUY_VIP2() {
        return HostManager.getInstance().getHostUrl() + URL_GOODS + BUY_VIP2;
    }

    //VIP套餐
    private String BUY_VIP3 ="buy_vip3";

    public String BUY_VIP3() {
        return HostManager.getInstance().getHostUrl() + URL_GOODS + BUY_VIP3;
    }

    /**
     * 客服模块
     */
    private String URL_GREET = "greet/";
    //客服中心
    private String URL_GREET_ENTER = URL_GREET + "dogreet";

    public String URL_GREET_ENTER() {
        return HostManager.getInstance().getHostUrl() + URL_GREET_ENTER;
    }

    /**
     * 文件模块
     */
    private String URL_FILE = "upload_file/";
    //文件上传之前鉴权
    private String URL_FILE_UPLOAD_AUTHENTICATION = URL_FILE + "ready_upload";

    public String URL_FILE_UPLOAD_AUTHENTICATION() {
        return HostManager.getInstance().getHostUrl() + URL_FILE_UPLOAD_AUTHENTICATION;
    }

    //购买多媒体文件
    private String URL_FILE_BUY = URL_FILE + "buy_file";

    public String URL_FILE_BUY() {
        return HostManager.getInstance().getHostUrl() + URL_FILE_BUY;
    }

    //浏览多媒体文件
    private String URL_FILE_BROWSE = URL_FILE + "browse_file";

    public String URL_FILE_BROWSE() {
        return HostManager.getInstance().getHostUrl() + URL_FILE_BROWSE;
    }

    //获取文件列表
    private String URL_FILE_LIST = URL_FILE + "list_file";

    public String URL_FILE_LIST() {
        return HostManager.getInstance().getHostUrl() + URL_FILE_LIST;
    }

    //改变多媒体文件访问权限
    private String URL_FILE_PRIVATE_CHANGED = URL_FILE + "change_private";

    public String URL_FILE_PRIVATE_CHANGED() {
        return HostManager.getInstance().getHostUrl() + URL_FILE_PRIVATE_CHANGED;
    }

    //删除多媒体文件
    private String URL_FILE_DELETE = URL_FILE + "close_file";

    public String URL_FILE_DELETE() {
        return HostManager.getInstance().getHostUrl() + URL_FILE_DELETE;
    }

    //主页视频数据
    private String URL_FILE_INDEX_TOP = URL_FILE + "file_top";

    public String URL_FILE_INDEX_TOP() {
        return HostManager.getInstance().getHostUrl() + URL_FILE_INDEX_TOP;
    }

    //点赞和分享
    private String URL_FILE_LOVE = URL_FILE + "op_note";

    public String URL_FILE_LOVE() {
        return HostManager.getInstance().getHostUrl() + URL_FILE_LOVE;
    }

    //设置默认封面
    private String URL_SET_HEAD = URL_FILE + "op_display";

    public String URL_SET_HEAD() {
        return HostManager.getInstance().getHostUrl() + URL_SET_HEAD;
    }

    //多媒体礼物榜单列表
    private String URL_MEDIA_GIFT_RANK = URL_FILE + "file_gift_rank";

    public String URL_MEDIA_GIFT_RANK() {
        return HostManager.getInstance().getHostUrl() + URL_MEDIA_GIFT_RANK;
    }

    //视频分享
    private String URL_SHARE_VIDEO = URL_FILE + "share";

    public String URL_SHARE_VIDEO() {
        return HostManager.getInstance().getHostUrl() + URL_SHARE_VIDEO;
    }

    //视频上传之前权限校验
    private String URL_CHECKED_UPLOAD = URL_FILE + "check_upladfile";

    public String URL_CHECKED_UPLOAD() {
        return HostManager.getInstance().getHostUrl() + URL_CHECKED_UPLOAD;
    }

    /**
     * 标签
     */
    private String URL_TAG = "labels/";
    //可设置的标签
    private String URL_TAGS = URL_TAG + "label_list";

    public String URL_TAGS() {
        return HostManager.getInstance().getHostUrl() + URL_TAGS;
    }

    /**
     * 附近
     */
    private String URL_NEARBY = URL_ROOM + "get_bearby";

    public String URL_NEARBY() {
        return HostManager.getInstance().getHostUrl() + URL_NEARBY;
    }
}