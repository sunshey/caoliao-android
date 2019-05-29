package com.yc.liaolive.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import com.yc.liaolive.bean.VideoDetailsMenu;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.media.ui.activity.ClipImageActivity;
import com.yc.liaolive.media.ui.activity.CropImageActivity;
import com.yc.liaolive.media.ui.activity.MediaPictruePhotoActivity;
import com.yc.liaolive.ui.dialog.CommonMenuDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/6
 * 本地图片选取
 */

public class PhotoSelectedUtil {

    private static final String TAG = "PhotoSelectedUtil";
    private static PhotoSelectedUtil mInstance;
    private Activity mActivity;
    private File mTempFile;
    private File mOutFilePath;
    private  final String IMAGE_DRR_PATH = "photo_image.jpg";//最终输出文件名
    private String mOutFileName=IMAGE_DRR_PATH;//输出文件名
    private  final String IMAGE_DRR_PATH_TEMP = "photo_image_temp.jpg";//临时图片
    private  final int INTENT_CODE_GALLERY_REQUEST = 121;//相册
    private  final int INTENT_CODE_CAMERA_REQUEST = 122;//相机
    private int widthScale=3;//裁剪比例
    private int heightScale=3;//裁剪比例
    private int maxWidth=1000;//最大宽
    private String outPathDir=null;//默认的输出路径
    private boolean clipCircle;//是否是圆形裁剪
    private boolean isSkipClip;//是否跳过裁剪
    private int cropMode;//裁剪模式 0：普通 1：新
    private int mImageCutStyle=0;//0：暗色，1：亮色

    public static synchronized PhotoSelectedUtil getInstance(){
        synchronized (PhotoSelectedUtil.class){
            if(null==mInstance){
                mInstance=new PhotoSelectedUtil();
            }
        }
        return mInstance;
    }

    public PhotoSelectedUtil(){
        outPathDir= Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator+ "VideoLive"+ File.separator+ "Photo"+File.separator;
    }

    /**
     * 必须先调用次方法依附 Avtivity
     * @param activity
     * @return
     */
    public PhotoSelectedUtil attachActivity(Activity activity){
        mActivity=activity;
        return this;
    }

    /**
     * 配置裁剪模式，0 ：旧模式 1：新模式
     * @param mode
     * @return
     */
    public PhotoSelectedUtil setCropMode(int mode) {
        this.cropMode=mode;
        return this;
    }

    /**
     * 设置输出文件名称,不带后缀
     * @param outFileName
     * @return
     */
    public PhotoSelectedUtil setOutFileName(String outFileName) {
        mOutFileName = outFileName;
        return this;
    }

    /**
     * 配置图片裁剪界面主题样式
     * @param imageCutStyle
     * @return
     */
    public PhotoSelectedUtil setImageCutStyle(int imageCutStyle) {
        mImageCutStyle = imageCutStyle;
        return this;
    }


    public interface OnSelectedPhotoOutListener{
        void onOutFile(File  file);
        void onError(int code,String errorMsg);
    }

    private  OnSelectedPhotoOutListener mOnSelectedPhotoOutListener;

    /**
     * 设置状态监听
     * @param onSelectedPhotoOutListener
     * @return
     */
    public PhotoSelectedUtil setOnSelectedPhotoOutListener(OnSelectedPhotoOutListener onSelectedPhotoOutListener) {
        mOnSelectedPhotoOutListener = onSelectedPhotoOutListener;
        return this;
    }

    /**
     * 设置图片最终的输出路径
     * @param outPathDir
     */
    public PhotoSelectedUtil setOutPathDir(String outPathDir) {
        this.outPathDir = outPathDir;
        return this;
    }

    /**
     * 设置最大的宽
     * @param maxWidth
     */
    public PhotoSelectedUtil setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    /**
     * 设置裁剪的比例  宽
     * @param widthScale
     */
    public PhotoSelectedUtil setCatScaleWidth(int widthScale){
        this.widthScale=widthScale;
        return this;
    }

    /**
     * 设置裁剪的比例  高
     * @param heightScale
     */
    public PhotoSelectedUtil setCatScaleHeight(int heightScale){
        this.heightScale=heightScale;
        return this;
    }

    /**
     * 是否是圆形裁剪输出
     * @param clipCircle
     * @return
     */
    public PhotoSelectedUtil setClipCircle(boolean clipCircle) {
        this.clipCircle = clipCircle;
        return this;
    }

    /**
     * 是否跳过裁剪
     * @param skipClip
     * @return
     */
    public PhotoSelectedUtil setSkipClip(boolean skipClip) {
        isSkipClip = skipClip;
        return this;
    }

