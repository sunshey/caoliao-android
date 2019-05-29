package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/12/17
 */

public class NoticeMessage  implements Serializable {

    /**
     * content : 直播间提示
     * color : #ff0000
     */
    private NoticeContent room;

    /**
     * content : 视频聊天提示
     * color : #ff0000
     */

    private NoticeContent video_chat;
    /**
     * content : 私信聊天提示
     * color : #ff0000
     */

    private NoticeContent message;


    public NoticeContent getRoom() {
        return room;
    }

    public void setRoom(NoticeContent room) {
        this.room = room;
    }

    public NoticeContent getVideo_chat() {
        return video_chat;
    }

    public void setVideo_chat(NoticeContent video_chat) {
        this.video_chat = video_chat;
    }

    public NoticeContent getMessage() {
        return message;
    }

    public void setMessage(NoticeContent message) {
        this.message = message;
    }
}
