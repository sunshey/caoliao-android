package com.yc.liaolive.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TimeUtils
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-8-24
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     * 
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     * 
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }
    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTimeForOld(long timeInMillis) {
        return getTime(timeInMillis, DATE_FORMAT_DATE);
    }
    /**
     * get current time in milliseconds
     * 
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     * 
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     * 
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }


    /**
     * 指定格式返回当前系统时间
     */
    public static String getDataTime() {
        SimpleDateFormat df = DATE_FORMAT_DATE;
        return df.format(new Date());
    }

    /**
     *
     *将毫秒的时间解析成字符串，格式为：SS:HH:MM
     */
    @SuppressLint("DefaultLocale")
    public static String formatDuration(int duration) {
        duration /= 1000; // milliseconds into seconds
        int minute = duration / 60;
        int hour = minute / 60;
        minute %= 60;
        int second = duration % 60;
        if (hour != 0)
            return String.format("%2d:%02d:%02d", hour, minute, second);
        else
            return String.format("%02d:%02d", minute, second);
    }

    public static String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;

        second = l.intValue() / 1000;

        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        //(getTwoLength(hour) + ":" +
        return (getTwoLength(minute)  + "分"  + getTwoLength(second)+"秒");
    }

    private static String getTwoLength(final int data) {
        if(data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }

    public static String getTilmNow(Long time) {
        Calendar cal = Calendar.getInstance();
        long timel = cal.getTimeInMillis() - time;
        if (timel / 1000 < 60) {
            return "1分钟以内";
        } else if (timel / 1000 / 60 < 60) {
            return timel / 1000 / 60 + "分钟前";
        } else if (timel / 1000 / 60 / 60 < 24) {
            return timel / 1000 / 60 / 60 + "小时前";
        } else {
            return getTimeForString(time);
        }
    }
    public static String getTimeForString(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(time);
    }
}
