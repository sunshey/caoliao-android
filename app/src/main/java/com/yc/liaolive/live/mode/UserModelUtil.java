package com.yc.liaolive.live.mode;

import android.graphics.drawable.Drawable;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import java.util.HashMap;
import java.util.Map;

/**
 * TinyHung@outlook.com
 * 2017/5/20
 * 用户等级
 */
public class UserModelUtil {

    public static final String REGEX_NOTIFY="通知";
    public static final String REGEX_AUTH="官方";

   public static Map<Integer, Integer> sUserGradleFace;
   public static Map<Integer, Integer> sUserVipGradleFace;
   public static Map<Integer, Integer> sNums;
   public static Map<Integer, Integer> sSendNums;
   public static Map<Integer, Integer> sdrawNums;
   public static Map<String, Integer> sDrawables;

   static {
       sUserGradleFace = new HashMap<>();

       sUserGradleFace.put(1, R.drawable.im_lv1);
       sUserGradleFace.put(2, R.drawable.im_lv2);
       sUserGradleFace.put(3, R.drawable.im_lv3);
       sUserGradleFace.put(4, R.drawable.im_lv4);
       sUserGradleFace.put(5, R.drawable.im_lv5);
       sUserGradleFace.put(6, R.drawable.im_lv6);
       sUserGradleFace.put(7, R.drawable.im_lv7);
       sUserGradleFace.put(8, R.drawable.im_lv8);
       sUserGradleFace.put(9, R.drawable.im_lv9);
       sUserGradleFace.put(10, R.drawable.im_lv10);
       sUserGradleFace.put(11, R.drawable.im_lv11);
       sUserGradleFace.put(12, R.drawable.im_lv12);
       sUserGradleFace.put(13, R.drawable.im_lv13);
       sUserGradleFace.put(14, R.drawable.im_lv14);
       sUserGradleFace.put(15, R.drawable.im_lv15);
       sUserGradleFace.put(16, R.drawable.im_lv16);
       sUserGradleFace.put(17, R.drawable.im_lv17);
       sUserGradleFace.put(18, R.drawable.im_lv18);
       sUserGradleFace.put(19, R.drawable.im_lv19);
       sUserGradleFace.put(20, R.drawable.im_lv20);
       sUserGradleFace.put(21, R.drawable.im_lv21);
       sUserGradleFace.put(22, R.drawable.im_lv22);
       sUserGradleFace.put(23, R.drawable.im_lv23);
       sUserGradleFace.put(24, R.drawable.im_lv24);
       sUserGradleFace.put(25, R.drawable.im_lv25);
       sUserGradleFace.put(26, R.drawable.im_lv26);
       sUserGradleFace.put(27, R.drawable.im_lv27);
       sUserGradleFace.put(28, R.drawable.im_lv28);
       sUserGradleFace.put(29, R.drawable.im_lv29);
       sUserGradleFace.put(30, R.drawable.im_lv30);
       sUserGradleFace.put(31, R.drawable.im_lv31);
       sUserGradleFace.put(32, R.drawable.im_lv32);
       sUserGradleFace.put(33, R.drawable.im_lv33);
       sUserGradleFace.put(34, R.drawable.im_lv34);
       sUserGradleFace.put(35, R.drawable.im_lv35);
       sUserGradleFace.put(36, R.drawable.im_lv36);
       sUserGradleFace.put(37, R.drawable.im_lv37);
       sUserGradleFace.put(38, R.drawable.im_lv38);
       sUserGradleFace.put(39, R.drawable.im_lv39);
       sUserGradleFace.put(40, R.drawable.im_lv40);
       sUserGradleFace.put(41, R.drawable.im_lv41);
       sUserGradleFace.put(42, R.drawable.im_lv42);
       sUserGradleFace.put(43, R.drawable.im_lv43);
       sUserGradleFace.put(44, R.drawable.im_lv44);
       sUserGradleFace.put(45, R.drawable.im_lv45);
       sUserGradleFace.put(46, R.drawable.im_lv46);
       sUserGradleFace.put(47, R.drawable.im_lv47);
       sUserGradleFace.put(48, R.drawable.im_lv48);
       sUserGradleFace.put(49, R.drawable.im_lv49);
       sUserGradleFace.put(50, R.drawable.im_lv50);

       sDrawables=new HashMap<>();
       sDrawables.put(REGEX_NOTIFY,R.drawable.ic_live_gift_notify);
       sDrawables.put(REGEX_AUTH,R.drawable.im_lv_official);

       sUserVipGradleFace = new HashMap<>();
       sUserVipGradleFace.put(1, R.drawable.vip_gradle1);
       sUserVipGradleFace.put(2, R.drawable.vip_gradle2);
       sUserVipGradleFace.put(3, R.drawable.vip_gradle3);
       sUserVipGradleFace.put(4, R.drawable.vip_gradle4);
       sUserVipGradleFace.put(5, R.drawable.vip_gradle5);
       sUserVipGradleFace.put(6, R.drawable.vip_gradle6);
       sUserVipGradleFace.put(7, R.drawable.vip_gradle7);
       sUserVipGradleFace.put(8, R.drawable.vip_gradle8);
       sUserVipGradleFace.put(9, R.drawable.vip_gradle9);
       sUserVipGradleFace.put(10, R.drawable.vip_gradle10);
       sUserVipGradleFace.put(11, R.drawable.vip_gradle11);
       sUserVipGradleFace.put(12, R.drawable.vip_gradle12);
       sUserVipGradleFace.put(13, R.drawable.vip_gradle13);
       sUserVipGradleFace.put(14, R.drawable.vip_gradle14);
       sUserVipGradleFace.put(15, R.drawable.vip_gradle15);
       sUserVipGradleFace.put(16, R.drawable.vip_gradle16);
       sUserVipGradleFace.put(17, R.drawable.vip_gradle17);
       sUserVipGradleFace.put(18, R.drawable.vip_gradle18);
       sUserVipGradleFace.put(19, R.drawable.vip_gradle19);
       sUserVipGradleFace.put(20, R.drawable.vip_gradle20);
       sUserVipGradleFace.put(21, R.drawable.vip_gradle21);
       sUserVipGradleFace.put(22, R.drawable.vip_gradle22);
       sUserVipGradleFace.put(23, R.drawable.vip_gradle23);
       sUserVipGradleFace.put(24, R.drawable.vip_gradle24);
       sUserVipGradleFace.put(25, R.drawable.vip_gradle25);
       sUserVipGradleFace.put(26, R.drawable.vip_gradle26);
       sUserVipGradleFace.put(27, R.drawable.vip_gradle27);
       sUserVipGradleFace.put(28, R.drawable.vip_gradle28);
       sUserVipGradleFace.put(29, R.drawable.vip_gradle29);
       sUserVipGradleFace.put(30, R.drawable.vip_gradle30);
       sUserVipGradleFace.put(31, R.drawable.vip_gradle31);
       sUserVipGradleFace.put(32, R.drawable.vip_gradle32);
       sUserVipGradleFace.put(33, R.drawable.vip_gradle33);
       sUserVipGradleFace.put(34, R.drawable.vip_gradle34);
       sUserVipGradleFace.put(35, R.drawable.vip_gradle35);
       sUserVipGradleFace.put(36, R.drawable.vip_gradle36);
       sUserVipGradleFace.put(37, R.drawable.vip_gradle37);
       sUserVipGradleFace.put(38, R.drawable.vip_gradle38);
       sUserVipGradleFace.put(39, R.drawable.vip_gradle39);
       sUserVipGradleFace.put(40, R.drawable.vip_gradle40);
       sUserVipGradleFace.put(41, R.drawable.vip_gradle41);
       sUserVipGradleFace.put(42, R.drawable.vip_gradle42);
       sUserVipGradleFace.put(43, R.drawable.vip_gradle43);
       sUserVipGradleFace.put(44, R.drawable.vip_gradle44);
       sUserVipGradleFace.put(45, R.drawable.vip_gradle45);
       sUserVipGradleFace.put(46, R.drawable.vip_gradle46);
       sUserVipGradleFace.put(47, R.drawable.vip_gradle47);
       sUserVipGradleFace.put(48, R.drawable.vip_gradle48);
       sUserVipGradleFace.put(49, R.drawable.vip_gradle49);
       sUserVipGradleFace.put(50, R.drawable.vip_gradle50);


       sNums = new HashMap<>();
       sNums.put(0,R.drawable.num0);
       sNums.put(1,R.drawable.num1);
       sNums.put(2,R.drawable.num2);
       sNums.put(3,R.drawable.num3);
       sNums.put(4,R.drawable.num4);
       sNums.put(5,R.drawable.num5);
       sNums.put(6,R.drawable.num6);
       sNums.put(7,R.drawable.num7);
       sNums.put(8,R.drawable.num8);
       sNums.put(9,R.drawable.num9);

       sdrawNums=new HashMap<>();
       sdrawNums.put(0,R.drawable.ic_draw_0);
       sdrawNums.put(1,R.drawable.ic_draw_1);
       sdrawNums.put(2,R.drawable.ic_draw_2);
       sdrawNums.put(3,R.drawable.ic_draw_3);
       sdrawNums.put(4,R.drawable.ic_draw_4);
       sdrawNums.put(5,R.drawable.ic_draw_5);
       sdrawNums.put(6,R.drawable.ic_draw_6);
       sdrawNums.put(7,R.drawable.ic_draw_7);
       sdrawNums.put(8,R.drawable.ic_draw_8);
       sdrawNums.put(9,R.drawable.ic_draw_9);

       sSendNums=new HashMap<>();
       sSendNums.put(0,R.drawable.gift_card_level_three_n_0);
       sSendNums.put(1,R.drawable.gift_card_level_three_n_1);
       sSendNums.put(2,R.drawable.gift_card_level_three_n_2);
       sSendNums.put(3,R.drawable.gift_card_level_three_n_3);
       sSendNums.put(4,R.drawable.gift_card_level_three_n_4);
       sSendNums.put(5,R.drawable.gift_card_level_three_n_5);
       sSendNums.put(6,R.drawable.gift_card_level_three_n_6);
       sSendNums.put(7,R.drawable.gift_card_level_three_n_7);
       sSendNums.put(8,R.drawable.gift_card_level_three_n_8);
       sSendNums.put(9,R.drawable.gift_card_level_three_n_9);
   }


