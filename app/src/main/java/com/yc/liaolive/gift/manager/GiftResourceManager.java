package com.yc.liaolive.gift.manager;

import android.os.AsyncTask;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.Logger;
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
 * 2018/7/16
 * 礼物资源管理
 * 本地缓存的获取和网络文件的下载
 */

public class GiftResourceManager {

    private static final String TAG = "GiftResourceManager";
    private static GiftResourceManager mInstance;
    ExecutorService LIMITED_TASK_EXECUTOR;//任务启动模式
    private OkHttpClient okHttpClient;
    private String mFinalCachePath;

    public static synchronized GiftResourceManager getInstance(){
        synchronized (GiftResourceManager.class){
            if(null==mInstance){
                mInstance=new GiftResourceManager();
            }
        }
        return mInstance;
    }

    /**
     * 获取缓存文件所在的绝对路径
     * @param url
     * @return
     */
    public File getGiftSvga(String url){
        if(null==url) return null;
        File file = new File(mFinalCachePath, Utils.getFileName(url));
        return file;
    }

    /**
     * 清除所有礼物数据缓存，不包含已下载到本地的动画文件
     */
    public void cleanAllGiftsCache() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                for (int i = 1; i <= 5; i++) {
                    cleanGiftTypeList(i);
                }
                for (int i = 1; i <= 5; i++) {
                    cleanGiftLists(i);
                }
            }
        }.start();
    }

    /**
     * 擦除所有场景下的礼物分类数据
     * @param sourceApiType
     */
    private void cleanGiftTypeList(int sourceApiType){
        ApplicationManager.getInstance().getCacheExample().remove("gift_type_" + sourceApiType);
    }

    /**
     * 擦除所有分类&&所有场景下的礼物列表数据
     * @param sourceApiType
     */
    private void cleanGiftLists(int sourceApiType){
        for (int i = 1; i <= 4; i++) {
            ApplicationManager.getInstance().getCacheExample().remove("gift_list_"+String.valueOf(i)+"_"+sourceApiType);
        }
    }



    /**
     * 根据礼物价格返回当前礼物可选择的倍率
     * @param price
     * @return
     */
    public List<Integer> getGiftCountMeals(int price){
        if(price<=0){
            return createDefaultGiftMeals();
        }
        List<List<List<Integer>>> giftCountMeals = UserManager.getInstance().getGiftCountMeals();
        if(null== giftCountMeals){
            String gear="[\n" +
                    "            [\n" +
                    "                [\n" +
                    "                    0,\n" +
                    "                    500\n" +
                    "                ],\n" +
                    "                [\n" +
                    "                    1,\n" +
                    "                    9,\n" +
                    "                    19,\n" +
                    "                    99,\n" +
                    "                    520,\n" +
                    "                    999\n" +
                    "                ]\n" +
                    "            ],\n" +
                    "            [\n" +
                    "                [\n" +
                    "                    501,\n" +
                    "                    2000\n" +
                    "                ],\n" +
                    "                [\n" +
                    "                    1,\n" +
                    "                    9,\n" +
                    "                    19,\n" +
                    "                    99\n" +
                    "                ]\n" +
                    "            ],\n" +
                    "            [\n" +
                    "                [\n" +
                    "                    2001,\n" +
                    "                    1000000\n" +
                    "                ],\n" +
                    "                [\n" +
                    "                    1\n" +
                    "                ]\n" +
                    "            ]\n" +
                    "        ]";

            giftCountMeals = new Gson().fromJson(gear, new TypeToken<List<List<List<Long>>>>() {}.getType());
        }
        try {
            for (int i = 0; i < giftCountMeals.size(); i++) {
                for (int i1 = 0; i1 < giftCountMeals.get(i).size(); i1++) {
                    if(price>=giftCountMeals.get(i).get(i1).get(0)&&price<=giftCountMeals.get(i).get(i1).get(1)){
                        return giftCountMeals.get(i).get(1);
                    }
                }
            }
        }catch (RuntimeException e){
            return createDefaultGiftMeals();
        }
        return createDefaultGiftMeals();
    }

    /**
     * 构建默认的礼物倍率
     * @return
     */
    private List<Integer> createDefaultGiftMeals() {
        List<Integer> countMeals = new ArrayList<>();
        countMeals.add(1);
        countMeals.add(9);
        countMeals.add(99);
        countMeals.add(199);
        countMeals.add(520);
        countMeals.add(999);
        countMeals.add(1314);
        countMeals.add(9999);
        return countMeals;
    }

    public class HttpInteraptorLog implements HttpLoggingInterceptor.Logger{
        @Override
        public void log(String message) {
        }
    }

    public GiftResourceManager(){
        LIMITED_TASK_EXECUTOR = (ExecutorService) Executors.newFixedThreadPool(7);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpInteraptorLog());
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        this.okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        //最终目录
        mFinalCachePath = ApplicationManager.getInstance().getFinalGiftCacheDir();
    }

    /**
     * 下载礼物素材至缓存目录
     */
    private void downloadFile(String urlPath) {
        if(TextUtils.isEmpty(urlPath)) return;
        new DownloadTask().executeOnExecutor(LIMITED_TASK_EXECUTOR,urlPath);
    }


    /**
     * 礼物下载
     */
    private class DownloadTask extends AsyncTask<String,Void,File>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(String... urlPath) {
            if(null!=okHttpClient&&null!=urlPath&&urlPath.length>0){
                String url = urlPath[0];
                String fileName = Utils.getFileName(url);
                File cacheOutPath = new File(mFinalCachePath,fileName);
                // 储存下载文件的目录
                try {
                    Request request = new Request.Builder().url(url).build();
                    Response response = okHttpClient.newCall(request).execute();
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = response.body().byteStream();
                        fos = new FileOutputStream(cacheOutPath);
                        fos.flush();
                    } catch (Exception e) {
                        FileUtils.deleteFile(cacheOutPath);
                        return null;
                    } finally {
                        try {
                            if (is != null)
                                is.close();
                        } catch (IOException e) {
                        }
                        try {
                            if (fos != null)
                                fos.close();
                        } catch (IOException e) {
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    FileUtils.deleteFile(cacheOutPath);
                    return null;
                }
                return cacheOutPath;
            }
            return null;
        }


        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
        }
    }
}
