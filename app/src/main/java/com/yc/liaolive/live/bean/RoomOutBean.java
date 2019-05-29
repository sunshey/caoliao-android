package com.yc.liaolive.live.bean;

/**
 * 结束直播返回数据
 * Created by Android on 2018/11/20.
 */

public class RoomOutBean {

    private String points = "0"; //积分
    private String diamond = "0"; //钻石
    private String intimacy = "0"; //亲密度
    private String duration = "0"; //时长
    private String op_group;

    public RoomOutBean() {
    }

    public RoomOutBean(String points, String diamond, String intimacy, String duration) {
        this.points = points;
        this.diamond = diamond;
        this.intimacy = intimacy;
        this.duration = duration;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getDiamond() {
        return diamond;
    }

    public void setDiamond(String diamond) {
        this.diamond = diamond;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getIntimacy() {
        return intimacy;
    }

    public void setIntimacy(String intimacy) {
        this.intimacy = intimacy;
    }

    public String getOp_group() {
        return op_group;
    }

    public void setOp_group(String op_group) {
        this.op_group = op_group;
    }
}
