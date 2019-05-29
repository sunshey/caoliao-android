package com.yc.liaolive.util.attach;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.ImageCache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * TinyHung@outlook.com
 * 2017-06-24 16:04
 * 加载本地视频缩略图的工具类
 */

public class LoadLocalBigImgTask extends AsyncTask<String,Void,Bitmap> {

    private final ImageView imageView;//控件
    private final int emptyImage;//加载失败占位图
    private final String mUrl;



    public LoadLocalBigImgTask(ImageView imageView, int emptyImage,String url){
        this.imageView=imageView;
        this.emptyImage=emptyImage;
        this.mUrl=url;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap videoThumbnail = null;
        try {
            videoThumbnail= getVideoThumbnail(mUrl,120,120, MediaStore.Images.Thumbnails.MINI_KIND);
        }catch (Exception e){

        }
//        saveBitmapLocation(videoThumbnail,mUrl);
        return videoThumbnail;
    }

    /**
     * 建个BITMAP缓存到本地
     * @param videoThumbnail
     * @param resourceUrl
     */
    private void saveBitmapLocation(Bitmap videoThumbnail, String resourceUrl) {

        if(TextUtils.isEmpty(resourceUrl)) return;
        if(null==videoThumbnail) return;
        File file = new File(Constant.IMAGE_PATH);
        if(!file.exists()&&!file.isDirectory()){
            file.mkdirs();
        }

        File filePath=new File(Constant.IMAGE_PATH, FileUtils.getFileName(resourceUrl));
        if(!filePath.exists()&&!filePath.isFile()){
            try {
                FileOutputStream fileOutputStream=new FileOutputStream(filePath);
                videoThumbnail.compress(Bitmap.CompressFormat.PNG,90,fileOutputStream);
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(null!=bitmap){
            ImageCache.getInstance().put(mUrl,bitmap);//缓存起来
            if(null!=imageView){
                imageView.setImageBitmap(bitmap);
            }
        }else{
            if(null!=imageView){
                imageView.setImageResource(emptyImage);
            }
        }
    }


    /**
     * 获取视频缩略图
     * @param path 文件路径
     * @param whidth 图片宽
     * @param height 图片高
     * @param kind 图片类型 略图图
     * @return
     */
    public static Bitmap getVideoThumbnail(String path, int whidth, int height, int kind) {
        if(TextUtils.isEmpty(path)) return null;
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(path, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, whidth, height, 2);
        return bitmap;
    }
}
