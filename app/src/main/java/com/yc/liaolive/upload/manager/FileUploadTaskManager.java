package com.yc.liaolive.upload.manager;

import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.model.AbortMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.AbortMultipartUploadResult;
import com.kaikai.securityhttp.utils.LogUtil;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.UploadAuthenticationInfo;
import com.yc.liaolive.bean.UploadObjectInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.upload.OSSFileFederationCredentialProvider;
import com.yc.liaolive.upload.PauseableUploadRequest;
import com.yc.liaolive.upload.PauseableUploadResult;
import com.yc.liaolive.upload.PauseableUploadTask;
import com.yc.liaolive.upload.bean.UploadDeteleTaskInfo;
import com.yc.liaolive.upload.bean.UploadParamsConfig;
import com.yc.liaolive.upload.listener.FileUploadListener;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SystemUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TinyHung@Outlook.com
 * 2017/8/17.
 * OSS 文件上传的封装  鉴权-构造上传-开始上传-上传维护-回调
 */

public class FileUploadTaskManager {

    public static final String TAG = "FileUploadTaskManager";
    private static FileUploadTaskManager mInstance;
    private UploadParamsConfig uploadParamsConfig;
    private Map<Long,PauseableUploadTask> mUploadTaskMap;//管理上传任务的任务栈
    private Map<Long,OSSClient> mOSSClientMap;//上传对象
    public FileUploadListener mFileUploadListener;//监听器
    public static final int PART_SIZE = 128 * 1024; // 设置分片大小
    private ClientConfiguration mConf;
    private int totalUploadCount =0;//上传总个数
    private int currentUploadCount=0;//当前正在上传第几个任务

    /**
     * 构造实例--单例模式
     * @return
     */
    public static synchronized FileUploadTaskManager getInstance() {
        if(null==mInstance){
            synchronized (FileUploadTaskManager.class){
                mInstance=new FileUploadTaskManager();
            }
        }
        return mInstance;
    }


    /**
     * 设置监听器
     * @param listener
     */
    public FileUploadTaskManager setUploadListener(FileUploadListener listener) {
        this.mFileUploadListener =listener;
        return mInstance;
    }

    /**
     * 移除监听器
     */
    public void removeUploadListener(){
        this.mFileUploadListener =null;
    }

    /**
     * 批量添加上传任务
     * @param data
     * @return
     */
    public void createUploadTasks(List<UploadObjectInfo> data) {
        if(null!=data){
            for (UploadObjectInfo datum : data) {
                createAndexecuteUploadTask(datum);
            }
        }
    }

