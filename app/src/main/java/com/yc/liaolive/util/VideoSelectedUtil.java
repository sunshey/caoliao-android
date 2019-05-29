package com.yc.liaolive.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.yc.liaolive.bean.VideoDetailsMenu;
import com.yc.liaolive.media.ui.activity.MediaLocationVideoListActivity;
import com.yc.liaolive.ui.dialog.CommonMenuDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/6
 * 本地视频选取
 * 支持从系统相机录制和本地选取
 */

public class VideoSelectedUtil {

    public static final int PERMISSION_REQUST_CODE=10001;

    private static final String TAG = "VideoSelectedUtil";
    private static VideoSelectedUtil mInstance;
    private Activity mActivity;
    //录制
    private String outPathDir=null;//默认的输出路径
    private String VIDEO_OUT_DIR_NAME = "record_video.mp4";//视频输出文件名
    private  final int INTENT_CODE_VIDEO_REC_REQUST = 131;//系统相机录制
    //本地检索的
    private  final int INTENT_CODE_VIDEO_FILE_REQUST = 133;//本地检索文件
    private  final int INTENT_CODE_VIDEO_FILE_RESULT = 134;//本地检索文件返回
    private File mOutFile;

    public static synchronized VideoSelectedUtil getInstance(){
        synchronized (VideoSelectedUtil.class){
            if(null==mInstance){
                mInstance=new VideoSelectedUtil();
            }
        }
        return mInstance;
    }

    public VideoSelectedUtil(){
        outPathDir= Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator+ "VideoLive"+ File.separator+ "Video"+File.separator;
        Logger.d(TAG,"outPathDir:"+outPathDir);
    }

    /**
     * 必须先调用次方法依附 Avtivity
     * @param activity
     * @return
     */
    public VideoSelectedUtil attachActivity(Activity activity){
        mActivity=activity;
        return this;
    }

    public interface OnSelectedPhotoOutListener{
        void onOutFile(String filePath);
        void onError(int code, String errorMsg);
    }

    private  OnSelectedPhotoOutListener mOnSelectedPhotoOutListener;

    /**
     * 设置状态监听
     * @param onSelectedPhotoOutListener
     * @return
     */
    public VideoSelectedUtil setOnSelectedPhotoOutListener(OnSelectedPhotoOutListener onSelectedPhotoOutListener) {
        mOnSelectedPhotoOutListener = onSelectedPhotoOutListener;
        return this;
    }

    /**
     * 设置图片最终的输出路径
     * @param outPathDir
     */
    public VideoSelectedUtil setOutPathDir(String outPathDir) {
        this.outPathDir = outPathDir;
        return this;
    }

    /**
     * 设置输出文件名称
     * @param fileName
     * @return
     */
    public VideoSelectedUtil setOutPutFileName(String fileName) {
        this.VIDEO_OUT_DIR_NAME = fileName;
        return this;
    }

    /**
     * 直接录制
     */
    public void startRecord(){
        if(null==mActivity) return;
        if(checkRecordPermission(mActivity)){
            File file = new File(outPathDir);
            if(!file.exists()){
                file.mkdirs();
            }
            try {
                Logger.d(TAG,"startRecord--1");
                headVideoFromCameraRec();
            }catch (Exception e){
                if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onError(-1,"初始化文件失败:"+e.getMessage());
            }
        }
    }

    /**
     * 开始选取视频
     */
    public void start(){
        if(null==mActivity) return;
        if(checkRecordPermission(mActivity)){
            File file = new File(outPathDir);
            if(!file.exists()){
                file.mkdirs();
            }
            try {
                List<VideoDetailsMenu> list=new ArrayList<>();
                VideoDetailsMenu videoDetailsMenu1=new VideoDetailsMenu();
                videoDetailsMenu1.setItemID(1);
                videoDetailsMenu1.setTextColor("#FF576A8D");
                videoDetailsMenu1.setItemName("从相册选择");
                list.add(videoDetailsMenu1);
                VideoDetailsMenu videoDetailsMenu2=new VideoDetailsMenu();
                videoDetailsMenu2.setItemID(2);
                videoDetailsMenu2.setTextColor("#FF576A8D");
                videoDetailsMenu2.setItemName("录制");
                list.add(videoDetailsMenu2);
                CommonMenuDialog commonMenuDialog =new CommonMenuDialog(mActivity);
                commonMenuDialog.setData(list);
                commonMenuDialog.setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int itemID, VideoDetailsMenu videoDetailsMenu) {
                        switch (itemID) {
                            case 1:
                                headVideoFromGallery();
                                break;
                            case 2:
                                headVideoFromCameraRec();
                                break;
                        }
                    }
                });
                commonMenuDialog.show();
            }catch (Exception e){
                if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onError(-1,"初始化文件失败:"+e.getMessage());
            }
        }
    }

    /**
     * 检查所必须权限
     * @param activity
     * @return
     */
    public static boolean checkRecordPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(activity, permissions.toArray(new String[0]), PERMISSION_REQUST_CODE);
                return false;
            }
        }
        return true;
    }


    /**
     * 调用系统相机录制视频
     */
    private void headVideoFromCameraRec() {
        if (null == mActivity) return;
        outPathDir = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "VideoLive" + File.separator + "Video" + File.separator;
        //创建输出文件
        //存放在sd卡的根目录下
        mOutFile = new File(outPathDir, VIDEO_OUT_DIR_NAME);
        //生成Intent.
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtils.getUriForFile(mActivity.getApplicationContext(),mOutFile));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);//设置拍摄的质量 0：低质量 1：高质量
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);//限制持续时长,单位秒
        //启动摄像头应用程序
        mActivity.startActivityForResult(intent, INTENT_CODE_VIDEO_REC_REQUST);
    }

    /**
     * 从本地相册选取视频
     */
    private void headVideoFromGallery() {
        if(null==mActivity) return;
        Intent intent=new Intent(mActivity, MediaLocationVideoListActivity.class);
        mActivity.startActivityForResult(intent,INTENT_CODE_VIDEO_FILE_REQUST);
    }

    /**
     * 在宿主Activity中对应方法调用
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUST_CODE:
                break;
            default:
                break;
        }
    }

    /**
     * 在宿主Activity中对应方法调用
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(null!=mActivity){
            if(resultCode== Activity.RESULT_CANCELED){
                return;
            }
            if(requestCode==INTENT_CODE_VIDEO_REC_REQUST&&null!=mOutFile&&mOutFile.isFile()){
                if(null!=mOnSelectedPhotoOutListener&&null!=mOutFile.getAbsolutePath()) {
                    mOnSelectedPhotoOutListener.onOutFile(mOutFile.getAbsolutePath());
                }
            }else if(requestCode==INTENT_CODE_VIDEO_FILE_REQUST&&resultCode==INTENT_CODE_VIDEO_FILE_RESULT){
                if(null!=data.getStringExtra("videoPath")){
                    if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onOutFile(data.getStringExtra("videoPath"));
                }
            }
        }
    }

    /**
     * 对应方法调用
     */
    public void onDestroy(){
        mActivity=null; mOnSelectedPhotoOutListener=null;mInstance=null;
    }
}
