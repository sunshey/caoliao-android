package com.yc.liaolive.manager;

import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.live.bean.GiftTypeInfo;
import com.yc.liaolive.msg.manager.DBVoiceMessageManager;
import com.yc.liaolive.observer.SubjectObservable;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ACache;
import com.yc.liaolive.util.DataFactory;
import com.yc.liaolive.util.DeviceUtils;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2017/12/12.
 * 持有数据库管理者对象，缓存管理者对象等。。当内存不足时释放这些对象
 */

public class ApplicationManager {

    private static final String TAG = "ApplicationManager";
    private static WeakReference<ApplicationManager> mInstanceWeakReference=null;//自己
    public WeakReference<ACache> mACacheWeakReference=null;//缓存
    public static SubjectObservable mObservableWeakReference;//观察者
    public WeakReference<DBVoiceMessageManager> mVoiceMessageManagerWeakReference=null;//语音消息

    public static synchronized ApplicationManager getInstance(){
        synchronized (ApplicationManager.class){
            if(null!=mInstanceWeakReference&&null!=mInstanceWeakReference.get()){
                return mInstanceWeakReference.get();
            }
            ApplicationManager applicationManager=new ApplicationManager();
            mInstanceWeakReference=new WeakReference<ApplicationManager>(applicationManager);
        }
        return mInstanceWeakReference.get();
    }

    public ApplicationManager(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    /**
     * 全局初始化需要传入
     * @param mACache
     */
    public void setCacheExample(ACache mACache) {
        if(null==mACacheWeakReference||null==mACacheWeakReference.get()){
            mACacheWeakReference=new WeakReference<ACache>(mACache);
        }
    }

    /**
     * 缓存的对象实例
     * @return
     */
    public ACache getCacheExample(){
        if(null==mACacheWeakReference||null==mACacheWeakReference.get()){
            ACache aCache=ACache.get(VideoApplication.getInstance().getApplicationContext());
            mACacheWeakReference=new WeakReference<ACache>(aCache);
        }
        return mACacheWeakReference.get();
    }


    /**
     * 获取根缓存目录
     * @return
     */
    public String getCacheDir() {
        String cachePath = FileUtils.getFileDir(VideoApplication.getInstance().getApplicationContext());
        if(null==cachePath){
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                File file= new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/"+Constant.APP_ROOT_PATH_NAME+"/Cache/");
                if(!file.exists()){
                    file.mkdirs();
                }
                //使用内部缓存
                cachePath=file.getAbsolutePath();
            }
        }
        return cachePath;
    }


