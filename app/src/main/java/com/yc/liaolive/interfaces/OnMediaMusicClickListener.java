package com.yc.liaolive.interfaces;

import com.yc.liaolive.bean.MediaMusicCategoryList;
import com.yc.liaolive.bean.MusicInfo;

/**
 * TinyHung@Outlook.com
 * 2017/11/9.
 * 音乐列表的监听器
 */

public interface OnMediaMusicClickListener {
    void onItemClick(int poistion);
    void onLikeClick(MediaMusicCategoryList.DataBean musicID);
    void onDetailsClick(String musicID);
    void onSubmitMusic(MediaMusicCategoryList.DataBean musicPath);
    void onSubmitLocationMusic(MusicInfo musicPath);
}
