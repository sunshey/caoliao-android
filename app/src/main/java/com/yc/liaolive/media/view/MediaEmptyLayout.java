package com.yc.liaolive.media.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2019/1/25
 * 多媒体文件为空占位界面
 */

public class MediaEmptyLayout extends FrameLayout{

    public static final int MEDIA_TYPE_IMAGE=0;//相册
    public static final int MEDIA_TYPE_VIDEO=1;//视频

    public MediaEmptyLayout(@NonNull Context context) {
        this(context,null);
    }

    public MediaEmptyLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_media_empty_layout, this);
        findViewById(R.id.btn_submit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnFuctionListener) mOnFuctionListener.onSubmit();
            }
        });
    }

    public void setMode(int mode){
        if(mode==MEDIA_TYPE_IMAGE){
            setIcon(R.drawable.ic_media_empty_photo);
            setTitle("暂无照片");
            setContent("你还没有上传自己的照片");
        }else if(mode==MEDIA_TYPE_VIDEO){
            setIcon(R.drawable.ic_media_empty_video);
            setTitle("暂无视频");
            setContent("你还没有上传自己的小视频");
        }
    }

    public void setIcon(int resID){
        ((ImageView) findViewById(R.id.view_empty_icon)).setImageResource(resID);
    }

    public void setTitle(String title){
        ((TextView) findViewById(R.id.view_title_tips)).setText(title);
    }

    public void setContent(String content){
        ((TextView) findViewById(R.id.view_content_tips)).setText(content);
    }

    public void setSubmitTitle(String submitTitle){
        ((TextView) findViewById(R.id.btn_submit)).setText(submitTitle);
    }

    public interface OnFuctionListener{
        void onSubmit();
    }

    private OnFuctionListener mOnFuctionListener;

    public void setOnFuctionListener(OnFuctionListener onFuctionListener) {
        mOnFuctionListener = onFuctionListener;
    }
}
