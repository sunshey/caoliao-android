package com.yc.liaolive.live.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2018/12/29
 * 视频通话大小窗口切换，变化
 */

public class SurfaceStateWindown extends FrameLayout {

    private static final String TAG = "SurfaceStateWindown";
    private View mCloseView;
    private String mUserID="";
    private TextView mCloseTextView;

    public SurfaceStateWindown(@NonNull Context context) {
        this(context,null);
    }

    public SurfaceStateWindown(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_live_call_surface_windown,this);
        mCloseView = findViewById(R.id.view_close_view);
        mCloseTextView = findViewById(R.id.view_close_text);
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
    }

    /**
     * 此控件的媒体流发生了变化
     * @param userId 对象ID
     * @param isAudioMuted 音频已禁用
     * @param isVideoMuted 视频已禁用
     * @param isLocation 是否本地触发
     */
    public void onRemoteMute(String userId, boolean isAudioMuted, boolean isVideoMuted,boolean isLocation){
        if(null!=mCloseView&&mUserID.equals(userId)){
            if(null!=mCloseTextView){
                String tips="";
                if(isAudioMuted&&isVideoMuted){
                    tips=isLocation?"已关闭照相机和声音":"对方已关闭照相机和声音";
                }else if(isAudioMuted){
                    tips=isLocation?"已关闭声音":"对方已关闭声音";
                }else if(isVideoMuted){
                    tips=isLocation?"已关闭照相机":"对方已关闭照相机";
                }
                mCloseTextView.setText(tips);
            }
            mCloseView.setVisibility(isVideoMuted ? View.VISIBLE : View.GONE);
        }
    }
}
