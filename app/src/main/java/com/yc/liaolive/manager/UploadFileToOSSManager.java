package com.yc.liaolive.manager;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.yc.liaolive.bean.FileInfos;
import com.yc.liaolive.bean.ImageInfo;
import com.yc.liaolive.bean.UploadObjectInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.interfaces.OnUploadObjectListener;
import com.yc.liaolive.ui.dialog.UploadProgressDialog;
import com.yc.liaolive.upload.listener.FileUploadListener;
import com.yc.liaolive.upload.manager.FileUploadTaskManager;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.util.VideoUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/14
 * 文件上传，维护上传和UI交互
 */

public class UploadFileToOSSManager implements FileUploadListener {

    private static final String TAG = "UploadWindownManager";
    private OnUploadObjectListener mListener;//界面组件需要实现的监听器
    private UploadProgressDialog mUploadProgressDialog;
    private Handler mHandler;
    private static UploadFileToOSSManager cUploadWindownManager;
    private boolean mShowDetails;
    private boolean isContinuation;//是否连续上传

    public static UploadFileToOSSManager get(Activity activity) {
        cUploadWindownManager = new UploadFileToOSSManager(activity);
        return cUploadWindownManager;
    }

    public UploadFileToOSSManager(Activity activity){
        if(null!=activity){
            mUploadProgressDialog = new UploadProgressDialog(activity);
            mUploadProgressDialog.setMax(100);
            mUploadProgressDialog.setOnDialogBackListener(new UploadProgressDialog.OnDialogBackListener() {
                @Override
                public void onBack() {
                    ToastUtils.showCenterToast("请等待上传完成！");
                }
            });
        }
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 添加上传监听
     * @param listener
     * @return
     */
    public UploadFileToOSSManager addUploadListener(OnUploadObjectListener listener) {
        this.mListener=listener;
        return this;
    }

    /**
     * 是否显示上传详情
     * @param showDetails
     * @return
     */
    public UploadFileToOSSManager showDetails(boolean showDetails) {
        this.mShowDetails=showDetails;
        return cUploadWindownManager;
    }

    /**
     * 设置是否连续上传
     * @param isContinuation
     * @return
     */
    public UploadFileToOSSManager setContinuation(boolean isContinuation) {
        this.isContinuation=isContinuation;
        return cUploadWindownManager;
    }

    /**
     * 构造异步上传任务，普通的小视频文件上传
     * @param filePath
     */
    public void createAsyncUploadTask(String filePath) {
        if(TextUtils.isEmpty(filePath)) return;
        createAsyncUploadTask(new File(filePath));
    }

    /**
     * 构造异步上传任务，普通的小视频文件上传
     * @param file
     */
    public void createAsyncUploadTask(File file) {
        if(null==file) return;
        //开始构造上传实例
        UploadObjectInfo objectInfo=new UploadObjectInfo();
        objectInfo.setFilePath(file.getAbsolutePath());
        objectInfo.setId(System.currentTimeMillis());
        createAsyncUploadTask(objectInfo);
    }

    /**
     * 批量创建上传任务
     * @param imageInfos，相册图片
     * 这里只对图片进行了处理
     */
    public void createAsyncUploadTask(final List<ImageInfo> imageInfos) {
        if(null==imageInfos) return;
        //先弹出上传对话框
        if(null!=mUploadProgressDialog&&!mUploadProgressDialog.isShowing()){
            mUploadProgressDialog.setProgress(1);
            mUploadProgressDialog.setTipsMessage("文件上传中，请稍后...");
            mUploadProgressDialog.show();
        }
        List<UploadObjectInfo> uploadObjectInfos=new ArrayList<>();
        //封装上传对象
        for (int i = 0; i < imageInfos.size(); i++) {
            ImageInfo imageInfo = imageInfos.get(i);
            //1.构造单个上传任务
            UploadObjectInfo uploadObjectInfo=new UploadObjectInfo();
            uploadObjectInfo.setFilePath(imageInfo.getFilePath());
            //确定存储在OOS文件目录名称
            uploadObjectInfo.setUploadFileFolder(0 == uploadObjectInfo.getFileSourceType() ? Constant.OOS_DIR_IMAGE : Constant.OOS_DIR_VIDEO);
            //确定文件类型，这里批量是图片类型
            uploadObjectInfo.setFileSourceType(Constant.OSS_FILE_TYPE_IMAGE);
            //2.检查文件大小
            FileInfos newFileInfo = VideoUtils.getVideoInfo(uploadObjectInfo.getFilePath(),uploadObjectInfo.getFileSourceType());
            if(null!=newFileInfo){
                uploadObjectInfo.setFileWidth(newFileInfo.getFileWidth());
                uploadObjectInfo.setFileHeight(newFileInfo.getFileHeight());
                uploadObjectInfo.setVideoDurtion(newFileInfo.getVideoDurtion());
                uploadObjectInfo.setFileSize(newFileInfo.getFileSize());
            }
            //3.获取文件MD5值
            UploadObjectInfo fileMd5 = getFileMd5(uploadObjectInfo);
            if(null!=fileMd5) uploadObjectInfos.add(fileMd5);
        }
        FileUploadTaskManager.getInstance().setUploadListener(UploadFileToOSSManager.this).createAndexecuteUploadTask(uploadObjectInfos);
    }

    /**
     * 获取文件的MD5值
     * @param uploadObjectInfo
     */
    private UploadObjectInfo getFileMd5(UploadObjectInfo uploadObjectInfo) {
        try {
            uploadObjectInfo.setFileMd5(FileUtils.getMd5ByFile(new File(uploadObjectInfo.getFilePath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }catch (RuntimeException e){

        }finally {
            if(TextUtils.isEmpty(uploadObjectInfo.getFileMd5())) uploadObjectInfo.setFileMd5("file"+String.valueOf(System.currentTimeMillis()));
            uploadObjectInfo.setFileName(uploadObjectInfo.getFileMd5()+"."+Utils.getFilePostName(uploadObjectInfo.getFilePath()));
            Logger.d(TAG,"getFileMd5---最终要上传的文件基本信息：\n"
                    +"FILE_NAME:"+uploadObjectInfo.getFileName()+"\n"
                    +"FILE_MD5:"+uploadObjectInfo.getFileMd5()+"\n"
                    +"FILE_WIDTH:"+uploadObjectInfo.getFileWidth()+"\n"
                    +"FILE_HEIGHT:"+uploadObjectInfo.getFileHeight()+"\n"
                    +"FILE_SIZE:"+uploadObjectInfo.getFileSize()+"KB\n"
                    +"POST_NAME：."+Utils.getFilePostName(uploadObjectInfo.getFilePath())+"\n"
                    +"SOURCE:"+uploadObjectInfo.getFileSourceType()+"\n"
                    +"DURTION:"+uploadObjectInfo.getVideoDurtion()+"\n"
                    +"OOS_DIR:"+uploadObjectInfo.getUploadFileFolder()+"\n"
                    +"VIDEO_DESP:"+uploadObjectInfo.getVideoDesp()
                    );

        }
        return uploadObjectInfo;
    }


//    /**
//     * 构造异步上传任务 开启图片压缩
//     * @param objectInfo
//     */
//    public void createAsyncUploadTask(UploadObjectInfo objectInfo) {
//        if(null==objectInfo) return;
//        String filePath = objectInfo.getFilePath();
//        if(TextUtils.isEmpty(filePath)) return;
//        //先弹出上传对话框
//        if(null!=mUploadProgressDialog&&!mUploadProgressDialog.isShowing()){
//            mUploadProgressDialog.show();
//            mUploadProgressDialog.setProgress(1);
//            mUploadProgressDialog.setTipsMessage("文件上传中，请稍后...");
//        }
//        //1.获取文件基本信息
//        if(filePath.endsWith(".mp4")||filePath.endsWith(".flv")||filePath.endsWith(".3gp")||filePath.endsWith(".mov")||filePath.endsWith("3gpp")){
//            objectInfo.setFileSourceType(1);
//        }
//        //2.确定存储在OOS文件名称
//        objectInfo.setUploadFileFolder(0==objectInfo.getFileSourceType()? Constant.OOS_DIR_IMAGE:Constant.OOS_DIR_VIDEO);
//        //3.获取文件基本信息
//        FileInfos fileInfo = VideoUtils.getVideoInfo(filePath,objectInfo.getFileSourceType());
//        if(null!=fileInfo){
//            objectInfo.setFileWidth(fileInfo.getFileWidth());
//            objectInfo.setFileHeight(fileInfo.getFileHeight());
//            objectInfo.setVideoDurtion(fileInfo.getVideoDurtion());
//            objectInfo.setFileSize(fileInfo.getFileSize());
//        }
//        //4.如果是图片，看是否需要压缩处理
//        if(filePath.endsWith(".jpg")||filePath.endsWith(".jpeg")||filePath.endsWith(".png")||filePath.endsWith("gif")||filePath.endsWith("bmp")){
//            objectInfo.setFileSourceType(0);
//            Logger.d(TAG,"图片类型");
//            if(objectInfo.getFileSize()>=150){
//                String outPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "CaoLiaoTemp" + File.separator;
//                Logger.d(TAG,"图片类型文件，需要压缩");
//                File newFile=null;
//                try {
//                    newFile= new CompressHelper.Builder(VideoApplication.getInstance().getApplicationContext())
//                            .setQuality(90)//压缩质量,越大质量越高
//                            .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
//                            .setDestinationDirectoryPath(outPath)
//                            .build()
//                            .compressToFile(new File(filePath));
//                }catch (RuntimeException e){
//
//                }finally {
//                    if(null!=newFile){
//                        objectInfo.setFilePath(newFile.getAbsolutePath());
//                        FileInfos newFileInfo = VideoUtils.getVideoInfo(filePath,objectInfo.getFileSourceType());
//                        if(null!=newFileInfo){
//                            objectInfo.setFileWidth(newFileInfo.getFileWidth());
//                            objectInfo.setFileHeight(newFileInfo.getFileHeight());
//                            objectInfo.setVideoDurtion(newFileInfo.getVideoDurtion());
//                            objectInfo.setFileSize(newFileInfo.getFileSize());
//                        }
//                    }
//                }
//            }
//        }
//        //5.拦截超过限制的视频文件
//        if(1==objectInfo.getFileSourceType()){
//            if(objectInfo.getVideoDurtion()>=(30*60*1000)){//30分钟之内
//                ToastUtils.showCenterToast("视频长度超过30分钟限制");
//                onStop();
//                return;
//            }
//            if(objectInfo.getFileSize()>=(1024*100)){//100M之内
//                ToastUtils.showCenterToast("视频大小超过100M限制");
//                onStop();
//                return;
//            }
//        }
//        //6.获取文件的MD5值
//        UploadObjectInfo fileMd5 = getFileMd5(objectInfo);
//        if(null!=fileMd5){
//            //开始构造上传任务
//            FileUploadTaskManager.getInstance().setUploadListener(this).createAndexecuteUploadTask(fileMd5);
//        }else{
//            onStop();
//        }
//    }


    /**
     * 构造异步上传任务，小视频
     * @param objectInfo
     */
    public void createAsyncUploadTask(UploadObjectInfo objectInfo) {
        if(null==objectInfo) return;
        String filePath = objectInfo.getFilePath();
        if(TextUtils.isEmpty(filePath)) return;
        //先弹出上传对话框
        if(null!=mUploadProgressDialog&&!mUploadProgressDialog.isShowing()){
            mUploadProgressDialog.show();
            mUploadProgressDialog.setProgress(1);
            mUploadProgressDialog.setTipsMessage("文件上传中，请稍后...");
        }
        //1.获取文件基本信息
        if(filePath.endsWith(".mp4")||filePath.endsWith(".flv")||filePath.endsWith(".3gp")||filePath.endsWith(".mov")||filePath.endsWith("3gpp")){
            objectInfo.setFileSourceType(Constant.OSS_FILE_TYPE_VIDEO);
        }
        //2.确定存储在OOS文件名称
        objectInfo.setUploadFileFolder(0==objectInfo.getFileSourceType()? Constant.OOS_DIR_IMAGE:Constant.OOS_DIR_VIDEO);
        //3.获取文件基本信息
        FileInfos fileInfo = VideoUtils.getVideoInfo(filePath,objectInfo.getFileSourceType());
        if(null!=fileInfo){
            objectInfo.setFileWidth(fileInfo.getFileWidth());
            objectInfo.setFileHeight(fileInfo.getFileHeight());
            objectInfo.setVideoDurtion(fileInfo.getVideoDurtion());
            objectInfo.setFileSize(fileInfo.getFileSize());
        }
        //5.拦截超过限制的视频文件
        if(1==objectInfo.getFileSourceType()){
            if(objectInfo.getVideoDurtion()>=(30*60*1000)){//30分钟之内
                ToastUtils.showCenterToast("视频长度超过30分钟限制");
                onStop();
                return;
            }
            if(objectInfo.getFileSize()>=(1024*100)){//100M之内
                ToastUtils.showCenterToast("视频大小超过100M限制");
                onStop();
                return;
            }
        }
        //6.获取文件的MD5值
        UploadObjectInfo fileMd5 = getFileMd5(objectInfo);
        if(null!=fileMd5){
            //开始构造上传任务
            FileUploadTaskManager.getInstance().setUploadListener(this).createAndexecuteUploadTask(fileMd5);
        }else{
            onStop();
        }
    }


    /**
     * 开始上传任务，ASMR分类下的音频、封面
     * @param objectInfo
     */
    public void startcreateAsyncUploadTask(UploadObjectInfo objectInfo) {
        if(null==objectInfo) return;
        //先弹出上传对话框
        if(null!=mUploadProgressDialog&&!mUploadProgressDialog.isShowing()){
            mUploadProgressDialog.show();
            mUploadProgressDialog.setProgress(1);
            mUploadProgressDialog.setTipsMessage("文件上传中，请稍后...");
        }
        //开始构造上传任务
        FileUploadTaskManager.getInstance().setUploadListener(this).createAndexecuteUploadTask(objectInfo);
    }

    /**
     * 构造异步上传任务，ASMR类视频文件上传
     * @param objectInfo
     */
    public void createAsyncUploadTaskToAsmr(UploadObjectInfo objectInfo) {
        if(null==objectInfo) return;
        String filePath = objectInfo.getFilePath();
        if(TextUtils.isEmpty(filePath)) return;
        //先弹出上传对话框
        if(null!=mUploadProgressDialog&&!mUploadProgressDialog.isShowing()){
            mUploadProgressDialog.show();
            mUploadProgressDialog.setProgress(1);
            mUploadProgressDialog.setTipsMessage("文件上传中，请稍后...");
        }
        //1.获取文件基本信息
        if(filePath.endsWith(".mp4")||filePath.endsWith(".flv")||filePath.endsWith(".3gp")||filePath.endsWith(".mov")||filePath.endsWith("3gpp")){
            objectInfo.setFileSourceType(Constant.OSS_FILE_TYPE_ASMR_VIDEO);
        }
        //2.确定存储在OOS文件名称
        objectInfo.setUploadFileFolder(Constant.OOS_DIR_ASMR_VIDEO);
        objectInfo.setFileName(Utils.getFileName(filePath));
        //3.获取文件基本信息
        FileInfos fileInfo = VideoUtils.getVideoInfo(filePath,objectInfo.getFileSourceType());
        if(null!=fileInfo){
            objectInfo.setFileWidth(fileInfo.getFileWidth());
            objectInfo.setFileHeight(fileInfo.getFileHeight());
            objectInfo.setVideoDurtion(fileInfo.getVideoDurtion());
            objectInfo.setFileSize(fileInfo.getFileSize());
        }
        //5.拦截超过限制的视频文件
        if(1==objectInfo.getFileSourceType()){
            if(objectInfo.getVideoDurtion()>=(30*60*1000)){//30分钟之内
                ToastUtils.showCenterToast("视频长度超过30分钟限制");
                onStop();
                return;
            }
            if(objectInfo.getFileSize()>=(1024*100)){//100M之内
                ToastUtils.showCenterToast("视频大小超过100M限制");
                onStop();
                return;
            }
        }
        //6.获取文件的MD5值
        UploadObjectInfo fileMd5 = getFileMd5(objectInfo);
        if(null!=fileMd5){
            //开始构造上传任务
            FileUploadTaskManager.getInstance().setUploadListener(this).createAndexecuteUploadTask(fileMd5);
        }else{
            onStop();
        }
    }

    //======================================内部上传状态监听=========================================

    @Override
    public void uploadStart(UploadObjectInfo data) {
        if(null!=mHandler){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null!=mListener) mListener.onStart();
                    if(null!=mUploadProgressDialog&&!mUploadProgressDialog.isShowing()){
                        mUploadProgressDialog.show();
                        mUploadProgressDialog.setProgress(1);
                        mUploadProgressDialog.setTipsMessage("文件上传中，请稍后...");
                    }
                }
            });
        }
    }

