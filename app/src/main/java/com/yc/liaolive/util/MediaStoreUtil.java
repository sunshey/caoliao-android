package com.yc.liaolive.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.music.player.lib.bean.BaseMediaInfo;
import com.yc.liaolive.bean.WeiXinVideo;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/5.
 */

public class MediaStoreUtil {

    private static MediaStoreUtil mInstance;

    public static synchronized MediaStoreUtil getInstance() {
        synchronized (MediaStoreUtil.class) {
            if (null == mInstance) {
                mInstance = new MediaStoreUtil();
            }
        }
        return mInstance;
    }

    /**
     * 查询音频文件名称
     * @param context
     * @return
     */
    public List<String> getAudioNames(Context context) {
        List<String> list = new ArrayList<String>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR, MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA }, null, new String[] {}, null);
        while (cursor.moveToNext()) {
            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            list.add(fileName);
        }
        return list;
    }

    /**
     * @param context
     * @return
     */
    public List<String> getImageNames(Context context) {
        List<String> list = new ArrayList<String>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
                        MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATA }, null,
                new String[] {}, null);
        while (cursor.moveToNext()) {
            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            list.add(filePath + "/" + fileName);
        }
        return list;
    }

    /**
     * 查询图片文件
     * @param context
     * @return
     */
    public List<File> getImages(Context context) {
        List<File> list = new ArrayList<File>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
                        MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATA }, null,
                new String[] {}, null);
        while (cursor.moveToNext()) {
            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //Logger.i(TAG, "fileName==" + fileName);
            File file = new File(filePath);
            list.add(file);
        }
        return list;
    }

    /**
     * 获取所有的缩列图
     * @param context
     * @return
     */
    public Bitmap[] getBitmaps(Context context) {
        Bitmap[] bitmaps;
        String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                MediaStore.Images.Media._ID);
        int count = cursor.getCount();
        int image_column_index = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        bitmaps = new Bitmap[count];
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int id = cursor.getInt(image_column_index);
            bitmaps[i] = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), id,
                    MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }
        return bitmaps;
    }

    /**
     * 查询设备所有照片
     * @param context
     * @return
     */
    public List<String> queryAllImageToName(Context context) {
        List<String> list = new ArrayList<String>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Thumbnails.KIND,
                        MediaStore.Images.Thumbnails.IMAGE_ID }, null, new String[] {}, null);
        while (cursor.moveToNext()) {
            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            list.add(fileName);
        }
        return list;
    }

    /**
     * 检索SD卡所有指定后缀视频文件
     * @param context
     * @param fromat 正则格式表达
     * @return
     */
    public List<WeiXinVideo> getVideoInfo(Context context, String... fromat){
        String[] thumbColumns = new String[]{
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID
        };
        String[] mediaColumns = new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION
        };
        //首先检索SDcard上所有的video
        try {
            Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);
            ArrayList<WeiXinVideo> videoList = new ArrayList<WeiXinVideo>();
            if(null!=cursor){
                if(cursor.moveToFirst()){
                    do{
                        String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        //只需要受支持的视频格式的文件
                        if(isSupport(filePath,fromat)){
                            File file =new File(filePath);
                            if(file.exists()&&file.isFile()){
                                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                                String durtion=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                                //防止.mp4的临时文件
                                if(null!=durtion&&0!=Integer.parseInt(durtion)){
                                    WeiXinVideo info = new WeiXinVideo();
                                    //获取当前Video对应的Id，然后根据该ID获取其Thumb
                                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                                    String selection = MediaStore.Video.Thumbnails.VIDEO_ID +"=?";
                                    String[] selectionArgs = new String[]{id+""
                                    };
                                    Cursor thumbCursor = context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, selection, selectionArgs, null);
                                    if(null!=thumbCursor){
                                        if(thumbCursor.moveToFirst()){
                                            String thumbPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                                            info.setVidepThbunPath(thumbPath);
                                        }
                                    }
                                    info.setFileName(title);
                                    info.setVideoPath(filePath);
                                    info.setVideoDortion(Integer.parseInt(TextUtils.isEmpty(durtion)?"0":durtion));
                                    info.setVideoCreazeTime(file.lastModified());
                                    videoList.add(info);
                                }
                            }
                        }
                    }while(cursor.moveToNext());
                }
            }

            if(null!=videoList&&videoList.size()>0){
                //对视频时间进行倒序排序
                Collections.sort(videoList, new Comparator<WeiXinVideo>() {
                    @Override
                    public int compare(WeiXinVideo o1, WeiXinVideo o2) {
                        return o2.getVideoCreazeTime().compareTo(o1.getVideoCreazeTime());
                    }
                });
            }
            return videoList;
        }catch (Exception e){

        }
        return null;
    }

    /**
     * 返回受支持的视频格式
     * @param filePath
     * @param fromat
     * @return
     */
    public boolean isSupport(String filePath, String... fromat) {
        if(TextUtils.isEmpty(filePath)) return false;
        String substring = filePath.substring(filePath.lastIndexOf(".")+1);
        if(null!=fromat&&fromat.length>0){
            for (String s : fromat) {
                if(TextUtils.equals(substring,s)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取SD卡所有音频文件
     * @return
     */
    public ArrayList<BaseMediaInfo> queryLocationMusics(Context context) {
        ArrayList<BaseMediaInfo> mediaInfos=null;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA },
                MediaStore.Audio.Media.MIME_TYPE + "=? or "
                        + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);
        if (null!=cursor&&cursor.moveToFirst()) {
            mediaInfos = new ArrayList<>();
            do {
                if(!TextUtils.isEmpty(cursor.getString(9))){
                    BaseMediaInfo mediaInfo = new BaseMediaInfo();
                    if(!TextUtils.isEmpty(cursor.getString(0))){
                        mediaInfo.setId(Long.parseLong(cursor.getString(0)));
                    }else{
                        mediaInfo.setId(System.currentTimeMillis());
                    }
                    // 文件名
                    //mediaInfo.setVideo_desp(cursor.getString(1));
                    // 歌曲名
                    if(!TextUtils.isEmpty(cursor.getString(2))){
                        mediaInfo.setVideo_desp(cursor.getString(2));
                    }
//                song.setPinyin(Pinyin.toPinyin(title.charAt(0)).substring(0, 1).toUpperCase());
                    // 时长
                    if(!TextUtils.isEmpty(cursor.getString(3))){
                        mediaInfo.setVideo_durtion(cursor.getInt(3));
                    }
                    // 歌手名
                    if(!TextUtils.isEmpty(cursor.getString(4))){
                        mediaInfo.setNickname(cursor.getString(4));
                    }
                    // 专辑名
                    if(!TextUtils.isEmpty(cursor.getString(5))){
                        mediaInfo.setMediaAlbum(cursor.getString(5));
                    }
                    // 年代 cursor.getString(6)
                    if(!TextUtils.isEmpty(cursor.getString(7))){
                        // 歌曲格式
                        if ("audio/mpeg".equals(cursor.getString(7).trim())) {
                            mediaInfo.setMediaType("mp3");
                        } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
                            mediaInfo.setMediaType("wma");
                        }
                    }
                    //文件大小 cursor.getString(8)
                    // 文件路径
                    //  /storage/emulated/0/Music/齐晨-咱们结婚吧.mp3
                    mediaInfo.setFile_path(cursor.getString(9));
                    mediaInfos.add(mediaInfo);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return mediaInfos;
    }
}