    /**
     * 开始拍照
     */
    public void startTakePictrue(){
        if(null==mActivity) return;
        if(checkRecordPermission(mActivity)){
            outPathDir=Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator+ "VideoLive"+ File.separator+ "Photo"+File.separator;
            File file = new File(outPathDir);
            if(!file.exists()){
                file.mkdirs();
            }
            try {
                mOutFilePath = new File(outPathDir + mOutFileName);
                mTempFile = new File(outPathDir + IMAGE_DRR_PATH_TEMP);
                headImageFromCameraCap();
            }catch (Exception e){
                if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onError(-1,"初始化文件失败:"+e.getMessage());
            }
        }
    }

    /**
     * 去预览裁剪
     * @param filePath
     */
    public void startCutPreview(String filePath) {
        outPathDir=Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator+ "VideoLive"+ File.separator+ "Photo"+File.separator;
        File file = new File(outPathDir);
        if(!file.exists()){
            file.mkdirs();
        }
        try {
            mOutFilePath = new File(outPathDir + mOutFileName);
            mTempFile = new File(filePath);
            clippingPictures();
        }catch (Exception e){
            if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onError(-1,"初始化文件失败:"+e.getMessage());
        }
    }

    /**
     * 开始选取照片
     */
    public void start(){
        if(null==mActivity) return;
        if(checkRecordPermission(mActivity)){
            outPathDir=Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator+ "VideoLive"+ File.separator+ "Photo"+File.separator;
            File file = new File(outPathDir);
            if(!file.exists()){
                file.mkdirs();
            }
            try {
                mOutFilePath = new File(outPathDir + mOutFileName);
                //删除已存在的缓存文件
                mTempFile = new File(outPathDir + IMAGE_DRR_PATH_TEMP);
                List<VideoDetailsMenu> list=new ArrayList<>();
                VideoDetailsMenu videoDetailsMenu1=new VideoDetailsMenu();
                videoDetailsMenu1.setItemID(1);
                videoDetailsMenu1.setTextColor("#FF576A8D");
                videoDetailsMenu1.setItemName("从相册选择");
                list.add(videoDetailsMenu1);
                VideoDetailsMenu videoDetailsMenu2=new VideoDetailsMenu();
                videoDetailsMenu2.setItemID(2);
                videoDetailsMenu2.setTextColor("#FF576A8D");
                videoDetailsMenu2.setItemName("拍一张");
                list.add(videoDetailsMenu2);
                CommonMenuDialog commonMenuDialog =new CommonMenuDialog(mActivity);
                commonMenuDialog.setData(list);
                commonMenuDialog.setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int itemID, VideoDetailsMenu videoDetailsMenu) {
                        switch (itemID) {
                            case 1:
                                headImageFromGallery();
                                break;
                            case 2:
                                headImageFromCameraCap();
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
     * 开始选取照片
     * 跳转至系统相册
     */
    public void startSystem(){
        if(null==mActivity) return;
        if(checkRecordPermission(mActivity)){
            outPathDir=Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator+ "VideoLive"+ File.separator+ "Photo"+File.separator;
            File file = new File(outPathDir);
            if(!file.exists()){
                file.mkdirs();
            }
            try {
                mOutFilePath = new File(outPathDir + mOutFileName);
                //删除已存在的缓存文件
                mTempFile = new File(outPathDir + IMAGE_DRR_PATH_TEMP);
                headImageFromGallery();
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
                ActivityCompat.requestPermissions(activity,
                        permissions.toArray(new String[0]),
                        100);
                return false;
            }
        }
        return true;
    }


    // 从本地相册选取图片作为头像
    private void headImageFromGallery() {
        if(null==mActivity) return;
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");//选择图片
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        mActivity.startActivityForResult(intentFromGallery, INTENT_CODE_GALLERY_REQUEST);
    }

    // 启动相机拍摄照片
    private void headImageFromCameraCap() {
        if(null==mActivity) return;
        //判断相机是否可用
        PackageManager pm = mActivity.getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD
                || Camera.getNumberOfCameras() > 0;
        //调用系统相机拍摄
        if(hasACamera){
            AndroidNFileUtils.startActionCapture(mActivity,mTempFile,INTENT_CODE_CAMERA_REQUEST);
            //使用自定义相机拍摄
        }else{
            Intent intent=new Intent(mActivity,MediaPictruePhotoActivity.class);
            intent.putExtra("output",mOutFilePath.getAbsolutePath());
            intent.putExtra("output-max-width",1000);
            mActivity.startActivityForResult(intent,Constant.REQUEST_TAKE_PHOTO);
        }
    }



    /**
     * 在宿主Activity中对应方法调用
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                start();
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
        if(resultCode== Activity.RESULT_CANCELED){
            return;
        }
        if(null==mActivity) mActivity=null;
        outPathDir= Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator+ "VideoLive"+ File.separator+ "Photo"+File.separator;
        File file=new File(outPathDir);
        if(!file.exists()){
            file.mkdirs();
        }
        mOutFilePath = new File(outPathDir + mOutFileName);
        //删除已存在的缓存文件
        mTempFile = new File(outPathDir + IMAGE_DRR_PATH_TEMP);
        try {
            //裁剪返回
            if (resultCode == Activity.RESULT_OK && data != null && (requestCode == Constant.REQUEST_CLIP_IMAGE || requestCode == Constant.REQUEST_TAKE_PHOTO)) {
                String path = ClipImageActivity.ClipOptions.createFromBundle(data).getOutputPath();
                if (path != null) {
                    File imageFile = new File(path);
                    if(imageFile.exists()&&imageFile.isFile()){
                        if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onOutFile(imageFile);
                    }else{
                        if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onError(-1,"本地相片操作失败");
                    }
                }else{
                    if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onError(-1,"操作错误");
                }
                //本地相册选取的图片,转换为Path路径后再交给裁剪界面处理
            }else if(requestCode== INTENT_CODE_GALLERY_REQUEST){
                if(null!=data){
                    Uri originalUri = data.getData();
                    try {
                        Bitmap bitmap = getThumbnail(mActivity, originalUri, 2000);
                        if(null!=bitmap&&bitmap.getWidth()>0){
                            String filePath = FileUtils.saveBitmap(bitmap, outPathDir + IMAGE_DRR_PATH_TEMP);
                            mTempFile=new File(filePath);
                            clippingPictures();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onError(-1,"操作错误");
                    }
                }
                //系统照相机拍照完成回调
            }else if(requestCode==INTENT_CODE_CAMERA_REQUEST){
                clippingPictures();
            }else if(requestCode==0xa1){
                clippingPictures();
                //新版裁剪返回
            }else if(requestCode==Constant.SELECT_CROP_IMAGE_REQUST&&resultCode==Constant.SELECT_CROP_IMAGE_RESULT){
                if(null!=data.getStringExtra("outPath")){
                    if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onOutFile(new File(data.getStringExtra("outPath")));
                }else{
                    if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onError(-1,"裁剪失败");
                }
            }
        }catch (Exception e){
            if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onError(-1,"操作错误"+e.getMessage());
        }
    }

    public static Bitmap getThumbnail(Context context, Uri uri, int size) throws FileNotFoundException, IOException{
        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    /**
     * 系统相机拍摄返回
     */
    public void clippingPictures(){
        if(null!=mTempFile&&mTempFile.exists()&&null!=mOutFilePath){
            if(0==cropMode){
                startClipActivity(mTempFile.getAbsolutePath(),mOutFilePath.getAbsolutePath());
            }else if (1==cropMode){
                startNewClipActivity(mTempFile.getAbsolutePath(),mOutFilePath.getAbsolutePath());
            }
        }
    }

    /**
     * 去裁剪
     * @param inputFilePath
     * @param outputFilePath
     */
    private void startClipActivity(String inputFilePath, String outputFilePath) {
        if(null==mActivity) return;
        if(isSkipClip){
            if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onOutFile(new File(inputFilePath));
            return;
        }
        Intent intent = new Intent(mActivity, ClipImageActivity.class);
        intent.putExtra("aspectX", widthScale);//音频直播的封面，给长方形的
        intent.putExtra("aspectY", heightScale);
        intent.putExtra("maxWidth", maxWidth);
        intent.putExtra("tip", "");
        intent.putExtra("inputPath", inputFilePath);
        intent.putExtra("outputPath", outputFilePath);
        intent.putExtra("clipCircle",clipCircle);
        intent.putExtra("theme_style",mImageCutStyle);
        mActivity.startActivityForResult(intent, Constant.REQUEST_CLIP_IMAGE);
    }

    /**
     * 去裁剪
     * @param inputFilePath
     * @param outputFilePath
     */
    private void startNewClipActivity(String inputFilePath, String outputFilePath) {
        if(null==mActivity) return;
        if(isSkipClip){
            if(null!=mOnSelectedPhotoOutListener) mOnSelectedPhotoOutListener.onOutFile(new File(inputFilePath));
            return;
        }
        Intent intent=new Intent(mActivity,CropImageActivity.class);
        intent.putExtra("inputPath",inputFilePath);
        intent.putExtra("outputPath",outputFilePath);
        intent.putExtra("fixAspectRatio",true);
        mActivity.startActivityForResult(intent, Constant.SELECT_CROP_IMAGE_REQUST);
    }

    /**
     * 对应方法调用
     */
    public void onDestroy(){
        isSkipClip=false;
        cropMode=0;
        mActivity=null; mOnSelectedPhotoOutListener=null;mInstance=null;
    }
}