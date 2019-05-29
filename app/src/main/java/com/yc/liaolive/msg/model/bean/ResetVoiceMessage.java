package com.yc.liaolive.msg.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * TinyHung@Outlook.com
 * 2018/12/10
 * reset api voice message
 */
@Entity
public class ResetVoiceMessage {

    /**
     * id : 123456
     * time : 123456
     * path : http://
     * durtion : 算好的分秒字符串
     */
    @Id(autoincrement = true)
    private Long id;//本地数据库的key
    private String time;//文件ID
    private String path;//文件播放地址
    private long durtion;//单位 秒
    private int isRead;//是否已读状态
    public int getIsRead() {
        return this.isRead;
    }
    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
    public long getDurtion() {
        return this.durtion;
    }
    public void setDurtion(long durtion) {
        this.durtion = durtion;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1478841816)
    public ResetVoiceMessage(Long id, String time, String path, long durtion,
            int isRead) {
        this.id = id;
        this.time = time;
        this.path = path;
        this.durtion = durtion;
        this.isRead = isRead;
    }
    @Generated(hash = 556447718)
    public ResetVoiceMessage() {
    }

}
