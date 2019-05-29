package com.yc.liaolive.contants;

import android.os.Environment;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.util.FileUtils;
import java.io.File;

/**
 * TinyHung@outlook.com
 * 2017/3/21 16:48
 */
public interface Constant {

    //微信登录Key
    String LOGIN_WX_KEY = "wx567c10f63b4f8f07";
    //登录SECRET
    String LOGIN_WX_SECRET = "6c44da31425964195284fbcb4a362755";
    //QQ登录Key
    String LOGIN_QQ_KEY = "1108185960";
    //登录SECRET
    String LOGIN_QQ_SECRET = "QLupGLbVKUJimBNs";
    //微博登录Key
    String LOGIN_WEIBO_KEY = "994868311";
    //登录SECRET
    String LOGIN_WEIBO_SECRET = "908f16503b8ebe004cdf9395cebe1b14";
    //回调地址
    String LOGIN_WEIBO_CALL_BACK_URL = "http://sns.whalecloud.com/sina2/callback";
    //Bugly
    String BUGLY_APP_ID = "6b412ed15a";

    int LOGIN_TYPE_QQ = 1;
    int LOGIN_TYPE_WEXIN = 2;
    int LOGIN_TYPE_WEIBO = 3;
    int LOGIN_TYPE_PHONE = 4;

    //登录注册
    int REGISTER_REQUST_CODE = 100;//注册请求
    int REGISTER_RESULT_CODE = 101;//注册回执
    int REGISTER_PHONE_REQUST_CODE = 102;//手机号码注册请求
    int REGISTER_PHONE_RESULT_CODE = 103;//手机号码注册回执
    int REGISTER_COMPLEMENT_REQUST_CODE = 104;//用户资料补全请求
    int REGISTER_COMPLEMENT_RESULT_CODE = 105;//用户资料补全回执
    int REGISTER_MODIFY_NICKNAME_REQUST = 106;//修改用户资料
    int REGISTER_MODIFY_NICKNAME_RESULT = 107;//修改用户资料

    //权限申请
    int PERMISSION_PUBLISH_REQUST_CODE = 2018;
    //关闭Popup等待时间
    long CLOSE_POPUPWINDOW_WAIT_TIME = 300;

    //sp文件名
    String SP_NAME = "huayan";

    /**
     * 文件存储目录
     */
    String APP_ROOT_PATH_NAME="HuaYan";
    String BASE_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "HuaYan" + File.separator;
    String PATH_DATA = FileUtils.createRootPath(VideoApplication.getInstance()) + "huayan" + File.separator + "cache" + File.separator;
    String IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "HuaYan" + File.separator + "Photo" + File.separator;
    String DOWNLOAD_PATH = BASE_CACHE_PATH + "File+" + File.separator + ".Download" + File.separator;
    String DOWNLOAD_WATERMARK_VIDEO_PATH = BASE_CACHE_PATH + "HuaYan" + File.separator;

    /**
     * QQ客服
     */
    String QQ = "3386990090";

    /**
     * 关闭Books 我的 列表编辑菜单栏
     */
    String SNACKBAR_ERROR = "snackbar_error";
    String SNACKBAR_DONE = "snackbar_done";
    String SUFFIX_ZIP = ".zip";
    /**
     * 设置
     */
    String SETTING_OPEN_PRIVATE_LIVE = "setting_open_private_live";
    //硬编开启状态
    String SETTING_HWCODEC_ENABLED = "setting_hwcodec_enabled";
    //音频悬浮窗
    String SETTING_AUDIO_WINDOWN = "setting_audio_windown";

    /**
     * 缓存
     */

    //首页，广告
    String CACHE_HOME_BANNERS = "cache_home_banners";
    //首页通告
    String CACHE_HOME_NOTICE = "cache_home_notice";


    /**
     * SP配置
     */
    //当天日期
    String SETTING_DAY = "setting_day";
    String SETTING_FIRST_START_GRADE = "setting_first_start_grade";
    String IS_DELETE_PHOTO_DIR = "is_delete_photo_dir";

