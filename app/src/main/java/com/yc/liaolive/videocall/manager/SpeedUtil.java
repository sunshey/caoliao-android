package com.yc.liaolive.videocall.manager;

import android.net.TrafficStats;

/**
 * TinyHung@Outlook.com
 * 2018/11/1
 * 网络上行、下行 速率获取
 */

public class SpeedUtil {

    private long total_tdata = TrafficStats.getTotalTxBytes();
    private long total_rdata = TrafficStats.getTotalRxBytes();

    public static SpeedUtil cSpeedUtil=new SpeedUtil();

    public static SpeedUtil  get(){
        return cSpeedUtil;
    }

    public long getNetSpeed(SpeedType type) {
        long traffic_data;
        if (type == SpeedType.UP) {//上传
            traffic_data = TrafficStats.getTotalTxBytes() - total_tdata;//总的发送的字节数
            total_tdata = TrafficStats.getTotalTxBytes();
        } else {//下载
            traffic_data = TrafficStats.getTotalRxBytes() - total_rdata;//总的接受字节数
            total_rdata = TrafficStats.getTotalRxBytes();
        }
        return traffic_data;
    }

    public String getSpeedStr(long netSpeed1) {
        String seep;
        if (netSpeed1 > 1024) {
            seep = netSpeed1 / 1024 + "Mb/s";
        } else {
            seep = netSpeed1 + "Kb/s";
        }
        return seep;
    }

    public enum SpeedType {
        UP, DOWN
    }
}
