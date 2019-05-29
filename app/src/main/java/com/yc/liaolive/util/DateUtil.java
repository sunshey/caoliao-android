package com.yc.liaolive.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * TinyHung@outlook.com
 * 2017/6/30 14:54
 */
public class DateUtil {

    private static final String TAG = "DateUtil";

    public static String getTimeLengthString(int var0) {
        if(var0 > 0) {
            StringBuffer var1 = new StringBuffer();
            Integer var2 = Integer.valueOf(var0 / 60);
            Integer var4 = Integer.valueOf(var0 % 60);
            if(var2.intValue() > 60) {
                Integer var3;
                if((var3 = Integer.valueOf(var2.intValue() / 60)).intValue() < 10) {
                    var1.append("0" + var3);
                } else {
                    var1.append(var3);
                }

                var1.append(":");
                if((var2 = Integer.valueOf(var2.intValue() % 60)).intValue() < 10) {
                    var1.append("0" + var2);
                } else {
                    var1.append(var2);
                }
            } else if(var2.intValue() < 10) {
                var1.append("0" + var2);
            } else {
                var1.append(var2);
            }

            var1.append(":");
            if(var4.intValue() < 10) {
                var1.append("0" + var4);
            } else {
                var1.append(var4);
            }

            return var1.toString();
        } else {
            return "00:00";
        }
    }

    public static String durtionFormat(long millis){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(millis);
    }

    public static String durtionFormatDian(long millis){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(millis);
    }

    /**
     * 时间格式化
     * @param durtion 秒
     * @return 小于60秒为1分钟，整除后有余商则顺加一分钟
     */
    public static String timeFormat(long durtion) {
        if(durtion<=0) return "0分钟";
        if(durtion<=60) return "1分钟";
        long minute=durtion / 60;
        if(durtion%60>0){
            minute+=1;
        }
        return minute+"分钟";
    }

    /**
     * 小于1小时格式化分秒，大于1小时格式化时分秒
     * @param millis
     * @return
     */
    public static String minuteFormat(long millis){
        SimpleDateFormat formatter=new SimpleDateFormat("mm:ss");
        if(millis>59000){
            formatter=new SimpleDateFormat("HH:mm:ss");
        }
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(millis);
    }

    /**
     * 格式化到天
     * @param millis
     * @return
     */
    public static String timeFormatDay(long millis){
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(millis);
        Date currenTimeZone = inputTime.getTime();
        return formatter.format(currenTimeZone);
    }

    /**
     * 格式化到天
     * @param millis
     * @return
     */
    public static String timeFormatDayStr(long millis){
        SimpleDateFormat formatter=new SimpleDateFormat("MM月dd日");
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(millis);
        Date currenTimeZone = inputTime.getTime();
        return formatter.format(currenTimeZone);
    }

    /**
     * 格式化到天
     * @param millis
     * @return
     */
    public static String timeFormatYTD(long millis){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(millis);
        Date currenTimeZone = inputTime.getTime();
        return formatter.format(currenTimeZone);
    }

    /**
     * 格式化到分
     * @param millis
     * @return
     */
    public static String timeFormatMinute(long millis){
        SimpleDateFormat formatter=new SimpleDateFormat("HH:mm");
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(millis);
        Date currenTimeZone = inputTime.getTime();
        return formatter.format(currenTimeZone);
    }

    /**
     * 格式化到秒
     * @param millis
     * @return
     */
    public static String timeFormatSecond(long millis){
        SimpleDateFormat formatter=new SimpleDateFormat("mm:ss");
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(millis);
        Date currenTimeZone = inputTime.getTime();
        return formatter.format(currenTimeZone);
    }
}
