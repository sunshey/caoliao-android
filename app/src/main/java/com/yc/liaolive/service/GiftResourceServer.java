package com.yc.liaolive.service;

import android.app.Service;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.AllGiftInfo;
import com.yc.liaolive.bean.TagInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.ui.contract.InitContract;
import com.yc.liaolive.ui.presenter.InitPresenter;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * TinyHung@Outlook.com
 * 2018/7/1
 * 礼物资源管理
 */

public class GiftResourceServer extends Service {

    private static final String TAG = "GiftResourceServer";
    private InitPresenter mPresenter;
    private List<String> mSvgaUrls;//ICON 、全屏动效
    private OkHttpClient okHttpClient;
    private String mFinalCachePath;//文件的最终存储目录
    private int mNetVerstion;//网络礼物版本号
    private int COUNT=0;//已下载个数
    private ExecutorService mExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPresenter=new InitPresenter();
        mSvgaUrls=new ArrayList<>();
        initGift();
        return super.onStartCommand(intent, flags, startId);
    }


    public class HttpInteraptorLog implements HttpLoggingInterceptor.Logger{
        @Override
        public void log(String message) {
        }
    }

    public void initGift(){
        //SD缓存目录
        ApplicationManager.getInstance().initSDPath();
        //SVGA 缓存 100M
        try {
            File cacheDir = new File(getApplicationContext().getCacheDir(), "http");
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //礼物动画缓存目录
        mFinalCachePath = ApplicationManager.getInstance().getFinalGiftCacheDir();
        File dirPath = new File(mFinalCachePath);
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
        //形象标签获取
        UserManager.getInstance().getTags(1, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null!=object && object instanceof List){
                    List<TagInfo> tags= (List<TagInfo>) object;
                    VideoApplication.getInstance().setTags(tags);
                }
            }
            @Override
            public void onFailure(int code, String errorMsg) {

            }
        });
        //礼物数据获取
        if(null!=mPresenter){
            //获取所有礼物列表
            mPresenter.getAllGift(new InitContract.OnCallBackListener() {
                @Override
                public void onSuccess(AllGiftInfo data) {
                    checkedLocationVerstion(data);
                }

                @Override
                public void onFailure(int code, String errorMsg) {

                }
            });
        }
    }


    /**
     * 检查本地缓存版本号
     * @param data 礼物数据
     */
    public void checkedLocationVerstion(AllGiftInfo data){

        if(null==mFinalCachePath){
            mFinalCachePath = ApplicationManager.getInstance().getFinalGiftCacheDir();
            File dirPath = new File(mFinalCachePath);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
        }
        int locVerstion = SharedPreferencesUtil.getInstance().getInt(Constant.SP_GIFT_VERSTION_CODE,0);
        if(data.getVersion()>locVerstion){
            this.mNetVerstion=data.getVersion();
            if(null==data.getList()) return;
            for (int i = 0; i < data.getList().size(); i++) {
                GiftInfo giftInfo = data.getList().get(i);
                //ICON
                if(!TextUtils.isEmpty(giftInfo.getSvga())&&giftInfo.getSvga().endsWith(".svga")){
                    mSvgaUrls.add(giftInfo.getSvga());
                }
                //FULL Animation
                if(!TextUtils.isEmpty(giftInfo.getBigSvga())&&giftInfo.getBigSvga().endsWith(".svga")){
                    mSvgaUrls.add(giftInfo.getBigSvga());
                }
            }
            updataAllGift();
        }
    }

    /**
     * 下载所有礼物素材至临时目录,下载完成逐个复制到最终的缓存目录
     */
    private void updataAllGift() {
        if(null!= mSvgaUrls) {
            //根据CPU核心数控制线程的并发
            int processors = Runtime.getRuntime().availableProcessors();
            mExecutorService = (ExecutorService) Executors.newFixedThreadPool(processors);
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpInteraptorLog());
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            this.okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            for (int i = 0; i < mSvgaUrls.size(); i++) {
                new DownloadTask().executeOnExecutor(mExecutorService,mSvgaUrls.get(i));
            }
        }
    }

    /**
     * 礼物下载
     */
    private class DownloadTask extends AsyncTask<String,Void,File> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(String... giftInfos) {
            if(null!=okHttpClient&&null!=giftInfos&&giftInfos.length>0){
                String urlPath = giftInfos[0];
                String fileName = Utils.getFileName(urlPath);
                if(null==mFinalCachePath){
                    mFinalCachePath = ApplicationManager.getInstance().getFinalGiftCacheDir();
                    File dirPath = new File(mFinalCachePath);
                    if (!dirPath.exists()) {
                        dirPath.mkdirs();
                    }
                }
                File cacheOutPath = new File(mFinalCachePath,fileName);
                //过滤已存在的文件
                if(null!=cacheOutPath&&cacheOutPath.exists()&&cacheOutPath.isFile()){
                    COUNT++;
                    return cacheOutPath;
                }
                // 储存下载文件的目录
                try {
                    Request request = new Request.Builder().url(urlPath).build();
                    Response response = okHttpClient.newCall(request).execute();
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    try {
                        is = response.body().byteStream();
                        long total = response.body().contentLength();
                        fos = new FileOutputStream(cacheOutPath);
                        long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += len;
                            int progress = (int) (sum * 1.0f / total * 100);
//                            Logger.d(TAG,"PROGRESS-->"+progress);
                        }
                        fos.flush();
                    } catch (Exception e) {
                        FileUtils.deleteFile(cacheOutPath);
                        return null;
                    } finally {
                        try {
                            if (null!=is)is.close();
                            if (null!=fos)fos.close();
                        }catch (IOException e){

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    FileUtils.deleteFile(cacheOutPath);
                    return null;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    FileUtils.deleteFile(cacheOutPath);
                    return null;
                }
                COUNT++;
                return cacheOutPath;
            }
            return null;
        }


        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if(null!=mSvgaUrls) Logger.d(TAG,"已下载："+COUNT+"/"+mSvgaUrls.size());
            if(null!=mSvgaUrls&&COUNT== mSvgaUrls.size()){
                Logger.d(TAG,"礼物资源更新完成,版本号："+mNetVerstion);
                if(null!=mExecutorService) mExecutorService.shutdown();
                mExecutorService=null;
                SharedPreferencesUtil.getInstance().putInt(Constant.SP_GIFT_VERSTION_CODE,mNetVerstion);
                stopSelf();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!= mSvgaUrls) mSvgaUrls.clear();
        mSvgaUrls =null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