    //缓存有效期 单位:秒 默认12小时
    int CACHE_TIME = 42300;
    int GIFT_CACHE_TIME = 86400;//24小时

    //加载对话框延时关闭时间
    int PROGRESS_CLOSE_DELYAED_TIME = 1900;
    /**
     * 阿里云上传
     */
    String STS_ENDPOINT = "http://oss-cn-shenzhen.aliyuncs.com";//终端
    String STS_CALLBACKADDRESS = "http://zbtest.6071.com/api/notify/osscallback";//回调地址
    String STS_BUCKET = "sleep-bshu";//分区
//    String STS_HOST = "zbtest.6071.com";//host
    String STS_HOST = "t.197754.com";//host
    String STS_CALLBACL_CONTENT_TYPE = "application/json";//回调类型
    //上传状态
    int UPLOAD_ERROR_CODE_FILE_NOTFIND = 1;
    int UPLOAD_ERROR_CODE_CLIENTEXCEPTION = 2;
    int UPLOAD_ERROR_CODE_SERVICEEXCEPTION = 3;
    int UPLOAD_ERROR_CODE_OTHER = 4;

    /**
     * 分区下的目录
     */
    //图片
    String OOS_DIR_IMAGE = "zb/image/";
    //视频
    String OOS_DIR_VIDEO = "zb/video/";
    //ASMR视频
    String OOS_DIR_ASMR_VIDEO = "zb/asmr/video/";
    //ASMR音频
    String OOS_DIR_ASMR_AUDIO = "zb/asmr/audio/";
    //ASMR图片
    String OOS_DIR_ASMR_IMAGE = "zb/asmr/image/";
    //普通相册图片
    int OSS_FILE_TYPE_IMAGE=0;
    //普通小视频
    int OSS_FILE_TYPE_VIDEO=1;
    //ASMR音频
    int OSS_FILE_TYPE_ASMR_AUDIO=3;
    //ASMR视频
    int OSS_FILE_TYPE_ASMR_VIDEO=4;
    //ASMR 视频、音频封面
    int OSS_FILE_TYPE_ASMR_COVER=-1;

    //关键功能提示
    String TIPS_SCANVIDEO_CODE = "tips_scanvideo_code";
    //首页我的关注、热门、我的作品、喜欢、用户中心作品 跳转至视频播放滑动列表界面色的标记
    String KEY_FRAGMENT_TYPE = "key_fragment_type";

    String KEY_MAIN_INSTANCE = "key_main_instance";

    /**
     * Fragment
     */
    int FRAGMENT_TYPE_PUBLIC_NOTICE = 0x1;//系统通告
    int FRAGMENT_TYPE_TASK_CENTER = 0x2;//任务中心
    int FRAGMENT_TYPE_TASK_RECHARGE = 0x3;//充值
    int FRAGMENT_TYPE_TASK_AUTHEN = 0x4;//实名认证
    int FRAGMENT_TYPE_NOTICE_DETAILS = 0x5;//通告详情
    int FRAGMENT_RECHARGE_AWARD = 0x6;//充值奖励
    int FRAGMENT_TYPE_MY_CALL = 0x7;//我的来电
    int FRAGMENT_TYPE_MY_MONERY = 0x8;//我的钻石
    int FRAGMENT_TYPE_MY_INTEGRAL = 0x9;//我的积分
    int FRAGMENT_TYPE_MY_MAKE = 0x10;//我的预约
    int FRAGMENT_TYPE_USER_TAG = 0x11;//我的预约

    //话题分类视频列表
    int KEY_FRAGMENT_TYPE_TOPIC_VIDEO_LISTT = 0x5;
    //实名认证
    int KEY_FRAGMENT_LIVE_ATTEST = 0x12;

    //约定参数
    String KEY_AUTHOR_TYPE = "key_author_type";
    String KEY_AUTHOR_ID = "key_author_id";
    String KEY_INDEX = "key_index";
    String KEY_URL = "url";
    String KEY_SUBTITLE = "subTitle";
    String KEY_ID = "key_id";
    String KEY_VIDEO_TOPIC_ID = "key_video_topic_id";
    String KEY_TITLE = "key_title";
    String KEY_SELECTED_KEY="selected_key";
    String KEY_SELECTED_SMAR_VIDEO="selected_asmr_video";