    /**
     * 根据用户等级获取对应资源ID
     * @param gradle
     * @return
     */
    public static int getUserGradleRes(int gradle) {
        if(gradle<1) return R.drawable.im_lv1;
        if(gradle>50) return sUserGradleFace.get(50);
        return sUserGradleFace.get(gradle);
    }



    /**
     * 根据用户会员等级获取对应资源ID
     * @param gradle
     * @return
     */
    public static int getUserVipGradleRes(int gradle) {
        if(gradle<1) return R.drawable.vip_gradle1;
        if(gradle>50) return sUserVipGradleFace.get(50);
        return sUserVipGradleFace.get(gradle);
    }
    /**
     * 根据Key获取对应的资源文件
     * @param key
     * @return
     */
    public static Drawable getDrawable(String key) {
        Integer integer = sDrawables.get(key);
        if(null!=integer){
            Drawable drawable = VideoApplication.getInstance().getApplicationContext().getResources().getDrawable(integer);
            return drawable;
        }
        return null;
    }

    /**
     * 赠送礼物数字
     * @param num
     * @return
     */
    public static int getNumCount(int num) {
        if(num<0|num>9) return 0;
        return sNums.get(num);
    }

    public static int giftSendNumFromat(int num) {
        if(num<0|num>9) return 0;
        return sSendNums.get(num);
    }

    /**
     * 中奖礼物数字
     * @param num
     * @return
     */
    public static int getDrawNumCount(int num) {
        if(num<0|num>9) return 0;
        return sdrawNums.get(num);
    }
}
