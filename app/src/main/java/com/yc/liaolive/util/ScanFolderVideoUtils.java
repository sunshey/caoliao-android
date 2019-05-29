package com.yc.liaolive.util;

/**
 * TinyHung@outlook.com
 * 2017/9/6
 * 扫描本机所有包含视频的文件夹列表
 */

public class ScanFolderVideoUtils {

//    private static final String TAG = ScanFolderVideoUtils.class.getSimpleName();
//    private static String FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath();
//
//    private boolean scanIng=true;
//    private boolean event;
//    private String mFilterFolderPath;
//
//    public void setScanEvent(boolean scanEvent) {
//        Logger.d("扫描视频","scanIng="+scanIng);
//        this.scanIng=scanEvent;
//    }
//
//    public void setEvent(boolean b) {
//        this.event=b;
//    }
//
//    public void setFilterFolderPath(String filterFolderPath) {
//        this.mFilterFolderPath=filterFolderPath;
//    }
//
//    public interface OnScanListener{
//        void onScanProgress(String file);
//    }
//
//    private OnScanListener mOnScanListener;
//
//    private List<WeiXinVideo> files = new ArrayList<>();
//    private List<String> exts;
//    private int max = 9;
//
//
//    public void setMax(int number) {
//        this.max = number;
//    }
//
//    public void setListener(OnScanListener onScanListener) {
//        this.mOnScanListener=onScanListener;
//    }
//
//    public List<FolderList> scanFolderFiles() {
//        return scanFolderFiles(null);
//    }
//
//
//    private int listLength=0;
//
//    List<WeiXinVideo> videos=new ArrayList<>();
//
//    List<FolderList> mFolderLists=new ArrayList<>();
//
//    public List<FolderList> scanFolderFiles(String dir) {
//
//        File folderFile = new File(TextUtils.isEmpty(dir)? FOLDER : dir);
//        if(folderFile.exists()){
//            if(folderFile.listFiles().length>0){//该目录下所有文件夹
//                for (File fileDirectory : folderFile.listFiles()) {
//                    Logger.d(TAG,"fileDirectory="+fileDirectory);
//                    if(fileDirectory.isFile()){
//                        if(isInExts(fileDirectory.getPath())){
//                            FolderList folderList=new FolderList();
//                            folderList.setFolderName(fileDirectory.getTitle());
//                            folderList.setPath(fileDirectory.getAbsolutePath());
//
//                        }
//                    }
//                }
//            }
//        }
//
//
//
//        File file = new File(TextUtils.isEmpty(dir)? FOLDER : dir);
//        if (file.exists()) {
//            if(file.listFiles().length>0){
//
//                for (File sFile : file.listFiles()) {
//                    if(!scanIng){
//                        return null;
//                    }
//                    String path = sFile.getPath();
//
//                    if (sFile.isFile()) {
//                        if (isInExts(path)) {
//                            if(null!=mOnScanListener){
//                                mOnScanListener.onScanProgress(FileUtils.getFileName(path));
//                            }
//                            WeiXinVideo videoDataForPath = VideoUtils.getVideoDataForPath(path);
//                            //只取大于3秒并且小于5分钟的视频
//                            if(null!=videoDataForPath&&videoDataForPath.getVideoDortion()>=(3*1000)&&videoDataForPath.getVideoDortion()<=(300*1000)){
//                                files.add(videoDataForPath);
//                                if(event){
//                                    videos.add(videoDataForPath);
//                                    listLength++;
//                                    //当集合长度大于5条数据的时候，试图发送消息给UI，让UI先刷新界面
//                                    if(listLength>=5){
//                                        ScanMessageEvent scanMessageEvent=new ScanMessageEvent();
//                                        scanMessageEvent.setMessage("updata_video_list");
//                                        List<WeiXinVideo> weiXinVideos=new ArrayList<>();
//                                        weiXinVideos.addAll(videos);
//                                        scanMessageEvent.setWeiXinVideos(weiXinVideos);
//                                        EventBus.getDefault().post(scanMessageEvent);
//                                        listLength=0;
//                                        if(null!=videos) videos.clear();
//                                    //最后的一点饰品可能不足5个
//                                    }
//                                }
//                            }
//                        }
//                    } else if (sFile.isDirectory()) {
//                        if( path.indexOf("/.") == -1){
//                            if(null!=mFilterFolderPath&&TextUtils.equals(mFilterFolderPath,path)){
//                                Logger.d("视频扫描","过滤的目录-----------------：="+path);
//                            }else{
//                                scanFiles(path);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        //最后的几个视频可能不足5个
//        if(null!=videos&&videos.size()>0&&0>listLength){
//            ScanMessageEvent scanMessageEvent=new ScanMessageEvent();
//            scanMessageEvent.setMessage("updata_video_list");
//            List<WeiXinVideo> weiXinVideos=new ArrayList<>();
//            weiXinVideos.addAll(videos);
//            scanMessageEvent.setWeiXinVideos(weiXinVideos);
//            EventBus.getDefault().post(scanMessageEvent);
//            listLength=0;
//            if(null!=videos) videos.clear();
//        }
//        if(event){
//            MoivesListAdapter.setIsEnd(true);
//        }
//        return files;
//    }
//
//
//
//
//    private int getDurtion(String path) {
//        MediaPlayer mp = MediaPlayer.create(VideoApplication.getInstance().getApplicationContext(), Uri.parse(path));
//        int duration=0;
//        if(null!=mp){
//            duration = mp.getDuration();
//        }
//        return duration;
//    }
//
//    public void setExts(String... exts) {
//        if (exts == null || exts.length == 0) return ;
//        this.exts = new ArrayList<>();
//        for (String ext : exts) {
//            this.exts.add(ext);
//        }
//    }
//
//    private boolean isInExts(String path) {
//        if (path == null || path.isEmpty()) return false;
//        if (exts == null || exts.size() == 0) return true;
//        boolean flag = false;
//        int idx = path.lastIndexOf(".");
//        if (idx != -1) {
//            String ext = path.substring(idx + 1, path.length());
//            for (String cext : exts) {
//                if (ext.equals(cext)) {
//                    flag = true;
//                    break;
//                }
//            }
//        }
//        return flag;
//    }
}
