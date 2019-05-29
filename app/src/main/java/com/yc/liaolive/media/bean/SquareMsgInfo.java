package com.yc.liaolive.media.bean;

import com.yc.liaolive.live.bean.RoomList;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/7
 */

public class SquareMsgInfo {

    private String cmd;
    private SquareMsgDataBean data;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }


    public class SquareMsgDataBean{
        private List<RoomList> private_room;
        public List<RoomList> getPrivate_room() {
            return private_room;
        }

        public void setPrivate_room(List<RoomList> private_room) {
            this.private_room = private_room;
        }
    }

    public SquareMsgDataBean getData() {
        return data;
    }

    public void setData(SquareMsgDataBean data) {
        this.data = data;
    }
}
