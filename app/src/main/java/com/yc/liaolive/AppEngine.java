package com.yc.liaolive;

import android.app.Application;
import com.danikula.videocache.HttpProxyCacheServer;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.recharge.model.bean.VipListItem;
import java.io.File;
import java.util.List;

/**
 * Application设置，必要时候使用Application上下文环境
 * Created by yangxueqin on 2018/10/26.
 */

public class AppEngine {

    private static final String TAG = "AppEngine";
    static AppEngine instance=new AppEngine();
    private HttpProxyCacheServer mProxyCacheServer;//缓存代理
    private List<VipListItem> mListCoin;

    public static AppEngine getInstance(){
        return instance;
    }

    private static Application application;

    /**
     * 默认不杀进程
     */
    private static boolean isNeedKillProcess;

    public static Application getApplication() {
        return application;
    }

    public static void setApplication(Application application) {
        AppEngine.application = application;
    }

    /**
     * 设置是否需要退出当前进程
     *
     * @param isNeedKillProcess
     */
    public static void setIsNeedKillProcess(boolean isNeedKillProcess) {
        AppEngine.isNeedKillProcess = isNeedKillProcess;
     }

     public static boolean getIsNeedKillProcess() {
     return isNeedKillProcess;
     }

     /**
     * 返回缓存代理
     * @return
     */
    public HttpProxyCacheServer getProxyCacheServer() {
        if(null==mProxyCacheServer){
            mProxyCacheServer=newProxy();
        }
        return mProxyCacheServer;
    }

    /**
     * 初始化并构造100M大小的缓存池
     * @return
     */
    private HttpProxyCacheServer newProxy() {
        if(null==application) application=VideoApplication.getInstance();
        int cacheSize = 100 * 1024 * 1024;
        return new HttpProxyCacheServer
                .Builder(application.getApplicationContext())
                .cacheDirectory(new File(ApplicationManager.getInstance().getVideoCacheDir()))
                .maxCacheSize(cacheSize)
                .build();
    }

    public List<VipListItem> getListCoin() {
        return mListCoin;
    }

    public void setVipListCoin(List<VipListItem> listCoin) {
        this.mListCoin = listCoin;
    }
}
