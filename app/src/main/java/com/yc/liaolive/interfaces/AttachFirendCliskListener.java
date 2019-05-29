package com.yc.liaolive.interfaces;

import android.view.View;
import com.yc.liaolive.bean.FansInfo;

/**
 * TinyHung@outlook.com
 * 2017/6/15 15:47
 * 针对于粉丝列表和关注列表的点击事件
 */
public interface AttachFirendCliskListener {
    //点击了用户头像
    void onUserHeadClick(String userID,View view);
    void onItemClick(int position,String userID,View view);
    void onUserStateClick(FansInfo userInfo);
}
