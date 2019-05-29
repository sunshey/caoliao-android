package com.yc.liaolive.util;

/**
 * 倒计时器
 * Created by yangxueqin on 18/12/15.
 */
public class CountDownTimer extends android.os.CountDownTimer {

    public static final int STATE_CANCEL = -1; // 取消

    public static final int STATE_COUNTING = 0;// 倒计时中

    public static final int STATE_ONCOMPLETED = 1;// 完成

    private CountTimeInfo mCountTimeInfo;

    private CLEventBus mEventBus;

    int ss = 1000;

    int mi = ss * 60;

    int hh = mi * 60;

    int da = hh * 24;

    private long millisInFuture;

    private StringBuilder sb;


    public CountTimeInfo getmCountTimeInfo() {
        return mCountTimeInfo;
    }

    public CountDownTimer(CLEventBus eventBus, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        // Log.v("lung", "time =" + millisInFuture);
        this.millisInFuture = millisInFuture;
        this.mEventBus = eventBus;
        sb = new StringBuilder();
    }


    /**
     * 初始化并且开始倒计时
     */
    public void initAndStart() {
        mCountTimeInfo = new CountTimeInfo();
        mCountTimeInfo.state = STATE_COUNTING;
        countTime(millisInFuture);
        start();
    }

    /**
     * 重置时间
     */
    public void reset() {
        cancel();
        mCountTimeInfo.state = STATE_CANCEL;
        mCountTimeInfo.reset();
        mEventBus.post(CountTimeInfo.class, mCountTimeInfo);
    }

    @Override
    public void onFinish() {
        mCountTimeInfo.reset();
        mCountTimeInfo.state = STATE_ONCOMPLETED;
        mEventBus.post(CountTimeInfo.class, mCountTimeInfo);

    }

    @Override
    public void onTick(final long millisUntilFinished) {
        // Log.v("lung","millisUntilFinished = "+millisUntilFinished);
        countTime(millisUntilFinished);

    }

    /**
     * 计算时间
     *
     * @param time
     */
    private void countTime(long time) {
        mCountTimeInfo.time = time;// 毫秒
        // 计算剩余的天、小时、分钟、秒钟
        long day = (mCountTimeInfo.time) / da;
        long hour = (mCountTimeInfo.time - day * da) / hh;
        long minute = (mCountTimeInfo.time - day * da - hour * hh) / mi;
        long second = (mCountTimeInfo.time - day * da - hour * hh - minute * mi) / ss;
        long millis =
                (mCountTimeInfo.time - day * da - hour * hh - minute * mi - second * ss) / 100;


        mCountTimeInfo.day = add(day);
        mCountTimeInfo.hour = add(hour);
        mCountTimeInfo.minute = add(minute);
        mCountTimeInfo.second = add(second);
        mCountTimeInfo.millis = millis + "";
        mCountTimeInfo.state = STATE_COUNTING;
        // 对外发出刷新通知
        mEventBus.post(CountTimeInfo.class, mCountTimeInfo);
    }

    /**
     * 日期数字少于两位时自动补0
     *
     * @param num
     * @return
     */

    private String add(long num) {
        sb.delete( 0, sb.length() );
        sb.append(num);
        if (sb.length() > 1) {
            return sb.toString();
        } else {
            return "0" + sb.toString();
        }
    }

    /**
     * 倒计时信息
     */
    public static class CountTimeInfo {
        public String day;

        public String hour;

        public String minute;

        public String millis;

        public String second;

        public int state;

        public long time;


        /**
         * 重置
         */
        public void reset() {
            day = "00";
            hour = "00";
            minute = "00";
            second = "00";
            millis = "0";
            time = 0;
        }
    }

}