    int MEDIA_VIDEO_EDIT_MAX_DURTION = 300 * 1000;//视频处理的最大时长,单位毫秒
    int MEDIA_VIDEO_EDIT_MIN_DURTION = 5 * 1000;//视频处理的最大时长,单位毫秒
    int MODE_USER_COMPLETE = 1;
    int MODE_USER_EDIT = 2;

    //直播间系统消息
    String MSG_CUSTOM_ROOM_SYSTEM = "room_group_sys";
    //只是人数发生了变化
    String MSG_CUSTOM_ROOM_SYSTEM_NUMBER = "room_group_onlineNumber";

    String MSG_CUSTOM_GROUP_TEXT = "CustomTextMsg";

    String MSG_CUSTOM_GROUP_CUSTOM_CMD = "CustomCmdMsg";

    long POST_DELAYED_ADD_DATA_TIME = 400;

    String MSG_NOTICE_ROOM = "系统公告：直播严禁低俗、色情、引诱、暴力、暴露、赌博、反动等不良内容，" +
            "一旦涉及将被封禁账号，网警和房管24小时在线巡查！";

    String MSG_NOTICE_PRIVATE_ROOM = "系统公告：您已开始视频聊天，请遵守视频聊天行为规范，任何不良的违规行为将会被记录，并提供给有关部门依法处置的依据！";

    String MSG_NOTICE_CHAT = "请勿私加微信、QQ。谨防受骗。禁止涉黄辱骂等违规行为，倡导绿色交友";

    //自定义文本消息
    String MSG_CUSTOM_TEXT = "msg_custom_text";
    //自定义礼物消息
    String MSG_CUSTOM_GIFT = "msg_custom_gift";
    //自定义系统通知消息
    String MSG_CUSTOM_NOTICE = "msg_custom_notice";
    //点赞
    String MSG_CUSTOM_PRICE = "msg_custom_price";
    //新增观众
    String MSG_CUSTOM_ADD_USER = "msg_custom_add_user";
    //减少观众
    String MSG_CUSTOM_REDUCE_USER = "msg_custom_reduce_user";
    //关注主播事件
    String MSG_CUSTOM_FOLLOW_ANCHOR = "msg_custom_add_follow_anchor";
    //用户数量改变
    String MSG_CUSTOM_NUMBER_USER = "msg_custom_number_user";
    //顶部排行榜单改变
    String MSG_CUSTOM_TOP_USER = "msg_custom_top_user";
    //全局系统消息，弹幕显示
    String MSG_CUSTOM_SYS_FULL = "msg_custom_sys_full";
    //中奖消息
    String MSG_CUSTOM_ROOM_DRAW = "msg_custom_lottery";
    //错误消息
    String MSG_CUSTOM_ERROR = "msg_custom_error";
    //主播端前后台切换
    String MSG_CUSTOM_ROOM_PUSH_SWITCH_CHANGED = "push_switch_changed";//通用消息,前后调切换
    //自定义消息 私有 直播
    String MESSAGE_PRIVATE_CUSTOM_LIVE="private_live_msg";
    //自定义消息 私有 多媒体
    String MESSAGE_PRIVATE_CUSTOM_MEDIA="private_media";
    //自定义消息 视频通话唤醒
    String MESSAGE_PRIVATE_CUSTOM_WAKEUP="private_live_wakeup";
    //语音聊天参数
    String MESSAGE_VOICE_PARAMS="msg_voice_params";

    //热门城市搜索
    String HOT_CITY = "hot_city";

    //视频通通话大厅信令
    String MESSAGE_PUBLIC_GROUP = "msg_public_group";
    //视频通话主页信令
    String MESSAGE_HOME_GROUP = "message_home_group";

    //声音开关
    String SOUND_SWITCH = "sound_switch";

