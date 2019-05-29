package com.yc.liaolive.bean;

/**
 * YuyeTinyHung@outlook.com
 * 2017/7/9
 * 文件上传 基本信息
 */

public class UploadObjectInfo{

    private long id;//时间戳
    private int fileWidth;//文件宽
    private int fileHeight;//文件高
    private long fileSize;//文件大小
    private String fileName;//文件名称
    private String fileMd5;//文件MD5
    private String filePath;//本地文件地址
    private long videoDurtion;//时长
    private int uploadProgress;//当前的上传进度
    private int fileSourceType;//源文件类型 0：图片 1：视频
    private String uploadFileFolder;//上传至阿里云的分区下的目录地址
    private long videoFrame;//视频封面
    private String uploadID;
    private String videoDesp;//视频描述
    private String serviceCallBackBody;//上传成功后服务端返回的回调信息

    public String getVideoDesp() {
        return videoDesp;
    }

    public void setVideoDesp(String videoDesp) {
        this.videoDesp = videoDesp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFileWidth() {
        return fileWidth;
    }

    public void setFileWidth(int fileWidth) {
        this.fileWidth = fileWidth;
    }

    public int getFileHeight() {
        return fileHeight;
    }

    public void setFileHeight(int fileHeight) {
        this.fileHeight = fileHeight;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getVideoDurtion() {
        return videoDurtion;
    }

    public void setVideoDurtion(long videoDurtion) {
        this.videoDurtion = videoDurtion;
    }

    public int getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(int uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    public int getFileSourceType() {
        return fileSourceType;
    }

    public void setFileSourceType(int fileSourceType) {
        this.fileSourceType = fileSourceType;
    }

    public String getUploadFileFolder() {
        return uploadFileFolder;
    }

    public void setUploadFileFolder(String uploadFileFolder) {
        this.uploadFileFolder = uploadFileFolder;
    }

    public String getUploadID() {
        return uploadID;
    }

    public void setUploadID(String uploadID) {
        this.uploadID = uploadID;
    }

    public String getServiceCallBackBody() {
        return serviceCallBackBody;
    }

    public void setServiceCallBackBody(String serviceCallBackBody) {
        this.serviceCallBackBody = serviceCallBackBody;
    }

    public long getVideoFrame() {
        return videoFrame;
    }

    public void setVideoFrame(long videoFrame) {
        this.videoFrame = videoFrame;
    }

    @Override
    public String toString() {
        return "UploadObjectInfo{" +
                "id=" + id +
                ", fileWidth=" + fileWidth +
                ", fileHeight=" + fileHeight +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", fileMd5='" + fileMd5 + '\'' +
                ", filePath='" + filePath + '\'' +
                ", videoDurtion=" + videoDurtion +
                ", uploadProgress=" + uploadProgress +
                ", fileSourceType=" + fileSourceType +
                ", uploadFileFolder='" + uploadFileFolder + '\'' +
                ", videoFrame=" + videoFrame +
                ", uploadID='" + uploadID + '\'' +
                ", videoDesp='" + videoDesp + '\'' +
                ", serviceCallBackBody='" + serviceCallBackBody + '\'' +
                '}';
    }
}
