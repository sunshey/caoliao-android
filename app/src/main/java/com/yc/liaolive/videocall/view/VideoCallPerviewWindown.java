package com.yc.liaolive.videocall.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.qiniu.droid.rtc.QNSurfaceView;
import com.yc.liaolive.R;
import com.yc.liaolive.live.view.SurfaceStateWindown;
import com.yc.liaolive.util.Logger;

/**
 * Created by hty_Yuye@Outlook.com
 * 2018/12/24
 * 视频通话预览窗口
 */

public class VideoCallPerviewWindown extends FrameLayout{

    private static final String TAG = "LiveLocatSurfaceView";
    private QNSurfaceView mSurfaceView;
    private FrameLayout mWindownState;//相机、音频开关状态

    public VideoCallPerviewWindown(@NonNull Context context) {
        this(context,null);
    }

    public VideoCallPerviewWindown(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_live_previerw_surface,this);
        mSurfaceView = (QNSurfaceView) findViewById(R.id.view_surface_view);
        mWindownState = (FrameLayout) findViewById(R.id.view_windown_state);
    }

    /**
     * 绑定渲染状态窗口
     * @param stateWindown
     */
    public void setWindownStateView(SurfaceStateWindown stateWindown){
        if(null!=mWindownState&&null!=stateWindown){
            mWindownState.removeAllViews();
            if(null!=stateWindown.getParent()) ((ViewGroup) stateWindown.getParent()).removeAllViews();
            mWindownState.addView(stateWindown);
        }
    }

    /**
     * 返回窗口实例
     * @return
     */
    public QNSurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    public void onDestroy() {
        if(null!=mWindownState) mWindownState.removeAllViews();
    }
}