    //震动开关
    String VIBRATE_SWITCH = "vibrate_switch";

    //推送信令
    String NOTICE_CMD_ATTEST = "notice_cmd_attest";
    //推送，新的至播间活动推送
    String NOTICE_CMD_ROOM = "notice_cmd_recommend_room";
    //直播间封禁
    String NOTICE_CMD_ROOM_CLOSE = "notice_cmd_room_close";
    //账号封禁
    String NOTICE_CMD_ACCOUNT_CLOSE = "notice_cmd_account_close";
    //新的任务可领取
    String NOTICE_CMD_ROOM_TASK_FINLISH = "notice_cmd_room_task_finlish";
    //推荐主播点击事件
    String NOTICE_ACTION_CMD_ROOM = "notice_action_cmd_room";
    //普通通知
    String NOTICE_ACTION_CMD_NOTICE = "notice_action_cmd_notice";
    //真实视频来电通知
    String NOTICE_ACTION_CMD_CALL = "notice_action_cmd_call";
    //推送视频来电通知
    String NOTICE_ACTION_CMD_CALL_FALSE = "notice_action_cmd_call_false";
    //私信
    String NOTICE_ACTION_CMD_CHAT_MSG = "notice_action_cmd_chat_msg";
    /**
     * 观察者
     */
    int OBSERVABLE_ACTION_LOGIN = 0;//登录
    int OBSERVABLE_ACTION_UNLOGIN = 1;//登出
    int OBSERVABLE_ACTION_ADD_UPLOAD_TAKS = 6;//添加了批量上传的任务
    int OBSERVABLE_ACTION_SCANWEIXIN_VIDEO_FINLISH = 7;//手机视频扫描任务已完成
    String OBSERVER_LOGIN_OUT = "observer_login_out";//退出

