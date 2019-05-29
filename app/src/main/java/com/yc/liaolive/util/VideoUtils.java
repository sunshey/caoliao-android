package com.yc.liaolive.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.FileInfos;
import com.yc.liaolive.bean.LoactionVideoInfo;
import com.yc.liaolive.bean.LocationVideoBean;
import com.yc.liaolive.bean.Video;
import com.yc.liaolive.bean.VideoFolder;
import com.yc.liaolive.bean.VideoParame;
import com.yc.liaolive.bean.WeiXinVideo;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.ui.dialog.RedAnimationDialog;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * TinyHung@outlook.com
 * 2017/4/15 11:29
 */

public class VideoUtils {

    private static final String TAG = "VideoUtils";
    private static List<LoactionVideoInfo> sLoactionVideoInfos=new ArrayList<>();


    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param kind 参照MediaStore.Images(Video).Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if(bitmap!= null){
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }



    public static String getVideoThbun(String url,String outPath){
        if(TextUtils.isEmpty(url)) return null;
        Bitmap bitmap = null;
        String thbumOutPutPath=null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(url, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        if(null!=bitmap){

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(new File(outPath));
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.v("bitmap", "bitmap="+bitmap);
        return outPath;
    }

    /**
     * 获取指定路径中的视频文件
     * @param file 指定的文件
     */
    public static List<LoactionVideoInfo> getVideoListToFile(File file) {// 获得视频文件

        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();
                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);//获取文件后缀名
                    if (name.equalsIgnoreCase(".mp4")  //忽略大小写
                            || name.equalsIgnoreCase(".3gp")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".ts")
                            || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".mov")
                            || name.equalsIgnoreCase(".m4v")
                            || name.equalsIgnoreCase(".avi")
                            || name.equalsIgnoreCase(".m3u8")
                            || name.equalsIgnoreCase(".3gpp")
                            || name.equalsIgnoreCase(".3gpp2")
                            || name.equalsIgnoreCase(".mkv")
                            || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".divx")
                            || name.equalsIgnoreCase(".f4v")
                            || name.equalsIgnoreCase(".rm")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".ram")
                            || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".v8")
                            || name.equalsIgnoreCase(".swf")
                            || name.equalsIgnoreCase(".m2v")
                            || name.equalsIgnoreCase(".asx")
                            || name.equalsIgnoreCase(".ra")
                            || name.equalsIgnoreCase(".ndivx")
                            || name.equalsIgnoreCase(".xvid")) {
                        LoactionVideoInfo videoInfo = new LoactionVideoInfo();
                        videoInfo.setName(file.getName());//文件名
                        videoInfo.setPath(file.getAbsolutePath());//文件路径
                        Bitmap videoThumbnail = getVideoThumbnail(file.getAbsolutePath(), 480, 480, MediaStore.Images.Thumbnails.MINI_KIND);
                        videoInfo.setThumbnail(videoThumbnail);
                        videoInfo.setFileSize(file.length());
                        videoInfo.setLastModified(file.lastModified());
//                        long duration=getVideoDuration(file.getAbsolutePath());
//                        videoInfo.setDuration(duration);
                        if(null!=sLoactionVideoInfos&&sLoactionVideoInfos.size()<=8){
                            sLoactionVideoInfos.add(videoInfo);
                        }
                        return true;
                    }
                } else if (file.isDirectory()) {
                    getVideoListToFile(file);
                }
                return false;
            }
        });
        return sLoactionVideoInfos;
    }

    /**
     * 获取视频文件的时长
     * @param url
     * @return
     */
    private static long getVideoDuration(String url) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mediaPlayer.getDuration();
    }

    /**
     * 获取视频文件的时长，宽，高等信息
     * @param mUri
     * @return
     */
    private static VideoParame getVideoParame(String mUri) {
        VideoParame videoParame = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (mUri != null) {
                HashMap<String, String> headers=null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            }
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
            videoParame=new VideoParame();
            videoParame.setDurtaion(Long.parseLong(duration));
            videoParame.setXpWidth(Integer.parseInt(width));
            videoParame.setXpHeight(Integer.parseInt(height));
            return videoParame;
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return videoParame;
    }
    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
            Log.d(TAG, "deleteFile: 删除完成"+sPath);
        }
        return flag;
    }


    /**
     * 获取本地视频文件
     * @param context
     * @return
     */
    public static List<LocationVideoBean> getVideoList(Context context) {
        List<LocationVideoBean> list = null;
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,null, null);
            if (cursor != null) {
                list = new ArrayList<LocationVideoBean>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));  //视频文件的标题内容
                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));  //
                    long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    long lastModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
                    Bitmap videoThumbnail = getVideoThumbnail(path, 512, 384, MediaStore.Images.Thumbnails.MINI_KIND);
                    LocationVideoBean videoinfo = new LocationVideoBean(id, title, album, artist, displayName, mimeType, path, size, duration, lastModified, videoThumbnail, false);
                    list.add(videoinfo);
                }
                cursor.close();
            }
        }
        return list;
    }


    public static WeiXinVideo getVideoDataForPath(String filePath) {
        File file1 = new File(filePath);
        if(!file1.exists()||!file1.isFile()) return null;
        WeiXinVideo weiXinVideo=null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);

            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            weiXinVideo=new WeiXinVideo();
            weiXinVideo.setVideoDortion(Integer.parseInt(duration));
            weiXinVideo.setIsSelector(true);
            weiXinVideo.setVideoPath(filePath);
            weiXinVideo.setFileName(file1.getName());
            File file = new File(filePath);
            weiXinVideo.setVideoCreazeTime(file.lastModified());
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return weiXinVideo;
    }

    /**
     * 获取视频的基本信息
     * @param videoPath
     * @return
     */

    public static FileInfos getVideoInfo(String videoPath) {
        File file = new File(videoPath);
        if(!TextUtils.isEmpty(videoPath)&&file.isFile()){
            FileInfos fileInfos=null;
            try{
                MediaMetadataRetriever retr = new MediaMetadataRetriever();
                retr.setDataSource(videoPath);
                if(null!=retr){
                    fileInfos=new FileInfos();
                    String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
                    String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
                    String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
//                    String bitrate = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE); // 视频比特率
                    String durtion = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 视频时长

                    fileInfos.setVideoDurtion(Long.parseLong(durtion));
                    fileInfos.setVideoRotation(Integer.parseInt(rotation));
                    fileInfos.setFileWidth(Integer.parseInt(width));
                    fileInfos.setFileHeight(Integer.parseInt(height));
                    fileInfos.setFileSize(file.length()/1024);
                    retr.release();
                }

            }catch (Exception e){
                return fileInfos;
            }
            return fileInfos;
        }else{
            return null;
        }
    }

    private static FileInfos getImageInfo(String videoPath) {
        try {
            File file=new File(videoPath);
            if(!TextUtils.isEmpty(videoPath)&&file.isFile()){
                FileInfos fileInfos=new FileInfos();
                fileInfos.setFileSize(file.length()/1024);
                return fileInfos;
            }
        }catch (RuntimeException e){

        }
        return null;
    }

    /**
     * 获取文件基本信息
     * @param videoPath
     * @return
     */

    public static FileInfos getVideoInfo(String videoPath,int fileType) {
        if(1==fileType){
            return getVideoInfo(videoPath);
        }
        return getImageInfo(videoPath);
    }

    public static int getVideoHeight(String videoPath) {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(videoPath);
        String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度

        return Integer.parseInt(height);
    }


    public static List<VideoFolder> getVideoFolderList() throws Exception{
        final String cameraFolder = ApplicationManager.getInstance().getOutPutPath(0);//项目的工作空间
        Cursor cursor =VideoApplication.getInstance().getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{ MediaStore.Video.Media._ID, MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATA }, null, null, MediaStore.MediaColumns.DATE_MODIFIED + " DESC");
        HashMap<String, VideoFolder> mDataResult = new HashMap<String, VideoFolder>();
        if (cursor != null) {
            int idxId = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
            int idxModified = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);
            int indxSize = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
            int idxData = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                long _id = cursor.getLong(idxId);
                long modified = cursor.getLong(idxModified);
                int duration = cursor.getInt(indxSize);
                String path = cursor.getString(idxData);
                if (StringUtils.isNotEmpty(path)) {
                    File file = new File(path);
                    if (file != null && file.canRead()) {
                        String folder = file.getParent();
                        String folder2 = file.getParent() + File.separator;//原来的对比有问题,需要加上\才能匹配对比
                        String folderKey = folder.toLowerCase(Locale.CHINESE);
                        // 过滤掉临时文件夹
                        if (StringUtils.isNotEmpty(folder) && (!folder.startsWith(cameraFolder) || StringUtils.equals(cameraFolder, folder))) {
                            // 过滤掉临时文件夹,过滤草稿箱视频
                            if (StringUtils.isNotEmpty(folder) && (!folder.startsWith(cameraFolder) && !StringUtils.equals(cameraFolder, folder2))) {
                                boolean isMoreThan3s = duration >= 3 * 1000;
                                VideoFolder iFolder;
                                if (!mDataResult.containsKey(folderKey)) {
                                    iFolder = new VideoFolder();
                                    iFolder._id = _id;
                                    iFolder.path = folder;
                                    iFolder.name = FileUtils.getFileName(folder);
                                    iFolder.url = path;

                                    if (isMoreThan3s) {
                                        iFolder.video = getVideo(_id, path, modified, duration);
                                    }

                                    mDataResult.put(folderKey, iFolder);
                                } else {
                                    iFolder = mDataResult.get(folderKey);
                                    if (iFolder.video == null && isMoreThan3s) {
                                        iFolder.video = getVideo(_id, path, modified, duration);
                                    }
                                }
                                if (iFolder != null && isMoreThan3s) {
                                    iFolder.count++;
                                }
                            }
                        }
                    }
                }
            }
            cursor.close();
        }
        HashSet<VideoFolder> resultSet = new HashSet<VideoFolder>(mDataResult.values());
        List<VideoFolder> result = new ArrayList<VideoFolder>(resultSet);

        Iterator<VideoFolder> it = result.iterator();
        while (it.hasNext()) {
            VideoFolder folder = it.next();
            if (folder.count == 0) {
                it.remove();
            }
        }

        // 排序
        Collections.sort(result, new Comparator<VideoFolder>() {
            @Override
            public int compare(VideoFolder lhs, VideoFolder rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        });

        //系统数据库可能没有微信中的视频，若没有且/MicroMsg/文件夹已存在，强行加入列表
        int weixinCunt=0;
        if(null!=result&&result.size()>0){
            boolean microMsgFolderExist=false;
            for (VideoFolder videoFolder : result) {
                if(TextUtils.equals("MicroMsg",videoFolder.name)){

                    microMsgFolderExist=true;
                    videoFolder.name="微信";
                    videoFolder._id=999999999;
                }else if(TextUtils.equals("WeiXin",videoFolder.name)){
                    weixinCunt=videoFolder.count;
                }
            }
            //列表中不存在微信文件夹,强行加入列表
            if(!microMsgFolderExist){
                File filePath = new File(NetContants.WEICHAT_VIDEO_PATH);
                if(filePath.exists()&&filePath.isDirectory()){
                    VideoFolder videoFolder = new VideoFolder();
                    videoFolder.name="微信";
                    videoFolder.count=weixinCunt;
                    videoFolder.path=NetContants.WEICHAT_VIDEO_PATH;
                    videoFolder._id=999999999;
                    result.add(0,videoFolder);
                }
            }
        }

        if(null!=result){
            VideoFolder videoFolder = new VideoFolder();
            videoFolder.name="所有视频";
            videoFolder.path= Environment.getExternalStorageDirectory().getAbsolutePath();
            videoFolder._id=888888888;
            videoFolder.count=0;
            result.add(0,videoFolder);
        }

        return result;
    }


    public static Video getVideo(long id, String path, long modified, long duration) {
        MediaMetadataRetriever metadata = new MediaMetadataRetriever();
        int orientation = 0;
        if (DeviceUtils.hasJellyBeanMr1()) {
            try {
                metadata.setDataSource(path);
                orientation = ConvertToUtils.toInt(metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION), 0);
            } catch (Exception e) {

            }
        }

        Video video = new Video(path, modified, duration);
        video._id = id;
        video.orientation = orientation;
        return video;
    }
}
