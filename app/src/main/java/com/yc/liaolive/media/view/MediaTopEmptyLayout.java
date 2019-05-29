package com.yc.liaolive.media.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/11/21
 * 多媒体预览视频礼物赠送排行榜
 */

public class MediaTopEmptyLayout extends FrameLayout {

    public MediaTopEmptyLayout(Context context) {
        super(context);
        init(context,null);
    }

    public MediaTopEmptyLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_media_top_empty_layout,this);
    }
}