    @Override
    public void uploadSuccess(final UploadObjectInfo data, final String extras, boolean isFinlish) {
        if(null!=mHandler&&isFinlish){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null!=mListener) mListener.onSuccess(data,extras);
                    onStop();
                }
            });
        }
    }

    @Override
    public void uploadProgress(final UploadObjectInfo data, final int currentCount, final int totalCount) {
        if(null!=mHandler){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(null!=mUploadProgressDialog){
                            if(!mUploadProgressDialog.isShowing()) mUploadProgressDialog.show();
                            mUploadProgressDialog.setProgress(data.getUploadProgress());
                            if(mShowDetails) mUploadProgressDialog.setDetails(currentCount+"/"+totalCount);
                        }
                        if(null!=mListener) mListener.onProgress(data.getUploadProgress());
                    }catch (RuntimeException e){

                    }catch (Exception e){

                    }
                }
            });
        }
    }

    @Override
    public void uploadFail(final UploadObjectInfo data, final int stateCode, int errorCode, final String errorMsg,boolean isFinlish) {
        if(isFinlish&&null!=mHandler){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null!=mUploadProgressDialog) mUploadProgressDialog.setTipsMessage("上传失败！");
                    if(null!=mListener) mListener.onFail(stateCode,errorMsg);
                    onStop();
                }
            });
        }
    }

    private void onStop(){
        if(null!=mUploadProgressDialog){
            mUploadProgressDialog.dismiss();
            mUploadProgressDialog=null;
        }
        mListener=null;
        if(null!=mHandler) mHandler.removeMessages(0); mHandler=null;
    }

    public void onDestroy(){
        onStop();
        FileUploadTaskManager.getInstance().onDestroy();
    }
}
