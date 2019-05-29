package com.yc.liaolive.util;

import android.database.Cursor;
import android.provider.MediaStore;

import com.github.promeg.pinyinhelper.Pinyin;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.MusicInfo;
import java.util.ArrayList;

/**
 * 音频查找工具类
 */
public class AudioUtils {

    /**
     * 获取SD卡所有音频文件
     * @return
     */
    public static ArrayList<MusicInfo> getAllSongs() {

        ArrayList<MusicInfo> songs = null;

        Cursor cursor = VideoApplication.getInstance().getContentResolver().query(
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

        songs = new ArrayList<>();

        if (cursor!=null&&cursor.moveToFirst()) {

            MusicInfo song = null;

            do {
                song = new MusicInfo();
                // 文件名
                song.setFileName(cursor.getString(1));

                String title = cursor.getString(2);
                // 歌曲名
                song.setTitle(title);
                song.setPinyin(Pinyin.toPinyin(title.charAt(0)).substring(0, 1).toUpperCase());
                // 时长
                song.setDuration(cursor.getInt(3));
                // 歌手名
                song.setSinger(cursor.getString(4));
                // 专辑名
                song.setAlbum(cursor.getString(5));
                // 年代
                if (cursor.getString(6) != null) {

                    song.setYear(cursor.getString(6));
                } else {
                    song.setYear("未知");
                }
                // 歌曲格式
                if ("audio/mpeg".equals(cursor.getString(7).trim())) {

                    song.setType("mp3");

                } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {

                    song.setType("wma");
                }
                // 文件大小
                if (cursor.getString(8) != null) {
                    float size = cursor.getInt(8) / 1024f / 1024f;
                    if(size==0.0){
                        song.setSize("未知");
                    }else{
                        song.setSize((size + "").substring(0, 4) + "M");
                    }

                } else {
                    song.setSize("未知");
                }
                // 文件路径
                if (cursor.getString(9) != null) {

                    song.setFileUrl(cursor.getString(9));
                }
                songs.add(song);

            } while (cursor.moveToNext());

            cursor.close();

        }
        return songs;
    }

}
