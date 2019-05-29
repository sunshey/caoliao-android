package com.yc.liaolive.util;

import com.yc.liaolive.bean.PrivateMedia;
import java.util.List;
import java.util.ArrayList;

/**
 * TinyHung@Outlook.com
 * 2018/9/25
 */

public class VideoDataUtils {

    private static VideoDataUtils mInstance;
    private List<PrivateMedia> mData;
    private int mPosition;//在播放界面浏览到第几页了
    private int index;//页面所属的位置（身份）  -1:用户多媒体中心 -2：会话界面
    private int fileType;//首页区分视频、照片
    private String hostUrl;//界面所属的HOST
    private String source;//主页绑定的类型
    private int page;//加载到了第几页


    public static VideoDataUtils getInstance(){
        synchronized (VideoDataUtils.class){
            if(null==mInstance){
                mInstance=new VideoDataUtils();
            }
        }
        return mInstance;
    }

    public void setVideoData(List<PrivateMedia> data, int position){
        this.mData=data;
        this.mPosition=position;
    }

    public List<PrivateMedia> getVideoData(){
        return mData;
    }

    public void addData(List<PrivateMedia> data) {
        if(null==mData) mData=new ArrayList<>();
        mData.addAll(data);
    }

    public void setPosition(int position) {
        this.mPosition=position;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPosition() {
        return mPosition;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public void setFileType(int fileType) {
        this.fileType=fileType;
    }

    public int getFileType() {
        return fileType;
    }
}
