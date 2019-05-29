package com.yc.liaolive.util;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.WeiXinVideo;
import com.yc.liaolive.bean.ScanMessageEvent;
import com.yc.liaolive.manager.ApplicationManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangkai on 2017/5/18.
 */

public class ScanWeixin {

    private static final String TAG =ScanWeixin.class.getSimpleName() ;
    private static String WEIXIN_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tencent/MicroMsg";

    private boolean scanIng=true;
    private boolean event;
    private String mFilterFolderPath;
    private int mMinDurtion=3000;//单位 毫秒
    private int mMaxDurtion=0;//默认不限制

    public void setScanEvent(boolean scanEvent) {
        this.scanIng=scanEvent;
    }

    public void setEvent(boolean b) {
        this.event=b;
    }

    public void setFilterFolderPath(String filterFolderPath) {
        this.mFilterFolderPath=filterFolderPath;
    }

    public void setMinDurtion(int minDurtion) {
        this.mMinDurtion=minDurtion;
    }

    public void setMaxDurtion(int maxDurtion) {
        this.mMaxDurtion=maxDurtion;
    }

    public interface OnScanListener{
        void onScanProgress(String file);
    }

    private OnScanListener mOnScanListener;

    private List<WeiXinVideo> files = new ArrayList<>();
    private List<String> exts;
    private int max = -1;


    public void setMax(int number) {
        this.max = number;
    }

    public void setListener(OnScanListener onScanListener) {
        this.mOnScanListener=onScanListener;
    }

    public List<WeiXinVideo> scanFiles() {
        return scanFiles(null);
    }

    List<WeiXinVideo> videos=new ArrayList<>();


    public List<WeiXinVideo> scanFiles(String dir) {
        File file = new File(TextUtils.isEmpty(dir)? WEIXIN_PATH : dir);
        if (file.exists()) {
            if(null!=file.listFiles()&&file.listFiles().length>0){

                for (File sFile : file.listFiles()) {
                    if(!scanIng){
                        return null;
                    }
                    String path = sFile.getPath();

                    if (sFile.isFile()) {
                        if (isInExts(path)) {
                            if(null!=mOnScanListener){
                                mOnScanListener.onScanProgress(FileUtils.getFileName(path));
                            }
                            WeiXinVideo weiXinVideo = VideoUtils.getVideoDataForPath(path);
                            if(null!=weiXinVideo){
                                if(weiXinVideo.getVideoDortion()>=mMinDurtion){
                                    //限制了最大时长
                                    if(mMaxDurtion>0&&weiXinVideo.getVideoDortion()<=mMaxDurtion){
                                        files.add(weiXinVideo);
                                        if(max>0&&files.size()>=max){
                                            return files;
                                        }
                                        if(event){ //需要发送异步消息
                                            boolean isEqual=false;
                                            if(null!=videos&&videos.size()>0){
                                                for (int i = 0; i < videos.size(); i++) {
                                                    WeiXinVideo weiXinVideo1 = videos.get(i);
                                                    if(TextUtils.equals(weiXinVideo.getFileName(),weiXinVideo1.getFileName())){
                                                        isEqual=true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if(!isEqual){
                                                videos.add(weiXinVideo);
                                            }
                                            //当集合长度大于5条数据的时候，试图发送消息给UI，让UI先刷新界面
                                            if(null!=videos&&videos.size()>=5){
                                                ScanMessageEvent scanMessageEvent=new ScanMessageEvent();
                                                scanMessageEvent.setMessage("updata_video_list");
                                                List<WeiXinVideo> weiXinVideos=new ArrayList<>();
                                                weiXinVideos.addAll(videos);
                                                scanMessageEvent.setWeiXinVideos(weiXinVideos);
                                                videos.clear();
                                                ApplicationManager.getInstance().observerUpdata(scanMessageEvent);
                                                //最后的一点饰品可能不足5个
                                            }
                                        }
                                    }else if(0==mMaxDurtion){
                                        files.add(weiXinVideo);
                                        if(max>0&&files.size()>=max){
                                            return files;
                                        }
                                        if(event){ //需要发送异步消息
                                            boolean isEqual=false;
                                            if(null!=videos&&videos.size()>0){
                                                for (int i = 0; i < videos.size(); i++) {
                                                    WeiXinVideo weiXinVideo1 = videos.get(i);
                                                    if(TextUtils.equals(weiXinVideo.getFileName(),weiXinVideo1.getFileName())){
                                                        isEqual=true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if(!isEqual){
                                                videos.add(weiXinVideo);
                                            }
                                            //当集合长度大于5条数据的时候，试图发送消息给UI，让UI先刷新界面
                                            if(null!=videos&&videos.size()>=5){
                                                ScanMessageEvent scanMessageEvent=new ScanMessageEvent();
                                                scanMessageEvent.setMessage("updata_video_list");
                                                List<WeiXinVideo> weiXinVideos=new ArrayList<>();
                                                weiXinVideos.addAll(videos);
                                                scanMessageEvent.setWeiXinVideos(weiXinVideos);
                                                videos.clear();
                                                ApplicationManager.getInstance().observerUpdata(scanMessageEvent);
                                                //最后的一点饰品可能不足5个
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (sFile.isDirectory()) {
                        if(path.indexOf("/.") == -1){
                            if(!TextUtils.isEmpty(mFilterFolderPath)&&TextUtils.equals(mFilterFolderPath,path)){
                                continue;
                            }else{
                                scanFiles(path);
                            }
                        }
                    }
                }
            }
        }
        //需要发送异步消息
        if(event){
            //最后的几个视频可能不足5个
            if(null!=videos&&videos.size()>0){
                ScanMessageEvent scanMessageEvent=new ScanMessageEvent();
                scanMessageEvent.setMessage("updata_video_list");
                List<WeiXinVideo> weiXinVideos=new ArrayList<>();
                weiXinVideos.addAll(videos);
                scanMessageEvent.setWeiXinVideos(weiXinVideos);
                ApplicationManager.getInstance().observerUpdata(scanMessageEvent);
                if(null!=videos) videos.clear();
            }
        }
        return files;
    }




    private int getDurtion(String path) {
        MediaPlayer mp = MediaPlayer.create(VideoApplication.getInstance().getApplicationContext(), Uri.parse(path));
        int duration=0;
        if(null!=mp){
            duration = mp.getDuration();
        }
        return duration;
    }

    public void setExts(String... exts) {
        if (exts == null || exts.length == 0) return ;
        this.exts = new ArrayList<>();
        for (String ext : exts) {
            this.exts.add(ext);
        }
    }

    private boolean isInExts(String path) {
        if (path == null || path.isEmpty()) return false;
        if (exts == null || exts.size() == 0) return true;
        boolean flag = false;
        int idx = path.lastIndexOf(".");
        if (idx != -1) {
            String ext = path.substring(idx + 1, path.length());
            for (String cext : exts) {
                if (ext.equals(cext)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
}