    //首页通知有新消息
    String OBSERVER_CMD_HAS_NEW_MESSAGE = "observer_cmd_has_new_message";
    //所有通告已阅读
    String OBSERVER_CMD_ALLREAD_MESSAGE = "observer_cmd_allread_message";
    //消息数据改变
    String OBSERVER_CMD_UPDATA_MESSAGE = "observer_cmd_updata_message";
    //关注发生了变化
    String OBSERVER_CMD_FOLLOW_CHANGE = "observer_cmd_follow_change";
    //首页刷新完成
    String OBSERVER_CMD_REFRESH_FINLISH = "observer_cmd_refresh_finlish";
    //滑动中
    String OBSERVER_CMD_INDEX_SCOLLING = "observer_cmd_index_scolling";
    //网络发生了变化
    String OBSERVER_CMD_NET_WORK_CHANGED = "observer_cmd_net_work_changed";
    //消息数量发生了变化
    String OBSERVER_LIVE_MESSAGE_CHANGED = "observer_live_message_changed";
    //任务已经获取了
    String OBSERVER_LIVE_ROOM_TASK_GET = "observer_live_room_task_get";
    //用户本地积分发生了变化
    String OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED = "observer_cmd_user_location_integral_changed";
    //刷新网络的
    String OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED_NET = "observer_cmd_user_location_integral_changed_net";
    //会话异常
    String OBSERVER_CMD_CALL_EXCEPTION = "observer_cmd_call_exception";
    //会员详情展开
    String OBSERVER_CMD_VIP_CONTENT_DOWN = "observer_cmd_vip_content_down";
    //会员详情收起
    String OBSERVER_CMD_VIP_CONTENT_UP = "observer_cmd_vip_content_up";
    //礼物未被选中
    String OBSERVER_GIFT_CLEAN_SELECTED_REST = "observer_gift_clean_selected_rest";
    //礼物面板的适配器初始化完成
    String OBSERVER_GIFT_RECOVERY_ADAPTER_INIT = "observer_gift_recovery_adapter_init";
    //关闭视频选择界面
    String OBSERVER_CLOSE_LOCATION_VIDEO_ACTIVITY = "observer_close_location_video_activity";
    //拥有刷新权限
    String OBSERVER_HAS_REFRESH_PERMISSION = "observer_has_refresh_permission";
    //失去刷新权限
    String OBSERVER_DOTHAS_REFRESH_PERMISSION = "observer_dothas_refresh_permission";
    //关闭可能已经打开的多媒体预览窗口
    String OBSERVER_FINLISH_MEDIA_PLAYER = "observer_finlish_media_player";
    //检查新的消息
    String OBSERVER_CHECKED_NEW_MSG = "observer_checked_new_msg";
    //刷新未读消息数量
    String OBSERVER_UPDATA_NEW_MSG = "observer_updata_new_msg";
    //分发触摸事件
    String OBSERVER_TOUCH_DISPATCHTOUCH_YES = "observer_touch_dispatchtouch_yes";
    //拦截触摸事件
    String OBSERVER_TOUCH_DISPATCHTOUCH_NO = "observer_touch_dispatchtouch_no";
    //身份信息审核通过
    String OBSERVER_CMD_IDENTITY_AUTHENTICATION_SUCCESS = "observer_cmd_identity_authentication_success";
    //关注状态 已关注
    String OBSERVER_CMD_FOLLOW_TRUE = "observer_cmd_follow_true";
    //关注状态 取关
    String OBSERVER_CMD_FOLLOW_FALSE = "observer_cmd_follow_false";
    //视频通话充值成功
    String OBSERVER_CMD_PRIVATE_RECHARGE_SUCCESS = "observer_cmd_private_recharge_success";
    //APP可用
    String OBSERVER_CMD_APP_AVAILABLE = "app_available";
    //未读消息数量
    String OBSERVER_CMD_MSG_UNREAD_COUND = "app_unred_msg_count";
    //主页未读消息数量
    String OBSERVER_CMD_MSG_UNREAD_COUND_MAIN = "app_unred_msg_count_main";
    //SP
    String SP_LOGIN_PLATFORM = "sp_login_platform";
    //首次进入直播间
    String SP_ROOM_FIRST_ENTER = "sp_room_first_enter";
    String SP_GIFT_FIRST_ENTER = "sp_gift_first_enter";
    //礼物素材版本号
    String SP_GIFT_VERSTION_CODE = "sp_gift_verstion_code";
    String DEFAULT_FRONT_COVER = "http://e.hiphotos.baidu.com/image/pic/item/b151f8198618367a2e8a46ee23738bd4b31ce586.jpg";
    String SP_START_FIRST = "sp_start_first";
    String SP_LOG_INFO = "sp_log_info";
    //首次启动激活设备
    String SP_FIRST_ACTIVATION = "first_activation";
    String SP_NOFI_NEW = "sp_nofi_new";
    //是否是从设置中退出
    String SP_SETTING_EXIT = "sp_setting_exit";
    //最后一次修改礼物配置的时间
    String SP_KEY_GIFT_LASTUPDATA_TIME = "sp_gift_time";
    //芝麻认证信息
    String SP_ZHIMA_AUTHENTI_RESULT = "sp_zhima_authenti_result";

    //广告意图  绑定手机号
    int BANNER_ACTION_BIND_PHONE = 101;
    String BIND_PHONE_GET_TASK_SUCCESS = "bind_phone_get_task_success";
    //支付成功
    String PAY_SUCCESS = "pay_success";
    //触觉反馈的时长
    long VIBRATOR_MILLIS = 30;

    //视频通话参数
    String APP_START_EXTRA_CALL = "callExtraInfo";
    //视频推送参数
    String APP_START_EXTRA_CALL_FALSE = "customCallExtra";
    //直播间参数
    String APP_START_EXTRA_ROOM = "roomExtra";
    //私信
    String APP_START_EXTRA_CHAT = "chatExtra";
    //Dialog提示
    String APP_START_EXTRA_DOALOG_CHAT = "chatDialogExtra";

