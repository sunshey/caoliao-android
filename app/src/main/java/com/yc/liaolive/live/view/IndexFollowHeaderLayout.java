package com.yc.liaolive.live.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/8/18
 * 关注界面占位布局
 */

public class IndexFollowHeaderLayout extends FrameLayout {

    public IndexFollowHeaderLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public IndexFollowHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.index_follow_header_layout, this);
    }
}
