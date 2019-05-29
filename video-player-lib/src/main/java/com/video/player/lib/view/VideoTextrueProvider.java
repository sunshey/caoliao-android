package com.video.player.lib.view;

import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * TinyHung@Outlook.com
 * 2019/4/11
 * ViewRound Radius
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoTextrueProvider extends ViewOutlineProvider {

    private float mRadius;
    private Rect mRect;

    public VideoTextrueProvider(float radius){
        this.mRadius = radius;
    }

    public VideoTextrueProvider(float radius, Rect rect) {
        this.mRadius = radius;
        this.mRect = rect;
    }

    @Override
    public void getOutline(View view, Outline outline) {
//        Rect rect = new Rect();
//        view.getGlobalVisibleRect(rect);
//        int leftMargin = 0;
//        int topMargin = 0;
//        Rect selfRect = new Rect(leftMargin, topMargin, rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin);
//        if(mRect!=null){
//            selfRect = mRect;
//        }
//        outline.setRoundRect(selfRect, mRadius);
        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), mRadius);
        view.setClipToOutline(true);
    }
}