    String VIP_SUCCESS = "vip_success";
    int ROOM_VIP_CHARGE = 11;//直播间VIP充值
    /**
     * 任务ID
     */
    //首充
    int APP_TASK_FIRST_RECHGRE = 1;
    //新手任务
    int APP_TASK_BINDPHONE = 2;
    //会员每日奖励
    int APP_TASK_VIP = 6;
    //请求
    int RECHARGE_REQUST_CODE = 101;
    //回执
    int RECHARGE_RESULT_CODE = 102;
    //领取任务了
    int GETTASK_RESULT_CODE = 103;

    //好友关系
    String FRIEND_SHIP = "friend_ship";
    //首页的HOST ID
    int INDEX_HOST_INDEX_ID_FOLLOE = 0;
    int INDEX_HOST_INDEX_ID_HOT = 1;
    int INDEX_HOST_INDEX_ID_NEARBY = 2;
    int INDEX_HOST_ID_FOLLOW = 3;
    /**
     * 首页数据类别
     */
    //普通直播间
    String INDEX_ITEM_TYPE_ROOM = "type_room";
    //视频通话
    String INDEX_ITEM_TYPE_PRIVATE = "type_private";
    //幻灯片广告
    String INDEX_ITEM_TYPE_BANNERS = "type_banners";
    //普通单个广告
    String INDEX_ITEM_TYPE_BANNER = "type_banner";
    //推荐栏位
    String INDEX_ITEM_TYPE_RECOMMEND = "type_recommend";
    //视频通话
    String INDEX_ITEM_TYPE_VIDEOCALL = "type_videocall";
    //离线
    String INDEX_ITEM_TYPE_OFFLINE = "type_offline";
    //勿扰
    String INDEX_ITEM_TYPE_QUITE = "type_quite";
    //空闲状态
    String INDEX_ITEM_TYPE_FREE = "type_free";
    //音频
    String INDEX_ITEM_AUDIO="type_audio";
    //视频
    String INDEX_ITEM_VIDEO="type_video";
    //图片
    String INDEX_ITEM_IMAGE="type_image";
    //ASMR视频
    String INDEX_ITEM_ASMR_VIDEO="type_asmr_video";

    //关注列表为空
    String INDEX_ITEM_TYPE_EMPTY = "follow_empty";
    //相册的添加按钮
    String ITEM_ACTION_ADD = "item_action_add";
    //客服账号
    String SERVER_ACCOUNT = "23584694";
    //广告 外部活动
    int BANNER_ACTION_TYPE_OUT = 0;
    //内部活动
    int BANNER_ACTION_TYPE_INNER = 1;
    /**
     * 下载状态
     */
    //开始下载
    String BUILD_START = "build_start";
    //下载中
    String BUILD_DOWNLOAD = "build_download";
    //正在下载中，防止重新下载
    String BUILD_DOWNLOADING = "build_downloading";
    //结束下载
    String BUILD_END = "build_end";
    //下载失败
    String BUILD_ERROR = "build_error";
    //自定义短信模板
    String SMS_TEMPLENT = "12300769";
    //直播间被封禁
    int REQUST_RESULT_CODE_ROOM_CLODE = 1202;
    /**
     * 直播间Banner任务
     */
    //每日首充
    int TASK_ITEM_ID_CHARGE = 101;
    //绑定手机号
    int TASK_ITEM_ID_BINDPHONE = 102;
    //会员充值
    int TASK_ITEM_ID_VIP = 103;
    /**
     * 购买多媒体文件渠道
     */
    //聊天界面
    String MEDIA_CHANNER_CHAT = "buy_channer_chat";
    //用户中心
    String MEDIA_USER_CENTER = "buy_channer_usercenter";
    //相册
    String MEDIA_USER_ALBUM = "buy_channer_album";
    //视频列表
    String MEDIA_VIDEO_LIST = "media_video_list";
    //音频列表
    String MEDIA_AUDIO_LIST = "media_audio_list";
    //视频播放器
    String MEDIA_VIDEO_BROWSE = "browse_file";
    //选择图片回执
    int SELECT_IMAGE_REQUST = 10010;
    int SELECT_IMAGE_RESULT = 10011;
    //选择视频回执
    int SELECT_VIDEO_REQUST = 10012;
    int SELECT_VIDEO_RESULT = 10013;
    //预览图片回执
    int PREVIRE_IMAGE_REQUST = 10014;
    int PREVIRE_IMAGE_RESULT = 10015;
    //拍摄图片
    int REQUEST_CLIP_IMAGE = 2028;
    int REQUEST_TAKE_PHOTO = 2029;
    //封面选取
    int MEDIA_CIDEO_CAT_REQUST = 10016;
    int MEDIA_CIDEO_CAT_RESULT = 10017;

