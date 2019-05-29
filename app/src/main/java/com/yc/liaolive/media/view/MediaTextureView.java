package com.yc.liaolive.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2018/12/29
 * 解决渲染黑屏的问题，动态调整大小
 */

public class MediaTextureView extends TextureView {

    protected static final String TAG = "MediaTextureView";
    public int mVideoWidth = 1;
    public int mVideoHeight = 1;

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public MediaTextureView(Context context) {
        super(context);
        mVideoWidth = 1;
        mVideoHeight = 1;
    }

    public MediaTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mVideoWidth = 1;
        mVideoHeight = 1;
    }

    public void setVideoSize(int currentVideoWidth, int currentVideoHeight) {
        if (this.mVideoWidth != currentVideoWidth || this.mVideoHeight != currentVideoHeight) {
            this.mVideoWidth = currentVideoWidth;
            this.mVideoHeight = currentVideoHeight;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int videoWidth = mVideoWidth;
        int videoHeight = mVideoHeight;
        setMeasuredDimension(videoWidth, videoHeight);
    }
}
