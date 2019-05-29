package com.yc.liaolive.model;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * TinyHung@outlook.com
 * 2017/5/24 8:58
 * 水平方向的ItemDecoration  适用于首页的视频合并-上传列表
 */
public class HomeHorzontalSpacesItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = HomeHorzontalSpacesItemDecoration.class.getSimpleName();
    private int space;

    public HomeHorzontalSpacesItemDecoration(int space) {
        this.space=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom=0;
        outRect.right=space;
        outRect.top=(space+space);
        if(parent.getChildLayoutPosition(view)==0){
            outRect.left=(space+space);
        }else{
            outRect.left=0;
        }
    }
}