    //选择单张图片模式界面回执
    int SELECT_SINGER_IMAGE_REQUST = 10018;
    int SELECT_SINGER_IMAGE_RESULT = 10019;
    //裁剪图片请求回执
    int SELECT_CROP_IMAGE_REQUST = 10020;
    int SELECT_CROP_IMAGE_RESULT = 10021;
    //选择音频文件请求与回执
    int SELECT_AUDIO_REQUST = 10022;
    int SELECT_AUDIO_RESULT = 10023;

    int SELECT_AUDIO_REQUST2 = 10024;
    int SELECT_AUDIO_RESULT2 = 10025;

    //主页用户中心ITEM
    int INDEX_MINE_ITEM_VIDEO = 1;
    int INDEX_MINE_ITEM_FANS = 2;
    int INDEX_MINE_ITEM_VLIVE = 3;
    int INDEX_MINE_ITEM_MONERY = 4;
    int INDEX_MINE_ITEM_SERVER = 5;
    int INDEX_MINE_ITEM_HELP = 6;
    int INDEX_MINE_ITEM_VERSION = 7;
    int INDEX_MINE_ITEM_SETTING = 8;
    int INDEX_MINE_ITEM_VIP = 9;//钻石、VIP
    int INDEX_MINE_ITEM_SINGTRUE = 10;//主播认证
    int INDEX_MINE_ITEM_CALL_REJECT = 11;//来电勿扰
    int INDEX_MINE_ITEM_BEAUTY = 12;//美颜设置

    int INDEX_MINE_TAB_ROOM = 1;
    int INDEX_MINE_TAB_NOTEC = 2;
    int INDEX_MINE_TAB_INTEGRAL = 3;
    int INDEX_MINE_TAB_CALL = 4;
    int INDEX_MINE_TAB_MAKE = 5;
    int INDEX_MINE_TAB_VIDEO = 6;
    int INDEX_MINE_TAB_PHOTO = 7;
    int INDEX_MINE_TAB_BEAUTY = 8;
    int INDEX_MINE_TAB_SERVER = 9;
    int INDEX_MINE_TAB_SETTING = 10;
    int INDEX_MINE_TAB_ANCHOR = 11;
    int INDEX_MINE_TAB_GAME = 12;

    //用户中心ItemID
    int INDEX_PERCENTER_ITEM_FANS = 1;
    int INDEX_PERCENTER_ITEM_PRICE = 2;
    int INDEX_PERCENTER_ITEM_TOP = 3;
    int INDEX_PERCENTER_ITEM_VIDEO = 4;
    int INDEX_PERCENTER_ITEM_IMAGE = 5;
    int INDEX_PERCENTER_ITEM_VIP = 6;
    //主页消息
    int INDEX_MSG_ITEM_CALL = 1;
    int INDEX_MSG_ITEM_MAKE = 2;
    int INDEX_MSG_ITEM_MONERY = 3;
    int INDEX_MSG_ITEM_INTEGRAL = 4;
    int INDEX_MSG_ITEM_ONLINEUSER = 5;
    int INDEX_MSG_ITEM_SERVER = 6;//客服会话

