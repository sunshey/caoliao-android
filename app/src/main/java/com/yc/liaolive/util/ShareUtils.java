package com.yc.liaolive.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.ShareInfo;
import com.yc.liaolive.interfaces.OnShareFinlishListener;

/**
 * TinyHung@outlook.com
 * 2017/5/24 14:47
 * 友盟分享工具类
 */
public class ShareUtils {

    public static final String TAG = "ShareUtils";

    /**
     * 通用分享,包括分享视频、用户主页、软件等
     * @param activity
     * @param shareInfo 分享包装的对象
     * @param platform 分享的Type
     * @param shareFinlishListener 监听器
     */
    public static void baseShare( Activity activity,ShareInfo shareInfo, SHARE_MEDIA platform, final OnShareFinlishListener shareFinlishListener) {
        UMWeb web = new UMWeb(shareInfo.getUrl());//连接地址
        web.setTitle(shareInfo.getTitle());//标题
        web.setDescription(shareInfo.getDesp());//描述
        if (TextUtils.isEmpty(shareInfo.getImageLogo())) {
            web.setThumb(new UMImage(activity, R.drawable.ic_launcher));  //本地缩略图
        } else {
            web.setThumb(new UMImage(activity, shareInfo.getImageLogo()));  //网络缩略图
        }
        new ShareAction(activity)
                .setPlatform(platform)
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                        if(null!=shareFinlishListener){
                            shareFinlishListener.onShareStart(share_media);
                        }
                    }

                    @Override
                    public void onResult(final SHARE_MEDIA share_media) {
                        if(null!=shareFinlishListener){
                            shareFinlishListener.onShareResult(share_media);
                        }
                    }

                    @Override
                    public void onError(final SHARE_MEDIA share_media, Throwable throwable) {
                        if(null!=shareFinlishListener){
                            shareFinlishListener.onShareError(share_media, throwable);
                        }
                    }

                    @Override
                    public void onCancel(final SHARE_MEDIA share_media) {
                        if(null!=shareFinlishListener){
                            shareFinlishListener.onShareCancel(share_media);
                        }
                    }
                })
                .share();
    }

    /**
     * 视频上传文成后分享，提取本地视频文件用作封面
     * @param activity
     * @param shareFinlishListener 监听器
     */
    public static void share(Activity activity, ShareInfo data,SHARE_MEDIA platform,final OnShareFinlishListener shareFinlishListener) {
        if(null==data) return;
        new ShareTask(activity,data,platform,shareFinlishListener).execute();
    }

    /**
     * 分享任务，先获取视频封面
     */
    private static class ShareTask extends AsyncTask<String,Void,Bitmap> {

        private final Activity activity;
        private final ShareInfo data;
        private final SHARE_MEDIA platform;
        private final OnShareFinlishListener shareFinlishListener;

        public ShareTask(Activity activity, ShareInfo data, SHARE_MEDIA platform, OnShareFinlishListener shareFinlishListener) {
            this.activity=activity;
            this.data=data;
            this.platform=platform;
            this.shareFinlishListener=shareFinlishListener;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            Bitmap videoThumbnail = null;
            if(!TextUtils.isEmpty(data.getVideoPath())){
                try {
                    videoThumbnail= getVideoThumbnail(data.getVideoPath(),120,120, MediaStore.Images.Thumbnails.MINI_KIND);
                }catch (Exception e){

                }
                return videoThumbnail;
            }
            return videoThumbnail;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            UMWeb web = new UMWeb(data.getUrl());//连接地址
            web.setTitle(data.getTitle());//标题
            web.setDescription(data.getDesp());//描述
            if(null!=bitmap){
                web.setThumb(new UMImage(activity,bitmap));
            }else{
                web.setThumb(new UMImage(activity, R.drawable.ic_launcher));
            }
            new ShareAction(activity)
                    .setPlatform(platform)
                    .withMedia(web)
                    .setCallback(new UMShareListener() {
                        @Override
                        public void onStart(SHARE_MEDIA share_media) {
                            if(null!=shareFinlishListener){
                                shareFinlishListener.onShareStart(share_media);
                            }
                        }

                        @Override
                        public void onResult(final SHARE_MEDIA share_media) {
                            if(null!=shareFinlishListener){
                                shareFinlishListener.onShareResult(share_media);
                            }
                        }

                        @Override
                        public void onError(final SHARE_MEDIA share_media, Throwable throwable) {
                            if(null!=shareFinlishListener){
                                shareFinlishListener.onShareError(share_media, throwable);
                            }
                        }

                        @Override
                        public void onCancel(final SHARE_MEDIA share_media) {
                            if(null!=shareFinlishListener){
                                shareFinlishListener.onShareCancel(share_media);
                            }
                        }
                    })
                    .share();
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
}
