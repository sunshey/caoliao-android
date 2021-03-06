package com.yc.liaolive.util.attach;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.yc.liaolive.util.ImageCache;


/**
 * TinyHung@outlook.com
 * 2017-06-24 16:04
 * 加载本地视频缩略图的工具类,小型的，存放于内存中的
 */

public class LoadLocalMusicShortImgTask extends AsyncTask<String,Void,Bitmap> {

    private final ImageView imageView;//控件
    private final int emptyImage;//加载失败占位图
    private final String mUrl;



    public LoadLocalMusicShortImgTask(ImageView imageView, int emptyImage, String url){
        this.imageView=imageView;
        this.emptyImage=emptyImage;
        this.mUrl=url;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {

        return createAlbumArt(mUrl);
    }


    public Bitmap createAlbumArt(final String filePath) {
        Bitmap bitmap = null;
        //能够获取多媒体文件元数据的类
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath); //设置数据源
            byte[] embedPic = retriever.getEmbeddedPicture(); //得到字节型数据
            if(null!=embedPic&&embedPic.length>0){
                bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic.length); //转换为图片
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return bitmap;
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
            imageView.setImageResource(emptyImage);
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