    /**
     * 获取视频缓存的目录
     * @return
     */
    public String getObjectCacheDir() {
        String cachePath = FileUtils.getFileDir(VideoApplication.getInstance().getApplicationContext());
        if(null==cachePath){
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                File file= new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/"+Constant.APP_ROOT_PATH_NAME+"/Cache/Video/");
                if(!file.exists()){
                    file.mkdirs();
                }
                //使用内部缓存
                cachePath=file.getAbsolutePath();
            }
        }
        return cachePath;
    }

    /**
     * 缓存目录,优先SD卡
     * @return
     */
    public String getVideoCacheDir() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File file= new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/"+Constant.APP_ROOT_PATH_NAME+"/Cache/Video/.videoCache");
            if(!file.exists()){
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return FileUtils.getFileDir(VideoApplication.getInstance().getApplicationContext());
    }

    /**
     * 返回正式下载路径
     * @return
     */
    public String getFinalGiftCacheDir() {
        String fileDir = FileUtils.getFileDir(VideoApplication.getInstance().getApplicationContext());
        if(null!=fileDir) return fileDir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File file= new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/"+Constant.APP_ROOT_PATH_NAME+"/.GiftCache");
            if(!file.exists()){
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取SD卡文件缓存路径
     * @return
     */
    public String  getSdPath(){
        if (FileUtils.getDiskCacheDir(VideoApplication.getInstance().getApplicationContext()) != null) {
            return FileUtils.getDiskCacheDir(VideoApplication.getInstance().getApplicationContext());
        }
        return null;
    }

    /**
     * 初始化SD卡路径
     */
    public void initSDPath() {

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            File file = new File(Constant.IMAGE_PATH);
            if(!file.exists()&&!file.isDirectory()){
                file.mkdirs();
            }
            try {
                if(0== SharedPreferencesUtil.getInstance().getInt(Constant.IS_DELETE_PHOTO_DIR)){
                    FileUtils.deleteFileOrDirectory(file);
                    SharedPreferencesUtil.getInstance().putInt(Constant.IS_DELETE_PHOTO_DIR,1);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }catch (RuntimeException e){

            }finally {
                File cacheFile=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+Constant.APP_ROOT_PATH_NAME+"/");
                if(!cacheFile.exists()&&!file.isDirectory()){
                    cacheFile.mkdirs();
                }

                //初始化拍摄视频缓存路径
                File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                String path;
                String comperPath;
                if (DeviceUtils.isZte()) {
                    if (dcim.exists()) {
                        path=dcim + "/"+Constant.APP_ROOT_PATH_NAME+"/Video";
                        comperPath=dcim + "/"+Constant.APP_ROOT_PATH_NAME+"/Video/Comple";
                    } else {
                        path=dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/"+Constant.APP_ROOT_PATH_NAME+"/Video";
                        comperPath=dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/"+Constant.APP_ROOT_PATH_NAME+"/Video/Comple";
                    }
                } else {
                    path=dcim + "/"+Constant.APP_ROOT_PATH_NAME+"/Video";
                    comperPath=dcim + "/"+Constant.APP_ROOT_PATH_NAME+"/Video/Comple";
                }
                File videoPath = new File(path);
                if (!videoPath.exists()) {
                    videoPath.mkdirs();
                }
                File complePath=new File(comperPath);
                if (!complePath.exists()) {
                    complePath.mkdirs();
                }
            }
        }
    }

    /**
     * 获取APP的各种缓存目录
     * @param MODE 0：视频录制输出目录，1：视频合成输出目录
     * @return
     */
    public String getOutPutPath(int MODE){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String recordOutPath;//视频录制输出目录
            String composeOutPath;//视频合成输出目录
            if (DeviceUtils.isZte()) {
                if (dcim.exists()) {
                    recordOutPath=dcim + "/"+Constant.APP_ROOT_PATH_NAME+"/Video";
                    composeOutPath=dcim + "/"+Constant.APP_ROOT_PATH_NAME+"/Video/Comple";
                } else {
                    recordOutPath=dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/"+Constant.APP_ROOT_PATH_NAME+"/Video";
                    composeOutPath=dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/"+Constant.APP_ROOT_PATH_NAME+"/Video/Comple";
                }
            } else {
                recordOutPath=dcim + "/"+Constant.APP_ROOT_PATH_NAME+"/Video";
                composeOutPath=dcim + "/"+Constant.APP_ROOT_PATH_NAME+"/Video/Comple";
            }

            File videoPath = new File(recordOutPath);
            if (!videoPath.exists()) {
                videoPath.mkdirs();
            }

            File complePath=new File(composeOutPath);
            if (!complePath.exists()) {
                complePath.mkdirs();
            }
            if(0==MODE){
                return recordOutPath;
            }else if(1==MODE){
                return composeOutPath;
            }
        }
        return null;
    }

    //添加一个观察者
    public void addObserver(Observer observer){
        if(null==mObservableWeakReference){
            mObservableWeakReference=new SubjectObservable();
        }
        mObservableWeakReference.addObserver(observer);
    }
    //移除一个观察者
    public void removeObserver(Observer observer){
        if(null!=mObservableWeakReference) mObservableWeakReference.deleteObserver(observer);
    }
    //移除所有观察者
    public void removeAllObserver(){
        if(null!=mObservableWeakReference) mObservableWeakReference.deleteObservers();
    }
    //刷新
    public void observerUpdata(Object obj){
        if(null!=mObservableWeakReference) mObservableWeakReference.updataSubjectObserivce(obj);
    }

    /**
     * 语音消息数据流
     * @return
     */
    public DBVoiceMessageManager getVoiceDBManager(){
        if(null==mVoiceMessageManagerWeakReference||null==mVoiceMessageManagerWeakReference.get()){
            DBVoiceMessageManager dbVideoUploadManager=new DBVoiceMessageManager(AppEngine.getApplication().getApplicationContext());
            mVoiceMessageManagerWeakReference=new WeakReference<>(dbVideoUploadManager);
        }
        return mVoiceMessageManagerWeakReference.get();
    }


    /**
     * 根据sourceApiType和typeID获取对应数据缓存
     * @param typeID
     * @param sourceApiType 场景Type
     * @return
     */
    public List<GiftInfo> getGiftCache(String typeID,int sourceApiType) {
        return (List<GiftInfo>) ApplicationManager.getInstance().getCacheExample().getAsObject("gift_list_"+typeID+"_"+sourceApiType);
    }

    /**
     * 根据sourceApiType和typeID缓存礼物列表
     * @param typeID
     * @param data
     * @param sourceApiType 数据场景类别
     */
    public void putGiftCache(final String  typeID, final List<GiftInfo> data, final int sourceApiType) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                ApplicationManager.getInstance().getCacheExample().put("gift_list_"+typeID+"_"+sourceApiType, data, Constant.GIFT_CACHE_TIME);
            }
        }.start();
    }

    /**
     * 根据 sourceApiType缓存礼物分类数据
     * @param sourceApiType
     */
    public void putGiftTypeList(final int sourceApiType, final List<GiftTypeInfo> giftTypeInfos){
        new Thread(){
            @Override
            public void run() {
                super.run();
                ApplicationManager.getInstance().getCacheExample().put("gift_type_" + sourceApiType,giftTypeInfos, Constant.GIFT_CACHE_TIME);
            }
        }.start();
    }

    /**
     * 根据sourceApiType获取礼物分类数据
     * @param sourceApiType
     * @return
     */
    public List<GiftTypeInfo> getGiftTypeList(int sourceApiType) {
        List<GiftTypeInfo> asObject = (List<GiftTypeInfo>) ApplicationManager.getInstance().getCacheExample().getAsObject("gift_type_" + sourceApiType);
        if(null!=asObject&&asObject.size()>0){
            return asObject;
        }
        UserManager.getInstance().getGiftTypeList(sourceApiType,null);
        return DataFactory.createGiftTabs();
    }

    public void onDestory(){
        removeAllObserver();
        if(null!=mACacheWeakReference){
            mACacheWeakReference.clear();
            mACacheWeakReference=null;
        }
        if(null!=mVoiceMessageManagerWeakReference){
            mVoiceMessageManagerWeakReference.clear();
            mVoiceMessageManagerWeakReference=null;
        }
        if(null!=mInstanceWeakReference){
            mInstanceWeakReference.clear();
            mInstanceWeakReference=null;
        }
        mObservableWeakReference=null;
    }
}