    int MEDIA_TYPE_IMAGE=0;//相册
    int MEDIA_TYPE_VIDEO=1;//视频
    int MEDIA_TYPE_AUDIO=2;//音频
    int MEDIA_TYPE_ASMR_AUDIO=3;//ASMR音频
    int MEDIA_TYPE_ASMR_VIDEO=4;//ASMR视频
    //离线
    String USER_STATE_OFLINE = "offline";
    //正在直播
    String USER_STATE_LIVE = "live";
    //视频通话中
    String USER_STATE_VIDEOCALL = "videocall";
    //防打扰
    String USER_STATE_DISTURBED = "disturbed";
    //在线、空闲状态
    String USER_STATE_FREE = "free";
    //修改用户资料 params KEY
    String MODITUTY_KEY_NICKNAME = "nickname";
    String MODITUTY_KEY_POSITION = "position";
    String MODITUTY_KEY_SIGNTURE = "signature";
    String MODITUTY_KEY_SEX = "sex";
    String MODITUTY_KEY_LATITUDE = "latitude";
    String MODITUTY_KEY_LONGITUDE = "longitude";
    String MODITUTY_KEY_PHONE = "phone";
    String MODITUTY_KEY_SPECIALITY = "speciality";
    String MODITUTY_KEY_HEIGHT = "height";
    String MODITUTY_KEY_WEIGHT = "weight";
    String MODITUTY_KEY_STAR = "star";
    String MODITUTY_KEY_LABEL = "label";

    //私信消息CMD
    String CMD_MESSAGE_CHAT_GIFT_MSG = "private_call_gift";
    //视频通话结算消息
    String PRIVATE_CALL_NOTICE = "private_call_notice";
    //私信-reset api 语音消息
    String PRIVATE_CHAT_VOICE = "private_chat_voice";
    //输入中状态
    String PRIVATE_CHAT_INPUT_ING = "private_chat_input_ing";//EIMAMSG_InputStatus_Ing

    String START_APP_TIME = "start_app_time";
    //客服身份
    int USER_TYPE_SERVER = 4;
    //机器人
    int USER_TYPE_ROBOT = 2;

    String PACKAGE_WEIXIN = "com.tencent.mm";
    String PACKAGE_QQ = "com.tencent.mobileqq";
    String PACKAGE_WEIBO = "com.sina.weibo ";
    int CHEKCED_REQUST_CALL_SECCESS = 101;

    //点播播放器事件
    int PLAY_STATE_NOIMAL=10001;//默认、失败、完成
    int PLAY_STATE_PLAYING=10002;//播放中
    int PLAY_STATE_PAUSE=10003;//暂停中
    int PLAY_STATE_LOADING=10004;//缓冲中
    //房间内被禁言
    int ROOM_CUSTOMMSG_CODE_SPEECH_TO_ROOM = 10017;
    int ROOM_CUSTOMMSG_CODE_SPEECH_TO_APP = 20012;
    int ROOM_CUSTOMMSG_CODE_DANGER = 80001;//敏感词汇
    int ROOM_CUSTOMMSG_CLOSE = 10010;
    //礼物类别
    String STRING_TAG_NEW = "new";
    //芝麻认证回跳协议
    String CONTENT_AGREEMENT_AUTHENTI = "huayan://huayanzhima";

    String CLASS_NAME_LIVE_PUSHER = "com.yc.liaolive.live.ui.activity.LiveRoomPusherActivity";
    String CLASS_NAME_BEAUTY_SETTING = "com.faceunity.beauty.ui.BeautySettingActivity";
    String CLASS_NAME_SERVER = "com.yc.liaolive.msg.ui.activity.ChatActivity";
    String CLASS_NAME_AUTHENTICATION = "com.yc.liaolive.user.ui.UserAuthenticationActivity";
    String CLASS_NAME_LOANBOX = "com.yc.loanbox.view.LoanboxMainActivity";
    String CLASS_NAME_SETTING = "com.yc.liaolive.user.ui.SettingActivity";

    String APP_CONFIG = "app_config";
    java.lang.String USER_CONFIG_FIRST = "app_config";

    int PAY_REQUST = 10087;
    int PAY_RESULT = 10088;
    //客服回话消息未读数量
    String KET_SERVER_MSG_COUNT = "ket_server_msg_count";
    String KET_SERVER_MSG_TIME = "ket_server_msg_time";
}
