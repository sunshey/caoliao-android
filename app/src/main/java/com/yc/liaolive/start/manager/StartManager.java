package com.yc.liaolive.start.manager;

import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.gift.manager.GiftResourceManager;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.HostManager;
import com.yc.liaolive.start.model.ConfigData;
import com.yc.liaolive.start.model.VersionCheckData;
import com.yc.liaolive.start.model.bean.ConfigBean;
import com.yc.liaolive.start.model.bean.UpdataApkInfo;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.SharedPreferencesUtil;
import java.util.List;
import io.reactivex.processors.BehaviorProcessor;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * 管理启动相关配置
 * Created by yangxueqin on 2018/10/26.
 */

public class StartManager {

    private static final String TAG = "StartManager";
    private static StartManager manager;
    private static BehaviorProcessor<ConfigBean> behaviorProcessor;
    private Subscription configSubscrition;
    private boolean isInitSuccess;
    private static List<ConfigBean.PageBean> pageBeanList;

    public static StartManager getInstance() {
        if (manager == null) {
            manager = new StartManager();
        }
        if (behaviorProcessor == null) {
            behaviorProcessor = BehaviorProcessor.create();
        }
        return manager;
    }

    private StartManager() {
        ConfigBean configBean = (ConfigBean) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.APP_CONFIG);
        if (configBean != null) {
            pageBeanList = configBean.getHome_page();
        }
    }

    public BehaviorProcessor<ConfigBean> getBehaviorProcessor() {
        return behaviorProcessor;
    }

    public void setBehaviorProcessor(BehaviorProcessor<ConfigBean> behaviorProcessor) {
        StartManager.behaviorProcessor = behaviorProcessor;
    }

    /**
     * 启动配置
     */
    public void startConfig() {
        isInitSuccess = false;
        HostManager.getInstance().initHostUrl();
        //获取配置域名
        UserManager.getInstance().getServerHost(new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                doAfterInit();
            }
            @Override
            public void onFailure(int code, String errorMsg) {
                doAfterInit();
            }
        });
    }

    /**
     * 获取app配置信息
     */
    public void getConfigInfo () {

        if (configSubscrition != null && !configSubscrition.isUnsubscribed()) {
            return;
        }

        if (behaviorProcessor == null) {
            behaviorProcessor = BehaviorProcessor.create();
        }

        if (isInitSuccess) {
            behaviorProcessor.onNext(new ConfigBean());
            return;
        }

        //获取数据配置
        configSubscrition = ConfigData.getConfig().observeOn(AndroidSchedulers.mainThread())//Schedulers.newThread()
                .filter(new Func1<ResultInfo<ConfigBean>, Boolean>() {
                    @Override
                    public Boolean call(ResultInfo<ConfigBean> configBeanResultInfo) {
                        if (behaviorProcessor == null) {
                            behaviorProcessor = BehaviorProcessor.create();
                        }
                        return true;
                    }
                })
                .subscribe(new Subscriber<ResultInfo<ConfigBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        isInitSuccess = false;
                        behaviorProcessor.onNext(new ConfigBean());
                    }

                    @Override
                    public void onNext(ResultInfo<ConfigBean> data) {
                        if (data != null) {
                            if (NetContants.API_RESULT_CODE == data.getCode()) {
                                VideoApplication.getInstance().setLoginIcon(true);
                                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_APP_AVAILABLE);
                                ConfigBean configBean = data.getData();
                                if (configBean != null) {
                                    isInitSuccess = true;
                                    pageBeanList = configBean.getHome_page();
                                    //礼物选择倍率
                                    UserManager.getInstance().setGiftCountMeals(configBean.getGift_config());
                                    //房间公告
                                    UserManager.getInstance().setNoticeMessage(configBean.getOfficial_notice());
                                    //主页的搜索按钮是否可用
                                    UserManager.getInstance().setSearchBut(configBean.getSearch_but());
                                    UserManager.getInstance().setPostAsmrBut(configBean.getPlus_but());
                                    //视频、照片控制器
                                    UserManager.getInstance().setImageController(configBean.getImage_controller());
                                    UserManager.getInstance().setVideoController(configBean.getVideo_controller());
                                    UserManager.getInstance().setChatController(configBean.getChat_controller());
                                    UserManager.getInstance().setAsmrController(configBean.getAsmr_controller());
                                    //客服信息
                                    UserManager.getInstance().setServer(configBean.getServer());
                                    //更新本地礼物配置
                                    if(null!=configBean.getGift_edit_lasttime()){
                                        String string = SharedPreferencesUtil.getInstance().getString(Constant.SP_KEY_GIFT_LASTUPDATA_TIME,"");
                                        if(!string.equals(configBean.getGift_edit_lasttime())){
                                            SharedPreferencesUtil.getInstance().putString(Constant.SP_KEY_GIFT_LASTUPDATA_TIME,configBean.getGift_edit_lasttime());
                                            GiftResourceManager.getInstance().cleanAllGiftsCache();
                                        }
                                    }
                                    ApplicationManager.getInstance().getCacheExample().put(Constant.APP_CONFIG, configBean, Constant.CACHE_TIME);
                                    //通知初始化完成
                                    behaviorProcessor.onNext(configBean);
                                } else {
                                    isInitSuccess = false;
                                    configBean = new ConfigBean();
                                    behaviorProcessor.onNext(configBean);

                                }
                            } else {
                                if(NetContants.API_APP_CLOSURE==data.getCode()){
                                    System.exit(0);
                                    return;
                                }
                                isInitSuccess = false;
                                behaviorProcessor.onNext(new ConfigBean());
                            }
                        } else {
                            isInitSuccess = false;
                            behaviorProcessor.onNext(new ConfigBean());
                        }
                    }
                });
    }

    /**
     * 获取初始化配置后再去初始化其他配置
     */
    private void doAfterInit() {
        AppEngine.setIsNeedKillProcess(true);
        getConfigInfo();

        //静默检测更新
        ApplicationManager.getInstance().getCacheExample().remove("updata_apk_info");
        VersionCheckManager.getInstance().setMainInit(false);
        VersionCheckData.checkedVerstion(0, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null != object && object instanceof UpdataApkInfo){
                    UpdataApkInfo updataApkInfo = (UpdataApkInfo) object;
                    VersionCheckManager.getInstance().checkVersion(updataApkInfo, false);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {

            }
        });
    }

    public List<ConfigBean.PageBean> getPageBeanList() {
        return pageBeanList;
    }

    public boolean isInitSuccess() {
        return isInitSuccess;
    }

}