    /**
     * 批量上传任务
     * @param uploadObjectInfos
     */
    public void createAndexecuteUploadTask(final List<UploadObjectInfo> uploadObjectInfos){
        if(null==uploadObjectInfos) return;
        initDefaultParams();
        //1.先鉴权，获取上传TOKEN及权限，获取上传反馈结果
        UserManager.getInstance().uploadFileAuthentication(uploadObjectInfos.get(0), new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null !=object && object instanceof UploadAuthenticationInfo){
                    final UploadAuthenticationInfo uploadAuthenticationInfo= (UploadAuthenticationInfo) object;
                    //替换服务器最新配置
                    if(!TextUtils.isEmpty(uploadAuthenticationInfo.getBucket())) uploadParamsConfig.setBucket(uploadAuthenticationInfo.getBucket());
                    if(!TextUtils.isEmpty(uploadAuthenticationInfo.getEndpoint())) uploadParamsConfig.setEndpoint(uploadAuthenticationInfo.getEndpoint());
                    if(!TextUtils.isEmpty(uploadAuthenticationInfo.getCallback())) uploadParamsConfig.setCallbackAddress(uploadAuthenticationInfo.getCallback());
                    if(!TextUtils.isEmpty(uploadAuthenticationInfo.getCallbackHost())) uploadParamsConfig.setCallBackHost(uploadAuthenticationInfo.getCallbackHost());
                    //2.鉴权通过，构造上传对象
                    if(null!=uploadParamsConfig&&null!=mConf){
                        totalUploadCount=uploadObjectInfos.size();
                        currentUploadCount=0;
                        //构造TOKEN
                        OSSFileFederationCredentialProvider provider=new OSSFileFederationCredentialProvider(uploadAuthenticationInfo);
                        final OSSClient ossClient = new OSSClient(VideoApplication.getInstance(), uploadParamsConfig.getEndpoint(), provider, mConf);
                        //3.开始上传，添加至管理队列
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                for (int i = 0; i < uploadObjectInfos.size(); i++) {
                                    new UploadVideoAsyncTask(uploadObjectInfos.get(i),ossClient).run();
                                }
                            }
                        }.start();
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                totalUploadCount =0;
                currentUploadCount=0;
                if(null!=mFileUploadListener){
                    mFileUploadListener.uploadFail(null,0,code,errorMsg,true);
                }
            }
        });
    }

    /**
     * 构造并执行单个上传任务
     * @param data
     */
    public void createAndexecuteUploadTask(final UploadObjectInfo data) {
        initDefaultParams();
        //1.先鉴权，获取上传TOKEN及权限，获取上传反馈结果
        UserManager.getInstance().uploadFileAuthentication(data, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null !=object && object instanceof UploadAuthenticationInfo){
                    final UploadAuthenticationInfo uploadAuthenticationInfo= (UploadAuthenticationInfo) object;
                    //替换服务器最新配置
                    if(!TextUtils.isEmpty(uploadAuthenticationInfo.getBucket())) uploadParamsConfig.setBucket(uploadAuthenticationInfo.getBucket());
                    if(!TextUtils.isEmpty(uploadAuthenticationInfo.getEndpoint())) uploadParamsConfig.setEndpoint(uploadAuthenticationInfo.getEndpoint());
                    if(!TextUtils.isEmpty(uploadAuthenticationInfo.getCallback())) uploadParamsConfig.setCallbackAddress(uploadAuthenticationInfo.getCallback());
                    if(!TextUtils.isEmpty(uploadAuthenticationInfo.getCallbackHost())) uploadParamsConfig.setCallBackHost(uploadAuthenticationInfo.getCallbackHost());
                    //2.鉴权通过，构造上传对象
                    if(null!=uploadParamsConfig&&null!=mConf){
                        totalUploadCount=1;
                        currentUploadCount=0;
                        //构造上传TOKEN
                        OSSFileFederationCredentialProvider provider=new OSSFileFederationCredentialProvider(uploadAuthenticationInfo);
                        final OSSClient ossClient = new OSSClient(VideoApplication.getInstance(), uploadParamsConfig.getEndpoint(), provider, mConf);
                        //3.开始上传，添加至管理队列
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                new UploadVideoAsyncTask(uploadAuthenticationInfo.getUploadInfo(),ossClient).run();
                            }
                        }.start();

                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                totalUploadCount =0;
                currentUploadCount=0;
                if(null!=mFileUploadListener){
                    //4.文件已存在，自己上传过了
                    if(1502==code){
                        mFileUploadListener.uploadFail(data,0,code,errorMsg,true);
                    //5.文件已存在，别人已经上传过了
                    }else if(1503==code){
                        data.setUploadProgress(100);
                        mFileUploadListener.uploadProgress(data,1,1);
                        //秒传
                        new android.os.Handler(Looper.getMainLooper()).postAtTime(new Runnable() {
                            @Override
                            public void run() {
                                if(null!=mFileUploadListener) mFileUploadListener.uploadSuccess(data,"上传完成",true);
                            }
                        }, SystemClock.uptimeMillis()+200);
                    }else{
                        mFileUploadListener.uploadFail(data,0,code,errorMsg,true);
                    }
                }
            }
        });
    }

    /**
     * 初始构造默认参数
     */
    private void initDefaultParams() {
        //构造默认上传配置
        if(null==uploadParamsConfig){
            uploadParamsConfig =new UploadParamsConfig();
            uploadParamsConfig.setBucket(Constant.STS_BUCKET);
            uploadParamsConfig.setCallbackAddress(Constant.STS_CALLBACKADDRESS);
            uploadParamsConfig.setEndpoint(Constant.STS_ENDPOINT);
            uploadParamsConfig.setEncryptResponse(true);
        }
        //构建上传Client配置项
        if(null==mConf){
            mConf = new ClientConfiguration();
            mConf.setConnectionTimeout(20 * 1000); // 连接超时
            mConf.setSocketTimeout(20 * 1000); // socket超时
            mConf.setMaxConcurrentRequest(3); // 最大并发上传任务数量，3个
            mConf.setMaxErrorRetry(5); // 失败后最大重试次数
        }
    }

    /**
     * 取消所有的上传任务
     */
    public void cleanUploadTask(){
        if(null!= mUploadTaskMap){
            Iterator<Map.Entry<Long, PauseableUploadTask>> iterator = mUploadTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, PauseableUploadTask> next = iterator.next();
                next.getValue().pause();
                if(null!= mUploadTaskMap) mUploadTaskMap.remove(next.getKey());
            }
        }
    }

    /**
     * 取消单个上传任务，并删除所有分片信息
     */
    public UploadDeteleTaskInfo canelUploadTask(UploadObjectInfo videoInfo) {

        UploadDeteleTaskInfo taskInfo=new UploadDeteleTaskInfo();
        if(null==videoInfo){
            taskInfo.setCancel(false);
            taskInfo.setMessage("要取消的视频参数为空");
            return taskInfo;
        }
        if(TextUtils.isEmpty(videoInfo.getUploadID())) {
            if(null==videoInfo){
                taskInfo.setCancel(false);
                taskInfo.setMessage("要取消的上传任务不存在");
                return taskInfo;
            }
        }
        //先暂停上传的任务
        AbortMultipartUploadRequest abort = new AbortMultipartUploadRequest(Constant.STS_BUCKET, videoInfo.getUploadFileFolder()+videoInfo.getFileName(), videoInfo.getUploadID());
        if(null!= mUploadTaskMap && mUploadTaskMap.size()>0){
            Iterator<Map.Entry<Long, PauseableUploadTask>> iterator = mUploadTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, PauseableUploadTask> next = iterator.next();
                if(videoInfo.getId()==next.getKey()){
                    next.getValue().pause();
                    taskInfo.setCancel(true);
                }
            }
        }
        if(null!=mOSSClientMap){
            Iterator<Map.Entry<Long, OSSClient>> iterator = mOSSClientMap.entrySet().iterator();
            while (iterator.hasNext()){
                try {
                    //若无异常删除成功
                    AbortMultipartUploadResult result = iterator.next().getValue().abortMultipartUpload(abort);// 若无异常抛出说明删除成功
                    if(null!=result){
                        if (204 == result.getStatusCode()) {
                            taskInfo.setCancel(true);
                            taskInfo.setMessage("取消上传成功");
                            if(null!= mUploadTaskMap) mUploadTaskMap.remove(videoInfo.getId());
                            return taskInfo;
                        }
                    }
                } catch (ClientException e) {
                    e.printStackTrace();
                    taskInfo.setCancel(false);
                    taskInfo.setMessage("取消失败，请检查您的网络连接");
                    return taskInfo;

                } catch (ServiceException e) {
                    taskInfo.setCancel(false);
                    taskInfo.setMessage("取消失败，服务器无响应");
                    return taskInfo;
                }
            }
        }
        taskInfo.setCancel(false);
        taskInfo.setMessage("取消失败，未知原因");
        return taskInfo;
    }

    /**
     * 上传
     */
    private class UploadVideoAsyncTask {

        private final UploadObjectInfo mUploadInfo;
        private final OSSClient mOssClient;

        public UploadVideoAsyncTask(UploadObjectInfo data, OSSClient ossClient) {
            this.mUploadInfo=data;
            this.mOssClient=ossClient;
            if(null== mUploadTaskMap) mUploadTaskMap =new HashMap<>();
            if(null==mOSSClientMap) mOSSClientMap=new HashMap<>();
        }

        public void run() {
            if(null!=mUploadInfo){
                //上传构造请求
                PauseableUploadRequest request = new PauseableUploadRequest(uploadParamsConfig.getBucket(), mUploadInfo.getUploadFileFolder()+mUploadInfo.getFileName(),mUploadInfo.getFilePath(), PART_SIZE);
                //上传进度监听
                request.setProgressCallback(new OSSProgressCallback<PauseableUploadRequest>() {
                    @Override
                    public void onProgress(PauseableUploadRequest request, long currentSize, long totalSize) {
                        int progress = (int) (100 * currentSize / totalSize);
                        LogUtil.msg("run---progress:"+progress);
                        if(null!=mUploadInfo) mUploadInfo.setUploadProgress(progress);
                        if(null!= mFileUploadListener) mFileUploadListener.uploadProgress(mUploadInfo,currentUploadCount+1, totalUploadCount);
                    }
                });
                //上传状态监听
                PauseableUploadTask pauseableUploadTask = new PauseableUploadTask(mOssClient, request, new OSSCompletedCallback<PauseableUploadRequest, PauseableUploadResult>() {
                    /**
                     * 上传成功
                     * @param request
                     * @param result
                     */
                    @Override
                    public void onSuccess(PauseableUploadRequest request, PauseableUploadResult result) {
                        currentUploadCount++;
                        //移除上传任务
                        if(null!= mUploadTaskMap) mUploadTaskMap.remove(mUploadInfo.getId());
                        //上传成功
                        mUploadInfo.setUploadProgress(100);
                        if(null!= mFileUploadListener) mFileUploadListener.uploadSuccess(mUploadInfo,"上传完成",currentUploadCount>=totalUploadCount?true:false);
                    }

                    /**
                     * 上传失败
                     * @param request
                     * @param clientException
                     * @param serviceException
                     *  上传失败有几种状态
                     *  error_code:1:本地文件不存在或读取SD卡权限被拒  2：客户端网络不可用 3：服务端接受文件失败或者参数错误
                     */
                    @Override
                    public void onFailure(PauseableUploadRequest request, ClientException clientException, ServiceException serviceException) {
                        currentUploadCount++;
                        //移除上传任务
                        if(null!= mUploadTaskMap && mUploadTaskMap.size()>0) mUploadTaskMap.remove(mUploadInfo.getId());
                        boolean isFinlish=currentUploadCount>=totalUploadCount?true:false;
                        if(null!=clientException){
                            if(null!=mFileUploadListener) mFileUploadListener.uploadFail(mUploadInfo,0,Constant.UPLOAD_ERROR_CODE_FILE_NOTFIND,clientException.getMessage(),isFinlish);
                            return;
                        }
                        if(null!=serviceException){
                            if(null!=mFileUploadListener) mFileUploadListener.uploadFail(mUploadInfo,0,Constant.UPLOAD_ERROR_CODE_FILE_NOTFIND,serviceException.getMessage(),isFinlish);
                            return;
                        }
                        if(null!=mFileUploadListener) mFileUploadListener.uploadFail(mUploadInfo,0,Constant.UPLOAD_ERROR_CODE_FILE_NOTFIND,"上传失败",isFinlish);
                    }
                });
                //设置上传参数,由阿里云回调给后台
                setUserPrams(pauseableUploadTask,mUploadInfo);
                try {
                    //新的上传任务
                    String uploadID=null;
                    if(TextUtils.isEmpty(mUploadInfo.getUploadID())){
                        uploadID = pauseableUploadTask.initUpload();//生成上传任务ID
                        mUploadInfo.setUploadID(uploadID);
                    }else{
                        uploadID=mUploadInfo.getUploadID();
                    }
                    //添加进任务管理实例中
                    if(null!= mUploadTaskMap){
                        mUploadTaskMap.put(mUploadInfo.getId(), pauseableUploadTask);
                    }
                    if(null!= mFileUploadListener){
                        mFileUploadListener.uploadStart(mUploadInfo);
                    }
                    pauseableUploadTask.upload(uploadID);
                }
                catch (ServiceException e) {
                    e.printStackTrace();
                    currentUploadCount++;
                    if(null!= mUploadTaskMap && mUploadTaskMap.size()>0) mUploadTaskMap.remove(mUploadInfo.getId());//移除上传任务栈中的元素
                    if(null!= mFileUploadListener){
                        boolean isFinlish=currentUploadCount>=totalUploadCount?true:false;
                        mFileUploadListener.uploadFail(mUploadInfo,e.getStatusCode(),Constant.UPLOAD_ERROR_CODE_SERVICEEXCEPTION,"上传失败-"+e.getMessage(),isFinlish);
                    }
                } catch (ClientException e) {
                    e.printStackTrace();
                    currentUploadCount++;
                    if(null!= mUploadTaskMap && mUploadTaskMap.size()>0) mUploadTaskMap.remove(mUploadInfo.getId());//移除上传任务栈中的元素
                    if(null!= mFileUploadListener){
                        boolean isFinlish=currentUploadCount>=totalUploadCount?true:false;
                        mFileUploadListener.uploadFail(mUploadInfo,0,Constant.UPLOAD_ERROR_CODE_CLIENTEXCEPTION,"上传失败-"+e.getMessage(),isFinlish);
                    }
                }
            }
        }
    }

    /**
     * 设置自定义参数 回调至Server端
     * @param pauseableUploadTask
     * @param videoInfo
     */
    private void setUserPrams(PauseableUploadTask pauseableUploadTask, UploadObjectInfo videoInfo) {
        Map<String,String> mParams = new HashMap<>();
        mParams.put("callbackUrl",uploadParamsConfig.getCallbackAddress());
        mParams.put("callbackHost",uploadParamsConfig.getCallBackHost());
        mParams.put("callbackBodyType",uploadParamsConfig.getCallBackType());
        mParams.put("callbackBody",
                "{\"bucket\":${bucket}," +
                "\"object\":${object}," +
                "\"mimeType\":${mimeType}," +
                "\"size\":${size}," +
                "\"filename\":${object}," +
                "\"file_md5\":${x:file_md5}," +
                "\"file_size\":${x:file_size}," +
                "\"file_type\":${x:file_type}," +
                "\"file_name\":${x:file_name}," +
                "\"imeil\":${x:imeil}," +
                "\"userid\":${x:userid}," +
                "\"video_frame\":${x:video_frame}," +
                "\"file_width\":${x:file_width}," +
                "\"file_height\":${x:file_height}," +
                "\"video_desp\":${x:video_desp}," +
                "\"video_durtion\":${x:video_durtion}," +
                "\"device_net_ip\":${x:device_net_ip}" +
                "}");

        Map<String,String>mParamVars = new HashMap<>();
        mParamVars.put("x:imeil", VideoApplication.mUuid);
        mParamVars.put("x:userid", UserManager.getInstance().getUserId());
        mParamVars.put("x:file_md5",videoInfo.getFileMd5());//MD5
        mParamVars.put("x:file_size",String.valueOf(videoInfo.getFileSize()));//文件大小
        mParamVars.put("x:file_type",String.valueOf(videoInfo.getFileSourceType()));//文件类型
        mParamVars.put("x:file_name",videoInfo.getFileName());//文件名称
        mParamVars.put("x:video_frame",String.valueOf(videoInfo.getVideoFrame()));//视频封面
        mParamVars.put("x:file_width",String.valueOf(videoInfo.getFileWidth()));//视频宽
        mParamVars.put("x:file_height",String.valueOf(videoInfo.getFileHeight()));//视频高
        mParamVars.put("x:video_durtion",String.valueOf(videoInfo.getVideoDurtion()));//视频时长
        mParamVars.put("x:video_desp",TextUtils.isEmpty(videoInfo.getVideoDesp())?"暂无描述":videoInfo.getVideoDesp());//视频描述
        String locastHostIP = SystemUtils.getLocastHostIP();
        mParamVars.put("x:device_net_ip",TextUtils.isEmpty(locastHostIP)?"0":locastHostIP );//用户IP
        pauseableUploadTask.setUserPrams(mParams,mParamVars);
        Logger.d(TAG,"setUserPrams-->mParams:"+mParams+",mParamVars:"+mParamVars);
    }

    /**
     * 调用此方法将失去管理上传任务能力
     */
    public void onDestroy() {
        if(null!=mUploadTaskMap) mUploadTaskMap.clear(); mUploadTaskMap=null;
        if(null!=mOSSClientMap) mOSSClientMap.clear(); mOSSClientMap=null;
        uploadParamsConfig=null; mFileUploadListener=null;mConf=null;
    }
}
